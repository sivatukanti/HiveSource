// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.coder;

import org.apache.hadoop.io.erasurecode.ECBlock;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureDecoder;
import org.apache.hadoop.io.erasurecode.rawcoder.DummyRawDecoder;
import org.apache.hadoop.io.erasurecode.ECBlockGroup;
import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;

public class DummyErasureDecoder extends ErasureDecoder
{
    public DummyErasureDecoder(final ErasureCoderOptions options) {
        super(options);
    }
    
    @Override
    protected ErasureCodingStep prepareDecodingStep(final ECBlockGroup blockGroup) {
        final RawErasureDecoder rawDecoder = new DummyRawDecoder(this.getOptions());
        final ECBlock[] inputBlocks = this.getInputBlocks(blockGroup);
        return new ErasureDecodingStep(inputBlocks, this.getErasedIndexes(inputBlocks), this.getOutputBlocks(blockGroup), rawDecoder);
    }
}
