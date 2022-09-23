// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.log;

import java.util.Iterator;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import java.security.AccessControlException;
import java.util.Map;
import java.util.Properties;
import java.io.PrintStream;
import org.eclipse.jetty.util.DateCache;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject("Jetty StdErr Logging Implementation")
public class StdErrLog extends AbstractLogger
{
    private static final String EOL;
    private static int __tagpad;
    private static DateCache _dateCache;
    private static final boolean __source;
    private static final boolean __long;
    private static final boolean __escape;
    private int _level;
    private int _configuredLevel;
    private PrintStream _stderr;
    private boolean _source;
    private boolean _printLongNames;
    private final String _name;
    private final String _abbrevname;
    private boolean _hideStacks;
    
    public static void setTagPad(final int pad) {
        StdErrLog.__tagpad = pad;
    }
    
    public static int getLoggingLevel(final Properties props, final String name) {
        int level = AbstractLogger.lookupLoggingLevel(props, name);
        if (level == -1) {
            level = AbstractLogger.lookupLoggingLevel(props, "log");
            if (level == -1) {
                level = 2;
            }
        }
        return level;
    }
    
    public static StdErrLog getLogger(final Class<?> clazz) {
        final Logger log = Log.getLogger(clazz);
        if (log instanceof StdErrLog) {
            return (StdErrLog)log;
        }
        throw new RuntimeException("Logger for " + clazz + " is not of type StdErrLog");
    }
    
    public StdErrLog() {
        this(null);
    }
    
    public StdErrLog(final String name) {
        this(name, null);
    }
    
    public StdErrLog(final String name, final Properties props) {
        this._level = 2;
        this._stderr = null;
        this._source = StdErrLog.__source;
        this._printLongNames = StdErrLog.__long;
        this._hideStacks = false;
        if (props != null && props != Log.__props) {
            Log.__props.putAll(props);
        }
        this._name = ((name == null) ? "" : name);
        this._abbrevname = AbstractLogger.condensePackageString(this._name);
        this._level = getLoggingLevel(Log.__props, this._name);
        this._configuredLevel = this._level;
        try {
            final String source = AbstractLogger.getLoggingProperty(Log.__props, this._name, "SOURCE");
            this._source = ((source == null) ? StdErrLog.__source : Boolean.parseBoolean(source));
        }
        catch (AccessControlException ace) {
            this._source = StdErrLog.__source;
        }
        try {
            final String stacks = AbstractLogger.getLoggingProperty(Log.__props, this._name, "STACKS");
            this._hideStacks = (stacks != null && !Boolean.parseBoolean(stacks));
        }
        catch (AccessControlException ex) {}
    }
    
    @Override
    public String getName() {
        return this._name;
    }
    
    public void setPrintLongNames(final boolean printLongNames) {
        this._printLongNames = printLongNames;
    }
    
    public boolean isPrintLongNames() {
        return this._printLongNames;
    }
    
    public boolean isHideStacks() {
        return this._hideStacks;
    }
    
    public void setHideStacks(final boolean hideStacks) {
        this._hideStacks = hideStacks;
    }
    
    public boolean isSource() {
        return this._source;
    }
    
    public void setSource(final boolean source) {
        this._source = source;
    }
    
    @Override
    public void warn(final String msg, final Object... args) {
        if (this._level <= 3) {
            final StringBuilder buffer = new StringBuilder(64);
            this.format(buffer, ":WARN:", msg, args);
            ((this._stderr == null) ? System.err : this._stderr).println(buffer);
        }
    }
    
    @Override
    public void warn(final Throwable thrown) {
        this.warn("", thrown);
    }
    
    @Override
    public void warn(final String msg, final Throwable thrown) {
        if (this._level <= 3) {
            final StringBuilder buffer = new StringBuilder(64);
            this.format(buffer, ":WARN:", msg, thrown);
            ((this._stderr == null) ? System.err : this._stderr).println(buffer);
        }
    }
    
    @Override
    public void info(final String msg, final Object... args) {
        if (this._level <= 2) {
            final StringBuilder buffer = new StringBuilder(64);
            this.format(buffer, ":INFO:", msg, args);
            ((this._stderr == null) ? System.err : this._stderr).println(buffer);
        }
    }
    
    @Override
    public void info(final Throwable thrown) {
        this.info("", thrown);
    }
    
    @Override
    public void info(final String msg, final Throwable thrown) {
        if (this._level <= 2) {
            final StringBuilder buffer = new StringBuilder(64);
            this.format(buffer, ":INFO:", msg, thrown);
            ((this._stderr == null) ? System.err : this._stderr).println(buffer);
        }
    }
    
    @ManagedAttribute("is debug enabled for root logger Log.LOG")
    @Override
    public boolean isDebugEnabled() {
        return this._level <= 1;
    }
    
    @Override
    public void setDebugEnabled(final boolean enabled) {
        if (enabled) {
            this._level = 1;
            for (final Logger log : Log.getLoggers().values()) {
                if (log.getName().startsWith(this.getName()) && log instanceof StdErrLog) {
                    ((StdErrLog)log).setLevel(1);
                }
            }
        }
        else {
            this._level = this._configuredLevel;
            for (final Logger log : Log.getLoggers().values()) {
                if (log.getName().startsWith(this.getName()) && log instanceof StdErrLog) {
                    ((StdErrLog)log).setLevel(((StdErrLog)log)._configuredLevel);
                }
            }
        }
    }
    
    public int getLevel() {
        return this._level;
    }
    
    public void setLevel(final int level) {
        this._level = level;
    }
    
    public void setStdErrStream(final PrintStream stream) {
        this._stderr = ((stream == System.err) ? null : stream);
    }
    
    @Override
    public void debug(final String msg, final Object... args) {
        if (this._level <= 1) {
            final StringBuilder buffer = new StringBuilder(64);
            this.format(buffer, ":DBUG:", msg, args);
            ((this._stderr == null) ? System.err : this._stderr).println(buffer);
        }
    }
    
    @Override
    public void debug(final String msg, final long arg) {
        if (this.isDebugEnabled()) {
            final StringBuilder buffer = new StringBuilder(64);
            this.format(buffer, ":DBUG:", msg, arg);
            ((this._stderr == null) ? System.err : this._stderr).println(buffer);
        }
    }
    
    @Override
    public void debug(final Throwable thrown) {
        this.debug("", thrown);
    }
    
    @Override
    public void debug(final String msg, final Throwable thrown) {
        if (this._level <= 1) {
            final StringBuilder buffer = new StringBuilder(64);
            this.format(buffer, ":DBUG:", msg, thrown);
            ((this._stderr == null) ? System.err : this._stderr).println(buffer);
        }
    }
    
    private void format(final StringBuilder buffer, final String level, final String msg, final Object... args) {
        final long now = System.currentTimeMillis();
        final int ms = (int)(now % 1000L);
        final String d = StdErrLog._dateCache.formatNow(now);
        this.tag(buffer, d, ms, level);
        this.format(buffer, msg, args);
    }
    
    private void format(final StringBuilder buffer, final String level, final String msg, final Throwable thrown) {
        this.format(buffer, level, msg, new Object[0]);
        if (this.isHideStacks()) {
            this.format(buffer, ": " + String.valueOf(thrown), new Object[0]);
        }
        else {
            this.format(buffer, thrown);
        }
    }
    
    private void tag(final StringBuilder buffer, final String d, final int ms, final String tag) {
        buffer.setLength(0);
        buffer.append(d);
        if (ms > 99) {
            buffer.append('.');
        }
        else if (ms > 9) {
            buffer.append(".0");
        }
        else {
            buffer.append(".00");
        }
        buffer.append(ms).append(tag);
        final String name = this._printLongNames ? this._name : this._abbrevname;
        final String tname = Thread.currentThread().getName();
        final int p = (StdErrLog.__tagpad > 0) ? (name.length() + tname.length() - StdErrLog.__tagpad) : 0;
        if (p < 0) {
            buffer.append(name).append(':').append("                                                  ", 0, -p).append(tname);
        }
        else if (p == 0) {
            buffer.append(name).append(':').append(tname);
        }
        buffer.append(':');
        if (this._source) {
            final Throwable source = new Throwable();
            final StackTraceElement[] frames = source.getStackTrace();
            for (int i = 0; i < frames.length; ++i) {
                final StackTraceElement frame = frames[i];
                final String clazz = frame.getClassName();
                if (!clazz.equals(StdErrLog.class.getName()) && !clazz.equals(Log.class.getName())) {
                    if (!this._printLongNames && clazz.startsWith("org.eclipse.jetty.")) {
                        buffer.append(AbstractLogger.condensePackageString(clazz));
                    }
                    else {
                        buffer.append(clazz);
                    }
                    buffer.append('#').append(frame.getMethodName());
                    if (frame.getFileName() != null) {
                        buffer.append('(').append(frame.getFileName()).append(':').append(frame.getLineNumber()).append(')');
                    }
                    buffer.append(':');
                    break;
                }
            }
        }
        buffer.append(' ');
    }
    
    private void format(final StringBuilder builder, String msg, final Object... args) {
        if (msg == null) {
            msg = "";
            for (int i = 0; i < args.length; ++i) {
                msg += "{} ";
            }
        }
        final String braces = "{}";
        int start = 0;
        for (final Object arg : args) {
            final int bracesIndex = msg.indexOf(braces, start);
            if (bracesIndex < 0) {
                this.escape(builder, msg.substring(start));
                builder.append(" ");
                builder.append(arg);
                start = msg.length();
            }
            else {
                this.escape(builder, msg.substring(start, bracesIndex));
                builder.append(String.valueOf(arg));
                start = bracesIndex + braces.length();
            }
        }
        this.escape(builder, msg.substring(start));
    }
    
    private void escape(final StringBuilder builder, final String string) {
        if (StdErrLog.__escape) {
            for (int i = 0; i < string.length(); ++i) {
                final char c = string.charAt(i);
                if (Character.isISOControl(c)) {
                    if (c == '\n') {
                        builder.append('|');
                    }
                    else if (c == '\r') {
                        builder.append('<');
                    }
                    else {
                        builder.append('?');
                    }
                }
                else {
                    builder.append(c);
                }
            }
        }
        else {
            builder.append(string);
        }
    }
    
    protected void format(final StringBuilder buffer, final Throwable thrown) {
        this.format(buffer, thrown, "");
    }
    
    protected void format(final StringBuilder buffer, final Throwable thrown, final String indent) {
        if (thrown == null) {
            buffer.append("null");
        }
        else {
            buffer.append(StdErrLog.EOL).append(indent);
            this.format(buffer, thrown.toString(), new Object[0]);
            final StackTraceElement[] elements = thrown.getStackTrace();
            for (int i = 0; elements != null && i < elements.length; ++i) {
                buffer.append(StdErrLog.EOL).append(indent).append("\tat ");
                this.format(buffer, elements[i].toString(), new Object[0]);
            }
            for (final Throwable suppressed : thrown.getSuppressed()) {
                buffer.append(StdErrLog.EOL).append(indent).append("Suppressed: ");
                this.format(buffer, suppressed, "\t|" + indent);
            }
            final Throwable cause = thrown.getCause();
            if (cause != null && cause != thrown) {
                buffer.append(StdErrLog.EOL).append(indent).append("Caused by: ");
                this.format(buffer, cause, indent);
            }
        }
    }
    
    @Override
    protected Logger newLogger(final String fullname) {
        final StdErrLog logger = new StdErrLog(fullname);
        logger.setPrintLongNames(this._printLongNames);
        logger._stderr = this._stderr;
        if (this._level != this._configuredLevel) {
            logger._level = this._level;
        }
        return logger;
    }
    
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        s.append("StdErrLog:");
        s.append(this._name);
        s.append(":LEVEL=");
        switch (this._level) {
            case 0: {
                s.append("ALL");
                break;
            }
            case 1: {
                s.append("DEBUG");
                break;
            }
            case 2: {
                s.append("INFO");
                break;
            }
            case 3: {
                s.append("WARN");
                break;
            }
            default: {
                s.append("?");
                break;
            }
        }
        return s.toString();
    }
    
    @Override
    public void ignore(final Throwable ignored) {
        if (this._level <= 0) {
            final StringBuilder buffer = new StringBuilder(64);
            this.format(buffer, ":IGNORED:", "", ignored);
            ((this._stderr == null) ? System.err : this._stderr).println(buffer);
        }
    }
    
    static {
        EOL = System.getProperty("line.separator");
        StdErrLog.__tagpad = Integer.parseInt(Log.__props.getProperty("org.eclipse.jetty.util.log.StdErrLog.TAG_PAD", "0"));
        __source = Boolean.parseBoolean(Log.__props.getProperty("org.eclipse.jetty.util.log.SOURCE", Log.__props.getProperty("org.eclipse.jetty.util.log.stderr.SOURCE", "false")));
        __long = Boolean.parseBoolean(Log.__props.getProperty("org.eclipse.jetty.util.log.stderr.LONG", "false"));
        __escape = Boolean.parseBoolean(Log.__props.getProperty("org.eclipse.jetty.util.log.stderr.ESCAPE", "true"));
        final String[] array;
        final String[] deprecatedProperties = array = new String[] { "DEBUG", "org.eclipse.jetty.util.log.DEBUG", "org.eclipse.jetty.util.log.stderr.DEBUG" };
        for (final String deprecatedProp : array) {
            if (System.getProperty(deprecatedProp) != null) {
                System.err.printf("System Property [%s] has been deprecated! (Use org.eclipse.jetty.LEVEL=DEBUG instead)%n", deprecatedProp);
            }
        }
        try {
            StdErrLog._dateCache = new DateCache("yyyy-MM-dd HH:mm:ss");
        }
        catch (Exception x) {
            x.printStackTrace(System.err);
        }
    }
}
