// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.log.Log;
import java.io.InterruptedIOException;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.io.RuntimeIOException;
import java.io.Writer;
import java.util.Formatter;
import java.io.IOException;
import java.util.Locale;
import org.eclipse.jetty.util.log.Logger;
import java.io.PrintWriter;

public class ResponseWriter extends PrintWriter
{
    private static final Logger LOG;
    private static final String __lineSeparator;
    private static final String __trueln;
    private static final String __falseln;
    private final HttpWriter _httpWriter;
    private final Locale _locale;
    private final String _encoding;
    private IOException _ioException;
    private boolean _isClosed;
    private Formatter _formatter;
    
    public ResponseWriter(final HttpWriter httpWriter, final Locale locale, final String encoding) {
        super(httpWriter, false);
        this._isClosed = false;
        this._httpWriter = httpWriter;
        this._locale = locale;
        this._encoding = encoding;
    }
    
    public boolean isFor(final Locale locale, final String encoding) {
        return (this._locale != null || locale == null) && (this._encoding != null || encoding == null) && this._encoding.equalsIgnoreCase(encoding) && this._locale.equals(locale);
    }
    
    protected void reopen() {
        synchronized (this.lock) {
            this._isClosed = false;
            this.clearError();
            this.out = this._httpWriter;
        }
    }
    
    @Override
    protected void clearError() {
        synchronized (this.lock) {
            this._ioException = null;
            super.clearError();
        }
    }
    
    @Override
    public boolean checkError() {
        synchronized (this.lock) {
            return this._ioException != null || super.checkError();
        }
    }
    
    private void setError(final Throwable th) {
        super.setError();
        if (th instanceof IOException) {
            this._ioException = (IOException)th;
        }
        else {
            (this._ioException = new IOException(String.valueOf(th))).initCause(th);
        }
        if (ResponseWriter.LOG.isDebugEnabled()) {
            ResponseWriter.LOG.debug(th);
        }
    }
    
    @Override
    protected void setError() {
        this.setError(new IOException());
    }
    
    private void isOpen() throws IOException {
        if (this._ioException != null) {
            throw new RuntimeIOException(this._ioException);
        }
        if (this._isClosed) {
            throw new EofException("Stream closed");
        }
    }
    
    @Override
    public void flush() {
        try {
            synchronized (this.lock) {
                this.isOpen();
                this.out.flush();
            }
        }
        catch (IOException ex) {
            this.setError(ex);
        }
    }
    
    @Override
    public void close() {
        try {
            synchronized (this.lock) {
                this.out.close();
                this._isClosed = true;
            }
        }
        catch (IOException ex) {
            this.setError(ex);
        }
    }
    
    @Override
    public void write(final int c) {
        try {
            synchronized (this.lock) {
                this.isOpen();
                this.out.write(c);
            }
        }
        catch (InterruptedIOException ex) {
            ResponseWriter.LOG.debug(ex);
            Thread.currentThread().interrupt();
        }
        catch (IOException ex2) {
            this.setError(ex2);
        }
    }
    
    @Override
    public void write(final char[] buf, final int off, final int len) {
        try {
            synchronized (this.lock) {
                this.isOpen();
                this.out.write(buf, off, len);
            }
        }
        catch (InterruptedIOException ex) {
            ResponseWriter.LOG.debug(ex);
            Thread.currentThread().interrupt();
        }
        catch (IOException ex2) {
            this.setError(ex2);
        }
    }
    
    @Override
    public void write(final char[] buf) {
        this.write(buf, 0, buf.length);
    }
    
    @Override
    public void write(final String s, final int off, final int len) {
        try {
            synchronized (this.lock) {
                this.isOpen();
                this.out.write(s, off, len);
            }
        }
        catch (InterruptedIOException ex) {
            ResponseWriter.LOG.debug(ex);
            Thread.currentThread().interrupt();
        }
        catch (IOException ex2) {
            this.setError(ex2);
        }
    }
    
    @Override
    public void write(final String s) {
        this.write(s, 0, s.length());
    }
    
    @Override
    public void print(final boolean b) {
        this.write(b ? "true" : "false");
    }
    
    @Override
    public void print(final char c) {
        this.write(c);
    }
    
    @Override
    public void print(final int i) {
        this.write(String.valueOf(i));
    }
    
    @Override
    public void print(final long l) {
        this.write(String.valueOf(l));
    }
    
    @Override
    public void print(final float f) {
        this.write(String.valueOf(f));
    }
    
    @Override
    public void print(final double d) {
        this.write(String.valueOf(d));
    }
    
    @Override
    public void print(final char[] s) {
        this.write(s);
    }
    
    @Override
    public void print(String s) {
        if (s == null) {
            s = "null";
        }
        this.write(s);
    }
    
    @Override
    public void print(final Object obj) {
        this.write(String.valueOf(obj));
    }
    
    @Override
    public void println() {
        try {
            synchronized (this.lock) {
                this.isOpen();
                this.out.write(ResponseWriter.__lineSeparator);
            }
        }
        catch (InterruptedIOException ex) {
            ResponseWriter.LOG.debug(ex);
            Thread.currentThread().interrupt();
        }
        catch (IOException ex2) {
            this.setError(ex2);
        }
    }
    
    @Override
    public void println(final boolean b) {
        this.println(b ? ResponseWriter.__trueln : ResponseWriter.__falseln);
    }
    
    @Override
    public void println(final char c) {
        try {
            synchronized (this.lock) {
                this.isOpen();
                this.out.write(c);
            }
        }
        catch (InterruptedIOException ex) {
            ResponseWriter.LOG.debug(ex);
            Thread.currentThread().interrupt();
        }
        catch (IOException ex2) {
            this.setError(ex2);
        }
    }
    
    @Override
    public void println(final int x) {
        this.println(String.valueOf(x));
    }
    
    @Override
    public void println(final long x) {
        this.println(String.valueOf(x));
    }
    
    @Override
    public void println(final float x) {
        this.println(String.valueOf(x));
    }
    
    @Override
    public void println(final double x) {
        this.println(String.valueOf(x));
    }
    
    @Override
    public void println(final char[] s) {
        try {
            synchronized (this.lock) {
                this.isOpen();
                this.out.write(s, 0, s.length);
                this.out.write(ResponseWriter.__lineSeparator);
            }
        }
        catch (InterruptedIOException ex) {
            ResponseWriter.LOG.debug(ex);
            Thread.currentThread().interrupt();
        }
        catch (IOException ex2) {
            this.setError(ex2);
        }
    }
    
    @Override
    public void println(String s) {
        if (s == null) {
            s = "null";
        }
        try {
            synchronized (this.lock) {
                this.isOpen();
                this.out.write(s, 0, s.length());
                this.out.write(ResponseWriter.__lineSeparator);
            }
        }
        catch (InterruptedIOException ex) {
            ResponseWriter.LOG.debug(ex);
            Thread.currentThread().interrupt();
        }
        catch (IOException ex2) {
            this.setError(ex2);
        }
    }
    
    @Override
    public void println(final Object x) {
        this.println(String.valueOf(x));
    }
    
    @Override
    public PrintWriter printf(final String format, final Object... args) {
        return this.format(this._locale, format, args);
    }
    
    @Override
    public PrintWriter printf(final Locale l, final String format, final Object... args) {
        return this.format(l, format, args);
    }
    
    @Override
    public PrintWriter format(final String format, final Object... args) {
        return this.format(this._locale, format, args);
    }
    
    @Override
    public PrintWriter format(final Locale l, final String format, final Object... args) {
        try {
            synchronized (this.lock) {
                this.isOpen();
                if (this._formatter == null || this._formatter.locale() != l) {
                    this._formatter = new Formatter(this, l);
                }
                this._formatter.format(l, format, args);
            }
        }
        catch (InterruptedIOException ex) {
            ResponseWriter.LOG.debug(ex);
            Thread.currentThread().interrupt();
        }
        catch (IOException ex2) {
            this.setError(ex2);
        }
        return this;
    }
    
    static {
        LOG = Log.getLogger(ResponseWriter.class);
        __lineSeparator = System.getProperty("line.separator");
        __trueln = "true" + ResponseWriter.__lineSeparator;
        __falseln = "false" + ResponseWriter.__lineSeparator;
    }
}
