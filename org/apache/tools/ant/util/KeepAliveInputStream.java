// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class KeepAliveInputStream extends FilterInputStream
{
    public KeepAliveInputStream(final InputStream in) {
        super(in);
    }
    
    @Override
    public void close() throws IOException {
    }
    
    public static InputStream wrapSystemIn() {
        return new KeepAliveInputStream(System.in);
    }
}
