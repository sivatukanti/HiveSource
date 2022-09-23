// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.crypto;

import org.apache.hadoop.fs.FileSystem;
import java.io.OutputStream;
import org.apache.hadoop.crypto.CryptoOutputStream;
import java.io.IOException;
import org.apache.hadoop.crypto.CryptoCodec;
import org.apache.hadoop.fs.FSDataOutputStream;

public class CryptoFSDataOutputStream extends FSDataOutputStream
{
    private final FSDataOutputStream fsOut;
    
    public CryptoFSDataOutputStream(final FSDataOutputStream out, final CryptoCodec codec, final int bufferSize, final byte[] key, final byte[] iv) throws IOException {
        this(out, codec, bufferSize, key, iv, true);
    }
    
    public CryptoFSDataOutputStream(final FSDataOutputStream out, final CryptoCodec codec, final int bufferSize, final byte[] key, final byte[] iv, final boolean closeOutputStream) throws IOException {
        super(new CryptoOutputStream(out, codec, bufferSize, key, iv, out.getPos(), closeOutputStream), null, out.getPos());
        this.fsOut = out;
    }
    
    public CryptoFSDataOutputStream(final FSDataOutputStream out, final CryptoCodec codec, final byte[] key, final byte[] iv) throws IOException {
        super(new CryptoOutputStream(out, codec, key, iv, out.getPos()), null, out.getPos());
        this.fsOut = out;
    }
    
    @Override
    public long getPos() {
        return this.fsOut.getPos();
    }
}
