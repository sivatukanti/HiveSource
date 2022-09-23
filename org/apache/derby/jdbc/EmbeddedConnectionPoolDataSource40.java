// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import org.apache.derby.impl.jdbc.Util;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.ConnectionPoolDataSource;

public class EmbeddedConnectionPoolDataSource40 extends EmbeddedConnectionPoolDataSource implements ConnectionPoolDataSource
{
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw (SQLFeatureNotSupportedException)Util.notImplemented("getParentLogger()");
    }
}
