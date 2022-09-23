// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz;

interface FilterEncoder extends FilterCoder
{
    long getFilterID();
    
    byte[] getFilterProps();
    
    boolean supportsFlushing();
    
    FinishableOutputStream getOutputStream(final FinishableOutputStream p0);
}
