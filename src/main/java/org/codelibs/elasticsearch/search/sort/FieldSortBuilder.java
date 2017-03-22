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

package org.codelibs.elasticsearch.search.sort;

import org.apache.lucene.search.SortField;
import org.codelibs.elasticsearch.common.ParseField;
import org.codelibs.elasticsearch.common.io.stream.StreamInput;
import org.codelibs.elasticsearch.common.io.stream.StreamOutput;
import org.codelibs.elasticsearch.common.xcontent.ObjectParser;
import org.codelibs.elasticsearch.common.xcontent.ObjectParser.ValueType;
import org.codelibs.elasticsearch.common.xcontent.XContentBuilder;
import org.codelibs.elasticsearch.index.query.QueryBuilder;
import org.codelibs.elasticsearch.index.query.QueryParseContext;
import org.codelibs.elasticsearch.index.query.QueryShardContext;
import org.codelibs.elasticsearch.search.DocValueFormat;

import java.io.IOException;
import java.util.Objects;

/**
 * A sort builder to sort based on a document field.
 */
public class FieldSortBuilder extends SortBuilder<FieldSortBuilder> {
    public static final String NAME = "field_sort";
    public static final ParseField MISSING = new ParseField("missing");
    public static final ParseField SORT_MODE = new ParseField("mode");
    public static final ParseField UNMAPPED_TYPE = new ParseField("unmapped_type");

    /**
     * special field name to sort by index order
     */
    public static final String DOC_FIELD_NAME = "_doc";
    private static final SortFieldAndFormat SORT_DOC = new SortFieldAndFormat(
            new SortField(null, SortField.Type.DOC), DocValueFormat.RAW);
    private static final SortFieldAndFormat SORT_DOC_REVERSE = new SortFieldAndFormat(
            new SortField(null, SortField.Type.DOC, true), DocValueFormat.RAW);

    private final String fieldName;

    private Object missing;

    private String unmappedType;

    private SortMode sortMode;

    private QueryBuilder nestedFilter;

    private String nestedPath;

    /** Copy constructor. */
    public FieldSortBuilder(FieldSortBuilder template) {
        this(template.fieldName);
        this.order(template.order());
        this.missing(template.missing());
        this.unmappedType(template.unmappedType());
        if (template.sortMode != null) {
            this.sortMode(template.sortMode());
        }
        this.setNestedFilter(template.getNestedFilter());
        this.setNestedPath(template.getNestedPath());
    }

    /**
     * Constructs a new sort based on a document field.
     *
     * @param fieldName
     *            The field name.
     */
    public FieldSortBuilder(String fieldName) {
        if (fieldName == null) {
            throw new IllegalArgumentException("fieldName must not be null");
        }
        this.fieldName = fieldName;
    }

    /**
     * Read from a stream.
     */
    public FieldSortBuilder(StreamInput in) throws IOException {
        fieldName = in.readString();
        nestedFilter = in.readOptionalNamedWriteable(QueryBuilder.class);
        nestedPath = in.readOptionalString();
        missing = in.readGenericValue();
        order = in.readOptionalWriteable(SortOrder::readFromStream);
        sortMode = in.readOptionalWriteable(SortMode::readFromStream);
        unmappedType = in.readOptionalString();
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeString(fieldName);
        out.writeOptionalNamedWriteable(nestedFilter);
        out.writeOptionalString(nestedPath);
        out.writeGenericValue(missing);
        out.writeOptionalWriteable(order);
        out.writeOptionalWriteable(sortMode);
        out.writeOptionalString(unmappedType);
    }

    /** Returns the document field this sort should be based on. */
    public String getFieldName() {
        return this.fieldName;
    }

    /**
     * Sets the value when a field is missing in a doc. Can also be set to <tt>_last</tt> or
     * <tt>_first</tt> to sort missing last or first respectively.
     */
    public FieldSortBuilder missing(Object missing) {
        this.missing = missing;
        return this;
    }

    /** Returns the value used when a field is missing in a doc. */
    public Object missing() {
        return missing;
    }

    /**
     * Set the type to use in case the current field is not mapped in an index.
     * Specifying a type tells Elasticsearch what type the sort values should
     * have, which is important for cross-index search, if there are sort fields
     * that exist on some indices only. If the unmapped type is <tt>null</tt>
     * then query execution will fail if one or more indices don't have a
     * mapping for the current field.
     */
    public FieldSortBuilder unmappedType(String type) {
        this.unmappedType = type;
        return this;
    }

    /**
     * Returns the type to use in case the current field is not mapped in an
     * index.
     */
    public String unmappedType() {
        return this.unmappedType;
    }

    /**
     * Defines what values to pick in the case a document contains multiple
     * values for the targeted sort field. Possible values: min, max, sum and
     * avg
     *
     * <p>
     * The last two values are only applicable for number based fields.
     */
    public FieldSortBuilder sortMode(SortMode sortMode) {
        Objects.requireNonNull(sortMode, "sort mode cannot be null");
        this.sortMode = sortMode;
        return this;
    }

    /**
     * Returns what values to pick in the case a document contains multiple
     * values for the targeted sort field.
     */
    public SortMode sortMode() {
        return this.sortMode;
    }

    /**
     * Sets the nested filter that the nested objects should match with in order
     * to be taken into account for sorting.
     *
     * TODO should the above getters and setters be deprecated/ changed in
     * favour of real getters and setters?
     */
    public FieldSortBuilder setNestedFilter(QueryBuilder nestedFilter) {
        this.nestedFilter = nestedFilter;
        return this;
    }

    /**
     * Returns the nested filter that the nested objects should match with in
     * order to be taken into account for sorting.
     */
    public QueryBuilder getNestedFilter() {
        return this.nestedFilter;
    }

    /**
     * Sets the nested path if sorting occurs on a field that is inside a nested
     * object. By default when sorting on a field inside a nested object, the
     * nearest upper nested object is selected as nested path.
     */
    public FieldSortBuilder setNestedPath(String nestedPath) {
        this.nestedPath = nestedPath;
        return this;
    }

    /**
     * Returns the nested path if sorting occurs in a field that is inside a
     * nested object.
     */
    public String getNestedPath() {
        return this.nestedPath;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        builder.startObject(fieldName);
        builder.field(ORDER_FIELD.getPreferredName(), order);
        if (missing != null) {
            builder.field(MISSING.getPreferredName(), missing);
        }
        if (unmappedType != null) {
            builder.field(UNMAPPED_TYPE.getPreferredName(), unmappedType);
        }
        if (sortMode != null) {
            builder.field(SORT_MODE.getPreferredName(), sortMode);
        }
        if (nestedFilter != null) {
            builder.field(NESTED_FILTER_FIELD.getPreferredName(), nestedFilter, params);
        }
        if (nestedPath != null) {
            builder.field(NESTED_PATH_FIELD.getPreferredName(), nestedPath);
        }
        builder.endObject();
        builder.endObject();
        return builder;
    }

    @Override
    public SortFieldAndFormat build(QueryShardContext context) throws IOException {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        FieldSortBuilder builder = (FieldSortBuilder) other;
        return (Objects.equals(this.fieldName, builder.fieldName) && Objects.equals(this.nestedFilter, builder.nestedFilter)
                && Objects.equals(this.nestedPath, builder.nestedPath) && Objects.equals(this.missing, builder.missing)
                && Objects.equals(this.order, builder.order) && Objects.equals(this.sortMode, builder.sortMode)
                && Objects.equals(this.unmappedType, builder.unmappedType));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.fieldName, this.nestedFilter, this.nestedPath, this.missing, this.order, this.sortMode, this.unmappedType);
    }

    @Override
    public String getWriteableName() {
        return NAME;
    }

    /**
     * Creates a new {@link FieldSortBuilder} from the query held by the {@link QueryParseContext} in
     * {@link org.codelibs.elasticsearch.common.xcontent.XContent} format.
     *
     * @param context the input parse context. The state on the parser contained in this context will be changed as a side effect of this
     *        method call
     * @param fieldName in some sort syntax variations the field name precedes the xContent object that specifies further parameters, e.g.
     *        in '{ "foo": { "order" : "asc"} }'. When parsing the inner object, the field name can be passed in via this argument
     */
    public static FieldSortBuilder fromXContent(QueryParseContext context, String fieldName) throws IOException {
        return PARSER.parse(context.parser(), new FieldSortBuilder(fieldName), context);
    }

    private static ObjectParser<FieldSortBuilder, QueryParseContext> PARSER = new ObjectParser<>(NAME);

    static {
        PARSER.declareField(FieldSortBuilder::missing, p -> p.objectText(),  MISSING, ValueType.VALUE);
        PARSER.declareString(FieldSortBuilder::setNestedPath , NESTED_PATH_FIELD);
        PARSER.declareString(FieldSortBuilder::unmappedType , UNMAPPED_TYPE);
        PARSER.declareString((b, v) -> b.order(SortOrder.fromString(v)) , ORDER_FIELD);
        PARSER.declareString((b, v) -> b.sortMode(SortMode.fromString(v)), SORT_MODE);
        PARSER.declareObject(FieldSortBuilder::setNestedFilter, SortBuilder::parseNestedFilter, NESTED_FILTER_FIELD);
    }
}
