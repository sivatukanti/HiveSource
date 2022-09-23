// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.bytecode;

class Conditional
{
    private static final int BRANCH16LIMIT = 32767;
    private final Conditional parent;
    private final int if_pc;
    private Type[] stack;
    private int thenGoto_pc;
    
    Conditional(final Conditional parent, final CodeChunk codeChunk, final short n, final Type[] stack) {
        this.parent = parent;
        this.if_pc = codeChunk.getPC();
        this.stack = stack;
        codeChunk.addInstrU2(n, 0);
    }
    
    Type[] startElse(final BCMethod bcMethod, final CodeChunk codeChunk, final Type[] stack) {
        codeChunk.addInstrU2((short)167, 0);
        this.fillIn(bcMethod, codeChunk, this.if_pc, codeChunk.getPC());
        this.thenGoto_pc = codeChunk.getPC() - 3;
        final Type[] stack2 = this.stack;
        this.stack = stack;
        return stack2;
    }
    
    Conditional end(final BCMethod bcMethod, final CodeChunk codeChunk, final Type[] array, final int n) {
        int n2;
        if (this.thenGoto_pc == 0) {
            n2 = this.if_pc;
        }
        else {
            n2 = this.thenGoto_pc;
        }
        this.fillIn(bcMethod, codeChunk, n2, codeChunk.getPC());
        return this.parent;
    }
    
    private void fillIn(final BCMethod bcMethod, final CodeChunk codeChunk, final int n, final int n2) {
        int n3 = n2 - n;
        short opcode = codeChunk.getOpcode(n);
        if (n3 <= 32767) {
            codeChunk.insertCodeSpace(n, 0).addInstrU2(opcode, n3);
            return;
        }
        if (opcode != 167) {
            if (n2 + 5 >= 65535) {
                bcMethod.cb.addLimitExceeded(bcMethod, "branch_target", 65535, n2 + 5);
            }
            switch (opcode) {
                case 199: {
                    opcode = 198;
                    break;
                }
                case 153: {
                    opcode = 154;
                    break;
                }
            }
            final CodeChunk insertCodeSpace = codeChunk.insertCodeSpace(n, 5);
            insertCodeSpace.addInstrU2(opcode, 8);
            n3 += 2;
            insertCodeSpace.addInstrU4((short)200, n3);
            return;
        }
        final CodeChunk insertCodeSpace2 = codeChunk.insertCodeSpace(n, 2);
        n3 += 2;
        insertCodeSpace2.addInstrU4((short)200, n3);
        int n4 = insertCodeSpace2.getPC() - this.if_pc;
        if (n4 <= 32769) {
            this.fillIn(bcMethod, codeChunk, this.if_pc, insertCodeSpace2.getPC());
            return;
        }
        final CodeChunk insertCodeSpace3 = codeChunk.insertCodeSpace(this.if_pc + 3, 0);
        n4 -= 3;
        insertCodeSpace3.addInstrU4((short)200, n4);
    }
}
