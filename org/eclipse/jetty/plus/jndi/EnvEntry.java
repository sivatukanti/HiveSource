// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jndi;

import javax.naming.NamingException;

public class EnvEntry extends NamingEntry
{
    private boolean overrideWebXml;
    
    public EnvEntry(final Object scope, final String jndiName, final Object objToBind, final boolean overrideWebXml) throws NamingException {
        super(scope, jndiName);
        this.save(objToBind);
        this.overrideWebXml = overrideWebXml;
    }
    
    public EnvEntry(final String jndiName, final Object objToBind, final boolean overrideWebXml) throws NamingException {
        super(jndiName);
        this.save(objToBind);
        this.overrideWebXml = overrideWebXml;
    }
    
    public EnvEntry(final String jndiName, final Object objToBind) throws NamingException {
        this(jndiName, objToBind, false);
    }
    
    public boolean isOverrideWebXml() {
        return this.overrideWebXml;
    }
}
