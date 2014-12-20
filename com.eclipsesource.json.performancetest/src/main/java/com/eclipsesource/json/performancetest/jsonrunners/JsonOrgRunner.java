package com.eclipsesource.json.performancetest.jsonrunners;

import java.io.Reader;
import java.io.Writer;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class JsonOrgRunner extends JsonRunner {

  @Override
  public Object readFromString( String string ) throws JSONException {
    return new JSONObject( string );
  }

  @Override
  public Object readFromReader( Reader reader ) throws JSONException {
    return new JSONObject( new JSONTokener( reader ) );
  }

  @Override
  public String writeToString( Object model ) {
    return model.toString();
  }

  @Override
  public void writeToWriter( Object model, Writer writer ) throws Exception {
    ((JSONObject)model).write( writer );
  }

}
