// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.transform;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassAdapter;

public abstract class AbstractClassTransformer extends ClassAdapter implements ClassTransformer
{
    protected AbstractClassTransformer() {
        super(null);
    }
    
    public void setTarget(final ClassVisitor target) {
        this.cv = target;
    }
}
