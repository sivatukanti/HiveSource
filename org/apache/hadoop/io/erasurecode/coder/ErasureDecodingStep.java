// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.coder;

import java.io.IOException;
import org.apache.hadoop.io.erasurecode.ECChunk;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureDecoder;
import org.apache.hadoop.io.erasurecode.ECBlock;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class ErasureDecodingStep implements ErasureCodingStep
{
    private ECBlock[] inputBlocks;
    private ECBlock[] outputBlocks;
    private int[] erasedIndexes;
    private RawErasureDecoder rawDecoder;
    
    public ErasureDecodingStep(final ECBlock[] inputBlocks, final int[] erasedIndexes, final ECBlock[] outputBlocks, final RawErasureDecoder rawDecoder) {
        this.inputBlocks = inputBlocks;
        this.outputBlocks = outputBlocks;
        this.erasedIndexes = erasedIndexes;
        this.rawDecoder = rawDecoder;
    }
    
    @Override
    public void performCoding(final ECChunk[] inputChunks, final ECChunk[] outputChunks) throws IOException {
        this.rawDecoder.decode(inputChunks, this.erasedIndexes, outputChunks);
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
