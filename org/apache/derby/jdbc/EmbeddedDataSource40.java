// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import org.apache.derby.impl.jdbc.Util;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class EmbeddedDataSource40 extends EmbeddedDataSource implements DataSource
{
    private static final long serialVersionUID = 4472591890758954803L;
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw (SQLFeatureNotSupportedException)Util.notImplemented("getParentLogger()");
    }
}
