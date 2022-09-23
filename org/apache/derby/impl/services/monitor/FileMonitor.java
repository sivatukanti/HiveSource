// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.monitor;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.IOException;
import java.io.FileInputStream;
import org.apache.derby.iapi.services.io.FileUtil;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;
import org.apache.derby.iapi.services.info.ProductVersionHolder;
import java.io.File;

public final class FileMonitor extends BaseMonitor
{
    private File home;
    private ProductVersionHolder engineVersion;
    
    public FileMonitor() {
        this.initialize(true);
        this.applicationProperties = this.readApplicationProperties();
    }
    
    public FileMonitor(final Properties properties, final PrintWriter printWriter) {
        this.runWithState(properties, printWriter);
    }
    
    private InputStream PBapplicationPropertiesStream() throws IOException {
        final File file = FileUtil.newFile(this.home, "derby.properties");
        if (!file.exists()) {
            return null;
        }
        return new FileInputStream(file);
    }
    
    public Object getEnvironment() {
        return this.home;
    }
    
    private static ThreadGroup createDaemonGroup() {
        try {
            final ThreadGroup threadGroup = new ThreadGroup("derby.daemons");
            threadGroup.setDaemon(true);
            return threadGroup;
        }
        catch (SecurityException ex) {
            return null;
        }
    }
    
    private boolean PBinitialize(final boolean b) {
        if (!b) {
            this.daemonGroup = createDaemonGroup();
        }
        this.engineVersion = ProductVersionHolder.getProductVersionHolderFromMyEnv(this.getClass().getResourceAsStream("/org/apache/derby/info/DBMS.properties"));
        String property;
        try {
            property = System.getProperty("derby.system.home");
        }
        catch (SecurityException ex) {
            property = null;
        }
        if (property != null) {
            this.home = new File(property);
            if (this.home.exists()) {
                if (!this.home.isDirectory()) {
                    this.report("derby.system.home=" + property + " does not represent a directory");
                    return false;
                }
            }
            else if (!b) {
                boolean b2;
                try {
                    b2 = (this.home.mkdir() || this.home.mkdirs());
                }
                catch (SecurityException ex2) {
                    return false;
                }
                if (b2) {
                    FileUtil.limitAccessToOwner(this.home);
                }
            }
        }
        return true;
    }
    
    private String PBgetJVMProperty(final String key) {
        try {
            return System.getProperty(key);
        }
        catch (SecurityException ex) {
            return null;
        }
    }
    
    final boolean initialize(final boolean b) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
            public Object run() {
                return FileMonitor.this.PBinitialize(b);
            }
        });
    }
    
    final Properties getDefaultModuleProperties() {
        return AccessController.doPrivileged((PrivilegedAction<Properties>)new PrivilegedAction() {
            public Object run() {
                return BaseMonitor.this.getDefaultModuleProperties();
            }
        });
    }
    
    public final String getJVMProperty(final String s) {
        if (!s.startsWith("derby.")) {
            return this.PBgetJVMProperty(s);
        }
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
            public Object run() {
                return FileMonitor.this.PBgetJVMProperty(s);
            }
        });
    }
    
    public final synchronized Thread getDaemonThread(final Runnable runnable, final String s, final boolean b) {
        return AccessController.doPrivileged((PrivilegedAction<Thread>)new PrivilegedAction() {
            public Object run() {
                try {
                    return BaseMonitor.this.getDaemonThread(runnable, s, b);
                }
                catch (IllegalThreadStateException ex) {
                    if (FileMonitor.this.daemonGroup != null && FileMonitor.this.daemonGroup.isDestroyed()) {
                        FileMonitor.this.daemonGroup = createDaemonGroup();
                        return BaseMonitor.this.getDaemonThread(runnable, s, b);
                    }
                    throw ex;
                }
            }
        });
    }
    
    public final void setThreadPriority(final int n) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            public Object run() {
                BaseMonitor.this.setThreadPriority(n);
                return null;
            }
        });
    }
    
    final InputStream applicationPropertiesStream() throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    return FileMonitor.this.PBapplicationPropertiesStream();
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    public final ProductVersionHolder getEngineVersion() {
        return this.engineVersion;
    }
}
