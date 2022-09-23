// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import java.io.IOException;
import org.apache.kerby.asn1.parse.Asn1ParseResult;
import org.apache.kerby.asn1.Tag;

public class Asn1Specific extends AbstractAsn1Type<byte[]>
{
    public Asn1Specific(final Tag tag, final byte[] value) {
        super(tag, value);
    }
    
    public Asn1Specific(final Tag tag) {
        super(tag);
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
        this.setValue(parseResult.readBodyBytes());
    }
    
    @Override
    public String toString() {
        return this.tag().typeStr() + "  <" + this.getValue().length + " bytes>";
    }
}
