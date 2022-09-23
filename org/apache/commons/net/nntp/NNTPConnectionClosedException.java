// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.net.nntp;

import java.io.IOException;

public final class NNTPConnectionClosedException extends IOException
{
    private static final long serialVersionUID = 1029785635891040770L;
    
    public NNTPConnectionClosedException() {
    }
    
    public NNTPConnectionClosedException(final String message) {
        super(message);
    }
}
