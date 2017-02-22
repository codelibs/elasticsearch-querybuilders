package org.codelibs.elasticsearch.querybuilders;

import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;

import static org.junit.Assert.*;

public class QueryBuildersTest {
    @Test
    public void test_queryBuilders() throws Exception {
        assertEquals("{\"match_all\":{\"boost\":1.0}}", toJsonDsl(QueryBuilders.matchAllQuery()));
        assertEquals("{\"term\":{\"field1\":{\"value\":\"keyword\",\"boost\":1.0}}}", toJsonDsl(QueryBuilders.termQuery("field1", "keyword")));
    }

    private static String toJsonDsl(final AbstractQueryBuilder queryBuilder) {
        QueryBuilders.matchAllQuery();
        return queryBuilder.buildAsBytes().utf8ToString();
    }
}
