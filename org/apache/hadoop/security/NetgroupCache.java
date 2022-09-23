// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import java.util.Map;
import java.util.Collections;
import java.util.Iterator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Unstable
public class NetgroupCache
{
    private static ConcurrentHashMap<String, Set<String>> userToNetgroupsMap;
    
    public static void getNetgroups(final String user, final List<String> groups) {
        final Set<String> userGroups = NetgroupCache.userToNetgroupsMap.get(user);
        if (userGroups != null) {
            groups.addAll(userGroups);
        }
    }
    
    public static List<String> getNetgroupNames() {
        return new LinkedList<String>(getGroups());
    }
    
    private static Set<String> getGroups() {
        final Set<String> allGroups = new HashSet<String>();
        for (final Set<String> userGroups : NetgroupCache.userToNetgroupsMap.values()) {
            allGroups.addAll(userGroups);
        }
        return allGroups;
    }
    
    public static boolean isCached(final String group) {
        return getGroups().contains(group);
    }
    
    public static void clear() {
        NetgroupCache.userToNetgroupsMap.clear();
    }
    
    public static void add(final String group, final List<String> users) {
        for (final String user : users) {
            Set<String> userGroups = NetgroupCache.userToNetgroupsMap.get(user);
            if (userGroups == null) {
                userGroups = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
                final Set<String> currentSet = NetgroupCache.userToNetgroupsMap.putIfAbsent(user, userGroups);
                if (currentSet != null) {
                    userGroups = currentSet;
                }
            }
            userGroups.add(group);
        }
    }
    
    static {
        NetgroupCache.userToNetgroupsMap = new ConcurrentHashMap<String, Set<String>>();
    }
}
