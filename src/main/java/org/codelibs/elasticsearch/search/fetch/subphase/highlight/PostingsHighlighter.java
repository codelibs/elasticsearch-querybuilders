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

import org.apache.lucene.index.IndexOptions;
import org.codelibs.elasticsearch.common.Strings;
import org.codelibs.elasticsearch.index.mapper.FieldMapper;
import org.codelibs.elasticsearch.search.internal.SearchContext;
import java.util.List;

public class PostingsHighlighter implements Highlighter {

    @Override
    public HighlightField highlight(HighlighterContext highlighterContext) {

        FieldMapper fieldMapper = highlighterContext.mapper;
        if (canHighlight(fieldMapper) == false) {
            throw new IllegalArgumentException("the field [" + highlighterContext.fieldName
                    + "] should be indexed with positions and offsets in the postings list to be used with postings highlighter");
        }

        throw new UnsupportedOperationException("QueryBuilders does not support this operation.");
        /*        FetchSubPhase.HitContext hitContext = highlighterContext.hitContext;
        if (!hitContext.cache().containsKey(CACHE_KEY)) {
            hitContext.cache().put(CACHE_KEY, new HighlighterEntry());
        }

        HighlighterEntry highlighterEntry = (HighlighterEntry) hitContext.cache().get(CACHE_KEY);
        MapperHighlighterEntry mapperHighlighterEntry = highlighterEntry.mappers.get(fieldMapper);

        throw new UnsupportedOperationException("QueryBuilders does not support this operation.");
        if (mapperHighlighterEntry == null) {
            Encoder encoder = field.fieldOptions().encoder().equals("html") ? Encoders.HTML : Encoders.DEFAULT;
            CustomPassageFormatter passageFormatter = new CustomPassageFormatter(
                    field.fieldOptions().preTags()[0], field.fieldOptions().postTags()[0], encoder);
            mapperHighlighterEntry = new MapperHighlighterEntry(passageFormatter);
        }

        List<Snippet> snippets = new ArrayList<>();
        int numberOfFragments;
        try {
            Analyzer analyzer = context.mapperService().documentMapper(hitContext.hit().type()).mappers().indexAnalyzer();
            List<Object> fieldValues = HighlightUtils.loadFieldValues(field, fieldMapper, context, hitContext);
            CustomPostingsHighlighter highlighter;
            if (field.fieldOptions().numberOfFragments() == 0) {
                //we use a control char to separate values, which is the only char that the custom break iterator breaks the text on,
                //so we don't lose the distinction between the different values of a field and we get back a snippet per value
                String fieldValue = mergeFieldValues(fieldValues, HighlightUtils.NULL_SEPARATOR);
                CustomSeparatorBreakIterator breakIterator = new CustomSeparatorBreakIterator(HighlightUtils.NULL_SEPARATOR);
                highlighter = new CustomPostingsHighlighter(analyzer, mapperHighlighterEntry.passageFormatter, breakIterator,
                        fieldValue, field.fieldOptions().noMatchSize() > 0);
                numberOfFragments = fieldValues.size(); //we are highlighting the whole content, one snippet per value
            } else {
                //using paragraph separator we make sure that each field value holds a discrete passage for highlighting
                String fieldValue = mergeFieldValues(fieldValues, HighlightUtils.PARAGRAPH_SEPARATOR);
                highlighter = new CustomPostingsHighlighter(analyzer, mapperHighlighterEntry.passageFormatter,
                        fieldValue, field.fieldOptions().noMatchSize() > 0);
                numberOfFragments = field.fieldOptions().numberOfFragments();
            }

            IndexSearcher searcher = new IndexSearcher(hitContext.reader());
            Snippet[] fieldSnippets = highlighter.highlightField(fieldMapper.fieldType().name(), highlighterContext.query, searcher,
                    hitContext.docId(), numberOfFragments);
            for (Snippet fieldSnippet : fieldSnippets) {
                if (Strings.hasText(fieldSnippet.getText())) {
                    snippets.add(fieldSnippet);
                }
            }

        } catch(IOException e) {
            throw new FetchPhaseExecutionException(context, "Failed to highlight field [" + highlighterContext.fieldName + "]", e);
        }

        snippets = filterSnippets(snippets, field.fieldOptions().numberOfFragments());

        if (field.fieldOptions().scoreOrdered()) {
            //let's sort the snippets by score if needed
            CollectionUtil.introSort(snippets, new Comparator<Snippet>() {
                @Override
                public int compare(Snippet o1, Snippet o2) {
                    return (int) Math.signum(o2.getScore() - o1.getScore());
                }
            });
        }

        String[] fragments = new String[snippets.size()];
        for (int i = 0; i < fragments.length; i++) {
            fragments[i] = snippets.get(i).getText();
        }

        if (fragments.length > 0) {
            return new HighlightField(highlighterContext.fieldName, Text.convertFromStringArray(fragments));
        }

        return null;*/
    }

    @Override
    public boolean canHighlight(FieldMapper fieldMapper) {
        return fieldMapper.fieldType().indexOptions() == IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS;
    }

}
