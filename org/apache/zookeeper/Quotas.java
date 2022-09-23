// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

public class Quotas
{
    public static final String procZookeeper = "/zookeeper";
    public static final String quotaZookeeper = "/zookeeper/quota";
    public static final String limitNode = "zookeeper_limits";
    public static final String statNode = "zookeeper_stats";
    
    public static String quotaPath(final String path) {
        return "/zookeeper/quota" + path + "/" + "zookeeper_limits";
    }
    
    public static String statPath(final String path) {
        return "/zookeeper/quota" + path + "/" + "zookeeper_stats";
    }
}
