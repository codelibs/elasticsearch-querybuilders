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

import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.search.vectorhighlight.FragListBuilder;
import org.apache.lucene.search.vectorhighlight.FragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleBoundaryScanner;
import org.codelibs.elasticsearch.common.settings.Setting;
import org.codelibs.elasticsearch.common.settings.Settings;
import org.codelibs.elasticsearch.index.mapper.FieldMapper;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class FastVectorHighlighter implements Highlighter {

    private static final SimpleBoundaryScanner DEFAULT_BOUNDARY_SCANNER = new SimpleBoundaryScanner();

    public static final Setting<Boolean> SETTING_TV_HIGHLIGHT_MULTI_VALUE = Setting.boolSetting("search.highlight.term_vector_multi_value",
        true, Setting.Property.NodeScope);
    private final Boolean termVectorMultiValue;

    public FastVectorHighlighter(Settings settings) {
        this.termVectorMultiValue = SETTING_TV_HIGHLIGHT_MULTI_VALUE.get(settings);
    }

    @Override
    public HighlightField highlight(HighlighterContext highlighterContext) {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    public boolean canHighlight(FieldMapper fieldMapper) {
        return fieldMapper.fieldType().storeTermVectors() && fieldMapper.fieldType().storeTermVectorOffsets()
                && fieldMapper.fieldType().storeTermVectorPositions();
    }

    private class MapperHighlightEntry {
    }
}
