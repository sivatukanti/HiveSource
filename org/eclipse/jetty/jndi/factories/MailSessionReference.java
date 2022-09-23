// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.jndi.factories;

import org.eclipse.jetty.util.security.Password;
import javax.mail.PasswordAuthentication;
import java.util.Iterator;
import java.util.Map;
import javax.naming.StringRefAddr;
import java.util.Enumeration;
import javax.mail.Authenticator;
import javax.mail.Session;
import javax.naming.RefAddr;
import java.util.Properties;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import javax.naming.Reference;

public class MailSessionReference extends Reference implements ObjectFactory
{
    public MailSessionReference() {
        super("javax.mail.Session", MailSessionReference.class.getName(), null);
    }
    
    public Object getObjectInstance(final Object ref, final Name arg1, final Context arg2, final Hashtable arg3) throws Exception {
        if (ref == null) {
            return null;
        }
        final Reference reference = (Reference)ref;
        final Properties props = new Properties();
        String user = null;
        String password = null;
        final Enumeration refs = reference.getAll();
        while (refs.hasMoreElements()) {
            final RefAddr refAddr = refs.nextElement();
            final String name = refAddr.getType();
            final String value = (String)refAddr.getContent();
            if (name.equalsIgnoreCase("user")) {
                user = value;
            }
            else if (name.equalsIgnoreCase("pwd")) {
                password = value;
            }
            else {
                props.put(name, value);
            }
        }
        if (password == null) {
            return Session.getInstance(props);
        }
        return Session.getInstance(props, new PasswordAuthenticator(user, password));
    }
    
    public void setUser(final String user) {
        final StringRefAddr addr = (StringRefAddr)this.get("user");
        if (addr != null) {
            throw new RuntimeException("user already set on SessionReference, can't be changed");
        }
        this.add(new StringRefAddr("user", user));
    }
    
    public void setPassword(final String password) {
        final StringRefAddr addr = (StringRefAddr)this.get("pwd");
        if (addr != null) {
            throw new RuntimeException("password already set on SessionReference, can't be changed");
        }
        this.add(new StringRefAddr("pwd", password));
    }
    
    public void setProperties(final Properties properties) {
        for (final Map.Entry e : properties.entrySet()) {
            final StringRefAddr sref = (StringRefAddr)this.get(e.getKey());
            if (sref != null) {
                throw new RuntimeException("property " + e.getKey() + " already set on Session reference, can't be changed");
            }
            this.add(new StringRefAddr(e.getKey(), e.getValue()));
        }
    }
    
    public static class PasswordAuthenticator extends Authenticator
    {
        PasswordAuthentication passwordAuthentication;
        private String user;
        private String password;
        
        public PasswordAuthenticator() {
        }
        
        public PasswordAuthenticator(final String user, final String password) {
            this.passwordAuthentication = new PasswordAuthentication(user, password.startsWith("OBF:") ? Password.deobfuscate(password) : password);
        }
        
        public PasswordAuthentication getPasswordAuthentication() {
            return this.passwordAuthentication;
        }
        
        public void setUser(final String user) {
            this.user = user;
        }
        
        public String getUser() {
            return this.user;
        }
        
        public String getPassword() {
            return this.password;
        }
        
        public void setPassword(final String password) {
            this.password = password;
        }
    }
}
