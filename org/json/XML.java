// 
// Decompiled by Procyon v0.5.36
// 

package org.json;

import java.util.Iterator;

public class XML
{
    public static final Character AMP;
    public static final Character APOS;
    public static final Character BANG;
    public static final Character EQ;
    public static final Character GT;
    public static final Character LT;
    public static final Character QUEST;
    public static final Character QUOT;
    public static final Character SLASH;
    
    public static String escape(final String s) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            switch (char1) {
                case 38: {
                    sb.append("&amp;");
                    break;
                }
                case 60: {
                    sb.append("&lt;");
                    break;
                }
                case 62: {
                    sb.append("&gt;");
                    break;
                }
                case 34: {
                    sb.append("&quot;");
                    break;
                }
                default: {
                    sb.append(char1);
                    break;
                }
            }
        }
        return sb.toString();
    }
    
    public static void noSpace(final String str) throws JSONException {
        final int length = str.length();
        if (length == 0) {
            throw new JSONException("Empty string.");
        }
        for (int i = 0; i < length; ++i) {
            if (Character.isWhitespace(str.charAt(i))) {
                throw new JSONException("'" + str + "' contains a space character.");
            }
        }
    }
    
    private static boolean parse(final XMLTokener xmlTokener, final JSONObject jsonObject, final String s) throws JSONException {
        final Object nextToken = xmlTokener.nextToken();
        if (nextToken == XML.BANG) {
            final char next = xmlTokener.next();
            if (next == '-') {
                if (xmlTokener.next() == '-') {
                    xmlTokener.skipPast("-->");
                    return false;
                }
                xmlTokener.back();
            }
            else if (next == '[') {
                if (xmlTokener.nextToken().equals("CDATA") && xmlTokener.next() == '[') {
                    final String nextCDATA = xmlTokener.nextCDATA();
                    if (nextCDATA.length() > 0) {
                        jsonObject.accumulate("content", nextCDATA);
                    }
                    return false;
                }
                throw xmlTokener.syntaxError("Expected 'CDATA['");
            }
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
            return false;
        }
        if (nextToken == XML.QUEST) {
            xmlTokener.skipPast("?>");
            return false;
        }
        if (nextToken == XML.SLASH) {
            final Object nextToken2 = xmlTokener.nextToken();
            if (s == null) {
                throw xmlTokener.syntaxError("Mismatched close tag" + nextToken2);
            }
            if (!nextToken2.equals(s)) {
                throw xmlTokener.syntaxError("Mismatched " + s + " and " + nextToken2);
            }
            if (xmlTokener.nextToken() != XML.GT) {
                throw xmlTokener.syntaxError("Misshaped close tag");
            }
            return true;
        }
        else {
            if (nextToken instanceof Character) {
                throw xmlTokener.syntaxError("Misshaped tag");
            }
            final String str = (String)nextToken;
            Object o = null;
            final JSONObject jsonObject2 = new JSONObject();
            while (true) {
                if (o == null) {
                    o = xmlTokener.nextToken();
                }
                if (o instanceof String) {
                    final String s2 = (String)o;
                    o = xmlTokener.nextToken();
                    if (o == XML.EQ) {
                        final Object nextToken3 = xmlTokener.nextToken();
                        if (!(nextToken3 instanceof String)) {
                            throw xmlTokener.syntaxError("Missing value");
                        }
                        jsonObject2.accumulate(s2, JSONObject.stringToValue((String)nextToken3));
                        o = null;
                    }
                    else {
                        jsonObject2.accumulate(s2, "");
                    }
                }
                else if (o == XML.SLASH) {
                    if (xmlTokener.nextToken() != XML.GT) {
                        throw xmlTokener.syntaxError("Misshaped tag");
                    }
                    jsonObject.accumulate(str, jsonObject2);
                    return false;
                }
                else {
                    if (o != XML.GT) {
                        throw xmlTokener.syntaxError("Misshaped tag");
                    }
                    while (true) {
                        final Object nextContent = xmlTokener.nextContent();
                        if (nextContent == null) {
                            if (str != null) {
                                throw xmlTokener.syntaxError("Unclosed tag " + str);
                            }
                            return false;
                        }
                        else if (nextContent instanceof String) {
                            final String s3 = (String)nextContent;
                            if (s3.length() <= 0) {
                                continue;
                            }
                            jsonObject2.accumulate("content", JSONObject.stringToValue(s3));
                        }
                        else {
                            if (nextContent == XML.LT && parse(xmlTokener, jsonObject2, str)) {
                                if (jsonObject2.length() == 0) {
                                    jsonObject.accumulate(str, "");
                                }
                                else if (jsonObject2.length() == 1 && jsonObject2.opt("content") != null) {
                                    jsonObject.accumulate(str, jsonObject2.opt("content"));
                                }
                                else {
                                    jsonObject.accumulate(str, jsonObject2);
                                }
                                return false;
                            }
                            continue;
                        }
                    }
                }
            }
        }
    }
    
    public static JSONObject toJSONObject(final String s) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        final XMLTokener xmlTokener = new XMLTokener(s);
        while (xmlTokener.more() && xmlTokener.skipPast("<")) {
            parse(xmlTokener, jsonObject, null);
        }
        return jsonObject;
    }
    
    public static String toString(final Object o) throws JSONException {
        return toString(o, null);
    }
    
    public static String toString(final Object o, final String str) throws JSONException {
        final StringBuffer sb = new StringBuffer();
        if (o instanceof JSONObject) {
            if (str != null) {
                sb.append('<');
                sb.append(str);
                sb.append('>');
            }
            final JSONObject jsonObject = (JSONObject)o;
            final Iterator keys = jsonObject.keys();
            while (keys.hasNext()) {
                final String string = keys.next().toString();
                Object opt = jsonObject.opt(string);
                if (opt == null) {
                    opt = "";
                }
                if (opt instanceof String) {
                    final String s = (String)opt;
                }
                if (string.equals("content")) {
                    if (opt instanceof JSONArray) {
                        final JSONArray jsonArray = (JSONArray)opt;
                        for (int length = jsonArray.length(), i = 0; i < length; ++i) {
                            if (i > 0) {
                                sb.append('\n');
                            }
                            sb.append(escape(jsonArray.get(i).toString()));
                        }
                    }
                    else {
                        sb.append(escape(opt.toString()));
                    }
                }
                else if (opt instanceof JSONArray) {
                    final JSONArray jsonArray2 = (JSONArray)opt;
                    for (int length2 = jsonArray2.length(), j = 0; j < length2; ++j) {
                        final Object value = jsonArray2.get(j);
                        if (value instanceof JSONArray) {
                            sb.append('<');
                            sb.append(string);
                            sb.append('>');
                            sb.append(toString(value));
                            sb.append("</");
                            sb.append(string);
                            sb.append('>');
                        }
                        else {
                            sb.append(toString(value, string));
                        }
                    }
                }
                else if (opt.equals("")) {
                    sb.append('<');
                    sb.append(string);
                    sb.append("/>");
                }
                else {
                    sb.append(toString(opt, string));
                }
            }
            if (str != null) {
                sb.append("</");
                sb.append(str);
                sb.append('>');
            }
            return sb.toString();
        }
        if (o instanceof JSONArray) {
            final JSONArray jsonArray3 = (JSONArray)o;
            for (int length3 = jsonArray3.length(), k = 0; k < length3; ++k) {
                sb.append(toString(jsonArray3.opt(k), (str == null) ? "array" : str));
            }
            return sb.toString();
        }
        final String s2 = (o == null) ? "null" : escape(o.toString());
        return (str == null) ? ("\"" + s2 + "\"") : ((s2.length() == 0) ? ("<" + str + "/>") : ("<" + str + ">" + s2 + "</" + str + ">"));
    }
    
    static {
        AMP = new Character('&');
        APOS = new Character('\'');
        BANG = new Character('!');
        EQ = new Character('=');
        GT = new Character('>');
        LT = new Character('<');
        QUEST = new Character('?');
        QUOT = new Character('\"');
        SLASH = new Character('/');
    }
}
