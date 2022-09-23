// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum.auth;

import java.io.IOException;
import java.net.Socket;

public interface QuorumAuthLearner
{
    void authenticate(final Socket p0, final String p1) throws IOException;
}
