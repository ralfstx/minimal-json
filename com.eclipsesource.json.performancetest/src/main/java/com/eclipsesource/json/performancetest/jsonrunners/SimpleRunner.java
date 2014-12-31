package com.eclipsesource.json.performancetest.jsonrunners;

import java.io.Reader;
import java.io.Writer;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class SimpleRunner extends JsonRunner {

  @Override
  public Object readFromString( String json ) {
    return JSONValue.parse( json );
  }

  @Override
  public Object readFromReader( Reader reader ) {
    return JSONValue.parse( reader );
  }

  @Override
  public String writeToString( Object model ) {
    return ((JSONObject) model).toJSONString();
  }

  @Override
  public void writeToWriter( Object model, Writer writer ) throws Exception {
    ((JSONObject) model).writeJSONString( writer );
  }

}
