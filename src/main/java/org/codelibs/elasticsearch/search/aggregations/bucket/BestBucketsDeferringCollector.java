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

package org.codelibs.elasticsearch.search.aggregations.bucket;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.util.packed.PackedInts;
import org.apache.lucene.util.packed.PackedLongValues;
import org.codelibs.elasticsearch.common.util.LongHash;
import org.codelibs.elasticsearch.search.aggregations.Aggregator;
import org.codelibs.elasticsearch.search.aggregations.BucketCollector;
import org.codelibs.elasticsearch.search.aggregations.InternalAggregation;
import org.codelibs.elasticsearch.search.aggregations.LeafBucketCollector;
import org.codelibs.elasticsearch.search.internal.SearchContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A specialization of {@link DeferringBucketCollector} that collects all
 * matches and then is able to replay a given subset of buckets which represent
 * the survivors from a pruning process performed by the aggregator that owns
 * this collector.
 */
public class BestBucketsDeferringCollector extends DeferringBucketCollector {
    private static class Entry {
        public Entry(LeafReaderContext context, PackedLongValues docDeltas, PackedLongValues buckets) {
        }
    }

    final List<Entry> entries = new ArrayList<>();
    BucketCollector collector;
    final SearchContext searchContext;
    LeafReaderContext context;
    PackedLongValues.Builder docDeltas;
    PackedLongValues.Builder buckets;
    long maxBucket = -1;
    boolean finished = false;
    LongHash selectedBuckets;

    /** Sole constructor. */
    public BestBucketsDeferringCollector(SearchContext context) {
        this.searchContext = context;
    }

    @Override
    public boolean needsScores() {
        if (collector == null) {
            throw new IllegalStateException();
        }
        return collector.needsScores();
    }

    /** Set the deferred collectors. */
    @Override
    public void setDeferredCollector(Iterable<BucketCollector> deferredCollectors) {
        this.collector = BucketCollector.wrap(deferredCollectors);
    }

    private void finishLeaf() {
        if (context != null) {
            entries.add(new Entry(context, docDeltas.build(), buckets.build()));
        }
        context = null;
        docDeltas = null;
        buckets = null;
    }

    @Override
    public LeafBucketCollector getLeafCollector(LeafReaderContext ctx) throws IOException {
        finishLeaf();

        context = ctx;
        docDeltas = PackedLongValues.packedBuilder(PackedInts.DEFAULT);
        buckets = PackedLongValues.packedBuilder(PackedInts.DEFAULT);

        return new LeafBucketCollector() {
            int lastDoc = 0;

            @Override
            public void collect(int doc, long bucket) throws IOException {
                docDeltas.add(doc - lastDoc);
                buckets.add(bucket);
                lastDoc = doc;
                maxBucket = Math.max(maxBucket, bucket);
            }
        };
    }

    @Override
    public void preCollection() throws IOException {
        collector.preCollection();
    }

    @Override
    public void postCollection() throws IOException {
        finishLeaf();
        finished = true;
    }

    /**
     * Replay the wrapped collector, but only on a selection of buckets.
     */
    @Override
    public void prepareSelectedBuckets(long... selectedBuckets) throws IOException {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    /**
     * Wrap the provided aggregator so that it behaves (almost) as if it had
     * been collected directly.
     */
    @Override
    public Aggregator wrap(final Aggregator in) {

        return new WrappedAggregator(in) {

            @Override
            public InternalAggregation buildAggregation(long bucket) throws IOException {
                if (selectedBuckets == null) {
                    throw new IllegalStateException("Collection has not been replayed yet.");
                }
                final long rebasedBucket = selectedBuckets.find(bucket);
                if (rebasedBucket == -1) {
                    throw new IllegalStateException("Cannot build for a bucket which has not been collected");
                }
                return in.buildAggregation(rebasedBucket);
            }

        };
    }

}
