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
import org.apache.kerby.kerberos.kerb.admin.message.GetprincsReq;

public class GetprincsRequest extends AdminRequest
{
    private String globString;
    
    public GetprincsRequest() {
        super(null);
        this.globString = null;
    }
    
    public GetprincsRequest(final String globString) {
        super(null);
        this.globString = null;
        this.globString = globString;
    }
    
    @Override
    public void process() throws KrbException {
        final GetprincsReq getprincsReq = new GetprincsReq();
        final XdrFieldInfo[] xdrFieldInfos = { new XdrFieldInfo(0, XdrDataType.ENUM, AdminMessageType.GET_PRINCS_REQ), new XdrFieldInfo(1, XdrDataType.INTEGER, 2), new XdrFieldInfo(2, XdrDataType.STRING, this.globString) };
        final AdminMessageCode value = new AdminMessageCode(xdrFieldInfos);
        byte[] encodeBytes;
        try {
            encodeBytes = value.encode();
        }
        catch (IOException e) {
            throw new KrbException("Xdr encode error when generate get principals request.", e);
        }
        final ByteBuffer messageBuffer = ByteBuffer.wrap(encodeBytes);
        getprincsReq.setMessageBuffer(messageBuffer);
        this.setAdminReq(getprincsReq);
    }
}
