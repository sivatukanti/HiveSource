// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import java.security.Permission;
import org.apache.derby.impl.jdbc.EmbedResultSet;
import org.apache.derby.iapi.sql.ResultSet;
import org.apache.derby.impl.jdbc.EmbedCallableStatement;
import java.sql.CallableStatement;
import org.apache.derby.impl.jdbc.EmbedPreparedStatement;
import java.sql.PreparedStatement;
import org.apache.derby.impl.jdbc.EmbedStatement;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.derby.impl.jdbc.EmbedConnection;
import java.util.Properties;

public class Driver169 extends InternalDriver
{
    protected EmbedConnection getNewEmbedConnection(final String s, final Properties properties) throws SQLException {
        return new EmbedConnection(this, s, properties);
    }
    
    protected EmbedConnection timeLogin(final String s, final Properties properties, final int n) throws SQLException {
        return this.getNewEmbedConnection(s, properties);
    }
    
    public Connection getNewNestedConnection(final EmbedConnection embedConnection) {
        return new EmbedConnection(embedConnection);
    }
    
    public Statement newEmbedStatement(final EmbedConnection embedConnection, final boolean b, final int n, final int n2, final int n3) {
        return new EmbedStatement(embedConnection, b, n, n2, n3);
    }
    
    public PreparedStatement newEmbedPreparedStatement(final EmbedConnection embedConnection, final String s, final boolean b, final int n, final int n2, final int n3, final int n4, final int[] array, final String[] array2) throws SQLException {
        return new EmbedPreparedStatement(embedConnection, s, b, n, n2, n3, n4, array, array2);
    }
    
    public CallableStatement newEmbedCallableStatement(final EmbedConnection embedConnection, final String s, final int n, final int n2, final int n3) throws SQLException {
        return new EmbedCallableStatement(embedConnection, s, n, n2, n3);
    }
    
    public EmbedResultSet newEmbedResultSet(final EmbedConnection embedConnection, final ResultSet set, final boolean b, final EmbedStatement embedStatement, final boolean b2) throws SQLException {
        return new EmbedResultSet(embedConnection, set, b, embedStatement, b2);
    }
    
    public void checkSystemPrivileges(final String s, final Permission permission) throws Exception {
    }
}
