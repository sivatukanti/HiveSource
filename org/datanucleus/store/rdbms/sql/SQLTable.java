// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql;

import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.table.Table;

public class SQLTable
{
    protected SQLStatement stmt;
    protected Table table;
    protected DatastoreIdentifier alias;
    protected String groupName;
    
    SQLTable(final SQLStatement stmt, final Table tbl, final DatastoreIdentifier alias, final String grpName) {
        this.stmt = stmt;
        this.table = tbl;
        this.alias = alias;
        this.groupName = grpName;
    }
    
    public SQLStatement getSQLStatement() {
        return this.stmt;
    }
    
    public Table getTable() {
        return this.table;
    }
    
    public DatastoreIdentifier getAlias() {
        return this.alias;
    }
    
    public String getGroupName() {
        return this.groupName;
    }
    
    @Override
    public int hashCode() {
        if (this.alias != null) {
            return this.alias.hashCode() ^ this.table.hashCode();
        }
        return this.table.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SQLTable)) {
            return false;
        }
        final SQLTable other = (SQLTable)obj;
        if (other.alias == null) {
            return other.table == this.table && this.alias == null;
        }
        return other.table == this.table && other.alias.equals(this.alias);
    }
    
    @Override
    public String toString() {
        if (this.alias != null) {
            return this.table.toString() + " " + this.alias.toString();
        }
        return this.table.toString();
    }
}
