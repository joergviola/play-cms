#!/bin/bash
MODULE="cms"
VERSION=`grep self conf/dependencies.yml | sed "s/.*$MODULE //"`
TARGET=/var/www/repo/play-$MODULE/$MODULE-$VERSION.zip

rm -fr dist
play dependencies --sync
play build-module

if [ -e $TARGET ]; then
    echo "Not publishing, $VERSION already exists"
else
    cp dist/*.zip $TARGET
fi
