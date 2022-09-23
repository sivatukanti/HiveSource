// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.mbox;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

class ContentLengthUpdater extends FilterOutputStream
{
    private String contentLength;
    private boolean inHeader;
    private boolean sawContentLength;
    private int lastb1;
    private int lastb2;
    private StringBuffer line;
    
    public ContentLengthUpdater(final OutputStream os, final long contentLength) {
        super(os);
        this.inHeader = true;
        this.sawContentLength = false;
        this.lastb1 = -1;
        this.lastb2 = -1;
        this.line = new StringBuffer();
        this.contentLength = "Content-Length: " + contentLength;
    }
    
    public void write(final int b) throws IOException {
        if (this.inHeader) {
            String eol = "\n";
            if (b == 13) {
                if (this.lastb1 == 13) {
                    this.inHeader = false;
                    eol = "\r";
                }
                else if (this.lastb1 == 10 && this.lastb2 == 13) {
                    this.inHeader = false;
                    eol = "\r\n";
                }
            }
            else if (b == 10 && this.lastb1 == 10) {
                this.inHeader = false;
                eol = "\n";
            }
            if (!this.inHeader && !this.sawContentLength) {
                this.out.write(this.contentLength.getBytes("iso-8859-1"));
                this.out.write(eol.getBytes("iso-8859-1"));
            }
            if (b == 13 || (b == 10 && this.lastb1 != 13)) {
                if (this.line.toString().regionMatches(true, 0, "content-length:", 0, 15)) {
                    this.sawContentLength = true;
                    this.out.write(this.contentLength.getBytes("iso-8859-1"));
                }
                else {
                    this.out.write(this.line.toString().getBytes("iso-8859-1"));
                }
                this.line.setLength(0);
            }
            if (b == 13 || b == 10) {
                this.out.write(b);
            }
            else {
                this.line.append((char)b);
            }
            this.lastb2 = this.lastb1;
            this.lastb1 = b;
        }
        else {
            this.out.write(b);
        }
    }
    
    public void write(final byte[] b) throws IOException {
        if (this.inHeader) {
            this.write(b, 0, b.length);
        }
        else {
            this.out.write(b);
        }
    }
    
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (this.inHeader) {
            for (int i = 0; i < len; ++i) {
                this.write(b[off + i]);
            }
        }
        else {
            this.out.write(b, off, len);
        }
    }
    
    public static void main(final String[] argv) throws Exception {
        final ContentLengthUpdater os = new ContentLengthUpdater(System.out, Long.parseLong(argv[0]));
        int b;
        while ((b = System.in.read()) >= 0) {
            os.write(b);
        }
        os.flush();
    }
}
