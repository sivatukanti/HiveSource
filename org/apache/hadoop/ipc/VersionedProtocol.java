// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import java.io.IOException;

public interface VersionedProtocol
{
    long getProtocolVersion(final String p0, final long p1) throws IOException;
    
    ProtocolSignature getProtocolSignature(final String p0, final long p1, final int p2) throws IOException;
}
