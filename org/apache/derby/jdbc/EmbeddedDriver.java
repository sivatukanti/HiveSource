// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import org.apache.derby.iapi.jdbc.JDBCBoot;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.Connection;
import java.util.Properties;
import java.sql.SQLException;
import java.sql.Driver;

public class EmbeddedDriver implements Driver
{
    public EmbeddedDriver() {
        boot();
    }
    
    public boolean acceptsURL(final String s) throws SQLException {
        return this.getDriverModule().acceptsURL(s);
    }
    
    public Connection connect(final String s, final Properties properties) throws SQLException {
        return this.getDriverModule().connect(s, properties);
    }
    
    public DriverPropertyInfo[] getPropertyInfo(final String s, final Properties properties) throws SQLException {
        return this.getDriverModule().getPropertyInfo(s, properties);
    }
    
    public int getMajorVersion() {
        try {
            return this.getDriverModule().getMajorVersion();
        }
        catch (SQLException ex) {
            return 0;
        }
    }
    
    public int getMinorVersion() {
        try {
            return this.getDriverModule().getMinorVersion();
        }
        catch (SQLException ex) {
            return 0;
        }
    }
    
    public boolean jdbcCompliant() {
        try {
            return this.getDriverModule().jdbcCompliant();
        }
        catch (SQLException ex) {
            return false;
        }
    }
    
    private Driver getDriverModule() throws SQLException {
        return AutoloadedDriver.getDriverModule();
    }
    
    static void boot() {
        PrintWriter logWriter = DriverManager.getLogWriter();
        if (logWriter == null) {
            logWriter = new PrintWriter(System.err, true);
        }
        new JDBCBoot().boot("jdbc:derby:", logWriter);
    }
    
    static {
        boot();
    }
}
