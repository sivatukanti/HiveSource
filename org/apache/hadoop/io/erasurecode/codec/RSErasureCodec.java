// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.codec;

import org.apache.hadoop.io.erasurecode.coder.RSErasureDecoder;
import org.apache.hadoop.io.erasurecode.coder.ErasureDecoder;
import org.apache.hadoop.io.erasurecode.coder.RSErasureEncoder;
import org.apache.hadoop.io.erasurecode.coder.ErasureEncoder;
import org.apache.hadoop.io.erasurecode.ErasureCodecOptions;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class RSErasureCodec extends ErasureCodec
{
    public RSErasureCodec(final Configuration conf, final ErasureCodecOptions options) {
        super(conf, options);
    }
    
    @Override
    public ErasureEncoder createEncoder() {
        return new RSErasureEncoder(this.getCoderOptions());
    }
    
    @Override
    public ErasureDecoder createDecoder() {
        return new RSErasureDecoder(this.getCoderOptions());
    }
}
