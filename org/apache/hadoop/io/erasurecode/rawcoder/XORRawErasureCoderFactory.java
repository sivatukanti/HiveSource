// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class XORRawErasureCoderFactory implements RawErasureCoderFactory
{
    public static final String CODER_NAME = "xor_java";
    
    @Override
    public RawErasureEncoder createEncoder(final ErasureCoderOptions coderOptions) {
        return new XORRawEncoder(coderOptions);
    }
    
    @Override
    public RawErasureDecoder createDecoder(final ErasureCoderOptions coderOptions) {
        return new XORRawDecoder(coderOptions);
    }
    
    @Override
    public String getCoderName() {
        return "xor_java";
    }
    
    @Override
    public String getCodecName() {
        return "xor";
    }
}
