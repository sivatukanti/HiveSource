// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.tree;

import java.util.Map;
import org.objectweb.asm.MethodVisitor;

public class MethodInsnNode extends AbstractInsnNode
{
    public String owner;
    public String name;
    public String desc;
    
    public MethodInsnNode(final int n, final String owner, final String name, final String desc) {
        super(n);
        this.owner = owner;
        this.name = name;
        this.desc = desc;
    }
    
    public void setOpcode(final int opcode) {
        this.opcode = opcode;
    }
    
    public int getType() {
        return 5;
    }
    
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitMethodInsn(this.opcode, this.owner, this.name, this.desc);
    }
    
    public AbstractInsnNode clone(final Map map) {
        return new MethodInsnNode(this.opcode, this.owner, this.name, this.desc);
    }
}
