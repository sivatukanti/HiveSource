// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.api.records;

import java.nio.ByteBuffer;

public interface MasterKey
{
    int getKeyId();
    
    void setKeyId(final int p0);
    
    ByteBuffer getBytes();
    
    void setBytes(final ByteBuffer p0);
}
