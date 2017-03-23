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
package org.codelibs.elasticsearch.search.suggest.completion;

import org.apache.lucene.search.suggest.document.CompletionQuery;
import org.codelibs.elasticsearch.index.query.QueryShardContext;
import org.codelibs.elasticsearch.search.suggest.SuggestionSearchContext;
import org.codelibs.elasticsearch.search.suggest.completion.context.ContextMapping;
import org.codelibs.elasticsearch.search.suggest.completion2x.context.ContextMapping.ContextQuery;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class CompletionSuggestionContext extends SuggestionSearchContext.SuggestionContext {

    protected CompletionSuggestionContext(QueryShardContext shardContext) {
        super(CompletionSuggester.INSTANCE, shardContext);
    }

    private FuzzyOptions fuzzyOptions;
    private RegexOptions regexOptions;
    private Map<String, List<ContextMapping.InternalQueryContext>> queryContexts = Collections.emptyMap();
    private List<ContextQuery> contextQueries;

    void setRegexOptions(RegexOptions regexOptions) {
        this.regexOptions = regexOptions;
    }

    void setFuzzyOptions(FuzzyOptions fuzzyOptions) {
        this.fuzzyOptions = fuzzyOptions;
    }

    void setQueryContexts(Map<String, List<ContextMapping.InternalQueryContext>> queryContexts) {
        this.queryContexts = queryContexts;
    }

    public FuzzyOptions getFuzzyOptions() {
        return fuzzyOptions;
    }

    public RegexOptions getRegexOptions() {
        return regexOptions;
    }

    public Map<String, List<ContextMapping.InternalQueryContext>> getQueryContexts() {
        return queryContexts;
    }

    CompletionQuery toQuery() {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    public void setContextQueries(List<ContextQuery> contextQueries) {
        this.contextQueries = contextQueries;
    }

    public List<ContextQuery> getContextQueries() {
        return contextQueries;
    }
}
