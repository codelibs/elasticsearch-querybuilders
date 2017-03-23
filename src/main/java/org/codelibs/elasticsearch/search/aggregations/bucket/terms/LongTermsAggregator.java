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
package org.codelibs.elasticsearch.search.aggregations.bucket.terms;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.SortedNumericDocValues;
import org.codelibs.elasticsearch.common.lease.Releasables;
import org.codelibs.elasticsearch.common.util.LongHash;
import org.codelibs.elasticsearch.search.DocValueFormat;
import org.codelibs.elasticsearch.search.aggregations.Aggregator;
import org.codelibs.elasticsearch.search.aggregations.AggregatorFactories;
import org.codelibs.elasticsearch.search.aggregations.InternalAggregation;
import org.codelibs.elasticsearch.search.aggregations.LeafBucketCollector;
import org.codelibs.elasticsearch.search.aggregations.LeafBucketCollectorBase;
import org.codelibs.elasticsearch.search.aggregations.bucket.terms.support.IncludeExclude;
import org.codelibs.elasticsearch.search.aggregations.bucket.terms.support.IncludeExclude.LongFilter;
import org.codelibs.elasticsearch.search.aggregations.pipeline.PipelineAggregator;
import org.codelibs.elasticsearch.search.aggregations.support.ValuesSource;
import org.codelibs.elasticsearch.search.internal.SearchContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

/**
 *
 */
public class LongTermsAggregator extends TermsAggregator {

    protected final ValuesSource.Numeric valuesSource;
    protected final LongHash bucketOrds;
    private boolean showTermDocCountError;
    private LongFilter longFilter;

    public LongTermsAggregator(String name, AggregatorFactories factories, ValuesSource.Numeric valuesSource, DocValueFormat format,
            Terms.Order order, BucketCountThresholds bucketCountThresholds, SearchContext aggregationContext, Aggregator parent,
            SubAggCollectionMode subAggCollectMode, boolean showTermDocCountError, IncludeExclude.LongFilter longFilter,
            List<PipelineAggregator> pipelineAggregators, Map<String, Object> metaData) throws IOException {
        super(name, factories, aggregationContext, parent, bucketCountThresholds, order, format, subAggCollectMode, pipelineAggregators, metaData);
        this.valuesSource = valuesSource;
        this.showTermDocCountError = showTermDocCountError;
        this.longFilter = longFilter;
        bucketOrds = new LongHash(1, aggregationContext.bigArrays());
    }

    @Override
    public boolean needsScores() {
        return (valuesSource != null && valuesSource.needsScores()) || super.needsScores();
    }

    protected SortedNumericDocValues getValues(ValuesSource.Numeric valuesSource, LeafReaderContext ctx) throws IOException {
        return valuesSource.longValues(ctx);
    }

    @Override
    public LeafBucketCollector getLeafCollector(LeafReaderContext ctx,
            final LeafBucketCollector sub) throws IOException {
        final SortedNumericDocValues values = getValues(valuesSource, ctx);
        return new LeafBucketCollectorBase(sub, values) {
            @Override
            public void collect(int doc, long owningBucketOrdinal) throws IOException {
                assert owningBucketOrdinal == 0;
                values.setDocument(doc);
                final int valuesCount = values.count();

                long previous = Long.MAX_VALUE;
                for (int i = 0; i < valuesCount; ++i) {
                    final long val = values.valueAt(i);
                    if (previous != val || i == 0) {
                        if ((longFilter == null) || (longFilter.accept(val))) {
                            long bucketOrdinal = bucketOrds.add(val);
                            if (bucketOrdinal < 0) { // already seen
                                bucketOrdinal = - 1 - bucketOrdinal;
                                collectExistingBucket(sub, doc, bucketOrdinal);
                            } else {
                                collectBucket(sub, doc, bucketOrdinal);
                            }
                        }

                        previous = val;
                    }
                }
            }
        };
    }

    @Override
    public InternalAggregation buildAggregation(long owningBucketOrdinal) throws IOException {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    public InternalAggregation buildEmptyAggregation() {
        return new LongTerms(name, order, bucketCountThresholds.getRequiredSize(), bucketCountThresholds.getMinDocCount(),
                pipelineAggregators(), metaData(), format, bucketCountThresholds.getShardSize(), showTermDocCountError, 0, emptyList(), 0);
    }

    @Override
    public void doClose() {
        Releasables.close(bucketOrds);
    }

}
