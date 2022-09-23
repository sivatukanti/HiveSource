// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha;

import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class HAServiceStatus
{
    private HAServiceProtocol.HAServiceState state;
    private boolean readyToBecomeActive;
    private String notReadyReason;
    
    public HAServiceStatus(final HAServiceProtocol.HAServiceState state) {
        this.state = state;
    }
    
    public HAServiceProtocol.HAServiceState getState() {
        return this.state;
    }
    
    public HAServiceStatus setReadyToBecomeActive() {
        this.readyToBecomeActive = true;
        this.notReadyReason = null;
        return this;
    }
    
    public HAServiceStatus setNotReadyToBecomeActive(final String reason) {
        this.readyToBecomeActive = false;
        this.notReadyReason = reason;
        return this;
    }
    
    public boolean isReadyToBecomeActive() {
        return this.readyToBecomeActive;
    }
    
    public String getNotReadyReason() {
        return this.notReadyReason;
    }
}
