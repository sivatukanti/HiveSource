// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.common.util;

import org.apache.commons.logging.LogFactory;
import org.apache.hive.common.HiveVersionAnnotation;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.common.classification.InterfaceStability;
import org.apache.hadoop.hive.common.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class HiveVersionInfo
{
    private static final Log LOG;
    private static Package myPackage;
    private static HiveVersionAnnotation version;
    
    static Package getPackage() {
        return HiveVersionInfo.myPackage;
    }
    
    public static String getVersion() {
        return (HiveVersionInfo.version != null) ? HiveVersionInfo.version.version() : "Unknown";
    }
    
    public static String getShortVersion() {
        return (HiveVersionInfo.version != null) ? HiveVersionInfo.version.shortVersion() : "Unknown";
    }
    
    public static String getRevision() {
        return (HiveVersionInfo.version != null) ? HiveVersionInfo.version.revision() : "Unknown";
    }
    
    public static String getBranch() {
        return (HiveVersionInfo.version != null) ? HiveVersionInfo.version.branch() : "Unknown";
    }
    
    public static String getDate() {
        return (HiveVersionInfo.version != null) ? HiveVersionInfo.version.date() : "Unknown";
    }
    
    public static String getUser() {
        return (HiveVersionInfo.version != null) ? HiveVersionInfo.version.user() : "Unknown";
    }
    
    public static String getUrl() {
        return (HiveVersionInfo.version != null) ? HiveVersionInfo.version.url() : "Unknown";
    }
    
    public static String getSrcChecksum() {
        return (HiveVersionInfo.version != null) ? HiveVersionInfo.version.srcChecksum() : "Unknown";
    }
    
    public static String getBuildVersion() {
        return getVersion() + " from " + getRevision() + " by " + getUser() + " source checksum " + getSrcChecksum();
    }
    
    public static void main(final String[] args) {
        HiveVersionInfo.LOG.debug("version: " + HiveVersionInfo.version);
        System.out.println("Hive " + getVersion());
        System.out.println("Subversion " + getUrl() + " -r " + getRevision());
        System.out.println("Compiled by " + getUser() + " on " + getDate());
        System.out.println("From source with checksum " + getSrcChecksum());
    }
    
    static {
        LOG = LogFactory.getLog(HiveVersionInfo.class);
        HiveVersionInfo.myPackage = HiveVersionAnnotation.class.getPackage();
        HiveVersionInfo.version = HiveVersionInfo.myPackage.getAnnotation(HiveVersionAnnotation.class);
    }
}
