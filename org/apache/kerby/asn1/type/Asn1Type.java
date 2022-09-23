// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.TaggingOption;
import java.nio.ByteBuffer;
import java.io.IOException;
import org.apache.kerby.asn1.Tag;

public interface Asn1Type
{
    Tag tag();
    
    void usePrimitive(final boolean p0);
    
    boolean isPrimitive();
    
    void useDefinitiveLength(final boolean p0);
    
    boolean isDefinitiveLength();
    
    void useImplicit(final boolean p0);
    
    boolean isImplicit();
    
    void useDER();
    
    boolean isDER();
    
    void useBER();
    
    boolean isBER();
    
    void useCER();
    
    boolean isCER();
    
    int encodingLength() throws IOException;
    
    byte[] encode() throws IOException;
    
    void encode(final ByteBuffer p0) throws IOException;
    
    void decode(final byte[] p0) throws IOException;
    
    void decode(final ByteBuffer p0) throws IOException;
    
    byte[] taggedEncode(final TaggingOption p0) throws IOException;
    
    void taggedEncode(final ByteBuffer p0, final TaggingOption p1) throws IOException;
    
    void taggedDecode(final ByteBuffer p0, final TaggingOption p1) throws IOException;
    
    void taggedDecode(final byte[] p0, final TaggingOption p1) throws IOException;
    
    public enum EncodingType
    {
        BER, 
        DER, 
        CER;
    }
}
