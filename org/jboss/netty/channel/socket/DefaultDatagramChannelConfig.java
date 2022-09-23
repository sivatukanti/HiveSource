// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket;

import org.jboss.netty.channel.FixedReceiveBufferSizePredictorFactory;
import java.io.IOException;
import java.net.MulticastSocket;
import java.net.SocketException;
import org.jboss.netty.channel.ChannelException;
import java.net.NetworkInterface;
import java.net.InetAddress;
import org.jboss.netty.util.internal.ConversionUtil;
import org.jboss.netty.channel.ReceiveBufferSizePredictor;
import java.net.DatagramSocket;
import org.jboss.netty.channel.ReceiveBufferSizePredictorFactory;
import org.jboss.netty.channel.DefaultChannelConfig;

public class DefaultDatagramChannelConfig extends DefaultChannelConfig implements DatagramChannelConfig
{
    private static final ReceiveBufferSizePredictorFactory DEFAULT_PREDICTOR_FACTORY;
    private final DatagramSocket socket;
    private volatile ReceiveBufferSizePredictor predictor;
    private volatile ReceiveBufferSizePredictorFactory predictorFactory;
    
    public DefaultDatagramChannelConfig(final DatagramSocket socket) {
        this.predictorFactory = DefaultDatagramChannelConfig.DEFAULT_PREDICTOR_FACTORY;
        if (socket == null) {
            throw new NullPointerException("socket");
        }
        this.socket = socket;
    }
    
    @Override
    public boolean setOption(final String key, final Object value) {
        if (super.setOption(key, value)) {
            return true;
        }
        if ("broadcast".equals(key)) {
            this.setBroadcast(ConversionUtil.toBoolean(value));
        }
        else if ("receiveBufferSize".equals(key)) {
            this.setReceiveBufferSize(ConversionUtil.toInt(value));
        }
        else if ("sendBufferSize".equals(key)) {
            this.setSendBufferSize(ConversionUtil.toInt(value));
        }
        else if ("receiveBufferSizePredictorFactory".equals(key)) {
            this.setReceiveBufferSizePredictorFactory((ReceiveBufferSizePredictorFactory)value);
        }
        else if ("receiveBufferSizePredictor".equals(key)) {
            this.setReceiveBufferSizePredictor((ReceiveBufferSizePredictor)value);
        }
        else if ("reuseAddress".equals(key)) {
            this.setReuseAddress(ConversionUtil.toBoolean(value));
        }
        else if ("loopbackModeDisabled".equals(key)) {
            this.setLoopbackModeDisabled(ConversionUtil.toBoolean(value));
        }
        else if ("interface".equals(key)) {
            this.setInterface((InetAddress)value);
        }
        else if ("networkInterface".equals(key)) {
            this.setNetworkInterface((NetworkInterface)value);
        }
        else if ("timeToLive".equals(key)) {
            this.setTimeToLive(ConversionUtil.toInt(value));
        }
        else {
            if (!"trafficClass".equals(key)) {
                return false;
            }
            this.setTrafficClass(ConversionUtil.toInt(value));
        }
        return true;
    }
    
    public boolean isBroadcast() {
        try {
            return this.socket.getBroadcast();
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public void setBroadcast(final boolean broadcast) {
        try {
            this.socket.setBroadcast(broadcast);
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public InetAddress getInterface() {
        if (this.socket instanceof MulticastSocket) {
            try {
                return ((MulticastSocket)this.socket).getInterface();
            }
            catch (SocketException e) {
                throw new ChannelException(e);
            }
        }
        throw new UnsupportedOperationException();
    }
    
    public void setInterface(final InetAddress interfaceAddress) {
        if (this.socket instanceof MulticastSocket) {
            try {
                ((MulticastSocket)this.socket).setInterface(interfaceAddress);
                return;
            }
            catch (SocketException e) {
                throw new ChannelException(e);
            }
            throw new UnsupportedOperationException();
        }
        throw new UnsupportedOperationException();
    }
    
    public boolean isLoopbackModeDisabled() {
        if (this.socket instanceof MulticastSocket) {
            try {
                return ((MulticastSocket)this.socket).getLoopbackMode();
            }
            catch (SocketException e) {
                throw new ChannelException(e);
            }
        }
        throw new UnsupportedOperationException();
    }
    
    public void setLoopbackModeDisabled(final boolean loopbackModeDisabled) {
        if (this.socket instanceof MulticastSocket) {
            try {
                ((MulticastSocket)this.socket).setLoopbackMode(loopbackModeDisabled);
                return;
            }
            catch (SocketException e) {
                throw new ChannelException(e);
            }
            throw new UnsupportedOperationException();
        }
        throw new UnsupportedOperationException();
    }
    
    public NetworkInterface getNetworkInterface() {
        if (this.socket instanceof MulticastSocket) {
            try {
                return ((MulticastSocket)this.socket).getNetworkInterface();
            }
            catch (SocketException e) {
                throw new ChannelException(e);
            }
        }
        throw new UnsupportedOperationException();
    }
    
    public void setNetworkInterface(final NetworkInterface networkInterface) {
        if (this.socket instanceof MulticastSocket) {
            try {
                ((MulticastSocket)this.socket).setNetworkInterface(networkInterface);
                return;
            }
            catch (SocketException e) {
                throw new ChannelException(e);
            }
            throw new UnsupportedOperationException();
        }
        throw new UnsupportedOperationException();
    }
    
    public boolean isReuseAddress() {
        try {
            return this.socket.getReuseAddress();
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public void setReuseAddress(final boolean reuseAddress) {
        try {
            this.socket.setReuseAddress(reuseAddress);
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public int getReceiveBufferSize() {
        try {
            return this.socket.getReceiveBufferSize();
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public void setReceiveBufferSize(final int receiveBufferSize) {
        try {
            this.socket.setReceiveBufferSize(receiveBufferSize);
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public int getSendBufferSize() {
        try {
            return this.socket.getSendBufferSize();
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public void setSendBufferSize(final int sendBufferSize) {
        try {
            this.socket.setSendBufferSize(sendBufferSize);
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public int getTimeToLive() {
        if (this.socket instanceof MulticastSocket) {
            try {
                return ((MulticastSocket)this.socket).getTimeToLive();
            }
            catch (IOException e) {
                throw new ChannelException(e);
            }
        }
        throw new UnsupportedOperationException();
    }
    
    public void setTimeToLive(final int ttl) {
        if (this.socket instanceof MulticastSocket) {
            try {
                ((MulticastSocket)this.socket).setTimeToLive(ttl);
                return;
            }
            catch (IOException e) {
                throw new ChannelException(e);
            }
            throw new UnsupportedOperationException();
        }
        throw new UnsupportedOperationException();
    }
    
    public int getTrafficClass() {
        try {
            return this.socket.getTrafficClass();
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public void setTrafficClass(final int trafficClass) {
        try {
            this.socket.setTrafficClass(trafficClass);
        }
        catch (SocketException e) {
            throw new ChannelException(e);
        }
    }
    
    public ReceiveBufferSizePredictor getReceiveBufferSizePredictor() {
        ReceiveBufferSizePredictor predictor = this.predictor;
        if (predictor == null) {
            try {
                predictor = (this.predictor = this.getReceiveBufferSizePredictorFactory().getPredictor());
            }
            catch (Exception e) {
                throw new ChannelException("Failed to create a new " + ReceiveBufferSizePredictor.class.getSimpleName() + '.', e);
            }
        }
        return predictor;
    }
    
    public void setReceiveBufferSizePredictor(final ReceiveBufferSizePredictor predictor) {
        if (predictor == null) {
            throw new NullPointerException("predictor");
        }
        this.predictor = predictor;
    }
    
    public ReceiveBufferSizePredictorFactory getReceiveBufferSizePredictorFactory() {
        return this.predictorFactory;
    }
    
    public void setReceiveBufferSizePredictorFactory(final ReceiveBufferSizePredictorFactory predictorFactory) {
        if (predictorFactory == null) {
            throw new NullPointerException("predictorFactory");
        }
        this.predictorFactory = predictorFactory;
    }
    
    static {
        DEFAULT_PREDICTOR_FACTORY = new FixedReceiveBufferSizePredictorFactory(768);
    }
}
