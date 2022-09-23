// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class DummyRawEncoder extends RawErasureEncoder
{
    public DummyRawEncoder(final ErasureCoderOptions coderOptions) {
        super(coderOptions);
    }
    
    @Override
    protected void doEncode(final ByteArrayEncodingState encodingState) {
    }
    
    @Override
    protected void doEncode(final ByteBufferEncodingState encodingState) {
    }
}
