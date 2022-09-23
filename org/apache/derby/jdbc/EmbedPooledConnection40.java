// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import java.util.Iterator;
import javax.sql.PooledConnection;
import javax.sql.StatementEvent;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.StatementEventListener;
import java.util.concurrent.CopyOnWriteArrayList;

class EmbedPooledConnection40 extends EmbedPooledConnection
{
    private final CopyOnWriteArrayList<StatementEventListener> statementEventListeners;
    
    EmbedPooledConnection40(final EmbeddedBaseDataSource embeddedBaseDataSource, final String s, final String s2, final boolean b) throws SQLException {
        super(embeddedBaseDataSource, s, s2, b);
        this.statementEventListeners = new CopyOnWriteArrayList<StatementEventListener>();
    }
    
    @Override
    public void removeStatementEventListener(final StatementEventListener o) {
        if (o == null) {
            return;
        }
        this.statementEventListeners.remove(o);
    }
    
    @Override
    public void addStatementEventListener(final StatementEventListener e) {
        if (!this.isActive) {
            return;
        }
        if (e == null) {
            return;
        }
        this.statementEventListeners.add(e);
    }
    
    @Override
    public void onStatementClose(final PreparedStatement statement) {
        if (!this.statementEventListeners.isEmpty()) {
            final StatementEvent statementEvent = new StatementEvent(this, statement);
            final Iterator<StatementEventListener> iterator = this.statementEventListeners.iterator();
            while (iterator.hasNext()) {
                iterator.next().statementClosed(statementEvent);
            }
        }
    }
    
    @Override
    public void onStatementErrorOccurred(final PreparedStatement statement, final SQLException exception) {
        if (!this.statementEventListeners.isEmpty()) {
            final StatementEvent statementEvent = new StatementEvent(this, statement, exception);
            final Iterator<StatementEventListener> iterator = this.statementEventListeners.iterator();
            while (iterator.hasNext()) {
                iterator.next().statementErrorOccurred(statementEvent);
            }
        }
    }
}
