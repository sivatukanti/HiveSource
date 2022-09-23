// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.jdbc;

import org.apache.derby.iapi.types.PositionedStream;
import java.io.InputStream;

public class CharacterStreamDescriptor
{
    public static final long BEFORE_FIRST = 0L;
    private final long dataOffset;
    private final long curBytePos;
    private final long curCharPos;
    private final long byteLength;
    private final long charLength;
    private final long maxCharLength;
    private final boolean bufferable;
    private final boolean positionAware;
    private final InputStream stream;
    
    private CharacterStreamDescriptor(final Builder builder) {
        this.bufferable = builder.bufferable;
        this.positionAware = builder.positionAware;
        this.dataOffset = builder.dataOffset;
        this.curBytePos = builder.curBytePos;
        this.curCharPos = builder.curCharPos;
        this.byteLength = builder.byteLength;
        this.charLength = builder.charLength;
        this.maxCharLength = builder.maxCharLength;
        this.stream = builder.stream;
    }
    
    public boolean isBufferable() {
        return this.bufferable;
    }
    
    public boolean isPositionAware() {
        return this.positionAware;
    }
    
    public long getByteLength() {
        return this.byteLength;
    }
    
    public long getCharLength() {
        return this.charLength;
    }
    
    public long getCurBytePos() {
        return this.curBytePos;
    }
    
    public long getCurCharPos() {
        return this.curCharPos;
    }
    
    public long getDataOffset() {
        return this.dataOffset;
    }
    
    public long getMaxCharLength() {
        return this.maxCharLength;
    }
    
    public InputStream getStream() {
        return this.stream;
    }
    
    public PositionedStream getPositionedStream() {
        if (!this.positionAware) {
            throw new IllegalStateException("stream is not position aware: " + this.stream.getClass().getName());
        }
        return (PositionedStream)this.stream;
    }
    
    public String toString() {
        return "CharacterStreamDescriptor-" + this.hashCode() + "#bufferable=" + this.bufferable + ":positionAware=" + this.positionAware + ":byteLength=" + this.byteLength + ":charLength=" + this.charLength + ":curBytePos=" + this.curBytePos + ":curCharPos=" + this.curCharPos + ":dataOffset=" + this.dataOffset + ":stream=" + this.stream.getClass();
    }
    
    public static class Builder
    {
        private static final long DEFAULT_MAX_CHAR_LENGTH = Long.MAX_VALUE;
        private boolean bufferable;
        private boolean positionAware;
        private long curBytePos;
        private long curCharPos;
        private long byteLength;
        private long charLength;
        private long dataOffset;
        private long maxCharLength;
        private InputStream stream;
        
        public Builder() {
            this.bufferable = false;
            this.positionAware = false;
            this.curBytePos = 0L;
            this.curCharPos = 1L;
            this.byteLength = 0L;
            this.charLength = 0L;
            this.dataOffset = 0L;
            this.maxCharLength = Long.MAX_VALUE;
        }
        
        public Builder bufferable(final boolean bufferable) {
            this.bufferable = bufferable;
            return this;
        }
        
        public Builder positionAware(final boolean positionAware) {
            this.positionAware = positionAware;
            return this;
        }
        
        public Builder curBytePos(final long curBytePos) {
            this.curBytePos = curBytePos;
            return this;
        }
        
        public Builder curCharPos(final long curCharPos) {
            this.curCharPos = curCharPos;
            return this;
        }
        
        public Builder byteLength(final long byteLength) {
            this.byteLength = byteLength;
            return this;
        }
        
        public Builder copyState(final CharacterStreamDescriptor characterStreamDescriptor) {
            this.bufferable = characterStreamDescriptor.bufferable;
            this.byteLength = characterStreamDescriptor.byteLength;
            this.charLength = characterStreamDescriptor.charLength;
            this.curBytePos = characterStreamDescriptor.curBytePos;
            this.curCharPos = characterStreamDescriptor.curCharPos;
            this.dataOffset = characterStreamDescriptor.dataOffset;
            this.maxCharLength = characterStreamDescriptor.maxCharLength;
            this.positionAware = characterStreamDescriptor.positionAware;
            this.stream = characterStreamDescriptor.stream;
            return this;
        }
        
        public Builder charLength(final long charLength) {
            this.charLength = charLength;
            return this;
        }
        
        public Builder dataOffset(final long dataOffset) {
            this.dataOffset = dataOffset;
            return this;
        }
        
        public Builder maxCharLength(final long maxCharLength) {
            this.maxCharLength = maxCharLength;
            return this;
        }
        
        public Builder stream(final InputStream stream) {
            this.stream = stream;
            return this;
        }
        
        public CharacterStreamDescriptor build() {
            return new CharacterStreamDescriptor(this, null);
        }
        
        public String toString() {
            return "CharacterStreamBuiler@" + this.hashCode() + ":bufferable=" + this.bufferable + ", isPositionAware=" + this.positionAware + ", curBytePos=" + this.curBytePos + ", curCharPos=" + this.curCharPos + ", dataOffset=" + this.dataOffset + ", byteLength=" + this.byteLength + ", charLength=" + this.charLength + ", maxCharLength=" + this.maxCharLength + ", stream=" + this.stream.getClass();
        }
    }
}
