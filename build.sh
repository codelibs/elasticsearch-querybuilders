#!/bin/bash

export LANG=en_US.UTF-8
export LC_ALL=en_US.UTF-8

VERSION=5.2.1

ES_DIR=elasticsearch-${VERSION}
ES_SOURCE_URL=https://github.com/elastic/elasticsearch/archive/v${VERSION}.zip
WORK_DIR=work
SOURCE_DIR=src/main/java
ES_SOURCE_DIR=$WORK_DIR/$ES_DIR/core/src/main/java

ORIG_PACKAGE="org.elasticsearch"
DST_PACKAGE="org.codelibs.elasticsearch"
ORIG_DIR=`echo $ORIG_PACKAGE | sed -e s#\\\\.#/#g`
DST_DIR=`echo $DST_PACKAGE | sed -e s#\\\\.#/#g`

rm -r $SOURCE_DIR
mkdir -p $WORK_DIR
mkdir -p $SOURCE_DIR

# Download zip
if [ ! -f v${VERSION}.zip ] ; then
  wget $ES_SOURCE_URL
fi
if [ ! -d $WORK_DIR/$ES_DIR ] ; then
  unzip -d $WORK_DIR v${VERSION}.zip
fi


COPY_LIST=`cat querybuilders-classes.list`

PRE_IFS=$IFS
IFS=$'\n'

echo "Start copying source codes..."
for target in $COPY_LIST
do
  if [ "x$target" = "x" ] ; then
    continue
  fi
  if [[ "$target" =~ ^#.* ]] ; then
    continue
  fi

  src_path=$ES_SOURCE_DIR/`echo $target | sed -e s#\\\\.#/#g`
  if [ -f $src_path.java ] ; then
    src_path=$src_path.java
  elif [ -d $src_path ] ; then
    src_path="$src_path/*"
  else
    echo "does not exist. $path"
    continue;
  fi
  dst_path=`echo "$src_path" | sed -e s#$ORIG_DIR#$DST_DIR#g | sed -e s#$ES_SOURCE_DIR#$SOURCE_DIR#g | sed -e s#\\\\*##g`
  path_dir=`echo "$dst_path" | sed -e s#/[^/]*\\\\.java##g`
  mkdir -p $path_dir
  echo "cp -r $src_path $dst_path"
  cp -r $src_path $dst_path
done

echo "Start replacing package..."
SOURCE_FILE_LIST=`find $SOURCE_DIR -name "*.java"`
for target in $COPY_LIST
do
  if [ "x$target" = "x" ] ; then
    continue
  fi
  if [[ "$target" =~ ^#.* ]] ; then
    continue
  fi

  dst_package=`echo "$target" | sed -e s#$ORIG_PACKAGE#$DST_PACKAGE#g`
  echo "replacing source code. $target to $dst_package"
  for source_file in $SOURCE_FILE_LIST
  do
    sed -i '_sedbk' -e s/$target/$dst_package/g $source_file
  done
done

for source_file in $SOURCE_FILE_LIST
do
  sed -i '_sedbk' -e "s/package $ORIG_PACKAGE/package $DST_PACKAGE/g" $source_file
done

IFS=$PRE_IFS

echo "Finished."

find . -name "*_sedbk" | xargs rm

mvn clean package
exit;
