#!/bin/sh

DIR=$(dirname "$0")

cd $DIR/src/db && 
	ls | while read f; do cat $f 2>/dev/null; done | sed -r '/^\s*$/d' | sed -e '/^import /d' | sed -e '/^package /d' | wc &&
	ls | while read f; do cat $f 2>/dev/null; done | sed -r '/^\s*$/d' | sed -e '/^import /d' | sed -e '/^package /d' | gzip | wc
