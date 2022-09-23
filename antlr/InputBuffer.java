// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public abstract class InputBuffer
{
    protected int nMarkers;
    protected int markerOffset;
    protected int numToConsume;
    protected CharQueue queue;
    
    public InputBuffer() {
        this.nMarkers = 0;
        this.markerOffset = 0;
        this.numToConsume = 0;
        this.queue = new CharQueue(1);
    }
    
    public void commit() {
        --this.nMarkers;
    }
    
    public void consume() {
        ++this.numToConsume;
    }
    
    public abstract void fill(final int p0) throws CharStreamException;
    
    public String getLAChars() {
        final StringBuffer sb = new StringBuffer();
        for (int i = this.markerOffset; i < this.queue.nbrEntries; ++i) {
            sb.append(this.queue.elementAt(i));
        }
        return sb.toString();
    }
    
    public String getMarkedChars() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.markerOffset; ++i) {
            sb.append(this.queue.elementAt(i));
        }
        return sb.toString();
    }
    
    public boolean isMarked() {
        return this.nMarkers != 0;
    }
    
    public char LA(final int n) throws CharStreamException {
        this.fill(n);
        return this.queue.elementAt(this.markerOffset + n - 1);
    }
    
    public int mark() {
        this.syncConsume();
        ++this.nMarkers;
        return this.markerOffset;
    }
    
    public void rewind(final int markerOffset) {
        this.syncConsume();
        this.markerOffset = markerOffset;
        --this.nMarkers;
    }
    
    public void reset() {
        this.nMarkers = 0;
        this.markerOffset = 0;
        this.numToConsume = 0;
        this.queue.reset();
    }
    
    protected void syncConsume() {
        while (this.numToConsume > 0) {
            if (this.nMarkers > 0) {
                ++this.markerOffset;
            }
            else {
                this.queue.removeFirst();
            }
            --this.numToConsume;
        }
    }
}
