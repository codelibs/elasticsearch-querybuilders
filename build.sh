#!/bin/bash

export LANG=en_US.UTF-8
export LC_ALL=en_US.UTF-8

VERSION=5.2.1

BASE_DIR=`pwd`
ES_DIR=elasticsearch-${VERSION}
ES_BINARY_URL=https://artifacts.elastic.co/downloads/elasticsearch/${ES_DIR}.zip
ES_SOURCE_URL=https://github.com/elastic/elasticsearch/archive/v${VERSION}.zip

ARCHIVE_NAME=querybuilders-${VERSION}.jar

LIB_DIR=lib
WORK_DIR=work
CLASSES_DIR=$WORK_DIR/classes
ES_CLASSES_DIR=$WORK_DIR/es_classes

rm -r $WORK_DIR
rm -r $LIB_DIR
mkdir -p $LIB_DIR
mkdir -p $WORK_DIR
mkdir -p $CLASSES_DIR
mkdir -p $ES_CLASSES_DIR

# Download zip
if [ ! -f ${ES_DIR}.zip ] ; then
  wget $ES_BINARY_URL
fi

unzip -d $WORK_DIR ${ES_DIR}.zip
unzip -d $ES_CLASSES_DIR $WORK_DIR/${ES_DIR}/lib/$ES_DIR.jar


########## Start: extract querybuilders classes ############################################################

mkdir -p $CLASSES_DIR/org/elasticsearch
cp $ES_CLASSES_DIR/org/elasticsearch/ElasticsearchException.class $CLASSES_DIR/org/elasticsearch
cp $ES_CLASSES_DIR/org/elasticsearch/ElasticsearchParseException.class $CLASSES_DIR/org/elasticsearch
cp $ES_CLASSES_DIR/org/elasticsearch/Version.class $CLASSES_DIR/org/elasticsearch

## index
mkdir -p $CLASSES_DIR/org/elasticsearch/index

### query
cp -r $ES_CLASSES_DIR/org/elasticsearch/index/query $CLASSES_DIR/org/elasticsearch/index

### mapper
cp -r $ES_CLASSES_DIR/org/elasticsearch/index/mapper $CLASSES_DIR/org/elasticsearch/index

## action
mkdir -p $CLASSES_DIR/org/elasticsearch/action/support
cp $ES_CLASSES_DIR/org/elasticsearch/action/support/ToXContentToBytes.class $CLASSES_DIR/org/elasticsearch/action/support/

## client
mkdir -p $CLASSES_DIR/org/elasticsearch/client
cp $ES_CLASSES_DIR/org/elasticsearch/client/Requests.class $CLASSES_DIR/org/elasticsearch/client

## common
mkdir -p $CLASSES_DIR/org/elasticsearch/common
cp $ES_CLASSES_DIR/org/elasticsearch/common/ParsingException.class $CLASSES_DIR/org/elasticsearch/common
cp $ES_CLASSES_DIR/org/elasticsearch/common/ParseField.class $CLASSES_DIR/org/elasticsearch/common
cp $ES_CLASSES_DIR/org/elasticsearch/common/ParseFieldMatcherSupplier.class $CLASSES_DIR/org/elasticsearch/common
cp $ES_CLASSES_DIR/org/elasticsearch/common/Nullable.class $CLASSES_DIR/org/elasticsearch/common
cp $ES_CLASSES_DIR/org/elasticsearch/common/Strings.class $CLASSES_DIR/org/elasticsearch/common
cp -r $ES_CLASSES_DIR/org/elasticsearch/common/breaker $CLASSES_DIR/org/elasticsearch/common/breaker
cp -r $ES_CLASSES_DIR/org/elasticsearch/common/bytes $CLASSES_DIR/org/elasticsearch/common/bytes
cp -r $ES_CLASSES_DIR/org/elasticsearch/common/geo $CLASSES_DIR/org/elasticsearch/common/geo
cp -r $ES_CLASSES_DIR/org/elasticsearch/common/regex $CLASSES_DIR/org/elasticsearch/common/regex
cp -r $ES_CLASSES_DIR/org/elasticsearch/common/io $CLASSES_DIR/org/elasticsearch/common/io
cp -r $ES_CLASSES_DIR/org/elasticsearch/common/lease $CLASSES_DIR/org/elasticsearch/common/lease
cp -r $ES_CLASSES_DIR/org/elasticsearch/common/logging $CLASSES_DIR/org/elasticsearch/common/logging
cp -r $ES_CLASSES_DIR/org/elasticsearch/common/lucene $CLASSES_DIR/org/elasticsearch/common/lucene
cp -r $ES_CLASSES_DIR/org/elasticsearch/common/settings $CLASSES_DIR/org/elasticsearch/common/settings
cp -r $ES_CLASSES_DIR/org/elasticsearch/common/text $CLASSES_DIR/org/elasticsearch/common/text
cp -r $ES_CLASSES_DIR/org/elasticsearch/common/unit $CLASSES_DIR/org/elasticsearch/common/unit
cp -r $ES_CLASSES_DIR/org/elasticsearch/common/util $CLASSES_DIR/org/elasticsearch/common/util
cp -r $ES_CLASSES_DIR/org/elasticsearch/common/xcontent $CLASSES_DIR/org/elasticsearch/common/xcontent

## rest
mkdir -p $CLASSES_DIR/org/elasticsearch/rest
cp $ES_CLASSES_DIR/org/elasticsearch/rest/RestStatus.class $CLASSES_DIR/org/elasticsearch/rest

########## End: extract querybuilders classes ############################################################


# archive elasticsearch-querybuilders.jar
cd $CLASSES_DIR
zip -r $BASE_DIR/$LIB_DIR/$ARCHIVE_NAME *
cd $BASE_DIR

mvn install:install-file -Dfile=$LIB_DIR/$ARCHIVE_NAME -DpomFile=querybuilders_pom.xml
