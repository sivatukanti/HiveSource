// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.util;

import java.io.IOException;
import java.io.InputStream;

public class Closing
{
    private final InputStream in;
    
    public static Closing with(final InputStream in) {
        return new Closing(in);
    }
    
    public Closing(final InputStream in) {
        this.in = in;
    }
    
    public void f(final Closure c) throws IOException {
        if (this.in == null) {
            return;
        }
        try {
            c.f(this.in);
        }
        finally {
            try {
                this.in.close();
            }
            catch (IOException ex) {
                throw ex;
            }
        }
    }
    
    public interface Closure
    {
        void f(final InputStream p0) throws IOException;
    }
}
