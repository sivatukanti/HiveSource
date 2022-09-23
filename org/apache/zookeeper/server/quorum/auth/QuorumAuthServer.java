// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum.auth;

import java.io.IOException;
import java.io.DataInputStream;
import java.net.Socket;

public interface QuorumAuthServer
{
    void authenticate(final Socket p0, final DataInputStream p1) throws IOException;
}
