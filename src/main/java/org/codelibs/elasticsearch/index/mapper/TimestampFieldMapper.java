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

import org.codelibs.elasticsearch.Version;
import org.codelibs.elasticsearch.common.joda.FormatDateTimeFormatter;
import org.codelibs.elasticsearch.common.joda.Joda;
import org.codelibs.elasticsearch.common.lucene.Lucene;
import org.codelibs.elasticsearch.common.settings.Settings;
import org.codelibs.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;

public class TimestampFieldMapper extends MetadataFieldMapper {

    public static final String NAME = "_timestamp";
    public static final String CONTENT_TYPE = "_timestamp";
    public static final String DEFAULT_DATE_TIME_FORMAT = "epoch_millis||strictDateOptionalTime";

    public static class Defaults extends LegacyDateFieldMapper.Defaults {
        public static final String NAME = "_timestamp";

        // TODO: this should be removed
        public static final TimestampFieldType FIELD_TYPE = new TimestampFieldType();
        public static final FormatDateTimeFormatter DATE_TIME_FORMATTER = Joda.forPattern(DEFAULT_DATE_TIME_FORMAT);

        static {
            FIELD_TYPE.setStored(true);
            FIELD_TYPE.setTokenized(false);
            FIELD_TYPE.setNumericPrecisionStep(Defaults.PRECISION_STEP_64_BIT);
            FIELD_TYPE.setName(NAME);
            FIELD_TYPE.setDateTimeFormatter(DATE_TIME_FORMATTER);
            FIELD_TYPE.setIndexAnalyzer(Lucene.KEYWORD_ANALYZER);
            FIELD_TYPE.setSearchAnalyzer(Lucene.KEYWORD_ANALYZER);
            FIELD_TYPE.setHasDocValues(true);
            FIELD_TYPE.freeze();
        }

        public static final EnabledAttributeMapper ENABLED = EnabledAttributeMapper.UNSET_DISABLED;
        public static final String DEFAULT_TIMESTAMP = "now";
    }

    public static class Builder extends MetadataFieldMapper.Builder<Builder, TimestampFieldMapper> {

        private EnabledAttributeMapper enabledState = EnabledAttributeMapper.UNSET_DISABLED;
        private String defaultTimestamp = Defaults.DEFAULT_TIMESTAMP;
        private Boolean ignoreMissing = null;

        public Builder(MappedFieldType existing) {
            super(Defaults.NAME, existing == null ? Defaults.FIELD_TYPE : existing, Defaults.FIELD_TYPE);
        }

        @Override
        public LegacyDateFieldMapper.DateFieldType fieldType() {
            return (LegacyDateFieldMapper.DateFieldType)fieldType;
        }

        public Builder enabled(EnabledAttributeMapper enabledState) {
            this.enabledState = enabledState;
            return builder;
        }

        public Builder dateTimeFormatter(FormatDateTimeFormatter dateTimeFormatter) {
            fieldType().setDateTimeFormatter(dateTimeFormatter);
            return this;
        }

        public Builder defaultTimestamp(String defaultTimestamp) {
            this.defaultTimestamp = defaultTimestamp;
            return builder;
        }

        public Builder ignoreMissing(boolean ignoreMissing) {
            this.ignoreMissing = ignoreMissing;
            return builder;
        }

        @Override
        public Builder store(boolean store) {
            return super.store(store);
        }

        @Override
        public TimestampFieldMapper build(BuilderContext context) {
            setupFieldType(context);
            return new TimestampFieldMapper(fieldType, defaultFieldType, enabledState, defaultTimestamp,
                    ignoreMissing, context.indexSettings());
        }
    }

    public static class TypeParser implements MetadataFieldMapper.TypeParser {
    }

    public static final class TimestampFieldType extends LegacyDateFieldMapper.DateFieldType {

        public TimestampFieldType() {}

        protected TimestampFieldType(TimestampFieldType ref) {
            super(ref);
        }

        @Override
        public TimestampFieldType clone() {
            return new TimestampFieldType(this);
        }

        @Override
        public Object valueForDisplay(Object value) {
            return value;
        }
    }


    private final EnabledAttributeMapper enabledState;
    private final String defaultTimestamp;
    private final Boolean ignoreMissing;

    private TimestampFieldMapper(Settings indexSettings, MappedFieldType existing) {
        this(existing != null ? existing : Defaults.FIELD_TYPE, Defaults.FIELD_TYPE, Defaults.ENABLED, Defaults.DEFAULT_TIMESTAMP, null, indexSettings);
    }

    private TimestampFieldMapper(MappedFieldType fieldType, MappedFieldType defaultFieldType, EnabledAttributeMapper enabledState,
                                   String defaultTimestamp, Boolean ignoreMissing, Settings indexSettings) {
        super(NAME, fieldType, defaultFieldType, indexSettings);
        if (enabledState.enabled && Version.indexCreated(indexSettings).onOrAfter(Version.V_5_0_0_alpha4)) {
            throw new IllegalArgumentException("[_timestamp] is removed in 5.0. As a replacement, you can use an ingest pipeline to add a field with the current timestamp to your documents.");
        }
        this.enabledState = enabledState;
        this.defaultTimestamp = defaultTimestamp;
        this.ignoreMissing = ignoreMissing;
    }

    @Override
    public TimestampFieldType fieldType() {
        return (TimestampFieldType)super.fieldType();
    }

    public boolean enabled() {
        return this.enabledState.enabled;
    }

    public String defaultTimestamp() {
        return this.defaultTimestamp;
    }

    public Boolean ignoreMissing() {
        return this.ignoreMissing;
    }

    @Override
    protected String contentType() {
        return CONTENT_TYPE;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        boolean includeDefaults = params.paramAsBoolean("include_defaults", false);

        // if all are defaults, no sense to write it at all
        if (!includeDefaults && enabledState == Defaults.ENABLED
                && fieldType().dateTimeFormatter().format().equals(Defaults.DATE_TIME_FORMATTER.format())
                && Defaults.DEFAULT_TIMESTAMP.equals(defaultTimestamp)) {
            return builder;
        }
        builder.startObject(CONTENT_TYPE);
        if (includeDefaults || enabledState != Defaults.ENABLED) {
            builder.field("enabled", enabledState.enabled);
        }
        // different format handling depending on index version
        String defaultDateFormat = Defaults.DATE_TIME_FORMATTER.format();
        if (includeDefaults || !fieldType().dateTimeFormatter().format().equals(defaultDateFormat)) {
            builder.field("format", fieldType().dateTimeFormatter().format());
        }
        if (includeDefaults || !Defaults.DEFAULT_TIMESTAMP.equals(defaultTimestamp)) {
            builder.field("default", defaultTimestamp);
        }
        if (includeDefaults || ignoreMissing != null) {
            builder.field("ignore_missing", ignoreMissing);
        }

        builder.endObject();
        return builder;
    }

    @Override
    protected void doMerge(Mapper mergeWith, boolean updateAllTypes) {
        throw new UnsupportedOperationException();
    }
}
