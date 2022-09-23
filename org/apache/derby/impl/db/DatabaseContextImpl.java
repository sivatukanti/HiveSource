// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.db;

import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.services.context.Context;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.db.Database;
import org.apache.derby.iapi.db.DatabaseContext;
import org.apache.derby.iapi.services.context.ContextImpl;

final class DatabaseContextImpl extends ContextImpl implements DatabaseContext
{
    private final Database db;
    
    DatabaseContextImpl(final ContextManager contextManager, final Database db) {
        super(contextManager, "Database");
        this.db = db;
    }
    
    public void cleanupOnError(final Throwable t) {
        if (!(t instanceof StandardException)) {
            return;
        }
        final StandardException ex = (StandardException)t;
        if (ex.getSeverity() < 40000) {
            return;
        }
        this.popMe();
        if (ex.getSeverity() >= 45000) {
            final DataDictionary dataDictionary = this.db.getDataDictionary();
            if (dataDictionary != null) {
                dataDictionary.disableIndexStatsRefresher();
            }
        }
        if (ex.getSeverity() == 45000) {
            ContextService.getFactory().notifyAllActiveThreads(this);
            Monitor.getMonitor().shutdown(this.db);
        }
    }
    
    public boolean equals(final Object o) {
        return o instanceof DatabaseContext && ((DatabaseContextImpl)o).db == this.db;
    }
    
    public int hashCode() {
        return this.db.hashCode();
    }
    
    public Database getDatabase() {
        return this.db;
    }
}
