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
import org.apache.kerby.kerberos.kerb.admin.message.RenamePrincipalReq;

public class RenamePrincipalRequest extends AdminRequest
{
    String newPrincipalName;
    
    public RenamePrincipalRequest(final String oldPrincipalName, final String newPrincipalName) {
        super(oldPrincipalName);
        this.newPrincipalName = newPrincipalName;
    }
    
    @Override
    public void process() throws KrbException {
        super.process();
        final RenamePrincipalReq renamePrincipalReq = new RenamePrincipalReq();
        final int paramNum = 2;
        final XdrFieldInfo[] xdrFieldInfos = new XdrFieldInfo[paramNum + 2];
        xdrFieldInfos[0] = new XdrFieldInfo(0, XdrDataType.ENUM, AdminMessageType.RENAME_PRINCIPAL_REQ);
        xdrFieldInfos[1] = new XdrFieldInfo(1, XdrDataType.INTEGER, paramNum);
        xdrFieldInfos[2] = new XdrFieldInfo(2, XdrDataType.STRING, this.getPrincipal());
        xdrFieldInfos[3] = new XdrFieldInfo(3, XdrDataType.STRING, this.newPrincipalName);
        final AdminMessageCode value = new AdminMessageCode(xdrFieldInfos);
        byte[] encodeBytes;
        try {
            encodeBytes = value.encode();
        }
        catch (IOException e) {
            throw new KrbException("Xdr encode error when generate rename principal request.", e);
        }
        final ByteBuffer messageBuffer = ByteBuffer.wrap(encodeBytes);
        renamePrincipalReq.setMessageBuffer(messageBuffer);
        this.setAdminReq(renamePrincipalReq);
    }
}
