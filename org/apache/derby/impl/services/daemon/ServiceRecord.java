// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.daemon;

import org.apache.derby.iapi.services.daemon.Serviceable;

class ServiceRecord
{
    final Serviceable client;
    private final boolean onDemandOnly;
    final boolean subscriber;
    private boolean serviceRequest;
    
    ServiceRecord(final Serviceable client, final boolean onDemandOnly, final boolean subscriber) {
        this.client = client;
        this.onDemandOnly = onDemandOnly;
        this.subscriber = subscriber;
    }
    
    final void serviced() {
        this.serviceRequest = false;
    }
    
    final boolean needImmediateService() {
        return this.serviceRequest;
    }
    
    final boolean needService() {
        return this.serviceRequest || !this.onDemandOnly;
    }
    
    final void called() {
        this.serviceRequest = true;
    }
}
