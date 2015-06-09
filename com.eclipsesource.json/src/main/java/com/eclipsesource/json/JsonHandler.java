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
 * <strong>Experimental:</strong>
 * <p>
 * Handles all parser events while parsing a JSON document. Methods can return arbitrary values to
 * be used by the handler for tracking arrays and objects.
 * </p>
 */
interface JsonHandler {

  /**
   * The most abstract kind of JSON array.
   * <p>
   * Implementations other than {@link JsonArray} may extract content from or filter array
   * elements as a means to reduce memory footprint and increased performance.
   * </p>
   */
  public abstract class ElementList extends JsonValue {

    /**
     * Called when an element of a JSON array has been parsed from the JSON stream.
     *
     * @param value
     *          a parsed element of the current JSON array
     * @throws IOException
     *          forwarded from failed skipping reads
     */
    abstract protected void addElement( JsonValue value, ParserContext context ) throws IOException;

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
  public abstract class MemberSet extends JsonValue {

    /**
     * Called when a member of a JSON object has been parsed from the JSON stream.
     *
     * @param name
     *          the field name of the object's member
     * @param value
     *          the JSON value of the object's member
     * @throws IOException
     *          forwarded from failed skipping reads
     */
    abstract protected void addMember( String name, JsonValue value, ParserContext context )
        throws IOException;

    /**
     * Unless implemented, replaces the JSON object with a stripping note.
     */
    @Override
    void write(JsonWriter writer) throws IOException {
      writer.writeString("Compacted JSON object.");
    }
  }

  /**
   * Handle a JSON literal.
   * @param literal
   * @param context
   * @return An object to pass to the caller, or <code>null</code>
   */
  JsonValue handleLiteral(JsonValue literal, ParserContext context);

  /**
   * Handle a JSON string.
   * @param begin the character index of the start position
   * @param end the character index that follows the string
   * @param string the string that has been read
   * @param context
   * @return An object to pass to the caller, or <code>null</code>
   */
  JsonValue handleString(String string, int begin, ParserContext context);

  /**
   * Handle a JSON number.
   * @param number the string that represents a JSON number
   * @param begin the character index of the start position
   * @param context
   * @return An object to pass to the caller, or <code>null</code>
   */
  JsonValue handleNumber(String number, int begin, ParserContext context);

  /**
   * Handle the start of a JSON array. This method can return an object for tracking this array.
   * This object will be passed to the next call to handleArrayElement or handleArrayEnd.
   * @param context
   *          the character index of the array's opening bracket
   * @return An object to be passed to the next handleArrayElement or handleArrayEnd call
   */
  JsonHandler.ElementList handleArrayStart(ParserContext context);

  /**
   * Handle the end of a JSON array.
   * @param begin the character index of opening bracket
   * @param end the character index that follows the closing bracket
   * @param array
   *          the object returned by the previous handleArrayStart method
   * @param context
   * @return An object to pass to the caller, or <code>null</code>
   */
  JsonHandler.ElementList handleArrayEnd(JsonHandler.ElementList array, ParserContext context);

  /**
   * Handle the start of a JSON object. This method can return an object used to track this
   * object. This object will be passed to the next call to handleObjectMember or handleObjectEnd.
   *
   * @param context
   *          the character index of the array's opening bracket
   * @return An object to be passed to the next handleObjectMember or handleObjectEnd call
   */
  JsonHandler.MemberSet handleObjectStart(ParserContext context);

  /**
   * Handle the name of a new member of a JSON object. This method is called after the name has
   * been parsed.
   *
   * @param name
   *          the name of this member
   * @param begin the first character index of the member name
   * @param context
   */
  void handleMemberName(String name, int begin, ParserContext context);

  /**
   * Handle the end of a JSON object.
   *
   * @param begin
   *          the character index of opening brace
   * @param end
   *          the character index that follows the closing brace
   * @param object
   *          the object returned by the previous handleObjectStart or handlerObjectMember method
   * @param context
   * @return An object to pass to the caller, or <code>null</code>
   */
  JsonHandler.MemberSet handleObjectEnd(JsonHandler.MemberSet object, ParserContext context);

}
