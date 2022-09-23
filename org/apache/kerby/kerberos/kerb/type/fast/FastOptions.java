// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.fast;

import org.apache.kerby.asn1.type.Asn1Flags;

public class FastOptions extends Asn1Flags
{
    public FastOptions() {
        this(0);
    }
    
    public FastOptions(final int value) {
        this.setFlags(value);
    }
}
