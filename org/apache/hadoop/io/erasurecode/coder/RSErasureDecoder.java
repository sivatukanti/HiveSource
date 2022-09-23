// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.coder;

import org.apache.hadoop.io.erasurecode.CodecUtil;
import org.apache.hadoop.io.erasurecode.ECBlock;
import org.apache.hadoop.io.erasurecode.ECBlockGroup;
import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureDecoder;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class RSErasureDecoder extends ErasureDecoder
{
    private RawErasureDecoder rsRawDecoder;
    
    public RSErasureDecoder(final ErasureCoderOptions options) {
        super(options);
    }
    
    @Override
    protected ErasureCodingStep prepareDecodingStep(final ECBlockGroup blockGroup) {
        final ECBlock[] inputBlocks = this.getInputBlocks(blockGroup);
        final ECBlock[] outputBlocks = this.getOutputBlocks(blockGroup);
        final RawErasureDecoder rawDecoder = this.checkCreateRSRawDecoder();
        return new ErasureDecodingStep(inputBlocks, this.getErasedIndexes(inputBlocks), outputBlocks, rawDecoder);
    }
    
    private RawErasureDecoder checkCreateRSRawDecoder() {
        if (this.rsRawDecoder == null) {
            this.rsRawDecoder = CodecUtil.createRawDecoder(this.getConf(), "rs", this.getOptions());
        }
        return this.rsRawDecoder;
    }
    
    @Override
    public void release() {
        if (this.rsRawDecoder != null) {
            this.rsRawDecoder.release();
        }
    }
}
