// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.io.IOException;

public class TokenStreamIOException extends TokenStreamException
{
    public IOException io;
    
    public TokenStreamIOException(final IOException io) {
        super(io.getMessage());
        this.io = io;
    }
}
