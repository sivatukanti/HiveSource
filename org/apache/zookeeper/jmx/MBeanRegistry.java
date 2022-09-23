// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.jmx;

import org.slf4j.LoggerFactory;
import javax.management.MalformedObjectNameException;
import java.util.Iterator;
import javax.management.ObjectName;
import javax.management.JMException;
import javax.management.MBeanServerFactory;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ConcurrentHashMap;
import javax.management.MBeanServer;
import java.util.Map;
import org.slf4j.Logger;

public class MBeanRegistry
{
    private static final Logger LOG;
    private static volatile MBeanRegistry instance;
    private Map<ZKMBeanInfo, String> mapBean2Path;
    private Map<String, ZKMBeanInfo> mapName2Bean;
    private MBeanServer mBeanServer;
    
    public static void setInstance(final MBeanRegistry instance) {
        MBeanRegistry.instance = instance;
    }
    
    public static MBeanRegistry getInstance() {
        return MBeanRegistry.instance;
    }
    
    public MBeanRegistry() {
        this.mapBean2Path = new ConcurrentHashMap<ZKMBeanInfo, String>();
        this.mapName2Bean = new ConcurrentHashMap<String, ZKMBeanInfo>();
        try {
            this.mBeanServer = ManagementFactory.getPlatformMBeanServer();
        }
        catch (Error e) {
            this.mBeanServer = MBeanServerFactory.createMBeanServer();
        }
    }
    
    public MBeanServer getPlatformMBeanServer() {
        return this.mBeanServer;
    }
    
    public void register(final ZKMBeanInfo bean, final ZKMBeanInfo parent) throws JMException {
        assert bean != null;
        String path = null;
        if (parent != null) {
            path = this.mapBean2Path.get(parent);
            assert path != null;
        }
        path = this.makeFullPath(path, parent);
        if (bean.isHidden()) {
            return;
        }
        final ObjectName oname = this.makeObjectName(path, bean);
        try {
            this.mBeanServer.registerMBean(bean, oname);
            this.mapBean2Path.put(bean, path);
            this.mapName2Bean.put(bean.getName(), bean);
        }
        catch (JMException e) {
            MBeanRegistry.LOG.warn("Failed to register MBean " + bean.getName());
            throw e;
        }
    }
    
    private void unregister(final String path, final ZKMBeanInfo bean) throws JMException {
        if (path == null) {
            return;
        }
        if (!bean.isHidden()) {
            try {
                this.mBeanServer.unregisterMBean(this.makeObjectName(path, bean));
            }
            catch (JMException e) {
                MBeanRegistry.LOG.warn("Failed to unregister MBean " + bean.getName());
                throw e;
            }
        }
    }
    
    public void unregister(final ZKMBeanInfo bean) {
        if (bean == null) {
            return;
        }
        final String path = this.mapBean2Path.get(bean);
        try {
            this.unregister(path, bean);
        }
        catch (JMException e) {
            MBeanRegistry.LOG.warn("Error during unregister", e);
        }
        this.mapBean2Path.remove(bean);
        this.mapName2Bean.remove(bean.getName());
    }
    
    public void unregisterAll() {
        for (final Map.Entry<ZKMBeanInfo, String> e : this.mapBean2Path.entrySet()) {
            try {
                this.unregister(e.getValue(), e.getKey());
            }
            catch (JMException e2) {
                MBeanRegistry.LOG.warn("Error during unregister", e2);
            }
        }
        this.mapBean2Path.clear();
        this.mapName2Bean.clear();
    }
    
    public String makeFullPath(final String prefix, final String... name) {
        final StringBuilder sb = new StringBuilder((prefix == null) ? "/" : (prefix.equals("/") ? prefix : (prefix + "/")));
        boolean first = true;
        for (final String s : name) {
            if (s != null) {
                if (!first) {
                    sb.append("/");
                }
                else {
                    first = false;
                }
                sb.append(s);
            }
        }
        return sb.toString();
    }
    
    protected String makeFullPath(final String prefix, final ZKMBeanInfo bean) {
        return this.makeFullPath(prefix, (bean == null) ? null : bean.getName());
    }
    
    private int tokenize(final StringBuilder sb, final String path, int index) {
        final String[] split;
        final String[] tokens = split = path.split("/");
        for (final String s : split) {
            if (s.length() != 0) {
                sb.append("name").append(index++).append("=").append(s).append(",");
            }
        }
        return index;
    }
    
    protected ObjectName makeObjectName(final String path, final ZKMBeanInfo bean) throws MalformedObjectNameException {
        if (path == null) {
            return null;
        }
        final StringBuilder beanName = new StringBuilder("org.apache.ZooKeeperService:");
        int counter = 0;
        counter = this.tokenize(beanName, path, counter);
        this.tokenize(beanName, bean.getName(), counter);
        beanName.deleteCharAt(beanName.length() - 1);
        try {
            return new ObjectName(beanName.toString());
        }
        catch (MalformedObjectNameException e) {
            MBeanRegistry.LOG.warn("Invalid name \"" + beanName.toString() + "\" for class " + bean.getClass().toString());
            throw e;
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(MBeanRegistry.class);
        MBeanRegistry.instance = new MBeanRegistry();
    }
}
