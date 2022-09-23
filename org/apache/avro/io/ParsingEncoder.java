// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io;

import java.util.Arrays;
import java.io.IOException;
import org.apache.avro.AvroTypeException;

public abstract class ParsingEncoder extends Encoder
{
    private long[] counts;
    protected int pos;
    
    public ParsingEncoder() {
        this.counts = new long[10];
        this.pos = -1;
    }
    
    @Override
    public void setItemCount(final long itemCount) throws IOException {
        if (this.counts[this.pos] != 0L) {
            throw new AvroTypeException("Incorrect number of items written. " + this.counts[this.pos] + " more required.");
        }
        this.counts[this.pos] = itemCount;
    }
    
    @Override
    public void startItem() throws IOException {
        final long[] counts = this.counts;
        final int pos = this.pos;
        --counts[pos];
    }
    
    protected final void push() {
        if (++this.pos == this.counts.length) {
            this.counts = Arrays.copyOf(this.counts, this.pos + 10);
        }
        this.counts[this.pos] = 0L;
    }
    
    protected final void pop() {
        if (this.counts[this.pos] != 0L) {
            throw new AvroTypeException("Incorrect number of items written. " + this.counts[this.pos] + " more required.");
        }
        --this.pos;
    }
    
    protected final int depth() {
        return this.pos;
    }
}
