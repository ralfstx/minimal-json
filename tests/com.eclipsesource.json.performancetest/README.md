Performance Tests for Mini JSON Parser
======================================

Download libraries from Maven central:

    $ ./download-jars.sh

Run tests:

    $ ./caliper.sh com.eclipsesource.json.performancetest.ReadWriteBenchmark -Dvariant=json,gson,jackson,mini
