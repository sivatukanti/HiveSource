// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.jmx;

import javax.management.JMException;
import java.util.Enumeration;
import org.apache.log4j.spi.LoggerRepository;
import javax.management.MBeanServer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import javax.management.ObjectName;
import org.apache.log4j.jmx.HierarchyDynamicMBean;

public class ManagedUtil
{
    public static void registerLog4jMBeans() throws JMException {
        if (Boolean.getBoolean("zookeeper.jmx.log4j.disable")) {
            return;
        }
        final MBeanServer mbs = MBeanRegistry.getInstance().getPlatformMBeanServer();
        final HierarchyDynamicMBean hdm = new HierarchyDynamicMBean();
        final ObjectName mbo = new ObjectName("log4j:hiearchy=default");
        mbs.registerMBean(hdm, mbo);
        final Logger rootLogger = Logger.getRootLogger();
        hdm.addLoggerMBean(rootLogger.getName());
        final LoggerRepository r = LogManager.getLoggerRepository();
        final Enumeration enumer = r.getCurrentLoggers();
        Logger logger = null;
        while (enumer.hasMoreElements()) {
            logger = enumer.nextElement();
            hdm.addLoggerMBean(logger.getName());
        }
    }
}
