// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.util.NoSuchElementException;
import java.util.Iterator;

class BoundedByteString extends LiteralByteString
{
    private final int bytesOffset;
    private final int bytesLength;
    
    BoundedByteString(final byte[] bytes, final int offset, final int length) {
        super(bytes);
        if (offset < 0) {
            throw new IllegalArgumentException("Offset too small: " + offset);
        }
        if (length < 0) {
            throw new IllegalArgumentException("Length too small: " + offset);
        }
        if (offset + (long)length > bytes.length) {
            throw new IllegalArgumentException("Offset+Length too large: " + offset + "+" + length);
        }
        this.bytesOffset = offset;
        this.bytesLength = length;
    }
    
    @Override
    public byte byteAt(final int index) {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException("Index too small: " + index);
        }
        if (index >= this.size()) {
            throw new ArrayIndexOutOfBoundsException("Index too large: " + index + ", " + this.size());
        }
        return this.bytes[this.bytesOffset + index];
    }
    
    @Override
    public int size() {
        return this.bytesLength;
    }
    
    @Override
    protected int getOffsetIntoBytes() {
        return this.bytesOffset;
    }
    
    @Override
    protected void copyToInternal(final byte[] target, final int sourceOffset, final int targetOffset, final int numberToCopy) {
        System.arraycopy(this.bytes, this.getOffsetIntoBytes() + sourceOffset, target, targetOffset, numberToCopy);
    }
    
    @Override
    public ByteIterator iterator() {
        return new BoundedByteIterator();
    }
    
    private class BoundedByteIterator implements ByteIterator
    {
        private int position;
        private final int limit;
        
        private BoundedByteIterator() {
            this.position = BoundedByteString.this.getOffsetIntoBytes();
            this.limit = this.position + BoundedByteString.this.size();
        }
        
        public boolean hasNext() {
            return this.position < this.limit;
        }
        
        public Byte next() {
            return this.nextByte();
        }
        
        public byte nextByte() {
            if (this.position >= this.limit) {
                throw new NoSuchElementException();
            }
            return BoundedByteString.this.bytes[this.position++];
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
