// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.transform;

import org.objectweb.asm.ClassVisitor;

public interface ClassTransformer extends ClassVisitor
{
    void setTarget(final ClassVisitor p0);
}
