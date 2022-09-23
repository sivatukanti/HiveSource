// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder;

import org.apache.hadoop.io.erasurecode.rawcoder.util.DumpUtil;
import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.io.erasurecode.rawcoder.util.RSUtil;
import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class RSRawEncoder extends RawErasureEncoder
{
    private byte[] encodeMatrix;
    private byte[] gfTables;
    
    public RSRawEncoder(final ErasureCoderOptions coderOptions) {
        super(coderOptions);
        if (this.getNumAllUnits() >= RSUtil.GF.getFieldSize()) {
            throw new HadoopIllegalArgumentException("Invalid numDataUnits and numParityUnits");
        }
        RSUtil.genCauchyMatrix(this.encodeMatrix = new byte[this.getNumAllUnits() * this.getNumDataUnits()], this.getNumAllUnits(), this.getNumDataUnits());
        if (this.allowVerboseDump()) {
            DumpUtil.dumpMatrix(this.encodeMatrix, this.getNumDataUnits(), this.getNumAllUnits());
        }
        this.gfTables = new byte[this.getNumAllUnits() * this.getNumDataUnits() * 32];
        RSUtil.initTables(this.getNumDataUnits(), this.getNumParityUnits(), this.encodeMatrix, this.getNumDataUnits() * this.getNumDataUnits(), this.gfTables);
        if (this.allowVerboseDump()) {
            System.out.println(DumpUtil.bytesToHex(this.gfTables, -1));
        }
    }
    
    @Override
    protected void doEncode(final ByteBufferEncodingState encodingState) {
        CoderUtil.resetOutputBuffers(encodingState.outputs, encodingState.encodeLength);
        RSUtil.encodeData(this.gfTables, encodingState.inputs, encodingState.outputs);
    }
    
    @Override
    protected void doEncode(final ByteArrayEncodingState encodingState) {
        CoderUtil.resetOutputBuffers(encodingState.outputs, encodingState.outputOffsets, encodingState.encodeLength);
        RSUtil.encodeData(this.gfTables, encodingState.encodeLength, encodingState.inputs, encodingState.inputOffsets, encodingState.outputs, encodingState.outputOffsets);
    }
}
