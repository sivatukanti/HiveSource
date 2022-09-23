// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.curator.framework.api.Backgroundable;
import org.apache.curator.utils.ThreadUtils;
import org.apache.curator.framework.api.ErrorListenerPathable;
import org.slf4j.LoggerFactory;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;

class FailedDeleteManager
{
    private final Logger log;
    private final CuratorFramework client;
    volatile FailedDeleteManagerListener debugListener;
    
    FailedDeleteManager(final CuratorFramework client) {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.debugListener = null;
        this.client = client;
    }
    
    void addFailedDelete(final String path) {
        if (this.debugListener != null) {
            this.debugListener.pathAddedForDelete(path);
        }
        if (this.client.getState() == CuratorFrameworkState.STARTED) {
            this.log.debug("Path being added to guaranteed delete set: " + path);
            try {
                ((Backgroundable<ErrorListenerPathable>)this.client.delete().guaranteed()).inBackground().forPath(path);
            }
            catch (Exception e) {
                ThreadUtils.checkInterrupted(e);
                this.addFailedDelete(path);
            }
        }
    }
    
    interface FailedDeleteManagerListener
    {
        void pathAddedForDelete(final String p0);
    }
}
