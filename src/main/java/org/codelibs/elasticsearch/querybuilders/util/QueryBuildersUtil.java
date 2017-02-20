package org.codelibs.elasticsearch.querybuilders.util;

import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class QueryBuildersUtil {
    private QueryBuildersUtil() {
    }

    public static String toJsonDsl(final AbstractQueryBuilder queryBuilder) {
        QueryBuilders.matchAllQuery();
        return queryBuilder.buildAsBytes().utf8ToString();
    }
}
