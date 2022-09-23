// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb;

import org.apache.kerby.kerberos.kerb.type.base.KrbError;

public class KrbErrorException extends KrbException
{
    private static final long serialVersionUID = -6726737724490205771L;
    private KrbError krbError;
    
    public KrbErrorException(final KrbError krbError) {
        super(krbError.getErrorCode().getMessage());
        this.krbError = krbError;
    }
    
    public KrbError getKrbError() {
        return this.krbError;
    }
}
