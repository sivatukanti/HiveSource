// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.valuegenerator;

import java.util.Properties;
import org.datanucleus.store.StoreManager;

public abstract class AbstractDatastoreGenerator extends AbstractGenerator
{
    protected StoreManager storeMgr;
    protected ValueGenerationConnectionProvider connectionProvider;
    
    public AbstractDatastoreGenerator(final String name, final Properties props) {
        super(name, props);
        this.allocationSize = 1;
    }
    
    public void setStoreManager(final StoreManager storeMgr) {
        this.storeMgr = storeMgr;
    }
    
    public void setConnectionProvider(final ValueGenerationConnectionProvider provider) {
        this.connectionProvider = provider;
    }
}
