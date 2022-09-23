// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import java.sql.SQLException;
import java.util.concurrent.Executor;

public interface EngineConnection40 extends EngineConnection
{
    void abort(final Executor p0) throws SQLException;
    
    void setNetworkTimeout(final Executor p0, final int p1) throws SQLException;
    
    int getNetworkTimeout() throws SQLException;
}
