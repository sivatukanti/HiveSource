// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.UniversalTag;

public class Asn1PrintableString extends Asn1String
{
    public Asn1PrintableString() {
        this((String)null);
    }
    
    public Asn1PrintableString(final String value) {
        super(UniversalTag.PRINTABLE_STRING, value);
    }
}
