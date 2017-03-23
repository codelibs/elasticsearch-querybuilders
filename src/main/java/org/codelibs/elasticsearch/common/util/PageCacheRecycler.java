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

import org.codelibs.elasticsearch.common.lease.Releasable;
import org.codelibs.elasticsearch.common.settings.Setting;
import org.codelibs.elasticsearch.common.settings.Setting.Property;
import org.codelibs.elasticsearch.common.settings.Settings;
import org.codelibs.elasticsearch.common.unit.ByteSizeValue;

import java.util.Locale;

/** A recycler of fixed-size pages. */
public class PageCacheRecycler implements Releasable {

    public static final Setting<Type> TYPE_SETTING =
        new Setting<>("cache.recycler.page.type", Type.CONCURRENT.name(), Type::parse, Property.NodeScope);
    public static final Setting<ByteSizeValue> LIMIT_HEAP_SETTING  =
        Setting.memorySizeSetting("cache.recycler.page.limit.heap", "10%", Property.NodeScope);
    public static final Setting<Double> WEIGHT_BYTES_SETTING  =
        Setting.doubleSetting("cache.recycler.page.weight.bytes", 1d, 0d, Property.NodeScope);
    public static final Setting<Double> WEIGHT_LONG_SETTING  =
        Setting.doubleSetting("cache.recycler.page.weight.longs", 1d, 0d, Property.NodeScope);
    public static final Setting<Double> WEIGHT_INT_SETTING  =
        Setting.doubleSetting("cache.recycler.page.weight.ints", 1d, 0d, Property.NodeScope);
    // object pages are less useful to us so we give them a lower weight by default
    public static final Setting<Double> WEIGHT_OBJECTS_SETTING  =
        Setting.doubleSetting("cache.recycler.page.weight.objects", 0.1d, 0d, Property.NodeScope);

    @Override
    public void close() {
    }

    protected PageCacheRecycler(Settings settings) {
        TYPE_SETTING .get(settings);
        final long limit = LIMIT_HEAP_SETTING .get(settings).getBytes();

        // We have a global amount of memory that we need to divide across data types.
        // Since some types are more useful than other ones we give them different weights.
        // Trying to store all of them in a single stack would be problematic because eg.
        // a work load could fill the recycler with only byte[] pages and then another
        // workload that would work with double[] pages couldn't recycle them because there
        // is no space left in the stack/queue. LRU/LFU policies are not an option either
        // because they would make obtain/release too costly: we really need constant-time
        // operations.
        // Ultimately a better solution would be to only store one kind of data and have the
        // ability to interpret it either as a source of bytes, doubles, longs, etc. eg. thanks
        // to direct ByteBuffers or sun.misc.Unsafe on a byte[] but this would have other issues
        // that would need to be addressed such as garbage collection of native memory or safety
        // of Unsafe writes.
        final double bytesWeight = WEIGHT_BYTES_SETTING .get(settings);
        final double intsWeight = WEIGHT_INT_SETTING .get(settings);
        final double longsWeight = WEIGHT_LONG_SETTING .get(settings);
        final double objectsWeight = WEIGHT_OBJECTS_SETTING .get(settings);

        final double totalWeight = bytesWeight + intsWeight + longsWeight + objectsWeight;
        final int maxPageCount = (int) Math.min(Integer.MAX_VALUE, limit / BigArrays.PAGE_SIZE_IN_BYTES);

        final int maxBytePageCount = (int) (bytesWeight * maxPageCount / totalWeight);

        final int maxIntPageCount = (int) (intsWeight * maxPageCount / totalWeight);

        final int maxLongPageCount = (int) (longsWeight * maxPageCount / totalWeight);

        final int maxObjectPageCount = (int) (objectsWeight * maxPageCount / totalWeight);

        assert BigArrays.PAGE_SIZE_IN_BYTES * (maxBytePageCount + maxIntPageCount + maxLongPageCount + maxObjectPageCount) <= limit;
    }

    public enum Type {
        QUEUE {
        },
        CONCURRENT {
        },
        NONE {
        };

        public static Type parse(String type) {
            try {
                return Type.valueOf(type.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("no type support [" + type + "]");
            }
        }
    }
}
