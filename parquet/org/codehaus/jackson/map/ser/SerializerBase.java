// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.ser;

import parquet.org.codehaus.jackson.type.JavaType;

@Deprecated
public abstract class SerializerBase<T> extends parquet.org.codehaus.jackson.map.ser.std.SerializerBase<T>
{
    protected SerializerBase(final Class<T> t) {
        super(t);
    }
    
    protected SerializerBase(final JavaType type) {
        super(type);
    }
    
    protected SerializerBase(final Class<?> t, final boolean dummy) {
        super(t, dummy);
    }
}
