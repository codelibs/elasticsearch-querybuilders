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

package org.codelibs.elasticsearch.index.fielddata.plain;

import org.codelibs.elasticsearch.querybuilders.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.RandomAccessOrds;
import org.codelibs.elasticsearch.common.logging.Loggers;
import org.codelibs.elasticsearch.index.Index;
import org.codelibs.elasticsearch.index.IndexSettings;
import org.codelibs.elasticsearch.index.fielddata.IndexFieldData;
import org.codelibs.elasticsearch.index.fielddata.IndexFieldDataCache;
import org.codelibs.elasticsearch.index.fielddata.IndexNumericFieldData.NumericType;
import org.codelibs.elasticsearch.index.fielddata.ScriptDocValues;
import org.codelibs.elasticsearch.index.mapper.IdFieldMapper;
import org.codelibs.elasticsearch.index.mapper.MappedFieldType;
import org.codelibs.elasticsearch.index.mapper.MapperService;
import org.codelibs.elasticsearch.index.mapper.UidFieldMapper;
import org.codelibs.elasticsearch.indices.breaker.CircuitBreakerService;

import java.util.Set;
import java.util.function.Function;

import static java.util.Collections.unmodifiableSet;
import static org.codelibs.elasticsearch.common.util.set.Sets.newHashSet;

/** {@link IndexFieldData} impl based on Lucene's doc values. Caching is done on the Lucene side. */
public abstract class DocValuesIndexFieldData {

    protected final Index index;
    protected final String fieldName;
    protected final Logger logger;

    public DocValuesIndexFieldData(Index index, String fieldName) {
        super();
        this.index = index;
        this.fieldName = fieldName;
        this.logger = Loggers.getLogger(getClass());
    }

    public final String getFieldName() {
        return fieldName;
    }

    public final void clear() {
        // can't do
    }

    public final void clear(IndexReader reader) {
        // can't do
    }

    public final Index index() {
        return index;
    }

    public static class Builder implements IndexFieldData.Builder {
        private static final Set<String> BINARY_INDEX_FIELD_NAMES = unmodifiableSet(newHashSet(UidFieldMapper.NAME, IdFieldMapper.NAME));

        private NumericType numericType;
        private Function<RandomAccessOrds, ScriptDocValues<?>> scriptFunction = AbstractAtomicOrdinalsFieldData.DEFAULT_SCRIPT_FUNCTION;

        public Builder numericType(NumericType type) {
            this.numericType = type;
            return this;
        }

        public Builder scriptFunction(Function<RandomAccessOrds, ScriptDocValues<?>> scriptFunction) {
            this.scriptFunction = scriptFunction;
            return this;
        }

        @Override
        public IndexFieldData<?> build(IndexSettings indexSettings, MappedFieldType fieldType, IndexFieldDataCache cache,
                                       CircuitBreakerService breakerService, MapperService mapperService) {
            // Ignore Circuit Breaker
            final String fieldName = fieldType.name();
            if (BINARY_INDEX_FIELD_NAMES.contains(fieldName)) {
                assert numericType == null;
                return new BinaryDVIndexFieldData(indexSettings.getIndex(), fieldName);
            } else if (numericType != null) {
                return new SortedNumericDVIndexFieldData(indexSettings.getIndex(), fieldName, numericType);
            } else {
                return new SortedSetDVOrdinalsIndexFieldData(indexSettings, cache, fieldName, breakerService, scriptFunction);
            }
        }

    }

}
