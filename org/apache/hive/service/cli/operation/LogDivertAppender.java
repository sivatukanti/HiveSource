// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.operation;

import org.apache.hadoop.hive.ql.log.PerfLogger;
import org.apache.hadoop.hive.ql.exec.Task;
import com.google.common.base.Joiner;
import java.util.regex.Pattern;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.Filter;
import java.io.Writer;
import org.apache.hadoop.hive.ql.session.OperationLog;
import java.util.Enumeration;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Appender;
import org.apache.hive.service.cli.CLIServiceUtils;
import java.io.CharArrayWriter;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;

public class LogDivertAppender extends WriterAppender
{
    private static final Logger LOG;
    private final OperationManager operationManager;
    private boolean isVerbose;
    private Layout verboseLayout;
    private final CharArrayWriter writer;
    
    private void setLayout(final boolean isVerbose, Layout lo) {
        if (isVerbose) {
            if (lo == null) {
                lo = CLIServiceUtils.verboseLayout;
                LogDivertAppender.LOG.info("Cannot find a Layout from a ConsoleAppender. Using default Layout pattern.");
            }
        }
        else {
            lo = CLIServiceUtils.nonVerboseLayout;
        }
        this.setLayout(lo);
    }
    
    private void initLayout(final boolean isVerbose) {
        final Logger root = Logger.getRootLogger();
        Layout layout = null;
        final Enumeration<?> appenders = (Enumeration<?>)root.getAllAppenders();
        while (appenders.hasMoreElements()) {
            final Appender ap = (Appender)appenders.nextElement();
            if (ap.getClass().equals(ConsoleAppender.class)) {
                layout = ap.getLayout();
                break;
            }
        }
        this.setLayout(isVerbose, layout);
    }
    
    public LogDivertAppender(final OperationManager operationManager, final OperationLog.LoggingLevel loggingMode) {
        this.writer = new CharArrayWriter();
        this.initLayout(this.isVerbose = (loggingMode == OperationLog.LoggingLevel.VERBOSE));
        this.setWriter(this.writer);
        this.setName("LogDivertAppender");
        this.operationManager = operationManager;
        this.verboseLayout = (this.isVerbose ? this.layout : CLIServiceUtils.verboseLayout);
        this.addFilter(new NameFilter(loggingMode, operationManager));
    }
    
    @Override
    public void doAppend(final LoggingEvent event) {
        final OperationLog log = this.operationManager.getOperationLogByThread();
        if (log != null) {
            final boolean isCurrModeVerbose = log.getOpLoggingLevel() == OperationLog.LoggingLevel.VERBOSE;
            if (isCurrModeVerbose != this.isVerbose) {
                this.setLayout(this.isVerbose = isCurrModeVerbose, this.verboseLayout);
            }
        }
        super.doAppend(event);
    }
    
    @Override
    protected void subAppend(final LoggingEvent event) {
        super.subAppend(event);
        final String logOutput = this.writer.toString();
        this.writer.reset();
        final OperationLog log = this.operationManager.getOperationLogByThread();
        if (log == null) {
            LogDivertAppender.LOG.debug(" ---+++=== Dropped log event from thread " + event.getThreadName());
            return;
        }
        log.writeOperationLog(logOutput);
    }
    
    static {
        LOG = Logger.getLogger(LogDivertAppender.class.getName());
    }
    
    private static class NameFilter extends Filter
    {
        private Pattern namePattern;
        private OperationLog.LoggingLevel loggingMode;
        private OperationManager operationManager;
        private static final Pattern verboseExcludeNamePattern;
        private static final Pattern executionIncludeNamePattern;
        private static final Pattern performanceIncludeNamePattern;
        
        private void setCurrentNamePattern(final OperationLog.LoggingLevel mode) {
            if (mode == OperationLog.LoggingLevel.VERBOSE) {
                this.namePattern = NameFilter.verboseExcludeNamePattern;
            }
            else if (mode == OperationLog.LoggingLevel.EXECUTION) {
                this.namePattern = NameFilter.executionIncludeNamePattern;
            }
            else if (mode == OperationLog.LoggingLevel.PERFORMANCE) {
                this.namePattern = NameFilter.performanceIncludeNamePattern;
            }
        }
        
        public NameFilter(final OperationLog.LoggingLevel loggingMode, final OperationManager op) {
            this.operationManager = op;
            this.setCurrentNamePattern(this.loggingMode = loggingMode);
        }
        
        @Override
        public int decide(final LoggingEvent ev) {
            final OperationLog log = this.operationManager.getOperationLogByThread();
            final boolean excludeMatches = this.loggingMode == OperationLog.LoggingLevel.VERBOSE;
            if (log == null) {
                return -1;
            }
            final OperationLog.LoggingLevel currentLoggingMode = log.getOpLoggingLevel();
            if (currentLoggingMode == OperationLog.LoggingLevel.NONE) {
                return -1;
            }
            if (currentLoggingMode != this.loggingMode) {
                this.setCurrentNamePattern(this.loggingMode = currentLoggingMode);
            }
            final boolean isMatch = this.namePattern.matcher(ev.getLoggerName()).matches();
            if (excludeMatches == isMatch) {
                return -1;
            }
            return 0;
        }
        
        static {
            verboseExcludeNamePattern = Pattern.compile(Joiner.on("|").join(new String[] { LogDivertAppender.LOG.getName(), OperationLog.class.getName(), OperationManager.class.getName() }));
            executionIncludeNamePattern = Pattern.compile(Joiner.on("|").join(new String[] { "org.apache.hadoop.mapreduce.JobSubmitter", "org.apache.hadoop.mapreduce.Job", "SessionState", Task.class.getName(), "org.apache.hadoop.hive.ql.exec.spark.status.SparkJobMonitor" }));
            performanceIncludeNamePattern = Pattern.compile(NameFilter.executionIncludeNamePattern.pattern() + "|" + PerfLogger.class.getName());
        }
    }
}
