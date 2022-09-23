// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.crypto;

import java.nio.ByteBuffer;
import java.io.IOException;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public interface Decryptor
{
    void init(final byte[] p0, final byte[] p1) throws IOException;
    
    boolean isContextReset();
    
    void decrypt(final ByteBuffer p0, final ByteBuffer p1) throws IOException;
}
