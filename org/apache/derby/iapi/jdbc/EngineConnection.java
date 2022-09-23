// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.sql.SQLWarning;
import java.sql.SQLException;
import java.sql.Connection;

public interface EngineConnection extends Connection
{
    void setDrdaID(final String p0);
    
    boolean isInGlobalTransaction();
    
    void setPrepareIsolation(final int p0) throws SQLException;
    
    int getPrepareIsolation() throws SQLException;
    
    int getHoldability() throws SQLException;
    
    void addWarning(final SQLWarning p0) throws SQLException;
    
    void clearLOBMapping() throws SQLException;
    
    Object getLOBMapping(final int p0) throws SQLException;
    
    String getCurrentSchemaName() throws SQLException;
    
    void resetFromPool() throws SQLException;
    
    ExceptionFactory getExceptionFactory();
    
    String getSchema() throws SQLException;
    
    void setSchema(final String p0) throws SQLException;
}
