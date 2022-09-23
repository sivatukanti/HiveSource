// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.sql.SQLType;
import java.sql.SQLException;

public class BrokeredCallableStatement42 extends BrokeredCallableStatement40
{
    public BrokeredCallableStatement42(final BrokeredStatementControl brokeredStatementControl, final String s) throws SQLException {
        super(brokeredStatementControl, s);
    }
    
    @Override
    public void registerOutParameter(final int parameterIndex, final SQLType sqlType) throws SQLException {
        this.getCallableStatement().registerOutParameter(parameterIndex, sqlType);
    }
    
    @Override
    public void registerOutParameter(final int parameterIndex, final SQLType sqlType, final int scale) throws SQLException {
        this.getCallableStatement().registerOutParameter(parameterIndex, sqlType, scale);
    }
    
    @Override
    public void registerOutParameter(final int parameterIndex, final SQLType sqlType, final String typeName) throws SQLException {
        this.getCallableStatement().registerOutParameter(parameterIndex, sqlType, typeName);
    }
    
    @Override
    public void registerOutParameter(final String parameterName, final SQLType sqlType) throws SQLException {
        this.getCallableStatement().registerOutParameter(parameterName, sqlType);
    }
    
    @Override
    public void registerOutParameter(final String parameterName, final SQLType sqlType, final int scale) throws SQLException {
        this.getCallableStatement().registerOutParameter(parameterName, sqlType, scale);
    }
    
    @Override
    public void registerOutParameter(final String parameterName, final SQLType sqlType, final String typeName) throws SQLException {
        this.getCallableStatement().registerOutParameter(parameterName, sqlType, typeName);
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x, final SQLType targetSqlType) throws SQLException {
        this.getCallableStatement().setObject(parameterIndex, x, targetSqlType);
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x, final SQLType targetSqlType, final int scaleOrLength) throws SQLException {
        this.getCallableStatement().setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    }
    
    @Override
    public void setObject(final String parameterName, final Object x, final SQLType targetSqlType) throws SQLException {
        this.getCallableStatement().setObject(parameterName, x, targetSqlType);
    }
    
    @Override
    public void setObject(final String parameterName, final Object x, final SQLType targetSqlType, final int scaleOrLength) throws SQLException {
        this.getCallableStatement().setObject(parameterName, x, targetSqlType, scaleOrLength);
    }
}
