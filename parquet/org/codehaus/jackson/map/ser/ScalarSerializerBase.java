// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.ser;

import parquet.org.codehaus.jackson.map.ser.std.SerializerBase;

@Deprecated
public abstract class ScalarSerializerBase<T> extends SerializerBase<T>
{
    protected ScalarSerializerBase(final Class<T> t) {
        super(t);
    }
    
    protected ScalarSerializerBase(final Class<?> t, final boolean dummy) {
        super(t);
    }
}
