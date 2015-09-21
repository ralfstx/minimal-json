/*******************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class JsonHelper {
	public static Object jsonValueAsObject(JsonValue value) {
		if(value.isBoolean())
			return value.asBoolean();
		else if(value.isNumber())
			return value.asInt();
		else if(value.isString())
			return value.asString();
		else if(value.isArray())
			return jsonArrayAsList(value.asArray());
		else if(value.isObject())
			return jsonObjectAsMap(value.asObject());
		else return null;
	}
	public static Set<Object> jsonArrayAsSet(JsonArray array) {
		Set<Object> set = new HashSet<>();
		for(JsonValue value:array)
			set.add(jsonValueAsObject(value));
		return set;
	}
	public static List<Object> jsonArrayAsList(JsonArray array) {
		List<Object> list = new ArrayList<>();
		for(JsonValue element:array)
			list.add(jsonValueAsObject(element));
		return list;
	}
	public static Map<String,Object> jsonObjectAsMap(JsonObject object) {
		Map<String,Object> map = new HashMap<>();
		for(JsonObject.Member member:object)
			map.put(member.getName(), jsonValueAsObject(member.getValue()));
		return map;		
	}
	
	public static JsonValue objectAsJsonValue(Object object) {
		if(object==null)
			return Json.NULL;
		else if(object instanceof Boolean)
			return Json.value((Boolean)object);
		else if(object instanceof Integer)
			return Json.value((Integer)object);
		else if(object instanceof Long)
			return Json.value((Long)object);
		else if(object instanceof Float)
			return Json.value((Float)object);
		else if(object instanceof Double)
			return Json.value((Double)object);
		else if(object instanceof String)
			return Json.value((String)object);
		else if(object instanceof Collection)
			return collectionAsJsonArray((Collection<?>)object);
		else if(object instanceof Map)
			return mapAsJsonObject((Map<?,?>)object);
		else return null;
	}
	public static JsonArray collectionAsJsonArray(Collection<?> collection) {
		JsonArray array = new JsonArray();
		for(Object element:collection)
			array.add(objectAsJsonValue(element));
		return array;
	}
	public static JsonObject mapAsJsonObject(Map<?,?> map) {
		JsonObject object = new JsonObject();
		for(Entry<?,?> entry:map.entrySet())
			object.add(String.valueOf(entry.getKey()),
				objectAsJsonValue(entry.getValue()));
		return object;
	}
}