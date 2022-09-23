// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.tree;

import java.util.List;
import java.util.Map;
import org.objectweb.asm.MethodVisitor;

public abstract class AbstractInsnNode
{
    public static final int INSN = 0;
    public static final int INT_INSN = 1;
    public static final int VAR_INSN = 2;
    public static final int TYPE_INSN = 3;
    public static final int FIELD_INSN = 4;
    public static final int METHOD_INSN = 5;
    public static final int JUMP_INSN = 6;
    public static final int LABEL = 7;
    public static final int LDC_INSN = 8;
    public static final int IINC_INSN = 9;
    public static final int TABLESWITCH_INSN = 10;
    public static final int LOOKUPSWITCH_INSN = 11;
    public static final int MULTIANEWARRAY_INSN = 12;
    public static final int FRAME = 13;
    public static final int LINE = 14;
    protected int opcode;
    AbstractInsnNode prev;
    AbstractInsnNode next;
    int index;
    
    protected AbstractInsnNode(final int opcode) {
        this.opcode = opcode;
        this.index = -1;
    }
    
    public int getOpcode() {
        return this.opcode;
    }
    
    public abstract int getType();
    
    public AbstractInsnNode getPrevious() {
        return this.prev;
    }
    
    public AbstractInsnNode getNext() {
        return this.next;
    }
    
    public abstract void accept(final MethodVisitor p0);
    
    public abstract AbstractInsnNode clone(final Map p0);
    
    static LabelNode clone(final LabelNode labelNode, final Map map) {
        return map.get(labelNode);
    }
    
    static LabelNode[] clone(final List list, final Map map) {
        final LabelNode[] array = new LabelNode[list.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = map.get(list.get(i));
        }
        return array;
    }
}
