// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.preauth;

import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.type.Asn1Flags;

public class PaFlags extends Asn1Flags
{
    public PaFlags() {
        this(0);
    }
    
    public PaFlags(final int value) {
        this.setFlags(value);
    }
    
    public boolean isReal() {
        return this.isFlagSet(PaFlag.PA_REAL);
    }
}
