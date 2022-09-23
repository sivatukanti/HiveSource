// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.crypto.cksum.provider;

import org.apache.kerby.kerberos.kerb.crypto.util.Crc32;

public class Crc32Provider extends AbstractHashProvider
{
    private byte[] output;
    
    public Crc32Provider() {
        super(4, 1);
    }
    
    @Override
    public void hash(final byte[] data, final int start, final int size) {
        this.output = Crc32.crc(data, start, size);
    }
    
    @Override
    public byte[] output() {
        return this.output.clone();
    }
}
