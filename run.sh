#!/bin/sh
set -e
DIR=$(dirname "$0")
cd $DIR &&
#cmd="java -cp bin`ls $DIR/lib|while read f;do echo -n :$DIR/lib/$f;done` main.Main $*" &&
cmd="java -cp bin:lib/mysql-connector-java-5.1.49.jar main.Main $*" &&
echo \> $cmd;$cmd
