// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.coder;

import org.apache.hadoop.io.erasurecode.CodecUtil;
import org.apache.hadoop.io.erasurecode.ECBlock;
import org.apache.hadoop.io.erasurecode.ECBlockGroup;
import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.io.erasurecode.rawcoder.RawErasureEncoder;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class HHXORErasureEncoder extends ErasureEncoder
{
    private RawErasureEncoder rsRawEncoder;
    private RawErasureEncoder xorRawEncoder;
    
    public HHXORErasureEncoder(final ErasureCoderOptions options) {
        super(options);
    }
    
    @Override
    protected ErasureCodingStep prepareEncodingStep(final ECBlockGroup blockGroup) {
        final RawErasureEncoder rsRawEncoderTmp = this.checkCreateRSRawEncoder();
        final RawErasureEncoder xorRawEncoderTmp = this.checkCreateXorRawEncoder();
        final ECBlock[] inputBlocks = this.getInputBlocks(blockGroup);
        return new HHXORErasureEncodingStep(inputBlocks, this.getOutputBlocks(blockGroup), rsRawEncoderTmp, xorRawEncoderTmp);
    }
    
    private RawErasureEncoder checkCreateRSRawEncoder() {
        if (this.rsRawEncoder == null) {
            this.rsRawEncoder = CodecUtil.createRawEncoder(this.getConf(), "rs", this.getOptions());
        }
        return this.rsRawEncoder;
    }
    
    private RawErasureEncoder checkCreateXorRawEncoder() {
        if (this.xorRawEncoder == null) {
            this.xorRawEncoder = CodecUtil.createRawEncoder(this.getConf(), "xor", this.getOptions());
        }
        return this.xorRawEncoder;
    }
    
    @Override
    public void release() {
        if (this.rsRawEncoder != null) {
            this.rsRawEncoder.release();
        }
        if (this.xorRawEncoder != null) {
            this.xorRawEncoder.release();
        }
    }
}
