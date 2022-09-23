// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.UniversalTag;

public class Asn1T61String extends Asn1String
{
    public Asn1T61String() {
        this((String)null);
    }
    
    public Asn1T61String(final String value) {
        super(UniversalTag.T61_STRING, value);
    }
}
