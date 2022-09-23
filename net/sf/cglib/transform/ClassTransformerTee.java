// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.transform;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassAdapter;

public class ClassTransformerTee extends ClassAdapter implements ClassTransformer
{
    private ClassVisitor branch;
    
    public ClassTransformerTee(final ClassVisitor branch) {
        super(null);
        this.branch = branch;
    }
    
    public void setTarget(final ClassVisitor target) {
        this.cv = (ClassVisitor)new ClassVisitorTee(this.branch, target);
    }
}
