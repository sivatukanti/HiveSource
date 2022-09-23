// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import java.io.IOException;
import org.apache.kerby.asn1.UniversalTag;

public class Asn1BitString extends Asn1Simple<byte[]>
{
    private int padding;
    
    public Asn1BitString() {
        this((byte[])null);
    }
    
    public Asn1BitString(final byte[] value) {
        this(value, 0);
    }
    
    public Asn1BitString(final byte[] value, final int padding) {
        super(UniversalTag.BIT_STRING, value);
        this.padding = padding;
    }
    
    public void setPadding(final int padding) {
        this.padding = padding;
    }
    
    public int getPadding() {
        return this.padding;
    }
    
    @Override
    protected int encodingBodyLength() {
        final byte[] body = this.getValue();
        if (body != null) {
            return body.length + 1;
        }
        return 0;
    }
    
    @Override
    protected void toBytes() {
        final byte[] bytes = new byte[this.encodingBodyLength()];
        final byte[] body = this.getValue();
        if (body != null) {
            bytes[0] = (byte)this.padding;
            System.arraycopy(body, 0, bytes, 1, bytes.length - 1);
        }
        this.setBytes(bytes);
    }
    
    @Override
    protected void toValue() throws IOException {
        final byte[] bytes = this.getBytes();
        if (bytes.length < 1) {
            throw new IOException("Bad stream, zero bytes found for bitstring");
        }
        final int paddingBits = bytes[0];
        this.validatePaddingBits(paddingBits);
        this.setPadding(paddingBits);
        final byte[] newBytes = new byte[bytes.length - 1];
        if (bytes.length > 1) {
            System.arraycopy(bytes, 1, newBytes, 0, bytes.length - 1);
        }
        this.setValue(newBytes);
    }
    
    private void validatePaddingBits(final int paddingBits) throws IOException {
        if (paddingBits < 0 || paddingBits > 7) {
            throw new IOException("Bad padding number: " + paddingBits + ", should be in [0, 7]");
        }
    }
    
    @Override
    public String toString() {
        final String typeStr = this.tag().typeStr() + " [" + "tag=" + this.tag() + ", len=" + this.getHeaderLength() + "+" + this.getBodyLength() + "] ";
        final byte[] valueBytes = this.getValue();
        String valueStr = "<null>";
        if (valueBytes != null) {
            valueStr = "<" + valueBytes.length + " bytes>";
        }
        return typeStr + valueStr;
    }
}
