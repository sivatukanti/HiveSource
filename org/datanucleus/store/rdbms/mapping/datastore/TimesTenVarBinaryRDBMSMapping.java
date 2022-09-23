// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.datastore;

import org.datanucleus.store.rdbms.mapping.java.SingleFieldMapping;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public class TimesTenVarBinaryRDBMSMapping extends VarBinaryRDBMSMapping
{
    public TimesTenVarBinaryRDBMSMapping(final JavaTypeMapping mapping, final RDBMSStoreManager storeMgr, final Column col) {
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
            if (this.column.getColumnMetaData().getLength() == null) {
                this.column.getColumnMetaData().setLength(1024);
            }
        }
        super.initialize();
    }
}
