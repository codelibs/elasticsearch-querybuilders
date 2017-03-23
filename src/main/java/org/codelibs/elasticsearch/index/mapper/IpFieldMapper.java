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

package org.codelibs.elasticsearch.index.mapper;

import org.apache.lucene.document.InetAddressPoint;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.PointValues;
import org.apache.lucene.index.RandomAccessOrds;
import org.apache.lucene.search.MatchNoDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.BytesRef;
import org.codelibs.elasticsearch.action.fieldstats.FieldStats;
import org.codelibs.elasticsearch.common.Explicit;
import org.codelibs.elasticsearch.common.Nullable;
import org.codelibs.elasticsearch.common.network.InetAddresses;
import org.codelibs.elasticsearch.common.settings.Settings;
import org.codelibs.elasticsearch.common.xcontent.XContentBuilder;
import org.codelibs.elasticsearch.index.fielddata.IndexFieldData;
import org.codelibs.elasticsearch.index.fielddata.ScriptDocValues;
import org.codelibs.elasticsearch.index.mapper.LegacyNumberFieldMapper.Defaults;
import org.codelibs.elasticsearch.index.query.QueryShardContext;
import org.codelibs.elasticsearch.search.DocValueFormat;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.net.InetAddress;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** A {FieldMapper} for ip addresses. */
public class IpFieldMapper extends FieldMapper {

    public static final String CONTENT_TYPE = "ip";

    public static class Builder extends FieldMapper.Builder<Builder, IpFieldMapper> {

        private Boolean ignoreMalformed;

        public Builder(String name) {
            super(name, new IpFieldType(), new IpFieldType());
            builder = this;
        }

        public Builder ignoreMalformed(boolean ignoreMalformed) {
            this.ignoreMalformed = ignoreMalformed;
            return builder;
        }

        protected Explicit<Boolean> ignoreMalformed(BuilderContext context) {
            if (ignoreMalformed != null) {
                return new Explicit<>(ignoreMalformed, true);
            }
            if (context.indexSettings() != null) {
                return new Explicit<>(IGNORE_MALFORMED_SETTING.get(context.indexSettings()), false);
            }
            return Defaults.IGNORE_MALFORMED;
        }

        @Override
        public IpFieldMapper build(BuilderContext context) {
            setupFieldType(context);
            return new IpFieldMapper(name, fieldType, defaultFieldType, ignoreMalformed(context),
                    includeInAll, context.indexSettings(), multiFieldsBuilder.build(this, context), copyTo);
        }
    }

    public static class TypeParser implements Mapper.TypeParser {

        public TypeParser() {
        }

    }

    public static final class IpFieldType extends MappedFieldType {

        IpFieldType() {
            super();
            setTokenized(false);
            setHasDocValues(true);
        }

        IpFieldType(IpFieldType other) {
            super(other);
        }

        @Override
        public MappedFieldType clone() {
            return new IpFieldType(this);
        }

        @Override
        public String typeName() {
            return CONTENT_TYPE;
        }

        private InetAddress parse(Object value) {
            if (value instanceof InetAddress) {
                return (InetAddress) value;
            } else {
                if (value instanceof BytesRef) {
                    value = ((BytesRef) value).utf8ToString();
                }
                return InetAddresses.forString(value.toString());
            }
        }

        @Override
        public Query termQuery(Object value, @Nullable QueryShardContext context) {
            failIfNotIndexed();
            if (value instanceof InetAddress) {
                return InetAddressPoint.newExactQuery(name(), (InetAddress) value);
            } else {
                if (value instanceof BytesRef) {
                    value = ((BytesRef) value).utf8ToString();
                }
                String term = value.toString();
                if (term.contains("/")) {
                    String[] fields = term.split("/");
                    if (fields.length == 2) {
                        InetAddress address = InetAddresses.forString(fields[0]);
                        int prefixLength = Integer.parseInt(fields[1]);
                        return InetAddressPoint.newPrefixQuery(name(), address, prefixLength);
                    } else {
                        throw new IllegalArgumentException("Expected [ip/prefix] but was [" + term + "]");
                    }
                }
                InetAddress address = InetAddresses.forString(term);
                return InetAddressPoint.newExactQuery(name(), address);
            }
        }

        @Override
        public Query rangeQuery(Object lowerTerm, Object upperTerm, boolean includeLower, boolean includeUpper, QueryShardContext context) {
            failIfNotIndexed();
            InetAddress lower;
            if (lowerTerm == null) {
                lower = InetAddressPoint.MIN_VALUE;
            } else {
                lower = parse(lowerTerm);
                if (includeLower == false) {
                    if (lower.equals(InetAddressPoint.MAX_VALUE)) {
                        return new MatchNoDocsQuery();
                    }
                    lower = InetAddressPoint.nextUp(lower);
                }
            }

            InetAddress upper;
            if (upperTerm == null) {
                upper = InetAddressPoint.MAX_VALUE;
            } else {
                upper = parse(upperTerm);
                if (includeUpper == false) {
                    if (upper.equals(InetAddressPoint.MIN_VALUE)) {
                        return new MatchNoDocsQuery();
                    }
                    upper = InetAddressPoint.nextDown(upper);
                }
            }

            return InetAddressPoint.newRangeQuery(name(), lower, upper);
        }

        @Override
        public FieldStats.Ip stats(IndexReader reader) throws IOException {
            String field = name();
            FieldInfo fi = org.apache.lucene.index.MultiFields.getMergedFieldInfos(reader).fieldInfo(name());
            if (fi == null) {
                return null;
            }
            long size = PointValues.size(reader, field);
            if (size == 0) {
                return new FieldStats.Ip(reader.maxDoc(), 0, -1, -1, isSearchable(), isAggregatable());
            }
            int docCount = PointValues.getDocCount(reader, field);
            byte[] min = PointValues.getMinPackedValue(reader, field);
            byte[] max = PointValues.getMaxPackedValue(reader, field);
            return new FieldStats.Ip(reader.maxDoc(), docCount, -1L, size,
                isSearchable(), isAggregatable(),
                InetAddressPoint.decode(min), InetAddressPoint.decode(max));
        }

        public static final class IpScriptDocValues extends AbstractList<String> implements ScriptDocValues<String> {

            private final RandomAccessOrds values;

            IpScriptDocValues(RandomAccessOrds values) {
                this.values = values;
            }

            @Override
            public void setNextDocId(int docId) {
                values.setDocument(docId);
            }

            public String getValue() {
                if (isEmpty()) {
                    return null;
                } else {
                    return get(0);
                }
            }

            @Override
            public List<String> getValues() {
                return Collections.unmodifiableList(this);
            }

            @Override
            public String get(int index) {
                BytesRef encoded = values.lookupOrd(values.ordAt(0));
                InetAddress address = InetAddressPoint.decode(
                        Arrays.copyOfRange(encoded.bytes, encoded.offset, encoded.offset + encoded.length));
                return InetAddresses.toAddrString(address);
            }

            @Override
            public int size() {
                return values.cardinality();
            }
        }

        @Override
        public IndexFieldData.Builder fielddataBuilder() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object valueForDisplay(Object value) {
            if (value == null) {
                return null;
            }
            return DocValueFormat.IP.format((BytesRef) value);
        }

        @Override
        public DocValueFormat docValueFormat(@Nullable String format, DateTimeZone timeZone) {
            if (format != null) {
                throw new IllegalArgumentException("Field [" + name() + "] of type [" + typeName() + "] does not support custom formats");
            }
            if (timeZone != null) {
                throw new IllegalArgumentException("Field [" + name() + "] of type [" + typeName()
                    + "] does not support custom time zones");
            }
            return DocValueFormat.IP;
        }
    }

    private Boolean includeInAll;

    private Explicit<Boolean> ignoreMalformed;

    private IpFieldMapper(
            String simpleName,
            MappedFieldType fieldType,
            MappedFieldType defaultFieldType,
            Explicit<Boolean> ignoreMalformed,
            Boolean includeInAll,
            Settings indexSettings,
            MultiFields multiFields,
            CopyTo copyTo) {
        super(simpleName, fieldType, defaultFieldType, indexSettings, multiFields, copyTo);
        this.ignoreMalformed = ignoreMalformed;
        this.includeInAll = includeInAll;
    }

    @Override
    public IpFieldType fieldType() {
        return (IpFieldType) super.fieldType();
    }

    @Override
    protected String contentType() {
        return fieldType.typeName();
    }

    @Override
    protected IpFieldMapper clone() {
        return (IpFieldMapper) super.clone();
    }

    @Override
    protected void doMerge(Mapper mergeWith, boolean updateAllTypes) {
        super.doMerge(mergeWith, updateAllTypes);
        IpFieldMapper other = (IpFieldMapper) mergeWith;
        this.includeInAll = other.includeInAll;
        if (other.ignoreMalformed.explicit()) {
            this.ignoreMalformed = other.ignoreMalformed;
        }
    }

    @Override
    protected void doXContentBody(XContentBuilder builder, boolean includeDefaults, Params params) throws IOException {
        super.doXContentBody(builder, includeDefaults, params);

        if (includeDefaults || fieldType().nullValue() != null) {
            Object nullValue = fieldType().nullValue();
            if (nullValue != null) {
                nullValue = InetAddresses.toAddrString((InetAddress) nullValue);
            }
            builder.field("null_value", nullValue);
        }

        if (includeDefaults || ignoreMalformed.explicit()) {
            builder.field("ignore_malformed", ignoreMalformed.value());
        }
        if (includeInAll != null) {
            builder.field("include_in_all", includeInAll);
        } else if (includeDefaults) {
            builder.field("include_in_all", false);
        }
    }
}
