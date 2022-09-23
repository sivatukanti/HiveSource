// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.error.StandardException;
import java.io.IOException;
import java.io.InputStream;

public interface PositionedStream
{
    InputStream asInputStream();
    
    long getPosition();
    
    void reposition(final long p0) throws IOException, StandardException;
}
