// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import java.io.IOException;
import org.apache.kerby.asn1.parse.Asn1Item;
import org.apache.kerby.asn1.parse.Asn1ParseResult;
import org.apache.kerby.asn1.UniversalTag;

public class Asn1OctetString extends Asn1Simple<byte[]>
{
    public Asn1OctetString() {
        this((byte[])null);
    }
    
    public Asn1OctetString(final byte[] value) {
        super(UniversalTag.OCTET_STRING, value);
    }
    
    @Override
    protected byte[] encodeBody() {
        return this.getValue();
    }
    
    @Override
    protected int encodingBodyLength() {
        if (this.getValue() != null) {
            return this.getValue().length;
        }
        return 0;
    }
    
    @Override
    protected void decodeBody(final Asn1ParseResult parseResult) throws IOException {
        final Asn1Item item = (Asn1Item)parseResult;
        this.setValue(item.readBodyBytes());
    }
    
    @Override
    public String toString() {
        final String typeStr = this.tag().typeStr() + " [" + "tag=" + this.tag() + ", len=" + this.getHeaderLength() + "+" + this.getBodyLength() + "] ";
        final byte[] valueBytes = this.getValue();
        String valueStr = "<null>";
        if (valueBytes != null) {
            valueStr = "<" + valueBytes.length + " octets>";
        }
        return typeStr + valueStr;
    }
}
