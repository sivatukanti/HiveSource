// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import org.apache.derby.iapi.sql.ResultSet;
import java.sql.SQLException;
import org.apache.derby.iapi.sql.conn.StatementContext;
import java.sql.Connection;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextManager;
import java.lang.ref.SoftReference;
import org.apache.derby.iapi.jdbc.ConnectionContext;
import org.apache.derby.iapi.services.context.ContextImpl;

class EmbedConnectionContext extends ContextImpl implements ConnectionContext
{
    private SoftReference connRef;
    
    EmbedConnectionContext(final ContextManager contextManager, final EmbedConnection referent) {
        super(contextManager, "JDBC_ConnectionContext");
        this.connRef = new SoftReference((T)referent);
    }
    
    public void cleanupOnError(final Throwable t) {
        if (this.connRef == null) {
            return;
        }
        final EmbedConnection embedConnection = this.connRef.get();
        if (t instanceof StandardException && ((StandardException)t).getSeverity() < 40000) {
            if (embedConnection != null) {
                embedConnection.needCommit = false;
            }
            return;
        }
        if (embedConnection != null) {
            embedConnection.setInactive();
        }
        this.connRef = null;
        this.popMe();
    }
    
    public Connection getNestedConnection(final boolean b) throws SQLException {
        final EmbedConnection embedConnection = this.connRef.get();
        if (embedConnection == null || embedConnection.isClosed()) {
            throw Util.noCurrentConnection();
        }
        if (!b) {
            final StatementContext statementContext = embedConnection.getLanguageConnection().getStatementContext();
            if (statementContext == null || statementContext.getSQLAllowed() < 0) {
                throw Util.noCurrentConnection();
            }
        }
        return embedConnection.getLocalDriver().getNewNestedConnection(embedConnection);
    }
    
    public java.sql.ResultSet getResultSet(final ResultSet set) throws SQLException {
        final EmbedConnection embedConnection = this.connRef.get();
        return embedConnection.getLocalDriver().newEmbedResultSet(embedConnection, set, false, null, true);
    }
    
    public boolean processInaccessibleDynamicResult(final java.sql.ResultSet set) {
        final EmbedConnection embedConnection = this.connRef.get();
        return embedConnection != null && EmbedStatement.processDynamicResult(embedConnection, set, null) != null;
    }
}
