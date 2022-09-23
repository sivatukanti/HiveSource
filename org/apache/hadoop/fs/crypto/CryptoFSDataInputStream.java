// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.crypto;

import java.io.IOException;
import java.io.InputStream;
import org.apache.hadoop.crypto.CryptoInputStream;
import org.apache.hadoop.crypto.CryptoCodec;
import org.apache.hadoop.fs.FSDataInputStream;

public class CryptoFSDataInputStream extends FSDataInputStream
{
    public CryptoFSDataInputStream(final FSDataInputStream in, final CryptoCodec codec, final int bufferSize, final byte[] key, final byte[] iv) throws IOException {
        super(new CryptoInputStream(in, codec, bufferSize, key, iv));
    }
    
    public CryptoFSDataInputStream(final FSDataInputStream in, final CryptoCodec codec, final byte[] key, final byte[] iv) throws IOException {
        super(new CryptoInputStream(in, codec, key, iv));
    }
}
