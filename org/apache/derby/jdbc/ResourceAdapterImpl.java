// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import javax.transaction.xa.XAException;
import java.util.Enumeration;
import java.sql.SQLException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.store.access.AccessFactory;
import java.util.Properties;
import org.apache.derby.iapi.store.access.xa.XAXactId;
import java.util.Hashtable;
import org.apache.derby.iapi.store.access.xa.XAResourceManager;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.jdbc.ResourceAdapter;

public class ResourceAdapterImpl implements ResourceAdapter, ModuleControl
{
    private boolean active;
    private XAResourceManager rm;
    private Hashtable<XAXactId, XATransactionState> connectionTable;
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        this.connectionTable = new Hashtable<XAXactId, XATransactionState>();
        this.rm = (XAResourceManager)((AccessFactory)Monitor.findServiceModule(this, "org.apache.derby.iapi.store.access.AccessFactory")).getXAResourceManager();
        this.active = true;
    }
    
    public void stop() {
        this.active = false;
        final Enumeration<XATransactionState> elements = this.connectionTable.elements();
        while (elements.hasMoreElements()) {
            final XATransactionState xaTransactionState = elements.nextElement();
            try {
                xaTransactionState.conn.close();
            }
            catch (SQLException ex) {}
        }
        this.active = false;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public synchronized Object findConnection(final XAXactId key) {
        return this.connectionTable.get(key);
    }
    
    public synchronized boolean addConnection(final XAXactId xaXactId, final Object o) {
        if (this.connectionTable.get(xaXactId) != null) {
            return false;
        }
        this.connectionTable.put(xaXactId, (XATransactionState)o);
        return true;
    }
    
    public synchronized Object removeConnection(final XAXactId key) {
        return this.connectionTable.remove(key);
    }
    
    public void cancelXATransaction(final XAXactId xaXactId, final String s) throws XAException {
        final XATransactionState xaTransactionState = (XATransactionState)this.findConnection(xaXactId);
        if (xaTransactionState != null) {
            xaTransactionState.cancel(s);
        }
    }
    
    public XAResourceManager getXAResourceManager() {
        return this.rm;
    }
}
