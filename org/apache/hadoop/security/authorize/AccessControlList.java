// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authorize;

import org.apache.hadoop.io.WritableFactories;
import org.apache.hadoop.io.WritableFactory;
import java.io.DataInput;
import java.io.IOException;
import org.apache.hadoop.io.Text;
import java.io.DataOutput;
import java.util.Iterator;
import org.apache.hadoop.security.UserGroupInformation;
import java.util.List;
import java.util.LinkedList;
import org.apache.hadoop.util.StringUtils;
import java.util.HashSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.Groups;
import java.util.Collection;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.Writable;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class AccessControlList implements Writable
{
    public static final String WILDCARD_ACL_VALUE = "*";
    private static final int INITIAL_CAPACITY = 256;
    private Collection<String> users;
    private Collection<String> groups;
    private boolean allAllowed;
    private Groups groupsMapping;
    
    public AccessControlList() {
        this.groupsMapping = Groups.getUserToGroupsMappingService(new Configuration());
    }
    
    public AccessControlList(final String aclString) {
        this.groupsMapping = Groups.getUserToGroupsMappingService(new Configuration());
        this.buildACL(aclString.split(" ", 2));
    }
    
    public AccessControlList(final String users, final String groups) {
        this.groupsMapping = Groups.getUserToGroupsMappingService(new Configuration());
        this.buildACL(new String[] { users, groups });
    }
    
    private void buildACL(final String[] userGroupStrings) {
        this.users = new HashSet<String>();
        this.groups = new HashSet<String>();
        for (final String aclPart : userGroupStrings) {
            if (aclPart != null && this.isWildCardACLValue(aclPart)) {
                this.allAllowed = true;
                break;
            }
        }
        if (!this.allAllowed) {
            if (userGroupStrings.length >= 1 && userGroupStrings[0] != null) {
                this.users = StringUtils.getTrimmedStringCollection(userGroupStrings[0]);
            }
            if (userGroupStrings.length == 2 && userGroupStrings[1] != null) {
                this.groups = StringUtils.getTrimmedStringCollection(userGroupStrings[1]);
                this.groupsMapping.cacheGroupsAdd(new LinkedList<String>(this.groups));
            }
        }
    }
    
    private boolean isWildCardACLValue(final String aclString) {
        return aclString.contains("*") && aclString.trim().equals("*");
    }
    
    public boolean isAllAllowed() {
        return this.allAllowed;
    }
    
    public void addUser(final String user) {
        if (this.isWildCardACLValue(user)) {
            throw new IllegalArgumentException("User " + user + " can not be added");
        }
        if (!this.isAllAllowed()) {
            this.users.add(user);
        }
    }
    
    public void addGroup(final String group) {
        if (this.isWildCardACLValue(group)) {
            throw new IllegalArgumentException("Group " + group + " can not be added");
        }
        if (!this.isAllAllowed()) {
            final List<String> groupsList = new LinkedList<String>();
            groupsList.add(group);
            this.groupsMapping.cacheGroupsAdd(groupsList);
            this.groups.add(group);
        }
    }
    
    public void removeUser(final String user) {
        if (this.isWildCardACLValue(user)) {
            throw new IllegalArgumentException("User " + user + " can not be removed");
        }
        if (!this.isAllAllowed()) {
            this.users.remove(user);
        }
    }
    
    public void removeGroup(final String group) {
        if (this.isWildCardACLValue(group)) {
            throw new IllegalArgumentException("Group " + group + " can not be removed");
        }
        if (!this.isAllAllowed()) {
            this.groups.remove(group);
        }
    }
    
    public Collection<String> getUsers() {
        return this.users;
    }
    
    public Collection<String> getGroups() {
        return this.groups;
    }
    
    public final boolean isUserInList(final UserGroupInformation ugi) {
        if (this.allAllowed || this.users.contains(ugi.getShortUserName())) {
            return true;
        }
        if (!this.groups.isEmpty()) {
            for (final String group : ugi.getGroups()) {
                if (this.groups.contains(group)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isUserAllowed(final UserGroupInformation ugi) {
        return this.isUserInList(ugi);
    }
    
    @Override
    public String toString() {
        String str = null;
        if (this.allAllowed) {
            str = "All users are allowed";
        }
        else if (this.users.isEmpty() && this.groups.isEmpty()) {
            str = "No users are allowed";
        }
        else {
            String usersStr = null;
            String groupsStr = null;
            if (!this.users.isEmpty()) {
                usersStr = this.users.toString();
            }
            if (!this.groups.isEmpty()) {
                groupsStr = this.groups.toString();
            }
            if (!this.users.isEmpty() && !this.groups.isEmpty()) {
                str = "Users " + usersStr + " and members of the groups " + groupsStr + " are allowed";
            }
            else if (!this.users.isEmpty()) {
                str = "Users " + usersStr + " are allowed";
            }
            else {
                str = "Members of the groups " + groupsStr + " are allowed";
            }
        }
        return str;
    }
    
    public String getAclString() {
        final StringBuilder sb = new StringBuilder(256);
        if (this.allAllowed) {
            sb.append('*');
        }
        else {
            sb.append(this.getUsersString());
            sb.append(" ");
            sb.append(this.getGroupsString());
        }
        return sb.toString();
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        final String aclString = this.getAclString();
        Text.writeString(out, aclString);
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        final String aclString = Text.readString(in);
        this.buildACL(aclString.split(" ", 2));
    }
    
    private String getUsersString() {
        return this.getString(this.users);
    }
    
    private String getGroupsString() {
        return this.getString(this.groups);
    }
    
    private String getString(final Collection<String> strings) {
        final StringBuilder sb = new StringBuilder(256);
        boolean first = true;
        for (final String str : strings) {
            if (!first) {
                sb.append(",");
            }
            else {
                first = false;
            }
            sb.append(str);
        }
        return sb.toString();
    }
    
    static {
        WritableFactories.setFactory(AccessControlList.class, new WritableFactory() {
            @Override
            public Writable newInstance() {
                return new AccessControlList();
            }
        });
    }
}
