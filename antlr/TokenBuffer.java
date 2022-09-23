// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public class TokenBuffer
{
    protected TokenStream input;
    int nMarkers;
    int markerOffset;
    int numToConsume;
    TokenQueue queue;
    
    public TokenBuffer(final TokenStream input) {
        this.nMarkers = 0;
        this.markerOffset = 0;
        this.numToConsume = 0;
        this.input = input;
        this.queue = new TokenQueue(1);
    }
    
    public final void reset() {
        this.nMarkers = 0;
        this.markerOffset = 0;
        this.numToConsume = 0;
        this.queue.reset();
    }
    
    public final void consume() {
        ++this.numToConsume;
    }
    
    private final void fill(final int n) throws TokenStreamException {
        this.syncConsume();
        while (this.queue.nbrEntries < n + this.markerOffset) {
            this.queue.append(this.input.nextToken());
        }
    }
    
    public TokenStream getInput() {
        return this.input;
    }
    
    public final int LA(final int n) throws TokenStreamException {
        this.fill(n);
        return this.queue.elementAt(this.markerOffset + n - 1).getType();
    }
    
    public final Token LT(final int n) throws TokenStreamException {
        this.fill(n);
        return this.queue.elementAt(this.markerOffset + n - 1);
    }
    
    public final int mark() {
        this.syncConsume();
        ++this.nMarkers;
        return this.markerOffset;
    }
    
    public final void rewind(final int markerOffset) {
        this.syncConsume();
        this.markerOffset = markerOffset;
        --this.nMarkers;
    }
    
    private final void syncConsume() {
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
