// 
// Decompiled by Procyon v0.5.36
// 

package org.json;

public class CDL
{
    private static String getValue(final JSONTokener jsonTokener) throws JSONException {
        char next;
        do {
            next = jsonTokener.next();
        } while (next == ' ' || next == '\t');
        switch (next) {
            case 0: {
                return null;
            }
            case 34:
            case 39: {
                return jsonTokener.nextString(next);
            }
            case 44: {
                jsonTokener.back();
                return "";
            }
            default: {
                jsonTokener.back();
                return jsonTokener.nextTo(',');
            }
        }
    }
    
    public static JSONArray rowToJSONArray(final JSONTokener jsonTokener) throws JSONException {
        final JSONArray jsonArray = new JSONArray();
        while (true) {
            final String value = getValue(jsonTokener);
            if (value == null || (jsonArray.length() == 0 && value.length() == 0)) {
                return null;
            }
            jsonArray.put(value);
            while (true) {
                final char next = jsonTokener.next();
                if (next == ',') {
                    break;
                }
                if (next == ' ') {
                    continue;
                }
                if (next == '\n' || next == '\r' || next == '\0') {
                    return jsonArray;
                }
                throw jsonTokener.syntaxError("Bad character '" + next + "' (" + (int)next + ").");
            }
        }
    }
    
    public static JSONObject rowToJSONObject(final JSONArray jsonArray, final JSONTokener jsonTokener) throws JSONException {
        final JSONArray rowToJSONArray = rowToJSONArray(jsonTokener);
        return (rowToJSONArray != null) ? rowToJSONArray.toJSONObject(jsonArray) : null;
    }
    
    public static JSONArray toJSONArray(final String s) throws JSONException {
        return toJSONArray(new JSONTokener(s));
    }
    
    public static JSONArray toJSONArray(final JSONTokener jsonTokener) throws JSONException {
        return toJSONArray(rowToJSONArray(jsonTokener), jsonTokener);
    }
    
    public static JSONArray toJSONArray(final JSONArray jsonArray, final String s) throws JSONException {
        return toJSONArray(jsonArray, new JSONTokener(s));
    }
    
    public static JSONArray toJSONArray(final JSONArray jsonArray, final JSONTokener jsonTokener) throws JSONException {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }
        final JSONArray jsonArray2 = new JSONArray();
        while (true) {
            final JSONObject rowToJSONObject = rowToJSONObject(jsonArray, jsonTokener);
            if (rowToJSONObject == null) {
                break;
            }
            jsonArray2.put(rowToJSONObject);
        }
        if (jsonArray2.length() == 0) {
            return null;
        }
        return jsonArray2;
    }
    
    public static String rowToString(final JSONArray jsonArray) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < jsonArray.length(); ++i) {
            if (i > 0) {
                sb.append(',');
            }
            final Object opt = jsonArray.opt(i);
            if (opt != null) {
                final String string = opt.toString();
                if (string.indexOf(44) >= 0) {
                    if (string.indexOf(34) >= 0) {
                        sb.append('\'');
                        sb.append(string);
                        sb.append('\'');
                    }
                    else {
                        sb.append('\"');
                        sb.append(string);
                        sb.append('\"');
                    }
                }
                else {
                    sb.append(string);
                }
            }
        }
        sb.append('\n');
        return sb.toString();
    }
    
    public static String toString(final JSONArray jsonArray) throws JSONException {
        final JSONObject optJSONObject = jsonArray.optJSONObject(0);
        if (optJSONObject != null) {
            final JSONArray names = optJSONObject.names();
            if (names != null) {
                return rowToString(names) + toString(names, jsonArray);
            }
        }
        return null;
    }
    
    public static String toString(final JSONArray jsonArray, final JSONArray jsonArray2) throws JSONException {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < jsonArray2.length(); ++i) {
            final JSONObject optJSONObject = jsonArray2.optJSONObject(i);
            if (optJSONObject != null) {
                sb.append(rowToString(optJSONObject.toJSONArray(jsonArray)));
            }
        }
        return sb.toString();
    }
}
