// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.IOException;

public interface Limit
{
    void setLimit(final int p0) throws IOException;
    
    int clearLimit();
}
