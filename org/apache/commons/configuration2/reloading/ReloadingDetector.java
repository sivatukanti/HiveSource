// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.reloading;

public interface ReloadingDetector
{
    boolean isReloadingRequired();
    
    void reloadingPerformed();
}
