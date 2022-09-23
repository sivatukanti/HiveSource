// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.preauth.builtin;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.kerberos.kerb.KrbCodec;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.client.request.TgsRequestWithTgt;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.pa.PaData;
import org.apache.kerby.kerberos.kerb.preauth.PluginRequestContext;
import org.apache.kerby.kerberos.kerb.client.request.KdcRequest;
import org.apache.kerby.kerberos.kerb.preauth.PreauthPluginMeta;
import org.apache.kerby.kerberos.kerb.preauth.builtin.TgtPreauthMeta;
import org.apache.kerby.kerberos.kerb.client.preauth.AbstractPreauthPlugin;

public class TgtPreauth extends AbstractPreauthPlugin
{
    public TgtPreauth() {
        super(new TgtPreauthMeta());
    }
    
    @Override
    public void tryFirst(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final PaData outPadata) throws KrbException {
        outPadata.addElement(this.makeEntry(kdcRequest));
    }
    
    @Override
    public boolean process(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final PaDataEntry inPadata, final PaData outPadata) throws KrbException {
        outPadata.addElement(this.makeEntry(kdcRequest));
        return true;
    }
    
    private PaDataEntry makeEntry(final KdcRequest kdcRequest) throws KrbException {
        final TgsRequestWithTgt tgsRequest = (TgsRequestWithTgt)kdcRequest;
        final PaDataEntry paEntry = new PaDataEntry();
        paEntry.setPaDataType(PaDataType.TGS_REQ);
        paEntry.setPaDataValue(KrbCodec.encode(tgsRequest.getApReq()));
        return paEntry;
    }
}
