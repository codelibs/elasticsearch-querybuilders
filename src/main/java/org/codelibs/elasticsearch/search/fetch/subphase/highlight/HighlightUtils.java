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

import org.apache.lucene.search.highlight.DefaultEncoder;
import org.apache.lucene.search.highlight.Encoder;
import org.apache.lucene.search.highlight.SimpleHTMLEncoder;
import org.codelibs.elasticsearch.index.mapper.FieldMapper;
import org.codelibs.elasticsearch.search.internal.SearchContext;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singleton;

public final class HighlightUtils {

    //U+2029 PARAGRAPH SEPARATOR (PS): each value holds a discrete passage for highlighting (postings highlighter)
    public static final char PARAGRAPH_SEPARATOR = 8233;
    public static final char NULL_SEPARATOR = '\u0000';

    private HighlightUtils() {

    }

    static class Encoders {
        static final Encoder DEFAULT = new DefaultEncoder();
        static final Encoder HTML = new SimpleHTMLEncoder();
    }
}
