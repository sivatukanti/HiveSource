// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import org.apache.derby.iapi.util.InterruptStatus;
import java.util.GregorianCalendar;
import java.sql.SQLException;
import java.util.Calendar;
import org.apache.derby.jdbc.InternalDriver;

abstract class ConnectionChild
{
    EmbedConnection localConn;
    final InternalDriver factory;
    private Calendar cal;
    
    ConnectionChild(final EmbedConnection localConn) {
        this.localConn = localConn;
        this.factory = localConn.getLocalDriver();
    }
    
    final EmbedConnection getEmbedConnection() {
        return this.localConn;
    }
    
    final Object getConnectionSynchronization() {
        return this.localConn.getConnectionSynchronization();
    }
    
    final SQLException handleException(final Throwable t) throws SQLException {
        return this.localConn.handleException(t);
    }
    
    final void needCommit() {
        this.localConn.needCommit();
    }
    
    final void commitIfNeeded() throws SQLException {
        this.localConn.commitIfNeeded();
    }
    
    final void commitIfAutoCommit() throws SQLException {
        this.localConn.commitIfAutoCommit();
    }
    
    final void setupContextStack() throws SQLException {
        this.localConn.setupContextStack();
    }
    
    final void restoreContextStack() throws SQLException {
        this.localConn.restoreContextStack();
    }
    
    Calendar getCal() {
        if (this.cal == null) {
            this.cal = new GregorianCalendar();
        }
        return this.cal;
    }
    
    SQLException newSQLException(final String s) {
        final EmbedConnection localConn = this.localConn;
        return EmbedConnection.newSQLException(s);
    }
    
    SQLException newSQLException(final String s, final Object o) {
        final EmbedConnection localConn = this.localConn;
        return EmbedConnection.newSQLException(s, o);
    }
    
    SQLException newSQLException(final String s, final Object o, final Object o2) {
        final EmbedConnection localConn = this.localConn;
        return EmbedConnection.newSQLException(s, o, o2);
    }
    
    protected static void restoreIntrFlagIfSeen(final boolean b, final EmbedConnection embedConnection) {
        if (b) {
            InterruptStatus.restoreIntrFlagIfSeen(embedConnection.getLanguageConnection());
        }
        else {
            InterruptStatus.restoreIntrFlagIfSeen();
        }
    }
}
