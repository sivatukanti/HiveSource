// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.sql.SQLType;
import java.sql.SQLException;

public class BrokeredPreparedStatement42 extends BrokeredPreparedStatement40
{
    public BrokeredPreparedStatement42(final BrokeredStatementControl brokeredStatementControl, final String s, final Object o) throws SQLException {
        super(brokeredStatementControl, s, o);
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x, final SQLType targetSqlType) throws SQLException {
        this.getPreparedStatement().setObject(parameterIndex, x, targetSqlType);
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x, final SQLType targetSqlType, final int scaleOrLength) throws SQLException {
        this.getPreparedStatement().setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    }
}
