// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.schema;

import java.sql.ResultSet;

public class H2TypeInfo extends SQLTypeInfo
{
    public H2TypeInfo(final ResultSet rs) {
        super(rs);
    }
    
    @Override
    public boolean isCompatibleWith(final RDBMSColumnInfo colInfo) {
        if (super.isCompatibleWith(colInfo)) {
            return true;
        }
        final short colDataType = colInfo.getDataType();
        return (this.dataType == 1 && colDataType == 12) || (this.dataType == 12 && colDataType == 1);
    }
}
