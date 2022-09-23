// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum.provider;

import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.crypto.cksum.HashProvider;

public abstract class AbstractHashProvider implements HashProvider
{
    private int blockSize;
    private int hashSize;
    
    public AbstractHashProvider(final int hashSize, final int blockSize) {
        this.hashSize = hashSize;
        this.blockSize = blockSize;
    }
    
    protected void init() {
    }
    
    @Override
    public int hashSize() {
        return this.hashSize;
    }
    
    @Override
    public int blockSize() {
        return this.blockSize;
    }
    
    @Override
    public void hash(final byte[] data) throws KrbException {
        this.hash(data, 0, data.length);
    }
}
