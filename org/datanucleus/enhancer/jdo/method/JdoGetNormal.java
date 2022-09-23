// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.enhancer.EnhanceUtils;
import org.datanucleus.asm.Label;
import org.datanucleus.asm.Type;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.enhancer.ClassMethod;

public class JdoGetNormal extends ClassMethod
{
    protected AbstractMemberMetaData fmd;
    
    public JdoGetNormal(final ClassEnhancer enhancer, final AbstractMemberMetaData fmd) {
        super(enhancer, enhancer.getNamer().getGetMethodPrefixMethodName() + fmd.getName(), (fmd.isPublic() ? 1 : 0) | (fmd.isProtected() ? 4 : 0) | (fmd.isPrivate() ? 2 : 0) | 0x8, fmd.getType(), null, null);
        this.argTypes = new Class[] { this.getClassEnhancer().getClassBeingEnhanced() };
        this.argNames = new String[] { "objPC" };
        this.fmd = fmd;
    }
    
    @Override
    public void execute() {
        this.visitor.visitCode();
        final String fieldTypeDesc = Type.getDescriptor(this.fmd.getType());
        final Label startLabel = new Label();
        this.visitor.visitLabel(startLabel);
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitFieldInsn(180, this.getClassEnhancer().getASMClassName(), this.fmd.getName(), fieldTypeDesc);
        EnhanceUtils.addReturnForType(this.visitor, (Class)this.returnType);
        final Label endLabel = new Label();
        this.visitor.visitLabel(endLabel);
        this.visitor.visitLocalVariable(this.argNames[0], this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel, 0);
        this.visitor.visitMaxs(1, 1);
        this.visitor.visitEnd();
    }
}
