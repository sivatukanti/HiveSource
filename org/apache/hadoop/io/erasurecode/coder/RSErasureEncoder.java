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
public class RSErasureEncoder extends ErasureEncoder
{
    private RawErasureEncoder rawEncoder;
    
    public RSErasureEncoder(final ErasureCoderOptions options) {
        super(options);
    }
    
    @Override
    protected ErasureCodingStep prepareEncodingStep(final ECBlockGroup blockGroup) {
        final RawErasureEncoder rawEncoder = this.checkCreateRSRawEncoder();
        final ECBlock[] inputBlocks = this.getInputBlocks(blockGroup);
        return new ErasureEncodingStep(inputBlocks, this.getOutputBlocks(blockGroup), rawEncoder);
    }
    
    private RawErasureEncoder checkCreateRSRawEncoder() {
        if (this.rawEncoder == null) {
            this.rawEncoder = CodecUtil.createRawEncoder(this.getConf(), "rs", this.getOptions());
        }
        return this.rawEncoder;
    }
    
    @Override
    public void release() {
        if (this.rawEncoder != null) {
            this.rawEncoder.release();
        }
    }
    
    @Override
    public boolean preferDirectBuffer() {
        return false;
    }
}
