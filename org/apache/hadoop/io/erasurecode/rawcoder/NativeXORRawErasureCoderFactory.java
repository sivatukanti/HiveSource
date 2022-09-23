// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class NativeXORRawErasureCoderFactory implements RawErasureCoderFactory
{
    public static final String CODER_NAME = "xor_native";
    
    @Override
    public RawErasureEncoder createEncoder(final ErasureCoderOptions coderOptions) {
        return new NativeXORRawEncoder(coderOptions);
    }
    
    @Override
    public RawErasureDecoder createDecoder(final ErasureCoderOptions coderOptions) {
        return new NativeXORRawDecoder(coderOptions);
    }
    
    @Override
    public String getCoderName() {
        return "xor_native";
    }
    
    @Override
    public String getCodecName() {
        return "xor";
    }
}
