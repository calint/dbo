#!/bin/sh
# this builds and runs in java 1.5
# download jdk1.5
#   https://www.oracle.com/java/technologies/java-archive-javase5-downloads.html
#   https://download.oracle.com/otn/java/jdk/1.5.0_22/jdk-1_5_0_22-linux-amd64.bin
# and then
#   export PATH=<jdk1.5 directory>/bin:$PATH
# and then
set -e
DIR=$(dirname "$0")
cd $DIR
rm -rf bin
mkdir bin
find src -type f > sources.txt
javac -verbose -d bin @sources.txt
rm sources.txt
java -cp bin:lib/mysql-connector-java-5.1.49.jar main.Main
