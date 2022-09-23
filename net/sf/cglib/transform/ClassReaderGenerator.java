// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.transform;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import net.sf.cglib.core.ClassGenerator;

public class ClassReaderGenerator implements ClassGenerator
{
    private final ClassReader r;
    private final Attribute[] attrs;
    private final int flags;
    
    public ClassReaderGenerator(final ClassReader r, final int flags) {
        this(r, null, flags);
    }
    
    public ClassReaderGenerator(final ClassReader r, final Attribute[] attrs, final int flags) {
        this.r = r;
        this.attrs = ((attrs != null) ? attrs : new Attribute[0]);
        this.flags = flags;
    }
    
    public void generateClass(final ClassVisitor v) {
        this.r.accept(v, this.attrs, this.flags);
    }
}
