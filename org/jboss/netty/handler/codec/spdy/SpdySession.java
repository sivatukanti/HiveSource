// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Iterator;
import org.jboss.netty.channel.MessageEvent;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

final class SpdySession
{
    private static final SpdyProtocolException STREAM_CLOSED;
    private final AtomicInteger activeLocalStreams;
    private final AtomicInteger activeRemoteStreams;
    private final Map<Integer, StreamState> activeStreams;
    private final StreamComparator streamComparator;
    private final AtomicInteger sendWindowSize;
    private final AtomicInteger receiveWindowSize;
    
    public SpdySession(final int sendWindowSize, final int receiveWindowSize) {
        this.activeLocalStreams = new AtomicInteger();
        this.activeRemoteStreams = new AtomicInteger();
        this.activeStreams = new ConcurrentHashMap<Integer, StreamState>();
        this.streamComparator = new StreamComparator();
        this.sendWindowSize = new AtomicInteger(sendWindowSize);
        this.receiveWindowSize = new AtomicInteger(receiveWindowSize);
    }
    
    int numActiveStreams(final boolean remote) {
        if (remote) {
            return this.activeRemoteStreams.get();
        }
        return this.activeLocalStreams.get();
    }
    
    boolean noActiveStreams() {
        return this.activeStreams.isEmpty();
    }
    
    boolean isActiveStream(final int streamId) {
        return this.activeStreams.containsKey(streamId);
    }
    
    Map<Integer, StreamState> activeStreams() {
        final Map<Integer, StreamState> streams = new TreeMap<Integer, StreamState>(this.streamComparator);
        streams.putAll(this.activeStreams);
        return streams;
    }
    
    void acceptStream(final int streamId, final byte priority, final boolean remoteSideClosed, final boolean localSideClosed, final int sendWindowSize, final int receiveWindowSize, final boolean remote) {
        if (!remoteSideClosed || !localSideClosed) {
            final StreamState state = this.activeStreams.put(streamId, new StreamState(priority, remoteSideClosed, localSideClosed, sendWindowSize, receiveWindowSize));
            if (state == null) {
                if (remote) {
                    this.activeRemoteStreams.incrementAndGet();
                }
                else {
                    this.activeLocalStreams.incrementAndGet();
                }
            }
        }
    }
    
    private StreamState removeActiveStream(final int streamId, final boolean remote) {
        final StreamState state = this.activeStreams.remove(streamId);
        if (state != null) {
            if (remote) {
                this.activeRemoteStreams.decrementAndGet();
            }
            else {
                this.activeLocalStreams.decrementAndGet();
            }
        }
        return state;
    }
    
    void removeStream(final int streamId, final boolean remote) {
        final StreamState state = this.removeActiveStream(streamId, remote);
        if (state != null) {
            for (MessageEvent e = state.removePendingWrite(); e != null; e = state.removePendingWrite()) {
                e.getFuture().setFailure(SpdySession.STREAM_CLOSED);
            }
        }
    }
    
    boolean isRemoteSideClosed(final int streamId) {
        final StreamState state = this.activeStreams.get(streamId);
        return state == null || state.isRemoteSideClosed();
    }
    
    void closeRemoteSide(final int streamId, final boolean remote) {
        final StreamState state = this.activeStreams.get(streamId);
        if (state != null) {
            state.closeRemoteSide();
            if (state.isLocalSideClosed()) {
                this.removeActiveStream(streamId, remote);
            }
        }
    }
    
    boolean isLocalSideClosed(final int streamId) {
        final StreamState state = this.activeStreams.get(streamId);
        return state == null || state.isLocalSideClosed();
    }
    
    void closeLocalSide(final int streamId, final boolean remote) {
        final StreamState state = this.activeStreams.get(streamId);
        if (state != null) {
            state.closeLocalSide();
            if (state.isRemoteSideClosed()) {
                this.removeActiveStream(streamId, remote);
            }
        }
    }
    
    boolean hasReceivedReply(final int streamId) {
        final StreamState state = this.activeStreams.get(streamId);
        return state != null && state.hasReceivedReply();
    }
    
    void receivedReply(final int streamId) {
        final StreamState state = this.activeStreams.get(streamId);
        if (state != null) {
            state.receivedReply();
        }
    }
    
    int getSendWindowSize(final int streamId) {
        if (streamId == 0) {
            return this.sendWindowSize.get();
        }
        final StreamState state = this.activeStreams.get(streamId);
        return (state != null) ? state.getSendWindowSize() : -1;
    }
    
    int updateSendWindowSize(final int streamId, final int deltaWindowSize) {
        if (streamId == 0) {
            return this.sendWindowSize.addAndGet(deltaWindowSize);
        }
        final StreamState state = this.activeStreams.get(streamId);
        return (state != null) ? state.updateSendWindowSize(deltaWindowSize) : -1;
    }
    
    int updateReceiveWindowSize(final int streamId, final int deltaWindowSize) {
        if (streamId == 0) {
            return this.receiveWindowSize.addAndGet(deltaWindowSize);
        }
        final StreamState state = this.activeStreams.get(streamId);
        if (deltaWindowSize > 0) {
            state.setReceiveWindowSizeLowerBound(0);
        }
        return (state != null) ? state.updateReceiveWindowSize(deltaWindowSize) : -1;
    }
    
    int getReceiveWindowSizeLowerBound(final int streamId) {
        if (streamId == 0) {
            return 0;
        }
        final StreamState state = this.activeStreams.get(streamId);
        return (state != null) ? state.getReceiveWindowSizeLowerBound() : 0;
    }
    
    void updateAllSendWindowSizes(final int deltaWindowSize) {
        for (final StreamState state : this.activeStreams.values()) {
            state.updateSendWindowSize(deltaWindowSize);
        }
    }
    
    void updateAllReceiveWindowSizes(final int deltaWindowSize) {
        for (final StreamState state : this.activeStreams.values()) {
            state.updateReceiveWindowSize(deltaWindowSize);
            if (deltaWindowSize < 0) {
                state.setReceiveWindowSizeLowerBound(deltaWindowSize);
            }
        }
    }
    
    boolean putPendingWrite(final int streamId, final MessageEvent evt) {
        final StreamState state = this.activeStreams.get(streamId);
        return state != null && state.putPendingWrite(evt);
    }
    
    MessageEvent getPendingWrite(final int streamId) {
        if (streamId == 0) {
            for (final Map.Entry<Integer, StreamState> e : this.activeStreams().entrySet()) {
                final StreamState state = e.getValue();
                if (state.getSendWindowSize() > 0) {
                    final MessageEvent evt = state.getPendingWrite();
                    if (evt != null) {
                        return evt;
                    }
                    continue;
                }
            }
            return null;
        }
        final StreamState state2 = this.activeStreams.get(streamId);
        return (state2 != null) ? state2.getPendingWrite() : null;
    }
    
    MessageEvent removePendingWrite(final int streamId) {
        final StreamState state = this.activeStreams.get(streamId);
        return (state != null) ? state.removePendingWrite() : null;
    }
    
    static {
        STREAM_CLOSED = new SpdyProtocolException("Stream closed");
    }
    
    private static final class StreamState
    {
        private final byte priority;
        private volatile boolean remoteSideClosed;
        private volatile boolean localSideClosed;
        private boolean receivedReply;
        private final AtomicInteger sendWindowSize;
        private final AtomicInteger receiveWindowSize;
        private volatile int receiveWindowSizeLowerBound;
        private final ConcurrentLinkedQueue<MessageEvent> pendingWriteQueue;
        
        StreamState(final byte priority, final boolean remoteSideClosed, final boolean localSideClosed, final int sendWindowSize, final int receiveWindowSize) {
            this.pendingWriteQueue = new ConcurrentLinkedQueue<MessageEvent>();
            this.priority = priority;
            this.remoteSideClosed = remoteSideClosed;
            this.localSideClosed = localSideClosed;
            this.sendWindowSize = new AtomicInteger(sendWindowSize);
            this.receiveWindowSize = new AtomicInteger(receiveWindowSize);
        }
        
        byte getPriority() {
            return this.priority;
        }
        
        boolean isRemoteSideClosed() {
            return this.remoteSideClosed;
        }
        
        void closeRemoteSide() {
            this.remoteSideClosed = true;
        }
        
        boolean isLocalSideClosed() {
            return this.localSideClosed;
        }
        
        void closeLocalSide() {
            this.localSideClosed = true;
        }
        
        boolean hasReceivedReply() {
            return this.receivedReply;
        }
        
        void receivedReply() {
            this.receivedReply = true;
        }
        
        int getSendWindowSize() {
            return this.sendWindowSize.get();
        }
        
        int updateSendWindowSize(final int deltaWindowSize) {
            return this.sendWindowSize.addAndGet(deltaWindowSize);
        }
        
        int updateReceiveWindowSize(final int deltaWindowSize) {
            return this.receiveWindowSize.addAndGet(deltaWindowSize);
        }
        
        int getReceiveWindowSizeLowerBound() {
            return this.receiveWindowSizeLowerBound;
        }
        
        void setReceiveWindowSizeLowerBound(final int receiveWindowSizeLowerBound) {
            this.receiveWindowSizeLowerBound = receiveWindowSizeLowerBound;
        }
        
        boolean putPendingWrite(final MessageEvent evt) {
            return this.pendingWriteQueue.offer(evt);
        }
        
        MessageEvent getPendingWrite() {
            return this.pendingWriteQueue.peek();
        }
        
        MessageEvent removePendingWrite() {
            return this.pendingWriteQueue.poll();
        }
    }
    
    private final class StreamComparator implements Comparator<Integer>, Serializable
    {
        private static final long serialVersionUID = 1161471649740544848L;
        
        StreamComparator() {
        }
        
        public int compare(final Integer id1, final Integer id2) {
            final StreamState state1 = SpdySession.this.activeStreams.get(id1);
            final StreamState state2 = SpdySession.this.activeStreams.get(id2);
            final int result = state1.getPriority() - state2.getPriority();
            if (result != 0) {
                return result;
            }
            return id1 - id2;
        }
    }
}
