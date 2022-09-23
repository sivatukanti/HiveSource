// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

public class ProposalStats
{
    private int lastProposalSize;
    private int minProposalSize;
    private int maxProposalSize;
    
    public ProposalStats() {
        this.lastProposalSize = -1;
        this.minProposalSize = -1;
        this.maxProposalSize = -1;
    }
    
    public synchronized int getLastProposalSize() {
        return this.lastProposalSize;
    }
    
    synchronized void setLastProposalSize(final int value) {
        this.lastProposalSize = value;
        if (this.minProposalSize == -1 || value < this.minProposalSize) {
            this.minProposalSize = value;
        }
        if (value > this.maxProposalSize) {
            this.maxProposalSize = value;
        }
    }
    
    public synchronized int getMinProposalSize() {
        return this.minProposalSize;
    }
    
    public synchronized int getMaxProposalSize() {
        return this.maxProposalSize;
    }
    
    public synchronized void reset() {
        this.lastProposalSize = -1;
        this.minProposalSize = -1;
        this.maxProposalSize = -1;
    }
    
    @Override
    public synchronized String toString() {
        return String.format("%d/%d/%d", this.lastProposalSize, this.minProposalSize, this.maxProposalSize);
    }
}
