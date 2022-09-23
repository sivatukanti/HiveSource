// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util.hash;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public abstract class Hash
{
    public static final int INVALID_HASH = -1;
    public static final int JENKINS_HASH = 0;
    public static final int MURMUR_HASH = 1;
    
    public static int parseHashType(final String name) {
        if ("jenkins".equalsIgnoreCase(name)) {
            return 0;
        }
        if ("murmur".equalsIgnoreCase(name)) {
            return 1;
        }
        return -1;
    }
    
    public static int getHashType(final Configuration conf) {
        final String name = conf.get("hadoop.util.hash.type", "murmur");
        return parseHashType(name);
    }
    
    public static Hash getInstance(final int type) {
        switch (type) {
            case 0: {
                return JenkinsHash.getInstance();
            }
            case 1: {
                return MurmurHash.getInstance();
            }
            default: {
                return null;
            }
        }
    }
    
    public static Hash getInstance(final Configuration conf) {
        final int type = getHashType(conf);
        return getInstance(type);
    }
    
    public int hash(final byte[] bytes) {
        return this.hash(bytes, bytes.length, -1);
    }
    
    public int hash(final byte[] bytes, final int initval) {
        return this.hash(bytes, bytes.length, initval);
    }
    
    public abstract int hash(final byte[] p0, final int p1, final int p2);
}
