// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.io.IOException;
import java.io.InputStream;

public class ByteBuffer extends InputBuffer
{
    public transient InputStream input;
    
    public ByteBuffer(final InputStream input) {
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
