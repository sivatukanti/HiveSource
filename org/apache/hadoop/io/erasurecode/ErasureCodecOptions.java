// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode;

import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class ErasureCodecOptions
{
    private ECSchema schema;
    
    public ErasureCodecOptions(final ECSchema schema) {
        this.schema = schema;
    }
    
    public ECSchema getSchema() {
        return this.schema;
    }
}
