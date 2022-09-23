// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.sql.SQLException;

public interface EngineLOB
{
    int getLocator();
    
    void free() throws SQLException;
}
