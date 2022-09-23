// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.reloading;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class ManagedReloadingDetector implements ReloadingDetector, ManagedReloadingDetectorMBean
{
    private final Log log;
    private volatile boolean reloadingRequired;
    
    public ManagedReloadingDetector() {
        this.log = LogFactory.getLog(ManagedReloadingDetector.class);
    }
    
    @Override
    public void reloadingPerformed() {
        this.reloadingRequired = false;
    }
    
    @Override
    public boolean isReloadingRequired() {
        return this.reloadingRequired;
    }
    
    @Override
    public void refresh() {
        this.log.info("Reloading configuration.");
        this.reloadingRequired = true;
    }
}
