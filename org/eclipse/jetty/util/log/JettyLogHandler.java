// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.log;

import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.text.MessageFormat;
import java.util.regex.Pattern;
import java.util.logging.LogRecord;
import java.util.logging.Level;
import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Handler;

public class JettyLogHandler extends Handler
{
    public static void config() {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final URL url = cl.getResource("logging.properties");
        if (url != null) {
            System.err.printf("Initializing java.util.logging from %s%n", url);
            try (final InputStream in = url.openStream()) {
                LogManager.getLogManager().readConfiguration(in);
            }
            catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
        else {
            System.err.printf("WARNING: java.util.logging failed to initialize: logging.properties not found%n", new Object[0]);
        }
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Jdk14Logger");
    }
    
    public JettyLogHandler() {
        if (Boolean.parseBoolean(Log.__props.getProperty("org.eclipse.jetty.util.log.DEBUG", "false"))) {
            this.setLevel(Level.FINEST);
        }
        if (Boolean.parseBoolean(Log.__props.getProperty("org.eclipse.jetty.util.log.IGNORED", "false"))) {
            this.setLevel(Level.ALL);
        }
        System.err.printf("%s Initialized at level [%s]%n", this.getClass().getName(), this.getLevel().getName());
    }
    
    private synchronized String formatMessage(final LogRecord record) {
        final String msg = this.getMessage(record);
        try {
            final Object[] params = record.getParameters();
            if (params == null || params.length == 0) {
                return msg;
            }
            if (Pattern.compile("\\{\\d+\\}").matcher(msg).find()) {
                return MessageFormat.format(msg, params);
            }
            return msg;
        }
        catch (Exception ex) {
            return msg;
        }
    }
    
    private String getMessage(final LogRecord record) {
        final ResourceBundle bundle = record.getResourceBundle();
        if (bundle != null) {
            try {
                return bundle.getString(record.getMessage());
            }
            catch (MissingResourceException ex) {}
        }
        return record.getMessage();
    }
    
    @Override
    public void publish(final LogRecord record) {
        final Logger JLOG = this.getJettyLogger(record.getLoggerName());
        final int level = record.getLevel().intValue();
        if (level >= Level.OFF.intValue()) {
            return;
        }
        final Throwable cause = record.getThrown();
        final String msg = this.formatMessage(record);
        if (level >= Level.WARNING.intValue()) {
            if (cause != null) {
                JLOG.warn(msg, cause);
            }
            else {
                JLOG.warn(msg, new Object[0]);
            }
            return;
        }
        if (level >= Level.INFO.intValue()) {
            if (cause != null) {
                JLOG.info(msg, cause);
            }
            else {
                JLOG.info(msg, new Object[0]);
            }
            return;
        }
        if (level >= Level.FINEST.intValue()) {
            if (cause != null) {
                JLOG.debug(msg, cause);
            }
            else {
                JLOG.debug(msg, new Object[0]);
            }
            return;
        }
        if (level >= Level.ALL.intValue()) {
            JLOG.ignore(cause);
        }
    }
    
    private Logger getJettyLogger(final String loggerName) {
        return Log.getLogger(loggerName);
    }
    
    @Override
    public void flush() {
    }
    
    @Override
    public void close() throws SecurityException {
    }
}
