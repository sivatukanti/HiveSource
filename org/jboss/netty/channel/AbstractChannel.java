// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel;

import java.util.concurrent.ConcurrentHashMap;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractChannel implements Channel
{
    static final ConcurrentMap<Integer, Channel> allChannels;
    private static final Random random;
    private final Integer id;
    private final Channel parent;
    private final ChannelFactory factory;
    private final ChannelPipeline pipeline;
    private final ChannelFuture succeededFuture;
    private final ChannelCloseFuture closeFuture;
    private volatile int interestOps;
    private boolean strValConnected;
    private String strVal;
    private volatile Object attachment;
    private static final AtomicIntegerFieldUpdater<AbstractChannel> UNWRITABLE_UPDATER;
    private volatile int unwritable;
    
    private static Integer allocateId(final Channel channel) {
        Integer id;
        for (id = AbstractChannel.random.nextInt(); AbstractChannel.allChannels.putIfAbsent(id, channel) != null; ++id) {}
        return id;
    }
    
    protected AbstractChannel(final Channel parent, final ChannelFactory factory, final ChannelPipeline pipeline, final ChannelSink sink) {
        this.succeededFuture = new SucceededChannelFuture(this);
        this.closeFuture = new ChannelCloseFuture();
        this.interestOps = 1;
        this.parent = parent;
        this.factory = factory;
        this.pipeline = pipeline;
        this.id = allocateId(this);
        pipeline.attach(this, sink);
    }
    
    protected AbstractChannel(final Integer id, final Channel parent, final ChannelFactory factory, final ChannelPipeline pipeline, final ChannelSink sink) {
        this.succeededFuture = new SucceededChannelFuture(this);
        this.closeFuture = new ChannelCloseFuture();
        this.interestOps = 1;
        this.id = id;
        this.parent = parent;
        this.factory = factory;
        (this.pipeline = pipeline).attach(this, sink);
    }
    
    public final Integer getId() {
        return this.id;
    }
    
    public Channel getParent() {
        return this.parent;
    }
    
    public ChannelFactory getFactory() {
        return this.factory;
    }
    
    public ChannelPipeline getPipeline() {
        return this.pipeline;
    }
    
    protected ChannelFuture getSucceededFuture() {
        return this.succeededFuture;
    }
    
    protected ChannelFuture getUnsupportedOperationFuture() {
        return new FailedChannelFuture(this, new UnsupportedOperationException());
    }
    
    @Override
    public final int hashCode() {
        return this.id;
    }
    
    @Override
    public final boolean equals(final Object o) {
        return this == o;
    }
    
    public final int compareTo(final Channel o) {
        return this.getId().compareTo(o.getId());
    }
    
    public boolean isOpen() {
        return !this.closeFuture.isDone();
    }
    
    protected boolean setClosed() {
        AbstractChannel.allChannels.remove(this.id);
        return this.closeFuture.setClosed();
    }
    
    public ChannelFuture bind(final SocketAddress localAddress) {
        return Channels.bind(this, localAddress);
    }
    
    public ChannelFuture unbind() {
        return Channels.unbind(this);
    }
    
    public ChannelFuture close() {
        final ChannelFuture returnedCloseFuture = Channels.close(this);
        assert this.closeFuture == returnedCloseFuture;
        return this.closeFuture;
    }
    
    public ChannelFuture getCloseFuture() {
        return this.closeFuture;
    }
    
    public ChannelFuture connect(final SocketAddress remoteAddress) {
        return Channels.connect(this, remoteAddress);
    }
    
    public ChannelFuture disconnect() {
        return Channels.disconnect(this);
    }
    
    public int getInterestOps() {
        if (!this.isOpen()) {
            return 4;
        }
        int interestOps = this.getInternalInterestOps() & 0xFFFFFFFB;
        if (!this.isWritable()) {
            interestOps |= 0x4;
        }
        return interestOps;
    }
    
    public ChannelFuture setInterestOps(final int interestOps) {
        return Channels.setInterestOps(this, interestOps);
    }
    
    protected int getInternalInterestOps() {
        return this.interestOps;
    }
    
    protected void setInternalInterestOps(final int interestOps) {
        this.interestOps = interestOps;
    }
    
    public boolean isReadable() {
        return (this.getInternalInterestOps() & 0x1) != 0x0;
    }
    
    public boolean isWritable() {
        return this.unwritable == 0;
    }
    
    public final boolean getUserDefinedWritability(final int index) {
        return (this.unwritable & writabilityMask(index)) == 0x0;
    }
    
    public final void setUserDefinedWritability(final int index, final boolean writable) {
        if (writable) {
            this.setUserDefinedWritability(index);
        }
        else {
            this.clearUserDefinedWritability(index);
        }
    }
    
    private void setUserDefinedWritability(final int index) {
        final int mask = ~writabilityMask(index);
        int oldValue;
        int newValue;
        do {
            oldValue = this.unwritable;
            newValue = (oldValue & mask);
        } while (!AbstractChannel.UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue));
        if (oldValue != 0 && newValue == 0) {
            this.getPipeline().sendUpstream(new UpstreamChannelStateEvent(this, ChannelState.INTEREST_OPS, this.getInterestOps()));
        }
    }
    
    private void clearUserDefinedWritability(final int index) {
        final int mask = writabilityMask(index);
        int oldValue;
        int newValue;
        do {
            oldValue = this.unwritable;
            newValue = (oldValue | mask);
        } while (!AbstractChannel.UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue));
        if (oldValue == 0 && newValue != 0) {
            this.getPipeline().sendUpstream(new UpstreamChannelStateEvent(this, ChannelState.INTEREST_OPS, this.getInterestOps()));
        }
    }
    
    private static int writabilityMask(final int index) {
        if (index < 1 || index > 31) {
            throw new IllegalArgumentException("index: " + index + " (expected: 1~31)");
        }
        return 1 << index;
    }
    
    protected boolean setWritable() {
        int oldValue;
        int newValue;
        do {
            oldValue = this.unwritable;
            newValue = (oldValue & 0xFFFFFFFE);
        } while (!AbstractChannel.UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue));
        return oldValue != 0 && newValue == 0;
    }
    
    protected boolean setUnwritable() {
        int oldValue;
        int newValue;
        do {
            oldValue = this.unwritable;
            newValue = (oldValue | 0x1);
        } while (!AbstractChannel.UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue));
        return oldValue == 0 && newValue != 0;
    }
    
    public ChannelFuture setReadable(final boolean readable) {
        if (readable) {
            return this.setInterestOps(this.getInterestOps() | 0x1);
        }
        return this.setInterestOps(this.getInterestOps() & 0xFFFFFFFE);
    }
    
    public ChannelFuture write(final Object message) {
        return Channels.write(this, message);
    }
    
    public ChannelFuture write(final Object message, final SocketAddress remoteAddress) {
        return Channels.write(this, message, remoteAddress);
    }
    
    public Object getAttachment() {
        return this.attachment;
    }
    
    public void setAttachment(final Object attachment) {
        this.attachment = attachment;
    }
    
    @Override
    public String toString() {
        final boolean connected = this.isConnected();
        if (this.strValConnected == connected && this.strVal != null) {
            return this.strVal;
        }
        final StringBuilder buf = new StringBuilder(128);
        buf.append("[id: 0x");
        buf.append(this.getIdString());
        final SocketAddress localAddress = this.getLocalAddress();
        final SocketAddress remoteAddress = this.getRemoteAddress();
        if (remoteAddress != null) {
            buf.append(", ");
            if (this.getParent() == null) {
                buf.append(localAddress);
                buf.append(connected ? " => " : " :> ");
                buf.append(remoteAddress);
            }
            else {
                buf.append(remoteAddress);
                buf.append(connected ? " => " : " :> ");
                buf.append(localAddress);
            }
        }
        else if (localAddress != null) {
            buf.append(", ");
            buf.append(localAddress);
        }
        buf.append(']');
        final String strVal = buf.toString();
        this.strVal = strVal;
        this.strValConnected = connected;
        return strVal;
    }
    
    private String getIdString() {
        String answer = Integer.toHexString(this.id);
        switch (answer.length()) {
            case 0: {
                answer = "00000000";
                break;
            }
            case 1: {
                answer = "0000000" + answer;
                break;
            }
            case 2: {
                answer = "000000" + answer;
                break;
            }
            case 3: {
                answer = "00000" + answer;
                break;
            }
            case 4: {
                answer = "0000" + answer;
                break;
            }
            case 5: {
                answer = "000" + answer;
                break;
            }
            case 6: {
                answer = "00" + answer;
                break;
            }
            case 7: {
                answer = '0' + answer;
                break;
            }
        }
        return answer;
    }
    
    static {
        allChannels = new ConcurrentHashMap<Integer, Channel>();
        random = new Random();
        UNWRITABLE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AbstractChannel.class, "unwritable");
    }
    
    private final class ChannelCloseFuture extends DefaultChannelFuture
    {
        ChannelCloseFuture() {
            super(AbstractChannel.this, false);
        }
        
        @Override
        public boolean setSuccess() {
            return false;
        }
        
        @Override
        public boolean setFailure(final Throwable cause) {
            return false;
        }
        
        boolean setClosed() {
            return super.setSuccess();
        }
    }
}
