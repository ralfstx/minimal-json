#!/bin/sh

CLASSPATH=

addPath() {
  test -e $1 || echo "Not found: $1"
  CLASSPATH=$CLASSPATH:$1
}

export PATH=$PATH:$JAVA_HOME/bin
base=`dirname $0`

for f in $base/lib/*.jar; do
  addPath $f
done

addPath $base/bin
addPath $base/../../bundles/com.eclipsesource.json/bin

exec java -cp $CLASSPATH com.google.caliper.Runner "$@"
