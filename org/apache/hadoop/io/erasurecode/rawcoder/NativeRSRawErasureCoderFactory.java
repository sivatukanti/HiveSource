// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class NativeRSRawErasureCoderFactory implements RawErasureCoderFactory
{
    public static final String CODER_NAME = "rs_native";
    
    @Override
    public RawErasureEncoder createEncoder(final ErasureCoderOptions coderOptions) {
        return new NativeRSRawEncoder(coderOptions);
    }
    
    @Override
    public RawErasureDecoder createDecoder(final ErasureCoderOptions coderOptions) {
        return new NativeRSRawDecoder(coderOptions);
    }
    
    @Override
    public String getCoderName() {
        return "rs_native";
    }
    
    @Override
    public String getCodecName() {
        return "rs";
    }
}
