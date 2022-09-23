// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.depend.constantpool;

import java.io.IOException;
import java.io.DataInputStream;

public class MethodHandleCPInfo extends ConstantPoolEntry
{
    private ConstantPoolEntry reference;
    private ReferenceKind referenceKind;
    private int referenceIndex;
    private int nameAndTypeIndex;
    
    public MethodHandleCPInfo() {
        super(15, 1);
    }
    
    @Override
    public void read(final DataInputStream cpStream) throws IOException {
        this.referenceKind = ReferenceKind.values()[cpStream.readUnsignedByte() - 1];
        this.referenceIndex = cpStream.readUnsignedShort();
    }
    
    @Override
    public String toString() {
        String value;
        if (this.isResolved()) {
            value = "MethodHandle : " + this.reference.toString();
        }
        else {
            value = "MethodHandle : Reference kind = " + this.referenceKind + "Reference index = " + this.referenceIndex;
        }
        return value;
    }
    
    @Override
    public void resolve(final ConstantPool constantPool) {
        (this.reference = constantPool.getEntry(this.referenceIndex)).resolve(constantPool);
        super.resolve(constantPool);
    }
    
    public enum ReferenceKind
    {
        REF_getField(1), 
        REF_getStatic(2), 
        REF_putField(3), 
        REF_putStatic(4), 
        REF_invokeVirtual(5), 
        REF_invokeStatic(6), 
        REF_invokeSpecial(7), 
        REF_newInvokeSpecial(8), 
        REF_invokeInterface(9);
        
        private final int referenceKind;
        
        private ReferenceKind(final int referenceKind) {
            this.referenceKind = referenceKind;
        }
    }
}
