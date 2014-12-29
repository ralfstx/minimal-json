/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.eclipsesource.json.performancetest.caliper;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;


/**
 * Transforms caliper v0.5 results JSON files into a small, generic data structure.
 */
class CaliperResultsPreprocessor {

// Caliper JSON structure example:
// -------------------------------
// {}
//   run: {}
//     benchmarkName: "com.eclipsesource.json.performancetest.ReadWriteBenchmark"
//     executedTimestamp: "2013-08-29T15:17:27UTC"
//     measurements: []
//       {}
//         k: {}
//           variables: {}
//             vm: "java"
//             trial: "0"
//             benchmark: "ReadFromString"
//             input: "long-string"
//             parser: "org-json"
//         v: {}
//           measurementSetMap: {}
//             TIME: {}
//               measurements: []
//                 {}
//                   raw: 5640.85597996243
//                   processed: 5640.85597996243
//                   unitNames: {}
//                     ns: 1
//                     s: 1000000000
//                     ms: 1000000
//                     us: 1000
//                 ...
//               unitNames: {}
//                 ns: 1
//                 s: 1000000000
//                 ms: 1000000
//                 us: 1000
//               systemOutCharCount: 0
//               systemErrCharCount: 0
//           eventLogMap: {}
//             TIME: "starting Scenario{vm=java, trial=0, benchmark=ReadFromString, input=...
//       ...
//   environment: {}
//     propertyMap: {}
//       jre.vmname: "Java HotSpot(TM) 64-Bit Server VM"
//       host.cpu.names: "[Intel(R) Core(TM) i5-3320M CPU @ 2.60GHz x 4]"
//       jre.version: "1.6.0_32-ea-b03"
//       host.cpu.cores: "[2 x 4]"
//       jre.availableProcessors: "4"
//       os.name: "Linux"
//       jre.vmversion: "20.7-b02"
//       host.memory.swap: "[6183932 kB]"
//       os.version: "3.5.0-25-generic"
//       host.cpu.cachesize: "[3072 KB x 4]"
//       host.cpus: "4"
//       host.name: "kelvin"
//       os.arch: "amd64"
//       host.memory.physical: "[7871592 kB]"
//
// Corresponding output:
// ---------------------
// {}
//   name: "ReadWriteBenchmark"
//   details: {}
//     benchmark.classname: "com.eclipsesource.json.performancetest.ReadWriteBenchmark"
//     benchmark.executionTime: "2013-08-29T15:17:27UTC"
//     jre.vmname: "Java HotSpot(TM) 64-Bit Server VM"
//     host.cpu.names: "[Intel(R) Core(TM) i5-3320M CPU @ 2.60GHz x 4]"
//     jre.version: "1.6.0_32-ea-b03"
//     host.cpu.cores: "[2 x 4]"
//     jre.availableProcessors: "4"
//     os.name: "Linux"
//     jre.vmversion: "20.7-b02"
//     host.memory.swap: "[6183932 kB]"
//     os.version: "3.5.0-25-generic"
//     host.cpu.cachesize: "[3072 KB x 4]"
//     host.cpus: "4"
//     host.name: "kelvin"
//     os.arch: "amd64"
//     host.memory.physical: "[7871592 kB]"
//   measurements: []
//     {}
//       variables: {}
//         vm: "java"
//         trial: "0"
//         benchmark: "ReadFromString"
//         input: "long-string"
//         parser: "org-json"
//       units: {}
//         ns: 1
//         s: 1000000000
//         ms: 1000000
//         us: 1000
//       values: []
//         5640.85597996243
//         ...
//     ...

  private final JsonObject results;

  CaliperResultsPreprocessor( JsonObject results ) {
    this.results = transformResults( results );
  }

  JsonObject getResults() {
    return new JsonObject( results );
  }

  private static JsonObject transformResults( JsonObject caliperResults ) {
    return new JsonObject()
      .add( "name", extractSimpleName( caliperResults ) )
      .add( "details", extractEnvironment( caliperResults ) )
      .add( "measurements", extractMeasurements( caliperResults ) );
  }

  private static JsonValue extractBenchmarkName( JsonObject caliperResults ) {
    return caliperResults.get( "run" ).asObject().get( "benchmarkName" );
  }

  private static JsonValue extractSimpleName( JsonObject caliperResults ) {
    String name = caliperResults.get( "run" ).asObject().get( "benchmarkName" ).asString();
    return JsonValue.valueOf( name.replaceFirst( ".*\\.", "" ) );
  }

  private static JsonValue extractTimestamp( JsonObject caliperResults ) {
    return caliperResults.get( "run" ).asObject().get( "executedTimestamp" );
  }

  private static JsonValue extractEnvironment( JsonObject caliperResults ) {
    JsonObject details = caliperResults.get( "environment" ).asObject()
      .get( "propertyMap" ).asObject();
    details.add( "benchmark.classname", extractBenchmarkName( caliperResults ) );
    details.add( "benchmark.executionTime", extractTimestamp( caliperResults ) );
    return details;
  }

  private static JsonArray extractMeasurements( JsonObject caliperResults ) {
    JsonArray result = new JsonArray();
    JsonArray measurements = caliperResults.get( "run" ).asObject().get( "measurements" ).asArray();
    for( JsonValue measurement : measurements ) {
      result.add( extractMeasurement( measurement.asObject() ) );
    }
    return result;
  }

  private static JsonObject extractMeasurement( JsonObject measurement ) {
    JsonObject times = measurement.get( "v" ).asObject()
      .get( "measurementSetMap" ).asObject()
      .get( "TIME" ).asObject();
    return new JsonObject()
      .add( "variables", measurement.get( "k" ).asObject().get( "variables" ) )
      .add( "units", times.get( "unitNames" ) )
      .add( "values", extractTimes( times.get( "measurements" ).asArray() ) );
  }

  private static JsonValue extractTimes( JsonArray measurements ) {
    JsonArray result = new JsonArray();
    for( JsonValue measurement : measurements ) {
      result.add( measurement.asObject().get( "processed" ) );
    }
    return result;
  }

}
