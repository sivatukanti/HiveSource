// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto;

import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class CipherOption
{
    private final CipherSuite suite;
    private final byte[] inKey;
    private final byte[] inIv;
    private final byte[] outKey;
    private final byte[] outIv;
    
    public CipherOption(final CipherSuite suite) {
        this(suite, null, null, null, null);
    }
    
    public CipherOption(final CipherSuite suite, final byte[] inKey, final byte[] inIv, final byte[] outKey, final byte[] outIv) {
        this.suite = suite;
        this.inKey = inKey;
        this.inIv = inIv;
        this.outKey = outKey;
        this.outIv = outIv;
    }
    
    public CipherSuite getCipherSuite() {
        return this.suite;
    }
    
    public byte[] getInKey() {
        return this.inKey;
    }
    
    public byte[] getInIv() {
        return this.inIv;
    }
    
    public byte[] getOutKey() {
        return this.outKey;
    }
    
    public byte[] getOutIv() {
        return this.outIv;
    }
}
