package org.codelibs.elasticsearch.querybuilders;

import org.codelibs.elasticsearch.ElasticsearchException;
import org.codelibs.elasticsearch.common.Nullable;
import org.codelibs.elasticsearch.common.unit.TimeValue;
import org.codelibs.elasticsearch.common.xcontent.XContentHelper;
import org.codelibs.elasticsearch.common.xcontent.yaml.YamlXContent;
import org.codelibs.elasticsearch.index.query.QueryBuilder;
import org.codelibs.elasticsearch.script.Script;
import org.codelibs.elasticsearch.search.SearchExtBuilder;
import org.codelibs.elasticsearch.search.aggregations.AggregationBuilder;
import org.codelibs.elasticsearch.search.aggregations.PipelineAggregationBuilder;
import org.codelibs.elasticsearch.search.builder.SearchSourceBuilder;
import org.codelibs.elasticsearch.search.fetch.StoredFieldsContext;
import org.codelibs.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.codelibs.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.codelibs.elasticsearch.search.rescore.RescoreBuilder;
import org.codelibs.elasticsearch.search.slice.SliceBuilder;
import org.codelibs.elasticsearch.search.sort.SortBuilder;
import org.codelibs.elasticsearch.search.sort.SortOrder;
import org.codelibs.elasticsearch.search.suggest.SuggestBuilder;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

public class SearchDslBuilder {
    public SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

    private SearchDslBuilder() {
    }

    public SearchDslBuilder query(Supplier<QueryBuilder> query) {
        return query(query.get());
    }

    public SearchDslBuilder query(QueryBuilder query) {
        searchSourceBuilder.query(query);
        return this;
    }

    public SearchDslBuilder postFilter(Supplier<QueryBuilder> postFilter) {
        return postFilter(postFilter.get());
    }

    public SearchDslBuilder postFilter(QueryBuilder postFilter) {
        searchSourceBuilder.postFilter(postFilter);
        return this;
    }

    public SearchDslBuilder from(int from) {
        searchSourceBuilder.from(from);
        return this;
    }

    public SearchDslBuilder size(int size) {
        searchSourceBuilder.size(size);
        return this;
    }

    public SearchDslBuilder minScore(float minScore) {
        searchSourceBuilder.minScore(minScore);
        return this;
    }

    public SearchDslBuilder explain(Boolean explain) {
        searchSourceBuilder.explain(explain);
        return this;
    }

    public SearchDslBuilder timeout(TimeValue timeout) {
        searchSourceBuilder.timeout(timeout);
        return this;
    }

    public SearchDslBuilder terminateAfter(int terminateAfter) {
        searchSourceBuilder.terminateAfter(terminateAfter);
        return this;
    }

    public SearchDslBuilder sort(String name, SortOrder order) {
        searchSourceBuilder.sort(name, order);
        return this;
    }

    public SearchDslBuilder sort(String name) {
        searchSourceBuilder.sort(name);
        return this;
    }

    public SearchDslBuilder sort(SortBuilder<?> sort) {
        searchSourceBuilder.sort(sort);
        return this;
    }

    public SearchDslBuilder trackScores(boolean trackScores) {
        searchSourceBuilder.trackScores(trackScores);
        return this;
    }

    public SearchDslBuilder searchAfter(Object[] values) {
        searchSourceBuilder.searchAfter(values);
        return this;
    }

    public SearchDslBuilder slice(SliceBuilder builder) {
        searchSourceBuilder.slice(builder);
        return this;
    }

    public SearchDslBuilder aggregation(Supplier<AggregationBuilder> aggregation) {
        return aggregation(aggregation.get());
    }

    public SearchDslBuilder aggregation(AggregationBuilder aggregation) {
        searchSourceBuilder.aggregation(aggregation);
        return this;
    }

    public SearchDslBuilder aggregation(PipelineAggregationBuilder aggregation) {
        searchSourceBuilder.aggregation(aggregation);
        return this;
    }

    public SearchDslBuilder highlighter(Supplier<HighlightBuilder> highlightBuilder) {
        return highlighter(highlightBuilder.get());
    }

    public SearchDslBuilder highlighter(HighlightBuilder highlightBuilder) {
        searchSourceBuilder.highlighter(highlightBuilder);
        return this;
    }

    public SearchDslBuilder suggest(Supplier<SuggestBuilder> suggestBuilder) {
        suggest(suggestBuilder.get());
        return this;
    }

    public SearchDslBuilder suggest(SuggestBuilder suggestBuilder) {
        searchSourceBuilder.suggest(suggestBuilder);
        return this;
    }

    public SearchDslBuilder addRescorer(Supplier<RescoreBuilder<?>> rescoreBuilder) {
        addRescorer(rescoreBuilder.get());
        return this;
    }

    public SearchDslBuilder addRescorer(RescoreBuilder<?> rescoreBuilder) {
        searchSourceBuilder.addRescorer(rescoreBuilder);
        return this;
    }

    public SearchDslBuilder clearRescorers() {
        searchSourceBuilder.clearRescorers();
        return this;
    }

    public SearchDslBuilder profile(boolean profile) {
        searchSourceBuilder.profile(profile);
        return this;
    }

    public SearchDslBuilder fetchSource(boolean fetch) {
        searchSourceBuilder.fetchSource(fetch);
        return this;
    }

    public SearchDslBuilder fetchSource(@Nullable String include, @Nullable String exclude) {
        searchSourceBuilder.fetchSource(include, exclude);
        return this;
    }

    public SearchDslBuilder fetchSource(@Nullable String[] includes, @Nullable String[] excludes) {
        searchSourceBuilder.fetchSource(includes, excludes);
        return this;
    }

    public SearchDslBuilder fetchSource(@Nullable FetchSourceContext fetchSourceContext) {
        searchSourceBuilder.fetchSource(fetchSourceContext);
        return this;
    }

    public SearchDslBuilder storedField(String name) {
        searchSourceBuilder.storedField(name);
        return this;
    }

    public SearchDslBuilder storedFields(List<String> fields) {
        searchSourceBuilder.storedFields(fields);
        return this;
    }

    public SearchDslBuilder storedFields(StoredFieldsContext context) {
        searchSourceBuilder.storedFields(context);
        return this;
    }

    public SearchDslBuilder docValueField(String name) {
        searchSourceBuilder.docValueField(name);
        return this;
    }

    public SearchDslBuilder scriptField(String name, Script script) {
        searchSourceBuilder.scriptField(name, script);
        return this;
    }

    public SearchDslBuilder scriptField(String name, Script script, boolean ignoreFailure) {
        searchSourceBuilder.scriptField(name, script, ignoreFailure);
        return this;
    }

    public SearchDslBuilder indexBoost(String index, float indexBoost) {
        searchSourceBuilder.indexBoost(index, indexBoost);
        return this;
    }

    public SearchDslBuilder stats(List<String> statsGroups) {
        searchSourceBuilder.stats(statsGroups);
        return this;
    }

    public SearchDslBuilder ext(List<SearchExtBuilder> searchExtBuilders) {
        searchSourceBuilder.ext(searchExtBuilders);
        return this;
    }


    public String build() {
        try {
            return XContentHelper.convertToJson(searchSourceBuilder.buildAsBytes(), true);
        } catch (IOException e) {
            throw new ElasticsearchException("Failed to build source.", e);
        }
    }

    public static SearchDslBuilder builder() {
        return new SearchDslBuilder();
    }
}
