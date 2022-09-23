// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.handler;

public abstract class AbstractStatisticsHandler extends HandlerWrapper
{
    protected void doStart() throws Exception {
        super.doStart();
        this.statsReset();
    }
    
    public abstract void statsReset();
    
    public abstract int getRequests();
    
    public abstract int getRequestsActive();
    
    public abstract int getRequestsActiveMax();
    
    public abstract int getResponses1xx();
    
    public abstract int getResponses2xx();
    
    public abstract int getResponses3xx();
    
    public abstract int getResponses4xx();
    
    public abstract int getResponses5xx();
    
    public abstract long getStatsOnMs();
    
    public abstract long getRequestTimeMin();
    
    public abstract long getRequestTimeMax();
    
    public abstract long getRequestTimeTotal();
    
    public abstract long getRequestTimeAverage();
}
