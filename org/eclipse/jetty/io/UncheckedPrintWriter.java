// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import org.eclipse.jetty.util.log.Log;
import java.io.InterruptedIOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.Writer;
import java.io.IOException;
import org.eclipse.jetty.util.log.Logger;
import java.io.PrintWriter;

public class UncheckedPrintWriter extends PrintWriter
{
    private static final Logger LOG;
    private boolean _autoFlush;
    private IOException _ioException;
    private boolean _isClosed;
    private String _lineSeparator;
    
    public UncheckedPrintWriter(final Writer out) {
        this(out, false);
    }
    
    public UncheckedPrintWriter(final Writer out, final boolean autoFlush) {
        super(out, autoFlush);
        this._autoFlush = false;
        this._isClosed = false;
        this._autoFlush = autoFlush;
        this._lineSeparator = System.getProperty("line.separator");
    }
    
    public UncheckedPrintWriter(final OutputStream out) {
        this(out, false);
    }
    
    public UncheckedPrintWriter(final OutputStream out, final boolean autoFlush) {
        this(new BufferedWriter(new OutputStreamWriter(out)), autoFlush);
    }
    
    @Override
    public boolean checkError() {
        return this._ioException != null || super.checkError();
    }
    
    private void setError(final Throwable th) {
        super.setError();
        if (th instanceof IOException) {
            this._ioException = (IOException)th;
        }
        else {
            (this._ioException = new IOException(String.valueOf(th))).initCause(th);
        }
        UncheckedPrintWriter.LOG.debug(th);
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
            throw new IOException("Stream closed");
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
        catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        }
        catch (IOException ex) {
            this.setError(ex);
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
        catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        }
        catch (IOException ex) {
            this.setError(ex);
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
        catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        }
        catch (IOException ex) {
            this.setError(ex);
        }
    }
    
    @Override
    public void write(final String s) {
        this.write(s, 0, s.length());
    }
    
    private void newLine() {
        try {
            synchronized (this.lock) {
                this.isOpen();
                this.out.write(this._lineSeparator);
                if (this._autoFlush) {
                    this.out.flush();
                }
            }
        }
        catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        }
        catch (IOException ex) {
            this.setError(ex);
        }
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
        this.newLine();
    }
    
    @Override
    public void println(final boolean x) {
        synchronized (this.lock) {
            this.print(x);
            this.println();
        }
    }
    
    @Override
    public void println(final char x) {
        synchronized (this.lock) {
            this.print(x);
            this.println();
        }
    }
    
    @Override
    public void println(final int x) {
        synchronized (this.lock) {
            this.print(x);
            this.println();
        }
    }
    
    @Override
    public void println(final long x) {
        synchronized (this.lock) {
            this.print(x);
            this.println();
        }
    }
    
    @Override
    public void println(final float x) {
        synchronized (this.lock) {
            this.print(x);
            this.println();
        }
    }
    
    @Override
    public void println(final double x) {
        synchronized (this.lock) {
            this.print(x);
            this.println();
        }
    }
    
    @Override
    public void println(final char[] x) {
        synchronized (this.lock) {
            this.print(x);
            this.println();
        }
    }
    
    @Override
    public void println(final String x) {
        synchronized (this.lock) {
            this.print(x);
            this.println();
        }
    }
    
    @Override
    public void println(final Object x) {
        synchronized (this.lock) {
            this.print(x);
            this.println();
        }
    }
    
    static {
        LOG = Log.getLogger(UncheckedPrintWriter.class);
    }
}
