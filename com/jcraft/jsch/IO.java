// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.net.SocketException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public class IO
{
    InputStream in;
    OutputStream out;
    OutputStream out_ext;
    private boolean in_dontclose;
    private boolean out_dontclose;
    private boolean out_ext_dontclose;
    
    public IO() {
        this.in_dontclose = false;
        this.out_dontclose = false;
        this.out_ext_dontclose = false;
    }
    
    void setOutputStream(final OutputStream out) {
        this.out = out;
    }
    
    void setOutputStream(final OutputStream out, final boolean dontclose) {
        this.out_dontclose = dontclose;
        this.setOutputStream(out);
    }
    
    void setExtOutputStream(final OutputStream out) {
        this.out_ext = out;
    }
    
    void setExtOutputStream(final OutputStream out, final boolean dontclose) {
        this.out_ext_dontclose = dontclose;
        this.setExtOutputStream(out);
    }
    
    void setInputStream(final InputStream in) {
        this.in = in;
    }
    
    void setInputStream(final InputStream in, final boolean dontclose) {
        this.in_dontclose = dontclose;
        this.setInputStream(in);
    }
    
    public void put(final Packet p) throws IOException, SocketException {
        this.out.write(p.buffer.buffer, 0, p.buffer.index);
        this.out.flush();
    }
    
    void put(final byte[] array, final int begin, final int length) throws IOException {
        this.out.write(array, begin, length);
        this.out.flush();
    }
    
    void put_ext(final byte[] array, final int begin, final int length) throws IOException {
        this.out_ext.write(array, begin, length);
        this.out_ext.flush();
    }
    
    int getByte() throws IOException {
        return this.in.read();
    }
    
    void getByte(final byte[] array) throws IOException {
        this.getByte(array, 0, array.length);
    }
    
    void getByte(final byte[] array, int begin, int length) throws IOException {
        do {
            final int completed = this.in.read(array, begin, length);
            if (completed < 0) {
                throw new IOException("End of IO Stream Read");
            }
            begin += completed;
            length -= completed;
        } while (length > 0);
    }
    
    void out_close() {
        try {
            if (this.out != null && !this.out_dontclose) {
                this.out.close();
            }
            this.out = null;
        }
        catch (Exception ex) {}
    }
    
    public void close() {
        try {
            if (this.in != null && !this.in_dontclose) {
                this.in.close();
            }
            this.in = null;
        }
        catch (Exception ex) {}
        this.out_close();
        try {
            if (this.out_ext != null && !this.out_ext_dontclose) {
                this.out_ext.close();
            }
            this.out_ext = null;
        }
        catch (Exception ex2) {}
    }
}
