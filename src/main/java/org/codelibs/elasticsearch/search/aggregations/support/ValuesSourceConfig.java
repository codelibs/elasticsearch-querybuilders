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
package org.codelibs.elasticsearch.search.aggregations.support;

import org.apache.lucene.util.BytesRef;
import org.codelibs.elasticsearch.common.Nullable;
import org.codelibs.elasticsearch.common.geo.GeoPoint;
import org.codelibs.elasticsearch.common.geo.GeoUtils;
import org.codelibs.elasticsearch.index.fielddata.IndexGeoPointFieldData;
import org.codelibs.elasticsearch.index.fielddata.IndexNumericFieldData;
import org.codelibs.elasticsearch.index.query.QueryShardContext;
import org.codelibs.elasticsearch.script.Script;
import org.codelibs.elasticsearch.script.SearchScript;
import org.codelibs.elasticsearch.search.DocValueFormat;
import org.codelibs.elasticsearch.search.aggregations.AggregationExecutionException;
import org.joda.time.DateTimeZone;

import java.io.IOException;

/**
 * A configuration that tells aggregations how to retrieve data from the index
 * in order to run a specific aggregation.
 */
public class ValuesSourceConfig<VS extends ValuesSource> {

    /**
     * Resolve a {@link ValuesSourceConfig} given configuration parameters.
     */
    public static <VS extends ValuesSource> ValuesSourceConfig<VS> resolve(
            QueryShardContext context,
            ValueType valueType,
            String field, Script script,
            Object missing,
            DateTimeZone timeZone,
            String format) {

        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    private final ValuesSourceType valueSourceType;
    private FieldContext fieldContext;
    private SearchScript script;
    private ValueType scriptValueType;
    private boolean unmapped = false;
    private DocValueFormat format = DocValueFormat.RAW;
    private Object missing;
    private DateTimeZone timeZone;

    public ValuesSourceConfig(ValuesSourceType valueSourceType) {
        this.valueSourceType = valueSourceType;
    }

    public ValuesSourceType valueSourceType() {
        return valueSourceType;
    }

    public FieldContext fieldContext() {
        return fieldContext;
    }

    public SearchScript script() {
        return script;
    }

    public boolean unmapped() {
        return unmapped;
    }

    public boolean valid() {
        return fieldContext != null || script != null || unmapped;
    }

    public ValuesSourceConfig<VS> fieldContext(FieldContext fieldContext) {
        this.fieldContext = fieldContext;
        return this;
    }

    public ValuesSourceConfig<VS> script(SearchScript script) {
        this.script = script;
        return this;
    }

    public ValuesSourceConfig<VS> scriptValueType(ValueType scriptValueType) {
        this.scriptValueType = scriptValueType;
        return this;
    }

    public ValueType scriptValueType() {
        return this.scriptValueType;
    }

    public ValuesSourceConfig<VS> unmapped(boolean unmapped) {
        this.unmapped = unmapped;
        return this;
    }

    public ValuesSourceConfig<VS> format(final DocValueFormat format) {
        this.format = format;
        return this;
    }

    public ValuesSourceConfig<VS> missing(final Object missing) {
        this.missing = missing;
        return this;
    }

    public Object missing() {
        return this.missing;
    }

    public ValuesSourceConfig<VS> timezone(final DateTimeZone timeZone) {
        this.timeZone= timeZone;
        return this;
    }

    public DateTimeZone timezone() {
        return this.timeZone;
    }

    public DocValueFormat format() {
        return format;
    }

    /** Get a value source given its configuration. A return value of null indicates that
     *  no value source could be built. */
    @Nullable
    public VS toValuesSource(QueryShardContext context) throws IOException {
        if (!valid()) {
            throw new IllegalStateException(
                    "value source config is invalid; must have either a field context or a script or marked as unwrapped");
        }

        final VS vs;
        if (unmapped()) {
            if (missing() == null) {
                // otherwise we will have values because of the missing value
                vs = null;
            } else if (valueSourceType() == ValuesSourceType.NUMERIC) {
                vs = (VS) ValuesSource.Numeric.EMPTY;
            } else if (valueSourceType() == ValuesSourceType.GEOPOINT) {
                vs = (VS) ValuesSource.GeoPoint.EMPTY;
            } else if (valueSourceType() == ValuesSourceType.ANY || valueSourceType() == ValuesSourceType.BYTES) {
                vs = (VS) ValuesSource.Bytes.WithOrdinals.EMPTY;
            } else {
                throw new IllegalArgumentException("Can't deal with unmapped ValuesSource type " + valueSourceType());
            }
        } else {
            vs = originalValuesSource();
        }

        if (missing() == null) {
            return vs;
        }

        if (vs instanceof ValuesSource.Bytes) {
            final BytesRef missing = new BytesRef(missing().toString());
            if (vs instanceof ValuesSource.Bytes.WithOrdinals) {
                return (VS) MissingValues.replaceMissing((ValuesSource.Bytes.WithOrdinals) vs, missing);
            } else {
                return (VS) MissingValues.replaceMissing((ValuesSource.Bytes) vs, missing);
            }
        } else if (vs instanceof ValuesSource.Numeric) {
            Number missing = format.parseDouble(missing().toString(), false, context::nowInMillis);
            return (VS) MissingValues.replaceMissing((ValuesSource.Numeric) vs, missing);
        } else if (vs instanceof ValuesSource.GeoPoint) {
            // TODO: also support the structured formats of geo points
            final GeoPoint missing = GeoUtils.parseGeoPoint(missing().toString(), new GeoPoint());
            return (VS) MissingValues.replaceMissing((ValuesSource.GeoPoint) vs, missing);
        } else {
            // Should not happen
            throw new IllegalArgumentException("Can't apply missing values on a " + vs.getClass());
        }
    }

    /**
     * Return the original values source, before we apply `missing`.
     */
    private VS originalValuesSource() throws IOException {
        if (fieldContext() == null) {
            if (valueSourceType() == ValuesSourceType.NUMERIC) {
                return (VS) numericScript();
            }
            if (valueSourceType() == ValuesSourceType.BYTES) {
                return (VS) bytesScript();
            }
            throw new AggregationExecutionException("value source of type [" + valueSourceType().name()
                    + "] is not supported by scripts");
        }

        if (valueSourceType() == ValuesSourceType.NUMERIC) {
            return (VS) numericField();
        }
        if (valueSourceType() == ValuesSourceType.GEOPOINT) {
            return (VS) geoPointField();
        }
        // falling back to bytes values
        return (VS) bytesField();
    }

    private ValuesSource.Numeric numericScript() throws IOException {
        return new ValuesSource.Numeric.Script(script(), scriptValueType());
    }

    private ValuesSource.Numeric numericField() throws IOException {

        if (!(fieldContext().indexFieldData() instanceof IndexNumericFieldData)) {
            throw new IllegalArgumentException("Expected numeric type on field [" + fieldContext().field() +
                    "], but got [" + fieldContext().fieldType().typeName() + "]");
        }

        ValuesSource.Numeric dataSource = new ValuesSource.Numeric.FieldData((IndexNumericFieldData)fieldContext().indexFieldData());
        if (script() != null) {
            dataSource = new ValuesSource.Numeric.WithScript(dataSource, script());
        }
        return dataSource;
    }

    private ValuesSource bytesField() throws IOException {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    private ValuesSource.Bytes bytesScript() throws IOException {
        return new ValuesSource.Bytes.Script(script());
    }

    private ValuesSource.GeoPoint geoPointField() throws IOException {

        if (!(fieldContext().indexFieldData() instanceof IndexGeoPointFieldData)) {
            throw new IllegalArgumentException("Expected geo_point type on field [" + fieldContext().field() +
                    "], but got [" + fieldContext().fieldType().typeName() + "]");
        }

        return new ValuesSource.GeoPoint.Fielddata((IndexGeoPointFieldData) fieldContext().indexFieldData());
    }
}
