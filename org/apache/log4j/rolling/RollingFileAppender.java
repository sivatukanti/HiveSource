// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.rolling;

import org.apache.log4j.Logger;
import java.io.IOException;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.extras.DOMConfigurator;
import java.util.Properties;
import org.w3c.dom.Element;
import java.io.OutputStreamWriter;
import org.apache.log4j.Appender;
import org.apache.log4j.spi.LoggingEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.helpers.QuietWriter;
import java.io.Writer;
import java.io.File;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.rolling.helper.Action;
import org.apache.log4j.xml.UnrecognizedElementHandler;
import org.apache.log4j.FileAppender;

public final class RollingFileAppender extends FileAppender implements UnrecognizedElementHandler
{
    private TriggeringPolicy triggeringPolicy;
    private RollingPolicy rollingPolicy;
    private long fileLength;
    private Action lastRolloverAsyncAction;
    
    public RollingFileAppender() {
        this.fileLength = 0L;
        this.lastRolloverAsyncAction = null;
    }
    
    public void activateOptions() {
        if (this.rollingPolicy == null) {
            LogLog.warn("Please set a rolling policy for the RollingFileAppender named '" + this.getName() + "'");
            return;
        }
        if (this.triggeringPolicy == null && this.rollingPolicy instanceof TriggeringPolicy) {
            this.triggeringPolicy = (TriggeringPolicy)this.rollingPolicy;
        }
        if (this.triggeringPolicy == null) {
            LogLog.warn("Please set a TriggeringPolicy for the RollingFileAppender named '" + this.getName() + "'");
            return;
        }
        Exception exception = null;
        synchronized (this) {
            this.triggeringPolicy.activateOptions();
            this.rollingPolicy.activateOptions();
            try {
                final RolloverDescription rollover = this.rollingPolicy.initialize(this.getFile(), this.getAppend());
                if (rollover != null) {
                    final Action syncAction = rollover.getSynchronous();
                    if (syncAction != null) {
                        syncAction.execute();
                    }
                    this.setFile(rollover.getActiveFileName());
                    this.setAppend(rollover.getAppend());
                    this.lastRolloverAsyncAction = rollover.getAsynchronous();
                    if (this.lastRolloverAsyncAction != null) {
                        final Thread runner = new Thread(this.lastRolloverAsyncAction);
                        runner.start();
                    }
                }
                final File activeFile = new File(this.getFile());
                if (this.getAppend()) {
                    this.fileLength = activeFile.length();
                }
                else {
                    this.fileLength = 0L;
                }
                super.activateOptions();
            }
            catch (Exception ex) {
                exception = ex;
            }
        }
        if (exception != null) {
            LogLog.warn("Exception while initializing RollingFileAppender named '" + this.getName() + "'", exception);
        }
    }
    
    private QuietWriter createQuietWriter(final Writer writer) {
        ErrorHandler handler = this.errorHandler;
        if (handler == null) {
            handler = new DefaultErrorHandler(this);
        }
        return new QuietWriter(writer, handler);
    }
    
    public boolean rollover() {
        if (this.rollingPolicy != null) {
            Exception exception = null;
            synchronized (this) {
                if (this.lastRolloverAsyncAction != null) {
                    this.lastRolloverAsyncAction.close();
                }
                try {
                    final RolloverDescription rollover = this.rollingPolicy.rollover(this.getFile());
                    if (rollover != null) {
                        if (rollover.getActiveFileName().equals(this.getFile())) {
                            this.closeWriter();
                            boolean success = true;
                            if (rollover.getSynchronous() != null) {
                                success = false;
                                try {
                                    success = rollover.getSynchronous().execute();
                                }
                                catch (Exception ex) {
                                    exception = ex;
                                }
                            }
                            if (success) {
                                if (rollover.getAppend()) {
                                    this.fileLength = new File(rollover.getActiveFileName()).length();
                                }
                                else {
                                    this.fileLength = 0L;
                                }
                                if (rollover.getAsynchronous() != null) {
                                    this.lastRolloverAsyncAction = rollover.getAsynchronous();
                                    new Thread(this.lastRolloverAsyncAction).start();
                                }
                                this.setFile(rollover.getActiveFileName(), rollover.getAppend(), this.bufferedIO, this.bufferSize);
                            }
                            else {
                                this.setFile(rollover.getActiveFileName(), true, this.bufferedIO, this.bufferSize);
                                if (exception == null) {
                                    LogLog.warn("Failure in post-close rollover action");
                                }
                                else {
                                    LogLog.warn("Exception in post-close rollover action", exception);
                                }
                            }
                        }
                        else {
                            final Writer newWriter = this.createWriter(this.createFileOutputStream(rollover.getActiveFileName(), rollover.getAppend()));
                            this.closeWriter();
                            this.setFile(rollover.getActiveFileName());
                            this.qw = this.createQuietWriter(newWriter);
                            boolean success2 = true;
                            if (rollover.getSynchronous() != null) {
                                success2 = false;
                                try {
                                    success2 = rollover.getSynchronous().execute();
                                }
                                catch (Exception ex2) {
                                    exception = ex2;
                                }
                            }
                            if (success2) {
                                if (rollover.getAppend()) {
                                    this.fileLength = new File(rollover.getActiveFileName()).length();
                                }
                                else {
                                    this.fileLength = 0L;
                                }
                                if (rollover.getAsynchronous() != null) {
                                    this.lastRolloverAsyncAction = rollover.getAsynchronous();
                                    new Thread(this.lastRolloverAsyncAction).start();
                                }
                            }
                            this.writeHeader();
                        }
                        return true;
                    }
                }
                catch (Exception ex3) {
                    exception = ex3;
                }
            }
            if (exception != null) {
                LogLog.warn("Exception during rollover, rollover deferred.", exception);
            }
        }
        return false;
    }
    
    private FileOutputStream createFileOutputStream(final String newFileName, final boolean append) throws FileNotFoundException {
        try {
            return new FileOutputStream(newFileName, append);
        }
        catch (FileNotFoundException ex) {
            final String parentName = new File(newFileName).getParent();
            if (parentName == null) {
                throw ex;
            }
            final File parentDir = new File(parentName);
            if (!parentDir.exists() && parentDir.mkdirs()) {
                return new FileOutputStream(newFileName, append);
            }
            throw ex;
        }
    }
    
    protected void subAppend(final LoggingEvent event) {
        if (this.triggeringPolicy.isTriggeringEvent(this, event, this.getFile(), this.getFileLength())) {
            try {
                this.rollover();
            }
            catch (Exception ex) {
                LogLog.warn("Exception during rollover attempt.", ex);
            }
        }
        super.subAppend(event);
    }
    
    public RollingPolicy getRollingPolicy() {
        return this.rollingPolicy;
    }
    
    public TriggeringPolicy getTriggeringPolicy() {
        return this.triggeringPolicy;
    }
    
    public void setRollingPolicy(final RollingPolicy policy) {
        this.rollingPolicy = policy;
    }
    
    public void setTriggeringPolicy(final TriggeringPolicy policy) {
        this.triggeringPolicy = policy;
    }
    
    public void close() {
        synchronized (this) {
            if (this.lastRolloverAsyncAction != null) {
                this.lastRolloverAsyncAction.close();
            }
        }
        super.close();
    }
    
    protected OutputStreamWriter createWriter(final OutputStream os) {
        return super.createWriter(new CountingOutputStream(os, this));
    }
    
    public long getFileLength() {
        return this.fileLength;
    }
    
    public synchronized void incrementFileLength(final int increment) {
        this.fileLength += increment;
    }
    
    public boolean parseUnrecognizedElement(final Element element, final Properties props) throws Exception {
        final String nodeName = element.getNodeName();
        if ("rollingPolicy".equals(nodeName)) {
            final OptionHandler rollingPolicy = DOMConfigurator.parseElement(element, props, RollingPolicy.class);
            if (rollingPolicy != null) {
                rollingPolicy.activateOptions();
                this.setRollingPolicy((RollingPolicy)rollingPolicy);
            }
            return true;
        }
        if ("triggeringPolicy".equals(nodeName)) {
            final OptionHandler triggerPolicy = DOMConfigurator.parseElement(element, props, TriggeringPolicy.class);
            if (triggerPolicy != null) {
                triggerPolicy.activateOptions();
                this.setTriggeringPolicy((TriggeringPolicy)triggerPolicy);
            }
            return true;
        }
        return false;
    }
    
    private static class CountingOutputStream extends OutputStream
    {
        private final OutputStream os;
        private final RollingFileAppender rfa;
        
        public CountingOutputStream(final OutputStream os, final RollingFileAppender rfa) {
            this.os = os;
            this.rfa = rfa;
        }
        
        public void close() throws IOException {
            this.os.close();
        }
        
        public void flush() throws IOException {
            this.os.flush();
        }
        
        public void write(final byte[] b) throws IOException {
            this.os.write(b);
            this.rfa.incrementFileLength(b.length);
        }
        
        public void write(final byte[] b, final int off, final int len) throws IOException {
            this.os.write(b, off, len);
            this.rfa.incrementFileLength(len);
        }
        
        public void write(final int b) throws IOException {
            this.os.write(b);
            this.rfa.incrementFileLength(1);
        }
    }
    
    private static final class DefaultErrorHandler implements ErrorHandler
    {
        private final RollingFileAppender appender;
        
        public DefaultErrorHandler(final RollingFileAppender appender) {
            this.appender = appender;
        }
        
        public void setLogger(final Logger logger) {
        }
        
        public void error(final String message, final Exception ioe, final int errorCode) {
            this.appender.close();
            LogLog.error("IO failure for appender named " + this.appender.getName(), ioe);
        }
        
        public void error(final String message) {
        }
        
        public void error(final String message, final Exception e, final int errorCode, final LoggingEvent event) {
        }
        
        public void setAppender(final Appender appender) {
        }
        
        public void setBackupAppender(final Appender appender) {
        }
        
        public void activateOptions() {
        }
    }
}
