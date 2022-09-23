// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io;

import java.io.IOException;
import org.apache.avro.Schema;

public interface DatumReader<D>
{
    void setSchema(final Schema p0);
    
    D read(final D p0, final Decoder p1) throws IOException;
}
