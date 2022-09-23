// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Vote
{
    private static final Logger LOG;
    private final int version;
    private final long id;
    private final long zxid;
    private final long electionEpoch;
    private final long peerEpoch;
    private final QuorumPeer.ServerState state;
    
    public Vote(final long id, final long zxid) {
        this.version = 0;
        this.id = id;
        this.zxid = zxid;
        this.electionEpoch = -1L;
        this.peerEpoch = -1L;
        this.state = QuorumPeer.ServerState.LOOKING;
    }
    
    public Vote(final long id, final long zxid, final long peerEpoch) {
        this.version = 0;
        this.id = id;
        this.zxid = zxid;
        this.electionEpoch = -1L;
        this.peerEpoch = peerEpoch;
        this.state = QuorumPeer.ServerState.LOOKING;
    }
    
    public Vote(final long id, final long zxid, final long electionEpoch, final long peerEpoch) {
        this.version = 0;
        this.id = id;
        this.zxid = zxid;
        this.electionEpoch = electionEpoch;
        this.peerEpoch = peerEpoch;
        this.state = QuorumPeer.ServerState.LOOKING;
    }
    
    public Vote(final int version, final long id, final long zxid, final long electionEpoch, final long peerEpoch, final QuorumPeer.ServerState state) {
        this.version = version;
        this.id = id;
        this.zxid = zxid;
        this.electionEpoch = electionEpoch;
        this.state = state;
        this.peerEpoch = peerEpoch;
    }
    
    public Vote(final long id, final long zxid, final long electionEpoch, final long peerEpoch, final QuorumPeer.ServerState state) {
        this.id = id;
        this.zxid = zxid;
        this.electionEpoch = electionEpoch;
        this.state = state;
        this.peerEpoch = peerEpoch;
        this.version = 0;
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public long getId() {
        return this.id;
    }
    
    public long getZxid() {
        return this.zxid;
    }
    
    public long getElectionEpoch() {
        return this.electionEpoch;
    }
    
    public long getPeerEpoch() {
        return this.peerEpoch;
    }
    
    public QuorumPeer.ServerState getState() {
        return this.state;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Vote)) {
            return false;
        }
        final Vote other = (Vote)o;
        if (this.state == QuorumPeer.ServerState.LOOKING || other.state == QuorumPeer.ServerState.LOOKING) {
            return this.id == other.id && this.zxid == other.zxid && this.electionEpoch == other.electionEpoch && this.peerEpoch == other.peerEpoch;
        }
        if (this.version > 0 ^ other.version > 0) {
            return this.id == other.id;
        }
        return this.id == other.id && this.peerEpoch == other.peerEpoch;
    }
    
    @Override
    public int hashCode() {
        return (int)(this.id & this.zxid);
    }
    
    @Override
    public String toString() {
        return String.format("(%d, %s, %s)", this.id, Long.toHexString(this.zxid), Long.toHexString(this.peerEpoch));
    }
    
    static {
        LOG = LoggerFactory.getLogger(Vote.class);
    }
}
