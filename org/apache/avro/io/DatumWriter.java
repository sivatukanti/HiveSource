// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io;

import java.io.IOException;
import org.apache.avro.Schema;

public interface DatumWriter<D>
{
    void setSchema(final Schema p0);
    
    void write(final D p0, final Encoder p1) throws IOException;
}
