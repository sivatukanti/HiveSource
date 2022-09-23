// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.http;

import org.slf4j.LoggerFactory;
import org.apache.log4j.Appender;
import org.apache.commons.logging.Log;
import org.eclipse.jetty.server.NCSARequestLog;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.RequestLog;
import java.util.HashMap;
import org.slf4j.Logger;

public class HttpRequestLog
{
    public static final Logger LOG;
    private static final HashMap<String, String> serverToComponent;
    
    public static RequestLog getRequestLog(String name) {
        final String lookup = HttpRequestLog.serverToComponent.get(name);
        if (lookup != null) {
            name = lookup;
        }
        final String loggerName = "http.requests." + name;
        final String appenderName = name + "requestlog";
        final Log logger = LogFactory.getLog(loggerName);
        boolean isLog4JLogger;
        try {
            isLog4JLogger = (logger instanceof Log4JLogger);
        }
        catch (NoClassDefFoundError err) {
            HttpRequestLog.LOG.debug("Could not load Log4JLogger class", err);
            isLog4JLogger = false;
        }
        if (!isLog4JLogger) {
            HttpRequestLog.LOG.warn("Jetty request log can only be enabled using Log4j");
            return null;
        }
        final Log4JLogger httpLog4JLog = (Log4JLogger)logger;
        final org.apache.log4j.Logger httpLogger = httpLog4JLog.getLogger();
        Appender appender = null;
        try {
            appender = httpLogger.getAppender(appenderName);
        }
        catch (LogConfigurationException e) {
            HttpRequestLog.LOG.warn("Http request log for {} could not be created", loggerName);
            throw e;
        }
        if (appender == null) {
            HttpRequestLog.LOG.info("Http request log for {} is not defined", loggerName);
            return null;
        }
        if (appender instanceof HttpRequestLogAppender) {
            final HttpRequestLogAppender requestLogAppender = (HttpRequestLogAppender)appender;
            final NCSARequestLog requestLog = new NCSARequestLog();
            requestLog.setFilename(requestLogAppender.getFilename());
            requestLog.setRetainDays(requestLogAppender.getRetainDays());
            return requestLog;
        }
        HttpRequestLog.LOG.warn("Jetty request log for {} was of the wrong class", loggerName);
        return null;
    }
    
    static {
        LOG = LoggerFactory.getLogger(HttpRequestLog.class);
        (serverToComponent = new HashMap<String, String>()).put("cluster", "resourcemanager");
        HttpRequestLog.serverToComponent.put("hdfs", "namenode");
        HttpRequestLog.serverToComponent.put("node", "nodemanager");
    }
}
