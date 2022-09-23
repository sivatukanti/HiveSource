// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.writer;

public class FakeMapper extends JsonReaderI<Object>
{
    public static JsonReaderI<Object> DEFAULT;
    
    static {
        FakeMapper.DEFAULT = new FakeMapper();
    }
    
    private FakeMapper() {
        super(null);
    }
    
    @Override
    public JsonReaderI<?> startObject(final String key) {
        return this;
    }
    
    @Override
    public JsonReaderI<?> startArray(final String key) {
        return this;
    }
    
    @Override
    public void setValue(final Object current, final String key, final Object value) {
    }
    
    @Override
    public void addValue(final Object current, final Object value) {
    }
    
    @Override
    public Object createObject() {
        return null;
    }
    
    @Override
    public Object createArray() {
        return null;
    }
}
