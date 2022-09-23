// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute;

import java.io.IOException;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public interface Record
{
    void serialize(final OutputArchive p0, final String p1) throws IOException;
    
    void deserialize(final InputArchive p0, final String p1) throws IOException;
}
