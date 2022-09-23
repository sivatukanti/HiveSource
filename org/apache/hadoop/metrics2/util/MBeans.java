// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.util;

import org.slf4j.LoggerFactory;
import com.google.common.annotations.VisibleForTesting;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import java.util.regex.Matcher;
import javax.management.MBeanServer;
import javax.management.InstanceAlreadyExistsException;
import com.google.common.base.Preconditions;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.HashMap;
import javax.management.ObjectName;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public final class MBeans
{
    private static final Logger LOG;
    private static final String DOMAIN_PREFIX = "Hadoop:";
    private static final String SERVICE_PREFIX = "service=";
    private static final String NAME_PREFIX = "name=";
    private static final Pattern MBEAN_NAME_PATTERN;
    
    private MBeans() {
    }
    
    public static ObjectName register(final String serviceName, final String nameName, final Object theMbean) {
        return register(serviceName, nameName, new HashMap<String, String>(), theMbean);
    }
    
    public static ObjectName register(final String serviceName, final String nameName, final Map<String, String> properties, final Object theMbean) {
        final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        Preconditions.checkNotNull(properties, (Object)"JMX bean properties should not be null for bean registration.");
        final ObjectName name = getMBeanName(serviceName, nameName, properties);
        if (name != null) {
            try {
                mbs.registerMBean(theMbean, name);
                MBeans.LOG.debug("Registered " + name);
                return name;
            }
            catch (InstanceAlreadyExistsException iaee) {
                if (MBeans.LOG.isTraceEnabled()) {
                    MBeans.LOG.trace("Failed to register MBean \"" + name + "\"", iaee);
                }
                else {
                    MBeans.LOG.warn("Failed to register MBean \"" + name + "\": Instance already exists.");
                }
            }
            catch (Exception e) {
                MBeans.LOG.warn("Failed to register MBean \"" + name + "\"", e);
            }
        }
        return null;
    }
    
    public static String getMbeanNameService(final ObjectName objectName) {
        final Matcher matcher = MBeans.MBEAN_NAME_PATTERN.matcher(objectName.toString());
        if (matcher.matches()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException(objectName + " is not a valid Hadoop mbean");
    }
    
    public static String getMbeanNameName(final ObjectName objectName) {
        final Matcher matcher = MBeans.MBEAN_NAME_PATTERN.matcher(objectName.toString());
        if (matcher.matches()) {
            return matcher.group(2);
        }
        throw new IllegalArgumentException(objectName + " is not a valid Hadoop mbean");
    }
    
    public static void unregister(final ObjectName mbeanName) {
        MBeans.LOG.debug("Unregistering " + mbeanName);
        final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        if (mbeanName == null) {
            MBeans.LOG.debug("Stacktrace: ", new Throwable());
            return;
        }
        try {
            mbs.unregisterMBean(mbeanName);
        }
        catch (Exception e) {
            MBeans.LOG.warn("Error unregistering " + mbeanName, e);
        }
        DefaultMetricsSystem.removeMBeanName(mbeanName);
    }
    
    @VisibleForTesting
    static ObjectName getMBeanName(final String serviceName, final String nameName, final Map<String, String> additionalParameters) {
        final String additionalKeys = additionalParameters.entrySet().stream().map(entry -> entry.getKey() + "=" + (String)entry.getValue()).collect((Collector<? super Object, ?, String>)Collectors.joining(","));
        final String nameStr = "Hadoop:service=" + serviceName + "," + "name=" + nameName + (additionalKeys.isEmpty() ? "" : ("," + additionalKeys));
        try {
            return DefaultMetricsSystem.newMBeanName(nameStr);
        }
        catch (Exception e) {
            MBeans.LOG.warn("Error creating MBean object name: " + nameStr, e);
            return null;
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(MBeans.class);
        MBEAN_NAME_PATTERN = Pattern.compile("^Hadoop:service=([^,]+),name=(.+)$");
    }
}
