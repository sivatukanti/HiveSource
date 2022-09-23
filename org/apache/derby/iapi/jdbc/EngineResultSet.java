// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.sql.SQLException;
import java.sql.ResultSet;

public interface EngineResultSet extends ResultSet
{
    boolean isForUpdate();
    
    boolean isNull(final int p0) throws SQLException;
    
    int getLength(final int p0) throws SQLException;
    
    int getHoldability() throws SQLException;
}
