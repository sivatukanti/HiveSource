// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.UniversalTag;

public class Asn1VisibleString extends Asn1String
{
    public Asn1VisibleString() {
        this((String)null);
    }
    
    public Asn1VisibleString(final String value) {
        super(UniversalTag.VISIBLE_STRING, value);
    }
}
