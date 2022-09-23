// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket;

import org.jboss.netty.channel.ReceiveBufferSizePredictorFactory;
import org.jboss.netty.channel.ReceiveBufferSizePredictor;
import java.net.NetworkInterface;
import java.net.InetAddress;
import org.jboss.netty.channel.ChannelConfig;

public interface DatagramChannelConfig extends ChannelConfig
{
    int getSendBufferSize();
    
    void setSendBufferSize(final int p0);
    
    int getReceiveBufferSize();
    
    void setReceiveBufferSize(final int p0);
    
    int getTrafficClass();
    
    void setTrafficClass(final int p0);
    
    boolean isReuseAddress();
    
    void setReuseAddress(final boolean p0);
    
    boolean isBroadcast();
    
    void setBroadcast(final boolean p0);
    
    boolean isLoopbackModeDisabled();
    
    void setLoopbackModeDisabled(final boolean p0);
    
    int getTimeToLive();
    
    void setTimeToLive(final int p0);
    
    InetAddress getInterface();
    
    void setInterface(final InetAddress p0);
    
    NetworkInterface getNetworkInterface();
    
    void setNetworkInterface(final NetworkInterface p0);
    
    ReceiveBufferSizePredictor getReceiveBufferSizePredictor();
    
    void setReceiveBufferSizePredictor(final ReceiveBufferSizePredictor p0);
    
    ReceiveBufferSizePredictorFactory getReceiveBufferSizePredictorFactory();
    
    void setReceiveBufferSizePredictorFactory(final ReceiveBufferSizePredictorFactory p0);
}
