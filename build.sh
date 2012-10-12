#!/bin/sh

if [ ! -d classes ]; 
then
    mkdir classes
fi

javac -deprecation -sourcepath src -classpath classes:$JCE_CLASSPATH -d classes src/net/sourceforge/herald/*.java

if [ -f herald.jar ];
then
    rm -f herald.jar.old
    mv herald.jar herald.jar.old
fi

jar cfm herald.jar src/MANIFEST COPYING sounds \
	-C classes net \
	-C src MessagesBundle_en.properties \
	-C src Version.properties
