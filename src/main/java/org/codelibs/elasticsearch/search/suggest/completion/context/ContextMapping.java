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

package org.codelibs.elasticsearch.search.suggest.completion.context;

import org.codelibs.elasticsearch.ElasticsearchParseException;
import org.codelibs.elasticsearch.common.xcontent.ToXContent;
import org.codelibs.elasticsearch.common.xcontent.XContentBuilder;
import org.codelibs.elasticsearch.common.xcontent.XContentParser;
import org.codelibs.elasticsearch.common.xcontent.XContentParser.Token;
import org.codelibs.elasticsearch.common.xcontent.json.JsonXContent;
import org.codelibs.elasticsearch.index.query.QueryParseContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A {ContextMapping} defines criteria that can be used to
 *
 * Implementations have to define how contexts are parsed at query/index time
 */
public abstract class ContextMapping<T extends ToXContent> implements ToXContent {

    public static final String FIELD_TYPE = "type";
    public static final String FIELD_NAME = "name";
    protected final Type type;
    protected final String name;

    public enum Type {
        CATEGORY, GEO;

        public static Type fromString(String type) {
            if (type.equalsIgnoreCase("category")) {
                return CATEGORY;
            } else if (type.equalsIgnoreCase("geo")) {
                return GEO;
            } else {
                throw new IllegalArgumentException("No context type for [" + type + "]");
            }
        }
    }

    /**
     * Define a new context mapping of a specific type
     *
     * @param type type of context mapping, either {Type#CATEGORY} or {Type#GEO}
     * @param name name of context mapping
     */
    protected ContextMapping(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * @return the type name of the context
     */
    public Type type() {
        return type;
    }

    /**
     * @return the name/id of the context
     */
    public String name() {
        return name;
    }

    /**
     * Prototype for the query context
     */
    protected abstract T fromXContent(QueryParseContext context) throws IOException;

    /**
     * Parses query contexts for this mapper
     */
    public final List<InternalQueryContext> parseQueryContext(QueryParseContext context) throws IOException, ElasticsearchParseException {
        List<T> queryContexts = new ArrayList<>();
        XContentParser parser = context.parser();
        Token token = parser.nextToken();
        if (token == Token.START_OBJECT || token == Token.VALUE_STRING) {
            queryContexts.add(fromXContent(context));
        } else if (token == Token.START_ARRAY) {
            while (parser.nextToken() != Token.END_ARRAY) {
                queryContexts.add(fromXContent(context));
            }
        }
        return toInternalQueryContexts(queryContexts);
    }

    /**
     * Convert query contexts to common representation
     */
    protected abstract List<InternalQueryContext> toInternalQueryContexts(List<T> queryContexts);

    /**
     * Implementations should add specific configurations
     * that need to be persisted
     */
    protected abstract XContentBuilder toInnerXContent(XContentBuilder builder, Params params) throws IOException;

    @Override
    public final XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.field(FIELD_NAME, name);
        builder.field(FIELD_TYPE, type.name());
        toInnerXContent(builder, params);
        return builder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContextMapping that = (ContextMapping) o;
        if (type != that.type) {
            return false;
        }
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name);
    }

    @Override
    public String toString() {
        try {
            return toXContent(JsonXContent.contentBuilder(), ToXContent.EMPTY_PARAMS).string();
        } catch (IOException e) {
            return super.toString();
        }
    }

    public static class InternalQueryContext {
        public final String context;
        public final int boost;
        public final boolean isPrefix;

        public InternalQueryContext(String context, int boost, boolean isPrefix) {
            this.context = context;
            this.boost = boost;
            this.isPrefix = isPrefix;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            InternalQueryContext that = (InternalQueryContext) o;

            if (boost != that.boost) {
                return false;
            }
            if (isPrefix != that.isPrefix) {
                return false;
            }
            return context != null ? context.equals(that.context) : that.context == null;

        }

        @Override
        public int hashCode() {
            int result = context != null ? context.hashCode() : 0;
            result = 31 * result + boost;
            result = 31 * result + (isPrefix ? 1 : 0);
            return result;
        }

        @Override
        public String toString() {
            return "QueryContext{" +
                    "context='" + context + '\'' +
                    ", boost=" + boost +
                    ", isPrefix=" + isPrefix +
                    '}';
        }
    }
}
