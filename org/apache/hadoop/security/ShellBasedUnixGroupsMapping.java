// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import java.util.LinkedList;
import java.util.StringTokenizer;
import com.google.common.base.Joiner;
import java.util.Map;
import java.io.File;
import org.apache.hadoop.util.Shell;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import java.util.List;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configured;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class ShellBasedUnixGroupsMapping extends Configured implements GroupMappingServiceProvider
{
    @VisibleForTesting
    protected static final Logger LOG;
    private long timeout;
    private static final List<String> EMPTY_GROUPS;
    
    public ShellBasedUnixGroupsMapping() {
        this.timeout = 0L;
    }
    
    @Override
    public void setConf(final Configuration conf) {
        super.setConf(conf);
        if (conf != null) {
            this.timeout = conf.getTimeDuration("hadoop.security.groups.shell.command.timeout", 0L, TimeUnit.MILLISECONDS);
        }
    }
    
    @Override
    public List<String> getGroups(final String userName) throws IOException {
        return this.getUnixGroups(userName);
    }
    
    @Override
    public void cacheGroupsRefresh() throws IOException {
    }
    
    @Override
    public void cacheGroupsAdd(final List<String> groups) throws IOException {
    }
    
    protected Shell.ShellCommandExecutor createGroupExecutor(final String userName) {
        return new Shell.ShellCommandExecutor(this.getGroupsForUserCommand(userName), null, null, this.timeout);
    }
    
    protected String[] getGroupsForUserCommand(final String userName) {
        return Shell.getGroupsForUserCommand(userName);
    }
    
    protected Shell.ShellCommandExecutor createGroupIDExecutor(final String userName) {
        return new Shell.ShellCommandExecutor(this.getGroupsIDForUserCommand(userName), null, null, this.timeout);
    }
    
    protected String[] getGroupsIDForUserCommand(final String userName) {
        return Shell.getGroupsIDForUserCommand(userName);
    }
    
    private boolean handleExecutorTimeout(final Shell.ShellCommandExecutor executor, final String user) {
        if (executor.isTimedOut()) {
            ShellBasedUnixGroupsMapping.LOG.warn("Unable to return groups for user '{}' as shell group lookup command '{}' ran longer than the configured timeout limit of {} seconds.", user, Joiner.on(' ').join(executor.getExecString()), this.timeout);
            return true;
        }
        return false;
    }
    
    private List<String> getUnixGroups(final String user) throws IOException {
        final Shell.ShellCommandExecutor executor = this.createGroupExecutor(user);
        List<String> groups;
        try {
            executor.execute();
            groups = this.resolveFullGroupNames(executor.getOutput());
        }
        catch (Shell.ExitCodeException e) {
            if (this.handleExecutorTimeout(executor, user)) {
                return ShellBasedUnixGroupsMapping.EMPTY_GROUPS;
            }
            try {
                groups = this.resolvePartialGroupNames(user, e.getMessage(), executor.getOutput());
            }
            catch (PartialGroupNameException pge) {
                ShellBasedUnixGroupsMapping.LOG.warn("unable to return groups for user {}", user, pge);
                return ShellBasedUnixGroupsMapping.EMPTY_GROUPS;
            }
        }
        catch (IOException ioe) {
            if (this.handleExecutorTimeout(executor, user)) {
                return ShellBasedUnixGroupsMapping.EMPTY_GROUPS;
            }
            throw ioe;
        }
        if (!Shell.WINDOWS) {
            for (int i = 1; i < groups.size(); ++i) {
                if (groups.get(i).equals(groups.get(0))) {
                    groups.remove(i);
                    break;
                }
            }
        }
        return groups;
    }
    
    private List<String> parsePartialGroupNames(final String groupNames, final String groupIDs) throws PartialGroupNameException {
        final StringTokenizer nameTokenizer = new StringTokenizer(groupNames, Shell.TOKEN_SEPARATOR_REGEX);
        final StringTokenizer idTokenizer = new StringTokenizer(groupIDs, Shell.TOKEN_SEPARATOR_REGEX);
        final List<String> groups = new LinkedList<String>();
        while (nameTokenizer.hasMoreTokens()) {
            if (!idTokenizer.hasMoreTokens()) {
                throw new PartialGroupNameException("Number of group names and ids do not match. group name =" + groupNames + ", group id = " + groupIDs);
            }
            final String groupName = nameTokenizer.nextToken();
            final String groupID = idTokenizer.nextToken();
            if (StringUtils.isNumeric(groupName) && groupName.equals(groupID)) {
                continue;
            }
            groups.add(groupName);
        }
        return groups;
    }
    
    private List<String> resolvePartialGroupNames(final String userName, final String errMessage, final String groupNames) throws PartialGroupNameException {
        if (Shell.WINDOWS) {
            throw new PartialGroupNameException("Does not support partial group name resolution on Windows. " + errMessage);
        }
        if (groupNames.isEmpty()) {
            throw new PartialGroupNameException("The user name '" + userName + "' is not found. " + errMessage);
        }
        ShellBasedUnixGroupsMapping.LOG.warn("Some group names for '{}' are not resolvable. {}", userName, errMessage);
        final Shell.ShellCommandExecutor partialResolver = this.createGroupIDExecutor(userName);
        try {
            partialResolver.execute();
            return this.parsePartialGroupNames(groupNames, partialResolver.getOutput());
        }
        catch (Shell.ExitCodeException ece) {
            throw new PartialGroupNameException("failed to get group id list for user '" + userName + "'", ece);
        }
        catch (IOException ioe) {
            String message = "Can't execute the shell command to get the list of group id for user '" + userName + "'";
            if (partialResolver.isTimedOut()) {
                message = message + " because of the command taking longer than the configured timeout: " + this.timeout + " seconds";
            }
            throw new PartialGroupNameException(message, ioe);
        }
    }
    
    @VisibleForTesting
    protected List<String> resolveFullGroupNames(final String groupNames) {
        final StringTokenizer tokenizer = new StringTokenizer(groupNames, Shell.TOKEN_SEPARATOR_REGEX);
        final List<String> groups = new LinkedList<String>();
        while (tokenizer.hasMoreTokens()) {
            groups.add(tokenizer.nextToken());
        }
        return groups;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ShellBasedUnixGroupsMapping.class);
        EMPTY_GROUPS = new LinkedList<String>();
    }
    
    private static class PartialGroupNameException extends IOException
    {
        public PartialGroupNameException(final String message) {
            super(message);
        }
        
        public PartialGroupNameException(final String message, final Throwable err) {
            super(message, err);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("PartialGroupNameException ");
            sb.append(super.getMessage());
            return sb.toString();
        }
    }
}
