// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.codec;

import org.apache.hadoop.io.erasurecode.coder.DummyErasureDecoder;
import org.apache.hadoop.io.erasurecode.coder.ErasureDecoder;
import org.apache.hadoop.io.erasurecode.coder.DummyErasureEncoder;
import org.apache.hadoop.io.erasurecode.coder.ErasureEncoder;
import org.apache.hadoop.io.erasurecode.ErasureCodecOptions;
import org.apache.hadoop.conf.Configuration;

public class DummyErasureCodec extends ErasureCodec
{
    public DummyErasureCodec(final Configuration conf, final ErasureCodecOptions options) {
        super(conf, options);
    }
    
    @Override
    public ErasureEncoder createEncoder() {
        return new DummyErasureEncoder(this.getCoderOptions());
    }
    
    @Override
    public ErasureDecoder createDecoder() {
        return new DummyErasureDecoder(this.getCoderOptions());
    }
}
