// 
// Decompiled by Procyon v0.5.36
// 

package org.json;

import java.io.IOException;
import java.io.Writer;
import java.util.TreeSet;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

public class JSONObject
{
    private Map map;
    public static final Object NULL;
    
    public JSONObject() {
        this.map = new HashMap();
    }
    
    public JSONObject(final JSONObject jsonObject, final String[] array) throws JSONException {
        this();
        for (int i = 0; i < array.length; ++i) {
            this.putOnce(array[i], jsonObject.opt(array[i]));
        }
    }
    
    public JSONObject(final JSONTokener jsonTokener) throws JSONException {
        this();
        if (jsonTokener.nextClean() != '{') {
            throw jsonTokener.syntaxError("A JSONObject text must begin with '{'");
        }
        while (true) {
            switch (jsonTokener.nextClean()) {
                case '\0': {
                    throw jsonTokener.syntaxError("A JSONObject text must end with '}'");
                }
                case '}': {}
                default: {
                    jsonTokener.back();
                    final String string = jsonTokener.nextValue().toString();
                    final char nextClean = jsonTokener.nextClean();
                    if (nextClean == '=') {
                        if (jsonTokener.next() != '>') {
                            jsonTokener.back();
                        }
                    }
                    else if (nextClean != ':') {
                        throw jsonTokener.syntaxError("Expected a ':' after a key");
                    }
                    this.putOnce(string, jsonTokener.nextValue());
                    switch (jsonTokener.nextClean()) {
                        case ',':
                        case ';': {
                            if (jsonTokener.nextClean() == '}') {
                                return;
                            }
                            jsonTokener.back();
                            continue;
                        }
                        case '}': {
                            return;
                        }
                        default: {
                            throw jsonTokener.syntaxError("Expected a ',' or '}'");
                        }
                    }
                    break;
                }
            }
        }
    }
    
    public JSONObject(final Map map) {
        this.map = ((map == null) ? new HashMap() : map);
    }
    
    public JSONObject(final Map map, final boolean b) {
        this.map = new HashMap();
        if (map != null) {
            for (final Map.Entry<Object, V> entry : map.entrySet()) {
                this.map.put(entry.getKey(), new JSONObject(entry.getValue(), b));
            }
        }
    }
    
    public JSONObject(final Object o) {
        this();
        this.populateInternalMap(o, false);
    }
    
    public JSONObject(final Object o, final boolean b) {
        this();
        this.populateInternalMap(o, b);
    }
    
    private void populateInternalMap(final Object obj, boolean b) {
        final Class<?> class1 = obj.getClass();
        if (class1.getClassLoader() == null) {
            b = false;
        }
        final Method[] array = b ? class1.getMethods() : class1.getDeclaredMethods();
        for (int i = 0; i < array.length; ++i) {
            try {
                final Method method = array[i];
                final String name = method.getName();
                String s = "";
                if (name.startsWith("get")) {
                    s = name.substring(3);
                }
                else if (name.startsWith("is")) {
                    s = name.substring(2);
                }
                if (s.length() > 0 && Character.isUpperCase(s.charAt(0)) && method.getParameterTypes().length == 0) {
                    if (s.length() == 1) {
                        s = s.toLowerCase();
                    }
                    else if (!Character.isUpperCase(s.charAt(1))) {
                        s = s.substring(0, 1).toLowerCase() + s.substring(1);
                    }
                    final Object invoke = method.invoke(obj, (Object[])null);
                    if (invoke == null) {
                        this.map.put(s, JSONObject.NULL);
                    }
                    else if (((Collection)invoke).getClass().isArray()) {
                        this.map.put(s, new JSONArray(invoke, b));
                    }
                    else if (invoke instanceof Collection) {
                        this.map.put(s, new JSONArray((Collection)invoke, b));
                    }
                    else if (invoke instanceof Map) {
                        this.map.put(s, new JSONObject((Map)invoke, b));
                    }
                    else if (this.isStandardProperty(((Map)invoke).getClass())) {
                        this.map.put(s, invoke);
                    }
                    else if (((Map)invoke).getClass().getPackage().getName().startsWith("java") || ((Map)invoke).getClass().getClassLoader() == null) {
                        this.map.put(s, invoke.toString());
                    }
                    else {
                        this.map.put(s, new JSONObject(invoke, b));
                    }
                }
            }
            catch (Exception cause) {
                throw new RuntimeException(cause);
            }
        }
    }
    
    private boolean isStandardProperty(final Class clazz) {
        return clazz.isPrimitive() || clazz.isAssignableFrom(Byte.class) || clazz.isAssignableFrom(Short.class) || clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(Long.class) || clazz.isAssignableFrom(Float.class) || clazz.isAssignableFrom(Double.class) || clazz.isAssignableFrom(Character.class) || clazz.isAssignableFrom(String.class) || clazz.isAssignableFrom(Boolean.class);
    }
    
    public JSONObject(final Object obj, final String[] array) {
        this();
        final Class<?> class1 = obj.getClass();
        for (int i = 0; i < array.length; ++i) {
            final String name = array[i];
            try {
                this.putOpt(name, class1.getField(name).get(obj));
            }
            catch (Exception ex) {}
        }
    }
    
    public JSONObject(final String s) throws JSONException {
        this(new JSONTokener(s));
    }
    
    public JSONObject accumulate(final String s, final Object o) throws JSONException {
        testValidity(o);
        final Object opt = this.opt(s);
        if (opt == null) {
            this.put(s, (o instanceof JSONArray) ? new JSONArray().put(o) : o);
        }
        else if (opt instanceof JSONArray) {
            ((JSONArray)opt).put(o);
        }
        else {
            this.put(s, new JSONArray().put(opt).put(o));
        }
        return this;
    }
    
    public JSONObject append(final String str, final Object o) throws JSONException {
        testValidity(o);
        final Object opt = this.opt(str);
        if (opt == null) {
            this.put(str, new JSONArray().put(o));
        }
        else {
            if (!(opt instanceof JSONArray)) {
                throw new JSONException("JSONObject[" + str + "] is not a JSONArray.");
            }
            this.put(str, ((JSONArray)opt).put(o));
        }
        return this;
    }
    
    public static String doubleToString(final double d) {
        if (Double.isInfinite(d) || Double.isNaN(d)) {
            return "null";
        }
        String s = Double.toString(d);
        if (s.indexOf(46) > 0 && s.indexOf(101) < 0 && s.indexOf(69) < 0) {
            while (s.endsWith("0")) {
                s = s.substring(0, s.length() - 1);
            }
            if (s.endsWith(".")) {
                s = s.substring(0, s.length() - 1);
            }
        }
        return s;
    }
    
    public Object get(final String s) throws JSONException {
        final Object opt = this.opt(s);
        if (opt == null) {
            throw new JSONException("JSONObject[" + quote(s) + "] not found.");
        }
        return opt;
    }
    
    public boolean getBoolean(final String s) throws JSONException {
        final Object value = this.get(s);
        if (value.equals(Boolean.FALSE) || (value instanceof String && ((String)value).equalsIgnoreCase("false"))) {
            return false;
        }
        if (value.equals(Boolean.TRUE) || (value instanceof String && ((String)value).equalsIgnoreCase("true"))) {
            return true;
        }
        throw new JSONException("JSONObject[" + quote(s) + "] is not a Boolean.");
    }
    
    public double getDouble(final String s) throws JSONException {
        final Object value = this.get(s);
        try {
            return (value instanceof Number) ? ((Number)value).doubleValue() : Double.valueOf((String)value);
        }
        catch (Exception ex) {
            throw new JSONException("JSONObject[" + quote(s) + "] is not a number.");
        }
    }
    
    public int getInt(final String s) throws JSONException {
        final Object value = this.get(s);
        return (value instanceof Number) ? ((Number)value).intValue() : ((int)this.getDouble(s));
    }
    
    public JSONArray getJSONArray(final String s) throws JSONException {
        final Object value = this.get(s);
        if (value instanceof JSONArray) {
            return (JSONArray)value;
        }
        throw new JSONException("JSONObject[" + quote(s) + "] is not a JSONArray.");
    }
    
    public JSONObject getJSONObject(final String s) throws JSONException {
        final Object value = this.get(s);
        if (value instanceof JSONObject) {
            return (JSONObject)value;
        }
        throw new JSONException("JSONObject[" + quote(s) + "] is not a JSONObject.");
    }
    
    public long getLong(final String s) throws JSONException {
        final Object value = this.get(s);
        return (value instanceof Number) ? ((Number)value).longValue() : ((long)this.getDouble(s));
    }
    
    public static String[] getNames(final JSONObject jsonObject) {
        final int length = jsonObject.length();
        if (length == 0) {
            return null;
        }
        final Iterator keys = jsonObject.keys();
        final String[] array = new String[length];
        int n = 0;
        while (keys.hasNext()) {
            array[n] = keys.next();
            ++n;
        }
        return array;
    }
    
    public static String[] getNames(final Object o) {
        if (o == null) {
            return null;
        }
        final Field[] fields = o.getClass().getFields();
        final int length = fields.length;
        if (length == 0) {
            return null;
        }
        final String[] array = new String[length];
        for (int i = 0; i < length; ++i) {
            array[i] = fields[i].getName();
        }
        return array;
    }
    
    public String getString(final String s) throws JSONException {
        return this.get(s).toString();
    }
    
    public boolean has(final String s) {
        return this.map.containsKey(s);
    }
    
    public boolean isNull(final String s) {
        return JSONObject.NULL.equals(this.opt(s));
    }
    
    public Iterator keys() {
        return this.map.keySet().iterator();
    }
    
    public int length() {
        return this.map.size();
    }
    
    public JSONArray names() {
        final JSONArray jsonArray = new JSONArray();
        final Iterator keys = this.keys();
        while (keys.hasNext()) {
            jsonArray.put(keys.next());
        }
        return (jsonArray.length() == 0) ? null : jsonArray;
    }
    
    public static String numberToString(final Number n) throws JSONException {
        if (n == null) {
            throw new JSONException("Null pointer");
        }
        testValidity(n);
        String s = n.toString();
        if (s.indexOf(46) > 0 && s.indexOf(101) < 0 && s.indexOf(69) < 0) {
            while (s.endsWith("0")) {
                s = s.substring(0, s.length() - 1);
            }
            if (s.endsWith(".")) {
                s = s.substring(0, s.length() - 1);
            }
        }
        return s;
    }
    
    public Object opt(final String s) {
        return (s == null) ? null : this.map.get(s);
    }
    
    public boolean optBoolean(final String s) {
        return this.optBoolean(s, false);
    }
    
    public boolean optBoolean(final String s, final boolean b) {
        try {
            return this.getBoolean(s);
        }
        catch (Exception ex) {
            return b;
        }
    }
    
    public JSONObject put(final String s, final Collection collection) throws JSONException {
        this.put(s, new JSONArray(collection));
        return this;
    }
    
    public double optDouble(final String s) {
        return this.optDouble(s, Double.NaN);
    }
    
    public double optDouble(final String s, final double n) {
        try {
            final Object opt = this.opt(s);
            return (opt instanceof Number) ? ((Number)opt).doubleValue() : new Double((String)opt);
        }
        catch (Exception ex) {
            return n;
        }
    }
    
    public int optInt(final String s) {
        return this.optInt(s, 0);
    }
    
    public int optInt(final String s, final int n) {
        try {
            return this.getInt(s);
        }
        catch (Exception ex) {
            return n;
        }
    }
    
    public JSONArray optJSONArray(final String s) {
        final Object opt = this.opt(s);
        return (opt instanceof JSONArray) ? ((JSONArray)opt) : null;
    }
    
    public JSONObject optJSONObject(final String s) {
        final Object opt = this.opt(s);
        return (opt instanceof JSONObject) ? ((JSONObject)opt) : null;
    }
    
    public long optLong(final String s) {
        return this.optLong(s, 0L);
    }
    
    public long optLong(final String s, final long n) {
        try {
            return this.getLong(s);
        }
        catch (Exception ex) {
            return n;
        }
    }
    
    public String optString(final String s) {
        return this.optString(s, "");
    }
    
    public String optString(final String s, final String s2) {
        final Object opt = this.opt(s);
        return (opt != null) ? opt.toString() : s2;
    }
    
    public JSONObject put(final String s, final boolean b) throws JSONException {
        this.put(s, b ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }
    
    public JSONObject put(final String s, final double value) throws JSONException {
        this.put(s, new Double(value));
        return this;
    }
    
    public JSONObject put(final String s, final int value) throws JSONException {
        this.put(s, new Integer(value));
        return this;
    }
    
    public JSONObject put(final String s, final long value) throws JSONException {
        this.put(s, new Long(value));
        return this;
    }
    
    public JSONObject put(final String s, final Map map) throws JSONException {
        this.put(s, new JSONObject(map));
        return this;
    }
    
    public JSONObject put(final String s, final Object o) throws JSONException {
        if (s == null) {
            throw new JSONException("Null key.");
        }
        if (o != null) {
            testValidity(o);
            this.map.put(s, o);
        }
        else {
            this.remove(s);
        }
        return this;
    }
    
    public JSONObject putOnce(final String str, final Object o) throws JSONException {
        if (str != null && o != null) {
            if (this.opt(str) != null) {
                throw new JSONException("Duplicate key \"" + str + "\"");
            }
            this.put(str, o);
        }
        return this;
    }
    
    public JSONObject putOpt(final String s, final Object o) throws JSONException {
        if (s != null && o != null) {
            this.put(s, o);
        }
        return this;
    }
    
    public static String quote(final String s) {
        if (s == null || s.length() == 0) {
            return "\"\"";
        }
        char char1 = '\0';
        final int length = s.length();
        final StringBuffer sb = new StringBuffer(length + 4);
        sb.append('\"');
        for (int i = 0; i < length; ++i) {
            final char c = char1;
            char1 = s.charAt(i);
            switch (char1) {
                case 34:
                case 92: {
                    sb.append('\\');
                    sb.append(char1);
                    break;
                }
                case 47: {
                    if (c == '<') {
                        sb.append('\\');
                    }
                    sb.append(char1);
                    break;
                }
                case 8: {
                    sb.append("\\b");
                    break;
                }
                case 9: {
                    sb.append("\\t");
                    break;
                }
                case 10: {
                    sb.append("\\n");
                    break;
                }
                case 12: {
                    sb.append("\\f");
                    break;
                }
                case 13: {
                    sb.append("\\r");
                    break;
                }
                default: {
                    if (char1 < ' ' || (char1 >= '\u0080' && char1 < ' ') || (char1 >= '\u2000' && char1 < '\u2100')) {
                        final String string = "000" + Integer.toHexString(char1);
                        sb.append("\\u" + string.substring(string.length() - 4));
                        break;
                    }
                    sb.append(char1);
                    break;
                }
            }
        }
        sb.append('\"');
        return sb.toString();
    }
    
    public Object remove(final String s) {
        return this.map.remove(s);
    }
    
    public Iterator sortedKeys() {
        return new TreeSet(this.map.keySet()).iterator();
    }
    
    public static Object stringToValue(final String s) {
        if (s.equals("")) {
            return s;
        }
        if (s.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        if (s.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        if (s.equalsIgnoreCase("null")) {
            return JSONObject.NULL;
        }
        final char char1 = s.charAt(0);
        if ((char1 >= '0' && char1 <= '9') || char1 == '.' || char1 == '-' || char1 == '+') {
            Label_0157: {
                if (char1 == '0') {
                    Label_0142: {
                        if (s.length() > 2) {
                            if (s.charAt(1) != 'x') {
                                if (s.charAt(1) != 'X') {
                                    break Label_0142;
                                }
                            }
                            try {
                                return new Integer(Integer.parseInt(s.substring(2), 16));
                            }
                            catch (Exception ex) {
                                break Label_0157;
                            }
                        }
                        try {
                            return new Integer(Integer.parseInt(s, 8));
                        }
                        catch (Exception ex2) {}
                    }
                }
                try {
                    return new Integer(s);
                }
                catch (Exception ex3) {
                    try {
                        return new Long(s);
                    }
                    catch (Exception ex4) {
                        try {
                            return new Double(s);
                        }
                        catch (Exception ex5) {}
                    }
                }
            }
        }
        return s;
    }
    
    static void testValidity(final Object o) throws JSONException {
        if (o != null) {
            if (o instanceof Double) {
                if (((Double)o).isInfinite() || ((Double)o).isNaN()) {
                    throw new JSONException("JSON does not allow non-finite numbers.");
                }
            }
            else if (o instanceof Float && (((Float)o).isInfinite() || ((Float)o).isNaN())) {
                throw new JSONException("JSON does not allow non-finite numbers.");
            }
        }
    }
    
    public JSONArray toJSONArray(final JSONArray jsonArray) throws JSONException {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }
        final JSONArray jsonArray2 = new JSONArray();
        for (int i = 0; i < jsonArray.length(); ++i) {
            jsonArray2.put(this.opt(jsonArray.getString(i)));
        }
        return jsonArray2;
    }
    
    @Override
    public String toString() {
        try {
            final Iterator keys = this.keys();
            final StringBuffer sb = new StringBuffer("{");
            while (keys.hasNext()) {
                if (sb.length() > 1) {
                    sb.append(',');
                }
                final Object next = keys.next();
                sb.append(quote(next.toString()));
                sb.append(':');
                sb.append(valueToString(this.map.get(next)));
            }
            sb.append('}');
            return sb.toString();
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public String toString(final int n) throws JSONException {
        return this.toString(n, 0);
    }
    
    String toString(final int n, final int n2) throws JSONException {
        final int length = this.length();
        if (length == 0) {
            return "{}";
        }
        final Iterator sortedKeys = this.sortedKeys();
        final StringBuffer sb = new StringBuffer("{");
        final int n3 = n2 + n;
        if (length == 1) {
            final Object next = sortedKeys.next();
            sb.append(quote(next.toString()));
            sb.append(": ");
            sb.append(valueToString(this.map.get(next), n, n2));
        }
        else {
            while (sortedKeys.hasNext()) {
                final Object next2 = sortedKeys.next();
                if (sb.length() > 1) {
                    sb.append(",\n");
                }
                else {
                    sb.append('\n');
                }
                for (int i = 0; i < n3; ++i) {
                    sb.append(' ');
                }
                sb.append(quote(next2.toString()));
                sb.append(": ");
                sb.append(valueToString(this.map.get(next2), n, n3));
            }
            if (sb.length() > 1) {
                sb.append('\n');
                for (int j = 0; j < n2; ++j) {
                    sb.append(' ');
                }
            }
        }
        sb.append('}');
        return sb.toString();
    }
    
    static String valueToString(final Object o) throws JSONException {
        if (o == null || o.equals(null)) {
            return "null";
        }
        if (o instanceof JSONString) {
            String jsonString;
            try {
                jsonString = ((JSONString)o).toJSONString();
            }
            catch (Exception ex) {
                throw new JSONException(ex);
            }
            if (jsonString instanceof String) {
                return jsonString;
            }
            throw new JSONException("Bad value from toJSONString: " + (Object)jsonString);
        }
        else {
            if (o instanceof Number) {
                return numberToString((Number)o);
            }
            if (o instanceof Boolean || o instanceof JSONObject || o instanceof JSONArray) {
                return o.toString();
            }
            if (o instanceof Map) {
                return new JSONObject((Map)o).toString();
            }
            if (o instanceof Collection) {
                return new JSONArray((Collection)o).toString();
            }
            if (o.getClass().isArray()) {
                return new JSONArray(o).toString();
            }
            return quote(o.toString());
        }
    }
    
    static String valueToString(final Object o, final int n, final int n2) throws JSONException {
        if (o == null || o.equals(null)) {
            return "null";
        }
        try {
            if (o instanceof JSONString) {
                final String jsonString = ((JSONString)o).toJSONString();
                if (jsonString instanceof String) {
                    return jsonString;
                }
            }
        }
        catch (Exception ex) {}
        if (o instanceof Number) {
            return numberToString((Number)o);
        }
        if (o instanceof Boolean) {
            return o.toString();
        }
        if (o instanceof JSONObject) {
            return ((JSONObject)o).toString(n, n2);
        }
        if (o instanceof JSONArray) {
            return ((JSONArray)o).toString(n, n2);
        }
        if (o instanceof Map) {
            return new JSONObject((Map)o).toString(n, n2);
        }
        if (o instanceof Collection) {
            return new JSONArray((Collection)o).toString(n, n2);
        }
        if (o.getClass().isArray()) {
            return new JSONArray(o).toString(n, n2);
        }
        return quote(o.toString());
    }
    
    public Writer write(final Writer writer) throws JSONException {
        try {
            int n = 0;
            final Iterator keys = this.keys();
            writer.write(123);
            while (keys.hasNext()) {
                if (n != 0) {
                    writer.write(44);
                }
                final Object next = keys.next();
                writer.write(quote(next.toString()));
                writer.write(58);
                final Object value = this.map.get(next);
                if (value instanceof JSONObject) {
                    ((JSONObject)value).write(writer);
                }
                else if (value instanceof JSONArray) {
                    ((JSONArray)value).write(writer);
                }
                else {
                    writer.write(valueToString(value));
                }
                n = 1;
            }
            writer.write(125);
            return writer;
        }
        catch (IOException ex) {
            throw new JSONException(ex);
        }
    }
    
    static {
        NULL = new Null();
    }
    
    private static final class Null
    {
        @Override
        protected final Object clone() {
            return this;
        }
        
        @Override
        public boolean equals(final Object o) {
            return o == null || o == this;
        }
        
        @Override
        public String toString() {
            return "null";
        }
    }
}
