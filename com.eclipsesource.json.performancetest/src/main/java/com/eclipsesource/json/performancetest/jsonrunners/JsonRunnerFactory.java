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
package com.eclipsesource.json.performancetest.jsonrunners;


public class JsonRunnerFactory {

  public static JsonRunner findByName( String name ) {
    if( "null".equals( name ) ) {
      return new NullRunner();
    } else if( "org-json".equals( name ) ) {
      return new JsonOrgRunner();
    } else if( "gson".equals( name ) ) {
      return new GsonRunner();
    } else if( "jackson".equals( name ) ) {
      return new JacksonRunner();
    } else if( "json-simple".equals( name ) ) {
      return new SimpleRunner();
    } else if( "minimal-json".equals( name ) ) {
      return new MinimalJsonRunner();
    } else {
      throw new IllegalArgumentException( "Unknown parser: " + name );
    }
  }

}
