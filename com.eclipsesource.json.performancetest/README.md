Performance Tests for minimal-json
==================================

Download required libraries
---------------------------

    $ cd com.eclipsesource.json.performancetest
    $ mvn -DoutputDirectory=lib dependency:copy-dependencies

This command copies all dependencies to `lib/`.

Run benchmarks
--------------

To run a benchmark, use the benchmark's main method. This will create a file `Benchmark.json` in
the `results` folder, where `Benchmark` is the benchmark's simple class name.

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
