// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.viewfs;

import org.apache.hadoop.util.StringUtils;
import java.util.Arrays;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;

public class ConfigUtil
{
    public static String getConfigViewFsPrefix(final String mountTableName) {
        return "fs.viewfs.mounttable." + mountTableName;
    }
    
    public static String getConfigViewFsPrefix() {
        return getConfigViewFsPrefix("fs.viewfs.mounttable.default");
    }
    
    public static void addLink(final Configuration conf, final String mountTableName, final String src, final URI target) {
        conf.set(getConfigViewFsPrefix(mountTableName) + "." + "link" + "." + src, target.toString());
    }
    
    public static void addLink(final Configuration conf, final String src, final URI target) {
        addLink(conf, "default", src, target);
    }
    
    public static void addLinkMergeSlash(final Configuration conf, final String mountTableName, final URI target) {
        conf.set(getConfigViewFsPrefix(mountTableName) + "." + "linkMergeSlash", target.toString());
    }
    
    public static void addLinkMergeSlash(final Configuration conf, final URI target) {
        addLinkMergeSlash(conf, "default", target);
    }
    
    public static void addLinkFallback(final Configuration conf, final String mountTableName, final URI target) {
        conf.set(getConfigViewFsPrefix(mountTableName) + "." + "linkFallback", target.toString());
    }
    
    public static void addLinkFallback(final Configuration conf, final URI target) {
        addLinkFallback(conf, "default", target);
    }
    
    public static void addLinkMerge(final Configuration conf, final String mountTableName, final URI[] targets) {
        conf.set(getConfigViewFsPrefix(mountTableName) + "." + "linkMerge", Arrays.toString(targets));
    }
    
    public static void addLinkMerge(final Configuration conf, final URI[] targets) {
        addLinkMerge(conf, "default", targets);
    }
    
    public static void addLinkNfly(final Configuration conf, final String mountTableName, final String src, String settings, final URI... targets) {
        settings = ((settings == null) ? "minReplication=2,repairOnRead=true" : settings);
        conf.set(getConfigViewFsPrefix(mountTableName) + "." + "linkNfly" + "." + settings + "." + src, StringUtils.uriToString(targets));
    }
    
    public static void addLinkNfly(final Configuration conf, final String src, final URI... targets) {
        addLinkNfly(conf, "default", src, null, targets);
    }
    
    public static void setHomeDirConf(final Configuration conf, final String homedir) {
        setHomeDirConf(conf, "default", homedir);
    }
    
    public static void setHomeDirConf(final Configuration conf, final String mountTableName, final String homedir) {
        if (!homedir.startsWith("/")) {
            throw new IllegalArgumentException("Home dir should start with /:" + homedir);
        }
        conf.set(getConfigViewFsPrefix(mountTableName) + "." + "homedir", homedir);
    }
    
    public static String getHomeDirValue(final Configuration conf) {
        return getHomeDirValue(conf, "default");
    }
    
    public static String getHomeDirValue(final Configuration conf, final String mountTableName) {
        return conf.get(getConfigViewFsPrefix(mountTableName) + "." + "homedir");
    }
}
