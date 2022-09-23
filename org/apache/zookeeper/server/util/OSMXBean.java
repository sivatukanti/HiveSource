// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.util;

import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.lang.management.RuntimeMXBean;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import org.slf4j.Logger;

public class OSMXBean
{
    static final Logger LOG;
    private OperatingSystemMXBean osMbean;
    private static final boolean ibmvendor;
    private static final boolean windows;
    private static final boolean linux;
    
    public OSMXBean() {
        this.osMbean = ManagementFactory.getOperatingSystemMXBean();
    }
    
    public boolean getUnix() {
        return !OSMXBean.windows && (!OSMXBean.ibmvendor || OSMXBean.linux);
    }
    
    private Long getOSUnixMXBeanMethod(final String mBeanMethodName) {
        try {
            final Class<?> classRef = Class.forName("com.sun.management.UnixOperatingSystemMXBean");
            if (classRef.isInstance(this.osMbean)) {
                final Method mBeanMethod = classRef.getDeclaredMethod(mBeanMethodName, (Class<?>[])new Class[0]);
                final Object unixos = classRef.cast(this.osMbean);
                return (Long)mBeanMethod.invoke(unixos, new Object[0]);
            }
        }
        catch (Exception e) {
            OSMXBean.LOG.warn("Not able to load class or method for com.sun.managment.UnixOperatingSystemMXBean.", e);
        }
        return null;
    }
    
    public long getOpenFileDescriptorCount() {
        if (!OSMXBean.ibmvendor) {
            final Long ofdc = this.getOSUnixMXBeanMethod("getOpenFileDescriptorCount");
            return (ofdc != null) ? ofdc : -1L;
        }
        try {
            final RuntimeMXBean rtmbean = ManagementFactory.getRuntimeMXBean();
            final String rtname = rtmbean.getName();
            final String[] pidhost = rtname.split("@");
            final Process p = Runtime.getRuntime().exec(new String[] { "bash", "-c", "ls /proc/" + pidhost[0] + "/fdinfo | wc -l" });
            final InputStream in = p.getInputStream();
            final BufferedReader output = new BufferedReader(new InputStreamReader(in));
            try {
                final String openFileDesCount;
                if ((openFileDesCount = output.readLine()) != null) {
                    return Long.parseLong(openFileDesCount);
                }
            }
            finally {
                if (output != null) {
                    output.close();
                }
            }
        }
        catch (IOException ie) {
            OSMXBean.LOG.warn("Not able to get the number of open file descriptors", ie);
        }
        return -1L;
    }
    
    public long getMaxFileDescriptorCount() {
        if (!OSMXBean.ibmvendor) {
            final Long mfdc = this.getOSUnixMXBeanMethod("getMaxFileDescriptorCount");
            return (mfdc != null) ? mfdc : -1L;
        }
        try {
            final Process p = Runtime.getRuntime().exec(new String[] { "bash", "-c", "ulimit -n" });
            final InputStream in = p.getInputStream();
            final BufferedReader output = new BufferedReader(new InputStreamReader(in));
            try {
                final String maxFileDesCount;
                if ((maxFileDesCount = output.readLine()) != null) {
                    return Long.parseLong(maxFileDesCount);
                }
            }
            finally {
                if (output != null) {
                    output.close();
                }
            }
        }
        catch (IOException ie) {
            OSMXBean.LOG.warn("Not able to get the max number of file descriptors", ie);
        }
        return -1L;
    }
    
    static {
        LOG = LoggerFactory.getLogger(OSMXBean.class);
        ibmvendor = System.getProperty("java.vendor").contains("IBM");
        windows = System.getProperty("os.name").startsWith("Windows");
        linux = System.getProperty("os.name").startsWith("Linux");
    }
}
