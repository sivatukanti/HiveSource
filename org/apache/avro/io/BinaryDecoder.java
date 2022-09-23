// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io;

import org.apache.avro.AvroRuntimeException;
import java.nio.ByteBuffer;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.avro.util.Utf8;

public class BinaryDecoder extends Decoder
{
    private ByteSource source;
    private byte[] buf;
    private int minPos;
    private int pos;
    private int limit;
    private final Utf8 scratchUtf8;
    
    byte[] getBuf() {
        return this.buf;
    }
    
    int getPos() {
        return this.pos;
    }
    
    int getLimit() {
        return this.limit;
    }
    
    void setBuf(final byte[] buf, final int pos, final int len) {
        this.buf = buf;
        this.pos = pos;
        this.limit = pos + len;
    }
    
    void clearBuf() {
        this.buf = null;
    }
    
    protected BinaryDecoder() {
        this.source = null;
        this.buf = null;
        this.minPos = 0;
        this.pos = 0;
        this.limit = 0;
        this.scratchUtf8 = new Utf8();
    }
    
    BinaryDecoder(final InputStream in, final int bufferSize) {
        this.source = null;
        this.buf = null;
        this.minPos = 0;
        this.pos = 0;
        this.limit = 0;
        this.scratchUtf8 = new Utf8();
        this.configure(in, bufferSize);
    }
    
    BinaryDecoder(final byte[] data, final int offset, final int length) {
        this.source = null;
        this.buf = null;
        this.minPos = 0;
        this.pos = 0;
        this.limit = 0;
        this.scratchUtf8 = new Utf8();
        this.configure(data, offset, length);
    }
    
    BinaryDecoder configure(final InputStream in, final int bufferSize) {
        this.configureSource(bufferSize, new InputStreamByteSource(in));
        return this;
    }
    
    BinaryDecoder configure(final byte[] data, final int offset, final int length) {
        this.configureSource(8192, new ByteArrayByteSource(data, offset, length));
        return this;
    }
    
    private void configureSource(final int bufferSize, final ByteSource source) {
        if (null != this.source) {
            this.source.detach();
        }
        source.attach(bufferSize, this);
        this.source = source;
    }
    
    @Override
    public void readNull() throws IOException {
    }
    
    @Override
    public boolean readBoolean() throws IOException {
        if (this.limit == this.pos) {
            this.limit = this.source.tryReadRaw(this.buf, 0, this.buf.length);
            this.pos = 0;
            if (this.limit == 0) {
                throw new EOFException();
            }
        }
        final int n = this.buf[this.pos++] & 0xFF;
        return n == 1;
    }
    
    @Override
    public int readInt() throws IOException {
        this.ensureBounds(5);
        int len = 1;
        int b = this.buf[this.pos] & 0xFF;
        int n = b & 0x7F;
        if (b > 127) {
            b = (this.buf[this.pos + len++] & 0xFF);
            n ^= (b & 0x7F) << 7;
            if (b > 127) {
                b = (this.buf[this.pos + len++] & 0xFF);
                n ^= (b & 0x7F) << 14;
                if (b > 127) {
                    b = (this.buf[this.pos + len++] & 0xFF);
                    n ^= (b & 0x7F) << 21;
                    if (b > 127) {
                        b = (this.buf[this.pos + len++] & 0xFF);
                        n ^= (b & 0x7F) << 28;
                        if (b > 127) {
                            throw new IOException("Invalid int encoding");
                        }
                    }
                }
            }
        }
        this.pos += len;
        if (this.pos > this.limit) {
            throw new EOFException();
        }
        return n >>> 1 ^ -(n & 0x1);
    }
    
    @Override
    public long readLong() throws IOException {
        this.ensureBounds(10);
        int b = this.buf[this.pos++] & 0xFF;
        int n = b & 0x7F;
        long l;
        if (b > 127) {
            b = (this.buf[this.pos++] & 0xFF);
            n ^= (b & 0x7F) << 7;
            if (b > 127) {
                b = (this.buf[this.pos++] & 0xFF);
                n ^= (b & 0x7F) << 14;
                if (b > 127) {
                    b = (this.buf[this.pos++] & 0xFF);
                    n ^= (b & 0x7F) << 21;
                    if (b > 127) {
                        l = this.innerLongDecode(n);
                    }
                    else {
                        l = n;
                    }
                }
                else {
                    l = n;
                }
            }
            else {
                l = n;
            }
        }
        else {
            l = n;
        }
        if (this.pos > this.limit) {
            throw new EOFException();
        }
        return l >>> 1 ^ -(l & 0x1L);
    }
    
    private long innerLongDecode(long l) throws IOException {
        int len = 1;
        int b = this.buf[this.pos] & 0xFF;
        l ^= ((long)b & 0x7FL) << 28;
        if (b > 127) {
            b = (this.buf[this.pos + len++] & 0xFF);
            l ^= ((long)b & 0x7FL) << 35;
            if (b > 127) {
                b = (this.buf[this.pos + len++] & 0xFF);
                l ^= ((long)b & 0x7FL) << 42;
                if (b > 127) {
                    b = (this.buf[this.pos + len++] & 0xFF);
                    l ^= ((long)b & 0x7FL) << 49;
                    if (b > 127) {
                        b = (this.buf[this.pos + len++] & 0xFF);
                        l ^= ((long)b & 0x7FL) << 56;
                        if (b > 127) {
                            b = (this.buf[this.pos + len++] & 0xFF);
                            l ^= ((long)b & 0x7FL) << 63;
                            if (b > 127) {
                                throw new IOException("Invalid long encoding");
                            }
                        }
                    }
                }
            }
        }
        this.pos += len;
        return l;
    }
    
    @Override
    public float readFloat() throws IOException {
        this.ensureBounds(4);
        int len = 1;
        final int n = (this.buf[this.pos] & 0xFF) | (this.buf[this.pos + len++] & 0xFF) << 8 | (this.buf[this.pos + len++] & 0xFF) << 16 | (this.buf[this.pos + len++] & 0xFF) << 24;
        if (this.pos + 4 > this.limit) {
            throw new EOFException();
        }
        this.pos += 4;
        return Float.intBitsToFloat(n);
    }
    
    @Override
    public double readDouble() throws IOException {
        this.ensureBounds(8);
        int len = 1;
        final int n1 = (this.buf[this.pos] & 0xFF) | (this.buf[this.pos + len++] & 0xFF) << 8 | (this.buf[this.pos + len++] & 0xFF) << 16 | (this.buf[this.pos + len++] & 0xFF) << 24;
        final int n2 = (this.buf[this.pos + len++] & 0xFF) | (this.buf[this.pos + len++] & 0xFF) << 8 | (this.buf[this.pos + len++] & 0xFF) << 16 | (this.buf[this.pos + len++] & 0xFF) << 24;
        if (this.pos + 8 > this.limit) {
            throw new EOFException();
        }
        this.pos += 8;
        return Double.longBitsToDouble(((long)n1 & 0xFFFFFFFFL) | (long)n2 << 32);
    }
    
    @Override
    public Utf8 readString(final Utf8 old) throws IOException {
        final int length = this.readInt();
        final Utf8 result = (old != null) ? old : new Utf8();
        result.setByteLength(length);
        if (0 != length) {
            this.doReadBytes(result.getBytes(), 0, length);
        }
        return result;
    }
    
    @Override
    public String readString() throws IOException {
        return this.readString(this.scratchUtf8).toString();
    }
    
    @Override
    public void skipString() throws IOException {
        this.doSkipBytes(this.readInt());
    }
    
    @Override
    public ByteBuffer readBytes(final ByteBuffer old) throws IOException {
        final int length = this.readInt();
        ByteBuffer result;
        if (old != null && length <= old.capacity()) {
            result = old;
            result.clear();
        }
        else {
            result = ByteBuffer.allocate(length);
        }
        this.doReadBytes(result.array(), result.position(), length);
        result.limit(length);
        return result;
    }
    
    @Override
    public void skipBytes() throws IOException {
        this.doSkipBytes(this.readInt());
    }
    
    @Override
    public void readFixed(final byte[] bytes, final int start, final int length) throws IOException {
        this.doReadBytes(bytes, start, length);
    }
    
    @Override
    public void skipFixed(final int length) throws IOException {
        this.doSkipBytes(length);
    }
    
    @Override
    public int readEnum() throws IOException {
        return this.readInt();
    }
    
    protected void doSkipBytes(long length) throws IOException {
        final int remaining = this.limit - this.pos;
        if (length <= remaining) {
            this.pos += (int)length;
        }
        else {
            final int n = 0;
            this.pos = n;
            this.limit = n;
            length -= remaining;
            this.source.skipSourceBytes(length);
        }
    }
    
    protected void doReadBytes(final byte[] bytes, int start, int length) throws IOException {
        if (length < 0) {
            throw new AvroRuntimeException("Malformed data. Length is negative: " + length);
        }
        final int remaining = this.limit - this.pos;
        if (length <= remaining) {
            System.arraycopy(this.buf, this.pos, bytes, start, length);
            this.pos += length;
        }
        else {
            System.arraycopy(this.buf, this.pos, bytes, start, remaining);
            start += remaining;
            length -= remaining;
            this.pos = this.limit;
            this.source.readRaw(bytes, start, length);
        }
    }
    
    protected long doReadItemCount() throws IOException {
        long result = this.readLong();
        if (result < 0L) {
            this.readLong();
            result = -result;
        }
        return result;
    }
    
    private long doSkipItems() throws IOException {
        long result;
        for (result = this.readInt(); result < 0L; result = this.readInt()) {
            final long bytecount = this.readLong();
            this.doSkipBytes(bytecount);
        }
        return result;
    }
    
    @Override
    public long readArrayStart() throws IOException {
        return this.doReadItemCount();
    }
    
    @Override
    public long arrayNext() throws IOException {
        return this.doReadItemCount();
    }
    
    @Override
    public long skipArray() throws IOException {
        return this.doSkipItems();
    }
    
    @Override
    public long readMapStart() throws IOException {
        return this.doReadItemCount();
    }
    
    @Override
    public long mapNext() throws IOException {
        return this.doReadItemCount();
    }
    
    @Override
    public long skipMap() throws IOException {
        return this.doSkipItems();
    }
    
    @Override
    public int readIndex() throws IOException {
        return this.readInt();
    }
    
    public boolean isEnd() throws IOException {
        if (this.limit - this.pos > 0) {
            return false;
        }
        if (this.source.isEof()) {
            return true;
        }
        final int read = this.source.tryReadRaw(this.buf, 0, this.buf.length);
        this.pos = 0;
        this.limit = read;
        return 0 == read;
    }
    
    private void ensureBounds(final int num) throws IOException {
        final int remaining = this.limit - this.pos;
        if (remaining < num) {
            this.source.compactAndFill(this.buf, this.pos, this.minPos, remaining);
            if (this.pos >= this.limit) {
                throw new EOFException();
            }
        }
    }
    
    public InputStream inputStream() {
        return this.source;
    }
    
    static class BufferAccessor
    {
        private final BinaryDecoder decoder;
        private byte[] buf;
        private int pos;
        private int limit;
        boolean detached;
        
        private BufferAccessor(final BinaryDecoder decoder) {
            this.detached = false;
            this.decoder = decoder;
        }
        
        void detach() {
            this.buf = this.decoder.buf;
            this.pos = this.decoder.pos;
            this.limit = this.decoder.limit;
            this.detached = true;
        }
        
        int getPos() {
            if (this.detached) {
                return this.pos;
            }
            return this.decoder.pos;
        }
        
        int getLim() {
            if (this.detached) {
                return this.limit;
            }
            return this.decoder.limit;
        }
        
        byte[] getBuf() {
            if (this.detached) {
                return this.buf;
            }
            return this.decoder.buf;
        }
        
        void setPos(final int pos) {
            if (this.detached) {
                this.pos = pos;
            }
            else {
                this.decoder.pos = pos;
            }
        }
        
        void setLimit(final int limit) {
            if (this.detached) {
                this.limit = limit;
            }
            else {
                this.decoder.limit = limit;
            }
        }
        
        void setBuf(final byte[] buf, final int offset, final int length) {
            if (this.detached) {
                this.buf = buf;
                this.limit = offset + length;
                this.pos = offset;
            }
            else {
                this.decoder.buf = buf;
                this.decoder.limit = offset + length;
                this.decoder.pos = offset;
                this.decoder.minPos = offset;
            }
        }
    }
    
    abstract static class ByteSource extends InputStream
    {
        protected BufferAccessor ba;
        
        protected ByteSource() {
        }
        
        abstract boolean isEof();
        
        protected void attach(final int bufferSize, final BinaryDecoder decoder) {
            decoder.buf = new byte[bufferSize];
            decoder.pos = 0;
            decoder.minPos = 0;
            decoder.limit = 0;
            this.ba = new BufferAccessor(decoder);
        }
        
        protected void detach() {
            this.ba.detach();
        }
        
        protected abstract void skipSourceBytes(final long p0) throws IOException;
        
        protected abstract long trySkipBytes(final long p0) throws IOException;
        
        protected abstract void readRaw(final byte[] p0, final int p1, final int p2) throws IOException;
        
        protected abstract int tryReadRaw(final byte[] p0, final int p1, final int p2) throws IOException;
        
        protected void compactAndFill(final byte[] buf, final int pos, final int minPos, final int remaining) throws IOException {
            System.arraycopy(buf, pos, buf, minPos, remaining);
            this.ba.setPos(minPos);
            final int newLimit = remaining + this.tryReadRaw(buf, minPos + remaining, buf.length - remaining);
            this.ba.setLimit(newLimit);
        }
        
        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            final int lim = this.ba.getLim();
            int pos = this.ba.getPos();
            final byte[] buf = this.ba.getBuf();
            final int remaining = lim - pos;
            if (remaining >= len) {
                System.arraycopy(buf, pos, b, off, len);
                pos += len;
                this.ba.setPos(pos);
                return len;
            }
            System.arraycopy(buf, pos, b, off, remaining);
            pos += remaining;
            this.ba.setPos(pos);
            final int inputRead = remaining + this.tryReadRaw(b, off + remaining, len - remaining);
            if (inputRead == 0) {
                return -1;
            }
            return inputRead;
        }
        
        @Override
        public long skip(final long n) throws IOException {
            final int lim = this.ba.getLim();
            int pos = this.ba.getPos();
            final int remaining = lim - pos;
            if (remaining > n) {
                pos += (int)n;
                this.ba.setPos(pos);
                return n;
            }
            pos = lim;
            this.ba.setPos(pos);
            final long isSkipCount = this.trySkipBytes(n - remaining);
            return isSkipCount + remaining;
        }
        
        @Override
        public int available() throws IOException {
            return this.ba.getLim() - this.ba.getPos();
        }
    }
    
    private static class InputStreamByteSource extends ByteSource
    {
        private InputStream in;
        protected boolean isEof;
        
        private InputStreamByteSource(final InputStream in) {
            this.isEof = false;
            this.in = in;
        }
        
        @Override
        protected void skipSourceBytes(long length) throws IOException {
            boolean readZero = false;
            while (length > 0L) {
                final long n = this.in.skip(length);
                if (n > 0L) {
                    length -= n;
                }
                else {
                    if (n != 0L) {
                        this.isEof = true;
                        throw new EOFException();
                    }
                    if (readZero) {
                        this.isEof = true;
                        throw new EOFException();
                    }
                    readZero = true;
                }
            }
        }
        
        @Override
        protected long trySkipBytes(final long length) throws IOException {
            long leftToSkip = length;
            try {
                boolean readZero = false;
                while (leftToSkip > 0L) {
                    final long n = this.in.skip(length);
                    if (n > 0L) {
                        leftToSkip -= n;
                    }
                    else {
                        if (n != 0L) {
                            this.isEof = true;
                            break;
                        }
                        if (readZero) {
                            this.isEof = true;
                            break;
                        }
                        readZero = true;
                    }
                }
            }
            catch (EOFException eof) {
                this.isEof = true;
            }
            return length - leftToSkip;
        }
        
        @Override
        protected void readRaw(final byte[] data, int off, int len) throws IOException {
            while (len > 0) {
                final int read = this.in.read(data, off, len);
                if (read < 0) {
                    this.isEof = true;
                    throw new EOFException();
                }
                len -= read;
                off += read;
            }
        }
        
        @Override
        protected int tryReadRaw(final byte[] data, int off, final int len) throws IOException {
            int leftToCopy = len;
            try {
                while (leftToCopy > 0) {
                    final int read = this.in.read(data, off, leftToCopy);
                    if (read < 0) {
                        this.isEof = true;
                        break;
                    }
                    leftToCopy -= read;
                    off += read;
                }
            }
            catch (EOFException eof) {
                this.isEof = true;
            }
            return len - leftToCopy;
        }
        
        @Override
        public int read() throws IOException {
            if (this.ba.getLim() - this.ba.getPos() == 0) {
                return this.in.read();
            }
            final int position = this.ba.getPos();
            final int result = this.ba.getBuf()[position] & 0xFF;
            this.ba.setPos(position + 1);
            return result;
        }
        
        public boolean isEof() {
            return this.isEof;
        }
        
        @Override
        public void close() throws IOException {
            this.in.close();
        }
    }
    
    private static class ByteArrayByteSource extends ByteSource
    {
        private byte[] data;
        private int position;
        private int max;
        private boolean compacted;
        
        private ByteArrayByteSource(final byte[] data, final int start, final int len) {
            this.compacted = false;
            if (data.length < 16 || len < 16) {
                System.arraycopy(data, start, this.data = new byte[16], 0, len);
                this.position = 0;
                this.max = len;
            }
            else {
                this.data = data;
                this.position = start;
                this.max = start + len;
            }
        }
        
        @Override
        protected void attach(final int bufferSize, final BinaryDecoder decoder) {
            decoder.buf = this.data;
            decoder.pos = this.position;
            decoder.minPos = this.position;
            decoder.limit = this.max;
            this.ba = new BufferAccessor(decoder);
        }
        
        @Override
        protected void skipSourceBytes(final long length) throws IOException {
            final long skipped = this.trySkipBytes(length);
            if (skipped < length) {
                throw new EOFException();
            }
        }
        
        @Override
        protected long trySkipBytes(final long length) throws IOException {
            this.max = this.ba.getLim();
            this.position = this.ba.getPos();
            final long remaining = this.max - this.position;
            if (remaining >= length) {
                this.position += (int)length;
                this.ba.setPos(this.position);
                return length;
            }
            this.position += (int)remaining;
            this.ba.setPos(this.position);
            return remaining;
        }
        
        @Override
        protected void readRaw(final byte[] data, final int off, final int len) throws IOException {
            final int read = this.tryReadRaw(data, off, len);
            if (read < len) {
                throw new EOFException();
            }
        }
        
        @Override
        protected int tryReadRaw(final byte[] data, final int off, final int len) throws IOException {
            return 0;
        }
        
        @Override
        protected void compactAndFill(final byte[] buf, final int pos, final int minPos, final int remaining) throws IOException {
            if (!this.compacted) {
                final byte[] tinybuf = new byte[remaining + 16];
                System.arraycopy(buf, pos, tinybuf, 0, remaining);
                this.ba.setBuf(tinybuf, 0, remaining);
                this.compacted = true;
            }
        }
        
        @Override
        public int read() throws IOException {
            this.max = this.ba.getLim();
            this.position = this.ba.getPos();
            if (this.position >= this.max) {
                return -1;
            }
            final int result = this.ba.getBuf()[this.position++] & 0xFF;
            this.ba.setPos(this.position);
            return result;
        }
        
        @Override
        public void close() throws IOException {
            this.ba.setPos(this.ba.getLim());
        }
        
        public boolean isEof() {
            final int remaining = this.ba.getLim() - this.ba.getPos();
            return remaining == 0;
        }
    }
}
