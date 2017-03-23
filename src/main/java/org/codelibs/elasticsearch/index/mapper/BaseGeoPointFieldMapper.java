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
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.codelibs.elasticsearch.action.fieldstats.FieldStats;
import org.codelibs.elasticsearch.common.Explicit;
import org.codelibs.elasticsearch.common.Nullable;
import org.codelibs.elasticsearch.common.geo.GeoHashUtils;
import org.codelibs.elasticsearch.common.logging.DeprecationLogger;
import org.codelibs.elasticsearch.common.logging.Loggers;
import org.codelibs.elasticsearch.common.settings.Settings;
import org.codelibs.elasticsearch.common.xcontent.XContentBuilder;
import org.codelibs.elasticsearch.index.fielddata.IndexFieldData;
import org.codelibs.elasticsearch.index.query.QueryShardContext;
import org.codelibs.elasticsearch.index.query.QueryShardException;
import org.codelibs.elasticsearch.search.DocValueFormat;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * GeoPointFieldMapper base class to maintain backward compatibility
 */
public abstract class BaseGeoPointFieldMapper extends FieldMapper {
    public static final String CONTENT_TYPE = "geo_point";
    protected static final DeprecationLogger deprecationLogger = new DeprecationLogger(Loggers.getLogger(BaseGeoPointFieldMapper.class));
    public static class Names {
        public static final String LAT = "lat";
        public static final String LAT_SUFFIX = "." + LAT;
        public static final String LON = "lon";
        public static final String LON_SUFFIX = "." + LON;
        public static final String GEOHASH = "geohash";
        public static final String IGNORE_MALFORMED = "ignore_malformed";
    }

    public static class Defaults {
        public static final boolean ENABLE_LATLON = false;
        public static final boolean ENABLE_GEOHASH = false;
        public static final boolean ENABLE_GEOHASH_PREFIX = false;
        public static final int GEO_HASH_PRECISION = GeoHashUtils.PRECISION;
        public static final Explicit<Boolean> IGNORE_MALFORMED = new Explicit<>(false, false);
    }

    public abstract static class Builder<T extends Builder, Y extends BaseGeoPointFieldMapper> extends FieldMapper.Builder<T, Y> {

        protected boolean enableLatLon = Defaults.ENABLE_LATLON;

        protected Integer precisionStep;

        protected boolean enableGeoHash = Defaults.ENABLE_GEOHASH;

        protected boolean enableGeoHashPrefix = Defaults.ENABLE_GEOHASH_PREFIX;

        protected int geoHashPrecision = Defaults.GEO_HASH_PRECISION;

        protected Boolean ignoreMalformed;

        public Builder(String name, MappedFieldType fieldType) {
            super(name, fieldType, fieldType);
        }

        @Override
        public GeoPointFieldType fieldType() {
            return (GeoPointFieldType)fieldType;
        }

        public T enableLatLon(boolean enableLatLon) {
            this.enableLatLon = enableLatLon;
            return builder;
        }

        public T precisionStep(int precisionStep) {
            this.precisionStep = precisionStep;
            return builder;
        }

        public T enableGeoHash(boolean enableGeoHash) {
            this.enableGeoHash = enableGeoHash;
            return builder;
        }

        public T geoHashPrefix(boolean enableGeoHashPrefix) {
            this.enableGeoHashPrefix = enableGeoHashPrefix;
            return builder;
        }

        public T geoHashPrecision(int precision) {
            this.geoHashPrecision = precision;
            return builder;
        }

        public T ignoreMalformed(boolean ignoreMalformed) {
            this.ignoreMalformed = ignoreMalformed;
            return builder;
        }

        protected Explicit<Boolean> ignoreMalformed(BuilderContext context) {
            if (ignoreMalformed != null) {
                return new Explicit<>(ignoreMalformed, true);
            }
            if (context.indexSettings() != null) {
                return new Explicit<>(IGNORE_MALFORMED_SETTING.get(context.indexSettings()), false);
            }
            return Defaults.IGNORE_MALFORMED;
        }

        public abstract Y build(BuilderContext context, String simpleName, MappedFieldType fieldType, MappedFieldType defaultFieldType,
                                Settings indexSettings, FieldMapper latMapper, FieldMapper lonMapper,
                                FieldMapper geoHashMapper, MultiFields multiFields, Explicit<Boolean> ignoreMalformed, CopyTo copyTo);

        @Override
        public Y build(Mapper.BuilderContext context) {
            throw new UnsupportedOperationException();
        }
    }

    public abstract static class TypeParser implements Mapper.TypeParser {
    }

    public abstract static class GeoPointFieldType extends MappedFieldType {
        GeoPointFieldType() {
        }

        GeoPointFieldType(GeoPointFieldType ref) {
            super(ref);
        }

        @Override
        public String typeName() {
            return CONTENT_TYPE;
        }

        @Override
        public FieldStats stats(IndexReader reader) throws IOException {
            int maxDoc = reader.maxDoc();
            FieldInfo fi = org.apache.lucene.index.MultiFields.getMergedFieldInfos(reader).fieldInfo(name());
            if (fi == null) {
                return null;
            }
            /**
             * we don't have a specific type for geo_point so we use an empty {@link FieldStats.Text}.
             * TODO: we should maybe support a new type that knows how to (de)encode the min/max information
             */
            return new FieldStats.Text(maxDoc, -1, -1, -1, isSearchable(), isAggregatable());
        }
    }

    public static class LegacyGeoPointFieldType extends GeoPointFieldType {
        protected MappedFieldType geoHashFieldType;
        protected int geoHashPrecision;
        protected boolean geoHashPrefixEnabled;

        protected MappedFieldType latFieldType;
        protected MappedFieldType lonFieldType;

        LegacyGeoPointFieldType() {}

        LegacyGeoPointFieldType(LegacyGeoPointFieldType ref) {
            super(ref);
            this.geoHashFieldType = ref.geoHashFieldType; // copying ref is ok, this can never be modified
            this.geoHashPrecision = ref.geoHashPrecision;
            this.geoHashPrefixEnabled = ref.geoHashPrefixEnabled;
            this.latFieldType = ref.latFieldType; // copying ref is ok, this can never be modified
            this.lonFieldType = ref.lonFieldType; // copying ref is ok, this can never be modified
        }

        @Override
        public MappedFieldType clone() {
            return new LegacyGeoPointFieldType(this);
        }

        @Override
        public boolean equals(Object o) {
            if (!super.equals(o)) {
                return false;
            }
            LegacyGeoPointFieldType that = (LegacyGeoPointFieldType) o;
            return  geoHashPrecision == that.geoHashPrecision &&
                    geoHashPrefixEnabled == that.geoHashPrefixEnabled &&
                    java.util.Objects.equals(geoHashFieldType, that.geoHashFieldType) &&
                    java.util.Objects.equals(latFieldType, that.latFieldType) &&
                    java.util.Objects.equals(lonFieldType, that.lonFieldType);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(super.hashCode(), geoHashFieldType, geoHashPrecision, geoHashPrefixEnabled, latFieldType,
                    lonFieldType);
        }

        @Override
        public void checkCompatibility(MappedFieldType fieldType, List<String> conflicts, boolean strict) {
            super.checkCompatibility(fieldType, conflicts, strict);
            LegacyGeoPointFieldType other = (LegacyGeoPointFieldType)fieldType;
            if (isLatLonEnabled() != other.isLatLonEnabled()) {
                conflicts.add("mapper [" + name() + "] has different [lat_lon]");
            }
            if (isLatLonEnabled() && other.isLatLonEnabled() &&
                    latFieldType().numericPrecisionStep() != other.latFieldType().numericPrecisionStep()) {
                conflicts.add("mapper [" + name() + "] has different [precision_step]");
            }
            if (isGeoHashEnabled() != other.isGeoHashEnabled()) {
                conflicts.add("mapper [" + name() + "] has different [geohash]");
            }
            if (geoHashPrecision() != other.geoHashPrecision()) {
                conflicts.add("mapper [" + name() + "] has different [geohash_precision]");
            }
            if (isGeoHashPrefixEnabled() != other.isGeoHashPrefixEnabled()) {
                conflicts.add("mapper [" + name() + "] has different [geohash_prefix]");
            }
        }

        public boolean isGeoHashEnabled() {
            return geoHashFieldType != null;
        }

        public MappedFieldType geoHashFieldType() {
            return geoHashFieldType;
        }

        public int geoHashPrecision() {
            return geoHashPrecision;
        }

        public boolean isGeoHashPrefixEnabled() {
            return geoHashPrefixEnabled;
        }

        public void setGeoHashEnabled(MappedFieldType geoHashFieldType, int geoHashPrecision, boolean geoHashPrefixEnabled) {
            checkIfFrozen();
            this.geoHashFieldType = geoHashFieldType;
            this.geoHashPrecision = geoHashPrecision;
            this.geoHashPrefixEnabled = geoHashPrefixEnabled;
        }

        public boolean isLatLonEnabled() {
            return latFieldType != null;
        }

        public MappedFieldType latFieldType() {
            return latFieldType;
        }

        public MappedFieldType lonFieldType() {
            return lonFieldType;
        }

        public void setLatLonEnabled(MappedFieldType latFieldType, MappedFieldType lonFieldType) {
            checkIfFrozen();
            this.latFieldType = latFieldType;
            this.lonFieldType = lonFieldType;
        }

        @Override
        public IndexFieldData.Builder fielddataBuilder() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DocValueFormat docValueFormat(@Nullable String format, DateTimeZone timeZone) {
            if (format != null) {
                throw new IllegalArgumentException("Field [" + name() + "] of type [" + typeName() + "] does not support custom formats");
            }
            if (timeZone != null) {
                throw new IllegalArgumentException("Field [" + name() + "] of type [" + typeName()
                    + "] does not support custom time zones");
            }
            return DocValueFormat.GEOHASH;
        }

        @Override
        public Query termQuery(Object value, QueryShardContext context) {
            throw new QueryShardException(context, "Geo fields do not support exact searching, use dedicated geo queries instead: [" + name() + "]");
        }
    }

    protected FieldMapper latMapper;

    protected FieldMapper lonMapper;

    protected FieldMapper geoHashMapper;

    protected Explicit<Boolean> ignoreMalformed;

    protected BaseGeoPointFieldMapper(String simpleName, MappedFieldType fieldType, MappedFieldType defaultFieldType, Settings indexSettings,
                                      FieldMapper latMapper, FieldMapper lonMapper, FieldMapper geoHashMapper,
                                      MultiFields multiFields, Explicit<Boolean> ignoreMalformed, CopyTo copyTo) {
        super(simpleName, fieldType, defaultFieldType, indexSettings, multiFields, copyTo);
        this.latMapper = latMapper;
        this.lonMapper = lonMapper;
        this.geoHashMapper = geoHashMapper;
        this.ignoreMalformed = ignoreMalformed;
    }



    public LegacyGeoPointFieldType legacyFieldType() {
        return (LegacyGeoPointFieldType) super.fieldType();
    }

    @Override
    protected void doMerge(Mapper mergeWith, boolean updateAllTypes) {
        super.doMerge(mergeWith, updateAllTypes);
        BaseGeoPointFieldMapper gpfmMergeWith = (BaseGeoPointFieldMapper) mergeWith;
        if (gpfmMergeWith.ignoreMalformed.explicit()) {
            this.ignoreMalformed = gpfmMergeWith.ignoreMalformed;
        }
    }

    @Override
    public Iterator<Mapper> iterator() {
        throw new UnsupportedOperationException();
    }

    public Iterator<Mapper> legacyIterator() {
        List<Mapper> extras = new ArrayList<>();
        if (legacyFieldType().isGeoHashEnabled()) {
            extras.add(geoHashMapper);
        }
        if (legacyFieldType().isLatLonEnabled()) {
            extras.add(latMapper);
            extras.add(lonMapper);
        }
        return extras.iterator();
    }

    @Override
    protected String contentType() {
        return CONTENT_TYPE;
    }

    @Override
    protected void doXContentBody(XContentBuilder builder, boolean includeDefaults, Params params) throws IOException {
        super.doXContentBody(builder, includeDefaults, params);
        throw new UnsupportedOperationException();
    }

    @Override
    public FieldMapper updateFieldType(Map<String, MappedFieldType> fullNameToFieldType) {
        BaseGeoPointFieldMapper updated = (BaseGeoPointFieldMapper) super.updateFieldType(fullNameToFieldType);
        FieldMapper geoUpdated = geoHashMapper == null ? null : geoHashMapper.updateFieldType(fullNameToFieldType);
        FieldMapper latUpdated = latMapper == null ? null : latMapper.updateFieldType(fullNameToFieldType);
        FieldMapper lonUpdated = lonMapper == null ? null : lonMapper.updateFieldType(fullNameToFieldType);
        if (updated == this
                && geoUpdated == geoHashMapper
                && latUpdated == latMapper
                && lonUpdated == lonMapper) {
            return this;
        }
        if (updated == this) {
            updated = (BaseGeoPointFieldMapper) updated.clone();
        }
        updated.geoHashMapper = geoUpdated;
        updated.latMapper = latUpdated;
        updated.lonMapper = lonUpdated;
        return updated;
    }
}
