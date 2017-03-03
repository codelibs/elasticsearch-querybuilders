package org.codelibs.elasticsearch.search.aggregations;

import org.codelibs.elasticsearch.common.geo.GeoPoint;
import org.codelibs.elasticsearch.common.xcontent.XContentHelper;
import org.codelibs.elasticsearch.index.query.QueryBuilders;
import org.codelibs.elasticsearch.script.Script;
import org.codelibs.elasticsearch.search.aggregations.bucket.filters.FiltersAggregator;
import org.codelibs.elasticsearch.search.builder.SearchSourceBuilder;
import org.codelibs.elasticsearch.search.sort.SortOrder;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class AggregationBuildersTest {
    @Test
    public void test_childrenAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.children("children", "child")
            .subAggregation(AggregationBuilders.terms("genre").field("genre"));
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"children\":{\"children\":{\"type\":\"child\"},\"aggregations\":{\"genre\":{\"terms\":{\"field\":\"genre\",\"size\":10,\"min_doc_count\":1,\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":[{\"_count\":\"desc\"},{\"_term\":\"asc\"}]}}}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_dateHistgramAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.dateHistogram("dateHistogram")
            .field("date")
            .interval(10000);
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"dateHistogram\":{\"date_histogram\":{\"field\":\"date\",\"interval\":10000,\"offset\":0,\"order\":{\"_key\":\"asc\"},\"keyed\":false,\"min_doc_count\":0}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_dateRangeAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.dateRange("range")
            .field("date")
            .format("MM-yyy")
            .addRange("now-10M/M", "now-10M/M");
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"range\":{\"date_range\":{\"field\":\"date\",\"format\":\"MM-yyy\",\"ranges\":[{\"from\":\"now-10M/M\",\"to\":\"now-10M/M\"}],\"keyed\":false}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_diversifiedSamplerAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.diversifiedSampler("sample")
            .shardSize(1000)
            .field("user.id")
            .subAggregation(AggregationBuilders.terms("name").field("text"));
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"sample\":{\"diversified_sampler\":{\"field\":\"user.id\",\"shard_size\":1000,\"max_docs_per_value\":1},\"aggregations\":{\"name\":{\"terms\":{\"field\":\"text\",\"size\":10,\"min_doc_count\":1,\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":[{\"_count\":\"desc\"},{\"_term\":\"asc\"}]}}}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_filterAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.filter("red_products", QueryBuilders.termQuery("color", "red"))
            .subAggregation(AggregationBuilders.terms("price").field("price"));
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"red_products\":{\"filter\":{\"term\":{\"color\":{\"value\":\"red\",\"boost\":1.0}}},\"aggregations\":{\"price\":{\"terms\":{\"field\":\"price\",\"size\":10,\"min_doc_count\":1,\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":[{\"_count\":\"desc\"},{\"_term\":\"asc\"}]}}}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_filtersAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.filters("messages", new FiltersAggregator.KeyedFilter("errors", QueryBuilders.termQuery("body", "error")), new FiltersAggregator.KeyedFilter("warnings", QueryBuilders.termQuery("body", "warning")));
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"messages\":{\"filters\":{\"filters\":{\"errors\":{\"term\":{\"body\":{\"value\":\"error\",\"boost\":1.0}}},\"warnings\":{\"term\":{\"body\":{\"value\":\"warning\",\"boost\":1.0}}}},\"other_bucket\":false,\"other_bucket_key\":\"_other_\"}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_geoDistanceAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.geoDistance("rings_around_amsterdam", new GeoPoint(52.3760f, 4.894f))
            .field("location")
            .addRange("range1", 0, 100)
            .addRange("range2", 100, 300);
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"rings_around_amsterdam\":{\"geo_distance\":{\"field\":\"location\",\"origin\":{\"lat\":52.375999450683594,\"lon\":4.894000053405762},\"ranges\":[{\"key\":\"range1\",\"from\":0.0,\"to\":100.0},{\"key\":\"range2\",\"from\":100.0,\"to\":300.0}],\"keyed\":false,\"unit\":\"m\",\"distance_type\":\"SLOPPY_ARC\"}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_getHashGridAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.geohashGrid("myLarge-GrainGeoHashGrid")
            .field("location")
            .precision(3);
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"myLarge-GrainGeoHashGrid\":{\"geohash_grid\":{\"field\":\"location\",\"precision\":3,\"size\":10000}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_globalAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.global("all_products")
            .subAggregation(AggregationBuilders.avg("avg_price").field("price"));
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"all_products\":{\"global\":{},\"aggregations\":{\"avg_price\":{\"avg\":{\"field\":\"price\"}}}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_histogramAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.histogram("prices")
            .field("price")
            .interval(50);
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"prices\":{\"histogram\":{\"field\":\"price\",\"interval\":50.0,\"offset\":0.0,\"order\":{\"_key\":\"asc\"},\"keyed\":false,\"min_doc_count\":0}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_ipRangeAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.ipRange("ip_ranges")
            .field("ip")
            .addRange("10.0.0.5", "10.0.0.10");
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"ip_ranges\":{\"ip_range\":{\"field\":\"ip\",\"ranges\":[{\"from\":\"10.0.0.5\",\"to\":\"10.0.0.10\"}],\"keyed\":false}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_missingAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.missing("products_without_a_price")
            .field("price");
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"products_without_a_price\":{\"missing\":{\"field\":\"price\"}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_nestedAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.nested("resellers", "resellers")
            .subAggregation(AggregationBuilders.avg("avg_price").field("resellers.price"));
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"resellers\":{\"nested\":{\"path\":\"resellers\"},\"aggregations\":{\"avg_price\":{\"avg\":{\"field\":\"resellers.price\"}}}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_rangeAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.range("price_ranges")
            .field("price")
            .addRange(0, 50)
            .addRange(50, 100);
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"price_ranges\":{\"range\":{\"field\":\"price\",\"ranges\":[{\"from\":0.0,\"to\":50.0},{\"from\":50.0,\"to\":100.0}],\"keyed\":false}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_reverseNestedAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.nested("comments", "comments")
            .subAggregation(AggregationBuilders.terms("top_usernames").field("comments.user")
                .subAggregation(AggregationBuilders.reverseNested("comment_to_issue").subAggregation(AggregationBuilders.terms("top_tags_per_comment").field("tags"))));
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"comments\":{\"nested\":{\"path\":\"comments\"},\"aggregations\":{\"top_usernames\":{\"terms\":{\"field\":\"comments.user\",\"size\":10,\"min_doc_count\":1,\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":[{\"_count\":\"desc\"},{\"_term\":\"asc\"}]},\"aggregations\":{\"comment_to_issue\":{\"reverse_nested\":{},\"aggregations\":{\"top_tags_per_comment\":{\"terms\":{\"field\":\"tags\",\"size\":10,\"min_doc_count\":1,\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":[{\"_count\":\"desc\"},{\"_term\":\"asc\"}]}}}}}}}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_samplerAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.sampler("sample")
            .shardSize(100)
            .subAggregation(AggregationBuilders.terms("name").field("text"));
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"sample\":{\"sampler\":{\"shard_size\":100},\"aggregations\":{\"name\":{\"terms\":{\"field\":\"text\",\"size\":10,\"min_doc_count\":1,\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":[{\"_count\":\"desc\"},{\"_term\":\"asc\"}]}}}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_significantTermsAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.significantTerms("significantCrimeTypes")
            .field("crime_type");
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"significantCrimeTypes\":{\"significant_terms\":{\"field\":\"crime_type\",\"size\":10,\"min_doc_count\":3,\"shard_min_doc_count\":0,\"jlh\":{}}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_termsAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms("terms").field("field1");
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"terms\":{\"terms\":{\"field\":\"field1\",\"size\":10,\"min_doc_count\":1,\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":[{\"_count\":\"desc\"},{\"_term\":\"asc\"}]}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_avgAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.avg("avg_grade").field("grade");
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"avg_grade\":{\"avg\":{\"field\":\"grade\"}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_cardinaityAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.cardinality("author_count").field("author");
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"author_count\":{\"cardinality\":{\"field\":\"author\"}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_extendedStatsAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.extendedStats("grades_stats").field("grade");
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"grades_stats\":{\"extended_stats\":{\"field\":\"grade\",\"sigma\":2.0}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_geoBoundsAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.geoBounds("viewport").field("location").wrapLongitude(true);
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"viewport\":{\"geo_bounds\":{\"field\":\"location\",\"wrap_longitude\":true}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_geoCentroidAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.geoCentroid("centroid").field("location");
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"centroid\":{\"geo_centroid\":{\"field\":\"location\"}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_maxAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.max("max_price").field("price");
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"max_price\":{\"max\":{\"field\":\"price\"}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_minAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.min("min_price").field("price");
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"min_price\":{\"min\":{\"field\":\"price\"}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_percentileAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.percentiles("load_time_outlier").field("load_time");
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"load_time_outlier\":{\"percentiles\":{\"field\":\"load_time\",\"percents\":[1.0,5.0,25.0,50.0,75.0,95.0,99.0],\"keyed\":true,\"tdigest\":{\"compression\":100.0}}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_percentileRanksAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.percentileRanks("load_time_outlier").field("load_time").values(15, 30);
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"load_time_outlier\":{\"percentile_ranks\":{\"field\":\"load_time\",\"values\":[15.0,30.0],\"keyed\":true,\"tdigest\":{\"compression\":100.0}}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_ScriptedMetricAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.scriptedMetric("profit")
            .initScript(new Script("params._agg.transactions = []"))
            .mapScript(new Script("params._agg.transactions.add(doc.type.value == 'sale' ? doc.amount.value : -1 * doc.amount.value)"))
            .combineScript(new Script("double profit = 0; for (t in params._agg.transactions) { profit += t } return profit"))
            .reduceScript(new Script("double profit = 0; for (a in params._aggs) { profit += a } return profit"));
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"profit\":{\"scripted_metric\":{\"init_script\":{\"inline\":\"params._agg.transactions = []\",\"lang\":\"painless\"},\"map_script\":{\"inline\":\"params._agg.transactions.add(doc.type.value == 'sale' ? doc.amount.value : -1 * doc.amount.value)\",\"lang\":\"painless\"},\"combine_script\":{\"inline\":\"double profit = 0; for (t in params._agg.transactions) { profit += t } return profit\",\"lang\":\"painless\"},\"reduce_script\":{\"inline\":\"double profit = 0; for (a in params._aggs) { profit += a } return profit\",\"lang\":\"painless\"}}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_statsAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.stats("grades_stats").field("grade");
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"grades_stats\":{\"stats\":{\"field\":\"grade\"}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_sumAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.sum("intraday_return").field("change");
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"intraday_return\":{\"sum\":{\"field\":\"change\"}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_topHitsAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.terms("top-tags").field("tags").size(3)
            .subAggregation(AggregationBuilders.topHits("top_tag_hits").sort("last_activity_date", SortOrder.DESC).fetchSource(new String[]{"title"}, new String[]{}).size(1));
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"top-tags\":{\"terms\":{\"field\":\"tags\",\"size\":3,\"min_doc_count\":1,\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":[{\"_count\":\"desc\"},{\"_term\":\"asc\"}]},\"aggregations\":{\"top_tag_hits\":{\"top_hits\":{\"from\":0,\"size\":1,\"version\":false,\"explain\":false,\"_source\":{\"includes\":[\"title\"],\"excludes\":[]},\"sort\":[{\"last_activity_date\":{\"order\":\"desc\"}}]}}}}}}", createSource(aggregationBuilder));
    }

    @Test
    public void test_valueCountAggregation() throws Exception {
        AggregationBuilder aggregationBuilder = AggregationBuilders.count("grades_count").field("grade");
        assertEquals("{\"query\":{\"match_all\":{\"boost\":1.0}},\"aggregations\":{\"grades_count\":{\"value_count\":{\"field\":\"grade\"}}}}", createSource(aggregationBuilder));
    }


    private String createSource(AggregationBuilder aggregationBuilder) throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.aggregation(aggregationBuilder);
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        System.out.println(XContentHelper.convertToJson(searchSourceBuilder.buildAsBytes(), false));
        return XContentHelper.convertToJson(searchSourceBuilder.buildAsBytes(), false);
    }
}
