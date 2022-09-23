// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Stack;
import net.minidev.json.writer.JsonReaderI;

public class JSONNavi<T>
{
    private JsonReaderI<? super T> mapper;
    private T root;
    private Stack<Object> stack;
    private Stack<Object> path;
    private Object current;
    private boolean failure;
    private String failureMessage;
    private boolean readonly;
    private Object missingKey;
    private static final JSONStyle ERROR_COMPRESS;
    
    static {
        ERROR_COMPRESS = new JSONStyle(2);
    }
    
    public static JSONNavi<JSONAwareEx> newInstance() {
        return new JSONNavi<JSONAwareEx>(JSONValue.defaultReader.DEFAULT_ORDERED);
    }
    
    public static JSONNavi<JSONObject> newInstanceObject() {
        final JSONNavi<JSONObject> o = new JSONNavi<JSONObject>((JsonReaderI<? super JSONObject>)JSONValue.defaultReader.getMapper((Class<? super T>)JSONObject.class));
        o.object();
        return o;
    }
    
    public static JSONNavi<JSONArray> newInstanceArray() {
        final JSONNavi<JSONArray> o = new JSONNavi<JSONArray>((JsonReaderI<? super JSONArray>)JSONValue.defaultReader.getMapper((Class<? super T>)JSONArray.class));
        o.array();
        return o;
    }
    
    public JSONNavi(final JsonReaderI<? super T> mapper) {
        this.stack = new Stack<Object>();
        this.path = new Stack<Object>();
        this.failure = false;
        this.readonly = false;
        this.missingKey = null;
        this.mapper = mapper;
    }
    
    public JSONNavi(final String json) {
        this.stack = new Stack<Object>();
        this.path = new Stack<Object>();
        this.failure = false;
        this.readonly = false;
        this.missingKey = null;
        this.root = (T)JSONValue.parse(json);
        this.current = this.root;
        this.readonly = true;
    }
    
    public JSONNavi(final String json, final JsonReaderI<T> mapper) {
        this.stack = new Stack<Object>();
        this.path = new Stack<Object>();
        this.failure = false;
        this.readonly = false;
        this.missingKey = null;
        this.root = JSONValue.parse(json, mapper);
        this.mapper = mapper;
        this.current = this.root;
        this.readonly = true;
    }
    
    public JSONNavi(final String json, final Class<T> mapTo) {
        this.stack = new Stack<Object>();
        this.path = new Stack<Object>();
        this.failure = false;
        this.readonly = false;
        this.missingKey = null;
        this.root = JSONValue.parse(json, mapTo);
        this.mapper = JSONValue.defaultReader.getMapper(mapTo);
        this.current = this.root;
        this.readonly = true;
    }
    
    public JSONNavi<T> root() {
        this.current = this.root;
        this.stack.clear();
        this.path.clear();
        this.failure = false;
        this.missingKey = null;
        this.failureMessage = null;
        return this;
    }
    
    public boolean hasFailure() {
        return this.failure;
    }
    
    public Object getCurrentObject() {
        return this.current;
    }
    
    public Collection<String> getKeys() {
        if (this.current instanceof Map) {
            return (Collection<String>)((Map)this.current).keySet();
        }
        return null;
    }
    
    public int getSize() {
        if (this.current == null) {
            return 0;
        }
        if (this.isArray()) {
            return ((List)this.current).size();
        }
        if (this.isObject()) {
            return ((Map)this.current).size();
        }
        return 1;
    }
    
    public String getString(final String key) {
        String v = null;
        if (!this.hasKey(key)) {
            return v;
        }
        this.at(key);
        v = this.asString();
        this.up();
        return v;
    }
    
    public int getInt(final String key) {
        int v = 0;
        if (!this.hasKey(key)) {
            return v;
        }
        this.at(key);
        v = this.asInt();
        this.up();
        return v;
    }
    
    public Integer getInteger(final String key) {
        Integer v = null;
        if (!this.hasKey(key)) {
            return v;
        }
        this.at(key);
        v = this.asIntegerObj();
        this.up();
        return v;
    }
    
    public double getDouble(final String key) {
        double v = 0.0;
        if (!this.hasKey(key)) {
            return v;
        }
        this.at(key);
        v = this.asDouble();
        this.up();
        return v;
    }
    
    public boolean hasKey(final String key) {
        return this.isObject() && this.o(this.current).containsKey(key);
    }
    
    public JSONNavi<?> at(final String key) {
        if (this.failure) {
            return this;
        }
        if (!this.isObject()) {
            this.object();
        }
        if (!(this.current instanceof Map)) {
            return this.failure("current node is not an Object", key);
        }
        if (this.o(this.current).containsKey(key)) {
            final Object next = this.o(this.current).get(key);
            this.stack.add(this.current);
            this.path.add(key);
            this.current = next;
            return this;
        }
        if (this.readonly) {
            return this.failure("current Object have no key named " + key, key);
        }
        this.stack.add(this.current);
        this.path.add(key);
        this.current = null;
        this.missingKey = key;
        return this;
    }
    
    public Object get(final String key) {
        if (this.failure) {
            return this;
        }
        if (!this.isObject()) {
            this.object();
        }
        if (!(this.current instanceof Map)) {
            return this.failure("current node is not an Object", key);
        }
        return this.o(this.current).get(key);
    }
    
    public Object get(final int index) {
        if (this.failure) {
            return this;
        }
        if (!this.isArray()) {
            this.array();
        }
        if (!(this.current instanceof List)) {
            return this.failure("current node is not an List", index);
        }
        return this.a(this.current).get(index);
    }
    
    public JSONNavi<T> set(final String key, final String value) {
        this.object();
        if (this.failure) {
            return this;
        }
        this.o(this.current).put(key, value);
        return this;
    }
    
    public JSONNavi<T> set(final String key, final Number value) {
        this.object();
        if (this.failure) {
            return this;
        }
        this.o(this.current).put(key, value);
        return this;
    }
    
    public JSONNavi<T> set(final String key, final long value) {
        return this.set(key, (Number)value);
    }
    
    public JSONNavi<T> set(final String key, final int value) {
        return this.set(key, (Number)value);
    }
    
    public JSONNavi<T> set(final String key, final double value) {
        return this.set(key, (Number)value);
    }
    
    public JSONNavi<T> set(final String key, final float value) {
        return this.set(key, (Number)value);
    }
    
    public JSONNavi<T> add(final Object... values) {
        this.array();
        if (this.failure) {
            return this;
        }
        final List<Object> list = this.a(this.current);
        for (final Object o : values) {
            list.add(o);
        }
        return this;
    }
    
    public String asString() {
        if (this.current == null) {
            return null;
        }
        if (this.current instanceof String) {
            return (String)this.current;
        }
        return this.current.toString();
    }
    
    public double asDouble() {
        if (this.current instanceof Number) {
            return ((Number)this.current).doubleValue();
        }
        return Double.NaN;
    }
    
    public Double asDoubleObj() {
        if (this.current == null) {
            return null;
        }
        if (!(this.current instanceof Number)) {
            return Double.NaN;
        }
        if (this.current instanceof Double) {
            return (Double)this.current;
        }
        return ((Number)this.current).doubleValue();
    }
    
    public double asFloat() {
        if (this.current instanceof Number) {
            return ((Number)this.current).floatValue();
        }
        return Double.NaN;
    }
    
    public Float asFloatObj() {
        if (this.current == null) {
            return null;
        }
        if (!(this.current instanceof Number)) {
            return Float.NaN;
        }
        if (this.current instanceof Float) {
            return (Float)this.current;
        }
        return ((Number)this.current).floatValue();
    }
    
    public int asInt() {
        if (this.current instanceof Number) {
            return ((Number)this.current).intValue();
        }
        return 0;
    }
    
    public Integer asIntegerObj() {
        if (this.current == null) {
            return null;
        }
        if (!(this.current instanceof Number)) {
            return null;
        }
        if (this.current instanceof Integer) {
            return (Integer)this.current;
        }
        if (this.current instanceof Long) {
            final Long l = (Long)this.current;
            if (l == l.intValue()) {
                return l.intValue();
            }
        }
        return null;
    }
    
    public long asLong() {
        if (this.current instanceof Number) {
            return ((Number)this.current).longValue();
        }
        return 0L;
    }
    
    public Long asLongObj() {
        if (this.current == null) {
            return null;
        }
        if (!(this.current instanceof Number)) {
            return null;
        }
        if (this.current instanceof Long) {
            return (Long)this.current;
        }
        if (this.current instanceof Integer) {
            return ((Number)this.current).longValue();
        }
        return null;
    }
    
    public boolean asBoolean() {
        return this.current instanceof Boolean && (boolean)this.current;
    }
    
    public Boolean asBooleanObj() {
        if (this.current == null) {
            return null;
        }
        if (this.current instanceof Boolean) {
            return (Boolean)this.current;
        }
        return null;
    }
    
    public JSONNavi<T> object() {
        if (this.failure) {
            return this;
        }
        if (this.current == null && this.readonly) {
            this.failure("Can not create Object child in readonly", null);
        }
        if (this.current != null) {
            if (this.isObject()) {
                return this;
            }
            if (this.isArray()) {
                this.failure("can not use Object feature on Array.", null);
            }
            this.failure("Can not use current possition as Object", null);
        }
        else {
            this.current = this.mapper.createObject();
        }
        if (this.root == null) {
            this.root = (T)this.current;
        }
        else {
            this.store();
        }
        return this;
    }
    
    public JSONNavi<T> array() {
        if (this.failure) {
            return this;
        }
        if (this.current == null && this.readonly) {
            this.failure("Can not create Array child in readonly", null);
        }
        if (this.current != null) {
            if (this.isArray()) {
                return this;
            }
            if (this.isObject()) {
                this.failure("can not use Object feature on Array.", null);
            }
            this.failure("Can not use current possition as Object", null);
        }
        else {
            this.current = this.mapper.createArray();
        }
        if (this.root == null) {
            this.root = (T)this.current;
        }
        else {
            this.store();
        }
        return this;
    }
    
    public JSONNavi<T> set(final Number num) {
        if (this.failure) {
            return this;
        }
        this.current = num;
        this.store();
        return this;
    }
    
    public JSONNavi<T> set(final Boolean bool) {
        if (this.failure) {
            return this;
        }
        this.current = bool;
        this.store();
        return this;
    }
    
    public JSONNavi<T> set(final String text) {
        if (this.failure) {
            return this;
        }
        this.current = text;
        this.store();
        return this;
    }
    
    public T getRoot() {
        return this.root;
    }
    
    private void store() {
        final Object parent = this.stack.peek();
        if (this.isObject(parent)) {
            this.o(parent).put((String)this.missingKey, this.current);
        }
        else if (this.isArray(parent)) {
            final int index = ((Number)this.missingKey).intValue();
            final List<Object> lst = this.a(parent);
            while (lst.size() <= index) {
                lst.add(null);
            }
            lst.set(index, this.current);
        }
    }
    
    public boolean isArray() {
        return this.isArray(this.current);
    }
    
    public boolean isObject() {
        return this.isObject(this.current);
    }
    
    private boolean isArray(final Object obj) {
        return obj != null && obj instanceof List;
    }
    
    private boolean isObject(final Object obj) {
        return obj != null && obj instanceof Map;
    }
    
    private List<Object> a(final Object obj) {
        return (List<Object>)obj;
    }
    
    private Map<String, Object> o(final Object obj) {
        return (Map<String, Object>)obj;
    }
    
    public JSONNavi<?> at(int index) {
        if (this.failure) {
            return this;
        }
        if (!(this.current instanceof List)) {
            return this.failure("current node is not an Array", index);
        }
        final List<Object> lst = (List<Object>)this.current;
        if (index < 0) {
            index += lst.size();
            if (index < 0) {
                index = 0;
            }
        }
        if (index < lst.size()) {
            final Object next = lst.get(index);
            this.stack.add(this.current);
            this.path.add(index);
            this.current = next;
            return this;
        }
        if (this.readonly) {
            return this.failure("Out of bound exception for index", index);
        }
        this.stack.add(this.current);
        this.path.add(index);
        this.current = null;
        this.missingKey = index;
        return this;
    }
    
    public JSONNavi<?> atNext() {
        if (this.failure) {
            return this;
        }
        if (!(this.current instanceof List)) {
            return this.failure("current node is not an Array", null);
        }
        final List<Object> lst = (List<Object>)this.current;
        return this.at(lst.size());
    }
    
    public JSONNavi<?> up(int level) {
        while (level-- > 0 && this.stack.size() > 0) {
            this.current = this.stack.pop();
            this.path.pop();
        }
        return this;
    }
    
    public JSONNavi<?> up() {
        if (this.stack.size() > 0) {
            this.current = this.stack.pop();
            this.path.pop();
        }
        return this;
    }
    
    @Override
    public String toString() {
        if (this.failure) {
            return JSONValue.toJSONString(this.failureMessage, JSONNavi.ERROR_COMPRESS);
        }
        return JSONValue.toJSONString(this.root);
    }
    
    public String toString(final JSONStyle compression) {
        if (this.failure) {
            return JSONValue.toJSONString(this.failureMessage, compression);
        }
        return JSONValue.toJSONString(this.root, compression);
    }
    
    private JSONNavi<?> failure(final String err, final Object jPathPostfix) {
        this.failure = true;
        final StringBuilder sb = new StringBuilder();
        sb.append("Error: ");
        sb.append(err);
        sb.append(" at ");
        sb.append(this.getJPath());
        if (jPathPostfix != null) {
            if (jPathPostfix instanceof Integer) {
                sb.append('[').append(jPathPostfix).append(']');
            }
            else {
                sb.append('/').append(jPathPostfix);
            }
        }
        this.failureMessage = sb.toString();
        return this;
    }
    
    public String getJPath() {
        final StringBuilder sb = new StringBuilder();
        for (final Object o : this.path) {
            if (o instanceof String) {
                sb.append('/').append(o.toString());
            }
            else {
                sb.append('[').append(o.toString()).append(']');
            }
        }
        return sb.toString();
    }
}
