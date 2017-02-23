#!/bin/bash

export LANG=en_US.UTF-8
export LC_ALL=en_US.UTF-8

cd `dirname $0`
BASE_DIR=`pwd`
VERSION=`grep elasticsearch.version pom.xml | sed -e "s/.*elasticsearch.version>\(.*\)<\/elasticsearch.version.*/\1/"`
ES_DIR=elasticsearch-${VERSION}
ES_SOURCE_URL=https://github.com/elastic/elasticsearch/archive/v${VERSION}.zip
WORK_DIR=$BASE_DIR/work
SOURCE_DIR=$BASE_DIR/src/main/java
ES_SOURCE_DIR=$WORK_DIR/$ES_DIR/core/src/main/java

ORIG_PACKAGE="org.elasticsearch"
DST_PACKAGE="org.codelibs.elasticsearch"
ORIG_DIR=`echo $ORIG_PACKAGE | sed -e s#\\\\.#/#g`
DST_DIR=`echo $DST_PACKAGE | sed -e s#\\\\.#/#g`
CLASS_LIST=$BASE_DIR/querybuilders-classes.list

clean_all() {
  rm -r $SOURCE_DIR
  mkdir -p $WORK_DIR
  mkdir -p $SOURCE_DIR
}

download_es() {
  if [ ! -f v${VERSION}.zip ] ; then
    wget $ES_SOURCE_URL
  fi
  if [ ! -d $WORK_DIR/$ES_DIR ] ; then
    unzip -d $WORK_DIR v${VERSION}.zip
  fi
}

copy_classfiles() {
  for target in `cat $CLASS_LIST` ; do
    if [ "x$target" = "x" ] ; then
      continue
    fi
    if [[ "$target" =~ ^#.* ]] ; then
      continue
    fi

    src_paths=$ES_SOURCE_DIR/`echo $target | sed -e s#\\\\.#/#g`
    if [ -f ${src_paths}.java ] ; then
      src_paths=${src_paths}.java
    elif [ -d $src_paths ] ; then
      src_paths=`find $src_paths -type f | grep \.java$`
    else
      src_paths=`echo $src_paths | sed -e 's#/[^/]*$#.java#'`
      while [ ! -f ${src_paths} ] ; do
        src_paths=`echo $src_paths | sed -e 's#/[^/]*$#.java#'`
        echo "Finding: $src_paths"
        if [ x"$src_paths" = "x.java" ] ; then
          echo "Not found: $target"
          break
        fi
      done
      if [ x"$src_paths" = "x.java" ] ; then
        continue
      fi
    fi

    for src_path in $src_paths ; do
      dst_path=`echo "$src_path" | sed -e s#$ORIG_DIR#$DST_DIR#g -e s#$ES_SOURCE_DIR#$SOURCE_DIR#g`
      mkdir -p `dirname $dst_path`
      echo "Generate $dst_path"
      sed -e "s/$ORIG_PACKAGE/$DST_PACKAGE/g" $src_path > $dst_path
    done
  done
}

clean_all
download_es

MAX_NUM=0
COUNT=1
while [ $COUNT -lt 21 ] ; do
  echo "Epoch $COUNT"
  copy_classfiles
  find $BASE_DIR/src/main/java/ -type f \
    | xargs grep "^import .*$DST_PACKAGE" \
    | sed -e "s/ static//" -e "s/.*import \(.*\);/\1/" -e "s/$DST_PACKAGE/$ORIG_PACKAGE/g" \
    | sort -u > $CLASS_LIST
  NUM=`wc -l $CLASS_LIST | awk '{ print $1 }'`
  echo "Remaining "`expr $NUM - $MAX_NUM`" classes"
  if [ $NUM = $MAX_NUM ] ; then
    echo "Finished at Epoch $COUNT"
    break
  fi
  MAX_NUM=$NUM
  COUNT=`expr $COUNT + 1`
done

mvn clean package
