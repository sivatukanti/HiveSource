// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.cache;

class CacheStat
{
    protected int findHit;
    protected int findMiss;
    protected int findFault;
    protected int findCachedHit;
    protected int findCachedMiss;
    protected int create;
    protected int ageOut;
    protected int cleanAll;
    protected int remove;
    protected long initialSize;
    protected long maxSize;
    protected long currentSize;
    protected long[] data;
    
    public long[] getStats() {
        if (this.data == null) {
            this.data = new long[14];
        }
        this.data[0] = this.findHit + this.findMiss;
        this.data[1] = this.findHit;
        this.data[2] = this.findMiss;
        this.data[3] = this.findFault;
        this.data[4] = this.findCachedHit + this.findCachedMiss;
        this.data[5] = this.findCachedHit;
        this.data[6] = this.findCachedMiss;
        this.data[7] = this.create;
        this.data[8] = this.ageOut;
        this.data[9] = this.cleanAll;
        this.data[10] = this.remove;
        this.data[11] = this.initialSize;
        this.data[12] = this.maxSize;
        this.data[13] = this.currentSize;
        return this.data;
    }
    
    public void reset() {
        this.findHit = 0;
        this.findMiss = 0;
        this.findFault = 0;
        this.findCachedHit = 0;
        this.findCachedMiss = 0;
        this.create = 0;
        this.ageOut = 0;
        this.cleanAll = 0;
        this.remove = 0;
    }
}
