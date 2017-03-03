package org.codelibs.elasticsearch.index.query;

import org.apache.lucene.search.join.ScoreMode;
import org.codelibs.elasticsearch.common.geo.GeoPoint;
import org.codelibs.elasticsearch.common.geo.builders.ShapeBuilder;
import org.codelibs.elasticsearch.common.geo.builders.ShapeBuilders;
import org.codelibs.elasticsearch.common.lucene.search.function.FieldValueFactorFunction;
import org.codelibs.elasticsearch.common.unit.DistanceUnit;
import org.codelibs.elasticsearch.common.unit.Fuzziness;
import org.codelibs.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.codelibs.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.codelibs.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.codelibs.elasticsearch.index.search.MatchQuery;
import org.codelibs.elasticsearch.script.Script;
import org.codelibs.elasticsearch.script.ScriptType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class QueryBuildersTest {
    @Test
    public void test_matchAllQuery() throws Exception {
        assertEquals("{\"match_all\":{\"boost\":2.0}}", toJsonDsl(QueryBuilders.matchAllQuery().boost(2.0f)));
    }

    @Test
    public void test_matchQuery() throws Exception {
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("field1", "keyword")
            .minimumShouldMatch("10%")
            .fuzziness(1)
            .operator(Operator.AND)
            .zeroTermsQuery(MatchQuery.ZeroTermsQuery.ALL)
            .cutoffFrequency(1.0f)
            .analyzer("analyzer1");
        assertEquals("{\"match\":{\"field1\":{\"query\":\"keyword\",\"operator\":\"AND\",\"analyzer\":\"analyzer1\",\"fuzziness\":\"1\",\"prefix_length\":0,\"max_expansions\":50,\"minimum_should_match\":\"10%\",\"fuzzy_transpositions\":true,\"lenient\":false,\"zero_terms_query\":\"ALL\",\"cutoff_frequency\":1.0,\"boost\":1.0}}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_matchPhraseQuery() throws Exception {
        MatchPhraseQueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery("field1", "keyword1 keyword2")
            .analyzer("analyzer1");
        assertEquals("{\"match_phrase\":{\"field1\":{\"query\":\"keyword1 keyword2\",\"analyzer\":\"analyzer1\",\"slop\":0,\"boost\":1.0}}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_matchPhrasePrefixQuery() throws Exception {
        MatchPhrasePrefixQueryBuilder queryBuilder = QueryBuilders.matchPhrasePrefixQuery("field1", "keyword1 keyword2")
            .maxExpansions(10);
        assertEquals("{\"match_phrase_prefix\":{\"field1\":{\"query\":\"keyword1 keyword2\",\"slop\":0,\"max_expansions\":10,\"boost\":1.0}}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_multiMatchQuery() throws Exception {
        MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery("keyword1", "field1", "field2");
        assertEquals("{\"multi_match\":{\"query\":\"keyword1\",\"fields\":[\"field1^1.0\",\"field2^1.0\"],\"type\":\"best_fields\",\"operator\":\"OR\",\"slop\":0,\"prefix_length\":0,\"max_expansions\":50,\"lenient\":false,\"zero_terms_query\":\"NONE\",\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_commontTermsQuery() throws Exception {
        CommonTermsQueryBuilder queryBuilder = QueryBuilders.commonTermsQuery("field1", "keyword1")
            .cutoffFrequency(0.001f)
            .disableCoord(true)
            .highFreqOperator(Operator.AND)
            .lowFreqOperator(Operator.AND)
            .highFreqMinimumShouldMatch("1")
            .lowFreqMinimumShouldMatch("2");
        assertEquals("{\"common\":{\"field1\":{\"query\":\"keyword1\",\"disable_coord\":true,\"high_freq_operator\":\"AND\",\"low_freq_operator\":\"AND\",\"cutoff_frequency\":0.001,\"minimum_should_match\":{\"low_freq\":\"2\",\"high_freq\":\"1\"},\"boost\":1.0}}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_queryStringQuery() throws Exception {
        QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery("field1:keyword1 AND field2:keyword2")
            .defaultField("field3")
            .allowLeadingWildcard(true)
            .analyzeWildcard(true)
            .autoGeneratePhraseQueries(true)
            .defaultOperator(Operator.AND)
            .enablePositionIncrements(true)
            .escape(true)
            .fuzziness(Fuzziness.ONE)
            .fuzzyMaxExpansions(1)
            .fuzzyPrefixLength(1)
            .phraseSlop(1)
            .maxDeterminizedStates(1)
            .lenient(true)
            .quoteFieldSuffix("suffix")
            .splitOnWhitespace(true);
        assertEquals("{\"query_string\":{\"query\":\"field1:keyword1 AND field2:keyword2\",\"default_field\":\"field3\",\"fields\":[],\"use_dis_max\":true,\"tie_breaker\":0.0,\"default_operator\":\"and\",\"auto_generate_phrase_queries\":true,\"max_determined_states\":1,\"allow_leading_wildcard\":true,\"enable_position_increment\":true,\"fuzziness\":\"1\",\"fuzzy_prefix_length\":1,\"fuzzy_max_expansions\":1,\"phrase_slop\":1,\"analyze_wildcard\":true,\"quote_field_suffix\":\"suffix\",\"lenient\":true,\"escape\":true,\"split_on_whitespace\":true,\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_simpleQueryStringQuery() throws Exception {
        SimpleQueryStringBuilder queryBuilder = QueryBuilders.simpleQueryStringQuery("field1:keyword1 AND field2:keyword2")
            .analyzeWildcard(true)
            .defaultOperator(Operator.AND)
            .lenient(true)
            .quoteFieldSuffix("suffix");
        assertEquals("{\"simple_query_string\":{\"query\":\"field1:keyword1 AND field2:keyword2\",\"flags\":-1,\"default_operator\":\"and\",\"lenient\":true,\"analyze_wildcard\":true,\"quote_field_suffix\":\"suffix\",\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_termQuery() throws Exception {
        TermQueryBuilder queryBuilder = QueryBuilders.termQuery("field1", "term1")
            .queryName("name");
        assertEquals("{\"term\":{\"field1\":{\"value\":\"term1\",\"boost\":1.0,\"_name\":\"name\"}}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_termsQuery() throws Exception {
        TermsQueryBuilder queryBuilder = QueryBuilders.termsQuery("field1", "term1", "term2")
            .queryName("_name");
        assertEquals("{\"terms\":{\"field1\":[\"term1\",\"term2\"],\"boost\":1.0,\"_name\":\"_name\"}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_rangeQuery() throws Exception {
        RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery("field1")
            .gte(10)
            .lte(100);
        assertEquals("{\"range\":{\"field1\":{\"from\":10,\"to\":100,\"include_lower\":true,\"include_upper\":true,\"boost\":1.0}}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_existsQuery() throws Exception {
        ExistsQueryBuilder queryBuilder = QueryBuilders.existsQuery("field1");
        assertEquals("{\"exists\":{\"field\":\"field1\",\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_prefixQuery() throws Exception {
        PrefixQueryBuilder prefixQueryBuilder = QueryBuilders.prefixQuery("field1", "key");
        assertEquals("{\"prefix\":{\"field1\":{\"value\":\"key\",\"boost\":1.0}}}", toJsonDsl(prefixQueryBuilder));
    }

    @Test
    public void test_wildcardQuery() throws Exception {
        WildcardQueryBuilder queryBuilder = QueryBuilders.wildcardQuery("field1", "k*d");
        assertEquals("{\"wildcard\":{\"field1\":{\"wildcard\":\"k*d\",\"boost\":1.0}}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_regexpQuery() throws Exception {
        RegexpQueryBuilder queryBuilder = QueryBuilders.regexpQuery("field1", "ke.*d")
            .flags(RegexpQueryBuilder.DEFAULT_FLAGS_VALUE)
            .maxDeterminizedStates(1);
        assertEquals("{\"regexp\":{\"field1\":{\"value\":\"ke.*d\",\"flags_value\":65535,\"max_determinized_states\":1,\"boost\":1.0}}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_fuzzyQuery() throws Exception {
        FuzzyQueryBuilder queryBuilder = QueryBuilders.fuzzyQuery("field1", "keyword")
            .fuzziness(Fuzziness.ONE)
            .prefixLength(2)
            .maxExpansions(2);
        assertEquals("{\"fuzzy\":{\"field1\":{\"value\":\"keyword\",\"fuzziness\":\"1\",\"prefix_length\":2,\"max_expansions\":2,\"transpositions\":false,\"boost\":1.0}}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_typeQuery() throws Exception {
        TypeQueryBuilder queryBuilder = QueryBuilders.typeQuery("type1");
        assertEquals("{\"type\":{\"value\":\"type1\",\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_idsQuery() throws Exception {
        IdsQueryBuilder queryBuilder = QueryBuilders.idsQuery("type")
            .addIds("1")
            .addIds("2");
        assertEquals("{\"ids\":{\"type\":[\"type\"],\"values\":[\"1\",\"2\"],\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_constantScoreQuery() throws Exception {
        ConstantScoreQueryBuilder queryBuilder = QueryBuilders.constantScoreQuery(QueryBuilders.matchAllQuery());
        assertEquals("{\"constant_score\":{\"filter\":{\"match_all\":{\"boost\":1.0}},\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_boolQueryBuilder() throws Exception {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
            .must(QueryBuilders.termQuery("field1", "term1"))
            .must(QueryBuilders.termQuery("field2", "term2"))
            .should(QueryBuilders.termQuery("field3", "term3"))
            .mustNot(QueryBuilders.termQuery("field4", "term4"))
            .minimumShouldMatch(1);
        assertEquals("{\"bool\":{\"must\":[{\"term\":{\"field1\":{\"value\":\"term1\",\"boost\":1.0}}},{\"term\":{\"field2\":{\"value\":\"term2\",\"boost\":1.0}}}],\"must_not\":[{\"term\":{\"field4\":{\"value\":\"term4\",\"boost\":1.0}}}],\"should\":[{\"term\":{\"field3\":{\"value\":\"term3\",\"boost\":1.0}}}],\"disable_coord\":false,\"adjust_pure_negative\":true,\"minimum_should_match\":\"1\",\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_disMaxQuery() throws Exception {
        DisMaxQueryBuilder queryBuilder = QueryBuilders.disMaxQuery()
            .add(QueryBuilders.termQuery("field1", "term1"))
            .add(QueryBuilders.termQuery("field2", "term2"))
            .tieBreaker(0.7f);
        assertEquals("{\"dis_max\":{\"tie_breaker\":0.7,\"queries\":[{\"term\":{\"field1\":{\"value\":\"term1\",\"boost\":1.0}}},{\"term\":{\"field2\":{\"value\":\"term2\",\"boost\":1.0}}}],\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_functionScoreQuery() throws Exception {
        ScoreFunctionBuilder scoreFunctionBuilder = ScoreFunctionBuilders.fieldValueFactorFunction("field1").missing(1).factor(1).modifier(FieldValueFactorFunction.Modifier.LN);
        FunctionScoreQueryBuilder queryBuilder = QueryBuilders.functionScoreQuery(QueryBuilders.matchAllQuery(), scoreFunctionBuilder);
        assertEquals("{\"function_score\":{\"query\":{\"match_all\":{\"boost\":1.0}},\"functions\":[{\"filter\":{\"match_all\":{\"boost\":1.0}},\"field_value_factor\":{\"field\":\"field1\",\"factor\":1.0,\"missing\":1.0,\"modifier\":\"ln\"}}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}", toJsonDsl(queryBuilder));

        FunctionScoreQueryBuilder.FilterFunctionBuilder[] filterFunctionBuilders = new FunctionScoreQueryBuilder.FilterFunctionBuilder[2];
        filterFunctionBuilders[0] = new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.termQuery("field1", "term1"), ScoreFunctionBuilders.weightFactorFunction(1));
        filterFunctionBuilders[1] = new FunctionScoreQueryBuilder.FilterFunctionBuilder(QueryBuilders.termQuery("field2", "term2"), ScoreFunctionBuilders.weightFactorFunction(2));
        queryBuilder = QueryBuilders.functionScoreQuery(filterFunctionBuilders);
        assertEquals("{\"function_score\":{\"query\":{\"match_all\":{\"boost\":1.0}},\"functions\":[{\"filter\":{\"term\":{\"field1\":{\"value\":\"term1\",\"boost\":1.0}}},\"weight\":1.0},{\"filter\":{\"term\":{\"field2\":{\"value\":\"term2\",\"boost\":1.0}}},\"weight\":2.0}],\"score_mode\":\"multiply\",\"max_boost\":3.4028235E38,\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_boostingQuery() throws Exception {
        BoostingQueryBuilder queryBuilder = QueryBuilders.boostingQuery(QueryBuilders.termQuery("field1", "term1"), QueryBuilders.termQuery("field2", "term2")).negativeBoost(0.2f);
        assertEquals("{\"boosting\":{\"positive\":{\"term\":{\"field1\":{\"value\":\"term1\",\"boost\":1.0}}},\"negative\":{\"term\":{\"field2\":{\"value\":\"term2\",\"boost\":1.0}}},\"negative_boost\":0.2,\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_nestedQuery() throws Exception {
        NestedQueryBuilder queryBuilder = QueryBuilders.nestedQuery("obj1", QueryBuilders.matchAllQuery(), ScoreMode.Avg);
        assertEquals("{\"nested\":{\"query\":{\"match_all\":{\"boost\":1.0}},\"path\":\"obj1\",\"ignore_unmapped\":false,\"score_mode\":\"avg\",\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_hasChildQuery() throws Exception {
        HasChildQueryBuilder queryBuilder = QueryBuilders.hasChildQuery("child", QueryBuilders.matchAllQuery(), ScoreMode.Avg);
        assertEquals("{\"has_child\":{\"query\":{\"match_all\":{\"boost\":1.0}},\"type\":\"child\",\"score_mode\":\"avg\",\"min_children\":0,\"max_children\":2147483647,\"ignore_unmapped\":false,\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_hasParentQuery() throws Exception {
        HasParentQueryBuilder queryBuilder = QueryBuilders.hasParentQuery("parent", QueryBuilders.matchAllQuery(), true);
        assertEquals("{\"has_parent\":{\"query\":{\"match_all\":{\"boost\":1.0}},\"parent_type\":\"parent\",\"score\":true,\"ignore_unmapped\":false,\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_geoShapeQuery() throws Exception {
        ShapeBuilder shapeBuilder = ShapeBuilders.newCircleBuilder().center(1, 1);
        GeoShapeQueryBuilder queryBuilder = QueryBuilders.geoShapeQuery("location", shapeBuilder);
        assertEquals("{\"geo_shape\":{\"location\":{\"shape\":{\"type\":\"circle\",\"radius\":\"0.0m\",\"coordinates\":[1.0,1.0]},\"relation\":\"intersects\"},\"ignore_unmapped\":false,\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_geoBoudingBoxQuery() throws Exception {
        GeoBoundingBoxQueryBuilder queryBuilder = QueryBuilders.geoBoundingBoxQuery("location").setCorners(1, 1, 0, 0);
        assertEquals("{\"geo_bounding_box\":{\"location\":{\"top_left\":[1.0,1.0],\"bottom_right\":[0.0,0.0]},\"validation_method\":\"STRICT\",\"type\":\"MEMORY\",\"ignore_unmapped\":false,\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_geoDistanceQuery() throws Exception {
        GeoDistanceQueryBuilder queryBuilder = QueryBuilders.geoDistanceQuery("location").distance(1, DistanceUnit.KILOMETERS).point(1, 1);
        assertEquals("{\"geo_distance\":{\"location\":[1.0,1.0],\"distance\":1000.0,\"distance_type\":\"sloppy_arc\",\"validation_method\":\"STRICT\",\"ignore_unmapped\":false,\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_geoPolygonQuery() throws Exception {
        List<GeoPoint> geoPoints = new ArrayList<>();
        geoPoints.add(new GeoPoint().resetLat(1).resetLon(1));
        geoPoints.add(new GeoPoint().resetLat(2).resetLon(2));
        geoPoints.add(new GeoPoint().resetLat(3).resetLon(3));
        GeoPolygonQueryBuilder queryBuilder = QueryBuilders.geoPolygonQuery("location", geoPoints);
        assertEquals("{\"geo_polygon\":{\"location\":{\"points\":[[1.0,1.0],[2.0,2.0],[3.0,3.0],[1.0,1.0]]},\"validation_method\":\"STRICT\",\"ignore_unmapped\":false,\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_moreLikeThisQuery() throws Exception {
        String[] fields = new String[]{"field1"};
        MoreLikeThisQueryBuilder.Item[] items = new MoreLikeThisQueryBuilder.Item[]{new MoreLikeThisQueryBuilder.Item("index", "type", "1")};
        String[] texts = new String[]{"texts"};
        MoreLikeThisQueryBuilder queryBuilder = QueryBuilders.moreLikeThisQuery(fields, texts, items).maxQueryTerms(3).minTermFreq(2);
        assertEquals("{\"more_like_this\":{\"fields\":[\"field1\"],\"like\":[\"texts\",{\"_index\":\"index\",\"_type\":\"type\",\"_id\":\"1\"}],\"max_query_terms\":3,\"min_term_freq\":2,\"min_doc_freq\":5,\"max_doc_freq\":2147483647,\"min_word_length\":0,\"max_word_length\":0,\"minimum_should_match\":\"30%\",\"boost_terms\":0.0,\"include\":false,\"fail_on_unsupported_field\":true,\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_scriptQuery() throws Exception {
        ScriptQueryBuilder queryBuilder = QueryBuilders.scriptQuery(new Script(ScriptType.INLINE, "painless", "doc['num1'].value > 1", Collections.singletonMap("num1", 1)));
        assertEquals("{\"script\":{\"script\":{\"inline\":\"doc['num1'].value > 1\",\"lang\":\"painless\",\"params\":{\"num1\":1}},\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_spanTermQuery() throws Exception {
        SpanTermQueryBuilder queryBuilder = QueryBuilders.spanTermQuery("field1", "term1");
        assertEquals("{\"span_term\":{\"field1\":{\"value\":\"term1\",\"boost\":1.0}}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_spanMultiTermQuery() throws Exception {
        SpanMultiTermQueryBuilder queryBuilder = QueryBuilders.spanMultiTermQueryBuilder(QueryBuilders.prefixQuery("field1", "ki"));
        assertEquals("{\"span_multi\":{\"match\":{\"prefix\":{\"field1\":{\"value\":\"ki\",\"boost\":1.0}}},\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_spanFirstQuery() throws Exception {
        SpanFirstQueryBuilder queryBuilder = QueryBuilders.spanFirstQuery(QueryBuilders.spanTermQuery("field1", "term1"), 3);
        assertEquals("{\"span_first\":{\"match\":{\"span_term\":{\"field1\":{\"value\":\"term1\",\"boost\":1.0}}},\"end\":3,\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_spanNearQuery() throws Exception {
        SpanNearQueryBuilder queryBuilder = QueryBuilders.spanNearQuery(QueryBuilders.spanTermQuery("field1", "term1"), 5).addClause(QueryBuilders.spanTermQuery("field2", "term2"));
        assertEquals("{\"span_near\":{\"clauses\":[{\"span_term\":{\"field1\":{\"value\":\"term1\",\"boost\":1.0}}},{\"span_term\":{\"field2\":{\"value\":\"term2\",\"boost\":1.0}}}],\"slop\":5,\"in_order\":true,\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_spanOrQuery() throws Exception {
        SpanOrQueryBuilder queryBuilder = QueryBuilders.spanOrQuery(QueryBuilders.spanTermQuery("field1", "term1")).addClause(QueryBuilders.spanTermQuery("field1", "term2"));
        assertEquals("{\"span_or\":{\"clauses\":[{\"span_term\":{\"field1\":{\"value\":\"term1\",\"boost\":1.0}}},{\"span_term\":{\"field1\":{\"value\":\"term2\",\"boost\":1.0}}}],\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_spanNotQuery() throws Exception {
        SpanNotQueryBuilder queryBuilder = QueryBuilders.spanNotQuery(QueryBuilders.spanTermQuery("field1", "term1"), QueryBuilders.spanTermQuery("field2", "term2"));
        assertEquals("{\"span_not\":{\"include\":{\"span_term\":{\"field1\":{\"value\":\"term1\",\"boost\":1.0}}},\"exclude\":{\"span_term\":{\"field2\":{\"value\":\"term2\",\"boost\":1.0}}},\"pre\":0,\"post\":0,\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_spanContainingQuery() throws Exception {
        SpanContainingQueryBuilder queryBuilder = QueryBuilders.spanContainingQuery(QueryBuilders.spanTermQuery("field1", "term1"), QueryBuilders.spanTermQuery("field2", "term2"));
        assertEquals("{\"span_containing\":{\"big\":{\"span_term\":{\"field1\":{\"value\":\"term1\",\"boost\":1.0}}},\"little\":{\"span_term\":{\"field2\":{\"value\":\"term2\",\"boost\":1.0}}},\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_spanWithinQuery() throws Exception {
        SpanWithinQueryBuilder queryBuilder = QueryBuilders.spanWithinQuery(QueryBuilders.spanTermQuery("field1", "term1"), QueryBuilders.spanTermQuery("field2", "term2"));
        assertEquals("{\"span_within\":{\"big\":{\"span_term\":{\"field1\":{\"value\":\"term1\",\"boost\":1.0}}},\"little\":{\"span_term\":{\"field2\":{\"value\":\"term2\",\"boost\":1.0}}},\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    @Test
    public void test_fieldMaskingSpanQuery() throws Exception {
        FieldMaskingSpanQueryBuilder queryBuilder = QueryBuilders.fieldMaskingSpanQuery(QueryBuilders.spanTermQuery("field1", "term2"), "field1");
        assertEquals("{\"field_masking_span\":{\"query\":{\"span_term\":{\"field1\":{\"value\":\"term2\",\"boost\":1.0}}},\"field\":\"field1\",\"boost\":1.0}}", toJsonDsl(queryBuilder));
    }

    private static String toJsonDsl(final AbstractQueryBuilder queryBuilder) {
        return queryBuilder.buildAsBytes().utf8ToString();
    }
}
