// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.UniversalTag;

public class Asn1IA5String extends Asn1String
{
    public Asn1IA5String() {
        super(UniversalTag.IA5_STRING);
    }
    
    public Asn1IA5String(final String value) {
        super(UniversalTag.IA5_STRING, value);
    }
}
