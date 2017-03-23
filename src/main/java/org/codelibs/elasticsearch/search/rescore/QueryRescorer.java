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

package org.codelibs.elasticsearch.search.rescore;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.codelibs.elasticsearch.search.internal.SearchContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

public final class QueryRescorer implements Rescorer {

    public static final Rescorer INSTANCE = new QueryRescorer();
    public static final String NAME = "query";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public TopDocs rescore(TopDocs topDocs, SearchContext context, RescoreSearchContext rescoreContext) throws IOException {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    public Explanation explain(int topLevelDocId, SearchContext context, RescoreSearchContext rescoreContext,
                               Explanation sourceExplanation) throws IOException {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    private static final Comparator<ScoreDoc> SCORE_DOC_COMPARATOR = (o1, o2) -> {
        int cmp = Float.compare(o2.score, o1.score);
        return cmp == 0 ?  Integer.compare(o1.doc, o2.doc) : cmp;
    };

    public static class QueryRescoreContext extends RescoreSearchContext {

        static final int DEFAULT_WINDOW_SIZE = 10;

        public QueryRescoreContext(QueryRescorer rescorer) {
            super(NAME, DEFAULT_WINDOW_SIZE, rescorer);
            this.scoreMode = QueryRescoreMode.Total;
        }

        private Query query;
        private float queryWeight = 1.0f;
        private float rescoreQueryWeight = 1.0f;
        private QueryRescoreMode scoreMode;

        public void setQuery(Query query) {
            this.query = query;
        }

        public Query query() {
            return query;
        }

        public float queryWeight() {
            return queryWeight;
        }

        public float rescoreQueryWeight() {
            return rescoreQueryWeight;
        }

        public QueryRescoreMode scoreMode() {
            return scoreMode;
        }

        public void setRescoreQueryWeight(float rescoreQueryWeight) {
            this.rescoreQueryWeight = rescoreQueryWeight;
        }

        public void setQueryWeight(float queryWeight) {
            this.queryWeight = queryWeight;
        }

        public void setScoreMode(QueryRescoreMode scoreMode) {
            this.scoreMode = scoreMode;
        }

        public void setScoreMode(String scoreMode) {
            setScoreMode(QueryRescoreMode.fromString(scoreMode));
        }
    }

    @Override
    public void extractTerms(SearchContext context, RescoreSearchContext rescoreContext, Set<Term> termsSet) {
        throw new UnsupportedOperationException();
    }

}
