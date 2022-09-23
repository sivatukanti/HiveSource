// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jndi;

import javax.naming.Name;
import javax.naming.NameParser;
import org.eclipse.jetty.jndi.NamingUtil;
import javax.naming.LinkRef;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.eclipse.jetty.util.log.Logger;

public abstract class NamingEntry
{
    private static Logger __log;
    public static final String __contextName = "__";
    protected final Object _scope;
    protected final String _jndiName;
    protected String _namingEntryNameString;
    protected String _objectNameString;
    
    @Override
    public String toString() {
        return this._jndiName;
    }
    
    protected NamingEntry(final Object scope, final String jndiName) throws NamingException {
        this._scope = scope;
        this._jndiName = jndiName;
    }
    
    protected NamingEntry(final String jndiName) throws NamingException {
        this(null, jndiName);
    }
    
    public void bindToENC(final String localName) throws NamingException {
        final InitialContext ic = new InitialContext();
        final Context env = (Context)ic.lookup("java:comp/env");
        NamingEntry.__log.debug("Binding java:comp/env/" + localName + " to " + this._objectNameString, new Object[0]);
        NamingUtil.bind(env, localName, new LinkRef(this._objectNameString));
    }
    
    public void unbindENC() {
        try {
            final InitialContext ic = new InitialContext();
            final Context env = (Context)ic.lookup("java:comp/env");
            NamingEntry.__log.debug("Unbinding java:comp/env/" + this.getJndiName(), new Object[0]);
            env.unbind(this.getJndiName());
        }
        catch (NamingException e) {
            NamingEntry.__log.warn(e);
        }
    }
    
    public void release() {
        try {
            final InitialContext ic = new InitialContext();
            ic.unbind(this._objectNameString);
            ic.unbind(this._namingEntryNameString);
            this._namingEntryNameString = null;
            this._objectNameString = null;
        }
        catch (NamingException e) {
            NamingEntry.__log.warn(e);
        }
    }
    
    public String getJndiName() {
        return this._jndiName;
    }
    
    public String getJndiNameInScope() {
        return this._objectNameString;
    }
    
    protected void save(final Object object) throws NamingException {
        NamingEntry.__log.debug("SAVE {} in {}", this, this._scope);
        final InitialContext ic = new InitialContext();
        final NameParser parser = ic.getNameParser("");
        final Name prefix = NamingEntryUtil.getNameForScope(this._scope);
        final Name namingEntryName = NamingEntryUtil.makeNamingEntryName(parser, this.getJndiName());
        namingEntryName.addAll(0, prefix);
        NamingUtil.bind(ic, this._namingEntryNameString = namingEntryName.toString(), this);
        final Name objectName = parser.parse(this.getJndiName());
        objectName.addAll(0, prefix);
        NamingUtil.bind(ic, this._objectNameString = objectName.toString(), object);
    }
    
    static {
        NamingEntry.__log = NamingUtil.__log;
    }
}
