// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.coder;

import java.io.IOException;
import org.apache.hadoop.io.erasurecode.ECChunk;
import org.apache.hadoop.io.erasurecode.ECBlock;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public interface ErasureCodingStep
{
    ECBlock[] getInputBlocks();
    
    ECBlock[] getOutputBlocks();
    
    void performCoding(final ECChunk[] p0, final ECChunk[] p1) throws IOException;
    
    void finish();
}
