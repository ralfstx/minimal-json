#!/bin/sh

download() {
  wget -O lib/`basename $1` $file http://search.maven.org/remotecontent?filepath=$1
}

download com/google/caliper/caliper/0.5-rc1/caliper-0.5-rc1.jar
download com/google/code/java-allocation-instrumenter/java-allocation-instrumenter/2.1/java-allocation-instrumenter-2.1.jar
download com/google/guava/guava/r09/guava-r09.jar
download com/google/code/gson/gson/2.2.2/gson-2.2.2.jar
download org/json/json/20090211/json-20090211.jar
download org/codehaus/jackson/jackson-core-asl/1.9.12/jackson-core-asl-1.9.12.jar
download org/codehaus/jackson/jackson-mapper-asl/1.9.12/jackson-mapper-asl-1.9.12.jar
download com/googlecode/json-simple/json-simple/1.1.1/json-simple-1.1.1.jar
