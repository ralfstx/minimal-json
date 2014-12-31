Performance Tests for minimal-json
==================================

Run benchmarks
--------------

Each benchmark is defined in a class that "extends SimpleBenchmark".

    $ mvn compile
    $ ./run <benchmark-class-name> [caliper-args...]

If you're on Windows:

    $ mvn compile
    $ mvn -DoutputDirectory=lib dependency:copy-dependencies
    $ java -cp target/classes;lib/* <benchmark-class-name> [caliper-args...]

Benchmarks use caliper v0.5-rc1 (v1.0 works different, uploads results to the cloud instead of
creating JSON files). Benchmarks are executed by CaliperRunner. This class delegates to caliper's
runner but transforms the caliper output to a simple, generic JSON format.

Display test results
--------------------

Along with the results, the HTML file `Benchmarks.html` is copied into the `results` directory
to display the results in nice charts.
This HTML file requires `d3.v3.min.js`, copy this file from `http://d3js.org/d3.v3.min.js` into the
`results` folder.

Some browsers may require the results directory to be mirrored by a web server, Firefox does not.

Input files
-----------

Input files can be selected using `-Dinput=<name>` where name is the filename without extension.
The files reside in `/src/main/resources/input`.

Some of them are real-world examples:

* `rap`: an exemplary RAP protocol message (minimal-json is used by the [RAP](http://eclipse.org/rap)
  project)
* `caliper`: an exemplary caliper output file, rather big (> 80kB) and contains some very long strings

and some are minimal JSON files to distinguish different challenges:

* `long-string`
* `numbers-array`
