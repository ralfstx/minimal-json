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
import java.util.Map;

import com.google.gson.Gson;


public class GsonRunner extends JsonRunner {

  private final Gson gson;

  public GsonRunner() {
    gson = new Gson();
  }

  @Override
  public Object readFromString(String string) throws Exception {
    return gson.fromJson(string, Map.class);
  }

  @Override
  public Object readFromReader(Reader reader) throws Exception {
    return gson.fromJson(reader, Map.class);
  }

  @Override
  public String writeToString(Object model) throws Exception {
    return gson.toJson(model);
  }

  @Override
  public void writeToWriter(Object model, Writer writer) throws Exception {
    gson.toJson(model, writer);
  }

}
