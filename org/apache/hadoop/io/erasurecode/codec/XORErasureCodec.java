// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.codec;

import org.apache.hadoop.io.erasurecode.coder.XORErasureDecoder;
import org.apache.hadoop.io.erasurecode.coder.ErasureDecoder;
import org.apache.hadoop.io.erasurecode.coder.XORErasureEncoder;
import org.apache.hadoop.io.erasurecode.coder.ErasureEncoder;
import org.apache.hadoop.io.erasurecode.ErasureCodecOptions;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class XORErasureCodec extends ErasureCodec
{
    public XORErasureCodec(final Configuration conf, final ErasureCodecOptions options) {
        super(conf, options);
        assert options.getSchema().getNumParityUnits() == 1;
    }
    
    @Override
    public ErasureEncoder createEncoder() {
        return new XORErasureEncoder(this.getCoderOptions());
    }
    
    @Override
    public ErasureDecoder createDecoder() {
        return new XORErasureDecoder(this.getCoderOptions());
    }
}
