#!/bin/sh
set -x

rm -rf classes
mkdir classes

cd src

javac -d ../classes com/amd/aparapi/sample/mandel/Main.java

cd ..

#java -ea -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining -XX:+PrintCompilation \
java -ea  \
 -classpath classes \
 com.amd.aparapi.sample.mandel.Main
