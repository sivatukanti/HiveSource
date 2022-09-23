// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.mbox;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

class LineCounter extends FilterOutputStream
{
    private int lastb;
    protected int lineCount;
    
    public LineCounter(final OutputStream os) {
        super(os);
        this.lastb = -1;
    }
    
    public void write(final int b) throws IOException {
        if (b == 13 || (b == 10 && this.lastb != 13)) {
            ++this.lineCount;
        }
        this.out.write(b);
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
    
    public int getLineCount() {
        return this.lineCount;
    }
    
    public static void main(final String[] argv) throws Exception {
        final LineCounter os = new LineCounter(System.out);
        int b;
        while ((b = System.in.read()) >= 0) {
            os.write(b);
        }
        os.flush();
        System.out.println(os.getLineCount());
    }
}
