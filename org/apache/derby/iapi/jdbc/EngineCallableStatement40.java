// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.sql.SQLException;
import java.sql.CallableStatement;

public interface EngineCallableStatement40 extends EngineStatement, CallableStatement
{
     <T> T getObject(final int p0, final Class<T> p1) throws SQLException;
    
     <T> T getObject(final String p0, final Class<T> p1) throws SQLException;
}
