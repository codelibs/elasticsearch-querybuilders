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

package org.codelibs.elasticsearch.index.query;

import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermsQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;
import org.codelibs.elasticsearch.common.ParseField;
import org.codelibs.elasticsearch.common.ParsingException;
import org.codelibs.elasticsearch.common.Strings;
import org.codelibs.elasticsearch.common.bytes.BytesReference;
import org.codelibs.elasticsearch.common.io.stream.BytesStreamOutput;
import org.codelibs.elasticsearch.common.io.stream.StreamInput;
import org.codelibs.elasticsearch.common.io.stream.StreamOutput;
import org.codelibs.elasticsearch.common.lucene.BytesRefs;
import org.codelibs.elasticsearch.common.lucene.search.Queries;
import org.codelibs.elasticsearch.common.xcontent.XContentBuilder;
import org.codelibs.elasticsearch.common.xcontent.XContentParser;
import org.codelibs.elasticsearch.common.xcontent.support.XContentMapValues;
import org.codelibs.elasticsearch.index.mapper.MappedFieldType;

import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A filter for a field based on several terms matching on any of them.
 */
public class TermsQueryBuilder extends AbstractQueryBuilder<TermsQueryBuilder> {
    public static final String NAME = "terms";
    public static final ParseField QUERY_NAME_FIELD = new ParseField(NAME, "in");

    private final String fieldName;
    private final List<?> values;

    /**
     * A filter for a field based on several terms matching on any of them.
     *
     * @param fieldName The field name
     * @param values The terms
     */
    public TermsQueryBuilder(String fieldName, String... values) {
        this(fieldName, values != null ? Arrays.asList(values) : null);
    }

    /**
     * A filter for a field based on several terms matching on any of them.
     *
     * @param fieldName The field name
     * @param values The terms
     */
    public TermsQueryBuilder(String fieldName, int... values) {
        this(fieldName, values != null ? Arrays.stream(values).mapToObj(s -> s).collect(Collectors.toList()) : (Iterable<?>) null);
    }

    /**
     * A filter for a field based on several terms matching on any of them.
     *
     * @param fieldName The field name
     * @param values The terms
     */
    public TermsQueryBuilder(String fieldName, long... values) {
        this(fieldName, values != null ? Arrays.stream(values).mapToObj(s -> s).collect(Collectors.toList()) : (Iterable<?>) null);
    }

    /**
     * A filter for a field based on several terms matching on any of them.
     *
     * @param fieldName The field name
     * @param values The terms
     */
    public TermsQueryBuilder(String fieldName, float... values) {
        this(fieldName, values != null ? IntStream.range(0, values.length)
                           .mapToObj(i -> values[i]).collect(Collectors.toList()) : (Iterable<?>) null);
    }

    /**
     * A filter for a field based on several terms matching on any of them.
     *
     * @param fieldName The field name
     * @param values The terms
     */
    public TermsQueryBuilder(String fieldName, double... values) {
        this(fieldName, values != null ? Arrays.stream(values).mapToObj(s -> s).collect(Collectors.toList()) : (Iterable<?>) null);
    }

    /**
     * A filter for a field based on several terms matching on any of them.
     *
     * @param fieldName The field name
     * @param values The terms
     */
    public TermsQueryBuilder(String fieldName, Object... values) {
        this(fieldName, values != null ? Arrays.asList(values) : (Iterable<?>) null);
    }

    /**
     * A filter for a field based on several terms matching on any of them.
     *
     * @param fieldName The field name
     * @param values The terms
     */
    public TermsQueryBuilder(String fieldName, Iterable<?> values) {
        if (Strings.isEmpty(fieldName)) {
            throw new IllegalArgumentException("field name cannot be null.");
        }
        if (values == null) {
            throw new IllegalArgumentException("No value specified for terms query");
        }
        this.fieldName = fieldName;
        this.values = convert(values);
    }

    /**
     * Read from a stream.
     */
    public TermsQueryBuilder(StreamInput in) throws IOException {
        super(in);
        fieldName = in.readString();
        values = (List<?>) in.readGenericValue();
    }

    @Override
    protected void doWriteTo(StreamOutput out) throws IOException {
        out.writeString(fieldName);
        out.writeGenericValue(values);
    }

    public String fieldName() {
        return this.fieldName;
    }

    public List<Object> values() {
        return convertBack(this.values);
    }

    private static final Set<Class<? extends Number>> INTEGER_TYPES = new HashSet<>(
            Arrays.asList(Byte.class, Short.class, Integer.class, Long.class));
    private static final Set<Class<?>> STRING_TYPES = new HashSet<>(
            Arrays.asList(BytesRef.class, String.class));

    /**
     * Same as {@link #convert(List)} but on an {@link Iterable}.
     */
    private static List<?> convert(Iterable<?> values) {
        List<?> list;
        if (values instanceof List<?>) {
            list = (List<?>) values;
        } else {
            ArrayList<Object> arrayList = new ArrayList<Object>();
            for (Object o : values) {
                arrayList.add(o);
            }
            list = arrayList;
        }
        return convert(list);
    }

    /**
     * Convert the list in a way that optimizes storage in the case that all
     * elements are either integers or {@link String}s/{@link BytesRef}s. This
     * is useful to help garbage collections for use-cases that involve sending
     * very large terms queries to Elasticsearch. If the list does not only
     * contain integers or {@link String}s, then a list is returned where all
     * {@link String}s have been replaced with {@link BytesRef}s.
     */
    static List<?> convert(List<?> list) {
        if (list.isEmpty()) {
            return Collections.emptyList();
        }

        final boolean allNumbers = list.stream().allMatch(o -> o != null && INTEGER_TYPES.contains(o.getClass()));
        if (allNumbers) {
            final long[] elements = list.stream().mapToLong(o -> ((Number) o).longValue()).toArray();
            return new AbstractList<Object>() {
                @Override
                public Object get(int index) {
                    return elements[index];
                }
                @Override
                public int size() {
                    return elements.length;
                }
            };
        }

        final boolean allStrings = list.stream().allMatch(o -> o != null && STRING_TYPES.contains(o.getClass()));
        if (allStrings) {
            final BytesRefBuilder builder = new BytesRefBuilder();
            try (final BytesStreamOutput bytesOut = new BytesStreamOutput()) {
                final int[] endOffsets = new int[list.size()];
                int i = 0;
                for (Object o : list) {
                    BytesRef b;
                    if (o instanceof BytesRef) {
                        b = (BytesRef) o;
                    } else {
                        builder.copyChars(o.toString()); 
                        b = builder.get();
                    }
                    bytesOut.writeBytes(b.bytes, b.offset, b.length);
                    if (i == 0) {
                        endOffsets[0] = b.length;
                    } else {
                        endOffsets[i] = Math.addExact(endOffsets[i-1], b.length);
                    }
                    ++i;
                }
                final BytesReference bytes = bytesOut.bytes();
                return new AbstractList<Object>() {
                    public Object get(int i) {
                        final int startOffset = i == 0 ? 0 : endOffsets[i-1];
                        final int endOffset = endOffsets[i];
                        return bytes.slice(startOffset, endOffset - startOffset).toBytesRef();
                    }
                    public int size() {
                        return endOffsets.length;
                    }
                };
            }
        }

        return list.stream().map(o -> o instanceof String ? new BytesRef(o.toString()) : o).collect(Collectors.toList());
    }

    /**
     * Convert the internal {@link List} of values back to a user-friendly list.
     * Integers are kept as-is since the terms query does not make any difference
     * between {@link Integer}s and {@link Long}s, but {@link BytesRef}s are
     * converted back to {@link String}s.
     */
    static List<Object> convertBack(List<?> list) {
        return new AbstractList<Object>() {
            @Override
            public int size() {
                return list.size();
            }
            @Override
            public Object get(int index) {
                Object o = list.get(index);
                if (o instanceof BytesRef) {
                    o = ((BytesRef) o).utf8ToString();
                }
                // we do not convert longs, all integer types are equivalent
                // as far as this query is concerned
                return o;
            }
        };
    }

    @Override
    protected void doXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject(NAME);
        builder.field(fieldName, convertBack(values));
        printBoostAndQueryName(builder);
        builder.endObject();
    }

    public static Optional<TermsQueryBuilder> fromXContent(QueryParseContext parseContext) throws IOException {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    private static List<Object> parseValues(XContentParser parser) throws IOException {
        List<Object> values = new ArrayList<>();
        while (parser.nextToken() != XContentParser.Token.END_ARRAY) {
            Object value = parser.objectBytes();
            if (value == null) {
                throw new ParsingException(parser.getTokenLocation(), "No value specified for terms query");
            }
            values.add(value);
        }
        return values;
    }

    @Override
    public String getWriteableName() {
        return NAME;
    }

    @Override
    protected Query doToQuery(QueryShardContext context) throws IOException {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }


    @Override
    protected int doHashCode() {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    protected boolean doEquals(TermsQueryBuilder other) {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    protected QueryBuilder doRewrite(QueryRewriteContext queryRewriteContext) throws IOException {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

}
