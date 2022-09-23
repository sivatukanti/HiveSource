// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.util.List;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public abstract class ByteString implements Iterable<Byte>
{
    static final int CONCATENATE_BY_COPY_SIZE = 128;
    static final int MIN_READ_FROM_CHUNK_SIZE = 256;
    static final int MAX_READ_FROM_CHUNK_SIZE = 8192;
    public static final ByteString EMPTY;
    
    ByteString() {
    }
    
    public abstract byte byteAt(final int p0);
    
    public abstract ByteIterator iterator();
    
    public abstract int size();
    
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    public ByteString substring(final int beginIndex) {
        return this.substring(beginIndex, this.size());
    }
    
    public abstract ByteString substring(final int p0, final int p1);
    
    public boolean startsWith(final ByteString prefix) {
        return this.size() >= prefix.size() && this.substring(0, prefix.size()).equals(prefix);
    }
    
    public static ByteString copyFrom(final byte[] bytes, final int offset, final int size) {
        final byte[] copy = new byte[size];
        System.arraycopy(bytes, offset, copy, 0, size);
        return new LiteralByteString(copy);
    }
    
    public static ByteString copyFrom(final byte[] bytes) {
        return copyFrom(bytes, 0, bytes.length);
    }
    
    public static ByteString copyFrom(final ByteBuffer bytes, final int size) {
        final byte[] copy = new byte[size];
        bytes.get(copy);
        return new LiteralByteString(copy);
    }
    
    public static ByteString copyFrom(final ByteBuffer bytes) {
        return copyFrom(bytes, bytes.remaining());
    }
    
    public static ByteString copyFrom(final String text, final String charsetName) throws UnsupportedEncodingException {
        return new LiteralByteString(text.getBytes(charsetName));
    }
    
    public static ByteString copyFromUtf8(final String text) {
        try {
            return new LiteralByteString(text.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 not supported?", e);
        }
    }
    
    public static ByteString readFrom(final InputStream streamToDrain) throws IOException {
        return readFrom(streamToDrain, 256, 8192);
    }
    
    public static ByteString readFrom(final InputStream streamToDrain, final int chunkSize) throws IOException {
        return readFrom(streamToDrain, chunkSize, chunkSize);
    }
    
    public static ByteString readFrom(final InputStream streamToDrain, final int minChunkSize, final int maxChunkSize) throws IOException {
        final Collection<ByteString> results = new ArrayList<ByteString>();
        int chunkSize = minChunkSize;
        while (true) {
            final ByteString chunk = readChunk(streamToDrain, chunkSize);
            if (chunk == null) {
                break;
            }
            results.add(chunk);
            chunkSize = Math.min(chunkSize * 2, maxChunkSize);
        }
        return copyFrom(results);
    }
    
    private static ByteString readChunk(final InputStream in, final int chunkSize) throws IOException {
        final byte[] buf = new byte[chunkSize];
        int bytesRead;
        int count;
        for (bytesRead = 0; bytesRead < chunkSize; bytesRead += count) {
            count = in.read(buf, bytesRead, chunkSize - bytesRead);
            if (count == -1) {
                break;
            }
        }
        if (bytesRead == 0) {
            return null;
        }
        return copyFrom(buf, 0, bytesRead);
    }
    
    public ByteString concat(final ByteString other) {
        final int thisSize = this.size();
        final int otherSize = other.size();
        if (thisSize + (long)otherSize >= 2147483647L) {
            throw new IllegalArgumentException("ByteString would be too long: " + thisSize + "+" + otherSize);
        }
        return RopeByteString.concatenate(this, other);
    }
    
    public static ByteString copyFrom(final Iterable<ByteString> byteStrings) {
        Collection<ByteString> collection;
        if (!(byteStrings instanceof Collection)) {
            collection = new ArrayList<ByteString>();
            for (final ByteString byteString : byteStrings) {
                collection.add(byteString);
            }
        }
        else {
            collection = (Collection<ByteString>)(Collection)byteStrings;
        }
        ByteString result;
        if (collection.isEmpty()) {
            result = ByteString.EMPTY;
        }
        else {
            result = balancedConcat(collection.iterator(), collection.size());
        }
        return result;
    }
    
    private static ByteString balancedConcat(final Iterator<ByteString> iterator, final int length) {
        assert length >= 1;
        ByteString result;
        if (length == 1) {
            result = iterator.next();
        }
        else {
            final int halfLength = length >>> 1;
            final ByteString left = balancedConcat(iterator, halfLength);
            final ByteString right = balancedConcat(iterator, length - halfLength);
            result = left.concat(right);
        }
        return result;
    }
    
    public void copyTo(final byte[] target, final int offset) {
        this.copyTo(target, 0, offset, this.size());
    }
    
    public void copyTo(final byte[] target, final int sourceOffset, final int targetOffset, final int numberToCopy) {
        if (sourceOffset < 0) {
            throw new IndexOutOfBoundsException("Source offset < 0: " + sourceOffset);
        }
        if (targetOffset < 0) {
            throw new IndexOutOfBoundsException("Target offset < 0: " + targetOffset);
        }
        if (numberToCopy < 0) {
            throw new IndexOutOfBoundsException("Length < 0: " + numberToCopy);
        }
        if (sourceOffset + numberToCopy > this.size()) {
            throw new IndexOutOfBoundsException("Source end offset < 0: " + (sourceOffset + numberToCopy));
        }
        if (targetOffset + numberToCopy > target.length) {
            throw new IndexOutOfBoundsException("Target end offset < 0: " + (targetOffset + numberToCopy));
        }
        if (numberToCopy > 0) {
            this.copyToInternal(target, sourceOffset, targetOffset, numberToCopy);
        }
    }
    
    protected abstract void copyToInternal(final byte[] p0, final int p1, final int p2, final int p3);
    
    public abstract void copyTo(final ByteBuffer p0);
    
    public byte[] toByteArray() {
        final int size = this.size();
        final byte[] result = new byte[size];
        this.copyToInternal(result, 0, 0, size);
        return result;
    }
    
    public abstract void writeTo(final OutputStream p0) throws IOException;
    
    public abstract ByteBuffer asReadOnlyByteBuffer();
    
    public abstract List<ByteBuffer> asReadOnlyByteBufferList();
    
    public abstract String toString(final String p0) throws UnsupportedEncodingException;
    
    public String toStringUtf8() {
        try {
            return this.toString("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 not supported?", e);
        }
    }
    
    public abstract boolean isValidUtf8();
    
    protected abstract int partialIsValidUtf8(final int p0, final int p1, final int p2);
    
    @Override
    public abstract boolean equals(final Object p0);
    
    @Override
    public abstract int hashCode();
    
    public abstract InputStream newInput();
    
    public abstract CodedInputStream newCodedInput();
    
    public static Output newOutput(final int initialCapacity) {
        return new Output(initialCapacity);
    }
    
    public static Output newOutput() {
        return new Output(128);
    }
    
    static CodedBuilder newCodedBuilder(final int size) {
        return new CodedBuilder(size);
    }
    
    protected abstract int getTreeDepth();
    
    protected abstract boolean isBalanced();
    
    protected abstract int peekCachedHashCode();
    
    protected abstract int partialHash(final int p0, final int p1, final int p2);
    
    @Override
    public String toString() {
        return String.format("<ByteString@%s size=%d>", Integer.toHexString(System.identityHashCode(this)), this.size());
    }
    
    static {
        EMPTY = new LiteralByteString(new byte[0]);
    }
    
    public static final class Output extends OutputStream
    {
        private static final byte[] EMPTY_BYTE_ARRAY;
        private final int initialCapacity;
        private final ArrayList<ByteString> flushedBuffers;
        private int flushedBuffersTotalBytes;
        private byte[] buffer;
        private int bufferPos;
        
        Output(final int initialCapacity) {
            if (initialCapacity < 0) {
                throw new IllegalArgumentException("Buffer size < 0");
            }
            this.initialCapacity = initialCapacity;
            this.flushedBuffers = new ArrayList<ByteString>();
            this.buffer = new byte[initialCapacity];
        }
        
        @Override
        public synchronized void write(final int b) {
            if (this.bufferPos == this.buffer.length) {
                this.flushFullBuffer(1);
            }
            this.buffer[this.bufferPos++] = (byte)b;
        }
        
        @Override
        public synchronized void write(final byte[] b, int offset, int length) {
            if (length <= this.buffer.length - this.bufferPos) {
                System.arraycopy(b, offset, this.buffer, this.bufferPos, length);
                this.bufferPos += length;
            }
            else {
                final int copySize = this.buffer.length - this.bufferPos;
                System.arraycopy(b, offset, this.buffer, this.bufferPos, copySize);
                offset += copySize;
                length -= copySize;
                this.flushFullBuffer(length);
                System.arraycopy(b, offset, this.buffer, 0, length);
                this.bufferPos = length;
            }
        }
        
        public synchronized ByteString toByteString() {
            this.flushLastBuffer();
            return ByteString.copyFrom(this.flushedBuffers);
        }
        
        private byte[] copyArray(final byte[] buffer, final int length) {
            final byte[] result = new byte[length];
            System.arraycopy(buffer, 0, result, 0, Math.min(buffer.length, length));
            return result;
        }
        
        public void writeTo(final OutputStream out) throws IOException {
            final ByteString[] cachedFlushBuffers;
            final byte[] cachedBuffer;
            final int cachedBufferPos;
            synchronized (this) {
                cachedFlushBuffers = this.flushedBuffers.toArray(new ByteString[this.flushedBuffers.size()]);
                cachedBuffer = this.buffer;
                cachedBufferPos = this.bufferPos;
            }
            for (final ByteString byteString : cachedFlushBuffers) {
                byteString.writeTo(out);
            }
            out.write(this.copyArray(cachedBuffer, cachedBufferPos));
        }
        
        public synchronized int size() {
            return this.flushedBuffersTotalBytes + this.bufferPos;
        }
        
        public synchronized void reset() {
            this.flushedBuffers.clear();
            this.flushedBuffersTotalBytes = 0;
            this.bufferPos = 0;
        }
        
        @Override
        public String toString() {
            return String.format("<ByteString.Output@%s size=%d>", Integer.toHexString(System.identityHashCode(this)), this.size());
        }
        
        private void flushFullBuffer(final int minSize) {
            this.flushedBuffers.add(new LiteralByteString(this.buffer));
            this.flushedBuffersTotalBytes += this.buffer.length;
            final int newSize = Math.max(this.initialCapacity, Math.max(minSize, this.flushedBuffersTotalBytes >>> 1));
            this.buffer = new byte[newSize];
            this.bufferPos = 0;
        }
        
        private void flushLastBuffer() {
            if (this.bufferPos < this.buffer.length) {
                if (this.bufferPos > 0) {
                    final byte[] bufferCopy = this.copyArray(this.buffer, this.bufferPos);
                    this.flushedBuffers.add(new LiteralByteString(bufferCopy));
                }
            }
            else {
                this.flushedBuffers.add(new LiteralByteString(this.buffer));
                this.buffer = Output.EMPTY_BYTE_ARRAY;
            }
            this.flushedBuffersTotalBytes += this.bufferPos;
            this.bufferPos = 0;
        }
        
        static {
            EMPTY_BYTE_ARRAY = new byte[0];
        }
    }
    
    static final class CodedBuilder
    {
        private final CodedOutputStream output;
        private final byte[] buffer;
        
        private CodedBuilder(final int size) {
            this.buffer = new byte[size];
            this.output = CodedOutputStream.newInstance(this.buffer);
        }
        
        public ByteString build() {
            this.output.checkNoSpaceLeft();
            return new LiteralByteString(this.buffer);
        }
        
        public CodedOutputStream getCodedOutput() {
            return this.output;
        }
    }
    
    public interface ByteIterator extends Iterator<Byte>
    {
        byte nextByte();
    }
}
