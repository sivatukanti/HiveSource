// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.UniversalTag;

public class Asn1GeneralString extends Asn1String
{
    public Asn1GeneralString() {
        super(UniversalTag.GENERAL_STRING);
    }
    
    public Asn1GeneralString(final String value) {
        super(UniversalTag.GENERAL_STRING, value);
    }
}
