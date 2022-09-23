// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.writer;

import java.lang.reflect.Type;
import java.io.IOException;
import net.minidev.json.parser.ParseException;

public abstract class JsonReaderI<T>
{
    public final JsonReader base;
    private static String ERR_MSG;
    
    static {
        JsonReaderI.ERR_MSG = "Invalid or non Implemented status";
    }
    
    public JsonReaderI(final JsonReader base) {
        this.base = base;
    }
    
    public JsonReaderI<?> startObject(final String key) throws ParseException, IOException {
        throw new RuntimeException(String.valueOf(JsonReaderI.ERR_MSG) + " startObject(String key) in " + this.getClass() + " key=" + key);
    }
    
    public JsonReaderI<?> startArray(final String key) throws ParseException, IOException {
        throw new RuntimeException(String.valueOf(JsonReaderI.ERR_MSG) + " startArray in " + this.getClass() + " key=" + key);
    }
    
    public void setValue(final Object current, final String key, final Object value) throws ParseException, IOException {
        throw new RuntimeException(String.valueOf(JsonReaderI.ERR_MSG) + " setValue in " + this.getClass() + " key=" + key);
    }
    
    public Object getValue(final Object current, final String key) {
        throw new RuntimeException(String.valueOf(JsonReaderI.ERR_MSG) + " getValue(Object current, String key) in " + this.getClass() + " key=" + key);
    }
    
    public Type getType(final String key) {
        throw new RuntimeException(String.valueOf(JsonReaderI.ERR_MSG) + " getType(String key) in " + this.getClass() + " key=" + key);
    }
    
    public void addValue(final Object current, final Object value) throws ParseException, IOException {
        throw new RuntimeException(String.valueOf(JsonReaderI.ERR_MSG) + " addValue(Object current, Object value) in " + this.getClass());
    }
    
    public Object createObject() {
        throw new RuntimeException(String.valueOf(JsonReaderI.ERR_MSG) + " createObject() in " + this.getClass());
    }
    
    public Object createArray() {
        throw new RuntimeException(String.valueOf(JsonReaderI.ERR_MSG) + " createArray() in " + this.getClass());
    }
    
    public T convert(final Object current) {
        return (T)current;
    }
}
