// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server;

import org.apache.kerby.kerberos.kerb.type.base.KrbError;
import org.apache.kerby.kerberos.kerb.KrbErrorException;

public class KdcRecoverableException extends KrbErrorException
{
    private static final long serialVersionUID = -3472169380126256193L;
    
    public KdcRecoverableException(final KrbError krbError) {
        super(krbError);
    }
}
