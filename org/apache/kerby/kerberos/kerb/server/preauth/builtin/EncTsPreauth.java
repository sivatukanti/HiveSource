// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.preauth.builtin;

import org.apache.kerby.kerberos.kerb.server.KdcContext;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.common.EncryptionUtil;
import org.apache.kerby.kerberos.kerb.type.base.KeyUsage;
import org.apache.kerby.kerberos.kerb.type.pa.PaEncTsEnc;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.KrbErrorCode;
import org.apache.kerby.kerberos.kerb.KrbCodec;
import org.apache.kerby.kerberos.kerb.type.base.EncryptedData;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.preauth.PluginRequestContext;
import org.apache.kerby.kerberos.kerb.server.request.KdcRequest;
import org.apache.kerby.kerberos.kerb.preauth.PreauthPluginMeta;
import org.apache.kerby.kerberos.kerb.preauth.builtin.EncTsPreauthMeta;
import org.apache.kerby.kerberos.kerb.server.preauth.AbstractPreauthPlugin;

public class EncTsPreauth extends AbstractPreauthPlugin
{
    public EncTsPreauth() {
        super(new EncTsPreauthMeta());
    }
    
    @Override
    public boolean verify(final KdcRequest kdcRequest, final PluginRequestContext requestContext, final PaDataEntry paData) throws KrbException {
        final EncryptedData encData = KrbCodec.decode(paData.getPaDataValue(), EncryptedData.class);
        final EncryptionKey clientKey = kdcRequest.getClientKey(encData.getEType());
        if (clientKey == null) {
            throw new KrbException(KrbErrorCode.KDC_ERR_ETYPE_NOSUPP);
        }
        final PaEncTsEnc timestamp = EncryptionUtil.unseal(encData, clientKey, KeyUsage.AS_REQ_PA_ENC_TS, PaEncTsEnc.class);
        final KdcContext kdcContext = kdcRequest.getKdcContext();
        final long clockSkew = kdcContext.getConfig().getAllowableClockSkew() * 1000L;
        if (!timestamp.getAllTime().isInClockSkew(clockSkew)) {
            throw new KrbException(KrbErrorCode.KDC_ERR_PREAUTH_FAILED);
        }
        return true;
    }
}
