// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.coder;

import org.apache.hadoop.io.erasurecode.ECBlock;
import org.apache.hadoop.io.erasurecode.ECBlockGroup;
import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configured;

@InterfaceAudience.Private
public abstract class ErasureEncoder extends Configured implements ErasureCoder
{
    private final int numDataUnits;
    private final int numParityUnits;
    private final ErasureCoderOptions options;
    
    public ErasureEncoder(final ErasureCoderOptions options) {
        this.options = options;
        this.numDataUnits = options.getNumDataUnits();
        this.numParityUnits = options.getNumParityUnits();
    }
    
    @Override
    public ErasureCodingStep calculateCoding(final ECBlockGroup blockGroup) {
        return this.prepareEncodingStep(blockGroup);
    }
    
    @Override
    public int getNumDataUnits() {
        return this.numDataUnits;
    }
    
    @Override
    public int getNumParityUnits() {
        return this.numParityUnits;
    }
    
    @Override
    public ErasureCoderOptions getOptions() {
        return this.options;
    }
    
    protected ECBlock[] getInputBlocks(final ECBlockGroup blockGroup) {
        return blockGroup.getDataBlocks();
    }
    
    protected ECBlock[] getOutputBlocks(final ECBlockGroup blockGroup) {
        return blockGroup.getParityBlocks();
    }
    
    @Override
    public boolean preferDirectBuffer() {
        return false;
    }
    
    @Override
    public void release() {
    }
    
    protected abstract ErasureCodingStep prepareEncodingStep(final ECBlockGroup p0);
}
