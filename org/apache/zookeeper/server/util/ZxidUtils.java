// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.util;

public class ZxidUtils
{
    public static long getEpochFromZxid(final long zxid) {
        return zxid >> 32;
    }
    
    public static long getCounterFromZxid(final long zxid) {
        return zxid & 0xFFFFFFFFL;
    }
    
    public static long makeZxid(final long epoch, final long counter) {
        return epoch << 32 | (counter & 0xFFFFFFFFL);
    }
    
    public static String zxidToString(final long zxid) {
        return Long.toHexString(zxid);
    }
}
