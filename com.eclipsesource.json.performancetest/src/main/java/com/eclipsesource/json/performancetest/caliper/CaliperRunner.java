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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.eclipsesource.json.JsonObject;
import com.google.caliper.Arguments;
import com.google.caliper.Benchmark;
import com.google.caliper.Runner;
import com.google.caliper.UserException;
import com.google.caliper.UserException.DisplayUsageException;

import static com.eclipsesource.json.performancetest.resources.Resources.readResource;


/**
 * Runs a caliper test for the given benchmark and saves the results to a file using a generic JSON
 * format. Requires caliper v0.5.
 * @see CaliperResultsPreprocessor
 */
public class CaliperRunner {

  private final Class<? extends Benchmark> benchmark;
  private final Map<String,String[]> parameterDefaults = new HashMap<String,String[]>();
  private final File resultsFile;

  public CaliperRunner( Class<? extends Benchmark> benchmark ) {
    this.benchmark = benchmark;
    this.resultsFile = getResultsFile();
  }

  public void addParameterDefault( String name, String... values ) {
    Object displaced = parameterDefaults.put( name, values );
    assert displaced == null : "duplicate parameter default for \"" + name + "\"";
  }

  public void exec( String[] args ) throws IOException {
    int exitCode = safeRun( args );
    if( exitCode == 0 && resultsFile != null ) {
      createJsonFile();
      copyHtmlResources( resultsFile.getParentFile() );
    }
    System.exit( exitCode ); // cleanup non-daemon threads from user code, see caliper.Runner
  }

  private File getResultsFile() {
    File file = new File( "results/" + getName() + ".json" );
    int i = 0;
    while( file.exists() ) {
      file = new File( "results/" + getName() + "-" + ++i + ".json" );
    }
    return file;
  }

  private String getName() {
    String name = benchmark.getSimpleName();
    if( name == null ) {
      name = "Unknown";
    }
    return name;
  }

  private void createJsonFile() throws IOException {
    JsonObject caliperJson = JsonObject.readFrom( readFromFile( resultsFile ) );
    String resultsJson = new CaliperResultsPreprocessor( caliperJson ).getResults().toString();
    writeToFile( resultsJson, resultsFile );
  }

  String[] adjustArgs( String[] args ) {
    ArrayList<String> adjustedArgs = new ArrayList<String>();
    adjustedArgs.add( benchmark.getName() );
    adjustedArgs.addAll( Arrays.asList( args ));

    Arguments parsed = Arguments.parse( adjustedArgs.toArray( new String[0] ) );

    // If a param wasn't specified in 'args', use the default value.
    for( Map.Entry<String,String[]> pd : parameterDefaults.entrySet() ) {
      if ( !parsed.getUserParameters().containsKey( pd.getKey() ) ) {
        adjustedArgs.add( "-D" + pd.getKey() + "=" + join( pd.getValue(), "," ) );
      }
    }

    if( resultsFile != null ) {
      if( parsed.getSaveResultsFile() != null ) {
        throw new UserException.DuplicateParameterException( "--saveResults is already used internally" );
      }
      adjustedArgs.add( "--saveResults" );
      adjustedArgs.add( resultsFile.getAbsolutePath() );
    }

    return adjustedArgs.toArray( new String[0] );
  }

  private static void copyHtmlResources( File parent ) throws IOException {
    copyResource( "Benchmarks.html", parent );
    copyResource( "benchmarks.js", parent );
    copyResource( "benchmarks.css", parent );
    copyResource( "parser.css", parent );
  }

  private static void copyResource( String name, File parent ) throws IOException {
    File file = new File( parent, name );
    if( !file.exists() ) {
      writeToFile( readResource( "charts/" + name ), file );
    }
  }

  private int safeRun( String[] args ) {
    // see caliper.Runner.main( String[] )
    try {
      new Runner().run( adjustArgs( args ) );
      return 0;
    } catch( DisplayUsageException e ) {
      e.display();
      return 0;
    } catch( UserException e ) {
      e.display();
      return 1;
    }
  }

  private static void writeToFile( String string, File file ) throws IOException {
    OutputStreamWriter writer = new OutputStreamWriter( new FileOutputStream( file ) );
    try {
      writer.write( string );
    } finally {
      writer.close();
    }
  }

  private static String readFromFile( File file ) throws IOException {
    FileInputStream inputStream = new FileInputStream( file );
    try {
      return readContent( inputStream, "UTF-8" );
    } finally {
      inputStream.close();
    }
  }

  private static String readContent( InputStream inputStream, String charset )
      throws UnsupportedEncodingException, IOException
  {
    BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream, charset ) );
    StringBuilder builder = new StringBuilder();
    String line = reader.readLine();
    while( line != null ) {
      builder.append( line );
      builder.append( '\n' );
      line = reader.readLine();
    }
    return builder.toString();
  }

  private static String join( String[] parts, String glue ) {
    StringBuilder buffer = new StringBuilder();
    for( int i = 0; i < parts.length; i++ ) {
      if( i > 0 ) {
        buffer.append( glue );
      }
      buffer.append( parts[i] );
    }
    return buffer.toString();
  }
}
