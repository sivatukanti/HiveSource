// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import java.security.AccessControlException;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import org.apache.derby.impl.jdbc.Util;
import java.sql.DriverPropertyInfo;
import java.sql.Connection;
import java.util.Properties;
import java.sql.SQLException;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.sql.DriverManager;
import java.sql.Driver;

public class AutoloadedDriver implements Driver
{
    private static boolean _engineForcedDown;
    private static AutoloadedDriver _autoloadedDriver;
    private static Driver _driverModule;
    
    protected static void registerMe(final AutoloadedDriver autoloadedDriver) {
        try {
            DriverManager.registerDriver(AutoloadedDriver._autoloadedDriver = autoloadedDriver);
        }
        catch (SQLException ex) {
            throw new IllegalStateException(MessageService.getTextMessage("I026", ex.getMessage()));
        }
    }
    
    public boolean acceptsURL(final String s) throws SQLException {
        return !AutoloadedDriver._engineForcedDown && InternalDriver.embeddedDriverAcceptsURL(s);
    }
    
    public Connection connect(final String s, final Properties properties) throws SQLException {
        if (!InternalDriver.embeddedDriverAcceptsURL(s)) {
            return null;
        }
        return getDriverModule().connect(s, properties);
    }
    
    public DriverPropertyInfo[] getPropertyInfo(final String s, final Properties properties) throws SQLException {
        return getDriverModule().getPropertyInfo(s, properties);
    }
    
    public int getMajorVersion() {
        try {
            return getDriverModule().getMajorVersion();
        }
        catch (SQLException ex) {
            return 0;
        }
    }
    
    public int getMinorVersion() {
        try {
            return getDriverModule().getMinorVersion();
        }
        catch (SQLException ex) {
            return 0;
        }
    }
    
    public boolean jdbcCompliant() {
        try {
            return getDriverModule().jdbcCompliant();
        }
        catch (SQLException ex) {
            return false;
        }
    }
    
    static Driver getDriverModule() throws SQLException {
        if (AutoloadedDriver._engineForcedDown && AutoloadedDriver._autoloadedDriver == null) {
            throw Util.generateCsSQLException("08006.C.8");
        }
        if (!isBooted()) {
            EmbeddedDriver.boot();
        }
        return AutoloadedDriver._driverModule;
    }
    
    static void registerDriverModule(final Driver driverModule) {
        AutoloadedDriver._driverModule = driverModule;
        AutoloadedDriver._engineForcedDown = false;
        try {
            if (AutoloadedDriver._autoloadedDriver == null) {
                DriverManager.registerDriver(AutoloadedDriver._autoloadedDriver = makeAutoloadedDriver());
            }
        }
        catch (SQLException ex) {}
    }
    
    static void unregisterDriverModule() {
        AutoloadedDriver._engineForcedDown = true;
        try {
            if (InternalDriver.getDeregister() && AutoloadedDriver._autoloadedDriver != null) {
                deregisterDriver(AutoloadedDriver._autoloadedDriver);
                AutoloadedDriver._autoloadedDriver = null;
            }
            InternalDriver.setDeregister(true);
            AutoloadedDriver._driverModule = null;
        }
        catch (SQLException ex) {}
    }
    
    private static void deregisterDriver(final AutoloadedDriver autoloadedDriver) throws SQLException {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                public Void run() throws SQLException {
                    DriverManager.deregisterDriver(autoloadedDriver);
                    return null;
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (SQLException)ex.getCause();
        }
        catch (AccessControlException ex2) {
            Monitor.logTextMessage("J137");
            Monitor.logThrowable(ex2);
        }
    }
    
    private static boolean isBooted() {
        return AutoloadedDriver._driverModule != null;
    }
    
    private static AutoloadedDriver makeAutoloadedDriver() {
        try {
            return (AutoloadedDriver)Class.forName("org.apache.derby.jdbc.AutoloadedDriver40").newInstance();
        }
        catch (Throwable t) {
            return new AutoloadedDriver();
        }
    }
    
    static {
        AutoloadedDriver._engineForcedDown = false;
        try {
            Class.forName("org.apache.derby.jdbc.AutoloadedDriver40");
        }
        catch (Throwable t) {
            registerMe(new AutoloadedDriver());
        }
    }
}
