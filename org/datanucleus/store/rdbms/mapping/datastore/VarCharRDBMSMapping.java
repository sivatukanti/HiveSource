// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.store.rdbms.mapping.java.SingleFieldMapping;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class VarCharRDBMSMapping extends CharRDBMSMapping
{
    public VarCharRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
        super(mapping, storeMgr, col);
    }
    
    @Override
    protected void initialize() {
        if (this.column != null) {
            if (this.getJavaTypeMapping() instanceof SingleFieldMapping && this.column.getColumnMetaData().getLength() == null) {
                final SingleFieldMapping m = (SingleFieldMapping)this.getJavaTypeMapping();
                if (m.getDefaultLength(0) > 0) {
                    this.column.getColumnMetaData().setLength(m.getDefaultLength(0));
                }
            }
            this.column.checkString();
            if (this.getJavaTypeMapping() instanceof SingleFieldMapping) {
                final Object[] validValues = ((SingleFieldMapping)this.getJavaTypeMapping()).getValidValues(0);
                if (validValues != null) {
                    final String constraints = this.storeMgr.getDatastoreAdapter().getCheckConstraintForValues(this.column.getIdentifier(), validValues, this.column.isNullable());
                    this.column.setConstraints(constraints);
                }
            }
        }
        this.initTypeInfo();
    }
    
    @Override
    public SQLTypeInfo getTypeInfo() {
        if (this.column != null && this.column.getColumnMetaData().getSqlType() != null) {
            return this.storeMgr.getSQLTypeInfoForJDBCType(12, this.column.getColumnMetaData().getSqlType());
        }
        return this.storeMgr.getSQLTypeInfoForJDBCType(12);
    }
}
