// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.proxy;

import net.sf.cglib.core.ReflectUtils;
import java.lang.reflect.Method;
import org.objectweb.asm.ClassVisitor;

class MixinBeanEmitter extends MixinEmitter
{
    public MixinBeanEmitter(final ClassVisitor v, final String className, final Class[] classes) {
        super(v, className, classes, null);
    }
    
    protected Class[] getInterfaces(final Class[] classes) {
        return null;
    }
    
    protected Method[] getMethods(final Class type) {
        return ReflectUtils.getPropertyMethods(ReflectUtils.getBeanProperties(type), true, true);
    }
}
