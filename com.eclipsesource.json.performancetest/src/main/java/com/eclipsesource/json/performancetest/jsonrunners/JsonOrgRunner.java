/*******************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource and others.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.eclipsesource.json.performancetest.jsonrunners;

import java.io.Reader;
import java.io.Writer;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class JsonOrgRunner extends JsonRunner {

  @Override
  public Object readFromString(String string) throws JSONException {
    return new JSONObject(string);
  }

  @Override
  public Object readFromReader(Reader reader) throws JSONException {
    return new JSONObject(new JSONTokener(reader));
  }

  @Override
  public String writeToString(Object model) {
    return model.toString();
  }

  @Override
  public void writeToWriter(Object model, Writer writer) throws Exception {
    ((JSONObject)model).write(writer);
  }

}
