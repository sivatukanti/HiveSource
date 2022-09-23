// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types;

import java.util.StringTokenizer;
import java.util.HashSet;
import java.util.Collection;
import java.util.Set;
import org.apache.tools.ant.ExitException;
import java.lang.reflect.Constructor;
import java.security.cert.Certificate;
import java.security.UnresolvedPermission;
import java.util.Iterator;
import java.util.PropertyPermission;
import java.security.Permission;
import java.net.SocketPermission;
import org.apache.tools.ant.BuildException;
import java.util.LinkedList;
import java.util.List;

public class Permissions
{
    private List<Permission> grantedPermissions;
    private List<Permission> revokedPermissions;
    private java.security.Permissions granted;
    private SecurityManager origSm;
    private boolean active;
    private boolean delegateToOldSM;
    private static final Class<?>[] PARAMS;
    
    public Permissions() {
        this(false);
    }
    
    public Permissions(final boolean delegateToOldSM) {
        this.grantedPermissions = new LinkedList<Permission>();
        this.revokedPermissions = new LinkedList<Permission>();
        this.granted = null;
        this.origSm = null;
        this.active = false;
        this.delegateToOldSM = delegateToOldSM;
    }
    
    public void addConfiguredGrant(final Permission perm) {
        this.grantedPermissions.add(perm);
    }
    
    public void addConfiguredRevoke(final Permission perm) {
        this.revokedPermissions.add(perm);
    }
    
    public synchronized void setSecurityManager() throws BuildException {
        this.origSm = System.getSecurityManager();
        this.init();
        System.setSecurityManager(new MySM());
        this.active = true;
    }
    
    private void init() throws BuildException {
        this.granted = new java.security.Permissions();
        for (final Permission p : this.revokedPermissions) {
            if (p.getClassName() == null) {
                throw new BuildException("Revoked permission " + p + " does not contain a class.");
            }
        }
        for (final Permission p : this.grantedPermissions) {
            if (p.getClassName() == null) {
                throw new BuildException("Granted permission " + p + " does not contain a class.");
            }
            final java.security.Permission perm = this.createPermission(p);
            this.granted.add(perm);
        }
        this.granted.add(new SocketPermission("localhost:1024-", "listen"));
        this.granted.add(new PropertyPermission("java.version", "read"));
        this.granted.add(new PropertyPermission("java.vendor", "read"));
        this.granted.add(new PropertyPermission("java.vendor.url", "read"));
        this.granted.add(new PropertyPermission("java.class.version", "read"));
        this.granted.add(new PropertyPermission("os.name", "read"));
        this.granted.add(new PropertyPermission("os.version", "read"));
        this.granted.add(new PropertyPermission("os.arch", "read"));
        this.granted.add(new PropertyPermission("file.encoding", "read"));
        this.granted.add(new PropertyPermission("file.separator", "read"));
        this.granted.add(new PropertyPermission("path.separator", "read"));
        this.granted.add(new PropertyPermission("line.separator", "read"));
        this.granted.add(new PropertyPermission("java.specification.version", "read"));
        this.granted.add(new PropertyPermission("java.specification.vendor", "read"));
        this.granted.add(new PropertyPermission("java.specification.name", "read"));
        this.granted.add(new PropertyPermission("java.vm.specification.version", "read"));
        this.granted.add(new PropertyPermission("java.vm.specification.vendor", "read"));
        this.granted.add(new PropertyPermission("java.vm.specification.name", "read"));
        this.granted.add(new PropertyPermission("java.vm.version", "read"));
        this.granted.add(new PropertyPermission("java.vm.vendor", "read"));
        this.granted.add(new PropertyPermission("java.vm.name", "read"));
    }
    
    private java.security.Permission createPermission(final Permission permission) {
        try {
            final Class<? extends java.security.Permission> clazz = Class.forName(permission.getClassName()).asSubclass(java.security.Permission.class);
            final String name = permission.getName();
            final String actions = permission.getActions();
            final Constructor<? extends java.security.Permission> ctr = clazz.getConstructor(Permissions.PARAMS);
            return (java.security.Permission)ctr.newInstance(name, actions);
        }
        catch (Exception e) {
            return new UnresolvedPermission(permission.getClassName(), permission.getName(), permission.getActions(), null);
        }
    }
    
    public synchronized void restoreSecurityManager() {
        this.active = false;
        System.setSecurityManager(this.origSm);
    }
    
    static {
        PARAMS = new Class[] { String.class, String.class };
    }
    
    private class MySM extends SecurityManager
    {
        @Override
        public void checkExit(final int status) {
            final java.security.Permission perm = new RuntimePermission("exitVM", null);
            try {
                this.checkPermission(perm);
            }
            catch (SecurityException e) {
                throw new ExitException(e.getMessage(), status);
            }
        }
        
        @Override
        public void checkPermission(final java.security.Permission perm) {
            if (Permissions.this.active) {
                if (Permissions.this.delegateToOldSM && !perm.getName().equals("exitVM")) {
                    boolean permOK = false;
                    if (Permissions.this.granted.implies(perm)) {
                        permOK = true;
                    }
                    this.checkRevoked(perm);
                    if (!permOK && Permissions.this.origSm != null) {
                        Permissions.this.origSm.checkPermission(perm);
                    }
                }
                else {
                    if (!Permissions.this.granted.implies(perm)) {
                        throw new SecurityException("Permission " + perm + " was not granted.");
                    }
                    this.checkRevoked(perm);
                }
            }
        }
        
        private void checkRevoked(final java.security.Permission perm) {
            for (final Permission revoked : Permissions.this.revokedPermissions) {
                if (revoked.matches(perm)) {
                    throw new SecurityException("Permission " + perm + " was revoked.");
                }
            }
        }
    }
    
    public static class Permission
    {
        private String className;
        private String name;
        private String actionString;
        private Set<String> actions;
        
        public void setClass(final String aClass) {
            this.className = aClass.trim();
        }
        
        public String getClassName() {
            return this.className;
        }
        
        public void setName(final String aName) {
            this.name = aName.trim();
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setActions(final String actions) {
            this.actionString = actions;
            if (actions.length() > 0) {
                this.actions = this.parseActions(actions);
            }
        }
        
        public String getActions() {
            return this.actionString;
        }
        
        boolean matches(final java.security.Permission perm) {
            if (!this.className.equals(perm.getClass().getName())) {
                return false;
            }
            if (this.name != null) {
                if (this.name.endsWith("*")) {
                    if (!perm.getName().startsWith(this.name.substring(0, this.name.length() - 1))) {
                        return false;
                    }
                }
                else if (!this.name.equals(perm.getName())) {
                    return false;
                }
            }
            if (this.actions != null) {
                final Set<String> as = this.parseActions(perm.getActions());
                final int size = as.size();
                as.removeAll(this.actions);
                if (as.size() == size) {
                    return false;
                }
            }
            return true;
        }
        
        private Set<String> parseActions(final String actions) {
            final Set<String> result = new HashSet<String>();
            final StringTokenizer tk = new StringTokenizer(actions, ",");
            while (tk.hasMoreTokens()) {
                final String item = tk.nextToken().trim();
                if (!item.equals("")) {
                    result.add(item);
                }
            }
            return result;
        }
        
        @Override
        public String toString() {
            return "Permission: " + this.className + " (\"" + this.name + "\", \"" + this.actions + "\")";
        }
    }
}
