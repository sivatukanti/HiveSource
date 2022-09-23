// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.io.Reader;
import java.sql.SQLException;
import java.io.InputStream;
import java.sql.PreparedStatement;

public interface EnginePreparedStatement extends PreparedStatement, EngineStatement
{
    void setBinaryStream(final int p0, final InputStream p1) throws SQLException;
    
    void setCharacterStream(final int p0, final Reader p1) throws SQLException;
    
    long getVersionCounter() throws SQLException;
    
    long executeLargeUpdate() throws SQLException;
}
