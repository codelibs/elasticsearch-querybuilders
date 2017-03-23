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

import org.codelibs.elasticsearch.common.ParseField;
import org.codelibs.elasticsearch.common.io.stream.StreamInput;
import org.codelibs.elasticsearch.common.io.stream.StreamOutput;
import org.codelibs.elasticsearch.common.io.stream.Writeable;
import org.codelibs.elasticsearch.common.xcontent.ConstructingObjectParser;
import org.codelibs.elasticsearch.common.xcontent.ObjectParser.ValueType;
import org.codelibs.elasticsearch.common.xcontent.XContentBuilder;
import org.codelibs.elasticsearch.index.query.QueryBuilder;
import org.codelibs.elasticsearch.index.query.QueryParseContext;
import org.codelibs.elasticsearch.index.query.QueryShardContext;
import org.codelibs.elasticsearch.script.Script;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import static org.codelibs.elasticsearch.common.xcontent.ConstructingObjectParser.constructorArg;

/**
 * Script sort builder allows to sort based on a custom script expression.
 */
public class ScriptSortBuilder extends SortBuilder<ScriptSortBuilder> {

    public static final String NAME = "_script";
    public static final ParseField TYPE_FIELD = new ParseField("type");
    public static final ParseField SCRIPT_FIELD = new ParseField("script");
    public static final ParseField SORTMODE_FIELD = new ParseField("mode");

    private final Script script;

    private final ScriptSortType type;

    private SortMode sortMode;

    private QueryBuilder nestedFilter;

    private String nestedPath;

    /**
     * Constructs a script sort builder with the given script.
     *
     * @param script
     *            The script to use.
     * @param type
     *            The type of the script, can be either {@link ScriptSortType#STRING} or
     *            {@link ScriptSortType#NUMBER}
     */
    public ScriptSortBuilder(Script script, ScriptSortType type) {
        Objects.requireNonNull(script, "script cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        this.script = script;
        this.type = type;
    }

    ScriptSortBuilder(ScriptSortBuilder original) {
        this.script = original.script;
        this.type = original.type;
        this.order = original.order;
        this.sortMode = original.sortMode;
        this.nestedFilter = original.nestedFilter;
        this.nestedPath = original.nestedPath;
    }

    /**
     * Read from a stream.
     */
    public ScriptSortBuilder(StreamInput in) throws IOException {
        script = new Script(in);
        type = ScriptSortType.readFromStream(in);
        order = SortOrder.readFromStream(in);
        sortMode = in.readOptionalWriteable(SortMode::readFromStream);
        nestedPath = in.readOptionalString();
        nestedFilter = in.readOptionalNamedWriteable(QueryBuilder.class);
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        script.writeTo(out);
        type.writeTo(out);
        order.writeTo(out);
        out.writeOptionalWriteable(sortMode);
        out.writeOptionalString(nestedPath);
        out.writeOptionalNamedWriteable(nestedFilter);
    }

    /**
     * Get the script used in this sort.
     */
    public Script script() {
        return this.script;
    }

    /**
     * Get the type used in this sort.
     */
    public ScriptSortType type() {
        return this.type;
    }

    /**
     * Defines which distance to use for sorting in the case a document contains multiple values.<br>
     * For {@link ScriptSortType#STRING}, the set of possible values is restricted to {@link SortMode#MIN} and {@link SortMode#MAX}
     */
    public ScriptSortBuilder sortMode(SortMode sortMode) {
        Objects.requireNonNull(sortMode, "sort mode cannot be null.");
        if (ScriptSortType.STRING.equals(type) && (sortMode == SortMode.SUM || sortMode == SortMode.AVG ||
                sortMode == SortMode.MEDIAN)) {
            throw new IllegalArgumentException("script sort of type [string] doesn't support mode [" + sortMode + "]");
        }
        this.sortMode = sortMode;
        return this;
    }

    /**
     * Get the sort mode.
     */
    public SortMode sortMode() {
        return this.sortMode;
    }

    /**
     * Sets the nested filter that the nested objects should match with in order to be taken into account
     * for sorting.
     */
    public ScriptSortBuilder setNestedFilter(QueryBuilder nestedFilter) {
        this.nestedFilter = nestedFilter;
        return this;
    }

    /**
     * Gets the nested filter.
     */
    public QueryBuilder getNestedFilter() {
        return this.nestedFilter;
    }

    /**
     * Sets the nested path if sorting occurs on a field that is inside a nested object. For sorting by script this
     * needs to be specified.
     */
    public ScriptSortBuilder setNestedPath(String nestedPath) {
        this.nestedPath = nestedPath;
        return this;
    }

    /**
     * Gets the nested path.
     */
    public String getNestedPath() {
        return this.nestedPath;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params builderParams) throws IOException {
        builder.startObject();
        builder.startObject(NAME);
        builder.field(SCRIPT_FIELD.getPreferredName(), script);
        builder.field(TYPE_FIELD.getPreferredName(), type);
        builder.field(ORDER_FIELD.getPreferredName(), order);
        if (sortMode != null) {
            builder.field(SORTMODE_FIELD.getPreferredName(), sortMode);
        }
        if (nestedPath != null) {
            builder.field(NESTED_PATH_FIELD.getPreferredName(), nestedPath);
        }
        if (nestedFilter != null) {
            builder.field(NESTED_FILTER_FIELD.getPreferredName(), nestedFilter, builderParams);
        }
        builder.endObject();
        builder.endObject();
        return builder;
    }

    private static ConstructingObjectParser<ScriptSortBuilder, QueryParseContext> PARSER = new ConstructingObjectParser<>(NAME,
            a -> new ScriptSortBuilder((Script) a[0], (ScriptSortType) a[1]));

    static {
        PARSER.declareField(constructorArg(), Script::parse, Script.SCRIPT_PARSE_FIELD, ValueType.OBJECT_OR_STRING);
        PARSER.declareField(constructorArg(), p -> ScriptSortType.fromString(p.text()), TYPE_FIELD, ValueType.STRING);
        PARSER.declareString((b, v) -> b.order(SortOrder.fromString(v)), ORDER_FIELD);
        PARSER.declareString((b, v) -> b.sortMode(SortMode.fromString(v)), SORTMODE_FIELD);
        PARSER.declareString(ScriptSortBuilder::setNestedPath , NESTED_PATH_FIELD);
        PARSER.declareObject(ScriptSortBuilder::setNestedFilter, SortBuilder::parseNestedFilter, NESTED_FILTER_FIELD);
    }

    /**
     * Creates a new {@link ScriptSortBuilder} from the query held by the {@link QueryParseContext} in
     * {@link org.codelibs.elasticsearch.common.xcontent.XContent} format.
     *
     * @param context the input parse context. The state on the parser contained in this context will be changed as a side effect of this
     *        method call
     * @param elementName in some sort syntax variations the field name precedes the xContent object that specifies further parameters, e.g.
     *        in '{ "foo": { "order" : "asc"} }'. When parsing the inner object, the field name can be passed in via this argument
     */
    public static ScriptSortBuilder fromXContent(QueryParseContext context, String elementName) throws IOException {
        return PARSER.apply(context.parser(), context);
    }


    @Override
    public SortFieldAndFormat build(QueryShardContext context) throws IOException {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ScriptSortBuilder other = (ScriptSortBuilder) object;
        return Objects.equals(script, other.script) &&
                Objects.equals(type, other.type) &&
                Objects.equals(order, other.order) &&
                Objects.equals(sortMode, other.sortMode) &&
                Objects.equals(nestedFilter, other.nestedFilter) &&
                Objects.equals(nestedPath, other.nestedPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(script, type, order, sortMode, nestedFilter, nestedPath);
    }

    @Override
    public String getWriteableName() {
        return NAME;
    }

    public enum ScriptSortType implements Writeable {
        /** script sort for a string value **/
        STRING,
        /** script sort for a numeric value **/
        NUMBER;

        @Override
        public void writeTo(final StreamOutput out) throws IOException {
            out.writeVInt(ordinal());
        }

        /**
         * Read from a stream.
         */
        static ScriptSortType readFromStream(final StreamInput in) throws IOException {
            int ordinal = in.readVInt();
            if (ordinal < 0 || ordinal >= values().length) {
                throw new IOException("Unknown ScriptSortType ordinal [" + ordinal + "]");
            }
            return values()[ordinal];
        }

        public static ScriptSortType fromString(final String str) {
            Objects.requireNonNull(str, "input string is null");
            switch (str.toLowerCase(Locale.ROOT)) {
                case ("string"):
                    return ScriptSortType.STRING;
                case ("number"):
                    return ScriptSortType.NUMBER;
                default:
                    throw new IllegalArgumentException("Unknown ScriptSortType [" + str + "]");
            }
        }

        @Override
        public String toString() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
