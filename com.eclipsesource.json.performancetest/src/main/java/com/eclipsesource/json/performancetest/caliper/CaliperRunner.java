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
import java.util.List;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
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
  private final List<String> options = new ArrayList<String>();
  private final List<String> parameters = new ArrayList<String>();
  private File resultsFile;

  public CaliperRunner( Class<? extends Benchmark> benchmark ) {
    this.benchmark = benchmark;
    this.resultsFile = new File( "results/" + getName() + ".json" );
  }

  public void setResultsFile( File resultsFile ) {
    this.resultsFile = resultsFile;
  }

  public void addParameter( String name, String... values ) {
    parameters.add( name );
    options.add( "-D" + name + "=" + join( values, "," ) );
  }

  public void exec() throws IOException {
    int exitCode = safeRun( getOptions().toArray( new String[0] ) );
    if( exitCode == 0 && resultsFile != null ) {
      replaceJsonFile();
      createHtmlFile();
    }
    System.exit( exitCode ); // cleanup non-daemon threads from user code, see caliper.Runner
  }

  private String getName() {
    String name = benchmark.getSimpleName();
    if( name == null ) {
      name = "Unknown";
    }
    return name;
  }

  private void replaceJsonFile() throws IOException {
    JsonObject caliperJson = JsonObject.readFrom( readFromFile( resultsFile ) );
    String resultsJson = new CaliperResultsPreprocessor( caliperJson ).getResults().toString();
    writeToFile( resultsJson, resultsFile );
  }

  private void createHtmlFile() throws IOException {
    File htmlFile = getHtmlFile();
    if( !htmlFile.exists() ) {
      File parent = htmlFile.getParentFile();
      parent.mkdirs();
      copyResource( "benchmarks.js", parent );
      copyResource( "benchmarks.css", parent );
      String template = readResource( "charts/template.html" );
      String html = replaceTemplate( template );
      writeToFile( html, htmlFile );
    }
  }

  private static void copyResource( String name, File parent ) throws IOException {
    File file = new File( parent, name );
    if( !file.exists() ) {
      writeToFile( readResource( "charts/" + name ), file );
    }
  }

  String replaceTemplate( String template ) {
    return template
      .replace( "{title}", getName() )
      .replace( "{filename}", resultsFile.getName() )
      .replace( "{parameters}", getParametersAsJson() );
  }

  private String getParametersAsJson() {
    JsonArray array = new JsonArray();
    for( String name : parameters ) {
      array.add( name );
    }
    return array.toString();
  }

  private File getHtmlFile() {
    return new File( resultsFile.getAbsolutePath().replaceFirst( ".json$", ".html" ) );
  }

  List<String> getOptions() {
    if( resultsFile != null ) {
      options.add( "--saveResults" );
      options.add( resultsFile.getAbsolutePath() );
    }
    options.add( benchmark.getName() );
    return options;
  }

  private static int safeRun( String[] args ) {
    // see caliper.Runner.main( String[] )
    try {
      new Runner().run( args );
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
