// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.io.PrintStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class KeepAliveOutputStream extends FilterOutputStream
{
    public KeepAliveOutputStream(final OutputStream out) {
        super(out);
    }
    
    @Override
    public void close() throws IOException {
    }
    
    public static PrintStream wrapSystemOut() {
        return wrap(System.out);
    }
    
    public static PrintStream wrapSystemErr() {
        return wrap(System.err);
    }
    
    private static PrintStream wrap(final PrintStream ps) {
        return new PrintStream(new KeepAliveOutputStream(ps));
    }
}
