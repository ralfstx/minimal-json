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

import java.io.IOException;

/**
 * Provides implementations for JSON array and object representations.
 * <p>
 * Implementations of this interface my choose to return custom implementations of {@code
 * ElementReader} or {@code MemeberReader} as opposed to {@link JsonArray} and {@code JsonObject}
 * respectively, depending on nesting level and/or field name. Providing a custom {@code
 * CollectionFactory} to {@link JsonValue#readFrom} allows clients to effectively use minimal-json
 * as a hybrid streaming API.
 * </p>
 */
public interface CollectionFactory {

  /**
   * The most abstract kind of JSON array.
   * <p>
   * Implementations other than {@link JsonArray} may extract content from or filter array
   * elements as a means to reduce memory footprint and increased performance.
   * </p>
   */
  public abstract class ElementReader extends JsonValue {

	/**
	 * Called when an element of a JSON array has been parsed from the JSON stream.
	 *
	 * @param value
	 *          a parsed element of the current JSON array
	 */
	abstract protected void addElement( JsonValue value, ParserContext context );

	/**
	 * Unless implemented, replaces the JSON array with a stripping note.
	 */
	@Override
	void write(JsonWriter writer) throws IOException {
	  writer.writeString("Compacted JSON array.");
	}
  }

  /**
   * The most abstract kind of JSON array.
   * <p>
   * Implementations other than {@link JsonArray} may extract content from or filter array
   * elements as a means to reduce memory footprint and increased performance.
   * </p>
   */
  public abstract class MemberReader extends JsonValue {

	/**
	 * Called when a member of a JSON object has been parsed from the JSON stream.
	 *
	 * @param name
	 *          the field name of the object's member
	 * @param value
	 *          the JSON value of the object's member
	 */
	abstract protected void addMember( String name, JsonValue value, ParserContext context );

	/**
	 * Unless implemented, replaces the JSON object with a stripping note.
	 */
	@Override
	void write(JsonWriter writer) throws IOException {
	  writer.writeString("Compacted JSON object.");
	}
  }

  /**
   * Implementations must return a new {@code JSONArray} or a custom kind of {@code ElementReader}.
   * <p>
   * A custom {@code ElementReader} can be useful for filtering, extracting from, or annotating the
   * streamed JSON array. Filtering or extracting from the stream may offer performance benefits
   * because not all elements of the JSON array need to be retained in memory in the form of {@code
   * JSONValue} objects. {@code context} provides access to the current parser state, including
   * position in the input stream, nesting level, and field name.
   * </p>
   *
   * @param context
   *          logical and absolute parser state, for deciding what array representation to return
   *
   * @return implementation of {@code ElementReader} depending on nesting and/or field name
   */
  public ElementReader createElementReader(ParserContext context);

  /**
   * Implementations must return a new {@code JSONObject} or a custom kind of {@code MemberReader}.
   * <p>
   * Factory method like {@link #createElementReader} but for object representations.
   * </p>
   *
   * @param context
   *          logical and absolute parser state, for deciding what object representation to return
   *
   * @return implementation of {@code ElementReader} depending on nesting and/or field name
   */
  public MemberReader createMemberReader(ParserContext context);

}
