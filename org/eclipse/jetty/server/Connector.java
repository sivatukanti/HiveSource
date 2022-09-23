// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import java.util.List;
import java.util.Collection;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.util.thread.Scheduler;
import java.util.concurrent.Executor;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.Graceful;
import org.eclipse.jetty.util.component.LifeCycle;

@ManagedObject("Connector Interface")
public interface Connector extends LifeCycle, Graceful
{
    Server getServer();
    
    Executor getExecutor();
    
    Scheduler getScheduler();
    
    ByteBufferPool getByteBufferPool();
    
    ConnectionFactory getConnectionFactory(final String p0);
    
     <T> T getConnectionFactory(final Class<T> p0);
    
    ConnectionFactory getDefaultConnectionFactory();
    
    Collection<ConnectionFactory> getConnectionFactories();
    
    List<String> getProtocols();
    
    @ManagedAttribute("maximum time a connection can be idle before being closed (in ms)")
    long getIdleTimeout();
    
    Object getTransport();
    
    Collection<EndPoint> getConnectedEndPoints();
    
    String getName();
}
