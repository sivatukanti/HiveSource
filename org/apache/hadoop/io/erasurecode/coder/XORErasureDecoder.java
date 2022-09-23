// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.coder;

import org.apache.hadoop.io.erasurecode.ECBlock;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureDecoder;
import org.apache.hadoop.io.erasurecode.CodecUtil;
import org.apache.hadoop.io.erasurecode.ECBlockGroup;
import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class XORErasureDecoder extends ErasureDecoder
{
    public XORErasureDecoder(final ErasureCoderOptions options) {
        super(options);
    }
    
    @Override
    protected ErasureCodingStep prepareDecodingStep(final ECBlockGroup blockGroup) {
        final RawErasureDecoder rawDecoder = CodecUtil.createRawDecoder(this.getConf(), "xor", this.getOptions());
        final ECBlock[] inputBlocks = this.getInputBlocks(blockGroup);
        return new ErasureDecodingStep(inputBlocks, this.getErasedIndexes(inputBlocks), this.getOutputBlocks(blockGroup), rawDecoder);
    }
    
    @Override
    protected ECBlock[] getOutputBlocks(final ECBlockGroup blockGroup) {
        final int erasedNum = this.getNumErasedBlocks(blockGroup);
        final ECBlock[] outputBlocks = new ECBlock[erasedNum];
        int idx = 0;
        for (int i = 0; i < this.getNumParityUnits(); ++i) {
            if (blockGroup.getParityBlocks()[i].isErased()) {
                outputBlocks[idx++] = blockGroup.getParityBlocks()[i];
            }
        }
        for (int i = 0; i < this.getNumDataUnits(); ++i) {
            if (blockGroup.getDataBlocks()[i].isErased()) {
                outputBlocks[idx++] = blockGroup.getDataBlocks()[i];
            }
        }
        return outputBlocks;
    }
}
