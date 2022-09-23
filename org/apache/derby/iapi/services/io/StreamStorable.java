// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import org.apache.derby.iapi.error.StandardException;
import java.io.InputStream;

public interface StreamStorable
{
    InputStream returnStream();
    
    void setStream(final InputStream p0);
    
    void loadStream() throws StandardException;
}
