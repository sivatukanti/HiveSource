// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.slf4j.LoggerFactory;
import java.net.SocketAddress;
import org.apache.zookeeper.server.Request;
import org.slf4j.Logger;
import org.apache.zookeeper.server.RequestProcessor;

class AckRequestProcessor implements RequestProcessor
{
    private static final Logger LOG;
    Leader leader;
    
    AckRequestProcessor(final Leader leader) {
        this.leader = leader;
    }
    
    @Override
    public void processRequest(final Request request) {
        final QuorumPeer self = this.leader.self;
        if (self != null) {
            this.leader.processAck(self.getId(), request.zxid, null);
        }
        else {
            AckRequestProcessor.LOG.error("Null QuorumPeer");
        }
    }
    
    @Override
    public void shutdown() {
    }
    
    static {
        LOG = LoggerFactory.getLogger(AckRequestProcessor.class);
    }
}
