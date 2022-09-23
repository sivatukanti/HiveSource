// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io.nio;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.io.FileNotFoundException;
import java.io.File;
import java.nio.channels.FileChannel;
import java.io.RandomAccessFile;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.AbstractBuffer;

public class RandomAccessFileBuffer extends AbstractBuffer implements Buffer
{
    final RandomAccessFile _file;
    final FileChannel _channel;
    final int _capacity;
    
    public RandomAccessFileBuffer(final File file) throws FileNotFoundException {
        super(2, true);
        assert file.length() <= 2147483647L;
        this._file = new RandomAccessFile(file, "rw");
        this._channel = this._file.getChannel();
        this._capacity = Integer.MAX_VALUE;
        this.setGetIndex(0);
        this.setPutIndex((int)file.length());
    }
    
    public RandomAccessFileBuffer(final File file, final int capacity) throws FileNotFoundException {
        super(2, true);
        assert capacity >= file.length();
        assert file.length() <= 2147483647L;
        this._capacity = capacity;
        this._file = new RandomAccessFile(file, "rw");
        this._channel = this._file.getChannel();
        this.setGetIndex(0);
        this.setPutIndex((int)file.length());
    }
    
    public RandomAccessFileBuffer(final File file, final int capacity, final int access) throws FileNotFoundException {
        super(access, true);
        assert capacity >= file.length();
        assert file.length() <= 2147483647L;
        this._capacity = capacity;
        this._file = new RandomAccessFile(file, (access == 2) ? "rw" : "r");
        this._channel = this._file.getChannel();
        this.setGetIndex(0);
        this.setPutIndex((int)file.length());
    }
    
    public byte[] array() {
        return null;
    }
    
    public int capacity() {
        return this._capacity;
    }
    
    @Override
    public void clear() {
        try {
            synchronized (this._file) {
                super.clear();
                this._file.setLength(0L);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public byte peek() {
        synchronized (this._file) {
            try {
                if (this._get != this._file.getFilePointer()) {
                    this._file.seek(this._get);
                }
                return this._file.readByte();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public byte peek(final int index) {
        synchronized (this._file) {
            try {
                this._file.seek(index);
                return this._file.readByte();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public int peek(final int index, final byte[] b, final int offset, final int length) {
        synchronized (this._file) {
            try {
                this._file.seek(index);
                return this._file.read(b, offset, length);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public void poke(final int index, final byte b) {
        synchronized (this._file) {
            try {
                this._file.seek(index);
                this._file.writeByte(b);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    @Override
    public int poke(final int index, final byte[] b, final int offset, final int length) {
        synchronized (this._file) {
            try {
                this._file.seek(index);
                this._file.write(b, offset, length);
                return length;
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public int writeTo(final WritableByteChannel channel, final int index, final int length) throws IOException {
        synchronized (this._file) {
            return (int)this._channel.transferTo(index, length, channel);
        }
    }
}
