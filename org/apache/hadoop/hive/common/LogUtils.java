// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common;

import org.apache.commons.logging.LogFactory;
import java.net.URL;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.LogManager;
import java.io.File;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.commons.logging.Log;

public class LogUtils
{
    private static final String HIVE_L4J = "hive-log4j.properties";
    private static final String HIVE_EXEC_L4J = "hive-exec-log4j.properties";
    private static final Log l4j;
    
    public static String initHiveLog4j() throws LogInitializationException {
        return initHiveLog4jCommon(HiveConf.ConfVars.HIVE_LOG4J_FILE);
    }
    
    public static String initHiveExecLog4j() throws LogInitializationException {
        return initHiveLog4jCommon(HiveConf.ConfVars.HIVE_EXEC_LOG4J_FILE);
    }
    
    private static String initHiveLog4jCommon(final HiveConf.ConfVars confVarName) throws LogInitializationException {
        final HiveConf conf = new HiveConf();
        if (HiveConf.getVar(conf, confVarName).equals("")) {
            return initHiveLog4jDefault(conf, "", confVarName);
        }
        final String log4jFileName = HiveConf.getVar(conf, confVarName);
        final File log4jConfigFile = new File(log4jFileName);
        final boolean fileExists = log4jConfigFile.exists();
        if (!fileExists) {
            return initHiveLog4jDefault(conf, "Not able to find conf file: " + log4jConfigFile, confVarName);
        }
        if (confVarName == HiveConf.ConfVars.HIVE_EXEC_LOG4J_FILE) {
            String queryId = HiveConf.getVar(conf, HiveConf.ConfVars.HIVEQUERYID);
            if (queryId == null || (queryId = queryId.trim()).isEmpty()) {
                queryId = "unknown-" + System.currentTimeMillis();
            }
            System.setProperty(HiveConf.ConfVars.HIVEQUERYID.toString(), queryId);
        }
        LogManager.resetConfiguration();
        PropertyConfigurator.configure(log4jFileName);
        logConfigLocation(conf);
        return "Logging initialized using configuration in " + log4jConfigFile;
    }
    
    private static String initHiveLog4jDefault(final HiveConf conf, final String logMessage, final HiveConf.ConfVars confVarName) throws LogInitializationException {
        URL hive_l4j = null;
        switch (confVarName) {
            case HIVE_EXEC_LOG4J_FILE: {
                hive_l4j = LogUtils.class.getClassLoader().getResource("hive-exec-log4j.properties");
                if (hive_l4j == null) {
                    hive_l4j = LogUtils.class.getClassLoader().getResource("hive-log4j.properties");
                }
                System.setProperty(HiveConf.ConfVars.HIVEQUERYID.toString(), HiveConf.getVar(conf, HiveConf.ConfVars.HIVEQUERYID));
                break;
            }
            case HIVE_LOG4J_FILE: {
                hive_l4j = LogUtils.class.getClassLoader().getResource("hive-log4j.properties");
                break;
            }
        }
        if (hive_l4j != null) {
            LogManager.resetConfiguration();
            PropertyConfigurator.configure(hive_l4j);
            logConfigLocation(conf);
            return logMessage + "\n" + "Logging initialized using configuration in " + hive_l4j;
        }
        throw new LogInitializationException(logMessage + "Unable to initialize logging using " + "hive-log4j.properties" + ", not found on CLASSPATH!");
    }
    
    private static void logConfigLocation(final HiveConf conf) throws LogInitializationException {
        if (conf.getHiveDefaultLocation() != null) {
            LogUtils.l4j.warn("DEPRECATED: Ignoring hive-default.xml found on the CLASSPATH at " + conf.getHiveDefaultLocation().getPath());
        }
        if (HiveConf.getHiveSiteLocation() == null) {
            LogUtils.l4j.warn("hive-site.xml not found on CLASSPATH");
        }
        else {
            LogUtils.l4j.debug("Using hive-site.xml found on CLASSPATH at " + HiveConf.getHiveSiteLocation().getPath());
        }
    }
    
    static {
        l4j = LogFactory.getLog(LogUtils.class);
    }
    
    public static class LogInitializationException extends Exception
    {
        public LogInitializationException(final String msg) {
            super(msg);
        }
    }
}
