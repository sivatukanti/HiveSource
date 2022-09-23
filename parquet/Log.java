// 
// Decompiled by Procyon v0.5.36
// 

package parquet;

import java.util.logging.Handler;
import java.io.OutputStream;
import java.util.logging.StreamHandler;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.FieldPosition;
import java.util.logging.LogRecord;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Log
{
    public static final Level LEVEL;
    public static final boolean DEBUG;
    public static final boolean INFO;
    public static final boolean WARN;
    public static final boolean ERROR;
    private Logger logger;
    
    public static Log getLog(final Class<?> c) {
        return new Log(c);
    }
    
    public Log(final Class<?> c) {
        this.logger = Logger.getLogger(c.getName());
    }
    
    public void debug(final Object m) {
        if (m instanceof Throwable) {
            this.logger.log(Level.FINE, "", (Throwable)m);
        }
        else {
            this.logger.fine(String.valueOf(m));
        }
    }
    
    public void debug(final Object m, final Throwable t) {
        this.logger.log(Level.FINE, String.valueOf(m), t);
    }
    
    public void info(final Object m) {
        if (m instanceof Throwable) {
            this.logger.log(Level.INFO, "", (Throwable)m);
        }
        else {
            this.logger.info(String.valueOf(m));
        }
    }
    
    public void info(final Object m, final Throwable t) {
        this.logger.log(Level.INFO, String.valueOf(m), t);
    }
    
    public void warn(final Object m) {
        if (m instanceof Throwable) {
            this.logger.log(Level.WARNING, "", (Throwable)m);
        }
        else {
            this.logger.warning(String.valueOf(m));
        }
    }
    
    public void warn(final Object m, final Throwable t) {
        this.logger.log(Level.WARNING, String.valueOf(m), t);
    }
    
    public void error(final Object m) {
        if (m instanceof Throwable) {
            this.logger.log(Level.SEVERE, "", (Throwable)m);
        }
        else {
            this.logger.warning(String.valueOf(m));
        }
    }
    
    public void error(final Object m, final Throwable t) {
        this.logger.log(Level.SEVERE, String.valueOf(m), t);
    }
    
    static {
        LEVEL = Level.INFO;
        DEBUG = (Log.LEVEL.intValue() <= Level.FINE.intValue());
        INFO = (Log.LEVEL.intValue() <= Level.INFO.intValue());
        WARN = (Log.LEVEL.intValue() <= Level.WARNING.intValue());
        ERROR = (Log.LEVEL.intValue() <= Level.SEVERE.intValue());
        final Logger logger = Logger.getLogger(Log.class.getPackage().getName());
        final Handler[] handlers = logger.getHandlers();
        if (handlers == null || handlers.length == 0) {
            logger.setUseParentHandlers(false);
            final StreamHandler handler = new StreamHandler(System.out, new Formatter() {
                Date dat = new Date();
                private static final String format = "{0,date} {0,time}";
                private MessageFormat formatter = new MessageFormat("{0,date} {0,time}");
                private Object[] args = new Object[1];
                
                @Override
                public synchronized String format(final LogRecord record) {
                    final StringBuffer sb = new StringBuffer();
                    this.dat.setTime(record.getMillis());
                    this.args[0] = this.dat;
                    this.formatter.format(this.args, sb, null);
                    sb.append(" ");
                    sb.append(record.getLevel().getLocalizedName());
                    sb.append(": ");
                    sb.append(record.getLoggerName());
                    sb.append(": ");
                    sb.append(this.formatMessage(record));
                    sb.append("\n");
                    if (record.getThrown() != null) {
                        try {
                            final StringWriter sw = new StringWriter();
                            final PrintWriter pw = new PrintWriter(sw);
                            record.getThrown().printStackTrace(pw);
                            pw.close();
                            sb.append(sw.toString());
                        }
                        catch (Exception ex) {}
                    }
                    return sb.toString();
                }
            });
            handler.setLevel(Log.LEVEL);
            logger.addHandler(handler);
        }
        logger.setLevel(Log.LEVEL);
    }
}
