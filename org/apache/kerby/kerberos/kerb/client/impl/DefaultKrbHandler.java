// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.client.impl;

import java.nio.ByteBuffer;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.transport.KrbTransport;
import org.apache.kerby.kerberos.kerb.client.request.KdcRequest;
import org.apache.kerby.kerberos.kerb.client.KrbHandler;

public class DefaultKrbHandler extends KrbHandler
{
    @Override
    public void handleRequest(final KdcRequest kdcRequest, final boolean tryNextKdc) throws KrbException {
        final KrbTransport transport = (KrbTransport)kdcRequest.getSessionData();
        transport.setAttachment(kdcRequest);
        super.handleRequest(kdcRequest, tryNextKdc);
        ByteBuffer receivedMessage = null;
        try {
            receivedMessage = transport.receiveMessage();
        }
        catch (IOException e) {
            throw new KrbException("Receiving response message failed", e);
        }
        super.onResponseMessage(kdcRequest, receivedMessage);
    }
    
    @Override
    protected void sendMessage(final KdcRequest kdcRequest, final ByteBuffer requestMessage) throws IOException {
        final KrbTransport transport = (KrbTransport)kdcRequest.getSessionData();
        transport.sendMessage(requestMessage);
    }
}
