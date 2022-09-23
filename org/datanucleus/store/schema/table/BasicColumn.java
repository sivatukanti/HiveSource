// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.schema.table;

import org.datanucleus.store.StoreManager;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ColumnMetaData;

public class BasicColumn implements Column
{
    Table table;
    ColumnMetaData colmd;
    String identifier;
    AbstractMemberMetaData mmd;
    
    public BasicColumn(final Table tbl, final StoreManager storeMgr, final ColumnMetaData colmd) {
        this.table = tbl;
        this.colmd = colmd;
        this.identifier = colmd.getName();
    }
    
    @Override
    public Table getTable() {
        return this.table;
    }
    
    @Override
    public ColumnMetaData getColumnMetaData() {
        return this.colmd;
    }
    
    @Override
    public AbstractMemberMetaData getMemberMetaData() {
        return this.mmd;
    }
    
    public void setMemberMetaData(final AbstractMemberMetaData mmd) {
        this.mmd = mmd;
    }
    
    @Override
    public String getIdentifier() {
        return this.identifier;
    }
    
    @Override
    public String toString() {
        return "Column " + this.identifier;
    }
}
