// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.remote.request;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.admin.message.AdminReq;
import org.apache.kerby.kerberos.kerb.transport.KrbTransport;

public class AdminRequest
{
    private String principal;
    private KrbTransport transport;
    private AdminReq adminReq;
    
    public AdminRequest(final String principal) {
        this.principal = principal;
    }
    
    public String getPrincipal() {
        return this.principal;
    }
    
    public void setPrincipal(final String principal) {
        this.principal = principal;
    }
    
    public void setAdminReq(final AdminReq adminReq) {
        this.adminReq = adminReq;
    }
    
    public AdminReq getAdminReq() {
        return this.adminReq;
    }
    
    public void process() throws KrbException {
    }
    
    public void setTransport(final KrbTransport transport) {
        this.transport = transport;
    }
    
    public KrbTransport getTransport() {
        return this.transport;
    }
}
