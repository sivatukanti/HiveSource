// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.request;

import org.apache.kerby.kerberos.kerb.type.base.TransitedEncodingType;
import org.apache.kerby.kerberos.kerb.type.base.TransitedEncoding;

public class TgtTicketIssuer extends TicketIssuer
{
    public TgtTicketIssuer(final AsRequest kdcRequest) {
        super(kdcRequest);
    }
    
    @Override
    protected TransitedEncoding getTransitedEncoding() {
        final TransitedEncoding transEnc = new TransitedEncoding();
        transEnc.setTrType(TransitedEncodingType.DOMAIN_X500_COMPRESS);
        final byte[] empty = new byte[0];
        transEnc.setContents(empty);
        return transEnc;
    }
}
