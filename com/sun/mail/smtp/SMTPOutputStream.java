// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.smtp;

import java.io.IOException;
import java.io.OutputStream;
import com.sun.mail.util.CRLFOutputStream;

public class SMTPOutputStream extends CRLFOutputStream
{
    public SMTPOutputStream(final OutputStream os) {
        super(os);
    }
    
    public void write(final int b) throws IOException {
        if ((this.lastb == 10 || this.lastb == 13 || this.lastb == -1) && b == 46) {
            this.out.write(46);
        }
        super.write(b);
    }
    
    public void write(final byte[] b, final int off, int len) throws IOException {
        int lastc = (this.lastb == -1) ? 10 : this.lastb;
        int start = off;
        len += off;
        for (int i = off; i < len; ++i) {
            if ((lastc == 10 || lastc == 13) && b[i] == 46) {
                super.write(b, start, i - start);
                this.out.write(46);
                start = i;
            }
            lastc = b[i];
        }
        if (len - start > 0) {
            super.write(b, start, len - start);
        }
    }
    
    public void flush() {
    }
    
    public void ensureAtBOL() throws IOException {
        if (!this.atBOL) {
            super.writeln();
        }
    }
}
