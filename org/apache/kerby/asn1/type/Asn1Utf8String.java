// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.kerby.asn1.UniversalTag;

public class Asn1Utf8String extends Asn1String
{
    public Asn1Utf8String() {
        this((String)null);
    }
    
    public Asn1Utf8String(final String value) {
        super(UniversalTag.UTF8_STRING, value);
    }
    
    @Override
    protected void toBytes() {
        final byte[] bytes = this.getValue().getBytes(StandardCharsets.UTF_8);
        this.setBytes(bytes);
    }
    
    @Override
    protected void toValue() throws IOException {
        final byte[] bytes = this.getBytes();
        this.setValue(new String(bytes, StandardCharsets.UTF_8));
    }
}
