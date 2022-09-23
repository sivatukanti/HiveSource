// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import java.nio.charset.StandardCharsets;
import org.apache.kerby.asn1.UniversalTag;

public class Asn1T61Utf8String extends Asn1String
{
    public Asn1T61Utf8String() {
        this((String)null);
    }
    
    public Asn1T61Utf8String(final String value) {
        super(UniversalTag.T61_STRING, value);
    }
    
    @Override
    protected void toBytes() {
        this.setBytes(this.getValue().getBytes(StandardCharsets.UTF_8));
    }
    
    @Override
    protected void toValue() {
        this.setValue(new String(this.getBytes(), StandardCharsets.UTF_8));
    }
}
