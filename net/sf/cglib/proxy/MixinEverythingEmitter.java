// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.proxy;

import net.sf.cglib.core.Predicate;
import net.sf.cglib.core.CollectionUtils;
import net.sf.cglib.core.RejectModifierPredicate;
import java.util.Collection;
import java.util.Arrays;
import java.lang.reflect.Method;
import java.util.List;
import net.sf.cglib.core.ReflectUtils;
import java.util.ArrayList;
import org.objectweb.asm.ClassVisitor;

class MixinEverythingEmitter extends MixinEmitter
{
    public MixinEverythingEmitter(final ClassVisitor v, final String className, final Class[] classes) {
        super(v, className, classes, null);
    }
    
    protected Class[] getInterfaces(final Class[] classes) {
        final List list = new ArrayList();
        for (int i = 0; i < classes.length; ++i) {
            ReflectUtils.addAllInterfaces(classes[i], list);
        }
        return list.toArray(new Class[list.size()]);
    }
    
    protected Method[] getMethods(final Class type) {
        final List methods = new ArrayList(Arrays.asList(type.getMethods()));
        CollectionUtils.filter(methods, new RejectModifierPredicate(24));
        return methods.toArray(new Method[methods.size()]);
    }
}
