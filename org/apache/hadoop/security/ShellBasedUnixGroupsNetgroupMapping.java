// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.util.Shell;
import java.util.Iterator;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class ShellBasedUnixGroupsNetgroupMapping extends ShellBasedUnixGroupsMapping
{
    private static final Logger LOG;
    
    @Override
    public List<String> getGroups(final String user) throws IOException {
        final List<String> groups = new LinkedList<String>(super.getGroups(user));
        NetgroupCache.getNetgroups(user, groups);
        return groups;
    }
    
    @Override
    public void cacheGroupsRefresh() throws IOException {
        final List<String> groups = NetgroupCache.getNetgroupNames();
        NetgroupCache.clear();
        this.cacheGroupsAdd(groups);
    }
    
    @Override
    public void cacheGroupsAdd(final List<String> groups) throws IOException {
        for (final String group : groups) {
            if (group.length() == 0) {
                continue;
            }
            if (group.charAt(0) != '@' || NetgroupCache.isCached(group)) {
                continue;
            }
            NetgroupCache.add(group, this.getUsersForNetgroup(group));
        }
    }
    
    protected List<String> getUsersForNetgroup(final String netgroup) throws IOException {
        final List<String> users = new LinkedList<String>();
        String usersRaw = this.execShellGetUserForNetgroup(netgroup);
        usersRaw = usersRaw.replaceAll(" +", "");
        usersRaw = usersRaw.replaceFirst(netgroup.replaceFirst("@", "") + "[()]+", "");
        final String[] split;
        final String[] userInfos = split = usersRaw.split("[()]+");
        for (final String userInfo : split) {
            String user = userInfo.replaceFirst("[^,]*,", "");
            user = user.replaceFirst(",.*$", "");
            users.add(user);
        }
        return users;
    }
    
    protected String execShellGetUserForNetgroup(final String netgroup) throws IOException {
        String result = "";
        try {
            result = Shell.execCommand(Shell.getUsersForNetgroupCommand(netgroup.substring(1)));
        }
        catch (Shell.ExitCodeException e) {
            ShellBasedUnixGroupsNetgroupMapping.LOG.warn("error getting users for netgroup " + netgroup, e);
        }
        return result;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ShellBasedUnixGroupsNetgroupMapping.class);
    }
}
