// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.xdr.type;

import java.nio.ByteBuffer;
import java.io.IOException;

public interface XdrType
{
    int encodingLength() throws IOException;
    
    byte[] encode() throws IOException;
    
    void encode(final ByteBuffer p0) throws IOException;
    
    void decode(final byte[] p0) throws IOException;
    
    void decode(final ByteBuffer p0) throws IOException;
}
