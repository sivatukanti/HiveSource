// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.compress;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public interface DirectDecompressionCodec extends CompressionCodec
{
    DirectDecompressor createDirectDecompressor();
}
