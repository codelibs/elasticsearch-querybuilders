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

package org.codelibs.elasticsearch.search.suggest.completion2x.context;

import org.apache.lucene.util.automaton.Automata;
import org.apache.lucene.util.automaton.Automaton;
import org.apache.lucene.util.automaton.Operations;
import org.codelibs.elasticsearch.ElasticsearchParseException;
import org.codelibs.elasticsearch.common.xcontent.XContentBuilder;
import org.codelibs.elasticsearch.common.xcontent.XContentParser;
import org.codelibs.elasticsearch.common.xcontent.XContentParser.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The {CategoryContextMapping} is used to define a {ContextMapping} that
 * references a field within a document. The value of the field in turn will be
 * used to setup the suggestions made by the completion suggester.
 */
public class CategoryContextMapping extends ContextMapping {

    protected static final String TYPE = "category";

    private static final String FIELD_FIELDNAME = "path";
    private static final String DEFAULT_FIELDNAME = "_type";

    private static final Iterable<String> EMPTY_VALUES = Collections.emptyList();

    private final String fieldName;
    private final Iterable<String> defaultValues;
    private final FieldConfig defaultConfig;

    /**
     * Create a new {CategoryContextMapping} with the default field
     * <code>[_type]</code>
     */
    public CategoryContextMapping(String name) {
        this(name, DEFAULT_FIELDNAME, EMPTY_VALUES);
    }

    /**
     * Create a new {CategoryContextMapping} with the default field
     * <code>[_type]</code>
     */
    public CategoryContextMapping(String name, String fieldName) {
        this(name, fieldName, EMPTY_VALUES);
    }

    /**
     * Create a new {CategoryContextMapping} with the default field
     * <code>[_type]</code>
     */
    public CategoryContextMapping(String name, Iterable<String> defaultValues) {
        this(name, DEFAULT_FIELDNAME, defaultValues);
    }

    /**
     * Create a new {CategoryContextMapping} with the default field
     * <code>[_type]</code>
     */
    public CategoryContextMapping(String name, String fieldName, Iterable<String> defaultValues) {
        super(TYPE, name);
        this.fieldName = fieldName;
        this.defaultValues = defaultValues;
        this.defaultConfig = new FieldConfig(fieldName, defaultValues, null);
    }

    /**
     * Name of the field used by this {CategoryContextMapping}
     */
    public String getFieldName() {
        return fieldName;
    }

    public Iterable<? extends CharSequence> getDefaultValues() {
        return defaultValues;
    }

    @Override
    public FieldConfig defaultConfig() {
        return defaultConfig;
    }

    /**
     * Load the specification of a {CategoryContextMapping}
     *
     * @param name
     *            name of the field to use. If <code>null</code> default field
     *            will be used
     * @return new {CategoryContextMapping}
     */
    protected static CategoryContextMapping load(String name, Map<String, Object> config) throws ElasticsearchParseException {
        CategoryContextMapping.Builder mapping = new CategoryContextMapping.Builder(name);

        Object fieldName = config.get(FIELD_FIELDNAME);
        Object defaultValues = config.get(FIELD_MISSING);

        if (fieldName != null) {
            mapping.fieldName(fieldName.toString());
            config.remove(FIELD_FIELDNAME);
        }

        if (defaultValues != null) {
            if (defaultValues instanceof Iterable) {
                for (Object value : (Iterable) defaultValues) {
                    mapping.addDefaultValue(value.toString());
                }
            } else {
                mapping.addDefaultValue(defaultValues.toString());
            }
            config.remove(FIELD_MISSING);
        }

        return mapping.build();
    }

    @Override
    protected XContentBuilder toInnerXContent(XContentBuilder builder, Params params) throws IOException {
        if (fieldName != null) {
            builder.field(FIELD_FIELDNAME, fieldName);
        }
        builder.startArray(FIELD_MISSING);
        for (CharSequence value : defaultValues) {
            builder.value(value);
        }
        builder.endArray();
        return builder;
    }

    @Override
    public FieldQuery parseQuery(String name, XContentParser parser) throws IOException, ElasticsearchParseException {
        Iterable<? extends CharSequence> values;
        Token token = parser.currentToken();
        if (token == Token.START_ARRAY) {
            ArrayList<String> list = new ArrayList<>();
            while ((token = parser.nextToken()) != Token.END_ARRAY) {
                list.add(parser.text());
            }
            values = list;
        } else if (token == Token.VALUE_NULL) {
            values = defaultValues;
        } else {
            values = Collections.singleton(parser.text());
        }

        return new FieldQuery(name, values);
    }

    public static FieldQuery query(String name, CharSequence... fieldvalues) {
        return query(name, Arrays.asList(fieldvalues));
    }

    public static FieldQuery query(String name, Iterable<? extends CharSequence> fieldvalues) {
        return new FieldQuery(name, fieldvalues);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CategoryContextMapping) {
            CategoryContextMapping other = (CategoryContextMapping) obj;
            if (this.fieldName.equals(other.fieldName)) {
                return Objects.deepEquals(this.defaultValues, other.defaultValues);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = fieldName.hashCode();
        for (CharSequence seq : defaultValues) {
            hashCode = 31 * hashCode + seq.hashCode();
        }
        return hashCode;
    }

    private static class FieldConfig extends ContextConfig {

        private final String fieldname;
        private final Iterable<String> defaultValues;
        private final Iterable<String> values;

        public FieldConfig(String fieldname, Iterable<String> defaultValues, Iterable<String> values) {
            this.fieldname = fieldname;
            this.defaultValues = defaultValues;
            this.values = values;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("FieldConfig(" + fieldname + " = [");
            if (this.values != null && this.values.iterator().hasNext()) {
                final Iterator<String> valuesIterator = this.values.iterator();
                sb.append("(");
                while (valuesIterator.hasNext()) {
                    sb.append(valuesIterator.next());
                    if (valuesIterator.hasNext()) {
                        sb.append(", ");
                    }
                }
                sb.append(")");
            }
            if (this.defaultValues != null && this.defaultValues.iterator().hasNext()) {
                final Iterator<String> defaultValuesIterator = this.defaultValues.iterator();
                sb.append(" default(");
                while (defaultValuesIterator.hasNext()) {
                    sb.append(defaultValuesIterator.next());
                    if (defaultValuesIterator.hasNext()) {
                        sb.append(", ");
                    }
                }
                sb.append(")");
            }
            return sb.append("])").toString();
        }

    }

    private static class FieldQuery extends ContextQuery {

        private final Iterable<? extends CharSequence> values;

        public FieldQuery(String name, Iterable<? extends CharSequence> values) {
            super(name);
            this.values = values;
        }

        @Override
        public Automaton toAutomaton() {
            List<Automaton> automatons = new ArrayList<>();
            for (CharSequence value : values) {
                automatons.add(Automata.makeString(value.toString()));
            }
            return Operations.union(automatons);
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startArray(name);
            for (CharSequence value : values) {
                builder.value(value);
            }
            builder.endArray();
            return builder;
        }
    }

    public static class Builder extends ContextBuilder<CategoryContextMapping> {

        private String fieldname;
        private List<String> defaultValues = new ArrayList<>();

        public Builder(String name) {
            this(name, DEFAULT_FIELDNAME);
        }

        public Builder(String name, String fieldname) {
            super(name);
            this.fieldname = fieldname;
        }

        /**
         * Set the name of the field to use
         */
        public Builder fieldName(String fieldname) {
            this.fieldname = fieldname;
            return this;
        }

        /**
         * Add value to the default values of the mapping
         */
        public Builder addDefaultValue(String defaultValue) {
            this.defaultValues.add(defaultValue);
            return this;
        }

        /**
         * Add set of default values to the mapping
         */
        public Builder addDefaultValues(String... defaultValues) {
            Collections.addAll(this.defaultValues, defaultValues);
            return this;
        }

        /**
         * Add set of default values to the mapping
         */
        public Builder addDefaultValues(Iterable<String> defaultValues) {
            for (String defaultValue : defaultValues) {
                this.defaultValues.add(defaultValue);
            }
            return this;
        }

        @Override
        public CategoryContextMapping build() {
            return new CategoryContextMapping(name, fieldname, defaultValues);
        }
    }
}
