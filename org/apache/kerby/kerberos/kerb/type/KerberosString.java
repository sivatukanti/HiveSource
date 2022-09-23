// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type;

import org.apache.kerby.asn1.type.Asn1GeneralString;

public class KerberosString extends Asn1GeneralString
{
    public KerberosString() {
    }
    
    public KerberosString(final String value) {
        super(value);
        if (value != null) {
            for (final char c : value.toCharArray()) {
                if ((c & '\uff80') != 0x0) {
                    throw new IllegalArgumentException("The value contains non ASCII chars " + value);
                }
            }
        }
    }
}
