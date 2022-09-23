// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import java.io.IOException;
import org.apache.kerby.asn1.parse.Asn1ParseResult;
import org.apache.kerby.asn1.UniversalTag;

public final class Asn1Null extends Asn1Simple<Object>
{
    public static final Asn1Null INSTANCE;
    private static final byte[] EMPTY_BYTES;
    
    private Asn1Null() {
        super(UniversalTag.NULL, null);
    }
    
    @Override
    protected byte[] encodeBody() {
        return Asn1Null.EMPTY_BYTES;
    }
    
    @Override
    protected int encodingBodyLength() {
        return 0;
    }
    
    @Override
    protected void decodeBody(final Asn1ParseResult parseResult) throws IOException {
        if (parseResult.getHeader().getLength() != 0) {
            throw new IOException("Unexpected bytes found for NULL");
        }
    }
    
    @Override
    public String toString() {
        final String typeStr = this.tag().typeStr() + " [" + "tag=" + this.tag() + ", len=" + this.getHeaderLength() + "+" + this.getBodyLength() + "] ";
        return typeStr + "null";
    }
    
    static {
        INSTANCE = new Asn1Null();
        EMPTY_BYTES = new byte[0];
    }
}
