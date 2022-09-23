// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.connection;

import org.datanucleus.util.StringUtils;
import org.datanucleus.Transaction;
import org.datanucleus.ExecutionContext;
import java.util.HashMap;
import java.util.Map;
import org.datanucleus.store.StoreManager;

public abstract class AbstractConnectionFactory implements ConnectionFactory
{
    protected StoreManager storeMgr;
    protected Map options;
    protected String resourceType;
    
    public AbstractConnectionFactory(final StoreManager storeMgr, final String resourceType) {
        this.options = null;
        this.storeMgr = storeMgr;
        this.resourceType = resourceType;
        if (resourceType != null) {
            if (resourceType.equals("tx")) {
                final String configuredResourceTypeProperty = storeMgr.getStringProperty("datanucleus.connection.resourceType");
                if (configuredResourceTypeProperty != null) {
                    if (this.options == null) {
                        this.options = new HashMap();
                    }
                    this.options.put("resource-type", configuredResourceTypeProperty);
                }
            }
            else {
                final String configuredResourceTypeProperty = storeMgr.getStringProperty("datanucleus.connection2.resourceType");
                if (configuredResourceTypeProperty != null) {
                    if (this.options == null) {
                        this.options = new HashMap();
                    }
                    this.options.put("resource-type", configuredResourceTypeProperty);
                }
            }
        }
    }
    
    @Override
    public ManagedConnection getConnection(final ExecutionContext ec, final Transaction txn, final Map options) {
        final Map addedOptions = new HashMap();
        if (options != null) {
            addedOptions.putAll(options);
        }
        if (this.options != null) {
            addedOptions.putAll(this.options);
        }
        final ManagedConnection mconn = this.storeMgr.getConnectionManager().allocateConnection(this, ec, txn, addedOptions);
        ((AbstractManagedConnection)mconn).incrementUseCount();
        return mconn;
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public String toString() {
        return "ConnectionFactory:" + this.resourceType + "[" + StringUtils.toJVMIDString(this) + "]";
    }
}
