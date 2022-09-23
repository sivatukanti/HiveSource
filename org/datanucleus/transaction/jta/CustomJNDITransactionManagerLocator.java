// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.transaction.jta;

import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.NucleusContext;

public class CustomJNDITransactionManagerLocator extends JNDIBasedTransactionManagerLocator
{
    protected String jndiLocation;
    
    public CustomJNDITransactionManagerLocator(final NucleusContext nucleusCtx) {
        this.jndiLocation = nucleusCtx.getPersistenceConfiguration().getStringProperty("datanucleus.jtaJndiLocation");
        if (this.jndiLocation == null) {
            new NucleusException("NO Custom JNDI Location specified in configuration.").setFatal();
        }
    }
    
    @Override
    public String getJNDIName() {
        return this.jndiLocation;
    }
}
