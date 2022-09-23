// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import org.apache.zookeeper.jmx.ZKMBeanInfo;
import org.apache.zookeeper.jmx.MBeanRegistry;
import java.util.Iterator;
import java.util.Map;
import java.util.HashSet;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Random;
import org.slf4j.Logger;

@Deprecated
public class LeaderElection implements Election
{
    private static final Logger LOG;
    protected static final Random epochGen;
    protected QuorumPeer self;
    
    public LeaderElection(final QuorumPeer self) {
        this.self = self;
    }
    
    protected ElectionResult countVotes(final HashMap<InetSocketAddress, Vote> votes, final HashSet<Long> heardFrom) {
        final ElectionResult result = new ElectionResult();
        result.vote = new Vote(Long.MIN_VALUE, Long.MIN_VALUE);
        result.winner = new Vote(Long.MIN_VALUE, Long.MIN_VALUE);
        final HashMap<InetSocketAddress, Vote> validVotes = new HashMap<InetSocketAddress, Vote>();
        final Map<Long, Long> maxZxids = new HashMap<Long, Long>();
        for (final Map.Entry<InetSocketAddress, Vote> e : votes.entrySet()) {
            final Vote v = e.getValue();
            if (heardFrom.contains(v.getId())) {
                validVotes.put(e.getKey(), v);
                final Long val = maxZxids.get(v.getId());
                if (val != null && val >= v.getZxid()) {
                    continue;
                }
                maxZxids.put(v.getId(), v.getZxid());
            }
        }
        for (final Map.Entry<InetSocketAddress, Vote> e : validVotes.entrySet()) {
            final Vote v = e.getValue();
            final Long zxid = maxZxids.get(v.getId());
            if (v.getZxid() < zxid) {
                e.setValue(new Vote(v.getId(), zxid, v.getElectionEpoch(), v.getPeerEpoch(), v.getState()));
            }
        }
        result.numValidVotes = validVotes.size();
        final HashMap<Vote, Integer> countTable = new HashMap<Vote, Integer>();
        final Iterator<Vote> iterator3 = validVotes.values().iterator();
        while (iterator3.hasNext()) {
            final Vote v = iterator3.next();
            Integer count = countTable.get(v);
            if (count == null) {
                count = 0;
            }
            countTable.put(v, count + 1);
            if (v.getId() == result.vote.getId()) {
                final ElectionResult electionResult = result;
                ++electionResult.count;
            }
            else {
                if (v.getZxid() <= result.vote.getZxid() && (v.getZxid() != result.vote.getZxid() || v.getId() <= result.vote.getId())) {
                    continue;
                }
                result.vote = v;
                result.count = 1;
            }
        }
        result.winningCount = 0;
        LeaderElection.LOG.info("Election tally: ");
        for (final Map.Entry<Vote, Integer> entry : countTable.entrySet()) {
            if (entry.getValue() > result.winningCount) {
                result.winningCount = entry.getValue();
                result.winner = entry.getKey();
            }
            LeaderElection.LOG.info(entry.getKey().getId() + "\t-> " + entry.getValue());
        }
        return result;
    }
    
    @Override
    public void shutdown() {
    }
    
    @Override
    public Vote lookForLeader() throws InterruptedException {
        try {
            this.self.jmxLeaderElectionBean = new LeaderElectionBean();
            MBeanRegistry.getInstance().register(this.self.jmxLeaderElectionBean, this.self.jmxLocalPeerBean);
        }
        catch (Exception e) {
            LeaderElection.LOG.warn("Failed to register with JMX", e);
            this.self.jmxLeaderElectionBean = null;
        }
        try {
            this.self.setCurrentVote(new Vote(this.self.getId(), this.self.getLastLoggedZxid()));
            final byte[] requestBytes = new byte[4];
            final ByteBuffer requestBuffer = ByteBuffer.wrap(requestBytes);
            final byte[] responseBytes = new byte[28];
            final ByteBuffer responseBuffer = ByteBuffer.wrap(responseBytes);
            DatagramSocket s = null;
            try {
                s = new DatagramSocket();
                s.setSoTimeout(200);
            }
            catch (SocketException e2) {
                LeaderElection.LOG.error("Socket exception when creating socket for leader election", e2);
                System.exit(4);
            }
            final DatagramPacket requestPacket = new DatagramPacket(requestBytes, requestBytes.length);
            final DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length);
            final int xid = LeaderElection.epochGen.nextInt();
            while (this.self.isRunning()) {
                final HashMap<InetSocketAddress, Vote> votes = new HashMap<InetSocketAddress, Vote>(this.self.getVotingView().size());
                requestBuffer.clear();
                requestBuffer.putInt(xid);
                requestPacket.setLength(4);
                final HashSet<Long> heardFrom = new HashSet<Long>();
                for (final QuorumPeer.QuorumServer server : this.self.getVotingView().values()) {
                    LeaderElection.LOG.info("Server address: " + server.addr);
                    try {
                        requestPacket.setSocketAddress(server.addr);
                    }
                    catch (IllegalArgumentException e3) {
                        throw new IllegalArgumentException("Unable to set socket address on packet, msg:" + e3.getMessage() + " with addr:" + server.addr, e3);
                    }
                    try {
                        s.send(requestPacket);
                        responsePacket.setLength(responseBytes.length);
                        s.receive(responsePacket);
                        if (responsePacket.getLength() != responseBytes.length) {
                            LeaderElection.LOG.error("Got a short response: " + responsePacket.getLength());
                        }
                        else {
                            responseBuffer.clear();
                            final int recvedXid = responseBuffer.getInt();
                            if (recvedXid != xid) {
                                LeaderElection.LOG.error("Got bad xid: expected " + xid + " got " + recvedXid);
                            }
                            else {
                                final long peerId = responseBuffer.getLong();
                                heardFrom.add(peerId);
                                final Vote vote = new Vote(responseBuffer.getLong(), responseBuffer.getLong());
                                final InetSocketAddress addr = (InetSocketAddress)responsePacket.getSocketAddress();
                                votes.put(addr, vote);
                            }
                        }
                    }
                    catch (IOException e4) {
                        LeaderElection.LOG.warn("Ignoring exception while looking for leader", e4);
                    }
                }
                final ElectionResult result = this.countVotes(votes, heardFrom);
                if (result.numValidVotes == 0) {
                    this.self.setCurrentVote(new Vote(this.self.getId(), this.self.getLastLoggedZxid()));
                }
                else if (result.winner.getId() >= 0L) {
                    this.self.setCurrentVote(result.vote);
                    if (result.winningCount > this.self.getVotingView().size() / 2) {
                        this.self.setCurrentVote(result.winner);
                        s.close();
                        final Vote current = this.self.getCurrentVote();
                        LeaderElection.LOG.info("Found leader: my type is: " + this.self.getLearnerType());
                        if (this.self.getLearnerType() != QuorumPeer.LearnerType.OBSERVER) {
                            this.self.setPeerState((current.getId() == this.self.getId()) ? QuorumPeer.ServerState.LEADING : QuorumPeer.ServerState.FOLLOWING);
                            if (this.self.getPeerState() == QuorumPeer.ServerState.FOLLOWING) {
                                Thread.sleep(100L);
                            }
                            return current;
                        }
                        if (current.getId() != this.self.getId()) {
                            this.self.setPeerState(QuorumPeer.ServerState.OBSERVING);
                            Thread.sleep(100L);
                            return current;
                        }
                        LeaderElection.LOG.error("OBSERVER elected as leader!");
                        Thread.sleep(100L);
                    }
                }
                Thread.sleep(1000L);
            }
            return null;
        }
        finally {
            try {
                if (this.self.jmxLeaderElectionBean != null) {
                    MBeanRegistry.getInstance().unregister(this.self.jmxLeaderElectionBean);
                }
            }
            catch (Exception e5) {
                LeaderElection.LOG.warn("Failed to unregister with JMX", e5);
            }
            this.self.jmxLeaderElectionBean = null;
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(LeaderElection.class);
        epochGen = new Random();
    }
    
    protected static class ElectionResult
    {
        public Vote vote;
        public int count;
        public Vote winner;
        public int winningCount;
        public int numValidVotes;
    }
}
