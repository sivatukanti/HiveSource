// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.UniversalTag;

public class Asn1UniversalString extends Asn1String
{
    public Asn1UniversalString() {
        this((String)null);
    }
    
    public Asn1UniversalString(final String value) {
        super(UniversalTag.UNIVERSAL_STRING, value);
    }
}
