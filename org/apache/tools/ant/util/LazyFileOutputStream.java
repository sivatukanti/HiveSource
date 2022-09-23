// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class LazyFileOutputStream extends OutputStream
{
    private FileOutputStream fos;
    private File file;
    private boolean append;
    private boolean alwaysCreate;
    private boolean opened;
    private boolean closed;
    
    public LazyFileOutputStream(final String name) {
        this(name, false);
    }
    
    public LazyFileOutputStream(final String name, final boolean append) {
        this(new File(name), append);
    }
    
    public LazyFileOutputStream(final File f) {
        this(f, false);
    }
    
    public LazyFileOutputStream(final File file, final boolean append) {
        this(file, append, false);
    }
    
    public LazyFileOutputStream(final File file, final boolean append, final boolean alwaysCreate) {
        this.opened = false;
        this.closed = false;
        this.file = file;
        this.append = append;
        this.alwaysCreate = alwaysCreate;
    }
    
    public void open() throws IOException {
        this.ensureOpened();
    }
    
    @Override
    public synchronized void close() throws IOException {
        if (this.alwaysCreate && !this.closed) {
            this.ensureOpened();
        }
        if (this.opened) {
            this.fos.close();
        }
        this.closed = true;
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    @Override
    public synchronized void write(final byte[] b, final int offset, final int len) throws IOException {
        this.ensureOpened();
        this.fos.write(b, offset, len);
    }
    
    @Override
    public synchronized void write(final int b) throws IOException {
        this.ensureOpened();
        this.fos.write(b);
    }
    
    private synchronized void ensureOpened() throws IOException {
        if (this.closed) {
            throw new IOException(this.file + " has already been closed.");
        }
        if (!this.opened) {
            this.fos = new FileOutputStream(this.file.getAbsolutePath(), this.append);
            this.opened = true;
        }
    }
}
