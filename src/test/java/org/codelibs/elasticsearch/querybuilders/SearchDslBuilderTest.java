package org.codelibs.elasticsearch.querybuilders;

import org.codelibs.elasticsearch.index.query.QueryBuilders;
import org.codelibs.elasticsearch.search.aggregations.AggregationBuilders;
import org.codelibs.elasticsearch.search.sort.SortOrder;
import org.junit.Test;

import static org.junit.Assert.*;

public class SearchDslBuilderTest {
    @Test
    public void test_searchDslBuilder() {
        assertEquals("{\"from\":0,\"size\":20,\"query\":{\"match_all\":{\"boost\":1.0}},\"sort\":[{\"field1\":{\"order\":\"desc\"}}],\"aggregations\":{\"test\":{\"terms\":{\"field\":\"field1\",\"size\":10,\"min_doc_count\":1,\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":[{\"_count\":\"desc\"},{\"_term\":\"asc\"}]}}}}",
            SearchDslBuilder.builder().query(() ->
                QueryBuilders.matchAllQuery()
            ).aggregation(() ->
                AggregationBuilders.terms("test").field("field1")
            ).from(0).size(20).sort("field1", SortOrder.DESC).build());
    }
}
