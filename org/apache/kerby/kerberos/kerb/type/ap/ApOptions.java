// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.ap;

import org.apache.kerby.asn1.type.Asn1Flags;

public class ApOptions extends Asn1Flags
{
    public ApOptions() {
        this(0);
    }
    
    public ApOptions(final int value) {
        this.setFlags(value);
    }
}
