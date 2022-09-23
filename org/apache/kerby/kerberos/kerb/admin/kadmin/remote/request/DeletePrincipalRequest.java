// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.remote.request;

import org.apache.kerby.kerberos.kerb.admin.message.AdminReq;
import java.nio.ByteBuffer;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.admin.message.AdminMessageCode;
import org.apache.kerby.kerberos.kerb.admin.message.AdminMessageType;
import org.apache.kerby.xdr.XdrDataType;
import org.apache.kerby.xdr.XdrFieldInfo;
import org.apache.kerby.kerberos.kerb.admin.message.DeletePrincipalReq;

public class DeletePrincipalRequest extends AdminRequest
{
    public DeletePrincipalRequest(final String principal) {
        super(principal);
    }
    
    @Override
    public void process() throws KrbException {
        super.process();
        final DeletePrincipalReq deletePrincipalReq = new DeletePrincipalReq();
        final XdrFieldInfo[] xdrFieldInfos = { new XdrFieldInfo(0, XdrDataType.ENUM, AdminMessageType.DELETE_PRINCIPAL_REQ), new XdrFieldInfo(1, XdrDataType.INTEGER, 1), new XdrFieldInfo(2, XdrDataType.STRING, this.getPrincipal()) };
        final AdminMessageCode value = new AdminMessageCode(xdrFieldInfos);
        byte[] encodeBytes;
        try {
            encodeBytes = value.encode();
        }
        catch (IOException e) {
            throw new KrbException("Xdr encode error when generate delete principal request.", e);
        }
        final ByteBuffer messageBuffer = ByteBuffer.wrap(encodeBytes);
        deletePrincipalReq.setMessageBuffer(messageBuffer);
        this.setAdminReq(deletePrincipalReq);
    }
}
