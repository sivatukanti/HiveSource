// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.schema;

import java.sql.ResultSet;

public class DerbyTypeInfo extends SQLTypeInfo
{
    public DerbyTypeInfo(final ResultSet rs) {
        super(rs);
        if (this.typeName.equalsIgnoreCase("DOUBLE")) {
            this.allowsPrecisionSpec = false;
        }
    }
}
