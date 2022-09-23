// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import java.io.IOException;
import org.apache.kerby.asn1.parse.Asn1ParseResult;
import java.nio.ByteBuffer;
import org.apache.kerby.asn1.UniversalTag;

public final class Asn1Eoc extends Asn1Simple<Object>
{
    public static final Asn1Eoc INSTANCE;
    private static final byte[] EMPTY_BYTES;
    
    private Asn1Eoc() {
        super(UniversalTag.EOC, null);
    }
    
    @Override
    public void encode(final ByteBuffer buffer) {
        buffer.put((byte)0);
        buffer.put((byte)0);
    }
    
    @Override
    protected byte[] encodeBody() {
        return Asn1Eoc.EMPTY_BYTES;
    }
    
    @Override
    protected int encodingBodyLength() {
        return 0;
    }
    
    @Override
    protected void decodeBody(final Asn1ParseResult parseResult) throws IOException {
        if (parseResult.getBodyLength() != 0) {
            throw new IOException("Unexpected bytes found for EOC");
        }
    }
    
    @Override
    public String toString() {
        final String typeStr = this.tag().typeStr() + " [" + "tag=" + this.tag() + ", len=" + this.getHeaderLength() + "+" + this.getBodyLength() + "] ";
        return typeStr + "eoc";
    }
    
    static {
        INSTANCE = new Asn1Eoc();
        EMPTY_BYTES = new byte[0];
    }
}
