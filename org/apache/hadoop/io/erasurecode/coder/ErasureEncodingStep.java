// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.coder;

import java.io.IOException;
import org.apache.hadoop.io.erasurecode.ECChunk;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureEncoder;
import org.apache.hadoop.io.erasurecode.ECBlock;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class ErasureEncodingStep implements ErasureCodingStep
{
    private ECBlock[] inputBlocks;
    private ECBlock[] outputBlocks;
    private RawErasureEncoder rawEncoder;
    
    public ErasureEncodingStep(final ECBlock[] inputBlocks, final ECBlock[] outputBlocks, final RawErasureEncoder rawEncoder) {
        this.inputBlocks = inputBlocks;
        this.outputBlocks = outputBlocks;
        this.rawEncoder = rawEncoder;
    }
    
    @Override
    public void performCoding(final ECChunk[] inputChunks, final ECChunk[] outputChunks) throws IOException {
        this.rawEncoder.encode(inputChunks, outputChunks);
    }
    
    @Override
    public ECBlock[] getInputBlocks() {
        return this.inputBlocks;
    }
    
    @Override
    public ECBlock[] getOutputBlocks() {
        return this.outputBlocks;
    }
    
    @Override
    public void finish() {
    }
}
