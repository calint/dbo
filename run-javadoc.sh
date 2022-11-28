#!/bin/sh
set -e
DIR=$(dirname "$0")
cd $DIR
javadoc -d doc/ -sourcepath src db
