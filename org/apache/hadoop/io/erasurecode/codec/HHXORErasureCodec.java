// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.codec;

import org.apache.hadoop.io.erasurecode.coder.HHXORErasureDecoder;
import org.apache.hadoop.io.erasurecode.coder.ErasureDecoder;
import org.apache.hadoop.io.erasurecode.coder.HHXORErasureEncoder;
import org.apache.hadoop.io.erasurecode.coder.ErasureEncoder;
import org.apache.hadoop.io.erasurecode.ErasureCodecOptions;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class HHXORErasureCodec extends ErasureCodec
{
    public HHXORErasureCodec(final Configuration conf, final ErasureCodecOptions options) {
        super(conf, options);
    }
    
    @Override
    public ErasureEncoder createEncoder() {
        return new HHXORErasureEncoder(this.getCoderOptions());
    }
    
    @Override
    public ErasureDecoder createDecoder() {
        return new HHXORErasureDecoder(this.getCoderOptions());
    }
}
