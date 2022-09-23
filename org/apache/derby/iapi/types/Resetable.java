// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.error.StandardException;
import java.io.IOException;

public interface Resetable
{
    void resetStream() throws IOException, StandardException;
    
    void initStream() throws StandardException;
    
    void closeStream();
}
