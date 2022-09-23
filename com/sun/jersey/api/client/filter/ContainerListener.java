// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client.filter;

public abstract class ContainerListener
{
    public void onSent(final long delta, final long bytes) {
    }
    
    public void onReceiveStart(final long totalBytes) {
    }
    
    public void onReceived(final long delta, final long bytes) {
    }
    
    public void onFinish() {
    }
}
