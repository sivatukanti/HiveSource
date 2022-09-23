// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util.hash;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class MurmurHash extends Hash
{
    private static MurmurHash _instance;
    
    public static Hash getInstance() {
        return MurmurHash._instance;
    }
    
    @Override
    public int hash(final byte[] data, final int length, final int seed) {
        return this.hash(data, 0, length, seed);
    }
    
    public int hash(final byte[] data, final int offset, int length, final int seed) {
        final int m = 1540483477;
        final int r = 24;
        int h = seed ^ length;
        final int len_4 = length >> 2;
        for (int i = 0; i < len_4; ++i) {
            final int i_4 = offset + (i << 2);
            int k = data[i_4 + 3];
            k <<= 8;
            k |= (data[i_4 + 2] & 0xFF);
            k <<= 8;
            k |= (data[i_4 + 1] & 0xFF);
            k <<= 8;
            k |= (data[i_4 + 0] & 0xFF);
            k *= m;
            k ^= k >>> r;
            k *= m;
            h *= m;
            h ^= k;
        }
        final int len_m = len_4 << 2;
        final int left = length - len_m;
        if (left != 0) {
            length += offset;
            if (left >= 3) {
                h ^= data[length - 3] << 16;
            }
            if (left >= 2) {
                h ^= data[length - 2] << 8;
            }
            if (left >= 1) {
                h ^= data[length - 1];
            }
            h *= m;
        }
        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;
        return h;
    }
    
    static {
        MurmurHash._instance = new MurmurHash();
    }
}
