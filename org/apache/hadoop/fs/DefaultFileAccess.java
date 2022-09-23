// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.ArrayList;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.permission.FsPermission;
import javax.security.auth.login.LoginException;
import java.security.AccessControlException;
import java.io.IOException;
import org.apache.hadoop.security.UserGroupInformation;
import java.util.Arrays;
import org.apache.hadoop.hive.shims.Utils;
import org.apache.hadoop.fs.permission.FsAction;
import java.util.List;
import org.apache.commons.logging.Log;

public class DefaultFileAccess
{
    private static Log LOG;
    private static List<String> emptyGroups;
    
    public static void checkFileAccess(final FileSystem fs, final FileStatus stat, final FsAction action) throws IOException, AccessControlException, LoginException {
        final UserGroupInformation currentUgi = Utils.getUGI();
        checkFileAccess(fs, stat, action, currentUgi.getShortUserName(), Arrays.asList(currentUgi.getGroupNames()));
    }
    
    public static void checkFileAccess(final FileSystem fs, final FileStatus stat, final FsAction action, final String user, List<String> groups) throws IOException, AccessControlException {
        if (groups == null) {
            groups = DefaultFileAccess.emptyGroups;
        }
        final String superGroupName = getSuperGroupName(fs.getConf());
        if (userBelongsToSuperGroup(superGroupName, groups)) {
            DefaultFileAccess.LOG.info("User \"" + user + "\" belongs to super-group \"" + superGroupName + "\". " + "Permission granted for action: " + action + ".");
            return;
        }
        final FsPermission dirPerms = stat.getPermission();
        final String grp = stat.getGroup();
        if (user.equals(stat.getOwner())) {
            if (dirPerms.getUserAction().implies(action)) {
                return;
            }
        }
        else if (groups.contains(grp)) {
            if (dirPerms.getGroupAction().implies(action)) {
                return;
            }
        }
        else if (dirPerms.getOtherAction().implies(action)) {
            return;
        }
        throw new AccessControlException("action " + action + " not permitted on path " + stat.getPath() + " for user " + user);
    }
    
    private static String getSuperGroupName(final Configuration configuration) {
        return configuration.get("dfs.permissions.supergroup", "");
    }
    
    private static boolean userBelongsToSuperGroup(final String superGroupName, final List<String> groups) {
        return groups.contains(superGroupName);
    }
    
    static {
        DefaultFileAccess.LOG = LogFactory.getLog(DefaultFileAccess.class);
        DefaultFileAccess.emptyGroups = new ArrayList<String>(0);
    }
}
