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
public abstract class ErasureDecoder extends Configured implements ErasureCoder
{
    private final int numDataUnits;
    private final int numParityUnits;
    private final ErasureCoderOptions options;
    
    public ErasureDecoder(final ErasureCoderOptions options) {
        this.options = options;
        this.numDataUnits = options.getNumDataUnits();
        this.numParityUnits = options.getNumParityUnits();
    }
    
    @Override
    public ErasureCodingStep calculateCoding(final ECBlockGroup blockGroup) {
        return this.prepareDecodingStep(blockGroup);
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
        final ECBlock[] inputBlocks = new ECBlock[this.getNumDataUnits() + this.getNumParityUnits()];
        System.arraycopy(blockGroup.getDataBlocks(), 0, inputBlocks, 0, this.getNumDataUnits());
        System.arraycopy(blockGroup.getParityBlocks(), 0, inputBlocks, this.getNumDataUnits(), this.getNumParityUnits());
        return inputBlocks;
    }
    
    protected ECBlock[] getOutputBlocks(final ECBlockGroup blockGroup) {
        final ECBlock[] outputBlocks = new ECBlock[this.getNumErasedBlocks(blockGroup)];
        int idx = 0;
        for (int i = 0; i < this.getNumDataUnits(); ++i) {
            if (blockGroup.getDataBlocks()[i].isErased()) {
                outputBlocks[idx++] = blockGroup.getDataBlocks()[i];
            }
        }
        for (int i = 0; i < this.getNumParityUnits(); ++i) {
            if (blockGroup.getParityBlocks()[i].isErased()) {
                outputBlocks[idx++] = blockGroup.getParityBlocks()[i];
            }
        }
        return outputBlocks;
    }
    
    @Override
    public boolean preferDirectBuffer() {
        return false;
    }
    
    @Override
    public void release() {
    }
    
    protected abstract ErasureCodingStep prepareDecodingStep(final ECBlockGroup p0);
    
    protected int getNumErasedBlocks(final ECBlockGroup blockGroup) {
        int num = getNumErasedBlocks(blockGroup.getParityBlocks());
        num += getNumErasedBlocks(blockGroup.getDataBlocks());
        return num;
    }
    
    protected static int getNumErasedBlocks(final ECBlock[] inputBlocks) {
        int numErased = 0;
        for (int i = 0; i < inputBlocks.length; ++i) {
            if (inputBlocks[i].isErased()) {
                ++numErased;
            }
        }
        return numErased;
    }
    
    protected int[] getErasedIndexes(final ECBlock[] inputBlocks) {
        final int numErased = getNumErasedBlocks(inputBlocks);
        if (numErased == 0) {
            return new int[0];
        }
        final int[] erasedIndexes = new int[numErased];
        for (int i = 0, j = 0; i < inputBlocks.length && j < erasedIndexes.length; ++i) {
            if (inputBlocks[i].isErased()) {
                erasedIndexes[j++] = i;
            }
        }
        return erasedIndexes;
    }
    
    protected ECBlock[] getErasedBlocks(final ECBlock[] inputBlocks) {
        final int numErased = getNumErasedBlocks(inputBlocks);
        if (numErased == 0) {
            return new ECBlock[0];
        }
        final ECBlock[] erasedBlocks = new ECBlock[numErased];
        for (int i = 0, j = 0; i < inputBlocks.length && j < erasedBlocks.length; ++i) {
            if (inputBlocks[i].isErased()) {
                erasedBlocks[j++] = inputBlocks[i];
            }
        }
        return erasedBlocks;
    }
}
