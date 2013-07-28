package com.eclipsesource.json.performancetest.jsonrunners;

import org.json.simple.JSONValue;


public class SimpleRunner implements JsonRunner {

  @Override
  public Object read( String json ) {
    return JSONValue.parse( json );
  }

  @Override
  public String write( Object model ) {
    return model.toString();
  }

}
