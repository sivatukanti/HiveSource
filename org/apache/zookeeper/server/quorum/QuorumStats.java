// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

public class QuorumStats
{
    private final Provider provider;
    
    protected QuorumStats(final Provider provider) {
        this.provider = provider;
    }
    
    public String getServerState() {
        return this.provider.getServerState();
    }
    
    public String[] getQuorumPeers() {
        return this.provider.getQuorumPeers();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(super.toString());
        final String state = this.getServerState();
        if (state.equals("leading")) {
            sb.append("Followers:");
            for (final String f : this.getQuorumPeers()) {
                sb.append(" ").append(f);
            }
            sb.append("\n");
        }
        else if (state.equals("following") || state.equals("observing")) {
            sb.append("Leader: ");
            final String[] ldr = this.getQuorumPeers();
            if (ldr.length > 0) {
                sb.append(ldr[0]);
            }
            else {
                sb.append("not connected");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public interface Provider
    {
        public static final String UNKNOWN_STATE = "unknown";
        public static final String LOOKING_STATE = "leaderelection";
        public static final String LEADING_STATE = "leading";
        public static final String FOLLOWING_STATE = "following";
        public static final String OBSERVING_STATE = "observing";
        
        String[] getQuorumPeers();
        
        String getServerState();
    }
}
