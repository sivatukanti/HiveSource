// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import org.apache.kerby.asn1.UniversalTag;

public class Asn1NumericsString extends Asn1String
{
    public Asn1NumericsString() {
        this((String)null);
    }
    
    public Asn1NumericsString(final String value) {
        super(UniversalTag.NUMERIC_STRING, value);
        if (value != null && !isNumeric(value)) {
            throw new IllegalArgumentException("Invalid numeric string");
        }
    }
    
    public static boolean isNumeric(final String s) {
        for (int i = s.length() - 1; i >= 0; --i) {
            final char c = s.charAt(i);
            if ((c < '0' || c > '9') && c != ' ') {
                return false;
            }
        }
        return true;
    }
}
