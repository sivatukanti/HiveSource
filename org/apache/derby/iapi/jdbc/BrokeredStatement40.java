// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.sql.SQLException;

public class BrokeredStatement40 extends BrokeredStatement
{
    BrokeredStatement40(final BrokeredStatementControl brokeredStatementControl) throws SQLException {
        super(brokeredStatementControl);
    }
    
    @Override
    public boolean isPoolable() throws SQLException {
        return this.getStatement().isPoolable();
    }
    
    @Override
    public void setPoolable(final boolean poolable) throws SQLException {
        this.getStatement().setPoolable(poolable);
    }
}
