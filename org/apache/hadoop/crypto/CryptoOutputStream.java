// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto;

import com.google.common.base.Preconditions;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.fs.StreamCapabilities;
import org.apache.hadoop.fs.CanSetDropBehind;
import org.apache.hadoop.fs.Syncable;
import java.io.FilterOutputStream;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class CryptoOutputStream extends FilterOutputStream implements Syncable, CanSetDropBehind, StreamCapabilities
{
    private final byte[] oneByteBuf;
    private final CryptoCodec codec;
    private final Encryptor encryptor;
    private final int bufferSize;
    private ByteBuffer inBuffer;
    private ByteBuffer outBuffer;
    private long streamOffset;
    private byte padding;
    private boolean closed;
    private final byte[] key;
    private final byte[] initIV;
    private byte[] iv;
    private boolean closeOutputStream;
    private byte[] tmpBuf;
    
    public CryptoOutputStream(final OutputStream out, final CryptoCodec codec, final int bufferSize, final byte[] key, final byte[] iv) throws IOException {
        this(out, codec, bufferSize, key, iv, 0L);
    }
    
    public CryptoOutputStream(final OutputStream out, final CryptoCodec codec, final int bufferSize, final byte[] key, final byte[] iv, final long streamOffset) throws IOException {
        this(out, codec, bufferSize, key, iv, streamOffset, true);
    }
    
    public CryptoOutputStream(final OutputStream out, final CryptoCodec codec, final int bufferSize, final byte[] key, final byte[] iv, final long streamOffset, final boolean closeOutputStream) throws IOException {
        super(out);
        this.oneByteBuf = new byte[1];
        this.streamOffset = 0L;
        CryptoStreamUtils.checkCodec(codec);
        this.bufferSize = CryptoStreamUtils.checkBufferSize(codec, bufferSize);
        this.codec = codec;
        this.key = key.clone();
        this.initIV = iv.clone();
        this.iv = iv.clone();
        this.inBuffer = ByteBuffer.allocateDirect(this.bufferSize);
        this.outBuffer = ByteBuffer.allocateDirect(this.bufferSize);
        this.streamOffset = streamOffset;
        this.closeOutputStream = closeOutputStream;
        try {
            this.encryptor = codec.createEncryptor();
        }
        catch (GeneralSecurityException e) {
            throw new IOException(e);
        }
        this.updateEncryptor();
    }
    
    public CryptoOutputStream(final OutputStream out, final CryptoCodec codec, final byte[] key, final byte[] iv) throws IOException {
        this(out, codec, key, iv, 0L);
    }
    
    public CryptoOutputStream(final OutputStream out, final CryptoCodec codec, final byte[] key, final byte[] iv, final long streamOffset) throws IOException {
        this(out, codec, key, iv, streamOffset, true);
    }
    
    public CryptoOutputStream(final OutputStream out, final CryptoCodec codec, final byte[] key, final byte[] iv, final long streamOffset, final boolean closeOutputStream) throws IOException {
        this(out, codec, CryptoStreamUtils.getBufferSize(codec.getConf()), key, iv, streamOffset, closeOutputStream);
    }
    
    public OutputStream getWrappedStream() {
        return this.out;
    }
    
    @Override
    public synchronized void write(final byte[] b, int off, int len) throws IOException {
        this.checkStream();
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off > b.length || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        while (len > 0) {
            final int remaining = this.inBuffer.remaining();
            if (len < remaining) {
                this.inBuffer.put(b, off, len);
                len = 0;
            }
            else {
                this.inBuffer.put(b, off, remaining);
                off += remaining;
                len -= remaining;
                this.encrypt();
            }
        }
    }
    
    private void encrypt() throws IOException {
        Preconditions.checkState(this.inBuffer.position() >= this.padding);
        if (this.inBuffer.position() == this.padding) {
            return;
        }
        this.inBuffer.flip();
        this.outBuffer.clear();
        this.encryptor.encrypt(this.inBuffer, this.outBuffer);
        this.inBuffer.clear();
        this.outBuffer.flip();
        if (this.padding > 0) {
            this.outBuffer.position((int)this.padding);
            this.padding = 0;
        }
        final int len = this.outBuffer.remaining();
        final byte[] tmp = this.getTmpBuf();
        this.outBuffer.get(tmp, 0, len);
        this.out.write(tmp, 0, len);
        this.streamOffset += len;
        if (this.encryptor.isContextReset()) {
            this.updateEncryptor();
        }
    }
    
    private void updateEncryptor() throws IOException {
        final long counter = this.streamOffset / this.codec.getCipherSuite().getAlgorithmBlockSize();
        this.padding = (byte)(this.streamOffset % this.codec.getCipherSuite().getAlgorithmBlockSize());
        this.inBuffer.position((int)this.padding);
        this.codec.calculateIV(this.initIV, counter, this.iv);
        this.encryptor.init(this.key, this.iv);
    }
    
    private byte[] getTmpBuf() {
        if (this.tmpBuf == null) {
            this.tmpBuf = new byte[this.bufferSize];
        }
        return this.tmpBuf;
    }
    
    @Override
    public synchronized void close() throws IOException {
        if (this.closed) {
            return;
        }
        try {
            this.flush();
            if (this.closeOutputStream) {
                super.close();
                this.codec.close();
            }
            this.freeBuffers();
        }
        finally {
            this.closed = true;
        }
    }
    
    @Override
    public synchronized void flush() throws IOException {
        if (this.closed) {
            return;
        }
        this.encrypt();
        super.flush();
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.oneByteBuf[0] = (byte)(b & 0xFF);
        this.write(this.oneByteBuf, 0, this.oneByteBuf.length);
    }
    
    private void checkStream() throws IOException {
        if (this.closed) {
            throw new IOException("Stream closed");
        }
    }
    
    @Override
    public void setDropBehind(final Boolean dropCache) throws IOException, UnsupportedOperationException {
        try {
            ((CanSetDropBehind)this.out).setDropBehind(dropCache);
        }
        catch (ClassCastException e) {
            throw new UnsupportedOperationException("This stream does not support setting the drop-behind caching.");
        }
    }
    
    @Override
    public void hflush() throws IOException {
        this.flush();
        if (this.out instanceof Syncable) {
            ((Syncable)this.out).hflush();
        }
    }
    
    @Override
    public void hsync() throws IOException {
        this.flush();
        if (this.out instanceof Syncable) {
            ((Syncable)this.out).hsync();
        }
    }
    
    private void freeBuffers() {
        CryptoStreamUtils.freeDB(this.inBuffer);
        CryptoStreamUtils.freeDB(this.outBuffer);
    }
    
    @Override
    public boolean hasCapability(final String capability) {
        return this.out instanceof StreamCapabilities && ((StreamCapabilities)this.out).hasCapability(capability);
    }
}
