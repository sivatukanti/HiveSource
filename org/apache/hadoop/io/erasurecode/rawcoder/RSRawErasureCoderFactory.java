// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class RSRawErasureCoderFactory implements RawErasureCoderFactory
{
    public static final String CODER_NAME = "rs_java";
    
    @Override
    public RawErasureEncoder createEncoder(final ErasureCoderOptions coderOptions) {
        return new RSRawEncoder(coderOptions);
    }
    
    @Override
    public RawErasureDecoder createDecoder(final ErasureCoderOptions coderOptions) {
        return new RSRawDecoder(coderOptions);
    }
    
    @Override
    public String getCoderName() {
        return "rs_java";
    }
    
    @Override
    public String getCodecName() {
        return "rs";
    }
}
