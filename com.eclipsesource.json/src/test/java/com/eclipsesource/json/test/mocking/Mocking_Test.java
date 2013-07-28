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
package com.eclipsesource.json.test.mocking;

import org.junit.Test;
import org.mockito.Mockito;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;

import static org.junit.Assert.assertNotNull;


/**
 * Make sure types do not prevent mocking by final or visibility constructs.
 */
public class Mocking_Test {

  @Test
  public void mockValue() {
    JsonValue jsonValue = Mockito.mock( JsonValue.class );

    assertNotNull( jsonValue );
  }

  @Test
  public void mockObject() {
    JsonObject jsonObject = Mockito.mock( JsonObject.class );

    assertNotNull( jsonObject );
  }

  @Test
  public void mockArray() {
    JsonArray jsonArray = Mockito.mock( JsonArray.class );

    assertNotNull( jsonArray );
  }

  @Test
  public void mockParseException() {
    Mockito.mock( ParseException.class );
  }

}
