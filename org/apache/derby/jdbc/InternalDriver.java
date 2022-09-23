// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.jdbc;

import org.apache.derby.mbeans.JDBCMBean;
import org.apache.derby.impl.jdbc.EmbedResultSetMetaData;
import org.apache.derby.iapi.sql.ResultColumnDescriptor;
import org.apache.derby.impl.jdbc.EmbedResultSet;
import org.apache.derby.iapi.sql.ResultSet;
import org.apache.derby.impl.jdbc.EmbedDatabaseMetaData;
import java.sql.DatabaseMetaData;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import org.apache.derby.impl.jdbc.EmbedStatement;
import java.sql.Statement;
import org.apache.derby.iapi.services.context.ContextManager;
import java.util.Locale;
import java.util.StringTokenizer;
import org.apache.derby.iapi.services.io.FormatableProperties;
import java.security.AccessControlException;
import org.apache.derby.security.SystemPermission;
import java.security.Permission;
import org.apache.derby.iapi.jdbc.ConnectionContext;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.impl.jdbc.EmbedConnection;
import java.sql.Connection;
import org.apache.derby.impl.jdbc.Util;
import java.sql.SQLException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.services.jmx.ManagementService;
import java.util.Properties;
import org.apache.derby.iapi.jdbc.AuthenticationService;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.services.monitor.ModuleControl;

public abstract class InternalDriver implements ModuleControl
{
    private static final Object syncMe;
    private static InternalDriver activeDriver;
    private Object mbean;
    protected boolean active;
    private ContextService contextServiceFactory;
    private AuthenticationService authenticationService;
    private static boolean deregister;
    
    public static final InternalDriver activeDriver() {
        return InternalDriver.activeDriver;
    }
    
    public InternalDriver() {
        this.contextServiceFactory = ContextService.getFactory();
    }
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        synchronized (InternalDriver.syncMe) {
            InternalDriver.activeDriver = this;
        }
        this.active = true;
        this.mbean = ((ManagementService)Monitor.getSystemModule("org.apache.derby.iapi.services.jmx.ManagementService")).registerMBean(new JDBC(this), JDBCMBean.class, "type=JDBC");
    }
    
    public void stop() {
        synchronized (InternalDriver.syncMe) {
            InternalDriver.activeDriver = null;
        }
        ((ManagementService)Monitor.getSystemModule("org.apache.derby.iapi.services.jmx.ManagementService")).unregisterMBean(this.mbean);
        this.active = false;
        this.contextServiceFactory = null;
    }
    
    public boolean acceptsURL(final String s) throws SQLException {
        return this.active && embeddedDriverAcceptsURL(s);
    }
    
    public static boolean embeddedDriverAcceptsURL(final String s) throws SQLException {
        if (s == null) {
            throw Util.generateCsSQLException("XJ028.C", "null");
        }
        return !s.startsWith("jdbc:derby:net:") && !s.startsWith("jdbc:derby://") && (s.startsWith("jdbc:derby:") || s.equals("jdbc:default:connection"));
    }
    
    public Connection connect(final String s, final Properties properties, final int n) throws SQLException {
        if (!this.acceptsURL(s)) {
            return null;
        }
        if (EmbedConnection.memoryState.isLowMemory()) {
            throw EmbedConnection.NO_MEM;
        }
        if (s.equals("jdbc:default:connection")) {
            final ConnectionContext connectionContext = this.getConnectionContext();
            if (connectionContext != null) {
                return connectionContext.getNestedConnection(false);
            }
            return null;
        }
        else {
            Properties attributes = null;
            try {
                attributes = this.getAttributes(s, properties);
                if (Boolean.valueOf(attributes.getProperty("shutdown")) && getDatabaseName(s, attributes).length() == 0) {
                    if (this.getAuthenticationService() == null) {
                        throw Util.generateCsSQLException("08004", MessageService.getTextMessage("A001"));
                    }
                    if (!this.getAuthenticationService().authenticate(null, attributes)) {
                        throw Util.generateCsSQLException("08004.C.1", MessageService.getTextMessage("A020"));
                    }
                    if (attributes.getProperty("deregister") != null) {
                        setDeregister(Boolean.valueOf(attributes.getProperty("deregister")));
                    }
                    Monitor.getMonitor().shutdown();
                    throw Util.generateCsSQLException("XJ015.M");
                }
                else {
                    EmbedConnection embedConnection;
                    if (n <= 0) {
                        embedConnection = this.getNewEmbedConnection(s, attributes);
                    }
                    else {
                        embedConnection = this.timeLogin(s, attributes, n);
                    }
                    if (embedConnection.isClosed()) {
                        return null;
                    }
                    return embedConnection;
                }
            }
            catch (OutOfMemoryError outOfMemoryError) {
                EmbedConnection.memoryState.setLowMemory();
                throw EmbedConnection.NO_MEM;
            }
            finally {
                if (attributes != null) {
                    ((FormatableProperties)attributes).clearDefaults();
                }
            }
        }
    }
    
    protected abstract EmbedConnection timeLogin(final String p0, final Properties p1, final int p2) throws SQLException;
    
    public abstract void checkSystemPrivileges(final String p0, final Permission p1) throws Exception;
    
    private void checkShutdownPrivileges(final String s) throws SQLException {
        if (System.getSecurityManager() == null) {
            return;
        }
        try {
            this.checkSystemPrivileges(s, new SystemPermission("engine", "shutdown"));
        }
        catch (AccessControlException ex) {
            throw Util.generateCsSQLException("08004.C.9", s, (Object)ex);
        }
        catch (Exception ex2) {
            throw Util.generateCsSQLException("08004.C.9", s, (Object)ex2);
        }
    }
    
    public int getMajorVersion() {
        return Monitor.getMonitor().getEngineVersion().getMajorVersion();
    }
    
    public int getMinorVersion() {
        return Monitor.getMonitor().getEngineVersion().getMinorVersion();
    }
    
    public boolean jdbcCompliant() {
        return true;
    }
    
    protected FormatableProperties getAttributes(final String str, final Properties properties) throws SQLException {
        final FormatableProperties formatableProperties = new FormatableProperties(properties);
        final StringTokenizer stringTokenizer = new StringTokenizer(str, ";");
        stringTokenizer.nextToken();
        while (stringTokenizer.hasMoreTokens()) {
            final String nextToken = stringTokenizer.nextToken();
            final int index = nextToken.indexOf(61);
            if (index == -1) {
                throw Util.generateCsSQLException("XJ028.C", str);
            }
            formatableProperties.put(nextToken.substring(0, index).trim(), nextToken.substring(index + 1).trim());
        }
        checkBoolean(formatableProperties, "dataEncryption");
        checkBoolean(formatableProperties, "create");
        checkBoolean(formatableProperties, "shutdown");
        checkBoolean(formatableProperties, "deregister");
        checkBoolean(formatableProperties, "upgrade");
        return formatableProperties;
    }
    
    private static void checkBoolean(final Properties properties, final String s) throws SQLException {
        checkEnumeration(properties, s, new String[] { "true", "false" });
    }
    
    private static void checkEnumeration(final Properties properties, final String key, final String[] array) throws SQLException {
        final String property = properties.getProperty(key);
        if (property == null) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            if (property.toUpperCase(Locale.ENGLISH).equals(array[i].toUpperCase(Locale.ENGLISH))) {
                return;
            }
        }
        String str = "{";
        for (int j = 0; j < array.length; ++j) {
            if (j > 0) {
                str += "|";
            }
            str += array[j];
        }
        throw Util.generateCsSQLException("XJ05B.C", key, property, str + "}");
    }
    
    public static String getDatabaseName(final String s, final Properties properties) {
        if (s.equals("jdbc:default:connection")) {
            return "";
        }
        final int index = s.indexOf(59);
        String defaultValue;
        if (index == -1) {
            defaultValue = s.substring("jdbc:derby:".length());
        }
        else {
            defaultValue = s.substring("jdbc:derby:".length(), index);
        }
        if (defaultValue.length() == 0 && properties != null) {
            defaultValue = properties.getProperty("databaseName", defaultValue);
        }
        return defaultValue.trim();
    }
    
    public final ContextService getContextServiceFactory() {
        return this.contextServiceFactory;
    }
    
    public AuthenticationService getAuthenticationService() {
        if (this.authenticationService == null) {
            this.authenticationService = (AuthenticationService)Monitor.findService("org.apache.derby.iapi.jdbc.AuthenticationService", "authentication");
        }
        return this.authenticationService;
    }
    
    protected abstract EmbedConnection getNewEmbedConnection(final String p0, final Properties p1) throws SQLException;
    
    private ConnectionContext getConnectionContext() {
        final ContextManager currentContextManager = this.getCurrentContextManager();
        ConnectionContext connectionContext = null;
        if (currentContextManager != null) {
            connectionContext = (ConnectionContext)currentContextManager.getContext("JDBC_ConnectionContext");
        }
        return connectionContext;
    }
    
    private ContextManager getCurrentContextManager() {
        return this.getContextServiceFactory().getCurrentContextManager();
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public abstract Connection getNewNestedConnection(final EmbedConnection p0);
    
    public Statement newEmbedStatement(final EmbedConnection embedConnection, final boolean b, final int n, final int n2, final int n3) {
        return new EmbedStatement(embedConnection, b, n, n2, n3);
    }
    
    public abstract PreparedStatement newEmbedPreparedStatement(final EmbedConnection p0, final String p1, final boolean p2, final int p3, final int p4, final int p5, final int p6, final int[] p7, final String[] p8) throws SQLException;
    
    public abstract CallableStatement newEmbedCallableStatement(final EmbedConnection p0, final String p1, final int p2, final int p3, final int p4) throws SQLException;
    
    public DatabaseMetaData newEmbedDatabaseMetaData(final EmbedConnection embedConnection, final String s) throws SQLException {
        return new EmbedDatabaseMetaData(embedConnection, s);
    }
    
    public abstract EmbedResultSet newEmbedResultSet(final EmbedConnection p0, final ResultSet p1, final boolean p2, final EmbedStatement p3, final boolean p4) throws SQLException;
    
    public EmbedResultSetMetaData newEmbedResultSetMetaData(final ResultColumnDescriptor[] array) {
        return new EmbedResultSetMetaData(array);
    }
    
    static void setDeregister(final boolean deregister) {
        InternalDriver.deregister = deregister;
    }
    
    static boolean getDeregister() {
        return InternalDriver.deregister;
    }
    
    static {
        syncMe = new Object();
        InternalDriver.deregister = true;
    }
}
