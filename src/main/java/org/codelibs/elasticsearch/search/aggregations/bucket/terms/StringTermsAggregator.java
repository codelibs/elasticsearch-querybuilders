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
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;
import org.codelibs.elasticsearch.common.lease.Releasables;
import org.codelibs.elasticsearch.common.util.BytesRefHash;
import org.codelibs.elasticsearch.index.fielddata.SortedBinaryDocValues;
import org.codelibs.elasticsearch.search.DocValueFormat;
import org.codelibs.elasticsearch.search.aggregations.Aggregator;
import org.codelibs.elasticsearch.search.aggregations.AggregatorFactories;
import org.codelibs.elasticsearch.search.aggregations.InternalAggregation;
import org.codelibs.elasticsearch.search.aggregations.LeafBucketCollector;
import org.codelibs.elasticsearch.search.aggregations.LeafBucketCollectorBase;
import org.codelibs.elasticsearch.search.aggregations.bucket.terms.support.BucketPriorityQueue;
import org.codelibs.elasticsearch.search.aggregations.bucket.terms.support.IncludeExclude;
import org.codelibs.elasticsearch.search.aggregations.pipeline.PipelineAggregator;
import org.codelibs.elasticsearch.search.aggregations.support.ValuesSource;
import org.codelibs.elasticsearch.search.internal.SearchContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * An aggregator of string values.
 */
public class StringTermsAggregator extends AbstractStringTermsAggregator {

    private final ValuesSource valuesSource;
    protected final BytesRefHash bucketOrds;
    private final IncludeExclude.StringFilter includeExclude;

    public StringTermsAggregator(String name, AggregatorFactories factories, ValuesSource valuesSource,
            Terms.Order order, DocValueFormat format, BucketCountThresholds bucketCountThresholds,
            IncludeExclude.StringFilter includeExclude, SearchContext context,
            Aggregator parent, SubAggCollectionMode collectionMode, boolean showTermDocCountError,
            List<PipelineAggregator> pipelineAggregators, Map<String, Object> metaData) throws IOException {

        super(name, factories, context, parent, order, format, bucketCountThresholds, collectionMode, showTermDocCountError,
                pipelineAggregators, metaData);
        this.valuesSource = valuesSource;
        this.includeExclude = includeExclude;
        bucketOrds = new BytesRefHash(1, context.bigArrays());
    }

    @Override
    public boolean needsScores() {
        return (valuesSource != null && valuesSource.needsScores()) || super.needsScores();
    }

    @Override
    public LeafBucketCollector getLeafCollector(LeafReaderContext ctx,
            final LeafBucketCollector sub) throws IOException {
        final SortedBinaryDocValues values = valuesSource.bytesValues(ctx);
        return new LeafBucketCollectorBase(sub, values) {
            final BytesRefBuilder previous = new BytesRefBuilder();

            @Override
            public void collect(int doc, long bucket) throws IOException {
                assert bucket == 0;
                values.setDocument(doc);
                final int valuesCount = values.count();

                // SortedBinaryDocValues don't guarantee uniqueness so we need to take care of dups
                previous.clear();
                for (int i = 0; i < valuesCount; ++i) {
                    final BytesRef bytes = values.valueAt(i);
                    if (includeExclude != null && !includeExclude.accept(bytes)) {
                        continue;
                    }
                    if (previous.get().equals(bytes)) {
                        continue;
                    }
                    long bucketOrdinal = bucketOrds.add(bytes);
                    if (bucketOrdinal < 0) { // already seen
                        bucketOrdinal = - 1 - bucketOrdinal;
                        collectExistingBucket(sub, doc, bucketOrdinal);
                    } else {
                        collectBucket(sub, doc, bucketOrdinal);
                    }
                    previous.copyBytes(bytes);
                }
            }
        };
    }

    @Override
    public InternalAggregation buildAggregation(long owningBucketOrdinal) throws IOException {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    public void doClose() {
        Releasables.close(bucketOrds);
    }

}

