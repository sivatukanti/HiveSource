// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import javax.sql.XAConnection;
import org.apache.derby.iapi.jdbc.ResourceAdapter;
import javax.sql.PooledConnection;
import org.apache.derby.iapi.jdbc.BrokeredConnection;
import org.apache.derby.iapi.jdbc.BrokeredConnectionControl;
import org.apache.derby.impl.jdbc.EmbedCallableStatement30;
import java.sql.CallableStatement;
import org.apache.derby.impl.jdbc.EmbedPreparedStatement30;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.sql.Connection;
import org.apache.derby.impl.jdbc.EmbedConnection;

public class Driver30 extends Driver20
{
    @Override
    public Connection getNewNestedConnection(final EmbedConnection embedConnection) {
        return new EmbedConnection(embedConnection);
    }
    
    @Override
    protected EmbedConnection getNewEmbedConnection(final String s, final Properties properties) throws SQLException {
        return new EmbedConnection(this, s, properties);
    }
    
    @Override
    public PreparedStatement newEmbedPreparedStatement(final EmbedConnection embedConnection, final String s, final boolean b, final int n, final int n2, final int n3, final int n4, final int[] array, final String[] array2) throws SQLException {
        return new EmbedPreparedStatement30(embedConnection, s, b, n, n2, n3, n4, array, array2);
    }
    
    @Override
    public CallableStatement newEmbedCallableStatement(final EmbedConnection embedConnection, final String s, final int n, final int n2, final int n3) throws SQLException {
        return new EmbedCallableStatement30(embedConnection, s, n, n2, n3);
    }
    
    @Override
    public BrokeredConnection newBrokeredConnection(final BrokeredConnectionControl brokeredConnectionControl) throws SQLException {
        return new BrokeredConnection(brokeredConnectionControl);
    }
    
    protected PooledConnection getNewPooledConnection(final EmbeddedBaseDataSource embeddedBaseDataSource, final String s, final String s2, final boolean b) throws SQLException {
        return new EmbedPooledConnection(embeddedBaseDataSource, s, s2, b);
    }
    
    protected XAConnection getNewXAConnection(final EmbeddedBaseDataSource embeddedBaseDataSource, final ResourceAdapter resourceAdapter, final String s, final String s2, final boolean b) throws SQLException {
        return new EmbedXAConnection(embeddedBaseDataSource, resourceAdapter, s, s2, b);
    }
}
