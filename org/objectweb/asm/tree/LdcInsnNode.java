// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.tree;

import java.util.Map;
import org.objectweb.asm.MethodVisitor;

public class LdcInsnNode extends AbstractInsnNode
{
    public Object cst;
    
    public LdcInsnNode(final Object cst) {
        super(18);
        this.cst = cst;
    }
    
    public int getType() {
        return 8;
    }
    
    public void accept(final MethodVisitor methodVisitor) {
        methodVisitor.visitLdcInsn(this.cst);
    }
    
    public AbstractInsnNode clone(final Map map) {
        return new LdcInsnNode(this.cst);
    }
}
