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

import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.BytesRef;
import org.codelibs.elasticsearch.common.lucene.Lucene;
import org.codelibs.elasticsearch.common.settings.Settings;
import org.codelibs.elasticsearch.common.xcontent.XContentBuilder;
import org.codelibs.elasticsearch.index.analysis.NamedAnalyzer;
import org.codelibs.elasticsearch.index.fielddata.IndexFieldData;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import static java.util.Collections.unmodifiableList;

/**
 * A field mapper for keywords. This mapper accepts strings and indexes them as-is.
 */
public final class KeywordFieldMapper extends FieldMapper {

    public static final String CONTENT_TYPE = "keyword";

    private static final List<String> SUPPORTED_PARAMETERS_FOR_AUTO_DOWNGRADE_TO_STRING = unmodifiableList(Arrays.asList(
            "type",
            // common keyword parameters, for which the upgrade is straightforward
            "index", "store", "doc_values", "omit_norms", "norms", "boost", "fields", "copy_to",
            "include_in_all", "ignore_above", "index_options", "similarity"));

    public static class Defaults {
        public static final MappedFieldType FIELD_TYPE = new KeywordFieldType();

        static {
            FIELD_TYPE.setTokenized(false);
            FIELD_TYPE.setOmitNorms(true);
            FIELD_TYPE.setIndexOptions(IndexOptions.DOCS);
            FIELD_TYPE.freeze();
        }

        public static final String NULL_VALUE = null;
        public static final int IGNORE_ABOVE = Integer.MAX_VALUE;
    }

    public static class Builder extends FieldMapper.Builder<Builder, KeywordFieldMapper> {

        protected String nullValue = Defaults.NULL_VALUE;
        protected int ignoreAbove = Defaults.IGNORE_ABOVE;

        public Builder(String name) {
            super(name, Defaults.FIELD_TYPE, Defaults.FIELD_TYPE);
            builder = this;
        }

        @Override
        public KeywordFieldType fieldType() {
            return (KeywordFieldType) super.fieldType();
        }

        public Builder ignoreAbove(int ignoreAbove) {
            if (ignoreAbove < 0) {
                throw new IllegalArgumentException("[ignore_above] must be positive, got " + ignoreAbove);
            }
            this.ignoreAbove = ignoreAbove;
            return this;
        }

        @Override
        public Builder indexOptions(IndexOptions indexOptions) {
            if (indexOptions.compareTo(IndexOptions.DOCS_AND_FREQS) > 0) {
                throw new IllegalArgumentException("The [keyword] field does not support positions, got [index_options]="
                        + indexOptionToString(indexOptions));
            }
            return super.indexOptions(indexOptions);
        }

        public Builder eagerGlobalOrdinals(boolean eagerGlobalOrdinals) {
            fieldType().setEagerGlobalOrdinals(eagerGlobalOrdinals);
            return builder;
        }

        public Builder normalizer(NamedAnalyzer normalizer) {
            fieldType().setNormalizer(normalizer);
            fieldType().setSearchAnalyzer(normalizer);
            return builder;
        }

        @Override
        public KeywordFieldMapper build(BuilderContext context) {
            setupFieldType(context);
            return new KeywordFieldMapper(
                    name, fieldType, defaultFieldType, ignoreAbove, includeInAll,
                    context.indexSettings(), multiFieldsBuilder.build(this, context), copyTo);
        }
    }

    public static class TypeParser implements Mapper.TypeParser {
    }

    public static final class KeywordFieldType extends StringFieldType {

        private NamedAnalyzer normalizer = null;

        public KeywordFieldType() {
            setIndexAnalyzer(Lucene.KEYWORD_ANALYZER);
            setSearchAnalyzer(Lucene.KEYWORD_ANALYZER);
        }

        protected KeywordFieldType(KeywordFieldType ref) {
            super(ref);
            this.normalizer = ref.normalizer;
        }

        @Override
        public KeywordFieldType clone() {
            return new KeywordFieldType(this);
        }

        @Override
        public boolean equals(Object o) {
            if (super.equals(o) == false) {
                return false;
            }
            return Objects.equals(normalizer, ((KeywordFieldType) o).normalizer);
        }

        @Override
        public void checkCompatibility(MappedFieldType otherFT, List<String> conflicts, boolean strict) {
            super.checkCompatibility(otherFT, conflicts, strict);
            KeywordFieldType other = (KeywordFieldType) otherFT;
            if (Objects.equals(normalizer, other.normalizer) == false) {
                conflicts.add("mapper [" + name() + "] has different [normalizer]");
            }
        }

        @Override
        public int hashCode() {
            return 31 * super.hashCode() + Objects.hashCode(normalizer);
        }

        @Override
        public String typeName() {
            return CONTENT_TYPE;
        }

        public NamedAnalyzer normalizer() {
            return normalizer;
        }

        public void setNormalizer(NamedAnalyzer normalizer) {
            checkIfFrozen();
            this.normalizer = normalizer;
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

        @Override
        public Object valueForDisplay(Object value) {
            if (value == null) {
                return null;
            }
            // keywords are internally stored as utf8 bytes
            BytesRef binaryValue = (BytesRef) value;
            return binaryValue.utf8ToString();
        }

        @Override
        protected BytesRef indexedValueForSearch(Object value) {
            if (value == null) {
                return null;
            }
            if (value instanceof BytesRef) {
                value = ((BytesRef) value).utf8ToString();
            }
            return searchAnalyzer().normalize(name(), value.toString());
        }
    }

    private Boolean includeInAll;
    private int ignoreAbove;

    protected KeywordFieldMapper(String simpleName, MappedFieldType fieldType, MappedFieldType defaultFieldType,
                                int ignoreAbove, Boolean includeInAll,
                                Settings indexSettings, MultiFields multiFields, CopyTo copyTo) {
        super(simpleName, fieldType, defaultFieldType, indexSettings, multiFields, copyTo);
        assert fieldType.indexOptions().compareTo(IndexOptions.DOCS_AND_FREQS) <= 0;
        this.ignoreAbove = ignoreAbove;
        this.includeInAll = includeInAll;
    }

    /** Values that have more chars than the return value of this method will
     *  be skipped at parsing time. */
    // pkg-private for testing
    int ignoreAbove() {
        return ignoreAbove;
    }

    @Override
    protected KeywordFieldMapper clone() {
        return (KeywordFieldMapper) super.clone();
    }

    @Override
    public KeywordFieldType fieldType() {
        return (KeywordFieldType) super.fieldType();
    }

    // pkg-private for testing
    Boolean includeInAll() {
        return includeInAll;
    }

    @Override
    protected String contentType() {
        return CONTENT_TYPE;
    }

    @Override
    protected void doMerge(Mapper mergeWith, boolean updateAllTypes) {
        super.doMerge(mergeWith, updateAllTypes);
        this.includeInAll = ((KeywordFieldMapper) mergeWith).includeInAll;
        this.ignoreAbove = ((KeywordFieldMapper) mergeWith).ignoreAbove;
    }

    @Override
    protected void doXContentBody(XContentBuilder builder, boolean includeDefaults, Params params) throws IOException {
        super.doXContentBody(builder, includeDefaults, params);

        if (includeDefaults || fieldType().nullValue() != null) {
            builder.field("null_value", fieldType().nullValue());
        }

        if (includeInAll != null) {
            builder.field("include_in_all", includeInAll);
        } else if (includeDefaults) {
            builder.field("include_in_all", true);
        }

        if (includeDefaults || ignoreAbove != Defaults.IGNORE_ABOVE) {
            builder.field("ignore_above", ignoreAbove);
        }

        if (fieldType().normalizer() != null) {
            builder.field("normalizer", fieldType().normalizer().name());
        } else if (includeDefaults) {
            builder.nullField("normalizer");
        }
    }
}
