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

import org.apache.lucene.index.IndexReader;
import org.codelibs.elasticsearch.common.ParseFieldMatcher;
import org.codelibs.elasticsearch.common.ParseFieldMatcherSupplier;
import org.codelibs.elasticsearch.common.bytes.BytesReference;
import org.codelibs.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.codelibs.elasticsearch.common.xcontent.XContentParser;
import org.codelibs.elasticsearch.script.Script;

/**
 * Context object used to rewrite {@link QueryBuilder} instances into simplified version.
 */
public class QueryRewriteContext implements ParseFieldMatcherSupplier {
    /** Return the current {@link IndexReader}, or {@code null} if no index reader is available, for
     *  instance if we are on the coordinating node or if this rewrite context is used to index
     *  queries (percolation). */
    public IndexReader getIndexReader() {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    public ParseFieldMatcher getParseFieldMatcher() {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    /**
     * The registry used to build new {@link XContentParser}s. Contains registered named parsers needed to parse the query.
     */
    public NamedXContentRegistry getXContentRegistry() {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    /**
     * Returns a new {@link QueryParseContext} that wraps the provided parser, using the ParseFieldMatcher settings that
     * are configured in the index settings. The default script language will always default to Painless.
     */
    public QueryParseContext newParseContext(XContentParser parser) {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    public long nowInMillis() {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    public BytesReference getTemplateBytes(Script template) {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }


}
