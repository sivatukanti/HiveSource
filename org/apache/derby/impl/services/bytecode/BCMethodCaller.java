// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.bytecode;

class BCMethodCaller extends BCLocalField
{
    final short opcode;
    
    BCMethodCaller(final short opcode, final Type type, final int n) {
        super(type, n);
        this.opcode = opcode;
    }
}
