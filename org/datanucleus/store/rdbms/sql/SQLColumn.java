// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql;

import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.table.Column;

public class SQLColumn
{
    protected SQLTable table;
    protected Column column;
    protected DatastoreIdentifier alias;
    
    public SQLColumn(final SQLTable table, final Column col, final DatastoreIdentifier alias) {
        this.table = table;
        this.column = col;
        this.alias = alias;
    }
    
    public SQLTable getTable() {
        return this.table;
    }
    
    public Column getColumn() {
        return this.column;
    }
    
    public DatastoreIdentifier getAlias() {
        return this.alias;
    }
    
    @Override
    public String toString() {
        String str = null;
        if (this.table.getAlias() != null) {
            str = this.table.getAlias() + "." + this.column.getIdentifier().toString();
        }
        else {
            str = this.table.getTable() + "." + this.column.getIdentifier().toString();
        }
        if (this.alias != null) {
            return this.column.applySelectFunction(str) + " AS " + this.alias;
        }
        return this.column.applySelectFunction(str);
    }
}
