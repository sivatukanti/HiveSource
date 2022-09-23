// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.nio.ByteBuffer;

class LiteralByteString extends ByteString
{
    protected final byte[] bytes;
    private int hash;
    
    LiteralByteString(final byte[] bytes) {
        this.hash = 0;
        this.bytes = bytes;
    }
    
    @Override
    public byte byteAt(final int index) {
        return this.bytes[index];
    }
    
    @Override
    public int size() {
        return this.bytes.length;
    }
    
    @Override
    public ByteString substring(final int beginIndex, final int endIndex) {
        if (beginIndex < 0) {
            throw new IndexOutOfBoundsException("Beginning index: " + beginIndex + " < 0");
        }
        if (endIndex > this.size()) {
            throw new IndexOutOfBoundsException("End index: " + endIndex + " > " + this.size());
        }
        final int substringLength = endIndex - beginIndex;
        if (substringLength < 0) {
            throw new IndexOutOfBoundsException("Beginning index larger than ending index: " + beginIndex + ", " + endIndex);
        }
        ByteString result;
        if (substringLength == 0) {
            result = ByteString.EMPTY;
        }
        else {
            result = new BoundedByteString(this.bytes, this.getOffsetIntoBytes() + beginIndex, substringLength);
        }
        return result;
    }
    
    @Override
    protected void copyToInternal(final byte[] target, final int sourceOffset, final int targetOffset, final int numberToCopy) {
        System.arraycopy(this.bytes, sourceOffset, target, targetOffset, numberToCopy);
    }
    
    @Override
    public void copyTo(final ByteBuffer target) {
        target.put(this.bytes, this.getOffsetIntoBytes(), this.size());
    }
    
    @Override
    public ByteBuffer asReadOnlyByteBuffer() {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(this.bytes, this.getOffsetIntoBytes(), this.size());
        return byteBuffer.asReadOnlyBuffer();
    }
    
    @Override
    public List<ByteBuffer> asReadOnlyByteBufferList() {
        final List<ByteBuffer> result = new ArrayList<ByteBuffer>(1);
        result.add(this.asReadOnlyByteBuffer());
        return result;
    }
    
    @Override
    public void writeTo(final OutputStream outputStream) throws IOException {
        outputStream.write(this.toByteArray());
    }
    
    @Override
    public String toString(final String charsetName) throws UnsupportedEncodingException {
        return new String(this.bytes, this.getOffsetIntoBytes(), this.size(), charsetName);
    }
    
    @Override
    public boolean isValidUtf8() {
        final int offset = this.getOffsetIntoBytes();
        return Utf8.isValidUtf8(this.bytes, offset, offset + this.size());
    }
    
    @Override
    protected int partialIsValidUtf8(final int state, final int offset, final int length) {
        final int index = this.getOffsetIntoBytes() + offset;
        return Utf8.partialIsValidUtf8(state, this.bytes, index, index + length);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ByteString)) {
            return false;
        }
        if (this.size() != ((ByteString)other).size()) {
            return false;
        }
        if (this.size() == 0) {
            return true;
        }
        if (other instanceof LiteralByteString) {
            return this.equalsRange((LiteralByteString)other, 0, this.size());
        }
        if (other instanceof RopeByteString) {
            return other.equals(this);
        }
        throw new IllegalArgumentException("Has a new type of ByteString been created? Found " + other.getClass());
    }
    
    boolean equalsRange(final LiteralByteString other, final int offset, final int length) {
        if (length > other.size()) {
            throw new IllegalArgumentException("Length too large: " + length + this.size());
        }
        if (offset + length > other.size()) {
            throw new IllegalArgumentException("Ran off end of other: " + offset + ", " + length + ", " + other.size());
        }
        final byte[] thisBytes = this.bytes;
        final byte[] otherBytes = other.bytes;
        for (int thisLimit = this.getOffsetIntoBytes() + length, thisIndex = this.getOffsetIntoBytes(), otherIndex = other.getOffsetIntoBytes() + offset; thisIndex < thisLimit; ++thisIndex, ++otherIndex) {
            if (thisBytes[thisIndex] != otherBytes[otherIndex]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int h = this.hash;
        if (h == 0) {
            final int size = this.size();
            h = this.partialHash(size, 0, size);
            if (h == 0) {
                h = 1;
            }
            this.hash = h;
        }
        return h;
    }
    
    @Override
    protected int peekCachedHashCode() {
        return this.hash;
    }
    
    @Override
    protected int partialHash(int h, final int offset, final int length) {
        final byte[] thisBytes = this.bytes;
        for (int i = this.getOffsetIntoBytes() + offset, limit = i + length; i < limit; ++i) {
            h = h * 31 + thisBytes[i];
        }
        return h;
    }
    
    @Override
    public InputStream newInput() {
        return new ByteArrayInputStream(this.bytes, this.getOffsetIntoBytes(), this.size());
    }
    
    @Override
    public CodedInputStream newCodedInput() {
        return CodedInputStream.newInstance(this.bytes, this.getOffsetIntoBytes(), this.size());
    }
    
    @Override
    public ByteIterator iterator() {
        return new LiteralByteIterator();
    }
    
    @Override
    protected int getTreeDepth() {
        return 0;
    }
    
    @Override
    protected boolean isBalanced() {
        return true;
    }
    
    protected int getOffsetIntoBytes() {
        return 0;
    }
    
    private class LiteralByteIterator implements ByteIterator
    {
        private int position;
        private final int limit;
        
        private LiteralByteIterator() {
            this.position = 0;
            this.limit = LiteralByteString.this.size();
        }
        
        public boolean hasNext() {
            return this.position < this.limit;
        }
        
        public Byte next() {
            return this.nextByte();
        }
        
        public byte nextByte() {
            try {
                return LiteralByteString.this.bytes[this.position++];
            }
            catch (ArrayIndexOutOfBoundsException e) {
                throw new NoSuchElementException(e.getMessage());
            }
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
