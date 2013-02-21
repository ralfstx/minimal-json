package com.eclipsesource.json.performancetest.jsonrunners;

import org.json.JSONException;
import org.json.JSONObject;


public class JsonOrgRunner implements JsonRunner {

  @Override
  public Object read( String json ) {
    try {
      return new JSONObject( json );
    } catch( JSONException exception ) {
      throw new RuntimeException( exception );
    }
  }

  @Override
  public String write( Object model ) {
    return model.toString();
  }

}
