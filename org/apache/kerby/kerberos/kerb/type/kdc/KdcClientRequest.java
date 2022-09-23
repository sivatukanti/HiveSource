// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.kdc;

import org.apache.kerby.kerberos.kerb.type.base.AuthToken;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import java.net.InetAddress;

public class KdcClientRequest
{
    private boolean isPreAuthenticated;
    private InetAddress clientAddress;
    private EncryptionType encryptionType;
    private EncryptionKey clientKey;
    private PrincipalName clientPrincipal;
    private AuthToken token;
    private boolean isToken;
    private boolean isPkinit;
    private boolean isAnonymous;
    
    public boolean isPreAuthenticated() {
        return this.isPreAuthenticated;
    }
    
    public void setPreAuthenticated(final boolean isPreAuthenticated) {
        this.isPreAuthenticated = isPreAuthenticated;
    }
    
    public InetAddress getClientAddress() {
        return this.clientAddress;
    }
    
    public void setClientAddress(final InetAddress clientAddress) {
        this.clientAddress = clientAddress;
    }
    
    public EncryptionType getEncryptionType() {
        return this.encryptionType;
    }
    
    public void setEncryptionType(final EncryptionType encryptionType) {
        this.encryptionType = encryptionType;
    }
    
    public EncryptionKey getClientKey() {
        return this.clientKey;
    }
    
    public void setClientKey(final EncryptionKey clientKey) {
        this.clientKey = clientKey;
    }
    
    public PrincipalName getClientPrincipal() {
        return this.clientPrincipal;
    }
    
    public void setClientPrincipal(final PrincipalName clientPrincipal) {
        this.clientPrincipal = clientPrincipal;
    }
    
    public AuthToken getToken() {
        return this.token;
    }
    
    public void setToken(final AuthToken token) {
        this.token = token;
    }
    
    public boolean isToken() {
        return this.isToken;
    }
    
    public void setToken(final boolean isToken) {
        this.isToken = isToken;
    }
    
    public boolean isPkinit() {
        return this.isPkinit;
    }
    
    public void setPkinit(final boolean isPkinit) {
        this.isPkinit = isPkinit;
    }
    
    public boolean isAnonymous() {
        return this.isAnonymous;
    }
    
    public void setAnonymous(final boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }
}
