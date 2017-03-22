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

package org.codelibs.elasticsearch.index.query;

import org.apache.lucene.search.MatchNoDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.spatial.geopoint.document.GeoPointField;
import org.codelibs.elasticsearch.Version;
import org.codelibs.elasticsearch.common.ParseField;
import org.codelibs.elasticsearch.common.ParsingException;
import org.codelibs.elasticsearch.common.Strings;
import org.codelibs.elasticsearch.common.geo.GeoDistance;
import org.codelibs.elasticsearch.common.geo.GeoPoint;
import org.codelibs.elasticsearch.common.geo.GeoUtils;
import org.codelibs.elasticsearch.common.io.stream.StreamInput;
import org.codelibs.elasticsearch.common.io.stream.StreamOutput;
import org.codelibs.elasticsearch.common.logging.DeprecationLogger;
import org.codelibs.elasticsearch.common.logging.Loggers;
import org.codelibs.elasticsearch.common.unit.DistanceUnit;
import org.codelibs.elasticsearch.common.xcontent.XContentBuilder;
import org.codelibs.elasticsearch.common.xcontent.XContentParser;
import org.codelibs.elasticsearch.index.fielddata.IndexGeoPointFieldData;
import org.codelibs.elasticsearch.index.mapper.MappedFieldType;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class GeoDistanceRangeQueryBuilder extends AbstractQueryBuilder<GeoDistanceRangeQueryBuilder> {
    public static final String NAME = "geo_distance_range";

    private static final DeprecationLogger deprecationLogger = new DeprecationLogger(Loggers.getLogger(GeoDistanceRangeQueryBuilder.class));

    public static final boolean DEFAULT_INCLUDE_LOWER = true;
    public static final boolean DEFAULT_INCLUDE_UPPER = true;
    public static final GeoDistance DEFAULT_GEO_DISTANCE = GeoDistance.DEFAULT;
    public static final DistanceUnit DEFAULT_UNIT = DistanceUnit.DEFAULT;
    @Deprecated
    public static final String DEFAULT_OPTIMIZE_BBOX = "memory";

    /**
     * The default value for ignore_unmapped.
     */
    public static final boolean DEFAULT_IGNORE_UNMAPPED = false;

    private static final ParseField FROM_FIELD = new ParseField("from");
    private static final ParseField TO_FIELD = new ParseField("to");
    private static final ParseField INCLUDE_LOWER_FIELD = new ParseField("include_lower");
    private static final ParseField INCLUDE_UPPER_FIELD = new ParseField("include_upper");
    private static final ParseField GT_FIELD = new ParseField("gt");
    private static final ParseField GTE_FIELD = new ParseField("gte", "ge");
    private static final ParseField LT_FIELD = new ParseField("lt");
    private static final ParseField LTE_FIELD = new ParseField("lte", "le");
    private static final ParseField UNIT_FIELD = new ParseField("unit");
    private static final ParseField DISTANCE_TYPE_FIELD = new ParseField("distance_type");
    private static final ParseField NAME_FIELD = new ParseField("_name");
    private static final ParseField BOOST_FIELD = new ParseField("boost");
    @Deprecated
    private static final ParseField OPTIMIZE_BBOX_FIELD = new ParseField("optimize_bbox")
            .withAllDeprecated("no replacement: `optimize_bbox` is no longer supported due to recent improvements");
    private static final ParseField COERCE_FIELD = new ParseField("coerce", "normalize")
            .withAllDeprecated("use validation_method instead");
    private static final ParseField IGNORE_MALFORMED_FIELD = new ParseField("ignore_malformed")
            .withAllDeprecated("use validation_method instead");
    private static final ParseField VALIDATION_METHOD = new ParseField("validation_method");
    private static final ParseField IGNORE_UNMAPPED_FIELD = new ParseField("ignore_unmapped");

    private final String fieldName;

    private Object from;
    private Object to;
    private boolean includeLower = DEFAULT_INCLUDE_LOWER;
    private boolean includeUpper = DEFAULT_INCLUDE_UPPER;

    private boolean ignoreUnmapped = DEFAULT_IGNORE_UNMAPPED;

    private final GeoPoint point;

    private GeoDistance geoDistance = DEFAULT_GEO_DISTANCE;

    private DistanceUnit unit = DEFAULT_UNIT;

    private String optimizeBbox = null;

    private GeoValidationMethod validationMethod = GeoValidationMethod.DEFAULT;

    public GeoDistanceRangeQueryBuilder(String fieldName, GeoPoint point) {
        if (Strings.isEmpty(fieldName)) {
            throw new IllegalArgumentException("fieldName must not be null");
        }
        if (point == null) {
            throw new IllegalArgumentException("point must not be null");
        }
        this.fieldName = fieldName;
        this.point = point;
    }

    public GeoDistanceRangeQueryBuilder(String fieldName, double lat, double lon) {
        this(fieldName, new GeoPoint(lat, lon));
    }

    public GeoDistanceRangeQueryBuilder(String fieldName, String geohash) {
        this(fieldName, geohash == null ? null : new GeoPoint().resetFromGeoHash(geohash));
    }

    /**
     * Read from a stream.
     */
    public GeoDistanceRangeQueryBuilder(StreamInput in) throws IOException {
        super(in);
        fieldName = in.readString();
        point = in.readGeoPoint();
        from = in.readGenericValue();
        to = in.readGenericValue();
        includeLower = in.readBoolean();
        includeUpper = in.readBoolean();
        unit = DistanceUnit.valueOf(in.readString());
        geoDistance = GeoDistance.readFromStream(in);
        optimizeBbox = in.readOptionalString();
        validationMethod = GeoValidationMethod.readFromStream(in);
        ignoreUnmapped = in.readBoolean();
    }

    @Override
    protected void doWriteTo(StreamOutput out) throws IOException {
        out.writeString(fieldName);
        out.writeGeoPoint(point);
        out.writeGenericValue(from);
        out.writeGenericValue(to);
        out.writeBoolean(includeLower);
        out.writeBoolean(includeUpper);
        out.writeString(unit.name());
        geoDistance.writeTo(out);;
        out.writeOptionalString(optimizeBbox);
        validationMethod.writeTo(out);
        out.writeBoolean(ignoreUnmapped);
    }

    public String fieldName() {
        return fieldName;
    }

    public GeoPoint point() {
        return point;
    }

    public GeoDistanceRangeQueryBuilder from(String from) {
        if (from == null) {
            throw new IllegalArgumentException("[from] must not be null");
        }
        this.from = from;
        return this;
    }

    public GeoDistanceRangeQueryBuilder from(Number from) {
        if (from == null) {
            throw new IllegalArgumentException("[from] must not be null");
        }
        this.from = from;
        return this;
    }

    public Object from() {
        return from;
    }

    public GeoDistanceRangeQueryBuilder to(String to) {
        if (to == null) {
            throw new IllegalArgumentException("[to] must not be null");
        }
        this.to = to;
        return this;
    }

    public GeoDistanceRangeQueryBuilder to(Number to) {
        if (to == null) {
            throw new IllegalArgumentException("[to] must not be null");
        }
        this.to = to;
        return this;
    }

    public Object to() {
        return to;
    }

    public GeoDistanceRangeQueryBuilder includeLower(boolean includeLower) {
        this.includeLower = includeLower;
        return this;
    }

    public boolean includeLower() {
        return includeLower;
    }

    public GeoDistanceRangeQueryBuilder includeUpper(boolean includeUpper) {
        this.includeUpper = includeUpper;
        return this;
    }

    public boolean includeUpper() {
        return includeUpper;
    }

    public GeoDistanceRangeQueryBuilder geoDistance(GeoDistance geoDistance) {
        if (geoDistance == null) {
            throw new IllegalArgumentException("geoDistance calculation mode must not be null");
        }
        this.geoDistance = geoDistance;
        return this;
    }

    public GeoDistance geoDistance() {
        return geoDistance;
    }

    public GeoDistanceRangeQueryBuilder unit(DistanceUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("distance unit must not be null");
        }
        this.unit = unit;
        return this;
    }

    public DistanceUnit unit() {
        return unit;
    }

    @Deprecated
    public GeoDistanceRangeQueryBuilder optimizeBbox(String optimizeBbox) {
        this.optimizeBbox = optimizeBbox;
        return this;
    }

    @Deprecated
    public String optimizeBbox() {
        return optimizeBbox;
    }

    /** Set validation method for coordinates. */
    public GeoDistanceRangeQueryBuilder setValidationMethod(GeoValidationMethod method) {
        this.validationMethod = method;
        return this;
    }

    /** Returns validation method for coordinates. */
    public GeoValidationMethod getValidationMethod() {
        return this.validationMethod;
    }

    /**
     * Sets whether the query builder should ignore unmapped fields (and run a
     * {@link MatchNoDocsQuery} in place of this query) or throw an exception if
     * the field is unmapped.
     */
    public GeoDistanceRangeQueryBuilder ignoreUnmapped(boolean ignoreUnmapped) {
        this.ignoreUnmapped = ignoreUnmapped;
        return this;
    }

    /**
     * Gets whether the query builder will ignore unmapped fields (and run a
     * {@link MatchNoDocsQuery} in place of this query) or throw an exception if
     * the field is unmapped.
     */
    public boolean ignoreUnmapped() {
        return ignoreUnmapped;
    }

    @Override
    protected Query doToQuery(QueryShardContext context) throws IOException {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    protected void doXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject(NAME);
        builder.startArray(fieldName).value(point.lon()).value(point.lat()).endArray();
        builder.field(FROM_FIELD.getPreferredName(), from);
        builder.field(TO_FIELD.getPreferredName(), to);
        builder.field(INCLUDE_LOWER_FIELD.getPreferredName(), includeLower);
        builder.field(INCLUDE_UPPER_FIELD.getPreferredName(), includeUpper);
        builder.field(UNIT_FIELD.getPreferredName(), unit);
        builder.field(DISTANCE_TYPE_FIELD.getPreferredName(), geoDistance.name().toLowerCase(Locale.ROOT));
        if (Strings.isEmpty(optimizeBbox) == false) {
            builder.field(OPTIMIZE_BBOX_FIELD.getPreferredName(), optimizeBbox);
        }
        builder.field(VALIDATION_METHOD.getPreferredName(), validationMethod);
        builder.field(IGNORE_UNMAPPED_FIELD.getPreferredName(), ignoreUnmapped);
        printBoostAndQueryName(builder);
        builder.endObject();
    }

    public static Optional<GeoDistanceRangeQueryBuilder> fromXContent(QueryParseContext parseContext) throws IOException {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    protected boolean doEquals(GeoDistanceRangeQueryBuilder other) {
        return ((Objects.equals(fieldName, other.fieldName)) &&
                (Objects.equals(point, other.point)) &&
                (Objects.equals(from, other.from)) &&
                (Objects.equals(to, other.to)) &&
                (Objects.equals(includeUpper, other.includeUpper)) &&
                (Objects.equals(includeLower, other.includeLower)) &&
                (Objects.equals(geoDistance, other.geoDistance)) &&
                (Objects.equals(optimizeBbox, other.optimizeBbox)) &&
                (Objects.equals(validationMethod, other.validationMethod))) &&
                Objects.equals(ignoreUnmapped, other.ignoreUnmapped);
    }

    @Override
    protected int doHashCode() {
        return Objects.hash(fieldName, point, from, to, includeUpper, includeLower, geoDistance, optimizeBbox, validationMethod,
                ignoreUnmapped);
    }

    @Override
    public String getWriteableName() {
        return NAME;
    }
}
