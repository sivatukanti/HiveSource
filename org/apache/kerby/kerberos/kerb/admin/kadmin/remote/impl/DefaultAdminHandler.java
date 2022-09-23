// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.remote.impl;

import java.util.List;
import java.nio.ByteBuffer;
import org.apache.kerby.kerberos.kerb.transport.KrbTransport;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.request.AdminRequest;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.AdminHandler;

public class DefaultAdminHandler extends AdminHandler
{
    @Override
    public void handleRequest(final AdminRequest adminRequest) throws KrbException {
        super.handleRequest(adminRequest);
        final KrbTransport transport = adminRequest.getTransport();
        ByteBuffer receiveMessage = null;
        try {
            receiveMessage = transport.receiveMessage();
        }
        catch (IOException e) {
            throw new KrbException("Admin receives response message failed", e);
        }
        super.onResponseMessage(adminRequest, receiveMessage);
    }
    
    @Override
    protected void sendMessage(final AdminRequest adminRequest, final ByteBuffer requestMessage) throws IOException {
        final KrbTransport transport = adminRequest.getTransport();
        transport.sendMessage(requestMessage);
    }
    
    public List<String> handleRequestForList(final AdminRequest adminRequest) throws KrbException {
        super.handleRequest(adminRequest);
        final KrbTransport transport = adminRequest.getTransport();
        ByteBuffer receiveMessage = null;
        List<String> prinicalList = null;
        try {
            receiveMessage = transport.receiveMessage();
            prinicalList = super.onResponseMessageForList(adminRequest, receiveMessage);
        }
        catch (IOException e) {
            throw new KrbException("Admin receives response message failed", e);
        }
        return prinicalList;
    }
}
