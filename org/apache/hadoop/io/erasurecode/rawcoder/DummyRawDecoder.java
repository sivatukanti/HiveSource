// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class DummyRawDecoder extends RawErasureDecoder
{
    public DummyRawDecoder(final ErasureCoderOptions coderOptions) {
        super(coderOptions);
    }
    
    @Override
    protected void doDecode(final ByteBufferDecodingState decodingState) {
    }
    
    @Override
    protected void doDecode(final ByteArrayDecodingState decodingState) {
    }
}
