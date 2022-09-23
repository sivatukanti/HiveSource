// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.io.IOException;
import java.io.Reader;

public class CharBuffer extends InputBuffer
{
    public transient Reader input;
    
    public CharBuffer(final Reader input) {
        this.input = input;
    }
    
    public void fill(final int n) throws CharStreamException {
        try {
            this.syncConsume();
            while (this.queue.nbrEntries < n + this.markerOffset) {
                this.queue.append((char)this.input.read());
            }
        }
        catch (IOException ex) {
            throw new CharStreamIOException(ex);
        }
    }
}
