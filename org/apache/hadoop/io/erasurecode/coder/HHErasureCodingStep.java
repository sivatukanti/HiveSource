// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.coder;

import org.apache.hadoop.io.erasurecode.ECBlock;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public abstract class HHErasureCodingStep implements ErasureCodingStep
{
    private ECBlock[] inputBlocks;
    private ECBlock[] outputBlocks;
    private static final int SUB_PACKET_SIZE = 2;
    
    public HHErasureCodingStep(final ECBlock[] inputBlocks, final ECBlock[] outputBlocks) {
        this.inputBlocks = inputBlocks;
        this.outputBlocks = outputBlocks;
    }
    
    protected int getSubPacketSize() {
        return 2;
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
