Performance Tests for Mini JSON Parser
======================================

Download libraries from Maven central:

    $ ./download-jars.sh

Run tests:

    $ ./caliper.sh com.eclipsesource.json.performancetest.ReadWriteBenchmark \
      --saveResults results.json -Dvariant=json,gson,jackson,mini

copy results.json into results.html to update chart
