// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.remote;

import java.util.Arrays;
import java.util.List;
import org.apache.kerby.xdr.XdrFieldInfo;
import org.apache.kerby.kerberos.kerb.admin.message.AdminMessageType;
import org.apache.kerby.xdr.type.XdrStructType;
import org.apache.kerby.kerberos.kerb.admin.message.AdminMessageCode;
import java.nio.ByteBuffer;
import org.apache.kerby.kerberos.kerb.admin.message.AdminReq;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.admin.message.AdminMessage;
import org.apache.kerby.kerberos.kerb.admin.message.KadminCode;
import org.apache.kerby.kerberos.kerb.admin.kadmin.remote.request.AdminRequest;

public abstract class AdminHandler
{
    public void init(final AdminContext context) {
    }
    
    public void handleRequest(final AdminRequest adminRequest) throws KrbException {
        adminRequest.process();
        final AdminReq adminReq = adminRequest.getAdminReq();
        final ByteBuffer requestMessage = KadminCode.encodeMessage(adminReq);
        requestMessage.flip();
        try {
            this.sendMessage(adminRequest, requestMessage);
        }
        catch (IOException e) {
            throw new KrbException("Admin sends request message failed", e);
        }
    }
    
    public void onResponseMessage(final AdminRequest adminRequest, final ByteBuffer responseMessage) throws KrbException {
        final XdrStructType decoded = new AdminMessageCode();
        try {
            decoded.decode(responseMessage);
        }
        catch (IOException e) {
            throw new KrbException("On response message failed.", e);
        }
        final XdrFieldInfo[] fieldInfos = decoded.getValue().getXdrFieldInfos();
        final AdminMessageType type = (AdminMessageType)fieldInfos[0].getValue();
        switch (type) {
            case ADD_PRINCIPAL_REP: {
                if (adminRequest.getAdminReq().getAdminMessageType() == AdminMessageType.ADD_PRINCIPAL_REQ) {
                    System.out.println((String)fieldInfos[2].getValue());
                    break;
                }
                throw new KrbException("Response message type error: need " + AdminMessageType.ADD_PRINCIPAL_REP);
            }
            case DELETE_PRINCIPAL_REP: {
                if (adminRequest.getAdminReq().getAdminMessageType() == AdminMessageType.DELETE_PRINCIPAL_REQ) {
                    System.out.println((String)fieldInfos[2].getValue());
                    break;
                }
                throw new KrbException("Response message type error: need " + AdminMessageType.DELETE_PRINCIPAL_REP);
            }
            case RENAME_PRINCIPAL_REP: {
                if (adminRequest.getAdminReq().getAdminMessageType() == AdminMessageType.RENAME_PRINCIPAL_REQ) {
                    System.out.println((String)fieldInfos[2].getValue());
                    break;
                }
                throw new KrbException("Response message type error: need " + AdminMessageType.RENAME_PRINCIPAL_REP);
            }
            default: {
                throw new KrbException("Response message type error: " + type);
            }
        }
    }
    
    public List<String> onResponseMessageForList(final AdminRequest adminRequest, final ByteBuffer responseMessage) throws KrbException {
        List<String> princalsList = null;
        final XdrStructType decoded = new AdminMessageCode();
        try {
            decoded.decode(responseMessage);
        }
        catch (IOException e) {
            throw new KrbException("On response message failed.", e);
        }
        final XdrFieldInfo[] fieldInfos = decoded.getValue().getXdrFieldInfos();
        final AdminMessageType type = (AdminMessageType)fieldInfos[0].getValue();
        switch (type) {
            case GET_PRINCS_REP: {
                if (adminRequest.getAdminReq().getAdminMessageType() == AdminMessageType.GET_PRINCS_REQ) {
                    final String[] temp = ((String)fieldInfos[2].getValue()).trim().split(" ");
                    princalsList = Arrays.asList(temp);
                    return princalsList;
                }
                throw new KrbException("Response message type error: need " + AdminMessageType.GET_PRINCS_REP);
            }
            default: {
                throw new KrbException("Response message type error: " + type);
            }
        }
    }
    
    protected abstract void sendMessage(final AdminRequest p0, final ByteBuffer p1) throws IOException;
    
    protected abstract List<String> handleRequestForList(final AdminRequest p0) throws KrbException;
}
