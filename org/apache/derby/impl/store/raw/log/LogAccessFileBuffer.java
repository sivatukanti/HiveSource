// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.log;

final class LogAccessFileBuffer
{
    protected byte[] buffer;
    protected int bytes_free;
    protected int position;
    protected int length;
    protected long greatest_instant;
    LogAccessFileBuffer next;
    LogAccessFileBuffer prev;
    
    public LogAccessFileBuffer(final int n) {
        this.buffer = new byte[n];
        this.prev = null;
        this.next = null;
        this.init(0);
    }
    
    public void init(final int position) {
        this.length = this.buffer.length - position;
        this.bytes_free = this.length;
        this.position = position;
        this.greatest_instant = -1L;
    }
}
