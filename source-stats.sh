#!/bin/sh
set -e
DIR=$(dirname "$0")
cd $DIR/src/db
echo source wc 
ls | while read f; do cat $f 2>/dev/null; done | 
	sed -r '/^\s*$/d' | 
	sed -e '/^import /d' | 
	sed -e '/^package /d' |
	sed -r '/^\s*\/\//d' | 
	sed -r '/^\s*}/d' |
	sed -r '/^\s*\/\*\*\s*/d' |
	sed -r '/^\s*\*\s*/d' |
	sed -r '/^\s*@Override\s*/d' |
	wc

echo source gzip wc
ls | while read f; do cat $f 2>/dev/null; done | 
	sed -r '/^\s*$/d' | 
	sed -e '/^import /d' | 
	sed -e '/^package /d' |
	sed -r '/^\s*\/\//d' | 
	sed -r '/^\s*}/d' |
	sed -r '/^\s*\/\*\*\s*/d' |
	sed -r '/^\s*\*\s*/d' |
	sed -r '/^\s*@Override\s*/d' |
	gzip | wc
