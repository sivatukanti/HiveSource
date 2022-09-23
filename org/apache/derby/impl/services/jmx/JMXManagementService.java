// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.jmx;

import java.security.AccessControlException;
import java.security.Permission;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.management.JMException;
import javax.management.MBeanInfo;
import java.security.AccessController;
import java.lang.management.ManagementFactory;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.mbeans.VersionMBean;
import org.apache.derby.iapi.services.info.Version;
import org.apache.derby.mbeans.ManagementMBean;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.util.HashMap;
import java.util.Properties;
import org.apache.derby.security.SystemPermission;
import javax.management.StandardMBean;
import javax.management.ObjectName;
import java.util.Map;
import javax.management.MBeanServer;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.services.jmx.ManagementService;

public final class JMXManagementService implements ManagementService, ModuleControl
{
    private MBeanServer mbeanServer;
    private Map<ObjectName, StandardMBean> registeredMbeans;
    private ObjectName myManagementBean;
    private MBeanServer myManagementServer;
    private String systemIdentifier;
    private static final SystemPermission CONTROL;
    
    public synchronized void boot(final boolean b, final Properties properties) throws StandardException {
        this.registeredMbeans = new HashMap<ObjectName, StandardMBean>();
        this.systemIdentifier = Monitor.getMonitor().getUUIDFactory().createUUID().toString();
        this.findServer();
        this.myManagementBean = (ObjectName)this.registerMBean(this, ManagementMBean.class, "type=Management");
        this.myManagementServer = this.mbeanServer;
        this.registerMBean(new Version(Monitor.getMonitor().getEngineVersion(), "engine"), VersionMBean.class, "type=Version,jar=derby.jar");
    }
    
    public synchronized void stop() {
        if (this.mbeanServer == null && this.myManagementBean != null) {
            this.mbeanServer = this.myManagementServer;
            this.unregisterMBean(this.myManagementBean);
            this.mbeanServer = null;
        }
        final Iterator<ObjectName> iterator = new HashSet<ObjectName>(this.registeredMbeans.keySet()).iterator();
        while (iterator.hasNext()) {
            this.unregisterMBean(iterator.next());
        }
        this.mbeanServer = null;
        this.registeredMbeans = null;
        this.myManagementServer = null;
        this.systemIdentifier = null;
    }
    
    private synchronized void findServer() {
        try {
            this.mbeanServer = AccessController.doPrivileged((PrivilegedAction<MBeanServer>)new PrivilegedAction<MBeanServer>() {
                public MBeanServer run() {
                    return ManagementFactory.getPlatformMBeanServer();
                }
            });
        }
        catch (SecurityException ex) {}
    }
    
    public synchronized Object registerMBean(final Object o, final Class clazz, final String str) throws StandardException {
        try {
            final ObjectName objectName = new ObjectName("org.apache.derby:" + str + ",system=" + this.systemIdentifier);
            final StandardMBean standardMBean = new StandardMBean(o, clazz) {
                @Override
                protected String getClassName(final MBeanInfo mBeanInfo) {
                    return clazz.getName();
                }
            };
            this.registeredMbeans.put(objectName, standardMBean);
            if (this.mbeanServer != null) {
                this.jmxRegister(standardMBean, objectName);
            }
            return objectName;
        }
        catch (JMException ex) {
            throw StandardException.plainWrapException(ex);
        }
    }
    
    private void jmxRegister(final StandardMBean standardMBean, final ObjectName objectName) throws JMException {
        if (this.mbeanServer.isRegistered(objectName)) {
            return;
        }
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                public Object run() throws JMException {
                    JMXManagementService.this.mbeanServer.registerMBean(standardMBean, objectName);
                    return null;
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (JMException)ex.getException();
        }
        catch (SecurityException ex2) {}
    }
    
    public void unregisterMBean(final Object o) {
        if (o == null) {
            return;
        }
        this.unregisterMBean((ObjectName)o);
    }
    
    private synchronized void unregisterMBean(final ObjectName objectName) {
        if (this.registeredMbeans == null) {
            return;
        }
        if (this.registeredMbeans.remove(objectName) == null) {
            return;
        }
        if (this.mbeanServer == null) {
            return;
        }
        this.jmxUnregister(objectName);
    }
    
    private void jmxUnregister(final ObjectName objectName) {
        if (!this.mbeanServer.isRegistered(objectName)) {
            return;
        }
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                public Object run() throws JMException {
                    JMXManagementService.this.mbeanServer.unregisterMBean(objectName);
                    return null;
                }
            });
        }
        catch (PrivilegedActionException ex) {}
        catch (SecurityException ex2) {}
    }
    
    public synchronized boolean isManagementActive() {
        return this.mbeanServer != null;
    }
    
    public synchronized void startManagement() {
        if (this.registeredMbeans == null) {
            return;
        }
        this.checkJMXControl();
        if (this.isManagementActive()) {
            return;
        }
        this.findServer();
        if (this.mbeanServer == null) {
            return;
        }
        for (final ObjectName objectName : this.registeredMbeans.keySet()) {
            if (objectName.equals(this.myManagementBean) && this.mbeanServer.isRegistered(this.myManagementBean)) {
                continue;
            }
            try {
                this.jmxRegister(this.registeredMbeans.get(objectName), objectName);
            }
            catch (JMException ex) {}
        }
    }
    
    public synchronized void stopManagement() {
        if (this.registeredMbeans == null) {
            return;
        }
        this.checkJMXControl();
        if (this.isManagementActive()) {
            for (final ObjectName objectName : this.registeredMbeans.keySet()) {
                if (objectName.equals(this.myManagementBean)) {
                    continue;
                }
                this.jmxUnregister(objectName);
            }
            this.mbeanServer = null;
        }
    }
    
    private void checkJMXControl() {
        try {
            if (System.getSecurityManager() != null) {
                AccessController.checkPermission(JMXManagementService.CONTROL);
            }
        }
        catch (AccessControlException ex) {
            throw new SecurityException(ex.getMessage());
        }
    }
    
    public synchronized String getSystemIdentifier() {
        return this.systemIdentifier;
    }
    
    static {
        CONTROL = new SystemPermission("jmx", "control");
    }
}
