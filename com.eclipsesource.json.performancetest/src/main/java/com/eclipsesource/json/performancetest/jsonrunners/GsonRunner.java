package com.eclipsesource.json.performancetest.jsonrunners;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import com.google.gson.Gson;


public class GsonRunner implements JsonRunner {

  private final Gson gson;

  public GsonRunner() {
    gson = new Gson();
  }

  @Override
  public Object readFromString( String string ) throws Exception {
    return gson.fromJson( string, Map.class );
  }

  @Override
  public Object readFromReader( Reader reader ) throws Exception {
    return gson.fromJson( reader, Map.class );
  }

  @Override
  public String writeToString( Object model ) throws Exception {
    return gson.toJson( model );
  }

  @Override
  public void writeToWriter( Object model, Writer writer ) throws Exception {
    gson.toJson( model, writer );
  }

}
