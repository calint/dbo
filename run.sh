#!/bin/sh
set -e
DIR=$(dirname "$0")
cd $DIR &&
cmd="java -cp bin`ls $DIR/lib|while read f;do echo -n :;echo -n $DIR/lib/$f;done` main.Main $*" &&
echo \> $cmd;$cmd
