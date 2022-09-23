// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.jndi.java;

import javax.naming.RefAddr;
import javax.naming.Reference;
import org.eclipse.jetty.jndi.ContextFactory;
import javax.naming.StringRefAddr;
import org.eclipse.jetty.jndi.NamingUtil;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.Name;
import javax.naming.NameParser;
import org.eclipse.jetty.jndi.NamingContext;
import java.util.Hashtable;
import org.eclipse.jetty.util.log.Logger;
import javax.naming.Context;

public class javaRootURLContext implements Context
{
    private static Logger __log;
    public static final String URL_PREFIX = "java:";
    protected Hashtable _env;
    protected static NamingContext __nameRoot;
    protected static NameParser __javaNameParser;
    
    public javaRootURLContext(final Hashtable env) {
        this._env = env;
    }
    
    public Object lookup(final Name name) throws NamingException {
        return getRoot().lookup(this.stripProtocol(name));
    }
    
    public Object lookup(final String name) throws NamingException {
        return getRoot().lookup(this.stripProtocol(name));
    }
    
    public void bind(final Name name, final Object obj) throws NamingException {
        getRoot().bind(this.stripProtocol(name), obj);
    }
    
    public void bind(final String name, final Object obj) throws NamingException {
        getRoot().bind(this.stripProtocol(name), obj);
    }
    
    public void unbind(final String name) throws NamingException {
        getRoot().unbind(this.stripProtocol(name));
    }
    
    public void unbind(final Name name) throws NamingException {
        getRoot().unbind(this.stripProtocol(name));
    }
    
    public void rename(final String oldStr, final String newStr) throws NamingException {
        getRoot().rename(this.stripProtocol(oldStr), this.stripProtocol(newStr));
    }
    
    public void rename(final Name oldName, final Name newName) throws NamingException {
        getRoot().rename(this.stripProtocol(oldName), this.stripProtocol(newName));
    }
    
    public void rebind(final Name name, final Object obj) throws NamingException {
        getRoot().rebind(this.stripProtocol(name), obj);
    }
    
    public void rebind(final String name, final Object obj) throws NamingException {
        getRoot().rebind(this.stripProtocol(name), obj);
    }
    
    public Object lookupLink(final Name name) throws NamingException {
        return getRoot().lookupLink(this.stripProtocol(name));
    }
    
    public Object lookupLink(final String name) throws NamingException {
        return getRoot().lookupLink(this.stripProtocol(name));
    }
    
    public Context createSubcontext(final Name name) throws NamingException {
        return getRoot().createSubcontext(this.stripProtocol(name));
    }
    
    public Context createSubcontext(final String name) throws NamingException {
        return getRoot().createSubcontext(this.stripProtocol(name));
    }
    
    public void destroySubcontext(final Name name) throws NamingException {
        getRoot().destroySubcontext(this.stripProtocol(name));
    }
    
    public void destroySubcontext(final String name) throws NamingException {
        getRoot().destroySubcontext(this.stripProtocol(name));
    }
    
    public NamingEnumeration list(final Name name) throws NamingException {
        return getRoot().list(this.stripProtocol(name));
    }
    
    public NamingEnumeration list(final String name) throws NamingException {
        return getRoot().list(this.stripProtocol(name));
    }
    
    public NamingEnumeration listBindings(final Name name) throws NamingException {
        return getRoot().listBindings(this.stripProtocol(name));
    }
    
    public NamingEnumeration listBindings(final String name) throws NamingException {
        return getRoot().listBindings(this.stripProtocol(name));
    }
    
    public Name composeName(final Name name, final Name prefix) throws NamingException {
        return getRoot().composeName(name, prefix);
    }
    
    public String composeName(final String name, final String prefix) throws NamingException {
        return getRoot().composeName(name, prefix);
    }
    
    public void close() throws NamingException {
    }
    
    public String getNameInNamespace() throws NamingException {
        return "java:";
    }
    
    public NameParser getNameParser(final Name name) throws NamingException {
        return javaRootURLContext.__javaNameParser;
    }
    
    public NameParser getNameParser(final String name) throws NamingException {
        return javaRootURLContext.__javaNameParser;
    }
    
    public Object addToEnvironment(final String propName, final Object propVal) throws NamingException {
        return this._env.put(propName, propVal);
    }
    
    public Object removeFromEnvironment(final String propName) throws NamingException {
        return this._env.remove(propName);
    }
    
    public Hashtable getEnvironment() {
        return this._env;
    }
    
    public static NamingContext getRoot() {
        return javaRootURLContext.__nameRoot;
    }
    
    protected Name stripProtocol(final Name name) throws NamingException {
        if (name != null && name.size() > 0) {
            String head = name.get(0);
            if (javaRootURLContext.__log.isDebugEnabled()) {
                javaRootURLContext.__log.debug("Head element of name is: " + head, new Object[0]);
            }
            if (head.startsWith("java:")) {
                head = head.substring("java:".length());
                name.remove(0);
                if (head.length() > 0) {
                    name.add(0, head);
                }
                if (javaRootURLContext.__log.isDebugEnabled()) {
                    javaRootURLContext.__log.debug("name modified to " + name.toString(), new Object[0]);
                }
            }
        }
        return name;
    }
    
    protected String stripProtocol(final String name) {
        String newName = name;
        if (name != null && !name.equals("") && name.startsWith("java:")) {
            newName = name.substring("java:".length());
        }
        return newName;
    }
    
    static {
        javaRootURLContext.__log = NamingUtil.__log;
        try {
            javaRootURLContext.__javaNameParser = new javaNameParser();
            javaRootURLContext.__nameRoot = new NamingContext(null, null, null, javaRootURLContext.__javaNameParser);
            final StringRefAddr parserAddr = new StringRefAddr("parser", javaRootURLContext.__javaNameParser.getClass().getName());
            final Reference ref = new Reference("javax.naming.Context", parserAddr, ContextFactory.class.getName(), null);
            javaRootURLContext.__nameRoot.bind("comp", ref);
        }
        catch (Exception e) {
            javaRootURLContext.__log.warn(e);
        }
    }
}
