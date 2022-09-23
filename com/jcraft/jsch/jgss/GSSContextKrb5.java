// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch.jgss;

import org.ietf.jgss.MessageProp;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import com.jcraft.jsch.JSchException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.Oid;
import com.jcraft.jsch.GSSContext;

public class GSSContextKrb5 implements GSSContext
{
    private static final String pUseSubjectCredsOnly = "javax.security.auth.useSubjectCredsOnly";
    private static String useSubjectCredsOnly;
    private org.ietf.jgss.GSSContext context;
    
    public GSSContextKrb5() {
        this.context = null;
    }
    
    public void create(final String user, final String host) throws JSchException {
        try {
            final Oid krb5 = new Oid("1.2.840.113554.1.2.2");
            final Oid principalName = new Oid("1.2.840.113554.1.2.2.1");
            final GSSManager mgr = GSSManager.getInstance();
            final GSSCredential crd = null;
            String cname = host;
            try {
                cname = InetAddress.getByName(cname).getCanonicalHostName();
            }
            catch (UnknownHostException ex2) {}
            final GSSName _host = mgr.createName("host/" + cname, principalName);
            (this.context = mgr.createContext(_host, krb5, crd, 0)).requestMutualAuth(true);
            this.context.requestConf(true);
            this.context.requestInteg(true);
            this.context.requestCredDeleg(true);
            this.context.requestAnonymity(false);
        }
        catch (GSSException ex) {
            throw new JSchException(ex.toString());
        }
    }
    
    public boolean isEstablished() {
        return this.context.isEstablished();
    }
    
    public byte[] init(final byte[] token, final int s, final int l) throws JSchException {
        try {
            if (GSSContextKrb5.useSubjectCredsOnly == null) {
                setSystemProperty("javax.security.auth.useSubjectCredsOnly", "false");
            }
            return this.context.initSecContext(token, 0, l);
        }
        catch (GSSException ex) {
            throw new JSchException(ex.toString());
        }
        catch (SecurityException ex2) {
            throw new JSchException(ex2.toString());
        }
        finally {
            if (GSSContextKrb5.useSubjectCredsOnly == null) {
                setSystemProperty("javax.security.auth.useSubjectCredsOnly", "true");
            }
        }
    }
    
    public byte[] getMIC(final byte[] message, final int s, final int l) {
        try {
            final MessageProp prop = new MessageProp(0, true);
            return this.context.getMIC(message, s, l, prop);
        }
        catch (GSSException ex) {
            return null;
        }
    }
    
    public void dispose() {
        try {
            this.context.dispose();
        }
        catch (GSSException ex) {}
    }
    
    private static String getSystemProperty(final String key) {
        try {
            return System.getProperty(key);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    private static void setSystemProperty(final String key, final String value) {
        try {
            System.setProperty(key, value);
        }
        catch (Exception ex) {}
    }
    
    static {
        GSSContextKrb5.useSubjectCredsOnly = getSystemProperty("javax.security.auth.useSubjectCredsOnly");
    }
}
