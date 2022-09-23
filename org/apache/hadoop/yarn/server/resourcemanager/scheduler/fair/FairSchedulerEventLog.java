// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Priority;
import org.apache.log4j.Category;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import java.io.IOException;
import java.io.File;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
class FairSchedulerEventLog
{
    private static final Log LOG;
    private boolean logDisabled;
    private String logDir;
    private String logFile;
    private DailyRollingFileAppender appender;
    
    FairSchedulerEventLog() {
        this.logDisabled = true;
    }
    
    boolean init(final FairSchedulerConfiguration conf) {
        if (conf.isEventLogEnabled()) {
            try {
                this.logDir = conf.getEventlogDir();
                final File logDirFile = new File(this.logDir);
                if (!logDirFile.exists() && !logDirFile.mkdirs()) {
                    throw new IOException("Mkdirs failed to create " + logDirFile.toString());
                }
                final String username = System.getProperty("user.name");
                this.logFile = String.format("%s%shadoop-%s-fairscheduler.log", this.logDir, File.separator, username);
                this.logDisabled = false;
                final PatternLayout layout = new PatternLayout("%d{ISO8601}\t%m%n");
                (this.appender = new DailyRollingFileAppender(layout, this.logFile, "'.'yyyy-MM-dd")).activateOptions();
                FairSchedulerEventLog.LOG.info("Initialized fair scheduler event log, logging to " + this.logFile);
            }
            catch (IOException e) {
                FairSchedulerEventLog.LOG.error("Failed to initialize fair scheduler event log. Disabling it.", e);
                this.logDisabled = true;
            }
        }
        else {
            this.logDisabled = true;
        }
        return !this.logDisabled;
    }
    
    synchronized void log(final String eventType, final Object... params) {
        try {
            if (this.logDisabled) {
                return;
            }
            final StringBuffer buffer = new StringBuffer();
            buffer.append(eventType);
            for (final Object param : params) {
                buffer.append("\t");
                buffer.append(param);
            }
            final String message = buffer.toString();
            final Logger logger = Logger.getLogger(this.getClass());
            this.appender.append(new LoggingEvent("", logger, Level.INFO, message, null));
        }
        catch (Exception e) {
            FairSchedulerEventLog.LOG.error("Failed to append to fair scheduler event log", e);
            this.logDisabled = true;
        }
    }
    
    synchronized void shutdown() {
        try {
            if (this.appender != null) {
                this.appender.close();
            }
        }
        catch (Exception e) {
            FairSchedulerEventLog.LOG.error("Failed to close fair scheduler event log", e);
            this.logDisabled = true;
        }
    }
    
    synchronized boolean isEnabled() {
        return !this.logDisabled;
    }
    
    public String getLogFile() {
        return this.logFile;
    }
    
    static {
        LOG = LogFactory.getLog(FairSchedulerEventLog.class.getName());
    }
}
