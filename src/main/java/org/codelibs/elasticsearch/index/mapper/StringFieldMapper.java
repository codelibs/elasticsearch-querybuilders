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

import org.codelibs.elasticsearch.querybuilders.mock.log4j.Logger;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.search.Query;
import org.codelibs.elasticsearch.Version;
import org.codelibs.elasticsearch.common.logging.DeprecationLogger;
import org.codelibs.elasticsearch.common.logging.Loggers;
import org.codelibs.elasticsearch.common.settings.Settings;
import org.codelibs.elasticsearch.common.xcontent.XContentBuilder;
import org.codelibs.elasticsearch.index.analysis.NamedAnalyzer;
import org.codelibs.elasticsearch.index.fielddata.IndexFieldData;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.apache.lucene.index.IndexOptions.NONE;

public class StringFieldMapper extends FieldMapper {

    public static final String CONTENT_TYPE = "string";
    private static final int POSITION_INCREMENT_GAP_USE_ANALYZER = -1;

    // If a string field is created on 5.x and all parameters are in this list then we
    // will automatically upgrade to a text/keyword field. Otherwise we will just fail
    // saying that string fields are not supported anymore.
    private static final Set<String> SUPPORTED_PARAMETERS_FOR_AUTO_UPGRADE_TO_KEYWORD = new HashSet<>(Arrays.asList(
            "type",
            // common keyword parameters, for which the upgrade is straightforward
            "index", "store", "doc_values", "omit_norms", "norms", "fields", "copy_to",
            "fielddata", "include_in_all", "ignore_above"));
    private static final Set<String> SUPPORTED_PARAMETERS_FOR_AUTO_UPGRADE_TO_TEXT = new HashSet<>(Arrays.asList(
            "type",
            // common text parameters, for which the upgrade is straightforward
            "index", "store", "doc_values", "omit_norms", "norms", "fields", "copy_to",
            "fielddata", "include_in_all", "analyzer", "search_analyzer", "search_quote_analyzer",
            "index_options", "position_increment_gap"));

    public static class Defaults {
        public static double FIELDDATA_MIN_FREQUENCY = 0;
        public static double FIELDDATA_MAX_FREQUENCY = Integer.MAX_VALUE;
        public static int FIELDDATA_MIN_SEGMENT_SIZE = 0;

        public static final MappedFieldType FIELD_TYPE = new StringFieldType();

        static {
            FIELD_TYPE.freeze();
        }

        // NOTE, when adding defaults here, make sure you add them in the builder
        public static final String NULL_VALUE = null;

        public static final int IGNORE_ABOVE = -1;
    }

    public static class Builder extends FieldMapper.Builder<Builder, StringFieldMapper> {

        private final DeprecationLogger deprecationLogger;

        protected String nullValue = Defaults.NULL_VALUE;

        /**
         * The distance between tokens from different values in the same field.
         * POSITION_INCREMENT_GAP_USE_ANALYZER means default to the analyzer's
         * setting which in turn defaults to Defaults.POSITION_INCREMENT_GAP.
         */
        protected int positionIncrementGap = POSITION_INCREMENT_GAP_USE_ANALYZER;

        protected int ignoreAbove = Defaults.IGNORE_ABOVE;

        public Builder(String name) {
            super(name, Defaults.FIELD_TYPE, Defaults.FIELD_TYPE);
            builder = this;
            Logger logger = Loggers.getLogger(getClass());
            this.deprecationLogger = new DeprecationLogger(logger);
        }

        @Override
        public StringFieldType fieldType() {
            return (StringFieldType) super.fieldType();
        }

        @Override
        public Builder searchAnalyzer(NamedAnalyzer searchAnalyzer) {
            super.searchAnalyzer(searchAnalyzer);
            return this;
        }

        public Builder positionIncrementGap(int positionIncrementGap) {
            this.positionIncrementGap = positionIncrementGap;
            return this;
        }

        public Builder ignoreAbove(int ignoreAbove) {
            this.ignoreAbove = ignoreAbove;
            return this;
        }

        public Builder fielddata(boolean fielddata) {
            fieldType().setFielddata(fielddata);
            return builder;
        }

        public Builder eagerGlobalOrdinals(boolean eagerGlobalOrdinals) {
            fieldType().setEagerGlobalOrdinals(eagerGlobalOrdinals);
            return builder;
        }

        public Builder fielddataFrequencyFilter(double minFreq, double maxFreq, int minSegmentSize) {
            fieldType().setFielddataMinFrequency(minFreq);
            fieldType().setFielddataMaxFrequency(maxFreq);
            fieldType().setFielddataMinSegmentSize(minSegmentSize);
            return builder;
        }

        @Override
        protected void setupFieldType(BuilderContext context) {
            super.setupFieldType(context);
            if (fieldType().hasDocValues() && fieldType().fielddata()) {
                fieldType().setFielddata(false);
            }
        }

        @Override
        public StringFieldMapper build(BuilderContext context) {
            // if the field is not analyzed, then by default, we should omit norms and have docs only
            // index options, as probably what the user really wants
            // if they are set explicitly, we will use those values
            // we also change the values on the default field type so that toXContent emits what
            // differs from the defaults
            if (fieldType.indexOptions() != IndexOptions.NONE && !fieldType.tokenized()) {
                defaultFieldType.setOmitNorms(true);
                defaultFieldType.setIndexOptions(IndexOptions.DOCS);
                if (!omitNormsSet && fieldType.boost() == 1.0f) {
                    fieldType.setOmitNorms(true);
                }
                if (!indexOptionsSet) {
                    fieldType.setIndexOptions(IndexOptions.DOCS);
                }
            }
            if (positionIncrementGap != POSITION_INCREMENT_GAP_USE_ANALYZER) {
                if (fieldType.indexOptions().compareTo(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) < 0) {
                    if (context.indexCreatedVersion().onOrAfter(Version.V_5_0_0_alpha1)) {
                        throw new IllegalArgumentException("Cannot set position_increment_gap on field ["
                                + name + "] without positions enabled");
                    } else {
                        deprecationLogger.deprecated("setting position_increment_gap on field [{}] without positions enabled " +
                                        "is deprecated and will be ignored", name);
                    }
                } else {
                    fieldType.setIndexAnalyzer(new NamedAnalyzer(fieldType.indexAnalyzer(), positionIncrementGap));
                    fieldType.setSearchAnalyzer(new NamedAnalyzer(fieldType.searchAnalyzer(), positionIncrementGap));
                    fieldType.setSearchQuoteAnalyzer(new NamedAnalyzer(fieldType.searchQuoteAnalyzer(), positionIncrementGap));
                }
            }
            setupFieldType(context);
            return new StringFieldMapper(
                    name, fieldType(), defaultFieldType, positionIncrementGap, ignoreAbove, includeInAll,
                    context.indexSettings(), multiFieldsBuilder.build(this, context), copyTo);
        }
    }

    public static class TypeParser implements Mapper.TypeParser {
        private final DeprecationLogger deprecationLogger;

        public TypeParser() {
            Logger logger = Loggers.getLogger(getClass());
            this.deprecationLogger = new DeprecationLogger(logger);
        }

    }

    public static final class StringFieldType extends org.codelibs.elasticsearch.index.mapper.StringFieldType {

        private boolean fielddata;
        private double fielddataMinFrequency;
        private double fielddataMaxFrequency;
        private int fielddataMinSegmentSize;

        public StringFieldType() {
            fielddata = true;
            fielddataMinFrequency = Defaults.FIELDDATA_MIN_FREQUENCY;
            fielddataMaxFrequency = Defaults.FIELDDATA_MAX_FREQUENCY;
            fielddataMinSegmentSize = Defaults.FIELDDATA_MIN_SEGMENT_SIZE;
        }

        protected StringFieldType(StringFieldType ref) {
            super(ref);
            this.fielddata = ref.fielddata;
            this.fielddataMinFrequency = ref.fielddataMinFrequency;
            this.fielddataMaxFrequency = ref.fielddataMaxFrequency;
            this.fielddataMinSegmentSize = ref.fielddataMinSegmentSize;
        }

        @Override
        public boolean equals(Object o) {
            if (super.equals(o) == false) {
                return false;
            }
            StringFieldType that = (StringFieldType) o;
            return fielddata == that.fielddata
                    && fielddataMinFrequency == that.fielddataMinFrequency
                    && fielddataMaxFrequency == that.fielddataMaxFrequency
                    && fielddataMinSegmentSize == that.fielddataMinSegmentSize;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), fielddata,
                    fielddataMinFrequency, fielddataMaxFrequency, fielddataMinSegmentSize);
        }

        @Override
        public StringFieldType clone() {
            return new StringFieldType(this);
        }

        @Override
        public String typeName() {
            return CONTENT_TYPE;
        }

        @Override
        public void checkCompatibility(MappedFieldType other,
                List<String> conflicts, boolean strict) {
            super.checkCompatibility(other, conflicts, strict);
            StringFieldType otherType = (StringFieldType) other;
            if (strict) {
                if (fielddata() != otherType.fielddata()) {
                    conflicts.add("mapper [" + name() + "] is used by multiple types. Set update_all_types to true to update [fielddata] "
                            + "across all types.");
                }
                if (fielddataMinFrequency() != otherType.fielddataMinFrequency()) {
                    conflicts.add("mapper [" + name() + "] is used by multiple types. Set update_all_types to true to update "
                            + "[fielddata_frequency_filter.min] across all types.");
                }
                if (fielddataMaxFrequency() != otherType.fielddataMaxFrequency()) {
                    conflicts.add("mapper [" + name() + "] is used by multiple types. Set update_all_types to true to update "
                            + "[fielddata_frequency_filter.max] across all types.");
                }
                if (fielddataMinSegmentSize() != otherType.fielddataMinSegmentSize()) {
                    conflicts.add("mapper [" + name() + "] is used by multiple types. Set update_all_types to true to update "
                            + "[fielddata_frequency_filter.min_segment_size] across all types.");
                }
            }
        }

        public boolean fielddata() {
            return fielddata;
        }

        public void setFielddata(boolean fielddata) {
            checkIfFrozen();
            this.fielddata = fielddata;
        }

        public double fielddataMinFrequency() {
            return fielddataMinFrequency;
        }

        public void setFielddataMinFrequency(double fielddataMinFrequency) {
            checkIfFrozen();
            this.fielddataMinFrequency = fielddataMinFrequency;
        }

        public double fielddataMaxFrequency() {
            return fielddataMaxFrequency;
        }

        public void setFielddataMaxFrequency(double fielddataMaxFrequency) {
            checkIfFrozen();
            this.fielddataMaxFrequency = fielddataMaxFrequency;
        }

        public int fielddataMinSegmentSize() {
            return fielddataMinSegmentSize;
        }

        public void setFielddataMinSegmentSize(int fielddataMinSegmentSize) {
            checkIfFrozen();
            this.fielddataMinSegmentSize = fielddataMinSegmentSize;
        }

        @Override
        public Query nullValueQuery() {
            if (nullValue() == null) {
                return null;
            }
            return termQuery(nullValue(), null);
        }

        @Override
        public IndexFieldData.Builder fielddataBuilder() {
            throw new UnsupportedOperationException();
        }
    }

    private Boolean includeInAll;
    private int positionIncrementGap;
    private int ignoreAbove;

    protected StringFieldMapper(String simpleName, StringFieldType fieldType, MappedFieldType defaultFieldType,
                                int positionIncrementGap, int ignoreAbove, Boolean includeInAll,
                                Settings indexSettings, MultiFields multiFields, CopyTo copyTo) {
        super(simpleName, fieldType, defaultFieldType, indexSettings, multiFields, copyTo);
        if (Version.indexCreated(indexSettings).onOrAfter(Version.V_5_0_0_alpha1)) {
            throw new IllegalArgumentException("The [string] type is removed in 5.0. You should now use either a [text] "
                    + "or [keyword] field instead for field [" + fieldType.name() + "]");
        }
        if (fieldType.tokenized() && fieldType.indexOptions() != NONE && fieldType().hasDocValues()) {
            throw new MapperParsingException("Field [" + fieldType.name() + "] cannot be analyzed and have doc values");
        }
        if (fieldType.hasDocValues() && (
                fieldType.fielddataMinFrequency() != Defaults.FIELDDATA_MIN_FREQUENCY
                || fieldType.fielddataMaxFrequency() != Defaults.FIELDDATA_MAX_FREQUENCY
                || fieldType.fielddataMinSegmentSize() != Defaults.FIELDDATA_MIN_SEGMENT_SIZE)) {
            throw new MapperParsingException("Field [" + fieldType.name() + "] cannot have doc values and use fielddata filtering");
        }
        this.positionIncrementGap = positionIncrementGap;
        this.ignoreAbove = ignoreAbove;
        this.includeInAll = includeInAll;
    }

    @Override
    protected StringFieldMapper clone() {
        return (StringFieldMapper) super.clone();
    }

    @Override
    protected boolean customBoost() {
        return true;
    }

    public int getPositionIncrementGap() {
        return this.positionIncrementGap;
    }

    public int getIgnoreAbove() {
        return ignoreAbove;
    }

    @Override
    protected String contentType() {
        return CONTENT_TYPE;
    }

    @Override
    protected void doMerge(Mapper mergeWith, boolean updateAllTypes) {
        super.doMerge(mergeWith, updateAllTypes);
        this.includeInAll = ((StringFieldMapper) mergeWith).includeInAll;
        this.ignoreAbove = ((StringFieldMapper) mergeWith).ignoreAbove;
    }

    @Override
    protected String indexTokenizeOption(boolean indexed, boolean tokenized) {
        if (!indexed) {
            return "no";
        } else if (tokenized) {
            return "analyzed";
        } else {
            return "not_analyzed";
        }
    }

    @Override
    public StringFieldType fieldType() {
        return (StringFieldType) super.fieldType();
    }

    @Override
    protected void doXContentBody(XContentBuilder builder, boolean includeDefaults, Params params) throws IOException {
        super.doXContentBody(builder, includeDefaults, params);
        doXContentAnalyzers(builder, includeDefaults);

        if (includeDefaults || fieldType().nullValue() != null) {
            builder.field("null_value", fieldType().nullValue());
        }
        if (includeInAll != null) {
            builder.field("include_in_all", includeInAll);
        } else if (includeDefaults) {
            builder.field("include_in_all", false);
        }

        if (includeDefaults || positionIncrementGap != POSITION_INCREMENT_GAP_USE_ANALYZER) {
            builder.field("position_increment_gap", positionIncrementGap);
        }

        if (includeDefaults || ignoreAbove != Defaults.IGNORE_ABOVE) {
            builder.field("ignore_above", ignoreAbove);
        }
        if (includeDefaults || fieldType().fielddata() != ((StringFieldType) defaultFieldType).fielddata()) {
            builder.field("fielddata", fieldType().fielddata());
        }
        if (fieldType().fielddata()) {
            if (includeDefaults
                    || fieldType().fielddataMinFrequency() != Defaults.FIELDDATA_MIN_FREQUENCY
                    || fieldType().fielddataMaxFrequency() != Defaults.FIELDDATA_MAX_FREQUENCY
                    || fieldType().fielddataMinSegmentSize() != Defaults.FIELDDATA_MIN_SEGMENT_SIZE) {
                builder.startObject("fielddata_frequency_filter");
                if (includeDefaults || fieldType().fielddataMinFrequency() != Defaults.FIELDDATA_MIN_FREQUENCY) {
                    builder.field("min", fieldType().fielddataMinFrequency());
                }
                if (includeDefaults || fieldType().fielddataMaxFrequency() != Defaults.FIELDDATA_MAX_FREQUENCY) {
                    builder.field("max", fieldType().fielddataMaxFrequency());
                }
                if (includeDefaults || fieldType().fielddataMinSegmentSize() != Defaults.FIELDDATA_MIN_SEGMENT_SIZE) {
                    builder.field("min_segment_size", fieldType().fielddataMinSegmentSize());
                }
                builder.endObject();
            }
        }
    }
}
