// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.db;

import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.services.loader.JarReader;
import org.apache.derby.iapi.store.access.AccessFactory;
import org.apache.derby.iapi.services.loader.ClassFactoryContext;

final class StoreClassFactoryContext extends ClassFactoryContext
{
    private final AccessFactory store;
    private final JarReader jarReader;
    
    StoreClassFactoryContext(final ContextManager contextManager, final ClassFactory classFactory, final AccessFactory store, final JarReader jarReader) {
        super(contextManager, classFactory);
        this.store = store;
        this.jarReader = jarReader;
    }
    
    public CompatibilitySpace getLockSpace() throws StandardException {
        if (this.store == null) {
            return null;
        }
        return this.store.getTransaction(this.getContextManager()).getLockSpace();
    }
    
    public PersistentSet getPersistentSet() throws StandardException {
        if (this.store == null) {
            return null;
        }
        return this.store.getTransaction(this.getContextManager());
    }
    
    public JarReader getJarReader() {
        return this.jarReader;
    }
}
