// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.tar;

import java.io.IOException;

public class TarArchiveSparseEntry implements TarConstants
{
    private boolean isExtended;
    
    public TarArchiveSparseEntry(final byte[] headerBuf) throws IOException {
        int offset = 0;
        offset += 504;
        this.isExtended = TarUtils.parseBoolean(headerBuf, offset);
    }
    
    public boolean isExtended() {
        return this.isExtended;
    }
}
