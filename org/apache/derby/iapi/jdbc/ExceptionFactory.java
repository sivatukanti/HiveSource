// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.sql.SQLException;

public interface ExceptionFactory
{
    SQLException getArgumentFerry(final SQLException p0);
    
    SQLException getSQLException(final String p0, final String p1, final SQLException p2, final int p3, final Throwable p4, final Object[] p5);
    
    SQLException getSQLException(final String p0, final SQLException p1, final Throwable p2, final Object[] p3);
}
