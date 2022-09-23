// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io;

import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import java.io.IOException;
import java.util.Arrays;
import java.io.OutputStream;

public class BlockingBinaryEncoder extends BufferedBinaryEncoder
{
    private byte[] buf;
    private int pos;
    private BlockedValue[] blockStack;
    private int stackTop;
    private static final int STACK_STEP = 10;
    private byte[] headerBuffer;
    static final /* synthetic */ boolean $assertionsDisabled;
    
    private boolean check() {
        assert this.buf != null;
        assert 0 <= this.pos;
        assert this.pos <= this.buf.length : this.pos + " " + this.buf.length;
        assert this.blockStack != null;
        BlockedValue prev = null;
        for (int i = 0; i <= this.stackTop; ++i) {
            final BlockedValue v = this.blockStack[i];
            v.check(prev, this.pos);
            prev = v;
        }
        return true;
    }
    
    BlockingBinaryEncoder(final OutputStream out, final int blockBufferSize, final int binaryEncoderBufferSize) {
        super(out, binaryEncoderBufferSize);
        this.stackTop = -1;
        this.headerBuffer = new byte[12];
        this.buf = new byte[blockBufferSize];
        this.pos = 0;
        this.blockStack = new BlockedValue[0];
        this.expandStack();
        final BlockedValue bv = this.blockStack[++this.stackTop];
        bv.type = null;
        bv.state = BlockedValue.State.ROOT;
        final BlockedValue blockedValue = bv;
        final BlockedValue blockedValue2 = bv;
        final int n = 0;
        blockedValue2.lastFullItem = n;
        blockedValue.start = n;
        bv.items = 1;
        assert this.check();
    }
    
    private void expandStack() {
        final int oldLength = this.blockStack.length;
        this.blockStack = Arrays.copyOf(this.blockStack, this.blockStack.length + 10);
        for (int i = oldLength; i < this.blockStack.length; ++i) {
            this.blockStack[i] = new BlockedValue();
        }
    }
    
    BlockingBinaryEncoder configure(final OutputStream out, final int blockBufferSize, final int binaryEncoderBufferSize) {
        super.configure(out, binaryEncoderBufferSize);
        this.pos = 0;
        this.stackTop = 0;
        if (null == this.buf || this.buf.length != blockBufferSize) {
            this.buf = new byte[blockBufferSize];
        }
        assert this.check();
        return this;
    }
    
    @Override
    public void flush() throws IOException {
        final BlockedValue bv = this.blockStack[this.stackTop];
        if (bv.state == BlockedValue.State.ROOT) {
            super.writeFixed(this.buf, 0, this.pos);
            this.pos = 0;
        }
        else {
            while (bv.state != BlockedValue.State.OVERFLOW) {
                this.compact();
            }
        }
        super.flush();
        assert this.check();
    }
    
    @Override
    public void writeBoolean(final boolean b) throws IOException {
        this.ensureBounds(1);
        this.pos += BinaryData.encodeBoolean(b, this.buf, this.pos);
    }
    
    @Override
    public void writeInt(final int n) throws IOException {
        this.ensureBounds(5);
        this.pos += BinaryData.encodeInt(n, this.buf, this.pos);
    }
    
    @Override
    public void writeLong(final long n) throws IOException {
        this.ensureBounds(10);
        this.pos += BinaryData.encodeLong(n, this.buf, this.pos);
    }
    
    @Override
    public void writeFloat(final float f) throws IOException {
        this.ensureBounds(4);
        this.pos += BinaryData.encodeFloat(f, this.buf, this.pos);
    }
    
    @Override
    public void writeDouble(final double d) throws IOException {
        this.ensureBounds(8);
        this.pos += BinaryData.encodeDouble(d, this.buf, this.pos);
    }
    
    @Override
    public void writeFixed(final byte[] bytes, final int start, final int len) throws IOException {
        this.doWriteBytes(bytes, start, len);
    }
    
    @Override
    protected void writeZero() throws IOException {
        this.ensureBounds(1);
        this.buf[this.pos++] = 0;
    }
    
    @Override
    public void writeArrayStart() throws IOException {
        if (this.stackTop + 1 == this.blockStack.length) {
            this.expandStack();
        }
        final BlockedValue bv = this.blockStack[++this.stackTop];
        bv.type = Schema.Type.ARRAY;
        bv.state = BlockedValue.State.REGULAR;
        final BlockedValue blockedValue = bv;
        final BlockedValue blockedValue2 = bv;
        final int pos = this.pos;
        blockedValue2.lastFullItem = pos;
        blockedValue.start = pos;
        bv.items = 0;
        assert this.check();
    }
    
    @Override
    public void setItemCount(final long itemCount) throws IOException {
        final BlockedValue v = this.blockStack[this.stackTop];
        assert v.type == Schema.Type.MAP;
        assert v.itemsLeftToWrite == 0L;
        v.itemsLeftToWrite = itemCount;
        assert this.check();
    }
    
    @Override
    public void startItem() throws IOException {
        if (this.blockStack[this.stackTop].state == BlockedValue.State.OVERFLOW) {
            this.finishOverflow();
        }
        final BlockedValue blockedValue;
        final BlockedValue t = blockedValue = this.blockStack[this.stackTop];
        ++blockedValue.items;
        t.lastFullItem = this.pos;
        final BlockedValue blockedValue2 = t;
        --blockedValue2.itemsLeftToWrite;
        assert this.check();
    }
    
    @Override
    public void writeArrayEnd() throws IOException {
        final BlockedValue top = this.blockStack[this.stackTop];
        if (top.type != Schema.Type.ARRAY) {
            throw new AvroTypeException("Called writeArrayEnd outside of an array.");
        }
        if (top.itemsLeftToWrite != 0L) {
            throw new AvroTypeException("Failed to write expected number of array elements.");
        }
        this.endBlockedValue();
        assert this.check();
    }
    
    @Override
    public void writeMapStart() throws IOException {
        if (this.stackTop + 1 == this.blockStack.length) {
            this.expandStack();
        }
        final BlockedValue bv = this.blockStack[++this.stackTop];
        bv.type = Schema.Type.MAP;
        bv.state = BlockedValue.State.REGULAR;
        final BlockedValue blockedValue = bv;
        final BlockedValue blockedValue2 = bv;
        final int pos = this.pos;
        blockedValue2.lastFullItem = pos;
        blockedValue.start = pos;
        bv.items = 0;
        assert this.check();
    }
    
    @Override
    public void writeMapEnd() throws IOException {
        final BlockedValue top = this.blockStack[this.stackTop];
        if (top.type != Schema.Type.MAP) {
            throw new AvroTypeException("Called writeMapEnd outside of a map.");
        }
        if (top.itemsLeftToWrite != 0L) {
            throw new AvroTypeException("Failed to read write expected number of array elements.");
        }
        this.endBlockedValue();
        assert this.check();
    }
    
    @Override
    public void writeIndex(final int unionIndex) throws IOException {
        this.ensureBounds(5);
        this.pos += BinaryData.encodeInt(unionIndex, this.buf, this.pos);
    }
    
    @Override
    public int bytesBuffered() {
        return this.pos + super.bytesBuffered();
    }
    
    private void endBlockedValue() throws IOException {
        while (BlockingBinaryEncoder.$assertionsDisabled || this.check()) {
            final BlockedValue t = this.blockStack[this.stackTop];
            assert t.state != BlockedValue.State.ROOT;
            if (t.state == BlockedValue.State.OVERFLOW) {
                this.finishOverflow();
            }
            assert t.state == BlockedValue.State.REGULAR;
            if (0 < t.items) {
                final int byteCount = this.pos - t.start;
                if (t.start == 0 && this.blockStack[this.stackTop - 1].state != BlockedValue.State.REGULAR) {
                    super.writeInt(-t.items);
                    super.writeInt(byteCount);
                }
                else {
                    int headerSize = 0;
                    headerSize += BinaryData.encodeInt(-t.items, this.headerBuffer, headerSize);
                    headerSize += BinaryData.encodeInt(byteCount, this.headerBuffer, headerSize);
                    if (this.buf.length < this.pos + headerSize) {
                        this.compact();
                        continue;
                    }
                    this.pos += headerSize;
                    final int m = t.start;
                    System.arraycopy(this.buf, m, this.buf, m + headerSize, byteCount);
                    System.arraycopy(this.headerBuffer, 0, this.buf, m, headerSize);
                }
            }
            --this.stackTop;
            this.ensureBounds(1);
            this.buf[this.pos++] = 0;
            assert this.check();
            if (this.blockStack[this.stackTop].state == BlockedValue.State.ROOT) {
                this.flush();
            }
            return;
        }
        throw new AssertionError();
    }
    
    private void finishOverflow() throws IOException {
        final BlockedValue s = this.blockStack[this.stackTop];
        if (s.state != BlockedValue.State.OVERFLOW) {
            throw new IllegalStateException("Not an overflow block");
        }
        assert this.check();
        super.writeFixed(this.buf, 0, this.pos);
        this.pos = 0;
        s.state = BlockedValue.State.REGULAR;
        final BlockedValue blockedValue = s;
        final BlockedValue blockedValue2 = s;
        final int n = 0;
        blockedValue2.lastFullItem = n;
        blockedValue.start = n;
        s.items = 0;
        assert this.check();
    }
    
    private void ensureBounds(final int l) throws IOException {
        while (this.buf.length < this.pos + l) {
            if (this.blockStack[this.stackTop].state == BlockedValue.State.REGULAR) {
                this.compact();
            }
            else {
                super.writeFixed(this.buf, 0, this.pos);
                this.pos = 0;
            }
        }
    }
    
    private void doWriteBytes(final byte[] bytes, final int start, final int len) throws IOException {
        if (len < this.buf.length) {
            this.ensureBounds(len);
            System.arraycopy(bytes, start, this.buf, this.pos, len);
            this.pos += len;
        }
        else {
            this.ensureBounds(this.buf.length);
            assert this.blockStack[this.stackTop].state == BlockedValue.State.OVERFLOW;
            this.write(bytes, start, len);
        }
    }
    
    private void write(final byte[] b, final int off, int len) throws IOException {
        if (this.blockStack[this.stackTop].state == BlockedValue.State.ROOT) {
            super.writeFixed(b, off, len);
        }
        else {
            assert this.check();
            while (this.buf.length < this.pos + len) {
                if (this.blockStack[this.stackTop].state == BlockedValue.State.REGULAR) {
                    this.compact();
                }
                else {
                    super.writeFixed(this.buf, 0, this.pos);
                    this.pos = 0;
                    if (this.buf.length > len) {
                        continue;
                    }
                    super.writeFixed(b, off, len);
                    len = 0;
                }
            }
            System.arraycopy(b, off, this.buf, this.pos, len);
            this.pos += len;
        }
        assert this.check();
    }
    
    private void compact() throws IOException {
        assert this.check();
        BlockedValue s = null;
        int i;
        for (i = 1; i <= this.stackTop; ++i) {
            s = this.blockStack[i];
            if (s.state == BlockedValue.State.REGULAR) {
                break;
            }
        }
        assert s != null;
        super.writeFixed(this.buf, 0, s.start);
        if (1 < s.items) {
            super.writeInt(-(s.items - 1));
            super.writeInt(s.lastFullItem - s.start);
            super.writeFixed(this.buf, s.start, s.lastFullItem - s.start);
            s.start = s.lastFullItem;
            s.items = 1;
        }
        super.writeInt(1);
        BlockedValue n = (i + 1 <= this.stackTop) ? this.blockStack[i + 1] : null;
        final int end = (n == null) ? this.pos : n.start;
        super.writeFixed(this.buf, s.lastFullItem, end - s.lastFullItem);
        System.arraycopy(this.buf, end, this.buf, 0, this.pos - end);
        for (int j = i + 1; j <= this.stackTop; ++j) {
            final BlockedValue blockedValue;
            n = (blockedValue = this.blockStack[j]);
            blockedValue.start -= end;
            final BlockedValue blockedValue2 = n;
            blockedValue2.lastFullItem -= end;
        }
        this.pos -= end;
        assert s.items == 1;
        final BlockedValue blockedValue3 = s;
        final BlockedValue blockedValue4 = s;
        final int n2 = 0;
        blockedValue4.lastFullItem = n2;
        blockedValue3.start = n2;
        s.state = BlockedValue.State.OVERFLOW;
        assert this.check();
    }
    
    private static class BlockedValue
    {
        public Schema.Type type;
        public State state;
        public int start;
        public int lastFullItem;
        public int items;
        public long itemsLeftToWrite;
        
        public BlockedValue() {
            this.type = null;
            this.state = State.ROOT;
            final int n = 0;
            this.lastFullItem = n;
            this.start = n;
            this.items = 1;
        }
        
        public boolean check(final BlockedValue prev, final int pos) {
            assert this.type == null;
            assert this.type == Schema.Type.MAP;
            assert 0 <= this.items;
            assert this.start == pos;
            assert this.start == this.lastFullItem;
            assert this.start <= this.lastFullItem;
            assert this.lastFullItem <= pos;
            switch (this.state) {
                case ROOT: {
                    assert this.start == 0;
                    assert prev == null;
                    break;
                }
                case REGULAR: {
                    assert this.start >= 0;
                    assert prev.lastFullItem <= this.start;
                    assert 1 <= prev.items;
                    break;
                }
                case OVERFLOW: {
                    assert this.start == 0;
                    assert this.items == 1;
                    assert prev.state == State.OVERFLOW;
                    break;
                }
            }
            return false;
        }
        
        public enum State
        {
            ROOT, 
            REGULAR, 
            OVERFLOW;
        }
    }
}
