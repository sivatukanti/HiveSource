// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.io;

public interface FileHandlerListener
{
    void loading(final FileHandler p0);
    
    void loaded(final FileHandler p0);
    
    void saving(final FileHandler p0);
    
    void saved(final FileHandler p0);
    
    void locationChanged(final FileHandler p0);
}
