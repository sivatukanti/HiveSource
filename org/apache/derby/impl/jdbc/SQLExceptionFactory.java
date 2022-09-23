// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.sql.SQLException;
import org.apache.derby.iapi.jdbc.ExceptionFactory;

public class SQLExceptionFactory implements ExceptionFactory
{
    public SQLException getSQLException(final String s, final String s2, final SQLException ex, final int n, final Throwable t, final Object[] array) {
        return new EmbedSQLException(s, s2, ex, n, t, array);
    }
    
    public final SQLException getSQLException(final String s, final SQLException ex, final Throwable t, final Object[] array) {
        return this.getSQLException(MessageService.getCompleteMessage(s, array), s, ex, StandardException.getSeverityFromIdentifier(s), t, array);
    }
    
    public SQLException getArgumentFerry(final SQLException ex) {
        return StandardException.getArgumentFerry(ex);
    }
}
