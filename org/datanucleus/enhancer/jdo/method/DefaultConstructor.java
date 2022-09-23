// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.enhancer.DataNucleusEnhancer;
import org.datanucleus.asm.Label;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class DefaultConstructor extends ClassMethod
{
    public static DefaultConstructor getInstance(final ClassEnhancer enhancer) {
        return new DefaultConstructor(enhancer, "<init>", 4, null, null, null);
    }
    
    public DefaultConstructor(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
        super(enhancer, name, access, returnType, argTypes, argNames);
    }
    
    @Override
    public void execute() {
        this.visitor.visitCode();
        final Label l0 = new Label();
        this.visitor.visitLabel(l0);
        this.visitor.visitVarInsn(25, 0);
        final Class superclass = this.enhancer.getClassBeingEnhanced().getSuperclass();
        this.visitor.visitMethodInsn(183, superclass.getName().replace('.', '/'), "<init>", "()V");
        this.visitor.visitInsn(177);
        final Label l2 = new Label();
        this.visitor.visitLabel(l2);
        this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, l0, l2, 0);
        this.visitor.visitMaxs(1, 1);
        this.visitor.visitEnd();
    }
    
    @Override
    public void close() {
        if (DataNucleusEnhancer.LOGGER.isDebugEnabled()) {
            DataNucleusEnhancer.LOGGER.debug(DefaultConstructor.LOCALISER.msg("Enhancer.AddConstructor", this.getClassEnhancer().getClassName() + "()"));
        }
    }
}
