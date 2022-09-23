// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum.flexible;

import org.slf4j.LoggerFactory;
import java.util.Set;
import org.slf4j.Logger;

public class QuorumMaj implements QuorumVerifier
{
    private static final Logger LOG;
    int half;
    
    public QuorumMaj(final int n) {
        this.half = n / 2;
    }
    
    @Override
    public long getWeight(final long id) {
        return 1L;
    }
    
    @Override
    public boolean containsQuorum(final Set<Long> set) {
        return set.size() > this.half;
    }
    
    static {
        LOG = LoggerFactory.getLogger(QuorumMaj.class);
    }
}
