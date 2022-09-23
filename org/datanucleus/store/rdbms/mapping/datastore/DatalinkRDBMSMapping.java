// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class DatalinkRDBMSMapping extends CharRDBMSMapping
{
    public DatalinkRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
        super(mapping, storeMgr, col);
    }
    
    @Override
    protected void initialize() {
        if (this.column != null && this.mapping.getMemberMetaData().getValueForExtension("select-function") == null) {
            this.column.setWrapperFunction("DLURLCOMPLETEONLY(?)", 0);
        }
        this.initTypeInfo();
    }
    
    @Override
    public SQLTypeInfo getTypeInfo() {
        if (this.column != null && this.column.getColumnMetaData().getSqlType() != null) {
            return this.storeMgr.getSQLTypeInfoForJDBCType(70, this.column.getColumnMetaData().getSqlType());
        }
        return this.storeMgr.getSQLTypeInfoForJDBCType(70);
    }
    
    @Override
    public String getInsertionInputParameter() {
        return "DLVALUE(? || '')";
    }
    
    @Override
    public boolean includeInFetchStatement() {
        return true;
    }
    
    @Override
    public String getUpdateInputParameter() {
        return "DLVALUE(? || '')";
    }
}
