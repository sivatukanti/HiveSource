// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.IOException;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class KerberosAuthException extends IOException
{
    static final long serialVersionUID = 31L;
    private String user;
    private String principal;
    private String keytabFile;
    private String ticketCacheFile;
    private String initialMessage;
    
    public KerberosAuthException(final String msg) {
        super(msg);
    }
    
    public KerberosAuthException(final Throwable cause) {
        super(cause);
    }
    
    public KerberosAuthException(final String initialMsg, final Throwable cause) {
        this(cause);
        this.initialMessage = initialMsg;
    }
    
    public void setUser(final String u) {
        this.user = u;
    }
    
    public void setPrincipal(final String p) {
        this.principal = p;
    }
    
    public void setKeytabFile(final String k) {
        this.keytabFile = k;
    }
    
    public void setTicketCacheFile(final String t) {
        this.ticketCacheFile = t;
    }
    
    public String getInitialMessage() {
        return this.initialMessage;
    }
    
    public String getKeytabFile() {
        return this.keytabFile;
    }
    
    public String getPrincipal() {
        return this.principal;
    }
    
    public String getTicketCacheFile() {
        return this.ticketCacheFile;
    }
    
    public String getUser() {
        return this.user;
    }
    
    @Override
    public String getMessage() {
        final StringBuilder sb = new StringBuilder();
        if (this.initialMessage != null) {
            sb.append(this.initialMessage);
        }
        if (this.user != null) {
            sb.append(" for user: " + this.user);
        }
        if (this.principal != null) {
            sb.append(" for principal: " + this.principal);
        }
        if (this.keytabFile != null) {
            sb.append(" from keytab " + this.keytabFile);
        }
        if (this.ticketCacheFile != null) {
            sb.append(" using ticket cache file: " + this.ticketCacheFile);
        }
        sb.append(" " + super.getMessage());
        return sb.toString();
    }
}
