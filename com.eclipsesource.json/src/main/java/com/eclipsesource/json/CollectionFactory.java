/*******************************************************************************
 * Copyright (c) 2015 EclipseSource.
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
package com.eclipsesource.json;

/**
 * Provides implementations for JSON array and object representations.
 * <p>
 * Implementations of this interface my choose to return custom implementations of {@code
 * ElementList} or {@code MemeberReader} as opposed to {@link JsonArray} and {@code JsonObject}
 * respectively, depending on nesting level and/or field name. Providing a custom {@code
 * CollectionFactory} to {@link JsonValue#readFrom} allows clients to effectively use minimal-json
 * as a hybrid streaming API.
 * </p>
 */
public abstract class CollectionFactory extends JsonValue.DefaultJsonHandler {

  /**
   * Implementations must return a new {@code JSONArray} or a custom kind of {@code ElementList},
   * or null for building a non-JSON data structure held by the caller.
   * <p>
   * A custom {@code ElementList} can be useful for filtering, extracting from, or annotating the
   * streamed JSON array. Filtering or extracting from the stream may offer performance benefits
   * because not all elements of the JSON array need to be retained in memory in the form of {@code
   * JSONValue} objects. {@code context} provides access to the current parser state, including
   * position in the input, nesting level, and field name.
   * </p>
   * <p>
   * Returning <em>null</em> is only allowed if <em>null</em> was also returned for the root
   * collection.
   * </p>
   *
   * @param context
   *          logical and absolute parser state, for deciding what array representation to return
   * @return implementation of {@code ElementList} or null depending on nesting and/or field name
   */
  @Override
  public abstract ElementList handleArrayStart(ParserContext context);

  /**
   * Implementations must return a new {@code JSONObject} or a custom kind of {@code MemberSet},
   * or null for building a non-JSON data structure held by the caller.
   * <p>
   * Factory interface method like {@link #handleArrayStart} but for object representations.
   * </p>
   *
   * @param context
   *          logical and absolute parser state, for deciding what object representation to return
   * @return implementation of {@code MemberSet} or null depending on nesting and/or field name
   */
  @Override
  public abstract MemberSet handleObjectStart(ParserContext context);

}
