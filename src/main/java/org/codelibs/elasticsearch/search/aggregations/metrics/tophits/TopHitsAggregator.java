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

package org.codelibs.elasticsearch.search.aggregations.metrics.tophits;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.search.Scorer;
import org.codelibs.elasticsearch.common.lease.Releasables;
import org.codelibs.elasticsearch.common.util.LongObjectPagedHashMap;
import org.codelibs.elasticsearch.search.aggregations.Aggregator;
import org.codelibs.elasticsearch.search.aggregations.InternalAggregation;
import org.codelibs.elasticsearch.search.aggregations.LeafBucketCollector;
import org.codelibs.elasticsearch.search.aggregations.LeafBucketCollectorBase;
import org.codelibs.elasticsearch.search.aggregations.metrics.MetricsAggregator;
import org.codelibs.elasticsearch.search.aggregations.pipeline.PipelineAggregator;
import org.codelibs.elasticsearch.search.internal.SearchContext;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 */
public class TopHitsAggregator extends MetricsAggregator {

    /** Simple wrapper around a top-level collector and the current leaf collector. */
    private static class TopDocsAndLeafCollector {
        LeafCollector leafCollector;
    }

    final LongObjectPagedHashMap<TopDocsAndLeafCollector> topDocsCollectors;

    public TopHitsAggregator(String name, SearchContext context,
            Aggregator parent, List<PipelineAggregator> pipelineAggregators, Map<String, Object> metaData) throws IOException {
        super(name, context, parent, pipelineAggregators, metaData);
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    public boolean needsScores() {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    public LeafBucketCollector getLeafCollector(final LeafReaderContext ctx,
            final LeafBucketCollector sub) throws IOException {

        return new LeafBucketCollectorBase(sub, null) {

            @Override
            public void setScorer(Scorer scorer) throws IOException {
                for (LongObjectPagedHashMap.Cursor<TopDocsAndLeafCollector> cursor : topDocsCollectors) {
                    cursor.value.leafCollector.setScorer(scorer);
                }
                super.setScorer(scorer);
            }

            @Override
            public void collect(int docId, long bucket) throws IOException {
                throw new UnsupportedOperationException("querybuilders does not support this operation.");
            }
        };
    }

    @Override
    public InternalAggregation buildAggregation(long owningBucketOrdinal) {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    public InternalTopHits buildEmptyAggregation() {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    protected void doClose() {
        Releasables.close(topDocsCollectors);
    }
}
