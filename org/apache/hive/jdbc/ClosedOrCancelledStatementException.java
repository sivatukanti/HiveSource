// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.jdbc;

import java.sql.SQLException;

public class ClosedOrCancelledStatementException extends SQLException
{
    private static final long serialVersionUID = 0L;
    
    public ClosedOrCancelledStatementException(final String msg) {
        super(msg);
    }
}
