// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.preauth;

import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.pa.PaData;
import org.apache.kerby.kerberos.kerb.server.request.KdcRequest;
import org.apache.kerby.kerberos.kerb.preauth.PluginRequestContext;

public class PreauthHandle
{
    public KdcPreauth preauth;
    public PluginRequestContext requestContext;
    
    public PreauthHandle(final KdcPreauth preauth) {
        this.preauth = preauth;
    }
    
    public void initRequestContext(final KdcRequest kdcRequest) {
        this.requestContext = this.preauth.initRequestContext(kdcRequest);
    }
    
    public void provideEdata(final KdcRequest kdcRequest, final PaData outPaData) throws KrbException {
        this.preauth.provideEdata(kdcRequest, this.requestContext, outPaData);
    }
    
    public void verify(final KdcRequest kdcRequest, final PaDataEntry paData) throws KrbException {
        this.preauth.verify(kdcRequest, this.requestContext, paData);
    }
    
    public void providePaData(final KdcRequest kdcRequest, final PaData paData) {
        this.preauth.providePaData(kdcRequest, this.requestContext, paData);
    }
    
    public void destroy() {
        this.preauth.destroy();
    }
}
