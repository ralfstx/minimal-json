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

import java.io.Reader;
import java.io.Writer;


public interface JsonRunner {

  public abstract Object readFromString( String string ) throws Exception;

  public abstract Object readFromReader( Reader reader ) throws Exception;

  public abstract String writeToString( Object model ) throws Exception;

  public abstract void writeToWriter( Object model, Writer writer ) throws Exception;

}
