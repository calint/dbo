#!/bin/sh

DIR=$(dirname "$0")

cd $DIR/src/db &&
	echo wc && 
	ls | while read f; do cat $f 2>/dev/null; done | 
		sed -r '/^\s*$/d' | 
		sed -e '/^import /d' | 
		sed -e '/^package /d' |
		sed -r '/^\s*\/\//d' | 
	wc &&

	echo wc gzip &&
	ls | while read f; do cat $f 2>/dev/null; done | 
		sed -r '/^\s*$/d' | 
		sed -e '/^import /d' | 
		sed -e '/^package /d' |
		sed -r '/^\s*\/\//d' | 
	gzip | wc
