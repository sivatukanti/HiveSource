// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto;

import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.fs.StreamCapabilitiesPolicy;
import java.security.GeneralSecurityException;
import java.io.FileInputStream;
import java.io.FileDescriptor;
import org.apache.hadoop.fs.ReadOption;
import java.util.EnumSet;
import org.apache.hadoop.io.ByteBufferPool;
import java.io.EOFException;
import com.google.common.base.Preconditions;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.nio.ByteBuffer;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.fs.StreamCapabilities;
import org.apache.hadoop.fs.CanUnbuffer;
import java.nio.channels.ReadableByteChannel;
import org.apache.hadoop.fs.HasEnhancedByteBufferAccess;
import org.apache.hadoop.fs.CanSetReadahead;
import org.apache.hadoop.fs.CanSetDropBehind;
import org.apache.hadoop.fs.HasFileDescriptor;
import org.apache.hadoop.fs.ByteBufferReadable;
import org.apache.hadoop.fs.PositionedReadable;
import org.apache.hadoop.fs.Seekable;
import java.io.FilterInputStream;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class CryptoInputStream extends FilterInputStream implements Seekable, PositionedReadable, ByteBufferReadable, HasFileDescriptor, CanSetDropBehind, CanSetReadahead, HasEnhancedByteBufferAccess, ReadableByteChannel, CanUnbuffer, StreamCapabilities
{
    private final byte[] oneByteBuf;
    private final CryptoCodec codec;
    private final Decryptor decryptor;
    private final int bufferSize;
    private ByteBuffer inBuffer;
    private ByteBuffer outBuffer;
    private long streamOffset;
    private Boolean usingByteBufferRead;
    private byte padding;
    private boolean closed;
    private final byte[] key;
    private final byte[] initIV;
    private byte[] iv;
    private final boolean isByteBufferReadable;
    private final boolean isReadableByteChannel;
    private final Queue<ByteBuffer> bufferPool;
    private final Queue<Decryptor> decryptorPool;
    private byte[] tmpBuf;
    
    public CryptoInputStream(final InputStream in, final CryptoCodec codec, final int bufferSize, final byte[] key, final byte[] iv) throws IOException {
        this(in, codec, bufferSize, key, iv, CryptoStreamUtils.getInputStreamOffset(in));
    }
    
    public CryptoInputStream(final InputStream in, final CryptoCodec codec, final int bufferSize, final byte[] key, final byte[] iv, final long streamOffset) throws IOException {
        super(in);
        this.oneByteBuf = new byte[1];
        this.streamOffset = 0L;
        this.usingByteBufferRead = null;
        this.bufferPool = new ConcurrentLinkedQueue<ByteBuffer>();
        this.decryptorPool = new ConcurrentLinkedQueue<Decryptor>();
        CryptoStreamUtils.checkCodec(codec);
        this.bufferSize = CryptoStreamUtils.checkBufferSize(codec, bufferSize);
        this.codec = codec;
        this.key = key.clone();
        this.initIV = iv.clone();
        this.iv = iv.clone();
        this.streamOffset = streamOffset;
        this.isByteBufferReadable = (in instanceof ByteBufferReadable);
        this.isReadableByteChannel = (in instanceof ReadableByteChannel);
        this.inBuffer = ByteBuffer.allocateDirect(this.bufferSize);
        this.outBuffer = ByteBuffer.allocateDirect(this.bufferSize);
        this.decryptor = this.getDecryptor();
        this.resetStreamOffset(streamOffset);
    }
    
    public CryptoInputStream(final InputStream in, final CryptoCodec codec, final byte[] key, final byte[] iv) throws IOException {
        this(in, codec, CryptoStreamUtils.getBufferSize(codec.getConf()), key, iv);
    }
    
    public InputStream getWrappedStream() {
        return this.in;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        this.checkStream();
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        final int remaining = this.outBuffer.remaining();
        if (remaining > 0) {
            final int n = Math.min(len, remaining);
            this.outBuffer.get(b, off, n);
            return n;
        }
        int n = 0;
        if (this.usingByteBufferRead == null) {
            Label_0180: {
                if (!this.isByteBufferReadable) {
                    if (!this.isReadableByteChannel) {
                        this.usingByteBufferRead = Boolean.FALSE;
                        break Label_0180;
                    }
                }
                try {
                    n = (this.isByteBufferReadable ? ((ByteBufferReadable)this.in).read(this.inBuffer) : ((ReadableByteChannel)this.in).read(this.inBuffer));
                    this.usingByteBufferRead = Boolean.TRUE;
                }
                catch (UnsupportedOperationException e) {
                    this.usingByteBufferRead = Boolean.FALSE;
                }
            }
            if (!this.usingByteBufferRead) {
                n = this.readFromUnderlyingStream(this.inBuffer);
            }
        }
        else if (this.usingByteBufferRead) {
            n = (this.isByteBufferReadable ? ((ByteBufferReadable)this.in).read(this.inBuffer) : ((ReadableByteChannel)this.in).read(this.inBuffer));
        }
        else {
            n = this.readFromUnderlyingStream(this.inBuffer);
        }
        if (n <= 0) {
            return n;
        }
        this.streamOffset += n;
        this.decrypt(this.decryptor, this.inBuffer, this.outBuffer, this.padding);
        this.padding = this.afterDecryption(this.decryptor, this.inBuffer, this.streamOffset, this.iv);
        n = Math.min(len, this.outBuffer.remaining());
        this.outBuffer.get(b, off, n);
        return n;
    }
    
    private int readFromUnderlyingStream(final ByteBuffer inBuffer) throws IOException {
        final int toRead = inBuffer.remaining();
        final byte[] tmp = this.getTmpBuf();
        final int n = this.in.read(tmp, 0, toRead);
        if (n > 0) {
            inBuffer.put(tmp, 0, n);
        }
        return n;
    }
    
    private byte[] getTmpBuf() {
        if (this.tmpBuf == null) {
            this.tmpBuf = new byte[this.bufferSize];
        }
        return this.tmpBuf;
    }
    
    private void decrypt(final Decryptor decryptor, final ByteBuffer inBuffer, final ByteBuffer outBuffer, final byte padding) throws IOException {
        Preconditions.checkState(inBuffer.position() >= padding);
        if (inBuffer.position() == padding) {
            return;
        }
        inBuffer.flip();
        outBuffer.clear();
        decryptor.decrypt(inBuffer, outBuffer);
        inBuffer.clear();
        outBuffer.flip();
        if (padding > 0) {
            outBuffer.position((int)padding);
        }
    }
    
    private byte afterDecryption(final Decryptor decryptor, final ByteBuffer inBuffer, final long position, final byte[] iv) throws IOException {
        byte padding = 0;
        if (decryptor.isContextReset()) {
            this.updateDecryptor(decryptor, position, iv);
            padding = this.getPadding(position);
            inBuffer.position((int)padding);
        }
        return padding;
    }
    
    private long getCounter(final long position) {
        return position / this.codec.getCipherSuite().getAlgorithmBlockSize();
    }
    
    private byte getPadding(final long position) {
        return (byte)(position % this.codec.getCipherSuite().getAlgorithmBlockSize());
    }
    
    private void updateDecryptor(final Decryptor decryptor, final long position, final byte[] iv) throws IOException {
        final long counter = this.getCounter(position);
        this.codec.calculateIV(this.initIV, counter, iv);
        decryptor.init(this.key, iv);
    }
    
    private void resetStreamOffset(final long offset) throws IOException {
        this.streamOffset = offset;
        this.inBuffer.clear();
        this.outBuffer.clear();
        this.outBuffer.limit(0);
        this.updateDecryptor(this.decryptor, offset, this.iv);
        this.padding = this.getPadding(offset);
        this.inBuffer.position((int)this.padding);
    }
    
    @Override
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        super.close();
        this.freeBuffers();
        this.codec.close();
        this.closed = true;
    }
    
    @Override
    public int read(final long position, final byte[] buffer, final int offset, final int length) throws IOException {
        this.checkStream();
        try {
            final int n = ((PositionedReadable)this.in).read(position, buffer, offset, length);
            if (n > 0) {
                this.decrypt(position, buffer, offset, n);
            }
            return n;
        }
        catch (ClassCastException e) {
            throw new UnsupportedOperationException("This stream does not support positioned read.");
        }
    }
    
    private void decrypt(final long position, final byte[] buffer, final int offset, final int length) throws IOException {
        final ByteBuffer inBuffer = this.getBuffer();
        final ByteBuffer outBuffer = this.getBuffer();
        Decryptor decryptor = null;
        try {
            decryptor = this.getDecryptor();
            final byte[] iv = this.initIV.clone();
            this.updateDecryptor(decryptor, position, iv);
            byte padding = this.getPadding(position);
            inBuffer.position((int)padding);
            int toDecrypt;
            for (int n = 0; n < length; n += toDecrypt, padding = this.afterDecryption(decryptor, inBuffer, position + n, iv)) {
                toDecrypt = Math.min(length - n, inBuffer.remaining());
                inBuffer.put(buffer, offset + n, toDecrypt);
                this.decrypt(decryptor, inBuffer, outBuffer, padding);
                outBuffer.get(buffer, offset + n, toDecrypt);
            }
        }
        finally {
            this.returnBuffer(inBuffer);
            this.returnBuffer(outBuffer);
            this.returnDecryptor(decryptor);
        }
    }
    
    @Override
    public void readFully(final long position, final byte[] buffer, final int offset, final int length) throws IOException {
        this.checkStream();
        try {
            ((PositionedReadable)this.in).readFully(position, buffer, offset, length);
            if (length > 0) {
                this.decrypt(position, buffer, offset, length);
            }
        }
        catch (ClassCastException e) {
            throw new UnsupportedOperationException("This stream does not support positioned readFully.");
        }
    }
    
    @Override
    public void readFully(final long position, final byte[] buffer) throws IOException {
        this.readFully(position, buffer, 0, buffer.length);
    }
    
    @Override
    public void seek(final long pos) throws IOException {
        if (pos < 0L) {
            throw new EOFException("Cannot seek to a negative offset");
        }
        this.checkStream();
        try {
            if (pos <= this.streamOffset && pos >= this.streamOffset - this.outBuffer.remaining()) {
                final int forward = (int)(pos - (this.streamOffset - this.outBuffer.remaining()));
                if (forward > 0) {
                    this.outBuffer.position(this.outBuffer.position() + forward);
                }
            }
            else {
                ((Seekable)this.in).seek(pos);
                this.resetStreamOffset(pos);
            }
        }
        catch (ClassCastException e) {
            throw new UnsupportedOperationException("This stream does not support seek.");
        }
    }
    
    @Override
    public long skip(long n) throws IOException {
        Preconditions.checkArgument(n >= 0L, (Object)"Negative skip length.");
        this.checkStream();
        if (n == 0L) {
            return 0L;
        }
        if (n <= this.outBuffer.remaining()) {
            final int pos = this.outBuffer.position() + (int)n;
            this.outBuffer.position(pos);
            return n;
        }
        n -= this.outBuffer.remaining();
        long skipped = this.in.skip(n);
        if (skipped < 0L) {
            skipped = 0L;
        }
        final long pos2 = this.streamOffset + skipped;
        skipped += this.outBuffer.remaining();
        this.resetStreamOffset(pos2);
        return skipped;
    }
    
    @Override
    public long getPos() throws IOException {
        this.checkStream();
        return this.streamOffset - this.outBuffer.remaining();
    }
    
    @Override
    public int read(final ByteBuffer buf) throws IOException {
        this.checkStream();
        if (!this.isByteBufferReadable && !this.isReadableByteChannel) {
            int n = 0;
            if (buf.hasArray()) {
                n = this.read(buf.array(), buf.position(), buf.remaining());
                if (n > 0) {
                    buf.position(buf.position() + n);
                }
            }
            else {
                final byte[] tmp = new byte[buf.remaining()];
                n = this.read(tmp);
                if (n > 0) {
                    buf.put(tmp, 0, n);
                }
            }
            return n;
        }
        final int unread = this.outBuffer.remaining();
        if (unread > 0) {
            final int toRead = buf.remaining();
            if (toRead <= unread) {
                final int limit = this.outBuffer.limit();
                this.outBuffer.limit(this.outBuffer.position() + toRead);
                buf.put(this.outBuffer);
                this.outBuffer.limit(limit);
                return toRead;
            }
            buf.put(this.outBuffer);
        }
        final int pos = buf.position();
        final int n2 = this.isByteBufferReadable ? ((ByteBufferReadable)this.in).read(buf) : ((ReadableByteChannel)this.in).read(buf);
        if (n2 > 0) {
            this.streamOffset += n2;
            this.decrypt(buf, n2, pos);
        }
        if (n2 >= 0) {
            return unread + n2;
        }
        if (unread == 0) {
            return -1;
        }
        return unread;
    }
    
    private void decrypt(final ByteBuffer buf, final int n, final int start) throws IOException {
        final int pos = buf.position();
        final int limit = buf.limit();
        int len = 0;
        while (len < n) {
            buf.position(start + len);
            buf.limit(start + len + Math.min(n - len, this.inBuffer.remaining()));
            this.inBuffer.put(buf);
            try {
                this.decrypt(this.decryptor, this.inBuffer, this.outBuffer, this.padding);
                buf.position(start + len);
                buf.limit(limit);
                len += this.outBuffer.remaining();
                buf.put(this.outBuffer);
            }
            finally {
                this.padding = this.afterDecryption(this.decryptor, this.inBuffer, this.streamOffset - (n - len), this.iv);
            }
        }
        buf.position(pos);
    }
    
    @Override
    public int available() throws IOException {
        this.checkStream();
        return this.in.available() + this.outBuffer.remaining();
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public void mark(final int readLimit) {
    }
    
    @Override
    public void reset() throws IOException {
        throw new IOException("Mark/reset not supported");
    }
    
    @Override
    public boolean seekToNewSource(final long targetPos) throws IOException {
        Preconditions.checkArgument(targetPos >= 0L, (Object)"Cannot seek to negative offset.");
        this.checkStream();
        try {
            final boolean result = ((Seekable)this.in).seekToNewSource(targetPos);
            this.resetStreamOffset(targetPos);
            return result;
        }
        catch (ClassCastException e) {
            throw new UnsupportedOperationException("This stream does not support seekToNewSource.");
        }
    }
    
    @Override
    public ByteBuffer read(final ByteBufferPool bufferPool, final int maxLength, final EnumSet<ReadOption> opts) throws IOException, UnsupportedOperationException {
        this.checkStream();
        try {
            if (this.outBuffer.remaining() > 0) {
                ((Seekable)this.in).seek(this.getPos());
                this.resetStreamOffset(this.getPos());
            }
            final ByteBuffer buffer = ((HasEnhancedByteBufferAccess)this.in).read(bufferPool, maxLength, opts);
            if (buffer != null) {
                final int n = buffer.remaining();
                if (n > 0) {
                    this.streamOffset += buffer.remaining();
                    final int pos = buffer.position();
                    this.decrypt(buffer, n, pos);
                }
            }
            return buffer;
        }
        catch (ClassCastException e) {
            throw new UnsupportedOperationException("This stream does not support enhanced byte buffer access.");
        }
    }
    
    @Override
    public void releaseBuffer(final ByteBuffer buffer) {
        try {
            ((HasEnhancedByteBufferAccess)this.in).releaseBuffer(buffer);
        }
        catch (ClassCastException e) {
            throw new UnsupportedOperationException("This stream does not support release buffer.");
        }
    }
    
    @Override
    public void setReadahead(final Long readahead) throws IOException, UnsupportedOperationException {
        try {
            ((CanSetReadahead)this.in).setReadahead(readahead);
        }
        catch (ClassCastException e) {
            throw new UnsupportedOperationException("This stream does not support setting the readahead caching strategy.");
        }
    }
    
    @Override
    public void setDropBehind(final Boolean dropCache) throws IOException, UnsupportedOperationException {
        try {
            ((CanSetDropBehind)this.in).setDropBehind(dropCache);
        }
        catch (ClassCastException e) {
            throw new UnsupportedOperationException("This stream does not support setting the drop-behind caching setting.");
        }
    }
    
    @Override
    public FileDescriptor getFileDescriptor() throws IOException {
        if (this.in instanceof HasFileDescriptor) {
            return ((HasFileDescriptor)this.in).getFileDescriptor();
        }
        if (this.in instanceof FileInputStream) {
            return ((FileInputStream)this.in).getFD();
        }
        return null;
    }
    
    @Override
    public int read() throws IOException {
        return (this.read(this.oneByteBuf, 0, 1) == -1) ? -1 : (this.oneByteBuf[0] & 0xFF);
    }
    
    private void checkStream() throws IOException {
        if (this.closed) {
            throw new IOException("Stream closed");
        }
    }
    
    private ByteBuffer getBuffer() {
        ByteBuffer buffer = this.bufferPool.poll();
        if (buffer == null) {
            buffer = ByteBuffer.allocateDirect(this.bufferSize);
        }
        return buffer;
    }
    
    private void returnBuffer(final ByteBuffer buf) {
        if (buf != null) {
            buf.clear();
            this.bufferPool.add(buf);
        }
    }
    
    private void freeBuffers() {
        CryptoStreamUtils.freeDB(this.inBuffer);
        CryptoStreamUtils.freeDB(this.outBuffer);
        this.cleanBufferPool();
    }
    
    private void cleanBufferPool() {
        ByteBuffer buf;
        while ((buf = this.bufferPool.poll()) != null) {
            CryptoStreamUtils.freeDB(buf);
        }
    }
    
    private Decryptor getDecryptor() throws IOException {
        Decryptor decryptor = this.decryptorPool.poll();
        if (decryptor == null) {
            try {
                decryptor = this.codec.createDecryptor();
            }
            catch (GeneralSecurityException e) {
                throw new IOException(e);
            }
        }
        return decryptor;
    }
    
    private void returnDecryptor(final Decryptor decryptor) {
        if (decryptor != null) {
            this.decryptorPool.add(decryptor);
        }
    }
    
    @Override
    public boolean isOpen() {
        return !this.closed;
    }
    
    private void cleanDecryptorPool() {
        this.decryptorPool.clear();
    }
    
    @Override
    public void unbuffer() {
        this.cleanBufferPool();
        this.cleanDecryptorPool();
        StreamCapabilitiesPolicy.unbuffer(this.in);
    }
    
    @Override
    public boolean hasCapability(final String capability) {
        final String lowerCase = StringUtils.toLowerCase(capability);
        switch (lowerCase) {
            case "in:readahead":
            case "dropbehind":
            case "in:unbuffer": {
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
