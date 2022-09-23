// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import org.apache.zookeeper.data.Id;
import java.util.List;
import org.apache.zookeeper.server.Request;
import org.slf4j.Logger;
import java.io.Flushable;
import org.apache.zookeeper.server.RequestProcessor;

public class SendAckRequestProcessor implements RequestProcessor, Flushable
{
    private static final Logger LOG;
    Learner learner;
    
    SendAckRequestProcessor(final Learner peer) {
        this.learner = peer;
    }
    
    @Override
    public void processRequest(final Request si) {
        if (si.type != 9) {
            final QuorumPacket qp = new QuorumPacket(3, si.hdr.getZxid(), null, null);
            try {
                this.learner.writePacket(qp, false);
            }
            catch (IOException e) {
                SendAckRequestProcessor.LOG.warn("Closing connection to leader, exception during packet send", e);
                try {
                    if (!this.learner.sock.isClosed()) {
                        this.learner.sock.close();
                    }
                }
                catch (IOException e2) {
                    SendAckRequestProcessor.LOG.debug("Ignoring error closing the connection", e2);
                }
            }
        }
    }
    
    @Override
    public void flush() throws IOException {
        try {
            this.learner.writePacket(null, true);
        }
        catch (IOException e) {
            SendAckRequestProcessor.LOG.warn("Closing connection to leader, exception during packet send", e);
            try {
                if (!this.learner.sock.isClosed()) {
                    this.learner.sock.close();
                }
            }
            catch (IOException e2) {
                SendAckRequestProcessor.LOG.debug("Ignoring error closing the connection", e2);
            }
        }
    }
    
    @Override
    public void shutdown() {
    }
    
    static {
        LOG = LoggerFactory.getLogger(SendAckRequestProcessor.class);
    }
}
