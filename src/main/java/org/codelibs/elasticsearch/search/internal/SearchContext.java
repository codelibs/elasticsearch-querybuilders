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
package org.codelibs.elasticsearch.search.internal;


import org.apache.lucene.search.Collector;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Counter;
import org.codelibs.elasticsearch.common.Nullable;
import org.codelibs.elasticsearch.common.lease.Releasable;
import org.codelibs.elasticsearch.common.lease.Releasables;
import org.codelibs.elasticsearch.common.unit.TimeValue;
import org.codelibs.elasticsearch.common.util.BigArrays;
import org.codelibs.elasticsearch.common.util.concurrent.AbstractRefCounted;
import org.codelibs.elasticsearch.common.util.concurrent.RefCounted;
import org.codelibs.elasticsearch.common.util.iterable.Iterables;
import org.codelibs.elasticsearch.index.mapper.MappedFieldType;
import org.codelibs.elasticsearch.index.query.ParsedQuery;
import org.codelibs.elasticsearch.index.query.QueryShardContext;
import org.codelibs.elasticsearch.search.SearchExtBuilder;
import org.codelibs.elasticsearch.search.aggregations.SearchContextAggregations;
import org.codelibs.elasticsearch.search.fetch.StoredFieldsContext;
import org.codelibs.elasticsearch.search.fetch.subphase.DocValueFieldsContext;
import org.codelibs.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.codelibs.elasticsearch.search.fetch.subphase.InnerHitsContext;
import org.codelibs.elasticsearch.search.fetch.subphase.ScriptFieldsContext;
import org.codelibs.elasticsearch.search.fetch.subphase.highlight.SearchContextHighlight;
import org.codelibs.elasticsearch.search.rescore.RescoreSearchContext;
import org.codelibs.elasticsearch.search.sort.SortAndFormats;
import org.codelibs.elasticsearch.search.suggest.SuggestionSearchContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class encapsulates the state needed to execute a search. It holds a reference to the
 * shards point in time snapshot (IndexReader / ContextIndexSearcher) and allows passing on
 * state from one query / fetch phase to another.
 *
 * This class also implements {@link RefCounted} since in some situations like in org.codelibs.elasticsearch.search.SearchService
 * a SearchContext can be closed concurrently due to independent events ie. when an index gets removed. To prevent accessing closed
 * IndexReader / IndexSearcher instances the SearchContext can be guarded by a reference count and fail if it's been closed by
 * an external event.
 */
// For reference why we use RefCounted here see #20095
public abstract class SearchContext extends AbstractRefCounted implements Releasable {

    public static final int DEFAULT_TERMINATE_AFTER = 0;
    private Map<Lifetime, List<Releasable>> clearables = null;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private InnerHitsContext innerHitsContext;

    protected SearchContext() {
        super("search_context");
    }

    public abstract boolean isCancelled();

    @Override
    public final void close() {
        if (closed.compareAndSet(false, true)) { // prevent double closing
            decRef();
        }
    }

    @Override
    protected final void closeInternal() {
        try {
            clearReleasables(Lifetime.CONTEXT);
        } finally {
            doClose();
        }
    }

    @Override
    protected void alreadyClosed() {
        throw new IllegalStateException("search context is already closed can't increment refCount current count [" + refCount() + "]");
    }

    protected abstract void doClose();

    /**
     * Should be called before executing the main query and after all other parameters have been set.
     * @param rewrite if the set query should be rewritten against the searcher returned from searcher()
     */
    public abstract void preProcess(boolean rewrite);

    public abstract Query searchFilter(String[] types);

    public abstract long id();

    public abstract String source();

    public abstract int numberOfShards();

    public abstract float queryBoost();

    public abstract long getOriginNanoTime();

    public abstract SearchContextAggregations aggregations();

    public abstract SearchContext aggregations(SearchContextAggregations aggregations);

    public abstract void addSearchExt(SearchExtBuilder searchExtBuilder);

    public abstract SearchExtBuilder getSearchExt(String name);

    public abstract SearchContextHighlight highlight();

    public abstract void highlight(SearchContextHighlight highlight);

    public InnerHitsContext innerHits() {
        if (innerHitsContext == null) {
            innerHitsContext = new InnerHitsContext();
        }
        return innerHitsContext;
    }

    public abstract SuggestionSearchContext suggest();

    public abstract void suggest(SuggestionSearchContext suggest);

    /**
     * @return list of all rescore contexts.  empty if there aren't any.
     */
    public abstract List<RescoreSearchContext> rescore();

    public abstract void addRescore(RescoreSearchContext rescore);

    public abstract boolean hasScriptFields();

    public abstract ScriptFieldsContext scriptFields();

    /**
     * A shortcut function to see whether there is a fetchSourceContext and it says the source is requested.
     */
    public abstract boolean sourceRequested();

    public abstract boolean hasFetchSourceContext();

    public abstract FetchSourceContext fetchSourceContext();

    public abstract SearchContext fetchSourceContext(FetchSourceContext fetchSourceContext);

    public abstract DocValueFieldsContext docValueFieldsContext();

    public abstract SearchContext docValueFieldsContext(DocValueFieldsContext docValueFieldsContext);

    public abstract BigArrays bigArrays();

    public abstract TimeValue timeout();

    public abstract void timeout(TimeValue timeout);

    public abstract int terminateAfter();

    public abstract void terminateAfter(int terminateAfter);

    /**
     * Indicates if the current index should perform frequent low level search cancellation check.
     *
     * Enabling low-level checks will make long running searches to react to the cancellation request faster. However,
     * since it will produce more cancellation checks it might slow the search performance down.
     */
    public abstract boolean lowLevelCancellation();

    public abstract SearchContext minimumScore(float minimumScore);

    public abstract Float minimumScore();

    public abstract SearchContext sort(SortAndFormats sort);

    public abstract SortAndFormats sort();

    public abstract SearchContext trackScores(boolean trackScores);

    public abstract boolean trackScores();

    public abstract SearchContext searchAfter(FieldDoc searchAfter);

    public abstract FieldDoc searchAfter();

    public abstract SearchContext parsedPostFilter(ParsedQuery postFilter);

    public abstract ParsedQuery parsedPostFilter();

    public abstract Query aliasFilter();

    public abstract SearchContext parsedQuery(ParsedQuery query);

    public abstract ParsedQuery parsedQuery();

    /**
     * The query to execute, might be rewritten.
     */
    public abstract Query query();

    public abstract int from();

    public abstract SearchContext from(int from);

    public abstract int size();

    public abstract SearchContext size(int size);

    public abstract boolean hasStoredFields();

    public abstract boolean hasStoredFieldsContext();

    /**
     * A shortcut function to see whether there is a storedFieldsContext and it says the fields are requested.
     */
    public abstract boolean storedFieldsRequested();

    public abstract StoredFieldsContext storedFieldsContext();

    public abstract SearchContext storedFieldsContext(StoredFieldsContext storedFieldsContext);

    public abstract boolean explain();

    public abstract void explain(boolean explain);

    @Nullable
    public abstract List<String> groupStats();

    public abstract void groupStats(List<String> groupStats);

    public abstract boolean version();

    public abstract void version(boolean version);

    public abstract int[] docIdsToLoad();

    public abstract int docIdsToLoadFrom();

    public abstract int docIdsToLoadSize();

    public abstract SearchContext docIdsToLoad(int[] docIdsToLoad, int docsIdsToLoadFrom, int docsIdsToLoadSize);

    public abstract void accessed(long accessTime);

    public abstract long lastAccessTime();

    public abstract long keepAlive();

    public abstract void keepAlive(long keepAlive);

    /**
     * Schedule the release of a resource. The time when {@link Releasable#close()} will be called on this object
     * is function of the provided {@link Lifetime}.
     */
    public void addReleasable(Releasable releasable, Lifetime lifetime) {
        if (clearables == null) {
            clearables = new HashMap<>();
        }
        List<Releasable> releasables = clearables.get(lifetime);
        if (releasables == null) {
            releasables = new ArrayList<>();
            clearables.put(lifetime, releasables);
        }
        releasables.add(releasable);
    }

    public void clearReleasables(Lifetime lifetime) {
        if (clearables != null) {
            List<List<Releasable>>releasables = new ArrayList<>();
            for (Lifetime lc : Lifetime.values()) {
                if (lc.compareTo(lifetime) > 0) {
                    break;
                }
                List<Releasable> remove = clearables.remove(lc);
                if (remove != null) {
                    releasables.add(remove);
                }
            }
            Releasables.close(Iterables.flatten(releasables));
        }
    }

    /**
     * @return true if the request contains only suggest
     */
    public final boolean hasOnlySuggest() {
        throw new UnsupportedOperationException();
    }

    /**
     * Looks up the given field, but does not restrict to fields in the types set on this context.
     */
    public abstract MappedFieldType smartNameFieldType(String name);

    public abstract Counter timeEstimateCounter();

    /** Return a view of the additional query collectors that should be run for this context. */
    public abstract Map<Class<?>, Collector> queryCollectors();

    /**
     * The life time of an object that is used during search execution.
     */
    public enum Lifetime {
        /**
         * This life time is for objects that only live during collection time.
         */
        COLLECTION,
        /**
         * This life time is for objects that need to live until the end of the current search phase.
         */
        PHASE,
        /**
         * This life time is for objects that need to live until the search context they are attached to is destroyed.
         */
        CONTEXT
    }

    public abstract QueryShardContext getQueryShardContext();

    @Override
    public String toString() {
        throw new UnsupportedOperationException();
    }
}