// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

public class StatsTrack
{
    private int count;
    private long bytes;
    private String countStr;
    private String byteStr;
    
    public StatsTrack() {
        this(null);
    }
    
    public StatsTrack(String stats) {
        this.countStr = "count";
        this.byteStr = "bytes";
        if (stats == null) {
            stats = "count=-1,bytes=-1";
        }
        final String[] split = stats.split(",");
        if (split.length != 2) {
            throw new IllegalArgumentException("invalid string " + stats);
        }
        this.count = Integer.parseInt(split[0].split("=")[1]);
        this.bytes = Long.parseLong(split[1].split("=")[1]);
    }
    
    public int getCount() {
        return this.count;
    }
    
    public void setCount(final int count) {
        this.count = count;
    }
    
    public long getBytes() {
        return this.bytes;
    }
    
    public void setBytes(final long bytes) {
        this.bytes = bytes;
    }
    
    @Override
    public String toString() {
        return this.countStr + "=" + this.count + "," + this.byteStr + "=" + this.bytes;
    }
}
