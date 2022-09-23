// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.sql.SQLException;

public class BrokeredConnection42 extends BrokeredConnection40
{
    public BrokeredConnection42(final BrokeredConnectionControl brokeredConnectionControl) throws SQLException {
        super(brokeredConnectionControl);
    }
    
    @Override
    public final BrokeredPreparedStatement newBrokeredStatement(final BrokeredStatementControl brokeredStatementControl, final String s, final Object o) throws SQLException {
        try {
            return new BrokeredPreparedStatement42(brokeredStatementControl, s, o);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    @Override
    public BrokeredCallableStatement newBrokeredStatement(final BrokeredStatementControl brokeredStatementControl, final String s) throws SQLException {
        try {
            return new BrokeredCallableStatement42(brokeredStatementControl, s);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
}
