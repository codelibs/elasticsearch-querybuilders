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

package org.codelibs.elasticsearch.search.aggregations.bucket.children;

import org.apache.lucene.search.Query;
import org.codelibs.elasticsearch.common.ParsingException;
import org.codelibs.elasticsearch.common.io.stream.StreamInput;
import org.codelibs.elasticsearch.common.io.stream.StreamOutput;
import org.codelibs.elasticsearch.common.xcontent.XContentBuilder;
import org.codelibs.elasticsearch.common.xcontent.XContentParser;
import org.codelibs.elasticsearch.index.query.QueryParseContext;
import org.codelibs.elasticsearch.search.aggregations.AggregatorFactories.Builder;
import org.codelibs.elasticsearch.search.aggregations.AggregatorFactory;
import org.codelibs.elasticsearch.search.aggregations.InternalAggregation.Type;
import org.codelibs.elasticsearch.search.aggregations.support.ValueType;
import org.codelibs.elasticsearch.search.aggregations.support.ValuesSource.Bytes.ParentChild;
import org.codelibs.elasticsearch.search.aggregations.support.ValuesSourceAggregationBuilder;
import org.codelibs.elasticsearch.search.aggregations.support.ValuesSourceAggregatorFactory;
import org.codelibs.elasticsearch.search.aggregations.support.ValuesSourceConfig;
import org.codelibs.elasticsearch.search.aggregations.support.ValuesSourceType;
import org.codelibs.elasticsearch.search.internal.SearchContext;

import java.io.IOException;
import java.util.Objects;

public class ChildrenAggregationBuilder extends ValuesSourceAggregationBuilder<ParentChild, ChildrenAggregationBuilder> {
    public static final String NAME = "children";
    private static final Type TYPE = new Type(NAME);

    private String parentType;
    private final String childType;
    private Query parentFilter;
    private Query childFilter;

    /**
     * @param name
     *            the name of this aggregation
     * @param childType
     *            the type of children documents
     */
    public ChildrenAggregationBuilder(String name, String childType) {
        super(name, TYPE, ValuesSourceType.BYTES, ValueType.STRING);
        if (childType == null) {
            throw new IllegalArgumentException("[childType] must not be null: [" + name + "]");
        }
        this.childType = childType;
    }

    /**
     * Read from a stream.
     */
    public ChildrenAggregationBuilder(StreamInput in) throws IOException {
        super(in, TYPE, ValuesSourceType.BYTES, ValueType.STRING);
        childType = in.readString();
    }

    @Override
    protected void innerWriteTo(StreamOutput out) throws IOException {
        out.writeString(childType);
    }

    @Override
    protected ValuesSourceAggregatorFactory<ParentChild, ?> innerBuild(SearchContext context,
            ValuesSourceConfig<ParentChild> config, AggregatorFactory<?> parent, Builder subFactoriesBuilder) throws IOException {
        return new ChildrenAggregatorFactory(name, type, config, parentType, childFilter, parentFilter, context, parent,
                subFactoriesBuilder, metaData);
    }

    @Override
    protected ValuesSourceConfig<ParentChild> resolveConfig(SearchContext context) {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    protected XContentBuilder doXContentBody(XContentBuilder builder, Params params) throws IOException {
        builder.field(ParentToChildrenAggregator.TYPE_FIELD.getPreferredName(), childType);
        return builder;
    }

    public static ChildrenAggregationBuilder parse(String aggregationName, QueryParseContext context) throws IOException {
        String childType = null;

        XContentParser.Token token;
        String currentFieldName = null;
        XContentParser parser = context.parser();
        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (token == XContentParser.Token.VALUE_STRING) {
                if ("type".equals(currentFieldName)) {
                    childType = parser.text();
                } else {
                    throw new ParsingException(parser.getTokenLocation(),
                            "Unknown key for a " + token + " in [" + aggregationName + "]: [" + currentFieldName + "].");
                }
            } else {
                throw new ParsingException(parser.getTokenLocation(), "Unexpected token " + token + " in [" + aggregationName + "].");
            }
        }

        if (childType == null) {
            throw new ParsingException(parser.getTokenLocation(),
                    "Missing [child_type] field for children aggregation [" + aggregationName + "]");
        }


        return new ChildrenAggregationBuilder(aggregationName, childType);
    }

    @Override
    protected int innerHashCode() {
        return Objects.hash(childType);
    }

    @Override
    protected boolean innerEquals(Object obj) {
        ChildrenAggregationBuilder other = (ChildrenAggregationBuilder) obj;
        return Objects.equals(childType, other.childType);
    }

    @Override
    public String getWriteableName() {
        return NAME;
    }
}
