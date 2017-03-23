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

package org.codelibs.elasticsearch.search.fetch.subphase;

import org.codelibs.elasticsearch.search.internal.SearchContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 */
public final class InnerHitsContext {

    private final Map<String, BaseInnerHits> innerHits;

    public InnerHitsContext() {
        this.innerHits = new HashMap<>();
    }

    public InnerHitsContext(Map<String, BaseInnerHits> innerHits) {
        this.innerHits = Objects.requireNonNull(innerHits);
    }

    public Map<String, BaseInnerHits> getInnerHits() {
        return innerHits;
    }

    public void addInnerHitDefinition(BaseInnerHits innerHit) {
        if (innerHits.containsKey(innerHit.getName())) {
            throw new IllegalArgumentException("inner_hit definition with the name [" + innerHit.getName() +
                    "] already exists. Use a different inner_hit name or define one explicitly");
        }

        innerHits.put(innerHit.getName(), innerHit);
    }

    public abstract static class BaseInnerHits {

        private final String name;
        private InnerHitsContext childInnerHits;

        protected BaseInnerHits(String name, SearchContext context) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public InnerHitsContext innerHits() {
            return childInnerHits;
        }

        public void setChildInnerHits(Map<String, InnerHitsContext.BaseInnerHits> childInnerHits) {
            this.childInnerHits = new InnerHitsContext(childInnerHits);
        }
    }
}
