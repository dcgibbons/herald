@echo off
rem
rem Windows Command Script to build Herald
rem

mkdir classes

javac -deprecation -sourcepath src -classpath classes -d classes src\net\sourceforge\herald\*.java

erase herald.jar.old
rename herald.jar herald.jar.old

jar cfm herald.jar src\MANIFEST COPYING sounds \
	-C classes net \
	-C src MessagesBundle_en.properties \
	-C src Version.properties
