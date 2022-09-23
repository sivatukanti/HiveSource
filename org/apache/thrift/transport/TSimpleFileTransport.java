// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.transport;

import java.io.IOException;
import java.io.RandomAccessFile;

public final class TSimpleFileTransport extends TTransport
{
    private RandomAccessFile file;
    private boolean readable;
    private boolean writable;
    private String path_;
    
    public TSimpleFileTransport(final String path, final boolean read, final boolean write, final boolean openFile) throws TTransportException {
        this.file = null;
        if (path.length() <= 0) {
            throw new TTransportException("No path specified");
        }
        if (!read && !write) {
            throw new TTransportException("Neither READ nor WRITE specified");
        }
        this.readable = read;
        this.writable = write;
        this.path_ = path;
        if (openFile) {
            this.open();
        }
    }
    
    public TSimpleFileTransport(final String path, final boolean read, final boolean write) throws TTransportException {
        this(path, read, write, true);
    }
    
    public TSimpleFileTransport(final String path) throws TTransportException {
        this(path, true, false, true);
    }
    
    @Override
    public boolean isOpen() {
        return this.file != null;
    }
    
    @Override
    public void open() throws TTransportException {
        if (this.file == null) {
            try {
                String access = "r";
                if (this.writable) {
                    access += "w";
                }
                this.file = new RandomAccessFile(this.path_, access);
            }
            catch (IOException ioe) {
                this.file = null;
                throw new TTransportException(ioe.getMessage());
            }
        }
    }
    
    @Override
    public void close() {
        if (this.file != null) {
            try {
                this.file.close();
            }
            catch (Exception ex) {}
            this.file = null;
        }
    }
    
    @Override
    public int read(final byte[] buf, final int off, final int len) throws TTransportException {
        if (!this.readable) {
            throw new TTransportException("Read operation on write only file");
        }
        int iBytesRead = 0;
        try {
            iBytesRead = this.file.read(buf, off, len);
        }
        catch (IOException ioe) {
            this.file = null;
            throw new TTransportException(ioe.getMessage());
        }
        return iBytesRead;
    }
    
    @Override
    public void write(final byte[] buf, final int off, final int len) throws TTransportException {
        try {
            this.file.write(buf, off, len);
        }
        catch (IOException ioe) {
            this.file = null;
            throw new TTransportException(ioe.getMessage());
        }
    }
    
    public void seek(final long offset) throws TTransportException {
        try {
            this.file.seek(offset);
        }
        catch (IOException ex) {
            throw new TTransportException(ex.getMessage());
        }
    }
    
    public long length() throws TTransportException {
        try {
            return this.file.length();
        }
        catch (IOException ex) {
            throw new TTransportException(ex.getMessage());
        }
    }
    
    public long getFilePointer() throws TTransportException {
        try {
            return this.file.getFilePointer();
        }
        catch (IOException ex) {
            throw new TTransportException(ex.getMessage());
        }
    }
}
