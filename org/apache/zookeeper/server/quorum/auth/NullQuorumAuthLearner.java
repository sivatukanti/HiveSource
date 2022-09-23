// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum.auth;

import java.net.Socket;

public class NullQuorumAuthLearner implements QuorumAuthLearner
{
    @Override
    public void authenticate(final Socket sock, final String hostname) {
    }
}
