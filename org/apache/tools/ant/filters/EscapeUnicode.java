// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.filters;

import java.io.IOException;
import org.apache.tools.ant.util.UnicodeUtil;
import java.io.Reader;

public class EscapeUnicode extends BaseParamFilterReader implements ChainableReader
{
    private StringBuffer unicodeBuf;
    
    public EscapeUnicode() {
        this.unicodeBuf = new StringBuffer();
    }
    
    public EscapeUnicode(final Reader in) {
        super(in);
        this.unicodeBuf = new StringBuffer();
    }
    
    @Override
    public final int read() throws IOException {
        if (!this.getInitialized()) {
            this.initialize();
            this.setInitialized(true);
        }
        int ch = -1;
        if (this.unicodeBuf.length() == 0) {
            ch = this.in.read();
            if (ch != -1) {
                final char achar = (char)ch;
                if (achar >= '\u0080') {
                    this.unicodeBuf = UnicodeUtil.EscapeUnicode(achar);
                    ch = 92;
                }
            }
        }
        else {
            ch = this.unicodeBuf.charAt(0);
            this.unicodeBuf.deleteCharAt(0);
        }
        return ch;
    }
    
    public final Reader chain(final Reader rdr) {
        final EscapeUnicode newFilter = new EscapeUnicode(rdr);
        newFilter.setInitialized(true);
        return newFilter;
    }
    
    private void initialize() {
    }
}
