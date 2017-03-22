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

package org.codelibs.elasticsearch.index.query;

import static java.util.Collections.unmodifiableMap;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.LongSupplier;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.join.BitSetProducer;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.SetOnce;
import org.codelibs.elasticsearch.Version;
import org.codelibs.elasticsearch.common.ParsingException;
import org.codelibs.elasticsearch.common.Strings;
import org.codelibs.elasticsearch.common.bytes.BytesReference;
import org.codelibs.elasticsearch.common.lucene.search.Queries;
import org.codelibs.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.codelibs.elasticsearch.index.fielddata.IndexFieldData;
import org.codelibs.elasticsearch.index.mapper.ContentPath;
import org.codelibs.elasticsearch.index.mapper.MappedFieldType;
import org.codelibs.elasticsearch.index.mapper.Mapper;
import org.codelibs.elasticsearch.index.mapper.TextFieldMapper;
import org.codelibs.elasticsearch.index.query.support.NestedScope;
import org.codelibs.elasticsearch.script.CompiledScript;
import org.codelibs.elasticsearch.script.ExecutableScript;
import org.codelibs.elasticsearch.script.Script;
import org.codelibs.elasticsearch.script.ScriptContext;
import org.codelibs.elasticsearch.script.SearchScript;

/**
 * Context object used to create lucene queries on the shard level.
 */
public class QueryShardContext extends QueryRewriteContext {
}
