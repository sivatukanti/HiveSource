// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import org.apache.derby.impl.jdbc.SQLExceptionFactory;
import org.apache.derby.impl.jdbc.SQLExceptionFactory40;
import org.apache.derby.impl.jdbc.Util;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class AutoloadedDriver40 extends AutoloadedDriver
{
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw (SQLFeatureNotSupportedException)Util.notImplemented("getParentLogger()");
    }
    
    static {
        AutoloadedDriver.registerMe(new AutoloadedDriver40());
        Util.setExceptionFactory(new SQLExceptionFactory40());
    }
}
