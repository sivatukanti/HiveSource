// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
abstract class EncodingState
{
    RawErasureEncoder encoder;
    int encodeLength;
    
     <T> void checkParameters(final T[] inputs, final T[] outputs) {
        if (inputs.length != this.encoder.getNumDataUnits()) {
            throw new HadoopIllegalArgumentException("Invalid inputs length");
        }
        if (outputs.length != this.encoder.getNumParityUnits()) {
            throw new HadoopIllegalArgumentException("Invalid outputs length");
        }
    }
}
