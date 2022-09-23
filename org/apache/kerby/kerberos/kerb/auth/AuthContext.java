// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.auth;

import org.apache.kerby.kerberos.kerb.type.ap.Authenticator;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import java.util.List;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.base.HostAddress;
import org.apache.kerby.asn1.type.Asn1Flags;

public class AuthContext
{
    private Asn1Flags flags;
    private HostAddress remoteAddress;
    private int remotePort;
    private HostAddress localAddress;
    private int localPort;
    private EncryptionKey key;
    private EncryptionKey sendSubkey;
    private EncryptionKey recvSubkey;
    private int remoteSeqNum;
    private int localSeqNum;
    private List<EncryptionType> permittedEncTypes;
    private EncryptionType negotiatedEncType;
    private Authenticator authenticator;
    
    public Asn1Flags getFlags() {
        return this.flags;
    }
    
    public void setFlags(final Asn1Flags flags) {
        this.flags = flags;
    }
    
    public HostAddress getRemoteAddress() {
        return this.remoteAddress;
    }
    
    public void setRemoteAddress(final HostAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
    
    public int getRemotePort() {
        return this.remotePort;
    }
    
    public void setRemotePort(final int remotePort) {
        this.remotePort = remotePort;
    }
    
    public HostAddress getLocalAddress() {
        return this.localAddress;
    }
    
    public void setLocalAddress(final HostAddress localAddress) {
        this.localAddress = localAddress;
    }
    
    public int getLocalPort() {
        return this.localPort;
    }
    
    public void setLocalPort(final int localPort) {
        this.localPort = localPort;
    }
    
    public EncryptionKey getKey() {
        return this.key;
    }
    
    public void setKey(final EncryptionKey key) {
        this.key = key;
    }
    
    public EncryptionKey getSendSubkey() {
        return this.sendSubkey;
    }
    
    public void setSendSubkey(final EncryptionKey sendSubkey) {
        this.sendSubkey = sendSubkey;
    }
    
    public EncryptionKey getRecvSubkey() {
        return this.recvSubkey;
    }
    
    public void setRecvSubkey(final EncryptionKey recvSubkey) {
        this.recvSubkey = recvSubkey;
    }
    
    public int getRemoteSeqNum() {
        return this.remoteSeqNum;
    }
    
    public void setRemoteSeqNum(final int remoteSeqNum) {
        this.remoteSeqNum = remoteSeqNum;
    }
    
    public int getLocalSeqNum() {
        return this.localSeqNum;
    }
    
    public void setLocalSeqNum(final int localSeqNum) {
        this.localSeqNum = localSeqNum;
    }
    
    public List<EncryptionType> getPermittedEncTypes() {
        return this.permittedEncTypes;
    }
    
    public void setPermittedEncTypes(final List<EncryptionType> permittedEncTypes) {
        this.permittedEncTypes = permittedEncTypes;
    }
    
    public EncryptionType getNegotiatedEncType() {
        return this.negotiatedEncType;
    }
    
    public void setNegotiatedEncType(final EncryptionType negotiatedEncType) {
        this.negotiatedEncType = negotiatedEncType;
    }
    
    public Authenticator getAuthenticator() {
        return this.authenticator;
    }
    
    public void setAuthenticator(final Authenticator authenticator) {
        this.authenticator = authenticator;
    }
}
