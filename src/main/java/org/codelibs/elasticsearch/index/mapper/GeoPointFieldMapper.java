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

import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.spatial.geopoint.document.GeoPointField;
import org.codelibs.elasticsearch.Version;
import org.codelibs.elasticsearch.common.Explicit;
import org.codelibs.elasticsearch.common.settings.Settings;

/**
 * Parsing: We handle:
 * <p>
 * - "field" : "geo_hash"
 * - "field" : "lat,lon"
 * - "field" : {
 * "lat" : 1.1,
 * "lon" : 2.1
 * }
 */
public class GeoPointFieldMapper extends BaseGeoPointFieldMapper  {

    public static final String CONTENT_TYPE = "geo_point";

    public static class Defaults extends BaseGeoPointFieldMapper.Defaults {

        public static final GeoPointFieldType FIELD_TYPE = new LegacyGeoPointFieldType();

        static {
            FIELD_TYPE.setIndexOptions(IndexOptions.DOCS);
            FIELD_TYPE.setTokenized(false);
            FIELD_TYPE.setOmitNorms(true);
            FIELD_TYPE.setDocValuesType(DocValuesType.SORTED_NUMERIC);
            FIELD_TYPE.setHasDocValues(true);
            FIELD_TYPE.freeze();
        }
    }

    /**
     * Concrete builder for indexed GeoPointField type
     */
    public static class Builder extends BaseGeoPointFieldMapper.Builder<Builder, GeoPointFieldMapper> {

        public Builder(String name) {
            super(name, Defaults.FIELD_TYPE);
            this.builder = this;
        }

        @Override
        public GeoPointFieldMapper build(BuilderContext context, String simpleName, MappedFieldType fieldType,
                                         MappedFieldType defaultFieldType, Settings indexSettings, FieldMapper latMapper,
                                         FieldMapper lonMapper, FieldMapper geoHashMapper, MultiFields multiFields, Explicit<Boolean> ignoreMalformed,
                                         CopyTo copyTo) {
            fieldType.setTokenized(false);
            if (context.indexCreatedVersion().before(Version.V_2_3_0)) {
                fieldType.setNumericPrecisionStep(GeoPointField.PRECISION_STEP);
                fieldType.setNumericType(FieldType.LegacyNumericType.LONG);
            }
            setupFieldType(context);
            return new GeoPointFieldMapper(simpleName, fieldType, defaultFieldType, indexSettings, latMapper, lonMapper,
                    geoHashMapper, multiFields, ignoreMalformed, copyTo);
        }

        @Override
        public GeoPointFieldMapper build(BuilderContext context) {
            if (context.indexCreatedVersion().before(Version.V_2_3_0)) {
                fieldType.setNumericPrecisionStep(GeoPointField.PRECISION_STEP);
                fieldType.setNumericType(FieldType.LegacyNumericType.LONG);
            }
            return super.build(context);
        }
    }

    public static class TypeParser extends BaseGeoPointFieldMapper.TypeParser {
    }

    public GeoPointFieldMapper(String simpleName, MappedFieldType fieldType, MappedFieldType defaultFieldType, Settings indexSettings,
                               FieldMapper latMapper, FieldMapper lonMapper,
                               FieldMapper geoHashMapper, MultiFields multiFields, Explicit<Boolean> ignoreMalformed, CopyTo copyTo) {
        super(simpleName, fieldType, defaultFieldType, indexSettings, latMapper, lonMapper, geoHashMapper, multiFields,
                ignoreMalformed, copyTo);
    }

    @Override
    public LegacyGeoPointFieldType fieldType() {
        return (LegacyGeoPointFieldType) super.fieldType();
    }
}
