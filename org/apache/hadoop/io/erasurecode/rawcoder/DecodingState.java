// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
class DecodingState
{
    RawErasureDecoder decoder;
    int decodeLength;
    
     <T> void checkParameters(final T[] inputs, final int[] erasedIndexes, final T[] outputs) {
        if (inputs.length != this.decoder.getNumParityUnits() + this.decoder.getNumDataUnits()) {
            throw new IllegalArgumentException("Invalid inputs length");
        }
        if (erasedIndexes.length != outputs.length) {
            throw new HadoopIllegalArgumentException("erasedIndexes and outputs mismatch in length");
        }
        if (erasedIndexes.length > this.decoder.getNumParityUnits()) {
            throw new HadoopIllegalArgumentException("Too many erased, not recoverable");
        }
    }
}
