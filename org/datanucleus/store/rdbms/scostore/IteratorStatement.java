// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.scostore;

import org.datanucleus.store.rdbms.mapping.StatementMappingIndex;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import org.datanucleus.store.rdbms.sql.SQLStatement;
import org.datanucleus.store.scostore.Store;

public class IteratorStatement
{
    Store backingStore;
    SQLStatement sqlStmt;
    StatementClassMapping stmtClassMapping;
    StatementMappingIndex ownerMapIndex;
    
    public IteratorStatement(final Store store, final SQLStatement stmt, final StatementClassMapping stmtClassMapping) {
        this.sqlStmt = null;
        this.stmtClassMapping = null;
        this.ownerMapIndex = null;
        this.backingStore = store;
        this.sqlStmt = stmt;
        this.stmtClassMapping = stmtClassMapping;
    }
    
    public Store getBackingStore() {
        return this.backingStore;
    }
    
    public SQLStatement getSQLStatement() {
        return this.sqlStmt;
    }
    
    public StatementClassMapping getStatementClassMapping() {
        return this.stmtClassMapping;
    }
    
    public StatementMappingIndex getOwnerMapIndex() {
        return this.ownerMapIndex;
    }
    
    public void setOwnerMapIndex(final StatementMappingIndex idx) {
        this.ownerMapIndex = idx;
    }
}
