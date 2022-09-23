// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

public interface RequestProcessor
{
    void processRequest(final Request p0) throws RequestProcessorException;
    
    void shutdown();
    
    public static class RequestProcessorException extends Exception
    {
        public RequestProcessorException(final String msg, final Throwable t) {
            super(msg, t);
        }
    }
}
