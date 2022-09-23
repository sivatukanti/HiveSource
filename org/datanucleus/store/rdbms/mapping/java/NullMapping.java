// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.rdbms.RDBMSStoreManager;

public class NullMapping extends SingleFieldMapping
{
    public NullMapping(final RDBMSStoreManager storeMgr) {
        this.initialize(storeMgr, null);
    }
    
    @Override
    public Class getJavaType() {
        return null;
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet resultSet, final int[] exprIndex) {
        return null;
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] exprIndex, final Object value) {
    }
}
