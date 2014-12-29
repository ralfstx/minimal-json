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

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.performancetest.resources.Resources;


public class CaliperResultsPreprocessor_Test {

  private static JsonObject caliperJson;

  @BeforeClass
  public static void loadCaliperJson() throws IOException {
    caliperJson = JsonObject.readFrom( Resources.readResource( "input/caliper.json" ) );
  }

  @Test
  public void results_structure() {
    CaliperResultsPreprocessor preprocessor = new CaliperResultsPreprocessor( caliperJson );

    JsonObject results = preprocessor.getResults();

    assertEquals( asList( "name", "details", "measurements" ), results.names() );
    assertTrue( results.get( "name" ).isString() );
    assertTrue( results.get( "details" ).isObject() );
    assertTrue( results.get( "measurements" ).isArray() );
  }

  @Test
  public void name_isSimpleName() {
    CaliperResultsPreprocessor preprocessor = new CaliperResultsPreprocessor( caliperJson );

    JsonObject results = preprocessor.getResults();

    String name = results.get( "name" ).asString();
    assertTrue( name.contains( "Benchmark" ) );
    assertFalse( name.contains( "." ) );
  }

  @Test
  public void details_containsEnvironmentVariables() {
    CaliperResultsPreprocessor preprocessor = new CaliperResultsPreprocessor( caliperJson );

    JsonObject results = preprocessor.getResults();

    assertTrue( results.get( "details" ).asObject().names().contains( "os.version" ) );
  }

  @Test
  public void details_containsBenchmarkNameAndTime() {
    CaliperResultsPreprocessor preprocessor = new CaliperResultsPreprocessor( caliperJson );

    JsonObject results = preprocessor.getResults();

    String name = results.get( "details" ).asObject().get( "benchmark.classname" ).asString();
    assertTrue( name.contains( "." ) );
    String time = results.get( "details" ).asObject().get( "benchmark.executionTime" ).asString();
    assertTrue( time.matches( "[\\d-]+T[\\d:]+UTC" ) );
  }

  @Test
  public void measurements_structure() {
    CaliperResultsPreprocessor preprocessor = new CaliperResultsPreprocessor( caliperJson );

    JsonObject results = preprocessor.getResults();

    assertFalse( results.get( "measurements" ).asArray().isEmpty() );
    JsonObject measurement = results.get( "measurements" ).asArray().get( 0 ).asObject();
    assertEquals( asList( "variables", "units", "values" ), measurement.names() );
    assertTrue( measurement.get( "variables" ).asObject().names().contains( "vm" ) );
    assertTrue( measurement.get( "units" ).asObject().names().contains( "ns" ) );
    assertTrue( measurement.get( "values" ).asArray().get( 0 ).isNumber() );
  }

}
