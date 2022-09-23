// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.parse.Asn1ParseResult;
import java.io.IOException;
import org.apache.kerby.asn1.UniversalTag;

public class Asn1BmpString extends Asn1Simple<String>
{
    public Asn1BmpString() {
        super(UniversalTag.BMP_STRING);
    }
    
    public Asn1BmpString(final String value) {
        super(UniversalTag.BMP_STRING, value);
    }
    
    @Override
    protected int encodingBodyLength() {
        return this.getValue().length() * 2;
    }
    
    @Override
    protected void toBytes() {
        final String strValue = this.getValue();
        final int len = strValue.length();
        final byte[] bytes = new byte[len * 2];
        for (int i = 0; i != len; ++i) {
            final char c = strValue.charAt(i);
            bytes[2 * i] = (byte)(c >> 8);
            bytes[2 * i + 1] = (byte)c;
        }
        this.setBytes(bytes);
    }
    
    @Override
    protected void toValue() throws IOException {
        final byte[] bytes = this.getBytes();
        final char[] chars = new char[bytes.length / 2];
        for (int i = 0; i != chars.length; ++i) {
            chars[i] = (char)(bytes[2 * i] << 8 | (bytes[2 * i + 1] & 0xFF));
        }
        this.setValue(new String(chars));
    }
    
    @Override
    protected void decodeBody(final Asn1ParseResult parseResult) throws IOException {
        if (parseResult.getBodyLength() % 2 != 0) {
            throw new IOException("Bad stream, BMP string expecting multiple of 2 bytes");
        }
        super.decodeBody(parseResult);
    }
}
