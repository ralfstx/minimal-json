/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
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
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.google.caliper.SimpleBenchmark;


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

    String[] options = runner.adjustArgs( new String[0] );

    List<String> expected = asList( TestBenchmark.class.getName(),
                                    "-Dfoo=foo1,foo2",
                                    "-Dbar=bar1,bar2",
                                    "--saveResults",
                                    new File( "results/TestBenchmark.json" ).getAbsolutePath() );
    assertEquals( expected, asList( options ) );
  }

  @Test
  public void caliperArguments_withOverriddenParameter() {
    CaliperRunner runner = new CaliperRunner( TestBenchmark.class );
    runner.addParameterDefault( "foo", "foo1", "foo2" );
    runner.addParameterDefault( "bar", "bar1", "bar2" );

    String[] options = runner.adjustArgs( new String[] {"-Dbar=bar2"} );

    List<String> expected = asList( TestBenchmark.class.getName(),
                                    "-Dbar=bar2",
                                    "-Dfoo=foo1,foo2",
                                    "--saveResults",
                                    new File( "results/TestBenchmark.json" ).getAbsolutePath() );
    assertEquals( expected, asList( options ) );
  }

  private static class TestBenchmark extends SimpleBenchmark {}

}
