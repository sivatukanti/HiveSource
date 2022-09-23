// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.mbox;

import java.io.IOException;
import java.io.OutputStream;

class ContentLengthCounter extends OutputStream
{
    private long size;
    private boolean inHeader;
    private int lastb1;
    private int lastb2;
    
    ContentLengthCounter() {
        this.size = 0L;
        this.inHeader = true;
        this.lastb1 = -1;
        this.lastb2 = -1;
    }
    
    public void write(final int b) throws IOException {
        if (this.inHeader) {
            if (b == 13 && this.lastb1 == 13) {
                this.inHeader = false;
            }
            else if (b == 10) {
                if (this.lastb1 == 10) {
                    this.inHeader = false;
                }
                else if (this.lastb1 == 13 && this.lastb2 == 10) {
                    this.inHeader = false;
                }
            }
            this.lastb2 = this.lastb1;
            this.lastb1 = b;
        }
        else {
            ++this.size;
        }
    }
    
    public void write(final byte[] b) throws IOException {
        if (this.inHeader) {
            super.write(b);
        }
        else {
            this.size += b.length;
        }
    }
    
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (this.inHeader) {
            super.write(b, off, len);
        }
        else {
            this.size += len;
        }
    }
    
    public long getSize() {
        return this.size;
    }
    
    public static void main(final String[] argv) throws Exception {
        final ContentLengthCounter os = new ContentLengthCounter();
        int b;
        while ((b = System.in.read()) >= 0) {
            os.write(b);
        }
        System.out.println("size " + os.getSize());
    }
}
