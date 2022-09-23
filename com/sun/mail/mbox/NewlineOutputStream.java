// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.mbox;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class NewlineOutputStream extends FilterOutputStream
{
    private int lastb;
    private static final byte[] newline;
    
    public NewlineOutputStream(final OutputStream os) {
        super(os);
        this.lastb = -1;
    }
    
    public void write(final int b) throws IOException {
        if (b == 13) {
            this.out.write(NewlineOutputStream.newline);
        }
        else if (b == 10) {
            if (this.lastb != 13) {
                this.out.write(NewlineOutputStream.newline);
            }
        }
        else {
            this.out.write(b);
        }
        this.lastb = b;
    }
    
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    public void write(final byte[] b, final int off, final int len) throws IOException {
        for (int i = 0; i < len; ++i) {
            this.write(b[off + i]);
        }
    }
    
    static {
        String s = null;
        try {
            s = System.getProperty("line.separator");
        }
        catch (SecurityException ex) {}
        if (s == null || s.length() <= 0) {
            s = "\n";
        }
        newline = new byte[s.length()];
        s.getBytes(0, s.length(), NewlineOutputStream.newline, 0);
    }
}
