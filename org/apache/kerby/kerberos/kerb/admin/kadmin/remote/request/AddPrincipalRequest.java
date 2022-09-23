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
import org.apache.kerby.kerberos.kerb.admin.message.AddPrincipalReq;
import org.apache.kerby.KOptions;

public class AddPrincipalRequest extends AdminRequest
{
    private KOptions kOptions;
    private String password;
    
    public AddPrincipalRequest(final String principal) {
        super(principal);
    }
    
    public AddPrincipalRequest(final String principal, final KOptions kOptions) {
        super(principal);
        this.kOptions = kOptions;
    }
    
    public AddPrincipalRequest(final String principal, final String password) {
        super(principal);
        this.password = password;
    }
    
    public AddPrincipalRequest(final String princial, final KOptions kOptions, final String password) {
        super(princial);
        this.kOptions = kOptions;
        this.password = password;
    }
    
    @Override
    public void process() throws KrbException {
        super.process();
        final AddPrincipalReq addPrincipalReq = new AddPrincipalReq();
        final int paramNum = this.getParamNum();
        final XdrFieldInfo[] xdrFieldInfos = new XdrFieldInfo[paramNum + 2];
        xdrFieldInfos[0] = new XdrFieldInfo(0, XdrDataType.ENUM, AdminMessageType.ADD_PRINCIPAL_REQ);
        xdrFieldInfos[1] = new XdrFieldInfo(1, XdrDataType.INTEGER, paramNum);
        xdrFieldInfos[2] = new XdrFieldInfo(2, XdrDataType.STRING, this.getPrincipal());
        if (paramNum == 2 && this.kOptions != null) {
            xdrFieldInfos[3] = new XdrFieldInfo(3, XdrDataType.STRUCT, this.kOptions);
        }
        else if (paramNum == 2 && this.password != null) {
            xdrFieldInfos[3] = new XdrFieldInfo(3, XdrDataType.STRING, this.password);
        }
        else if (paramNum == 3) {
            xdrFieldInfos[3] = new XdrFieldInfo(3, XdrDataType.STRUCT, this.kOptions);
            xdrFieldInfos[4] = new XdrFieldInfo(4, XdrDataType.STRING, this.password);
        }
        final AdminMessageCode value = new AdminMessageCode(xdrFieldInfos);
        byte[] encodeBytes;
        try {
            encodeBytes = value.encode();
        }
        catch (IOException e) {
            throw new KrbException("Xdr encode error when generate add principal request.", e);
        }
        final ByteBuffer messageBuffer = ByteBuffer.wrap(encodeBytes);
        addPrincipalReq.setMessageBuffer(messageBuffer);
        this.setAdminReq(addPrincipalReq);
    }
    
    public int getParamNum() {
        int paramNum = 0;
        if (this.getPrincipal() == null) {
            throw new RuntimeException("Principal name missing.");
        }
        if (this.kOptions == null && this.password == null) {
            paramNum = 1;
        }
        else if (this.kOptions == null || this.password == null) {
            paramNum = 2;
        }
        else {
            paramNum = 3;
        }
        return paramNum;
    }
}
