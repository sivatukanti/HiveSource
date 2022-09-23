// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.mail;

import java.io.OutputStream;
import java.io.PrintStream;

class MailPrintStream extends PrintStream
{
    private int lastChar;
    
    public MailPrintStream(final OutputStream out) {
        super(out, true);
    }
    
    @Override
    public void write(final int b) {
        if (b == 10 && this.lastChar != 13) {
            this.rawWrite(13);
            this.rawWrite(b);
        }
        else if (b == 46 && this.lastChar == 10) {
            this.rawWrite(46);
            this.rawWrite(b);
        }
        else {
            this.rawWrite(b);
        }
        this.lastChar = b;
    }
    
    @Override
    public void write(final byte[] buf, final int off, final int len) {
        for (int i = 0; i < len; ++i) {
            this.write(buf[off + i]);
        }
    }
    
    void rawWrite(final int b) {
        super.write(b);
    }
    
    void rawPrint(final String s) {
        for (int len = s.length(), i = 0; i < len; ++i) {
            this.rawWrite(s.charAt(i));
        }
    }
}
