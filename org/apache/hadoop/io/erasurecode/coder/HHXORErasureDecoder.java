// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.coder;

import org.apache.hadoop.io.erasurecode.CodecUtil;
import org.apache.hadoop.io.erasurecode.ECBlock;
import org.apache.hadoop.io.erasurecode.ECBlockGroup;
import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureEncoder;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureDecoder;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class HHXORErasureDecoder extends ErasureDecoder
{
    private RawErasureDecoder rsRawDecoder;
    private RawErasureEncoder xorRawEncoder;
    
    public HHXORErasureDecoder(final ErasureCoderOptions options) {
        super(options);
    }
    
    @Override
    protected ErasureCodingStep prepareDecodingStep(final ECBlockGroup blockGroup) {
        final ECBlock[] inputBlocks = this.getInputBlocks(blockGroup);
        final ECBlock[] outputBlocks = this.getOutputBlocks(blockGroup);
        final RawErasureDecoder rawDecoder = this.checkCreateRSRawDecoder();
        final RawErasureEncoder rawEncoder = this.checkCreateXorRawEncoder();
        return new HHXORErasureDecodingStep(inputBlocks, this.getErasedIndexes(inputBlocks), outputBlocks, rawDecoder, rawEncoder);
    }
    
    private RawErasureDecoder checkCreateRSRawDecoder() {
        if (this.rsRawDecoder == null) {
            this.rsRawDecoder = CodecUtil.createRawDecoder(this.getConf(), "rs", this.getOptions());
        }
        return this.rsRawDecoder;
    }
    
    private RawErasureEncoder checkCreateXorRawEncoder() {
        if (this.xorRawEncoder == null) {
            this.xorRawEncoder = CodecUtil.createRawEncoder(this.getConf(), "xor", this.getOptions());
        }
        return this.xorRawEncoder;
    }
    
    @Override
    public boolean preferDirectBuffer() {
        return false;
    }
    
    @Override
    public void release() {
        if (this.rsRawDecoder != null) {
            this.rsRawDecoder.release();
        }
        if (this.xorRawEncoder != null) {
            this.xorRawEncoder.release();
        }
    }
}
