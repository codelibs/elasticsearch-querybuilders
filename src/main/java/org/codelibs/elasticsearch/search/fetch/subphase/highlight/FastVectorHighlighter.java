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
package org.codelibs.elasticsearch.search.fetch.subphase.highlight;

import org.apache.lucene.search.highlight.Encoder;
import org.apache.lucene.search.vectorhighlight.BaseFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.BoundaryScanner;
import org.apache.lucene.search.vectorhighlight.FieldFragList;
import org.apache.lucene.search.vectorhighlight.FieldPhraseList.WeightedPhraseInfo;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.search.vectorhighlight.FragListBuilder;
import org.apache.lucene.search.vectorhighlight.FragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.ScoreOrderFragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleBoundaryScanner;
import org.apache.lucene.search.vectorhighlight.SimpleFieldFragList;
import org.apache.lucene.search.vectorhighlight.SimpleFragListBuilder;
import org.apache.lucene.search.vectorhighlight.SingleFragListBuilder;
import org.codelibs.elasticsearch.common.settings.Setting;
import org.codelibs.elasticsearch.common.settings.Settings;
import org.codelibs.elasticsearch.common.text.Text;
import org.codelibs.elasticsearch.index.mapper.FieldMapper;
import org.codelibs.elasticsearch.search.fetch.FetchPhaseExecutionException;
import org.codelibs.elasticsearch.search.fetch.FetchSubPhase;
import org.codelibs.elasticsearch.search.internal.SearchContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class FastVectorHighlighter implements Highlighter {

    private static final SimpleBoundaryScanner DEFAULT_BOUNDARY_SCANNER = new SimpleBoundaryScanner();

    public static final Setting<Boolean> SETTING_TV_HIGHLIGHT_MULTI_VALUE = Setting.boolSetting("search.highlight.term_vector_multi_value",
        true, Setting.Property.NodeScope);
    private static final String CACHE_KEY = "highlight-fsv";
    private final Boolean termVectorMultiValue;

    public FastVectorHighlighter(Settings settings) {
        this.termVectorMultiValue = SETTING_TV_HIGHLIGHT_MULTI_VALUE.get(settings);
    }

    @Override
    public HighlightField highlight(HighlighterContext highlighterContext) {
        SearchContextHighlight.Field field = highlighterContext.field;
        SearchContext context = highlighterContext.context;
        FetchSubPhase.HitContext hitContext = highlighterContext.hitContext;
        FieldMapper mapper = highlighterContext.mapper;

        if (canHighlight(mapper) == false) {
            throw new IllegalArgumentException("the field [" + highlighterContext.fieldName
                    + "] should be indexed with term vector with position offsets to be used with fast vector highlighter");
        }

        Encoder encoder = field.fieldOptions().encoder().equals("html") ? HighlightUtils.Encoders.HTML : HighlightUtils.Encoders.DEFAULT;

        if (!hitContext.cache().containsKey(CACHE_KEY)) {
            hitContext.cache().put(CACHE_KEY, new HighlighterEntry());
        }
        HighlighterEntry cache = (HighlighterEntry) hitContext.cache().get(CACHE_KEY);

        throw new UnsupportedOperationException("QueryBuilders does not support this operation.");
    }

    @Override
    public boolean canHighlight(FieldMapper fieldMapper) {
        return fieldMapper.fieldType().storeTermVectors() && fieldMapper.fieldType().storeTermVectorOffsets()
                && fieldMapper.fieldType().storeTermVectorPositions();
    }

    private class MapperHighlightEntry {
        public FragListBuilder fragListBuilder;
        public FragmentsBuilder fragmentsBuilder;
    }

    private class HighlighterEntry {
        public org.apache.lucene.search.vectorhighlight.FastVectorHighlighter fvh;
        public FieldQuery noFieldMatchFieldQuery;
        public FieldQuery fieldMatchFieldQuery;
        public Map<FieldMapper, MapperHighlightEntry> mappers = new HashMap<>();
    }
}
