// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server;

import org.slf4j.LoggerFactory;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.kerberos.kerb.type.base.KrbMessage;
import org.apache.kerby.kerberos.kerb.server.request.KdcRequest;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import org.apache.kerby.kerberos.kerb.type.base.KrbError;
import org.apache.kerby.kerberos.kerb.server.request.AsRequest;
import org.apache.kerby.kerberos.kerb.type.kdc.AsReq;
import org.apache.kerby.kerberos.kerb.server.request.TgsRequest;
import org.apache.kerby.kerberos.kerb.type.kdc.TgsReq;
import org.apache.kerby.kerberos.kerb.type.kdc.KdcReq;
import org.apache.kerby.kerberos.kerb.type.base.KrbMessageType;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.KrbErrorCode;
import org.apache.kerby.kerberos.kerb.KrbCodec;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import org.slf4j.Logger;

public class KdcHandler
{
    private static final Logger LOG;
    private final KdcContext kdcContext;
    
    public KdcHandler(final KdcContext kdcContext) {
        this.kdcContext = kdcContext;
    }
    
    public ByteBuffer handleMessage(final ByteBuffer receivedMessage, final boolean isTcp, final InetAddress remoteAddress) throws KrbException {
        KdcRequest kdcRequest = null;
        final ByteBuffer message = receivedMessage.duplicate();
        KrbMessage krbRequest;
        try {
            krbRequest = KrbCodec.decodeMessage(receivedMessage);
        }
        catch (IOException e) {
            KdcHandler.LOG.error("Krb decoding message failed", e);
            throw new KrbException(KrbErrorCode.KRB_AP_ERR_MSG_TYPE, "Krb decoding message failed");
        }
        final KrbMessageType messageType = krbRequest.getMsgType();
        if (messageType == KrbMessageType.TGS_REQ || messageType == KrbMessageType.AS_REQ) {
            final KdcReq kdcReq = (KdcReq)krbRequest;
            final String realm = this.getRequestRealm(kdcReq);
            if (realm == null || !this.kdcContext.getKdcRealm().equals(realm)) {
                KdcHandler.LOG.error("Invalid realm from kdc request: " + realm);
                throw new KrbException(KrbErrorCode.WRONG_REALM, "Invalid realm from kdc request: " + realm);
            }
            if (messageType == KrbMessageType.TGS_REQ) {
                kdcRequest = new TgsRequest((TgsReq)kdcReq, this.kdcContext);
            }
            else {
                if (messageType != KrbMessageType.AS_REQ) {
                    KdcHandler.LOG.error("Invalid message type: " + messageType);
                    throw new KrbException(KrbErrorCode.KRB_AP_ERR_MSG_TYPE);
                }
                kdcRequest = new AsRequest((AsReq)kdcReq, this.kdcContext);
            }
        }
        if (kdcRequest == null) {
            throw new KrbException("Kdc request is null.");
        }
        kdcRequest.setReqPackage(message);
        if (remoteAddress == null) {
            throw new KrbException("Remote address is null, not available.");
        }
        kdcRequest.setClientAddress(remoteAddress);
        kdcRequest.isTcp(isTcp);
        KrbMessage krbResponse;
        try {
            kdcRequest.process();
            krbResponse = kdcRequest.getReply();
        }
        catch (Throwable e2) {
            if (e2 instanceof KdcRecoverableException) {
                krbResponse = this.handleRecoverableException((KdcRecoverableException)e2, kdcRequest);
            }
            else {
                final KrbError krbError = new KrbError();
                krbError.setStime(KerberosTime.now());
                krbError.setSusec(100);
                KrbErrorCode errorCode = KrbErrorCode.UNKNOWN_ERR;
                if (e2 instanceof KrbException && ((KrbException)e2).getKrbErrorCode() != null) {
                    errorCode = ((KrbException)e2).getKrbErrorCode();
                }
                krbError.setErrorCode(errorCode);
                krbError.setCrealm(this.kdcContext.getKdcRealm());
                if (kdcRequest.getClientPrincipal() != null) {
                    krbError.setCname(kdcRequest.getClientPrincipal());
                }
                krbError.setRealm(this.kdcContext.getKdcRealm());
                if (kdcRequest.getServerPrincipal() != null) {
                    krbError.setSname(kdcRequest.getServerPrincipal());
                }
                else {
                    final PrincipalName serverPrincipal = kdcRequest.getKdcReq().getReqBody().getSname();
                    serverPrincipal.setRealm(kdcRequest.getKdcReq().getReqBody().getRealm());
                    krbError.setSname(serverPrincipal);
                }
                if (KrbErrorCode.KRB_AP_ERR_BAD_INTEGRITY.equals(errorCode)) {
                    krbError.setEtext("PREAUTH_FAILED");
                }
                else {
                    krbError.setEtext(e2.getMessage());
                }
                krbResponse = krbError;
            }
        }
        final int bodyLen = krbResponse.encodingLength();
        ByteBuffer responseMessage;
        if (isTcp) {
            responseMessage = ByteBuffer.allocate(bodyLen + 4);
            responseMessage.putInt(bodyLen);
        }
        else {
            responseMessage = ByteBuffer.allocate(bodyLen);
        }
        KrbCodec.encode(krbResponse, responseMessage);
        responseMessage.flip();
        return responseMessage;
    }
    
    private KrbMessage handleRecoverableException(final KdcRecoverableException e, final KdcRequest kdcRequest) throws KrbException {
        KdcHandler.LOG.info("KRB error occurred while processing request:" + e.getMessage());
        final KrbError error = e.getKrbError();
        error.setStime(KerberosTime.now());
        error.setSusec(100);
        error.setErrorCode(e.getKrbError().getErrorCode());
        error.setRealm(this.kdcContext.getKdcRealm());
        if (kdcRequest != null) {
            error.setSname(kdcRequest.getKdcReq().getReqBody().getCname());
        }
        else {
            error.setSname(new PrincipalName("NONE"));
        }
        error.setEtext(e.getMessage());
        return error;
    }
    
    private String getRequestRealm(final KdcReq kdcReq) {
        String realm = kdcReq.getReqBody().getRealm();
        if (realm == null && kdcReq.getReqBody().getCname() != null) {
            realm = kdcReq.getReqBody().getCname().getRealm();
        }
        return realm;
    }
    
    static {
        LOG = LoggerFactory.getLogger(KdcHandler.class);
    }
}
