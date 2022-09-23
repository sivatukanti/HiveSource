// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.jdbc;

import java.sql.SQLException;

public class JdbcUriParseException extends SQLException
{
    private static final long serialVersionUID = 0L;
    
    public JdbcUriParseException(final Throwable cause) {
        super(cause);
    }
    
    public JdbcUriParseException(final String msg) {
        super(msg);
    }
    
    public JdbcUriParseException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
