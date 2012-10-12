#!/bin/sh

files=`find . -name '*.java' -print`

for i in $files
do
    echo ${i}
    mv ${i} ${i}.old
    tr -d '\r' < ${i}.old | expand -t 4 - > ${i}
done
