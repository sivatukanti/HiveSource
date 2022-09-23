// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo.method;

import org.datanucleus.asm.Label;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.datanucleus.enhancer.ClassEnhancer;
import org.datanucleus.enhancer.ClassMethod;

public class WriteObject extends ClassMethod
{
    public static WriteObject getInstance(final ClassEnhancer enhancer) {
        return new WriteObject(enhancer, "writeObject", 2, null, new Class[] { ObjectOutputStream.class }, new String[] { "out" }, new String[] { IOException.class.getName().replace('.', '/') });
    }
    
    public WriteObject(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
        super(enhancer, name, access, returnType, argTypes, argNames);
    }
    
    public WriteObject(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames, final String[] exceptions) {
        super(enhancer, name, access, returnType, argTypes, argNames, exceptions);
    }
    
    @Override
    public void execute() {
        this.visitor.visitCode();
        final Label startLabel = new Label();
        this.visitor.visitLabel(startLabel);
        this.visitor.visitVarInsn(25, 0);
        this.visitor.visitMethodInsn(182, this.getClassEnhancer().getASMClassName(), this.getNamer().getPreSerializeMethodName(), "()V");
        this.visitor.visitVarInsn(25, 1);
        this.visitor.visitMethodInsn(182, "java/io/ObjectOutputStream", "defaultWriteObject", "()V");
        this.visitor.visitInsn(177);
        final Label endLabel = new Label();
        this.visitor.visitLabel(endLabel);
        this.visitor.visitLocalVariable("this", this.getClassEnhancer().getClassDescriptor(), null, startLabel, endLabel, 0);
        this.visitor.visitLocalVariable(this.argNames[0], "Ljava/io/ObjectOutputStream;", null, startLabel, endLabel, 1);
        this.visitor.visitMaxs(1, 2);
        this.visitor.visitEnd();
    }
}
