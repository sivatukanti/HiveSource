// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public interface RawErasureCoderFactory
{
    RawErasureEncoder createEncoder(final ErasureCoderOptions p0);
    
    RawErasureDecoder createDecoder(final ErasureCoderOptions p0);
    
    String getCoderName();
    
    String getCodecName();
}
