// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.io;

import java.io.InterruptedIOException;
import org.mortbay.log.Log;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.io.Writer;
import java.io.PrintWriter;

public class UncheckedPrintWriter extends PrintWriter
{
    private boolean autoFlush;
    private String lineSeparator;
    
    public UncheckedPrintWriter(final Writer out) {
        this(out, false);
    }
    
    public UncheckedPrintWriter(final Writer out, final boolean autoFlush) {
        super(out, autoFlush);
        this.autoFlush = false;
        this.autoFlush = autoFlush;
        this.lineSeparator = System.getProperty("line.separator");
    }
    
    public UncheckedPrintWriter(final OutputStream out) {
        this(out, false);
    }
    
    public UncheckedPrintWriter(final OutputStream out, final boolean autoFlush) {
        this(new BufferedWriter(new OutputStreamWriter(out)), autoFlush);
    }
    
    private void isOpen() throws IOException {
        if (super.out == null) {
            throw new IOException("Stream closed");
        }
    }
    
    public void flush() {
        try {
            synchronized (this.lock) {
                this.isOpen();
                this.out.flush();
            }
        }
        catch (IOException ex) {
            Log.debug(ex);
            this.setError();
            throw new RuntimeIOException(ex);
        }
    }
    
    public void close() {
        try {
            synchronized (this.lock) {
                this.out.close();
            }
        }
        catch (IOException ex) {
            Log.debug(ex);
            this.setError();
            throw new RuntimeIOException(ex);
        }
    }
    
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
            Log.debug(ex);
            this.setError();
            throw new RuntimeIOException(ex);
        }
    }
    
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
            Log.debug(ex);
            this.setError();
            throw new RuntimeIOException(ex);
        }
    }
    
    public void write(final char[] buf) {
        this.write(buf, 0, buf.length);
    }
    
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
            Log.debug(ex);
            this.setError();
            throw new RuntimeIOException(ex);
        }
    }
    
    public void write(final String s) {
        this.write(s, 0, s.length());
    }
    
    private void newLine() {
        try {
            synchronized (this.lock) {
                this.isOpen();
                this.out.write(this.lineSeparator);
                if (this.autoFlush) {
                    this.out.flush();
                }
            }
        }
        catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        }
        catch (IOException ex) {
            Log.debug(ex);
            this.setError();
            throw new RuntimeIOException(ex);
        }
    }
    
    public void print(final boolean b) {
        this.write(b ? "true" : "false");
    }
    
    public void print(final char c) {
        this.write(c);
    }
    
    public void print(final int i) {
        this.write(String.valueOf(i));
    }
    
    public void print(final long l) {
        this.write(String.valueOf(l));
    }
    
    public void print(final float f) {
        this.write(String.valueOf(f));
    }
    
    public void print(final double d) {
        this.write(String.valueOf(d));
    }
    
    public void print(final char[] s) {
        this.write(s);
    }
    
    public void print(String s) {
        if (s == null) {
            s = "null";
        }
        this.write(s);
    }
    
    public void print(final Object obj) {
        this.write(String.valueOf(obj));
    }
    
    public void println() {
        this.newLine();
    }
    
    public void println(final boolean x) {
        synchronized (this.lock) {
            this.print(x);
            this.println();
        }
    }
    
    public void println(final char x) {
        synchronized (this.lock) {
            this.print(x);
            this.println();
        }
    }
    
    public void println(final int x) {
        synchronized (this.lock) {
            this.print(x);
            this.println();
        }
    }
    
    public void println(final long x) {
        synchronized (this.lock) {
            this.print(x);
            this.println();
        }
    }
    
    public void println(final float x) {
        synchronized (this.lock) {
            this.print(x);
            this.println();
        }
    }
    
    public void println(final double x) {
        synchronized (this.lock) {
            this.print(x);
            this.println();
        }
    }
    
    public void println(final char[] x) {
        synchronized (this.lock) {
            this.print(x);
            this.println();
        }
    }
    
    public void println(final String x) {
        synchronized (this.lock) {
            this.print(x);
            this.println();
        }
    }
    
    public void println(final Object x) {
        synchronized (this.lock) {
            this.print(x);
            this.println();
        }
    }
}
