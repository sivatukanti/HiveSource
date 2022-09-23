// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.log;

import org.mortbay.util.DateCache;

public class StdErrLog implements Logger
{
    private static DateCache _dateCache;
    private static boolean __debug;
    private String _name;
    StringBuffer _buffer;
    
    public StdErrLog() {
        this(null);
    }
    
    public StdErrLog(final String name) {
        this._buffer = new StringBuffer();
        this._name = ((name == null) ? "" : name);
    }
    
    public boolean isDebugEnabled() {
        return StdErrLog.__debug;
    }
    
    public void setDebugEnabled(final boolean enabled) {
        StdErrLog.__debug = enabled;
    }
    
    public void info(final String msg, final Object arg0, final Object arg1) {
        final String d = StdErrLog._dateCache.now();
        final int ms = StdErrLog._dateCache.lastMs();
        synchronized (this._buffer) {
            this.tag(d, ms, ":INFO:");
            this.format(msg, arg0, arg1);
            System.err.println(this._buffer.toString());
        }
    }
    
    public void debug(final String msg, final Throwable th) {
        if (StdErrLog.__debug) {
            final String d = StdErrLog._dateCache.now();
            final int ms = StdErrLog._dateCache.lastMs();
            synchronized (this._buffer) {
                this.tag(d, ms, ":DBUG:");
                this.format(msg);
                this.format(th);
                System.err.println(this._buffer.toString());
            }
        }
    }
    
    public void debug(final String msg, final Object arg0, final Object arg1) {
        if (StdErrLog.__debug) {
            final String d = StdErrLog._dateCache.now();
            final int ms = StdErrLog._dateCache.lastMs();
            synchronized (this._buffer) {
                this.tag(d, ms, ":DBUG:");
                this.format(msg, arg0, arg1);
                System.err.println(this._buffer.toString());
            }
        }
    }
    
    public void warn(final String msg, final Object arg0, final Object arg1) {
        final String d = StdErrLog._dateCache.now();
        final int ms = StdErrLog._dateCache.lastMs();
        synchronized (this._buffer) {
            this.tag(d, ms, ":WARN:");
            this.format(msg, arg0, arg1);
            System.err.println(this._buffer.toString());
        }
    }
    
    public void warn(final String msg, final Throwable th) {
        final String d = StdErrLog._dateCache.now();
        final int ms = StdErrLog._dateCache.lastMs();
        synchronized (this._buffer) {
            this.tag(d, ms, ":WARN:");
            this.format(msg);
            this.format(th);
            System.err.println(this._buffer.toString());
        }
    }
    
    private void tag(final String d, final int ms, final String tag) {
        this._buffer.setLength(0);
        this._buffer.append(d);
        if (ms > 99) {
            this._buffer.append('.');
        }
        else if (ms > 9) {
            this._buffer.append(".0");
        }
        else {
            this._buffer.append(".00");
        }
        this._buffer.append(ms).append(tag).append(this._name).append(':');
    }
    
    private void format(final String msg, final Object arg0, final Object arg1) {
        final int i0 = (msg == null) ? -1 : msg.indexOf("{}");
        final int i2 = (i0 < 0) ? -1 : msg.indexOf("{}", i0 + 2);
        if (i0 >= 0) {
            this.format(msg.substring(0, i0));
            this.format(String.valueOf((arg0 == null) ? "null" : arg0));
            if (i2 >= 0) {
                this.format(msg.substring(i0 + 2, i2));
                this.format(String.valueOf((arg1 == null) ? "null" : arg1));
                this.format(msg.substring(i2 + 2));
            }
            else {
                this.format(msg.substring(i0 + 2));
                if (arg1 != null) {
                    this._buffer.append(' ');
                    this.format(String.valueOf(arg1));
                }
            }
        }
        else {
            this.format(msg);
            if (arg0 != null) {
                this._buffer.append(' ');
                this.format(String.valueOf(arg0));
            }
            if (arg1 != null) {
                this._buffer.append(' ');
                this.format(String.valueOf(arg1));
            }
        }
    }
    
    private void format(final String msg) {
        if (msg == null) {
            this._buffer.append("null");
        }
        else {
            for (int i = 0; i < msg.length(); ++i) {
                final char c = msg.charAt(i);
                if (Character.isISOControl(c)) {
                    if (c == '\n') {
                        this._buffer.append('|');
                    }
                    else if (c == '\r') {
                        this._buffer.append('<');
                    }
                    else {
                        this._buffer.append('?');
                    }
                }
                else {
                    this._buffer.append(c);
                }
            }
        }
    }
    
    private void format(final Throwable th) {
        if (th == null) {
            this._buffer.append("null");
        }
        else {
            this._buffer.append('\n');
            this.format(th.toString());
            final StackTraceElement[] elements = th.getStackTrace();
            for (int i = 0; elements != null && i < elements.length; ++i) {
                this._buffer.append("\n\tat ");
                this.format(elements[i].toString());
            }
        }
    }
    
    public Logger getLogger(final String name) {
        if ((name == null && this._name == null) || (name != null && name.equals(this._name))) {
            return this;
        }
        return new StdErrLog(name);
    }
    
    public String toString() {
        return "STDERR" + this._name;
    }
    
    static {
        StdErrLog.__debug = (System.getProperty("DEBUG", null) != null);
        try {
            StdErrLog._dateCache = new DateCache("yyyy-MM-dd HH:mm:ss");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
