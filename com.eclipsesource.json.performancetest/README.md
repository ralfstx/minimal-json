Performance Tests for minimal-json
==================================

Download libraries from Maven central
-------------------------------------

    $ ./download-jars.sh

TODO: use maven to download the dependencies defined in pom file

Run tests
---------

Either use the benchmark's main methods, or caliper from the command line:

    $ ./caliper.sh com.eclipsesource.json.performancetest.ReadWriteBenchmark \
      -Dparser=org-json,gson,jackson,json-simple,minimal-json \
      -Dinput=long-string,numbers-array \
      --saveResults results.json

After running, copy `results.json` into `results.html` to update the chart.

Input files
-----------

Input files can be selected using `-Dinput=<name>` where name is the filename without extension.
The files reside in `/src/main/resources`.

Some of them are real-world examples:

* `rap`: an exemplary RAP protocol message (minimal-json is used by the [RAP](http://eclipse.org/rap)
  project)
* `caliper`: an exemplary caliper output file, rather big (> 80kB) and contains some very long strings

and some are minimal JSON files to distiguish different challenges:

* `long-string`
* `numbers-array`
