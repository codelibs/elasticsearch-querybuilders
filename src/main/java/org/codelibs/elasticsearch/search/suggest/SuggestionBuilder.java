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

package org.codelibs.elasticsearch.search.suggest;

import org.codelibs.elasticsearch.ElasticsearchParseException;
import org.codelibs.elasticsearch.common.ParseField;
import org.codelibs.elasticsearch.common.ParseFieldMatcher;
import org.codelibs.elasticsearch.common.ParsingException;
import org.codelibs.elasticsearch.common.io.stream.NamedWriteable;
import org.codelibs.elasticsearch.common.io.stream.StreamInput;
import org.codelibs.elasticsearch.common.io.stream.StreamOutput;
import org.codelibs.elasticsearch.common.xcontent.ToXContent;
import org.codelibs.elasticsearch.common.xcontent.XContentBuilder;
import org.codelibs.elasticsearch.common.xcontent.XContentParser;
import org.codelibs.elasticsearch.index.query.QueryParseContext;
import org.codelibs.elasticsearch.index.query.QueryShardContext;
import org.codelibs.elasticsearch.search.suggest.SuggestionSearchContext.SuggestionContext;

import java.io.IOException;
import java.util.Objects;

/**
 * Base class for the different suggestion implementations.
 */
public abstract class SuggestionBuilder<T extends SuggestionBuilder<T>> implements NamedWriteable, ToXContent {

    protected final String field;
    protected String text;
    protected String prefix;
    protected String regex;
    protected String analyzer;
    protected Integer size;
    protected Integer shardSize;

    protected static final ParseField TEXT_FIELD = new ParseField("text");
    protected static final ParseField PREFIX_FIELD = new ParseField("prefix");
    protected static final ParseField REGEX_FIELD = new ParseField("regex");
    protected static final ParseField FIELDNAME_FIELD = new ParseField("field");
    protected static final ParseField ANALYZER_FIELD = new ParseField("analyzer");
    protected static final ParseField SIZE_FIELD = new ParseField("size");
    protected static final ParseField SHARDSIZE_FIELD = new ParseField("shard_size");

    /**
     * Creates a new suggestion.
     * @param field field to execute suggestions on
     */
    protected SuggestionBuilder(String field) {
        Objects.requireNonNull(field, "suggestion requires a field name");
        if (field.isEmpty()) {
            throw new IllegalArgumentException("suggestion field name is empty");
        }
        this.field = field;
    }

    /**
     * internal copy constructor that copies over all class fields from second SuggestionBuilder except field name.
     */
    protected SuggestionBuilder(String field, SuggestionBuilder<?> in) {
        this(field);
        text = in.text;
        prefix = in.prefix;
        regex = in.regex;
        analyzer = in.analyzer;
        size = in.size;
        shardSize = in.shardSize;
    }

    /**
     * Read from a stream.
     */
    protected SuggestionBuilder(StreamInput in) throws IOException {
        field = in.readString();
        text = in.readOptionalString();
        prefix = in.readOptionalString();
        regex = in.readOptionalString();
        analyzer = in.readOptionalString();
        size = in.readOptionalVInt();
        shardSize = in.readOptionalVInt();
    }

    @Override
    public final void writeTo(StreamOutput out) throws IOException {
        out.writeString(field);
        out.writeOptionalString(text);
        out.writeOptionalString(prefix);
        out.writeOptionalString(regex);
        out.writeOptionalString(analyzer);
        out.writeOptionalVInt(size);
        out.writeOptionalVInt(shardSize);
        doWriteTo(out);
    }

    protected abstract void doWriteTo(StreamOutput out) throws IOException;


    /**
     * Same as in {SuggestBuilder#setGlobalText(String)}, but in the suggestion scope.
     */
    @SuppressWarnings("unchecked")
    public T text(String text) {
        this.text = text;
        return (T) this;
    }

    /**
     * get the text for this suggestion
     */
    public String text() {
        return this.text;
    }

    @SuppressWarnings("unchecked")
    protected T prefix(String prefix) {
        this.prefix = prefix;
        return (T) this;
    }

    /**
     * get the prefix for this suggestion
     */
    public String prefix() {
        return this.prefix;
    }

    @SuppressWarnings("unchecked")
    protected T regex(String regex) {
        this.regex = regex;
        return (T) this;
    }

    /**
     * get the regex for this suggestion
     */
    public String regex() {
        return this.regex;
    }

    /**
     * get the {#field()} parameter
     */
    public String field() {
        return this.field;
    }

    /**
     * Sets the analyzer to analyse to suggest text with. Defaults to the search
     * analyzer of the suggest field.
     */
    @SuppressWarnings("unchecked")
    public T analyzer(String analyzer) {
        this.analyzer = analyzer;
        return (T)this;
    }

    /**
     * get the {#analyzer()} parameter
     */
    public String analyzer() {
        return this.analyzer;
    }

    /**
     * Sets the maximum suggestions to be returned per suggest text term.
     */
    @SuppressWarnings("unchecked")
    public T size(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be positive");
        }
        this.size = size;
        return (T)this;
    }

    /**
     * get the {#size()} parameter
     */
    public Integer size() {
        return this.size;
    }

    /**
     * Sets the maximum number of suggested term to be retrieved from each
     * individual shard. During the reduce phase the only the top N suggestions
     * are returned based on the <code>size</code> option. Defaults to the
     * <code>size</code> option.
     * <p>
     * Setting this to a value higher than the `size` can be useful in order to
     * get a more accurate document frequency for suggested terms. Due to the
     * fact that terms are partitioned amongst shards, the shard level document
     * frequencies of suggestions may not be precise. Increasing this will make
     * these document frequencies more precise.
     */
    @SuppressWarnings("unchecked")
    public T shardSize(Integer shardSize) {
        this.shardSize = shardSize;
        return (T)this;
    }

    /**
     * get the {#shardSize()} parameter
     */
    public Integer shardSize() {
        return this.shardSize;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        if (text != null) {
            builder.field(TEXT_FIELD.getPreferredName(), text);
        }
        if (prefix != null) {
            builder.field(PREFIX_FIELD.getPreferredName(), prefix);
        }
        if (regex != null) {
            builder.field(REGEX_FIELD.getPreferredName(), regex);
        }
        builder.startObject(getSuggesterName());
        if (analyzer != null) {
            builder.field(ANALYZER_FIELD.getPreferredName(), analyzer);
        }
        builder.field(FIELDNAME_FIELD.getPreferredName(), field);
        if (size != null) {
            builder.field(SIZE_FIELD.getPreferredName(), size);
        }
        if (shardSize != null) {
            builder.field(SHARDSIZE_FIELD.getPreferredName(), shardSize);
        }

        builder = innerToXContent(builder, params);
        builder.endObject();
        return builder;
    }

    protected abstract XContentBuilder innerToXContent(XContentBuilder builder, Params params) throws IOException;

    static SuggestionBuilder<?> fromXContent(QueryParseContext parseContext, Suggesters suggesters)
            throws IOException {
        XContentParser parser = parseContext.parser();
        parseContext.getParseFieldMatcher();
        XContentParser.Token token;
        String currentFieldName = null;
        String suggestText = null;
        String prefix = null;
        String regex = null;
        SuggestionBuilder<?> suggestionBuilder = null;

        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
            if (token == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (token.isValue()) {
                if (TEXT_FIELD.match(currentFieldName)) {
                    suggestText = parser.text();
                } else if (PREFIX_FIELD.match(currentFieldName)) {
                    prefix = parser.text();
                } else if (REGEX_FIELD.match(currentFieldName)) {
                    regex = parser.text();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "suggestion does not support [" + currentFieldName + "]");
                }
            } else if (token == XContentParser.Token.START_OBJECT) {
                suggestionBuilder = suggesters.getSuggester(currentFieldName).innerFromXContent(parseContext);
            }
        }
        if (suggestionBuilder == null) {
            throw new ElasticsearchParseException("missing suggestion object");
        }
        if (suggestText != null) {
            suggestionBuilder.text(suggestText);
        }
        if (prefix != null) {
            suggestionBuilder.prefix(prefix);
        }
        if (regex != null) {
            suggestionBuilder.regex(regex);
        }
        return suggestionBuilder;
    }

    protected abstract SuggestionContext build(QueryShardContext context) throws IOException;

    private String getSuggesterName() {
        //default impl returns the same as writeable name, but we keep the distinction between the two just to make sure
        return getWriteableName();
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        @SuppressWarnings("unchecked")
        T other = (T) obj;
        return Objects.equals(text, other.text()) &&
               Objects.equals(prefix, other.prefix()) &&
               Objects.equals(regex, other.regex()) &&
               Objects.equals(field, other.field()) &&
               Objects.equals(analyzer, other.analyzer()) &&
               Objects.equals(size, other.size()) &&
               Objects.equals(shardSize, other.shardSize()) &&
               doEquals(other);
    }

    /**
     * Indicates whether some other {SuggestionBuilder} of the same type is "equal to" this one.
     */
    protected abstract boolean doEquals(T other);

    @Override
    public final int hashCode() {
        return Objects.hash(text, prefix, regex, field, analyzer, size, shardSize, doHashCode());
    }

    /**
     * HashCode for the subclass of {SuggestionBuilder} to implement.
     */
    protected abstract int doHashCode();

}
