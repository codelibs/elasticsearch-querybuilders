/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.codelibs.elasticsearch.index.mapper;

import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.spatial.prefix.PrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.TermQueryPrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.apache.lucene.spatial.prefix.tree.PackedQuadPrefixTree;
import org.apache.lucene.spatial.prefix.tree.QuadPrefixTree;
import org.apache.lucene.spatial.prefix.tree.SpatialPrefixTree;
import org.codelibs.elasticsearch.action.fieldstats.FieldStats;
import org.codelibs.elasticsearch.common.Explicit;
import org.codelibs.elasticsearch.common.geo.GeoUtils;
import org.codelibs.elasticsearch.common.geo.SpatialStrategy;
import org.codelibs.elasticsearch.common.geo.builders.ShapeBuilder;
import org.codelibs.elasticsearch.common.geo.builders.ShapeBuilder.Orientation;
import org.codelibs.elasticsearch.common.settings.Settings;
import org.codelibs.elasticsearch.common.unit.DistanceUnit;
import org.codelibs.elasticsearch.common.xcontent.XContentBuilder;
import org.codelibs.elasticsearch.index.query.QueryShardContext;
import org.codelibs.elasticsearch.index.query.QueryShardException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


/**
 * FieldMapper for indexing {@link org.locationtech.spatial4j.shape.Shape}s.
 * <p>
 * Currently Shapes can only be indexed and can only be queried using
 * {@link org.codelibs.elasticsearch.index.query.GeoShapeQueryBuilder}, consequently
 * a lot of behavior in this Mapper is disabled.
 * <p>
 * Format supported:
 * <p>
 * "field" : {
 * "type" : "polygon",
 * "coordinates" : [
 * [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ]
 * ]
 * }
 */
public class GeoShapeFieldMapper extends FieldMapper {

    public static final String CONTENT_TYPE = "geo_shape";

    public static class Names {
        public static final String TREE = "tree";
        public static final String TREE_GEOHASH = "geohash";
        public static final String TREE_QUADTREE = "quadtree";
        public static final String TREE_LEVELS = "tree_levels";
        public static final String TREE_PRESISION = "precision";
        public static final String DISTANCE_ERROR_PCT = "distance_error_pct";
        public static final String ORIENTATION = "orientation";
        public static final String STRATEGY = "strategy";
        public static final String STRATEGY_POINTS_ONLY = "points_only";
        public static final String COERCE = "coerce";
    }

    public static class Defaults {
        public static final String TREE = Names.TREE_GEOHASH;
        public static final String STRATEGY = SpatialStrategy.RECURSIVE.getStrategyName();
        public static final boolean POINTS_ONLY = false;
        public static final int GEOHASH_LEVELS = GeoUtils.geoHashLevelsForPrecision("50m");
        public static final int QUADTREE_LEVELS = GeoUtils.quadTreeLevelsForPrecision("50m");
        public static final Orientation ORIENTATION = Orientation.RIGHT;
        public static final double LEGACY_DISTANCE_ERROR_PCT = 0.025d;
        public static final Explicit<Boolean> COERCE = new Explicit<>(false, false);

        public static final MappedFieldType FIELD_TYPE = new GeoShapeFieldType();

        static {
            // setting name here is a hack so freeze can be called...instead all these options should be
            // moved to the default ctor for GeoShapeFieldType, and defaultFieldType() should be removed from mappers...
            FIELD_TYPE.setName("DoesNotExist");
            FIELD_TYPE.setIndexOptions(IndexOptions.DOCS);
            FIELD_TYPE.setTokenized(false);
            FIELD_TYPE.setStored(false);
            FIELD_TYPE.setStoreTermVectors(false);
            FIELD_TYPE.setOmitNorms(true);
            FIELD_TYPE.freeze();
        }
    }

    public static class Builder extends FieldMapper.Builder<Builder, GeoShapeFieldMapper> {

        private Boolean coerce;

        public Builder(String name) {
            super(name, Defaults.FIELD_TYPE, Defaults.FIELD_TYPE);
        }

        @Override
        public GeoShapeFieldType fieldType() {
            return (GeoShapeFieldType)fieldType;
        }

        public Builder coerce(boolean coerce) {
            this.coerce = coerce;
            return builder;
        }

        protected Explicit<Boolean> coerce(BuilderContext context) {
            if (coerce != null) {
                return new Explicit<>(coerce, true);
            }
            if (context.indexSettings() != null) {
                return new Explicit<>(COERCE_SETTING.get(context.indexSettings()), false);
            }
            return Defaults.COERCE;
        }

        @Override
        public GeoShapeFieldMapper build(BuilderContext context) {
            GeoShapeFieldType geoShapeFieldType = (GeoShapeFieldType)fieldType;

            if (geoShapeFieldType.treeLevels() == 0 && geoShapeFieldType.precisionInMeters() < 0) {
                geoShapeFieldType.setDefaultDistanceErrorPct(Defaults.LEGACY_DISTANCE_ERROR_PCT);
            }
            setupFieldType(context);

            return new GeoShapeFieldMapper(name, fieldType, coerce(context), context.indexSettings(), multiFieldsBuilder.build(this,
                    context), copyTo);
        }
    }

    public static class TypeParser implements Mapper.TypeParser {
    }

    public static final class GeoShapeFieldType extends MappedFieldType {

        private String tree = Defaults.TREE;
        private String strategyName = Defaults.STRATEGY;
        private boolean pointsOnly = Defaults.POINTS_ONLY;
        private int treeLevels = 0;
        private double precisionInMeters = -1;
        private Double distanceErrorPct;
        private double defaultDistanceErrorPct = 0.0;
        private Orientation orientation = Defaults.ORIENTATION;

        // these are built when the field type is frozen
        private PrefixTreeStrategy defaultStrategy;
        private RecursivePrefixTreeStrategy recursiveStrategy;
        private TermQueryPrefixTreeStrategy termStrategy;

        public GeoShapeFieldType() {}

        protected GeoShapeFieldType(GeoShapeFieldType ref) {
            super(ref);
            this.tree = ref.tree;
            this.strategyName = ref.strategyName;
            this.pointsOnly = ref.pointsOnly;
            this.treeLevels = ref.treeLevels;
            this.precisionInMeters = ref.precisionInMeters;
            this.distanceErrorPct = ref.distanceErrorPct;
            this.defaultDistanceErrorPct = ref.defaultDistanceErrorPct;
            this.orientation = ref.orientation;
        }

        @Override
        public GeoShapeFieldType clone() {
            return new GeoShapeFieldType(this);
        }

        @Override
        public boolean equals(Object o) {
            if (!super.equals(o)) {
                return false;
            }
            GeoShapeFieldType that = (GeoShapeFieldType) o;
            return treeLevels == that.treeLevels &&
                precisionInMeters == that.precisionInMeters &&
                defaultDistanceErrorPct == that.defaultDistanceErrorPct &&
                Objects.equals(tree, that.tree) &&
                Objects.equals(strategyName, that.strategyName) &&
                pointsOnly == that.pointsOnly &&
                Objects.equals(distanceErrorPct, that.distanceErrorPct) &&
                orientation == that.orientation;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), tree, strategyName, pointsOnly, treeLevels, precisionInMeters, distanceErrorPct,
                    defaultDistanceErrorPct, orientation);
        }

        @Override
        public String typeName() {
            return CONTENT_TYPE;
        }

        @Override
        public void freeze() {
            super.freeze();
            // This is a bit hackish: we need to setup the spatial tree and strategies once the field name is set, which
            // must be by the time freeze is called.
            SpatialPrefixTree prefixTree;
            if ("geohash".equals(tree)) {
                prefixTree = new GeohashPrefixTree(ShapeBuilder.SPATIAL_CONTEXT, getLevels(treeLevels, precisionInMeters, Defaults.GEOHASH_LEVELS, true));
            } else if ("legacyquadtree".equals(tree)) {
                prefixTree = new QuadPrefixTree(ShapeBuilder.SPATIAL_CONTEXT, getLevels(treeLevels, precisionInMeters, Defaults.QUADTREE_LEVELS, false));
            } else if ("quadtree".equals(tree)) {
                prefixTree = new PackedQuadPrefixTree(ShapeBuilder.SPATIAL_CONTEXT, getLevels(treeLevels, precisionInMeters, Defaults.QUADTREE_LEVELS, false));
            } else {
                throw new IllegalArgumentException("Unknown prefix tree type [" + tree + "]");
            }

            recursiveStrategy = new RecursivePrefixTreeStrategy(prefixTree, name());
            recursiveStrategy.setDistErrPct(distanceErrorPct());
            recursiveStrategy.setPruneLeafyBranches(false);
            termStrategy = new TermQueryPrefixTreeStrategy(prefixTree, name());
            termStrategy.setDistErrPct(distanceErrorPct());
            defaultStrategy = resolveStrategy(strategyName);
            defaultStrategy.setPointsOnly(pointsOnly);
        }

        @Override
        public void checkCompatibility(MappedFieldType fieldType, List<String> conflicts, boolean strict) {
            super.checkCompatibility(fieldType, conflicts, strict);
            GeoShapeFieldType other = (GeoShapeFieldType)fieldType;
            // prevent user from changing strategies
            if (strategyName().equals(other.strategyName()) == false) {
                conflicts.add("mapper [" + name() + "] has different [strategy]");
            }

            // prevent user from changing trees (changes encoding)
            if (tree().equals(other.tree()) == false) {
                conflicts.add("mapper [" + name() + "] has different [tree]");
            }

            if ((pointsOnly() != other.pointsOnly())) {
                conflicts.add("mapper [" + name() + "] has different points_only");
            }

            // TODO we should allow this, but at the moment levels is used to build bookkeeping variables
            // in lucene's SpatialPrefixTree implementations, need a patch to correct that first
            if (treeLevels() != other.treeLevels()) {
                conflicts.add("mapper [" + name() + "] has different [tree_levels]");
            }
            if (precisionInMeters() != other.precisionInMeters()) {
                conflicts.add("mapper [" + name() + "] has different [precision]");
            }

            if (strict) {
                if (orientation() != other.orientation()) {
                    conflicts.add("mapper [" + name() + "] is used by multiple types. Set update_all_types to true to update [orientation] across all types.");
                }
                if (distanceErrorPct() != other.distanceErrorPct()) {
                    conflicts.add("mapper [" + name() + "] is used by multiple types. Set update_all_types to true to update [distance_error_pct] across all types.");
                }
            }
        }

        private static int getLevels(int treeLevels, double precisionInMeters, int defaultLevels, boolean geoHash) {
            if (treeLevels > 0 || precisionInMeters >= 0) {
                return Math.max(treeLevels, precisionInMeters >= 0 ? (geoHash ? GeoUtils.geoHashLevelsForPrecision(precisionInMeters)
                    : GeoUtils.quadTreeLevelsForPrecision(precisionInMeters)) : 0);
            }
            return defaultLevels;
        }

        public String tree() {
            return tree;
        }

        public void setTree(String tree) {
            checkIfFrozen();
            this.tree = tree;
        }

        public String strategyName() {
            return strategyName;
        }

        public void setStrategyName(String strategyName) {
            checkIfFrozen();
            this.strategyName = strategyName;
            if (this.strategyName.equals(SpatialStrategy.TERM)) {
                this.pointsOnly = true;
            }
        }

        public boolean pointsOnly() {
            return pointsOnly;
        }

        public void setPointsOnly(boolean pointsOnly) {
            checkIfFrozen();
            this.pointsOnly = pointsOnly;
        }
        public int treeLevels() {
            return treeLevels;
        }

        public void setTreeLevels(int treeLevels) {
            checkIfFrozen();
            this.treeLevels = treeLevels;
        }

        public double precisionInMeters() {
            return precisionInMeters;
        }

        public void setPrecisionInMeters(double precisionInMeters) {
            checkIfFrozen();
            this.precisionInMeters = precisionInMeters;
        }

        public double distanceErrorPct() {
            return distanceErrorPct == null ? defaultDistanceErrorPct : distanceErrorPct;
        }

        public void setDistanceErrorPct(double distanceErrorPct) {
            checkIfFrozen();
            this.distanceErrorPct = distanceErrorPct;
        }

        public void setDefaultDistanceErrorPct(double defaultDistanceErrorPct) {
            checkIfFrozen();
            this.defaultDistanceErrorPct = defaultDistanceErrorPct;
        }

        public Orientation orientation() { return this.orientation; }

        public void setOrientation(Orientation orientation) {
            checkIfFrozen();
            this.orientation = orientation;
        }

        public PrefixTreeStrategy defaultStrategy() {
            return this.defaultStrategy;
        }

        public PrefixTreeStrategy resolveStrategy(SpatialStrategy strategy) {
            return resolveStrategy(strategy.getStrategyName());
        }

        public PrefixTreeStrategy resolveStrategy(String strategyName) {
            if (SpatialStrategy.RECURSIVE.getStrategyName().equals(strategyName)) {
                return recursiveStrategy;
            }
            if (SpatialStrategy.TERM.getStrategyName().equals(strategyName)) {
                return termStrategy;
            }
            throw new IllegalArgumentException("Unknown prefix tree strategy [" + strategyName + "]");
        }

        @Override
        public Query termQuery(Object value, QueryShardContext context) {
            throw new QueryShardException(context, "Geo fields do not support exact searching, use dedicated geo queries instead");
        }

        @Override
        public FieldStats stats(IndexReader reader) throws IOException {
            int maxDoc = reader.maxDoc();
            FieldInfo fi = org.apache.lucene.index.MultiFields.getMergedFieldInfos(reader).fieldInfo(name());
            if (fi == null) {
                return null;
            }
            /**
             * we don't have a specific type for geo_shape so we use an empty {@link FieldStats.Text}.
             * TODO: we should maybe support a new type that knows how to (de)encode the min/max information
             */
            return new FieldStats.Text(maxDoc, -1, -1, -1, isSearchable(), isAggregatable());
        }
    }

    protected Explicit<Boolean> coerce;

    public GeoShapeFieldMapper(String simpleName, MappedFieldType fieldType, Explicit<Boolean> coerce, Settings indexSettings,
                               MultiFields multiFields, CopyTo copyTo) {
        super(simpleName, fieldType, Defaults.FIELD_TYPE, indexSettings, multiFields, copyTo);
        this.coerce = coerce;
    }

    @Override
    public GeoShapeFieldType fieldType() {
        return (GeoShapeFieldType) super.fieldType();
    }

    @Override
    protected void doMerge(Mapper mergeWith, boolean updateAllTypes) {
        super.doMerge(mergeWith, updateAllTypes);

        GeoShapeFieldMapper gsfm = (GeoShapeFieldMapper)mergeWith;
        if (gsfm.coerce.explicit()) {
            this.coerce = gsfm.coerce;
        }
    }

    @Override
    protected void doXContentBody(XContentBuilder builder, boolean includeDefaults, Params params) throws IOException {
        builder.field("type", contentType());

        if (includeDefaults || fieldType().tree().equals(Defaults.TREE) == false) {
            builder.field(Names.TREE, fieldType().tree());
        }
        if (includeDefaults || fieldType().treeLevels() != 0) {
            builder.field(Names.TREE_LEVELS, fieldType().treeLevels());
        }
        if (includeDefaults || fieldType().precisionInMeters() != -1) {
            builder.field(Names.TREE_PRESISION, DistanceUnit.METERS.toString(fieldType().precisionInMeters()));
        }
        if (includeDefaults || fieldType().strategyName() != Defaults.STRATEGY) {
            builder.field(Names.STRATEGY, fieldType().strategyName());
        }
        if (includeDefaults || fieldType().distanceErrorPct() != fieldType().defaultDistanceErrorPct) {
            builder.field(Names.DISTANCE_ERROR_PCT, fieldType().distanceErrorPct());
        }
        if (includeDefaults || fieldType().orientation() != Defaults.ORIENTATION) {
            builder.field(Names.ORIENTATION, fieldType().orientation());
        }
        if (includeDefaults || fieldType().pointsOnly() != GeoShapeFieldMapper.Defaults.POINTS_ONLY) {
            builder.field(Names.STRATEGY_POINTS_ONLY, fieldType().pointsOnly());
        }
        if (includeDefaults || coerce.explicit()) {
            builder.field("coerce", coerce.value());
        }
    }

    public Explicit<Boolean> coerce() {
        return coerce;
    }

    @Override
    protected String contentType() {
        return CONTENT_TYPE;
    }
}
