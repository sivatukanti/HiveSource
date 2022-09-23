// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class DummyRawErasureCoderFactory implements RawErasureCoderFactory
{
    public static final String CODER_NAME = "dummy_dummy";
    
    @Override
    public RawErasureEncoder createEncoder(final ErasureCoderOptions coderOptions) {
        return new DummyRawEncoder(coderOptions);
    }
    
    @Override
    public RawErasureDecoder createDecoder(final ErasureCoderOptions coderOptions) {
        return new DummyRawDecoder(coderOptions);
    }
    
    @Override
    public String getCoderName() {
        return "dummy_dummy";
    }
    
    @Override
    public String getCodecName() {
        return "dummy";
    }
}
