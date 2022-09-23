// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.jdbc;

import java.sql.DriverManager;
import java.io.IOException;
import java.util.jar.Manifest;
import java.net.URL;
import java.sql.DriverPropertyInfo;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import java.sql.Connection;
import java.util.Properties;
import java.sql.SQLException;
import java.util.regex.Pattern;
import java.util.jar.Attributes;
import java.sql.Driver;

public class HiveDriver implements Driver
{
    private static final boolean JDBC_COMPLIANT = false;
    private static final String DBNAME_PROPERTY_KEY = "DBNAME";
    private static final String HOST_PROPERTY_KEY = "HOST";
    private static final String PORT_PROPERTY_KEY = "PORT";
    private static Attributes manifestAttributes;
    
    public HiveDriver() {
        final SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkWrite("foobah");
        }
    }
    
    @Override
    public boolean acceptsURL(final String url) throws SQLException {
        return Pattern.matches("jdbc:hive2://.*", url);
    }
    
    @Override
    public Connection connect(final String url, final Properties info) throws SQLException {
        return this.acceptsURL(url) ? new HiveConnection(url, info) : null;
    }
    
    static int getMajorDriverVersion() {
        int version = -1;
        try {
            final String fullVersion = fetchManifestAttribute(Attributes.Name.IMPLEMENTATION_VERSION);
            final String[] tokens = fullVersion.split("\\.");
            if (tokens != null && tokens.length > 0 && tokens[0] != null) {
                version = Integer.parseInt(tokens[0]);
            }
        }
        catch (Exception e) {
            version = -1;
        }
        return version;
    }
    
    static int getMinorDriverVersion() {
        int version = -1;
        try {
            final String fullVersion = fetchManifestAttribute(Attributes.Name.IMPLEMENTATION_VERSION);
            final String[] tokens = fullVersion.split("\\.");
            if (tokens != null && tokens.length > 1 && tokens[1] != null) {
                version = Integer.parseInt(tokens[1]);
            }
        }
        catch (Exception e) {
            version = -1;
        }
        return version;
    }
    
    @Override
    public int getMajorVersion() {
        return getMajorDriverVersion();
    }
    
    @Override
    public int getMinorVersion() {
        return getMinorDriverVersion();
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Method not supported");
    }
    
    @Override
    public DriverPropertyInfo[] getPropertyInfo(final String url, Properties info) throws SQLException {
        if (info == null) {
            info = new Properties();
        }
        if (url != null && url.startsWith("jdbc:hive2://")) {
            info = this.parseURLforPropertyInfo(url, info);
        }
        final DriverPropertyInfo hostProp = new DriverPropertyInfo("HOST", info.getProperty("HOST", ""));
        hostProp.required = false;
        hostProp.description = "Hostname of Hive Server2";
        final DriverPropertyInfo portProp = new DriverPropertyInfo("PORT", info.getProperty("PORT", ""));
        portProp.required = false;
        portProp.description = "Port number of Hive Server2";
        final DriverPropertyInfo dbProp = new DriverPropertyInfo("DBNAME", info.getProperty("DBNAME", "default"));
        dbProp.required = false;
        dbProp.description = "Database name";
        final DriverPropertyInfo[] dpi = { hostProp, portProp, dbProp };
        return dpi;
    }
    
    @Override
    public boolean jdbcCompliant() {
        return false;
    }
    
    private Properties parseURLforPropertyInfo(final String url, final Properties defaults) throws SQLException {
        final Properties urlProps = (defaults != null) ? new Properties(defaults) : new Properties();
        if (url == null || !url.startsWith("jdbc:hive2://")) {
            throw new SQLException("Invalid connection url: " + url);
        }
        Utils.JdbcConnectionParams params = null;
        try {
            params = Utils.parseURL(url);
        }
        catch (ZooKeeperHiveClientException e) {
            throw new SQLException(e);
        }
        String host = params.getHost();
        if (host == null) {
            host = "";
        }
        String port = Integer.toString(params.getPort());
        if (host.equals("")) {
            port = "";
        }
        else if (port.equals("0") || port.equals("-1")) {
            port = "10000";
        }
        final String db = params.getDbName();
        urlProps.put("HOST", host);
        urlProps.put("PORT", port);
        urlProps.put("DBNAME", db);
        return urlProps;
    }
    
    private static synchronized void loadManifestAttributes() throws IOException {
        if (HiveDriver.manifestAttributes != null) {
            return;
        }
        final Class<?> clazz = HiveDriver.class;
        final String classContainer = clazz.getProtectionDomain().getCodeSource().getLocation().toString();
        final URL manifestUrl = new URL("jar:" + classContainer + "!/META-INF/MANIFEST.MF");
        final Manifest manifest = new Manifest(manifestUrl.openStream());
        HiveDriver.manifestAttributes = manifest.getMainAttributes();
    }
    
    static String fetchManifestAttribute(final Attributes.Name attributeName) throws SQLException {
        try {
            loadManifestAttributes();
        }
        catch (IOException e) {
            throw new SQLException("Couldn't load manifest attributes.", e);
        }
        return HiveDriver.manifestAttributes.getValue(attributeName);
    }
    
    static {
        try {
            DriverManager.registerDriver(new HiveDriver());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        HiveDriver.manifestAttributes = null;
    }
}
