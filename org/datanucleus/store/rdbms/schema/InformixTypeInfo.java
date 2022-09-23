// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.schema;

import java.sql.ResultSet;

public class InformixTypeInfo extends SQLTypeInfo
{
    public InformixTypeInfo(final ResultSet rs) {
        super(rs);
        if (this.dataType == 12) {
            this.precision = 255;
            this.typeName = "VARCHAR";
        }
    }
}
