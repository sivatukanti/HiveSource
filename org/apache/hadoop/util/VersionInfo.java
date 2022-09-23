// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.io.InputStream;
import java.io.Closeable;
import org.apache.hadoop.io.IOUtils;
import java.io.IOException;
import org.slf4j.LoggerFactory;
import java.util.Properties;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class VersionInfo
{
    private static final Logger LOG;
    private Properties info;
    private static VersionInfo COMMON_VERSION_INFO;
    
    protected VersionInfo(final String component) {
        this.info = new Properties();
        final String versionInfoFile = component + "-version-info.properties";
        InputStream is = null;
        try {
            is = ThreadUtil.getResourceAsStream(VersionInfo.class.getClassLoader(), versionInfoFile);
            this.info.load(is);
        }
        catch (IOException ex) {
            LoggerFactory.getLogger(this.getClass()).warn("Could not read '" + versionInfoFile + "', " + ex.toString(), ex);
        }
        finally {
            IOUtils.closeStream(is);
        }
    }
    
    protected String _getVersion() {
        return this.info.getProperty("version", "Unknown");
    }
    
    protected String _getRevision() {
        return this.info.getProperty("revision", "Unknown");
    }
    
    protected String _getBranch() {
        return this.info.getProperty("branch", "Unknown");
    }
    
    protected String _getDate() {
        return this.info.getProperty("date", "Unknown");
    }
    
    protected String _getUser() {
        return this.info.getProperty("user", "Unknown");
    }
    
    protected String _getUrl() {
        return this.info.getProperty("url", "Unknown");
    }
    
    protected String _getSrcChecksum() {
        return this.info.getProperty("srcChecksum", "Unknown");
    }
    
    protected String _getBuildVersion() {
        return this._getVersion() + " from " + this._getRevision() + " by " + this._getUser() + " source checksum " + this._getSrcChecksum();
    }
    
    protected String _getProtocVersion() {
        return this.info.getProperty("protocVersion", "Unknown");
    }
    
    public static String getVersion() {
        return VersionInfo.COMMON_VERSION_INFO._getVersion();
    }
    
    public static String getRevision() {
        return VersionInfo.COMMON_VERSION_INFO._getRevision();
    }
    
    public static String getBranch() {
        return VersionInfo.COMMON_VERSION_INFO._getBranch();
    }
    
    public static String getDate() {
        return VersionInfo.COMMON_VERSION_INFO._getDate();
    }
    
    public static String getUser() {
        return VersionInfo.COMMON_VERSION_INFO._getUser();
    }
    
    public static String getUrl() {
        return VersionInfo.COMMON_VERSION_INFO._getUrl();
    }
    
    public static String getSrcChecksum() {
        return VersionInfo.COMMON_VERSION_INFO._getSrcChecksum();
    }
    
    public static String getBuildVersion() {
        return VersionInfo.COMMON_VERSION_INFO._getBuildVersion();
    }
    
    public static String getProtocVersion() {
        return VersionInfo.COMMON_VERSION_INFO._getProtocVersion();
    }
    
    public static void main(final String[] args) {
        VersionInfo.LOG.debug("version: " + getVersion());
        System.out.println("Hadoop " + getVersion());
        System.out.println("Source code repository " + getUrl() + " -r " + getRevision());
        System.out.println("Compiled by " + getUser() + " on " + getDate());
        System.out.println("Compiled with protoc " + getProtocVersion());
        System.out.println("From source with checksum " + getSrcChecksum());
        System.out.println("This command was run using " + ClassUtil.findContainingJar(VersionInfo.class));
    }
    
    static {
        LOG = LoggerFactory.getLogger(VersionInfo.class);
        VersionInfo.COMMON_VERSION_INFO = new VersionInfo("common");
    }
}
