// 
// Decompiled by Procyon v0.5.36
// 

package org.json;

import java.util.Iterator;

public class JSONML
{
    private static Object parse(final XMLTokener xmlTokener, final boolean b, final JSONArray jsonArray) throws JSONException {
        while (true) {
            final Object nextContent = xmlTokener.nextContent();
            if (nextContent == XML.LT) {
                final Object nextToken = xmlTokener.nextToken();
                if (nextToken instanceof Character) {
                    if (nextToken == XML.SLASH) {
                        final Object nextToken2 = xmlTokener.nextToken();
                        if (!(nextToken2 instanceof String)) {
                            throw new JSONException("Expected a closing name instead of '" + nextToken2 + "'.");
                        }
                        if (xmlTokener.nextToken() != XML.GT) {
                            throw xmlTokener.syntaxError("Misshaped close tag");
                        }
                        return nextToken2;
                    }
                    else if (nextToken == XML.BANG) {
                        final char next = xmlTokener.next();
                        if (next == '-') {
                            if (xmlTokener.next() == '-') {
                                xmlTokener.skipPast("-->");
                            }
                            xmlTokener.back();
                        }
                        else if (next == '[') {
                            if (!xmlTokener.nextToken().equals("CDATA") || xmlTokener.next() != '[') {
                                throw xmlTokener.syntaxError("Expected 'CDATA['");
                            }
                            if (jsonArray == null) {
                                continue;
                            }
                            jsonArray.put(xmlTokener.nextCDATA());
                        }
                        else {
                            int i = 1;
                            do {
                                final Object nextMeta = xmlTokener.nextMeta();
                                if (nextMeta == null) {
                                    throw xmlTokener.syntaxError("Missing '>' after '<!'.");
                                }
                                if (nextMeta == XML.LT) {
                                    ++i;
                                }
                                else {
                                    if (nextMeta != XML.GT) {
                                        continue;
                                    }
                                    --i;
                                }
                            } while (i > 0);
                        }
                    }
                    else {
                        if (nextToken != XML.QUEST) {
                            throw xmlTokener.syntaxError("Misshaped tag");
                        }
                        xmlTokener.skipPast("?>");
                    }
                }
                else {
                    if (!(nextToken instanceof String)) {
                        throw xmlTokener.syntaxError("Bad tagName '" + nextToken + "'.");
                    }
                    final String s = (String)nextToken;
                    final JSONArray jsonArray2 = new JSONArray();
                    final JSONObject jsonObject = new JSONObject();
                    if (b) {
                        jsonArray2.put(s);
                        if (jsonArray != null) {
                            jsonArray.put(jsonArray2);
                        }
                    }
                    else {
                        jsonObject.put("tagName", s);
                        if (jsonArray != null) {
                            jsonArray.put(jsonObject);
                        }
                    }
                    Object o = null;
                    while (true) {
                        if (o == null) {
                            o = xmlTokener.nextToken();
                        }
                        if (o == null) {
                            throw xmlTokener.syntaxError("Misshaped tag");
                        }
                        if (!(o instanceof String)) {
                            if (b && jsonObject.length() > 0) {
                                jsonArray2.put(jsonObject);
                            }
                            if (o == XML.SLASH) {
                                if (xmlTokener.nextToken() != XML.GT) {
                                    throw xmlTokener.syntaxError("Misshaped tag");
                                }
                                if (jsonArray != null) {
                                    break;
                                }
                                if (b) {
                                    return jsonArray2;
                                }
                                return jsonObject;
                            }
                            else {
                                if (o != XML.GT) {
                                    throw xmlTokener.syntaxError("Misshaped tag");
                                }
                                final String str = (String)parse(xmlTokener, b, jsonArray2);
                                if (str == null) {
                                    break;
                                }
                                if (!str.equals(s)) {
                                    throw xmlTokener.syntaxError("Mismatched '" + s + "' and '" + str + "'");
                                }
                                if (!b && jsonArray2.length() > 0) {
                                    jsonObject.put("childNodes", jsonArray2);
                                }
                                if (jsonArray != null) {
                                    break;
                                }
                                if (b) {
                                    return jsonArray2;
                                }
                                return jsonObject;
                            }
                        }
                        else {
                            final String s2 = (String)o;
                            if (!b && (s2 == "tagName" || s2 == "childNode")) {
                                throw xmlTokener.syntaxError("Reserved attribute.");
                            }
                            o = xmlTokener.nextToken();
                            if (o == XML.EQ) {
                                final Object nextToken3 = xmlTokener.nextToken();
                                if (!(nextToken3 instanceof String)) {
                                    throw xmlTokener.syntaxError("Missing value");
                                }
                                jsonObject.accumulate(s2, JSONObject.stringToValue((String)nextToken3));
                                o = null;
                            }
                            else {
                                jsonObject.accumulate(s2, "");
                            }
                        }
                    }
                }
            }
            else {
                if (jsonArray == null) {
                    continue;
                }
                jsonArray.put((nextContent instanceof String) ? JSONObject.stringToValue((String)nextContent) : nextContent);
            }
        }
    }
    
    public static JSONArray toJSONArray(final String s) throws JSONException {
        return toJSONArray(new XMLTokener(s));
    }
    
    public static JSONArray toJSONArray(final XMLTokener xmlTokener) throws JSONException {
        return (JSONArray)parse(xmlTokener, true, null);
    }
    
    public static JSONObject toJSONObject(final XMLTokener xmlTokener) throws JSONException {
        return (JSONObject)parse(xmlTokener, false, null);
    }
    
    public static JSONObject toJSONObject(final String s) throws JSONException {
        return toJSONObject(new XMLTokener(s));
    }
    
    public static String toString(final JSONArray jsonArray) throws JSONException {
        final StringBuffer sb = new StringBuffer();
        final String string = jsonArray.getString(0);
        XML.noSpace(string);
        final String escape = XML.escape(string);
        sb.append('<');
        sb.append(escape);
        final Object opt = jsonArray.opt(1);
        int i;
        if (opt instanceof JSONObject) {
            i = 2;
            final JSONObject jsonObject = (JSONObject)opt;
            final Iterator keys = jsonObject.keys();
            while (keys.hasNext()) {
                final String string2 = keys.next().toString();
                XML.noSpace(string2);
                final String optString = jsonObject.optString(string2);
                if (optString != null) {
                    sb.append(' ');
                    sb.append(XML.escape(string2));
                    sb.append('=');
                    sb.append('\"');
                    sb.append(XML.escape(optString));
                    sb.append('\"');
                }
            }
        }
        else {
            i = 1;
        }
        final int length = jsonArray.length();
        if (i >= length) {
            sb.append('/');
            sb.append('>');
        }
        else {
            sb.append('>');
            do {
                final Object value = jsonArray.get(i);
                ++i;
                if (value != null) {
                    if (value instanceof String) {
                        sb.append(XML.escape(value.toString()));
                    }
                    else if (value instanceof JSONObject) {
                        sb.append(toString((JSONObject)value));
                    }
                    else {
                        if (!(value instanceof JSONArray)) {
                            continue;
                        }
                        sb.append(toString((JSONArray)value));
                    }
                }
            } while (i < length);
            sb.append('<');
            sb.append('/');
            sb.append(escape);
            sb.append('>');
        }
        return sb.toString();
    }
    
    public static String toString(final JSONObject jsonObject) throws JSONException {
        final StringBuffer sb = new StringBuffer();
        final String optString = jsonObject.optString("tagName");
        if (optString == null) {
            return XML.escape(jsonObject.toString());
        }
        XML.noSpace(optString);
        final String escape = XML.escape(optString);
        sb.append('<');
        sb.append(escape);
        final Iterator keys = jsonObject.keys();
        while (keys.hasNext()) {
            final String string = keys.next().toString();
            if (!string.equals("tagName") && !string.equals("childNodes")) {
                XML.noSpace(string);
                final String optString2 = jsonObject.optString(string);
                if (optString2 == null) {
                    continue;
                }
                sb.append(' ');
                sb.append(XML.escape(string));
                sb.append('=');
                sb.append('\"');
                sb.append(XML.escape(optString2));
                sb.append('\"');
            }
        }
        final JSONArray optJSONArray = jsonObject.optJSONArray("childNodes");
        if (optJSONArray == null) {
            sb.append('/');
            sb.append('>');
        }
        else {
            sb.append('>');
            for (int length = optJSONArray.length(), i = 0; i < length; ++i) {
                final Object value = optJSONArray.get(i);
                if (value != null) {
                    if (value instanceof String) {
                        sb.append(XML.escape(value.toString()));
                    }
                    else if (value instanceof JSONObject) {
                        sb.append(toString((JSONObject)value));
                    }
                    else if (value instanceof JSONArray) {
                        sb.append(toString((JSONArray)value));
                    }
                }
            }
            sb.append('<');
            sb.append('/');
            sb.append(escape);
            sb.append('>');
        }
        return sb.toString();
    }
}
