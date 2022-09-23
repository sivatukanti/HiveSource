// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.codec;

import org.apache.hadoop.io.erasurecode.grouper.BlockGrouper;
import org.apache.hadoop.io.erasurecode.coder.ErasureDecoder;
import org.apache.hadoop.io.erasurecode.coder.ErasureEncoder;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.io.erasurecode.ErasureCodecOptions;
import org.apache.hadoop.io.erasurecode.ECSchema;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public abstract class ErasureCodec
{
    private ECSchema schema;
    private ErasureCodecOptions codecOptions;
    private ErasureCoderOptions coderOptions;
    
    public ErasureCodec(final Configuration conf, final ErasureCodecOptions options) {
        this.schema = options.getSchema();
        this.codecOptions = options;
        final boolean allowChangeInputs = false;
        this.coderOptions = new ErasureCoderOptions(this.schema.getNumDataUnits(), this.schema.getNumParityUnits(), allowChangeInputs, false);
    }
    
    public String getName() {
        return this.schema.getCodecName();
    }
    
    public ECSchema getSchema() {
        return this.schema;
    }
    
    public ErasureCodecOptions getCodecOptions() {
        return this.codecOptions;
    }
    
    protected void setCodecOptions(final ErasureCodecOptions options) {
        this.codecOptions = options;
        this.schema = options.getSchema();
    }
    
    public ErasureCoderOptions getCoderOptions() {
        return this.coderOptions;
    }
    
    protected void setCoderOptions(final ErasureCoderOptions options) {
        this.coderOptions = options;
    }
    
    public abstract ErasureEncoder createEncoder();
    
    public abstract ErasureDecoder createDecoder();
    
    public BlockGrouper createBlockGrouper() {
        final BlockGrouper blockGrouper = new BlockGrouper();
        blockGrouper.setSchema(this.getSchema());
        return blockGrouper;
    }
}
