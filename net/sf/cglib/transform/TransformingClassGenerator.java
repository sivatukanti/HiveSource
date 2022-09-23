// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.transform;

import org.objectweb.asm.ClassVisitor;
import net.sf.cglib.core.ClassGenerator;

public class TransformingClassGenerator implements ClassGenerator
{
    private ClassGenerator gen;
    private ClassTransformer t;
    
    public TransformingClassGenerator(final ClassGenerator gen, final ClassTransformer t) {
        this.gen = gen;
        this.t = t;
    }
    
    public void generateClass(final ClassVisitor v) throws Exception {
        this.t.setTarget(v);
        this.gen.generateClass((ClassVisitor)this.t);
    }
}
