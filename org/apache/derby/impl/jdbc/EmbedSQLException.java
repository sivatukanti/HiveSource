// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.error.DerbySQLException;
import java.sql.SQLException;

public class EmbedSQLException extends SQLException implements DerbySQLException
{
    private transient Object[] arguments;
    private String messageId;
    private transient boolean simpleWrapper;
    
    EmbedSQLException(final String reason, final String messageId, final SQLException nextException, final int vendorCode, Throwable cause, final Object[] arguments) {
        super(reason, StandardException.getSQLStateFromIdentifier(messageId), vendorCode);
        this.messageId = messageId;
        this.arguments = arguments;
        if (nextException != null) {
            this.setNextException(nextException);
        }
        if (cause == null) {
            cause = nextException;
        }
        if (cause != null) {
            this.initCause(cause);
        }
    }
    
    public String getMessageId() {
        return this.messageId;
    }
    
    public Object[] getArguments() {
        return this.arguments;
    }
    
    public String toString() {
        return "java.sql.SQLException: " + this.getMessage();
    }
    
    public static SQLException wrapStandardException(final String s, final String s2, final int n, final Throwable t) {
        final EmbedSQLException ex = new EmbedSQLException(s, s2, null, n, t, (t instanceof StandardException) ? ((StandardException)t).getArguments() : null);
        ex.simpleWrapper = true;
        return ex;
    }
    
    public boolean isSimpleWrapper() {
        return this.getNextException() == null && this.simpleWrapper;
    }
}
