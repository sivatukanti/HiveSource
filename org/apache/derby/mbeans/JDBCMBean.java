// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.mbeans;

import java.sql.SQLException;

public interface JDBCMBean
{
    String getDriverLevel();
    
    int getMajorVersion();
    
    int getMinorVersion();
    
    boolean isCompliantDriver();
    
    boolean acceptsURL(final String p0) throws SQLException;
}
