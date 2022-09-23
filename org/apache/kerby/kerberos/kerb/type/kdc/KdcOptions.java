// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.kdc;

import org.apache.kerby.asn1.type.Asn1Flags;

public class KdcOptions extends Asn1Flags
{
    public KdcOptions() {
        this(0);
    }
    
    public KdcOptions(final int value) {
        this.setFlags(value);
    }
}
