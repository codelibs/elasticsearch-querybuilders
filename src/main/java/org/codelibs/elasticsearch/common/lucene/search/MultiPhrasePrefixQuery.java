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

package org.codelibs.elasticsearch.common.lucene.search;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MultiPhrasePrefixQuery extends Query {

    private String field;
    private ArrayList<Term[]> termArrays = new ArrayList<>();
    private ArrayList<Integer> positions = new ArrayList<>();
    private int slop = 0;

    /**
     * Sets the phrase slop for this query.
     *
     * @see org.apache.lucene.search.PhraseQuery.Builder#setSlop(int)
     */
    public void setSlop(int s) {
        slop = s;
    }

    public void setMaxExpansions(int maxExpansions) {
    }

    /**
     * Sets the phrase slop for this query.
     *
     * @see org.apache.lucene.search.PhraseQuery.Builder#getSlop()
     */
    public int getSlop() {
        return slop;
    }

    /**
     * Add a single term at the next position in the phrase.
     *
     * @see org.apache.lucene.search.PhraseQuery.Builder#add(Term)
     */
    public void add(Term term) {
        add(new Term[]{term});
    }

    /**
     * Add multiple terms at the next position in the phrase.  Any of the terms
     * may match.
     *
     * @see org.apache.lucene.search.PhraseQuery.Builder#add(Term)
     */
    public void add(Term[] terms) {
        int position = 0;
        if (positions.size() > 0) {
            position = positions.get(positions.size() - 1) + 1;
        }

        add(terms, position);
    }

    /**
     * Allows to specify the relative position of terms within the phrase.
     *
     * @param terms the terms
     * @param position the position of the terms provided as argument
     * @see org.apache.lucene.search.PhraseQuery.Builder#add(Term, int)
     */
    public void add(Term[] terms, int position) {
        if (termArrays.size() == 0) {
            field = terms[0].field();
        }

        for (Term term : terms) {
            if (term.field() != field) {
                throw new IllegalArgumentException(
                        "All phrase terms must be in the same field (" + field + "): "
                                + term);
            }
        }

        termArrays.add(terms);
        positions.add(position);
    }

    /**
     * Returns the relative positions of terms in this phrase.
     */
    public int[] getPositions() {
        int[] result = new int[positions.size()];
        for (int i = 0; i < positions.size(); i++) {
            result[i] = positions.get(i);
        }
        return result;
    }

    @Override
    public Query rewrite(IndexReader reader) throws IOException {
        throw new UnsupportedOperationException("querybuilders does not support this operation.");
    }

    @Override
    public final String toString(String f) {
        StringBuilder buffer = new StringBuilder();
        if (field == null || !field.equals(f)) {
            buffer.append(field);
            buffer.append(":");
        }

        buffer.append("\"");
        Iterator<Term[]> i = termArrays.iterator();
        while (i.hasNext()) {
            Term[] terms = i.next();
            if (terms.length > 1) {
                buffer.append("(");
                for (int j = 0; j < terms.length; j++) {
                    buffer.append(terms[j].text());
                    if (j < terms.length - 1) {
                        if (i.hasNext()) {
                            buffer.append(" ");
                        } else {
                            buffer.append("* ");
                        }
                    }
                }
                if (i.hasNext()) {
                    buffer.append(") ");
                } else {
                    buffer.append("*)");
                }
            } else {
                buffer.append(terms[0].text());
                if (i.hasNext()) {
                    buffer.append(" ");
                } else {
                    buffer.append("*");
                }
            }
        }
        buffer.append("\"");

        if (slop != 0) {
            buffer.append("~");
            buffer.append(slop);
        }

        return buffer.toString();
    }

    /**
     * Returns true if <code>o</code> is equal to this.
     */
    @Override
    public boolean equals(Object o) {
        if (sameClassAs(o) == false) {
            return false;
        }
        MultiPhrasePrefixQuery other = (MultiPhrasePrefixQuery) o;
        return this.slop == other.slop
                && termArraysEquals(this.termArrays, other.termArrays)
                && this.positions.equals(other.positions);
    }

    /**
     * Returns a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return classHash()
                ^ slop
                ^ termArraysHashCode()
                ^ positions.hashCode();
    }

    // Breakout calculation of the termArrays hashcode
    private int termArraysHashCode() {
        int hashCode = 1;
        for (final Term[] termArray : termArrays) {
            hashCode = 31 * hashCode
                    + (termArray == null ? 0 : Arrays.hashCode(termArray));
        }
        return hashCode;
    }

    // Breakout calculation of the termArrays equals
    private boolean termArraysEquals(List<Term[]> termArrays1, List<Term[]> termArrays2) {
        if (termArrays1.size() != termArrays2.size()) {
            return false;
        }
        ListIterator<Term[]> iterator1 = termArrays1.listIterator();
        ListIterator<Term[]> iterator2 = termArrays2.listIterator();
        while (iterator1.hasNext()) {
            Term[] termArray1 = iterator1.next();
            Term[] termArray2 = iterator2.next();
            if (!(termArray1 == null ? termArray2 == null : Arrays.equals(termArray1,
                    termArray2))) {
                return false;
            }
        }
        return true;
    }

    public String getField() {
        return field;
    }
}
