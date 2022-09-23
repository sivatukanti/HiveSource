// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.util.concurrent.Executor;
import java.util.Properties;
import java.sql.SQLClientInfoException;
import java.sql.Struct;
import java.sql.SQLXML;
import java.sql.NClob;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Array;
import java.sql.SQLException;

public class BrokeredConnection40 extends BrokeredConnection implements EngineConnection40
{
    public BrokeredConnection40(final BrokeredConnectionControl brokeredConnectionControl) throws SQLException {
        super(brokeredConnectionControl);
    }
    
    @Override
    public Array createArrayOf(final String s, final Object[] array) throws SQLException {
        try {
            return this.getRealConnection().createArrayOf(s, array);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    @Override
    public Blob createBlob() throws SQLException {
        try {
            return this.getRealConnection().createBlob();
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    @Override
    public Clob createClob() throws SQLException {
        try {
            return this.getRealConnection().createClob();
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    @Override
    public NClob createNClob() throws SQLException {
        try {
            return this.getRealConnection().createNClob();
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    @Override
    public SQLXML createSQLXML() throws SQLException {
        try {
            return this.getRealConnection().createSQLXML();
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    @Override
    public Struct createStruct(final String s, final Object[] array) throws SQLException {
        try {
            return this.getRealConnection().createStruct(s, array);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    @Override
    public final boolean isValid(final int n) throws SQLException {
        if (this.isClosed()) {
            return false;
        }
        try {
            return this.getRealConnection().isValid(n);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    @Override
    public void setClientInfo(final String s, final String s2) throws SQLClientInfoException {
        try {
            this.getRealConnection().setClientInfo(s, s2);
        }
        catch (SQLClientInfoException ex) {
            this.notifyException(ex);
            throw ex;
        }
        catch (SQLException ex2) {
            this.notifyException(ex2);
            throw new SQLClientInfoException(ex2.getMessage(), ex2.getSQLState(), ex2.getErrorCode(), new FailedProperties40(FailedProperties40.makeProperties(s, s2)).getProperties());
        }
    }
    
    @Override
    public void setClientInfo(final Properties clientInfo) throws SQLClientInfoException {
        try {
            this.getRealConnection().setClientInfo(clientInfo);
        }
        catch (SQLClientInfoException ex) {
            this.notifyException(ex);
            throw ex;
        }
        catch (SQLException ex2) {
            this.notifyException(ex2);
            throw new SQLClientInfoException(ex2.getMessage(), ex2.getSQLState(), ex2.getErrorCode(), new FailedProperties40(clientInfo).getProperties());
        }
    }
    
    @Override
    public String getClientInfo(final String s) throws SQLException {
        try {
            return this.getRealConnection().getClientInfo(s);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    @Override
    public Properties getClientInfo() throws SQLException {
        try {
            return this.getRealConnection().getClientInfo();
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    @Override
    public final BrokeredStatement newBrokeredStatement(final BrokeredStatementControl brokeredStatementControl) throws SQLException {
        try {
            return new BrokeredStatement40(brokeredStatementControl);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    @Override
    public BrokeredPreparedStatement newBrokeredStatement(final BrokeredStatementControl brokeredStatementControl, final String s, final Object o) throws SQLException {
        try {
            return new BrokeredPreparedStatement40(brokeredStatementControl, s, o);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    @Override
    public BrokeredCallableStatement newBrokeredStatement(final BrokeredStatementControl brokeredStatementControl, final String s) throws SQLException {
        try {
            return new BrokeredCallableStatement40(brokeredStatementControl, s);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    @Override
    public final boolean isWrapperFor(final Class<?> clazz) throws SQLException {
        try {
            if (this.getRealConnection().isClosed()) {
                throw this.noCurrentConnection();
            }
            return clazz.isInstance(this);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    @Override
    public final <T> T unwrap(final Class<T> clazz) throws SQLException {
        try {
            if (this.getRealConnection().isClosed()) {
                throw this.noCurrentConnection();
            }
            try {
                return clazz.cast(this);
            }
            catch (ClassCastException ex2) {
                throw this.getExceptionFactory().getSQLException("XJ128.S", null, null, new Object[] { clazz });
            }
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    @Override
    public void abort(final Executor executor) throws SQLException {
        if (!this.isClosed) {
            ((EngineConnection40)this.getRealConnection()).abort(executor);
        }
    }
    
    @Override
    public int getNetworkTimeout() throws SQLException {
        try {
            return ((EngineConnection40)this.getRealConnection()).getNetworkTimeout();
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
    
    @Override
    public void setNetworkTimeout(final Executor executor, final int n) throws SQLException {
        try {
            ((EngineConnection40)this.getRealConnection()).setNetworkTimeout(executor, n);
        }
        catch (SQLException ex) {
            this.notifyException(ex);
            throw ex;
        }
    }
}
