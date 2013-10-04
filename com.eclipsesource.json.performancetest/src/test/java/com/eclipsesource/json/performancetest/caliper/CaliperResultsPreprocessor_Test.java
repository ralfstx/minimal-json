/*******************************************************************************
 * Copyright (c) 2013 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Sternberg - initial implementation and API
 ******************************************************************************/
package com.eclipsesource.json.performancetest.caliper;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.performancetest.resources.Resources;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;


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
