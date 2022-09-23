// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.replication.buffer;

class LogBufferElement
{
    private final byte[] bufferdata;
    private int position;
    private long greatestInstant;
    private boolean recycleMe;
    
    protected LogBufferElement(final int n) {
        this.bufferdata = new byte[n];
        this.init();
    }
    
    protected void init() {
        this.position = 0;
        this.greatestInstant = 0L;
        this.recycleMe = true;
    }
    
    protected void appendLog(final long greatestInstant, final byte[] array, final int n, final int n2) {
        this.greatestInstant = greatestInstant;
        this.position = this.appendBytes(array, n, this.position, n2);
    }
    
    protected byte[] getData() {
        return this.bufferdata;
    }
    
    protected long getLastInstant() {
        return this.greatestInstant;
    }
    
    protected int freeSize() {
        return this.bufferdata.length - this.position;
    }
    
    protected int size() {
        return this.position;
    }
    
    protected boolean isRecyclable() {
        return this.recycleMe;
    }
    
    protected void setRecyclable(final boolean recycleMe) {
        this.recycleMe = recycleMe;
    }
    
    private int appendBytes(final byte[] array, final int n, final int n2, final int n3) {
        System.arraycopy(array, n, this.bufferdata, n2, n3);
        return n2 + n3;
    }
}
