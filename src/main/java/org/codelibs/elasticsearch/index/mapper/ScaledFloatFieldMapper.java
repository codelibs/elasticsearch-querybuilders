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

import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedNumericDocValues;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.Query;
import org.codelibs.elasticsearch.action.fieldstats.FieldStats;
import org.codelibs.elasticsearch.common.Explicit;
import org.codelibs.elasticsearch.common.settings.Setting;
import org.codelibs.elasticsearch.common.settings.Settings;
import org.codelibs.elasticsearch.common.xcontent.XContentBuilder;
import org.codelibs.elasticsearch.index.fielddata.AtomicNumericFieldData;
import org.codelibs.elasticsearch.index.fielddata.FieldData;
import org.codelibs.elasticsearch.index.fielddata.IndexFieldData;
import org.codelibs.elasticsearch.index.fielddata.NumericDoubleValues;
import org.codelibs.elasticsearch.index.fielddata.ScriptDocValues;
import org.codelibs.elasticsearch.index.fielddata.SortedBinaryDocValues;
import org.codelibs.elasticsearch.index.fielddata.SortedNumericDoubleValues;
import org.codelibs.elasticsearch.index.mapper.LegacyNumberFieldMapper.Defaults;
import org.codelibs.elasticsearch.index.query.QueryShardContext;
import org.codelibs.elasticsearch.search.DocValueFormat;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A {FieldMapper} for scaled floats. Values are internally multiplied
 *  by a scaling factor and rounded to the closest long. */
public class ScaledFloatFieldMapper extends FieldMapper {

    public static final String CONTENT_TYPE = "scaled_float";
    // use the same default as numbers
    private static final Setting<Boolean> COERCE_SETTING = NumberFieldMapper.COERCE_SETTING;

    public static class Builder extends FieldMapper.Builder<Builder, ScaledFloatFieldMapper> {

        private boolean scalingFactorSet = false;
        private Boolean ignoreMalformed;
        private Boolean coerce;

        public Builder(String name) {
            super(name, new ScaledFloatFieldType(), new ScaledFloatFieldType());
            builder = this;
        }

        public Builder ignoreMalformed(boolean ignoreMalformed) {
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

        public Builder coerce(boolean coerce) {
            this.coerce = coerce;
            return builder;
        }

        public Builder scalingFactor(double scalingFactor) {
            ((ScaledFloatFieldType) fieldType).setScalingFactor(scalingFactor);
            scalingFactorSet = true;
            return this;
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
        public ScaledFloatFieldMapper build(BuilderContext context) {
            if (scalingFactorSet == false) {
                throw new IllegalArgumentException("Field [" + name + "] misses required parameter [scaling_factor]");
            }
            setupFieldType(context);
            return new ScaledFloatFieldMapper(name, fieldType, defaultFieldType, ignoreMalformed(context),
                    coerce(context), includeInAll, context.indexSettings(), multiFieldsBuilder.build(this, context), copyTo);
        }
    }

    public static class TypeParser implements Mapper.TypeParser {
    }

    public static final class ScaledFloatFieldType extends MappedFieldType {

        private double scalingFactor;

        public ScaledFloatFieldType() {
            super();
            setTokenized(false);
            setHasDocValues(true);
            setOmitNorms(true);
        }

        ScaledFloatFieldType(ScaledFloatFieldType other) {
            super(other);
            this.scalingFactor = other.scalingFactor;
        }

        public double getScalingFactor() {
            return scalingFactor;
        }

        public void setScalingFactor(double scalingFactor) {
            checkIfFrozen();
            this.scalingFactor = scalingFactor;
        }

        @Override
        public MappedFieldType clone() {
            return new ScaledFloatFieldType(this);
        }

        @Override
        public String typeName() {
            return CONTENT_TYPE;
        }

        @Override
        public void checkCompatibility(MappedFieldType other, List<String> conflicts, boolean strict) {
            super.checkCompatibility(other, conflicts, strict);
            if (scalingFactor != ((ScaledFloatFieldType) other).getScalingFactor()) {
                conflicts.add("mapper [" + name() + "] has different [scaling_factor] values");
            }
        }

        @Override
        public Query termQuery(Object value, QueryShardContext context) {
            failIfNotIndexed();
            double queryValue = NumberFieldMapper.NumberType.DOUBLE.parse(value, false).doubleValue();
            long scaledValue = Math.round(queryValue * scalingFactor);
            Query query = NumberFieldMapper.NumberType.LONG.termQuery(name(), scaledValue);
            if (boost() != 1f) {
                query = new BoostQuery(query, boost());
            }
            return query;
        }

        @Override
        public Query termsQuery(List values, QueryShardContext context) {
            failIfNotIndexed();
            List<Long> scaledValues = new ArrayList<>(values.size());
            for (Object value : values) {
                double queryValue = NumberFieldMapper.NumberType.DOUBLE.parse(value, false).doubleValue();
                long scaledValue = Math.round(queryValue * scalingFactor);
                scaledValues.add(scaledValue);
            }
            Query query = NumberFieldMapper.NumberType.LONG.termsQuery(name(), Collections.unmodifiableList(scaledValues));
            if (boost() != 1f) {
                query = new BoostQuery(query, boost());
            }
            return query;
        }

        @Override
        public Query rangeQuery(Object lowerTerm, Object upperTerm, boolean includeLower, boolean includeUpper, QueryShardContext context) {
            failIfNotIndexed();
            Long lo = null;
            if (lowerTerm != null) {
                double dValue = NumberFieldMapper.NumberType.DOUBLE.parse(lowerTerm, false).doubleValue();
                if (includeLower == false) {
                    dValue = Math.nextUp(dValue);
                }
                lo = Math.round(Math.ceil(dValue * scalingFactor));
            }
            Long hi = null;
            if (upperTerm != null) {
                double dValue = NumberFieldMapper.NumberType.DOUBLE.parse(upperTerm, false).doubleValue();
                if (includeUpper == false) {
                    dValue = Math.nextDown(dValue);
                }
                hi = Math.round(Math.floor(dValue * scalingFactor));
            }
            Query query = NumberFieldMapper.NumberType.LONG.rangeQuery(name(), lo, hi, true, true);
            if (boost() != 1f) {
                query = new BoostQuery(query, boost());
            }
            return query;
        }

        @Override
        public FieldStats<?> stats(IndexReader reader) throws IOException {
            FieldStats.Long stats = (FieldStats.Long) NumberFieldMapper.NumberType.LONG.stats(
                    reader, name(), isSearchable(), isAggregatable());
            if (stats == null) {
                return null;
            }
            return new FieldStats.Double(stats.getMaxDoc(), stats.getDocCount(),
                    stats.getSumDocFreq(), stats.getSumTotalTermFreq(),
                    stats.isSearchable(), stats.isAggregatable(),
                    stats.getMinValue() == null ? null : stats.getMinValue() / scalingFactor,
                    stats.getMaxValue() == null ? null : stats.getMaxValue() / scalingFactor);
        }

        @Override
        public IndexFieldData.Builder fielddataBuilder() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object valueForDisplay(Object value) {
            if (value == null) {
                return null;
            }
            return ((Number) value).longValue() / scalingFactor;
        }

        @Override
        public DocValueFormat docValueFormat(String format, DateTimeZone timeZone) {
            if (timeZone != null) {
                throw new IllegalArgumentException("Field [" + name() + "] of type [" + typeName()
                    + "] does not support custom time zones");
            }
            if (format == null) {
                return DocValueFormat.RAW;
            } else {
                return new DocValueFormat.Decimal(format);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (super.equals(o) == false) {
                return false;
            }
            return scalingFactor == ((ScaledFloatFieldType) o).scalingFactor;
        }

        @Override
        public int hashCode() {
            return 31 * super.hashCode() + Double.hashCode(scalingFactor);
        }
    }

    private Boolean includeInAll;

    private Explicit<Boolean> ignoreMalformed;

    private Explicit<Boolean> coerce;

    private ScaledFloatFieldMapper(
            String simpleName,
            MappedFieldType fieldType,
            MappedFieldType defaultFieldType,
            Explicit<Boolean> ignoreMalformed,
            Explicit<Boolean> coerce,
            Boolean includeInAll,
            Settings indexSettings,
            MultiFields multiFields,
            CopyTo copyTo) {
        super(simpleName, fieldType, defaultFieldType, indexSettings, multiFields, copyTo);
        final double scalingFactor = fieldType().getScalingFactor();
        if (Double.isFinite(scalingFactor) == false || scalingFactor <= 0) {
            throw new IllegalArgumentException("[scaling_factor] must be a positive number, got [" + scalingFactor + "]");
        }
        this.ignoreMalformed = ignoreMalformed;
        this.coerce = coerce;
        this.includeInAll = includeInAll;
    }

    @Override
    public ScaledFloatFieldType fieldType() {
        return (ScaledFloatFieldType) super.fieldType();
    }

    @Override
    protected String contentType() {
        return fieldType.typeName();
    }

    @Override
    protected ScaledFloatFieldMapper clone() {
        return (ScaledFloatFieldMapper) super.clone();
    }

    @Override
    protected void doMerge(Mapper mergeWith, boolean updateAllTypes) {
        super.doMerge(mergeWith, updateAllTypes);
        ScaledFloatFieldMapper other = (ScaledFloatFieldMapper) mergeWith;
        this.includeInAll = other.includeInAll;
        if (other.ignoreMalformed.explicit()) {
            this.ignoreMalformed = other.ignoreMalformed;
        }
        if (other.coerce.explicit()) {
            this.coerce = other.coerce;
        }
    }

    @Override
    protected void doXContentBody(XContentBuilder builder, boolean includeDefaults, Params params) throws IOException {
        super.doXContentBody(builder, includeDefaults, params);

        builder.field("scaling_factor", fieldType().getScalingFactor());

        if (includeDefaults || ignoreMalformed.explicit()) {
            builder.field("ignore_malformed", ignoreMalformed.value());
        }
        if (includeDefaults || coerce.explicit()) {
            builder.field("coerce", coerce.value());
        }

        if (includeDefaults || fieldType().nullValue() != null) {
            builder.field("null_value", fieldType().nullValue());
        }

        if (includeInAll != null) {
            builder.field("include_in_all", includeInAll);
        } else if (includeDefaults) {
            builder.field("include_in_all", false);
        }
    }
}
