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

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.google.caliper.SimpleBenchmark;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;


public class CaliperRunner_Test {

  @Test
  public void caliperArguments() {
    CaliperRunner runner = new CaliperRunner( TestBenchmark.class );

    String[] options = runner.adjustArgs( new String[0] );

    List<String> expected = asList( TestBenchmark.class.getName(),
                                    "--saveResults",
                                    new File( "results/TestBenchmark.json" ).getAbsolutePath() );
    assertEquals( expected, asList( options ) );
  }

  @Test
  public void caliperArguments_withParameters() {
    CaliperRunner runner = new CaliperRunner( TestBenchmark.class );
    runner.addParameterDefault( "foo", "foo1", "foo2" );
    runner.addParameterDefault( "bar", "bar1", "bar2" );

    {
      String[] options = runner.adjustArgs( new String[0] );

      List<String> expected = asList( TestBenchmark.class.getName(),
                                      "-Dfoo=foo1,foo2",
                                      "-Dbar=bar1,bar2",
                                      "--saveResults",
                                      new File( "results/TestBenchmark.json" ).getAbsolutePath() );
      assertEquals( expected, asList( options ) );
    }

    {
      String[] options = runner.adjustArgs( new String[] {"-Dbar=bar2"} );

      List<String> expected = asList( TestBenchmark.class.getName(),
                                      "-Dbar=bar2",
                                      "-Dfoo=foo1,foo2",
                                      "--saveResults",
                                      new File( "results/TestBenchmark.json" ).getAbsolutePath() );
      assertEquals( expected, asList( options ) );
    }
  }

  private static class TestBenchmark extends SimpleBenchmark {}

}
