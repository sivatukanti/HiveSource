// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class RSLegacyRawErasureCoderFactory implements RawErasureCoderFactory
{
    public static final String CODER_NAME = "rs-legacy_java";
    
    @Override
    public RawErasureEncoder createEncoder(final ErasureCoderOptions coderOptions) {
        return new RSLegacyRawEncoder(coderOptions);
    }
    
    @Override
    public RawErasureDecoder createDecoder(final ErasureCoderOptions coderOptions) {
        return new RSLegacyRawDecoder(coderOptions);
    }
    
    @Override
    public String getCoderName() {
        return "rs-legacy_java";
    }
    
    @Override
    public String getCodecName() {
        return "rs-legacy";
    }
}
