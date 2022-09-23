// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum.flexible;

import java.util.Set;

public interface QuorumVerifier
{
    long getWeight(final long p0);
    
    boolean containsQuorum(final Set<Long> p0);
}
