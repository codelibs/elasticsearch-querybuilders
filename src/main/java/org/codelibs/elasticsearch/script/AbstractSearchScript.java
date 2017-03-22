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

package org.codelibs.elasticsearch.script;

import org.apache.lucene.search.Scorer;
import org.codelibs.elasticsearch.index.fielddata.ScriptDocValues;

import java.io.IOException;
import java.util.Map;

/**
 * A base class for any script type that is used during the search process (custom score, aggs, and so on).
 * <p>
 * If the script returns a specific numeric type, consider overriding the type specific base classes
 * such as {@link AbstractDoubleSearchScript} and {@link AbstractLongSearchScript}
 * for better performance.
 * <p>
 * The use is required to implement the {@link #run()} method.
 */
public abstract class AbstractSearchScript extends AbstractExecutableScript implements LeafSearchScript {

    private Scorer scorer;

    /**
     * Returns the current score and only applicable when used as a scoring script in a custom score query!.
     */
    protected final float score() throws IOException {
        return scorer.score();
    }

    @Override
    public void setScorer(Scorer scorer) {
        this.scorer = scorer;
    }

    @Override
    public void setDocument(int doc) {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    public void setSource(Map<String, Object> source) {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    public long runAsLong() {
        return ((Number) run()).longValue();
    }

    @Override
    public double runAsDouble() {
        return ((Number) run()).doubleValue();
    }
}
