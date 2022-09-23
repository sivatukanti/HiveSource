// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import java.io.PrintWriter;
import org.apache.zookeeper.server.ZKDatabase;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.server.ZooKeeperServer;

public abstract class QuorumZooKeeperServer extends ZooKeeperServer
{
    protected final QuorumPeer self;
    
    protected QuorumZooKeeperServer(final FileTxnSnapLog logFactory, final int tickTime, final int minSessionTimeout, final int maxSessionTimeout, final DataTreeBuilder treeBuilder, final ZKDatabase zkDb, final QuorumPeer self) {
        super(logFactory, tickTime, minSessionTimeout, maxSessionTimeout, treeBuilder, zkDb);
        this.self = self;
    }
    
    @Override
    public void dumpConf(final PrintWriter pwriter) {
        super.dumpConf(pwriter);
        pwriter.print("initLimit=");
        pwriter.println(this.self.getInitLimit());
        pwriter.print("syncLimit=");
        pwriter.println(this.self.getSyncLimit());
        pwriter.print("electionAlg=");
        pwriter.println(this.self.getElectionType());
        pwriter.print("electionPort=");
        pwriter.println(this.self.quorumPeers.get(this.self.getId()).electionAddr.getPort());
        pwriter.print("quorumPort=");
        pwriter.println(this.self.quorumPeers.get(this.self.getId()).addr.getPort());
        pwriter.print("peerType=");
        pwriter.println(this.self.getLearnerType().ordinal());
    }
    
    @Override
    protected void setState(final State state) {
        this.state = state;
    }
}
