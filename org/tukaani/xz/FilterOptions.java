// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

import java.io.IOException;
import java.io.InputStream;

public abstract class FilterOptions implements Cloneable
{
    public static int getEncoderMemoryUsage(final FilterOptions[] array) {
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            n += array[i].getEncoderMemoryUsage();
        }
        return n;
    }
    
    public static int getDecoderMemoryUsage(final FilterOptions[] array) {
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            n += array[i].getDecoderMemoryUsage();
        }
        return n;
    }
    
    public abstract int getEncoderMemoryUsage();
    
    public abstract FinishableOutputStream getOutputStream(final FinishableOutputStream p0);
    
    public abstract int getDecoderMemoryUsage();
    
    public abstract InputStream getInputStream(final InputStream p0) throws IOException;
    
    abstract FilterEncoder getFilterEncoder();
    
    FilterOptions() {
    }
}
