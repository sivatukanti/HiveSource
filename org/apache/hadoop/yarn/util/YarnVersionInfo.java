// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.util.VersionInfo;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class YarnVersionInfo extends VersionInfo
{
    private static final Log LOG;
    private static YarnVersionInfo YARN_VERSION_INFO;
    
    protected YarnVersionInfo() {
        super("yarn");
    }
    
    public static String getVersion() {
        return YarnVersionInfo.YARN_VERSION_INFO._getVersion();
    }
    
    public static String getRevision() {
        return YarnVersionInfo.YARN_VERSION_INFO._getRevision();
    }
    
    public static String getBranch() {
        return YarnVersionInfo.YARN_VERSION_INFO._getBranch();
    }
    
    public static String getDate() {
        return YarnVersionInfo.YARN_VERSION_INFO._getDate();
    }
    
    public static String getUser() {
        return YarnVersionInfo.YARN_VERSION_INFO._getUser();
    }
    
    public static String getUrl() {
        return YarnVersionInfo.YARN_VERSION_INFO._getUrl();
    }
    
    public static String getSrcChecksum() {
        return YarnVersionInfo.YARN_VERSION_INFO._getSrcChecksum();
    }
    
    public static String getBuildVersion() {
        return YarnVersionInfo.YARN_VERSION_INFO._getBuildVersion();
    }
    
    public static void main(final String[] args) {
        YarnVersionInfo.LOG.debug("version: " + getVersion());
        System.out.println("Yarn " + getVersion());
        System.out.println("Subversion " + getUrl() + " -r " + getRevision());
        System.out.println("Compiled by " + getUser() + " on " + getDate());
        System.out.println("From source with checksum " + getSrcChecksum());
    }
    
    static {
        LOG = LogFactory.getLog(YarnVersionInfo.class);
        YarnVersionInfo.YARN_VERSION_INFO = new YarnVersionInfo();
    }
}
