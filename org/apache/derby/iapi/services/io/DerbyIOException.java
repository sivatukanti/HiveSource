// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.IOException;

public final class DerbyIOException extends IOException
{
    private final String sqlState;
    
    public DerbyIOException(final String message, final String sqlState) {
        super(message);
        this.sqlState = sqlState;
    }
    
    public String getSQLState() {
        return this.sqlState;
    }
}
