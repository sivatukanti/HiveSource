// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.monitor;

import org.apache.derby.iapi.services.info.ProductVersionHolder;
import org.apache.derby.iapi.services.property.PropertyUtil;
import java.util.Locale;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.services.loader.InstanceGetter;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.stream.HeaderPrintWriter;
import org.apache.derby.impl.services.monitor.FileMonitor;
import java.io.PrintWriter;
import java.util.Properties;

public class Monitor
{
    public static final String SERVICE_TYPE_DIRECTORY = "serviceDirectory";
    public static final Object syncMe;
    public static final String NEW_INSTANCE_FROM_ID_TRACE_DEBUG_FLAG;
    public static final String DEBUG_TRUE;
    public static final String DEBUG_FALSE;
    private static ModuleFactory monitor;
    private static boolean active;
    
    public static void startMonitor(final Properties properties, final PrintWriter printWriter) {
        new FileMonitor(properties, printWriter);
    }
    
    public static boolean setMonitor(final ModuleFactory monitor) {
        synchronized (Monitor.syncMe) {
            if (Monitor.active) {
                return false;
            }
            Monitor.monitor = monitor;
            return Monitor.active = true;
        }
    }
    
    public static void clearMonitor() {
        synchronized (Monitor.syncMe) {
            Monitor.active = false;
        }
    }
    
    public static ModuleFactory getMonitor() {
        return Monitor.monitor;
    }
    
    public static ModuleFactory getMonitorLite() {
        synchronized (Monitor.syncMe) {
            if (Monitor.active && Monitor.monitor != null) {
                return Monitor.monitor;
            }
        }
        return new FileMonitor();
    }
    
    public static HeaderPrintWriter getStream() {
        return Monitor.monitor.getSystemStreams().stream();
    }
    
    public static String getServiceName(final Object o) {
        return Monitor.monitor.getServiceName(o);
    }
    
    public static Object startSystemModule(final String s) throws StandardException {
        return Monitor.monitor.startModule(false, null, s, null, null);
    }
    
    public static Object findSystemModule(final String s) throws StandardException {
        final Object module = getMonitor().findModule(null, s, null);
        if (module == null) {
            throw missingImplementation(s);
        }
        return module;
    }
    
    public static Object getSystemModule(final String s) {
        final ModuleFactory monitor = getMonitor();
        if (monitor == null) {
            return null;
        }
        return monitor.findModule(null, s, null);
    }
    
    public static Object bootServiceModule(final boolean b, final Object o, final String s, final Properties properties) throws StandardException {
        return Monitor.monitor.startModule(b, o, s, null, properties);
    }
    
    public static Object bootServiceModule(final boolean b, final Object o, final String s, final String s2, final Properties properties) throws StandardException {
        return Monitor.monitor.startModule(b, o, s, s2, properties);
    }
    
    public static Object findServiceModule(final Object o, final String s) throws StandardException {
        final Object module = getMonitor().findModule(o, s, null);
        if (module == null) {
            throw missingImplementation(s);
        }
        return module;
    }
    
    public static Object getServiceModule(final Object o, final String s) {
        return getMonitor().findModule(o, s, null);
    }
    
    public static Object findService(final String s, final String s2) {
        return Monitor.monitor.findService(s, s2);
    }
    
    public static boolean startPersistentService(final String s, final Properties properties) throws StandardException {
        return Monitor.monitor.startPersistentService(s, properties);
    }
    
    public static Object startNonPersistentService(final String s, final String s2, final Properties properties) throws StandardException {
        return Monitor.monitor.startNonPersistentService(s, s2, properties);
    }
    
    public static Object createPersistentService(final String s, final String s2, final Properties properties) throws StandardException {
        return Monitor.monitor.createPersistentService(s, s2, properties);
    }
    
    public static void removePersistentService(final String s) throws StandardException {
        if (!s.startsWith("memory:")) {
            throw StandardException.newException("XBM0I.D", s);
        }
        Monitor.monitor.removePersistentService(s);
    }
    
    public static InstanceGetter classFromIdentifier(final int n) throws StandardException {
        return Monitor.monitor.classFromIdentifier(n);
    }
    
    public static Object newInstanceFromIdentifier(final int n) throws StandardException {
        return Monitor.monitor.newInstanceFromIdentifier(n);
    }
    
    public static StandardException missingProductVersion(final String s) {
        return StandardException.newException("XBM05.D", s);
    }
    
    public static StandardException missingImplementation(final String s) {
        return StandardException.newException("XBM02.D", s);
    }
    
    public static StandardException exceptionStartingModule(final Throwable t) {
        return StandardException.newException("XBM01.D", t);
    }
    
    public static void logMessage(final String s) {
        getStream().println(s);
    }
    
    public static void logTextMessage(final String s) {
        getStream().println(MessageService.getTextMessage(s));
    }
    
    public static void logTextMessage(final String s, final Object o) {
        getStream().println(MessageService.getTextMessage(s, o));
    }
    
    public static void logTextMessage(final String s, final Object o, final Object o2) {
        getStream().println(MessageService.getTextMessage(s, o, o2));
    }
    
    public static void logTextMessage(final String s, final Object o, final Object o2, final Object o3) {
        getStream().println(MessageService.getTextMessage(s, o, o2, o3));
    }
    
    public static void logTextMessage(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
        getStream().println(MessageService.getTextMessage(s, o, o2, o3, o4));
    }
    
    public static Locale getLocaleFromString(final String s) throws StandardException {
        return Monitor.monitor.getLocaleFromString(s);
    }
    
    public static boolean isFullUpgrade(final Properties properties, final String s) throws StandardException {
        final boolean booleanValue = Boolean.valueOf(properties.getProperty("upgrade"));
        final ProductVersionHolder engineVersion = getMonitor().getEngineVersion();
        if ((engineVersion.isBeta() || engineVersion.isAlpha()) && !PropertyUtil.getSystemBoolean("derby.database.allowPreReleaseUpgrade")) {
            throw StandardException.newException("XCW00.D", s, engineVersion.getSimpleVersionString());
        }
        return booleanValue;
    }
    
    public static boolean isDesiredType(final Properties properties, final int n) {
        int engineType = 2;
        if (properties != null) {
            engineType = getEngineType(properties);
        }
        return (engineType & n) != 0x0;
    }
    
    public static boolean isDesiredType(final int n, final int n2) {
        return (n & n2) != 0x0;
    }
    
    public static int getEngineType(final Properties properties) {
        if (properties != null) {
            final String property = properties.getProperty("derby.engineType");
            return (property == null) ? 2 : Integer.parseInt(property.trim());
        }
        return 2;
    }
    
    public static boolean isDesiredCreateType(final Properties properties, final int n) {
        if (Boolean.valueOf(properties.getProperty("create"))) {
            return (n & 0x2) != 0x0;
        }
        return isDesiredType(properties, n);
    }
    
    public static void logThrowable(final Throwable t) {
        t.printStackTrace(getStream().getPrintWriter());
    }
    
    static {
        syncMe = new Object();
        NEW_INSTANCE_FROM_ID_TRACE_DEBUG_FLAG = null;
        DEBUG_TRUE = null;
        DEBUG_FALSE = null;
    }
}
