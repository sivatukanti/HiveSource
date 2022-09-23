// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.reflect;

import java.lang.reflect.Field;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.Decoder;
import java.io.IOException;

abstract class FieldAccessor
{
    protected abstract Object get(final Object p0) throws IllegalAccessException;
    
    protected abstract void set(final Object p0, final Object p1) throws IllegalAccessException, IOException;
    
    protected void read(final Object object, final Decoder in) throws IOException {
    }
    
    protected void write(final Object object, final Encoder out) throws IOException {
    }
    
    protected boolean supportsIO() {
        return false;
    }
    
    protected abstract Field getField();
    
    protected boolean isStringable() {
        return false;
    }
    
    protected boolean isCustomEncoded() {
        return false;
    }
}
