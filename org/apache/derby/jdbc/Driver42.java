// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import org.apache.derby.impl.jdbc.EmbedCallableStatement42;
import java.sql.CallableStatement;
import org.apache.derby.impl.jdbc.EmbedResultSet42;
import org.apache.derby.impl.jdbc.EmbedResultSet;
import org.apache.derby.impl.jdbc.EmbedStatement;
import org.apache.derby.iapi.sql.ResultSet;
import org.apache.derby.iapi.jdbc.BrokeredConnection42;
import org.apache.derby.iapi.jdbc.BrokeredConnection;
import org.apache.derby.iapi.jdbc.BrokeredConnectionControl;
import java.sql.SQLException;
import org.apache.derby.impl.jdbc.EmbedPreparedStatement42;
import java.sql.PreparedStatement;
import org.apache.derby.impl.jdbc.EmbedConnection;

public class Driver42 extends Driver40
{
    @Override
    public PreparedStatement newEmbedPreparedStatement(final EmbedConnection embedConnection, final String s, final boolean b, final int n, final int n2, final int n3, final int n4, final int[] array, final String[] array2) throws SQLException {
        return new EmbedPreparedStatement42(embedConnection, s, b, n, n2, n3, n4, array, array2);
    }
    
    @Override
    public BrokeredConnection newBrokeredConnection(final BrokeredConnectionControl brokeredConnectionControl) throws SQLException {
        return new BrokeredConnection42(brokeredConnectionControl);
    }
    
    @Override
    public EmbedResultSet newEmbedResultSet(final EmbedConnection embedConnection, final ResultSet set, final boolean b, final EmbedStatement embedStatement, final boolean b2) throws SQLException {
        return new EmbedResultSet42(embedConnection, set, b, embedStatement, b2);
    }
    
    @Override
    public CallableStatement newEmbedCallableStatement(final EmbedConnection embedConnection, final String s, final int n, final int n2, final int n3) throws SQLException {
        return new EmbedCallableStatement42(embedConnection, s, n, n2, n3);
    }
}
