// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import java.io.IOException;
import java.io.ObjectOutput;

public interface StreamHeaderGenerator
{
    public static final byte[] DERBY_EOF_MARKER = { -32, 0, 0 };
    
    boolean expectsCharCount();
    
    int generateInto(final byte[] p0, final int p1, final long p2);
    
    int generateInto(final ObjectOutput p0, final long p1) throws IOException;
    
    int writeEOF(final byte[] p0, final int p1, final long p2);
    
    int writeEOF(final ObjectOutput p0, final long p1) throws IOException;
    
    int getMaxHeaderLength();
}
