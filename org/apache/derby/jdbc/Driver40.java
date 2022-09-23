// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import org.apache.derby.impl.jdbc.EmbedResultSetMetaData;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.XAConnection;
import org.apache.derby.iapi.jdbc.ResourceAdapter;
import javax.sql.PooledConnection;
import org.apache.derby.impl.jdbc.EmbedResultSetMetaData40;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import org.apache.derby.impl.jdbc.EmbedDatabaseMetaData40;
import java.sql.DatabaseMetaData;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.impl.jdbc.SQLExceptionFactory;
import org.apache.derby.impl.jdbc.Util;
import org.apache.derby.impl.jdbc.SQLExceptionFactory40;
import org.apache.derby.impl.jdbc.EmbedResultSet40;
import org.apache.derby.impl.jdbc.EmbedResultSet;
import org.apache.derby.impl.jdbc.EmbedStatement;
import org.apache.derby.iapi.sql.ResultSet;
import org.apache.derby.iapi.jdbc.BrokeredConnection40;
import org.apache.derby.iapi.jdbc.BrokeredConnection;
import org.apache.derby.iapi.jdbc.BrokeredConnectionControl;
import org.apache.derby.impl.jdbc.EmbedCallableStatement40;
import java.sql.CallableStatement;
import org.apache.derby.impl.jdbc.EmbedPreparedStatement40;
import java.sql.PreparedStatement;
import org.apache.derby.impl.jdbc.EmbedStatement40;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.derby.impl.jdbc.EmbedConnection40;
import java.sql.Connection;
import org.apache.derby.impl.jdbc.EmbedConnection;

public class Driver40 extends Driver30
{
    @Override
    public Connection getNewNestedConnection(final EmbedConnection embedConnection) {
        return new EmbedConnection40(embedConnection);
    }
    
    @Override
    protected EmbedConnection getNewEmbedConnection(final String s, final Properties properties) throws SQLException {
        return new EmbedConnection40(this, s, properties);
    }
    
    @Override
    public Statement newEmbedStatement(final EmbedConnection embedConnection, final boolean b, final int n, final int n2, final int n3) {
        return new EmbedStatement40(embedConnection, b, n, n2, n3);
    }
    
    @Override
    public PreparedStatement newEmbedPreparedStatement(final EmbedConnection embedConnection, final String s, final boolean b, final int n, final int n2, final int n3, final int n4, final int[] array, final String[] array2) throws SQLException {
        return new EmbedPreparedStatement40(embedConnection, s, b, n, n2, n3, n4, array, array2);
    }
    
    @Override
    public CallableStatement newEmbedCallableStatement(final EmbedConnection embedConnection, final String s, final int n, final int n2, final int n3) throws SQLException {
        return new EmbedCallableStatement40(embedConnection, s, n, n2, n3);
    }
    
    @Override
    public BrokeredConnection newBrokeredConnection(final BrokeredConnectionControl brokeredConnectionControl) throws SQLException {
        return new BrokeredConnection40(brokeredConnectionControl);
    }
    
    @Override
    public EmbedResultSet newEmbedResultSet(final EmbedConnection embedConnection, final ResultSet set, final boolean b, final EmbedStatement embedStatement, final boolean b2) throws SQLException {
        return new EmbedResultSet40(embedConnection, set, b, embedStatement, b2);
    }
    
    @Override
    public void boot(final boolean b, final Properties properties) throws StandardException {
        Util.setExceptionFactory(new SQLExceptionFactory40());
        super.boot(b, properties);
    }
    
    @Override
    public DatabaseMetaData newEmbedDatabaseMetaData(final EmbedConnection embedConnection, final String s) throws SQLException {
        return new EmbedDatabaseMetaData40(embedConnection, s);
    }
    
    @Override
    public EmbedResultSetMetaData40 newEmbedResultSetMetaData(final ResultColumnDescriptor[] array) {
        return new EmbedResultSetMetaData40(array);
    }
    
    @Override
    protected PooledConnection getNewPooledConnection(final EmbeddedBaseDataSource embeddedBaseDataSource, final String s, final String s2, final boolean b) throws SQLException {
        return new EmbedPooledConnection40(embeddedBaseDataSource, s, s2, b);
    }
    
    @Override
    protected XAConnection getNewXAConnection(final EmbeddedBaseDataSource embeddedBaseDataSource, final ResourceAdapter resourceAdapter, final String s, final String s2, final boolean b) throws SQLException {
        return new EmbedXAConnection40(embeddedBaseDataSource, resourceAdapter, s, s2, b);
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw (SQLFeatureNotSupportedException)Util.notImplemented("getParentLogger()");
    }
}
