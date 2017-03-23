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

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.BulkScorer;
import org.apache.lucene.search.CollectionTerminatedException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.suggest.Lookup;
import org.apache.lucene.search.suggest.document.CompletionQuery;
import org.apache.lucene.search.suggest.document.TopSuggestDocs;
import org.apache.lucene.search.suggest.document.TopSuggestDocsCollector;
import org.apache.lucene.util.CharsRefBuilder;
import org.apache.lucene.util.PriorityQueue;
import org.codelibs.elasticsearch.common.io.stream.StreamInput;
import org.codelibs.elasticsearch.index.query.QueryParseContext;
import org.codelibs.elasticsearch.search.suggest.Suggest;
import org.codelibs.elasticsearch.search.suggest.Suggester;
import org.codelibs.elasticsearch.search.suggest.SuggestionBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CompletionSuggester extends Suggester<CompletionSuggestionContext> {

    public static final CompletionSuggester INSTANCE = new CompletionSuggester();

    private CompletionSuggester() {}

    @Override
    protected Suggest.Suggestion<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> innerExecute(String name,
            final CompletionSuggestionContext suggestionContext, final IndexSearcher searcher, CharsRefBuilder spare) throws IOException {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    private static final ScoreComparator scoreComparator = new ScoreComparator();
    public static class ScoreComparator implements
        Comparator<org.codelibs.elasticsearch.search.suggest.completion2x.CompletionSuggestion.Entry.Option> {
        @Override
        public int compare(org.codelibs.elasticsearch.search.suggest.completion2x.CompletionSuggestion.Entry.Option o1,
                           org.codelibs.elasticsearch.search.suggest.completion2x.CompletionSuggestion.Entry.Option o2) {
            return Float.compare(o2.getScore(), o1.getScore());
        }
    }

    @Override
    public SuggestionBuilder<?> innerFromXContent(QueryParseContext context) throws IOException {
        return CompletionSuggestionBuilder.innerFromXContent(context);
    }

    @Override
    public SuggestionBuilder<?> read(StreamInput in) throws IOException {
        return new CompletionSuggestionBuilder(in);
    }
}
