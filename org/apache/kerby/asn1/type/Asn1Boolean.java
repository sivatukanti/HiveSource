// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import java.io.IOException;
import org.apache.kerby.asn1.parse.Asn1ParseResult;
import org.apache.kerby.asn1.UniversalTag;

public class Asn1Boolean extends Asn1Simple<Boolean>
{
    private static final byte[] TRUE_BYTE;
    private static final byte[] FALSE_BYTE;
    public static final Asn1Boolean TRUE;
    public static final Asn1Boolean FALSE;
    
    public Asn1Boolean() {
        this((Boolean)null);
    }
    
    public Asn1Boolean(final Boolean value) {
        super(UniversalTag.BOOLEAN, value);
    }
    
    @Override
    protected int encodingBodyLength() {
        return 1;
    }
    
    @Override
    protected void decodeBody(final Asn1ParseResult parseResult) throws IOException {
        if (parseResult.getBodyLength() != 1) {
            throw new IOException("More than 1 byte found for Boolean");
        }
        super.decodeBody(parseResult);
    }
    
    @Override
    protected void toBytes() {
        this.setBytes(((boolean)this.getValue()) ? Asn1Boolean.TRUE_BYTE : Asn1Boolean.FALSE_BYTE);
    }
    
    @Override
    protected void toValue() throws IOException {
        final byte[] bytes = this.getBytes();
        if (bytes[0] == 0) {
            this.setValue(false);
        }
        else if ((bytes[0] & 0xFF) == 0xFF) {
            this.setValue(true);
        }
        else if (this.isBER()) {
            this.setValue(true);
        }
        else {
            this.setValue(false);
        }
    }
    
    static {
        TRUE_BYTE = new byte[] { -1 };
        FALSE_BYTE = new byte[] { 0 };
        TRUE = new Asn1Boolean(true);
        FALSE = new Asn1Boolean(false);
    }
}
