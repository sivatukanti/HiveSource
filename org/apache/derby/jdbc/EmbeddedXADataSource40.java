// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import org.apache.derby.impl.jdbc.Util;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.XADataSource;

public class EmbeddedXADataSource40 extends EmbeddedXADataSource implements XADataSource
{
    private static final long serialVersionUID = 4048303427908481258L;
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw (SQLFeatureNotSupportedException)Util.notImplemented("getParentLogger()");
    }
}
