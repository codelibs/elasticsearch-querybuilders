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

package org.codelibs.elasticsearch.common.util;

import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.RamUsageEstimator;

import java.util.Arrays;

/** Common implementation for array lists that slice data into fixed-size blocks. */
abstract class AbstractBigArray extends AbstractArray {

    private final Object recycler;

    private final int pageShift;
    private final int pageMask;
    protected long size;

    protected AbstractBigArray(int pageSize, BigArrays bigArrays, boolean clearOnResize) {
        super(bigArrays, clearOnResize);
        this.recycler = bigArrays.recycler;
        if (pageSize < 128) {
            throw new IllegalArgumentException("pageSize must be >= 128");
        }
        if ((pageSize & (pageSize - 1)) != 0) {
            throw new IllegalArgumentException("pageSize must be a power of two");
        }
        this.pageShift = Integer.numberOfTrailingZeros(pageSize);
        this.pageMask = pageSize - 1;
        size = 0;
    }

    final int numPages(long capacity) {
        final long numPages = (capacity + pageMask) >>> pageShift;
        if (numPages > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("pageSize=" + (pageMask + 1) + " is too small for such as capacity: " + capacity);
        }
        return (int) numPages;
    }

    final int pageSize() {
        return pageMask + 1;
    }

    final int pageIndex(long index) {
        return (int) (index >>> pageShift);
    }

    final int indexInPage(long index) {
        return (int) (index & pageMask);
    }

    @Override
    public final long size() {
        return size;
    }

    public abstract void resize(long newSize);

    protected abstract int numBytesPerElement();

    @Override
    public final long ramBytesUsed() {
        // rough approximate, we only take into account the size of the values, not the overhead of the array objects
        return ((long) pageIndex(size - 1) + 1) * pageSize() * numBytesPerElement();
    }

    protected final byte[] newBytePage(int page) {
        if (recycler != null) {
            throw new UnsupportedOperationException("querybuilders does not support this operation.");
        } else {
            return new byte[BigArrays.BYTE_PAGE_SIZE];
        }
    }

    protected final int[] newIntPage(int page) {
        if (recycler != null) {
            throw new UnsupportedOperationException("querybuilders does not support this operation.");
        } else {
            return new int[BigArrays.INT_PAGE_SIZE];
        }
    }

    protected final long[] newLongPage(int page) {
        if (recycler != null) {
            throw new UnsupportedOperationException("querybuilders does not support this operation.");
        } else {
            return new long[BigArrays.LONG_PAGE_SIZE];
        }
    }

    protected final Object[] newObjectPage(int page) {
        if (recycler != null) {
            throw new UnsupportedOperationException("querybuilders does not support this operation.");
        } else {
            return new Object[BigArrays.OBJECT_PAGE_SIZE];
        }
    }

    protected final void releasePage(int page) {
        if (recycler != null) {
            throw new UnsupportedOperationException("querybuilders does not support this operation.");
        }
    }

    @Override
    protected final void doClose() {
        if (recycler != null) {
            throw new UnsupportedOperationException("querybuilders does not support this operation.");
        }
    }

}
