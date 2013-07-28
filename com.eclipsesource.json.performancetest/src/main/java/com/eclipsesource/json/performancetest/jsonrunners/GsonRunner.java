package com.eclipsesource.json.performancetest.jsonrunners;

import java.util.Map;

import com.google.gson.Gson;


public class GsonRunner implements JsonRunner {

  private final Gson gson;

  public GsonRunner() {
    gson = new Gson();
  }

  @Override
  public Object read( String json ) {
    try {
      return gson.fromJson( json, Map.class );
    } catch( Exception exception ) {
      throw new RuntimeException( exception );
    }
  }

  @Override
  public String write( Object model ) {
    try {
      return gson.toJson( model );
    } catch( Exception exception ) {
      throw new RuntimeException( exception );
    }
  }

}
