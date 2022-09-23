// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.dbcp;

import java.sql.SQLException;

public class SQLNestedException extends SQLException
{
    private static final long serialVersionUID = 1046151479543081202L;
    
    public SQLNestedException(final String msg, final Throwable cause) {
        super(msg);
        if (cause != null) {
            this.initCause(cause);
        }
    }
}
