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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.search.highlight.Encoder;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.NullFragmenter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefHash;
import org.apache.lucene.util.CollectionUtil;
import org.codelibs.elasticsearch.ExceptionsHelper;
import org.codelibs.elasticsearch.common.text.Text;
import org.codelibs.elasticsearch.index.mapper.FieldMapper;
import org.codelibs.elasticsearch.search.fetch.FetchPhaseExecutionException;
import org.codelibs.elasticsearch.search.internal.SearchContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class PlainHighlighter implements Highlighter {

    private static final String CACHE_KEY = "highlight-plain";

    @Override
    public HighlightField highlight(HighlighterContext highlighterContext) {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    public boolean canHighlight(FieldMapper fieldMapper) {
        return true;
    }

    private static int findGoodEndForNoHighlightExcerpt(int noMatchSize, Analyzer analyzer, String fieldName, String contents)
            throws IOException {
        try (TokenStream tokenStream = analyzer.tokenStream(fieldName, contents)) {
            if (!tokenStream.hasAttribute(OffsetAttribute.class)) {
                // Can't split on term boundaries without offsets
                return -1;
            }
            int end = -1;
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                OffsetAttribute attr = tokenStream.getAttribute(OffsetAttribute.class);
                if (attr.endOffset() >= noMatchSize) {
                    // Jump to the end of this token if it wouldn't put us past the boundary
                    if (attr.endOffset() == noMatchSize) {
                        end = noMatchSize;
                    }
                    return end;
                }
                end = attr.endOffset();
            }
            tokenStream.end();
            // We've exhausted the token stream so we should just highlight everything.
            return end;
        }
    }
}
