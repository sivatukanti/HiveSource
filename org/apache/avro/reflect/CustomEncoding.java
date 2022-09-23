// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.reflect;

import org.apache.avro.io.Decoder;
import java.io.IOException;
import org.apache.avro.io.Encoder;
import org.apache.avro.Schema;

public abstract class CustomEncoding<T>
{
    protected Schema schema;
    
    protected abstract void write(final Object p0, final Encoder p1) throws IOException;
    
    protected abstract T read(final Object p0, final Decoder p1) throws IOException;
    
    T read(final Decoder in) throws IOException {
        return this.read(null, in);
    }
    
    protected Schema getSchema() {
        return this.schema;
    }
}
