// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.preauth;

import org.apache.kerby.kerberos.kerb.preauth.PaFlags;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.type.pa.PaData;
import org.apache.kerby.KOptions;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.client.request.KdcRequest;
import org.apache.kerby.kerberos.kerb.preauth.PluginRequestContext;

public class PreauthHandle
{
    public KrbPreauth preauth;
    public PluginRequestContext requestContext;
    
    public PreauthHandle(final KrbPreauth preauth) {
        this.preauth = preauth;
    }
    
    public void initRequestContext(final KdcRequest kdcRequest) {
        this.requestContext = this.preauth.initRequestContext(kdcRequest);
    }
    
    public void prepareQuestions(final KdcRequest kdcRequest) throws KrbException {
        this.preauth.prepareQuestions(kdcRequest, this.requestContext);
    }
    
    public void setPreauthOptions(final KdcRequest kdcRequest, final KOptions preauthOptions) throws KrbException {
        this.preauth.setPreauthOptions(kdcRequest, this.requestContext, preauthOptions);
    }
    
    public void tryFirst(final KdcRequest kdcRequest, final PaData outPadata) throws KrbException {
        this.preauth.tryFirst(kdcRequest, this.requestContext, outPadata);
    }
    
    public boolean process(final KdcRequest kdcRequest, final PaDataEntry inPadata, final PaData outPadata) throws KrbException {
        return this.preauth.process(kdcRequest, this.requestContext, inPadata, outPadata);
    }
    
    public boolean tryAgain(final KdcRequest kdcRequest, final PaDataType paType, final PaData errPadata, final PaData paData) {
        return this.preauth.tryAgain(kdcRequest, this.requestContext, paType, errPadata, paData);
    }
    
    public boolean isReal(final PaDataType paType) {
        final PaFlags paFlags = this.preauth.getFlags(paType);
        return paFlags.isReal();
    }
}
