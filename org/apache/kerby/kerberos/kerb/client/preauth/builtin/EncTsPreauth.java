// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.preauth.builtin;

import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.kerberos.kerb.KrbCodec;
import org.apache.kerby.asn1.type.Asn1Encodeable;
import org.apache.kerby.kerberos.kerb.common.EncryptionUtil;
import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.kerberos.kerb.type.pa.PaEncTsEnc;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.kerberos.kerb.preauth.PaFlag;
import org.apache.kerby.kerberos.kerb.preauth.PaFlags;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.type.pa.PaData;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.preauth.PluginRequestContext;
import org.apache.kerby.kerberos.kerb.client.request.KdcRequest;
import org.apache.kerby.kerberos.kerb.preauth.PreauthPluginMeta;
import org.apache.kerby.kerberos.kerb.preauth.builtin.EncTsPreauthMeta;
import org.apache.kerby.kerberos.kerb.client.preauth.AbstractPreauthPlugin;

public class EncTsPreauth extends AbstractPreauthPlugin
{
    public EncTsPreauth() {
        super(new EncTsPreauthMeta());
    }
    
    @Override
    public void prepareQuestions(final KdcRequest kdcRequest, final PluginRequestContext requestContext) throws KrbException {
        kdcRequest.needAsKey();
    }
    
    @Override
    public void tryFirst(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final PaData outPadata) throws KrbException {
        if (kdcRequest.getAsKey() == null) {
            kdcRequest.needAsKey();
        }
        outPadata.addElement(this.makeEntry(kdcRequest));
    }
    
    @Override
    public boolean process(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final PaDataEntry inPadata, final PaData outPadata) throws KrbException {
        if (kdcRequest.getAsKey() == null) {
            kdcRequest.needAsKey();
        }
        outPadata.addElement(this.makeEntry(kdcRequest));
        return true;
    }
    
    @Override
    public PaFlags getFlags(final PaDataType paType) {
        final PaFlags paFlags = new PaFlags(0);
        paFlags.setFlag(PaFlag.PA_REAL);
        return paFlags;
    }
    
    private PaDataEntry makeEntry(final KdcRequest kdcRequest) throws KrbException {
        final PaEncTsEnc paTs = new PaEncTsEnc();
        paTs.setPaTimestamp(kdcRequest.getPreauthTime());
        final EncryptedData paDataValue = EncryptionUtil.seal(paTs, kdcRequest.getAsKey(), KeyUsage.AS_REQ_PA_ENC_TS);
        final PaDataEntry tsPaEntry = new PaDataEntry();
        tsPaEntry.setPaDataType(PaDataType.ENC_TIMESTAMP);
        tsPaEntry.setPaDataValue(KrbCodec.encode(paDataValue));
        return tsPaEntry;
    }
}
