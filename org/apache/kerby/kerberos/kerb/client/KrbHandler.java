// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.List;
import org.apache.kerby.kerberos.kerb.type.base.KrbMessage;
import org.apache.kerby.kerberos.kerb.type.base.EtypeInfo2Entry;
import org.apache.kerby.kerberos.kerb.type.base.EtypeInfo2;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataType;
import org.apache.kerby.kerberos.kerb.type.pa.PaDataEntry;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import java.util.ArrayList;
import org.apache.kerby.kerberos.kerb.type.base.MethodData;
import org.apache.kerby.kerberos.kerb.KrbErrorCode;
import org.apache.kerby.kerberos.kerb.type.base.KrbError;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcRep;
import org.apache.kerby.kerberos.kerb.type.base.KrbMessageType;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcReq;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.kerberos.kerb.KrbCodec;
import java.nio.ByteBuffer;
import org.apache.kerby.kerberos.kerb.transport.KrbTransport;
import org.apache.kerby.kerberos.kerb.client.request.KdcRequest;
import org.apache.kerby.kerberos.kerb.client.preauth.PreauthHandler;
import org.slf4j.Logger;

public abstract class KrbHandler
{
    private static final Logger LOG;
    private PreauthHandler preauthHandler;
    
    public void init(final KrbContext context) {
        (this.preauthHandler = new PreauthHandler()).init(context);
    }
    
    public void handleRequest(final KdcRequest kdcRequest, final boolean tryNextKdc) throws KrbException {
        if (!tryNextKdc || kdcRequest.getKdcReq() == null) {
            kdcRequest.process();
        }
        final KdcReq kdcReq = kdcRequest.getKdcReq();
        final int bodyLen = kdcReq.encodingLength();
        final KrbTransport transport = (KrbTransport)kdcRequest.getSessionData();
        final boolean isTcp = transport.isTcp();
        ByteBuffer requestMessage;
        if (!isTcp) {
            requestMessage = ByteBuffer.allocate(bodyLen);
        }
        else {
            requestMessage = ByteBuffer.allocate(bodyLen + 4);
            requestMessage.putInt(bodyLen);
        }
        KrbCodec.encode(kdcReq, requestMessage);
        requestMessage.flip();
        try {
            this.sendMessage(kdcRequest, requestMessage);
        }
        catch (IOException e) {
            throw new KrbException("sending message failed", e);
        }
    }
    
    public void onResponseMessage(final KdcRequest kdcRequest, final ByteBuffer responseMessage) throws KrbException {
        KrbMessage kdcRep = null;
        try {
            kdcRep = KrbCodec.decodeMessage(responseMessage);
        }
        catch (IOException e) {
            throw new KrbException("Krb decoding message failed", e);
        }
        final KrbMessageType messageType = kdcRep.getMsgType();
        if (messageType == KrbMessageType.AS_REP) {
            kdcRequest.processResponse((KdcRep)kdcRep);
        }
        else if (messageType == KrbMessageType.TGS_REP) {
            kdcRequest.processResponse((KdcRep)kdcRep);
        }
        else if (messageType == KrbMessageType.KRB_ERROR) {
            final KrbError error = (KrbError)kdcRep;
            KrbHandler.LOG.info("KDC server response with message: " + error.getErrorCode().getMessage());
            if (error.getErrorCode() != KrbErrorCode.KDC_ERR_PREAUTH_REQUIRED) {
                KrbHandler.LOG.info(error.getErrorCode().getMessage());
                throw new KrbException(error.getErrorCode(), error.getEtext());
            }
            final MethodData methodData = KrbCodec.decode(error.getEdata(), MethodData.class);
            final List<PaDataEntry> paDataEntryList = methodData.getElements();
            final List<EncryptionType> encryptionTypes = new ArrayList<EncryptionType>();
            for (final PaDataEntry paDataEntry : paDataEntryList) {
                if (paDataEntry.getPaDataType() == PaDataType.ETYPE_INFO2) {
                    final EtypeInfo2 etypeInfo2 = KrbCodec.decode(paDataEntry.getPaDataValue(), EtypeInfo2.class);
                    final List<EtypeInfo2Entry> info2Entries = etypeInfo2.getElements();
                    for (final EtypeInfo2Entry info2Entry : info2Entries) {
                        encryptionTypes.add(info2Entry.getEtype());
                    }
                }
            }
            kdcRequest.setEncryptionTypes(encryptionTypes);
            kdcRequest.setPreauthRequired(true);
            kdcRequest.resetPrequthContxt();
            this.handleRequest(kdcRequest, false);
            KrbHandler.LOG.info("Retry with the new kdc request including pre-authentication.");
        }
    }
    
    protected abstract void sendMessage(final KdcRequest p0, final ByteBuffer p1) throws IOException;
    
    static {
        LOG = LoggerFactory.getLogger(KrbHandler.class);
    }
}
