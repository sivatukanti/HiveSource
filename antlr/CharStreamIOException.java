// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.io.IOException;

public class CharStreamIOException extends CharStreamException
{
    public IOException io;
    
    public CharStreamIOException(final IOException io) {
        super(io.getMessage());
        this.io = io;
    }
}
