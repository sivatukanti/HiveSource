// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.kdc;

import org.apache.kerby.kerberos.kerb.type.base.KrbMessageType;

public class TgsReq extends KdcReq
{
    public TgsReq() {
        super(KrbMessageType.TGS_REQ);
    }
}
