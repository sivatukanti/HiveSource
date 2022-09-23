// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.shims;

import org.apache.hadoop.util.VersionInfo;
import java.util.Map;
import java.util.HashMap;
import org.apache.hadoop.hive.thrift.HadoopThriftAuthBridge;
import org.apache.log4j.AppenderSkeleton;

public abstract class ShimLoader
{
    public static String HADOOP20SVERSIONNAME;
    public static String HADOOP23VERSIONNAME;
    private static HadoopShims hadoopShims;
    private static JettyShims jettyShims;
    private static AppenderSkeleton eventCounter;
    private static HadoopThriftAuthBridge hadoopThriftAuthBridge;
    private static SchedulerShim schedulerShim;
    private static final HashMap<String, String> HADOOP_SHIM_CLASSES;
    private static final HashMap<String, String> JETTY_SHIM_CLASSES;
    private static final HashMap<String, String> EVENT_COUNTER_SHIM_CLASSES;
    private static final HashMap<String, String> HADOOP_THRIFT_AUTH_BRIDGE_CLASSES;
    private static final String SCHEDULER_SHIM_CLASSE = "org.apache.hadoop.hive.schshim.FairSchedulerShim";
    
    public static synchronized HadoopShims getHadoopShims() {
        if (ShimLoader.hadoopShims == null) {
            ShimLoader.hadoopShims = loadShims(ShimLoader.HADOOP_SHIM_CLASSES, HadoopShims.class);
        }
        return ShimLoader.hadoopShims;
    }
    
    public static synchronized JettyShims getJettyShims() {
        if (ShimLoader.jettyShims == null) {
            ShimLoader.jettyShims = loadShims(ShimLoader.JETTY_SHIM_CLASSES, JettyShims.class);
        }
        return ShimLoader.jettyShims;
    }
    
    public static synchronized AppenderSkeleton getEventCounter() {
        if (ShimLoader.eventCounter == null) {
            ShimLoader.eventCounter = loadShims(ShimLoader.EVENT_COUNTER_SHIM_CLASSES, AppenderSkeleton.class);
        }
        return ShimLoader.eventCounter;
    }
    
    public static synchronized HadoopThriftAuthBridge getHadoopThriftAuthBridge() {
        if (ShimLoader.hadoopThriftAuthBridge == null) {
            ShimLoader.hadoopThriftAuthBridge = loadShims(ShimLoader.HADOOP_THRIFT_AUTH_BRIDGE_CLASSES, HadoopThriftAuthBridge.class);
        }
        return ShimLoader.hadoopThriftAuthBridge;
    }
    
    public static synchronized SchedulerShim getSchedulerShims() {
        if (ShimLoader.schedulerShim == null) {
            ShimLoader.schedulerShim = createShim("org.apache.hadoop.hive.schshim.FairSchedulerShim", SchedulerShim.class);
        }
        return ShimLoader.schedulerShim;
    }
    
    private static <T> T loadShims(final Map<String, String> classMap, final Class<T> xface) {
        final String vers = getMajorVersion();
        final String className = classMap.get(vers);
        return createShim(className, xface);
    }
    
    private static <T> T createShim(final String className, final Class<T> xface) {
        try {
            final Class<?> clazz = Class.forName(className);
            return xface.cast(clazz.newInstance());
        }
        catch (Exception e) {
            throw new RuntimeException("Could not load shims in class " + className, e);
        }
    }
    
    public static String getMajorVersion() {
        final String vers = VersionInfo.getVersion();
        final String[] parts = vers.split("\\.");
        if (parts.length < 2) {
            throw new RuntimeException("Illegal Hadoop Version: " + vers + " (expected A.B.* format)");
        }
        switch (Integer.parseInt(parts[0])) {
            case 1: {
                return ShimLoader.HADOOP20SVERSIONNAME;
            }
            case 2: {
                return ShimLoader.HADOOP23VERSIONNAME;
            }
            default: {
                throw new IllegalArgumentException("Unrecognized Hadoop major version number: " + vers);
            }
        }
    }
    
    private ShimLoader() {
    }
    
    static {
        ShimLoader.HADOOP20SVERSIONNAME = "0.20S";
        ShimLoader.HADOOP23VERSIONNAME = "0.23";
        (HADOOP_SHIM_CLASSES = new HashMap<String, String>()).put(ShimLoader.HADOOP20SVERSIONNAME, "org.apache.hadoop.hive.shims.Hadoop20SShims");
        ShimLoader.HADOOP_SHIM_CLASSES.put(ShimLoader.HADOOP23VERSIONNAME, "org.apache.hadoop.hive.shims.Hadoop23Shims");
        (JETTY_SHIM_CLASSES = new HashMap<String, String>()).put(ShimLoader.HADOOP20SVERSIONNAME, "org.apache.hadoop.hive.shims.Jetty20SShims");
        ShimLoader.JETTY_SHIM_CLASSES.put(ShimLoader.HADOOP23VERSIONNAME, "org.apache.hadoop.hive.shims.Jetty23Shims");
        (EVENT_COUNTER_SHIM_CLASSES = new HashMap<String, String>()).put(ShimLoader.HADOOP20SVERSIONNAME, "org.apache.hadoop.log.metrics.EventCounter");
        ShimLoader.EVENT_COUNTER_SHIM_CLASSES.put(ShimLoader.HADOOP23VERSIONNAME, "org.apache.hadoop.log.metrics.EventCounter");
        (HADOOP_THRIFT_AUTH_BRIDGE_CLASSES = new HashMap<String, String>()).put(ShimLoader.HADOOP20SVERSIONNAME, "org.apache.hadoop.hive.thrift.HadoopThriftAuthBridge");
        ShimLoader.HADOOP_THRIFT_AUTH_BRIDGE_CLASSES.put(ShimLoader.HADOOP23VERSIONNAME, "org.apache.hadoop.hive.thrift.HadoopThriftAuthBridge23");
    }
}
