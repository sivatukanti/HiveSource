// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.tree;

import java.util.Map;
import org.objectweb.asm.MethodVisitor;

public class JumpInsnNode extends AbstractInsnNode
{
    public LabelNode label;
    
    public JumpInsnNode(final int n, final LabelNode label) {
        super(n);
        this.label = label;
    }
    
    public void setOpcode(final int opcode) {
        this.opcode = opcode;
    }
    
    public int getType() {
        return 6;
    }
    
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitJumpInsn(this.opcode, this.label.getLabel());
    }
    
    public AbstractInsnNode clone(final Map map) {
        return new JumpInsnNode(this.opcode, AbstractInsnNode.clone(this.label, map));
    }
}
