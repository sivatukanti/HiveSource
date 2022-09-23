// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.loader;

import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.services.context.ContextImpl;

public abstract class ClassFactoryContext extends ContextImpl
{
    public static final String CONTEXT_ID = "ClassFactoryContext";
    private final ClassFactory cf;
    
    protected ClassFactoryContext(final ContextManager contextManager, final ClassFactory cf) {
        super(contextManager, "ClassFactoryContext");
        this.cf = cf;
    }
    
    public final ClassFactory getClassFactory() {
        return this.cf;
    }
    
    public abstract CompatibilitySpace getLockSpace() throws StandardException;
    
    public abstract PersistentSet getPersistentSet() throws StandardException;
    
    public abstract JarReader getJarReader();
    
    public final void cleanupOnError(final Throwable t) {
        if (t instanceof StandardException && ((StandardException)t).getSeverity() >= 40000) {
            this.popMe();
        }
    }
}
