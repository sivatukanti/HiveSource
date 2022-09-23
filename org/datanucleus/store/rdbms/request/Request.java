// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.request;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.rdbms.table.AbstractClassTable;
import org.datanucleus.store.rdbms.key.PrimaryKey;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.util.Localiser;

public abstract class Request
{
    protected static final Localiser LOCALISER;
    protected DatastoreClass table;
    protected PrimaryKey key;
    
    public Request(final DatastoreClass table) {
        this.table = table;
        this.key = ((AbstractClassTable)table).getPrimaryKey();
    }
    
    public abstract void execute(final ObjectProvider p0);
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
