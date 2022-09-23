// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import org.mortbay.util.ajax.Continuation;
import org.mortbay.io.EndPoint;
import java.io.IOException;
import org.mortbay.io.Buffers;
import org.mortbay.component.LifeCycle;

public interface Connector extends LifeCycle, Buffers
{
    String getName();
    
    void open() throws IOException;
    
    void close() throws IOException;
    
    void setServer(final Server p0);
    
    Server getServer();
    
    int getHeaderBufferSize();
    
    void setHeaderBufferSize(final int p0);
    
    int getRequestBufferSize();
    
    void setRequestBufferSize(final int p0);
    
    int getResponseBufferSize();
    
    void setResponseBufferSize(final int p0);
    
    int getIntegralPort();
    
    String getIntegralScheme();
    
    boolean isIntegral(final Request p0);
    
    int getConfidentialPort();
    
    String getConfidentialScheme();
    
    boolean isConfidential(final Request p0);
    
    void customize(final EndPoint p0, final Request p1) throws IOException;
    
    void persist(final EndPoint p0) throws IOException;
    
    Continuation newContinuation();
    
    String getHost();
    
    void setHost(final String p0);
    
    void setPort(final int p0);
    
    int getPort();
    
    int getLocalPort();
    
    int getMaxIdleTime();
    
    void setMaxIdleTime(final int p0);
    
    int getLowResourceMaxIdleTime();
    
    void setLowResourceMaxIdleTime(final int p0);
    
    Object getConnection();
    
    boolean getResolveNames();
    
    int getRequests();
    
    long getConnectionsDurationMin();
    
    long getConnectionsDurationTotal();
    
    int getConnectionsOpenMin();
    
    int getConnectionsRequestsMin();
    
    int getConnections();
    
    int getConnectionsOpen();
    
    int getConnectionsOpenMax();
    
    long getConnectionsDurationAve();
    
    long getConnectionsDurationMax();
    
    int getConnectionsRequestsAve();
    
    int getConnectionsRequestsMax();
    
    void statsReset();
    
    void setStatsOn(final boolean p0);
    
    boolean getStatsOn();
    
    long getStatsOnMs();
}
