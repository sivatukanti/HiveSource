// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.transform.impl;

import net.sf.cglib.core.TypeUtils;
import net.sf.cglib.transform.TransformingClassGenerator;
import net.sf.cglib.core.ClassGenerator;
import net.sf.cglib.transform.MethodFilterTransformer;
import net.sf.cglib.transform.MethodFilter;
import net.sf.cglib.transform.ClassTransformer;
import net.sf.cglib.core.DefaultGeneratorStrategy;

public class UndeclaredThrowableStrategy extends DefaultGeneratorStrategy
{
    private ClassTransformer t;
    private static final MethodFilter TRANSFORM_FILTER;
    
    public UndeclaredThrowableStrategy(final Class wrapper) {
        this.t = new UndeclaredThrowableTransformer(wrapper);
        this.t = new MethodFilterTransformer(UndeclaredThrowableStrategy.TRANSFORM_FILTER, this.t);
    }
    
    protected ClassGenerator transform(final ClassGenerator cg) throws Exception {
        return new TransformingClassGenerator(cg, this.t);
    }
    
    static {
        TRANSFORM_FILTER = new MethodFilter() {
            public boolean accept(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                return !TypeUtils.isPrivate(access) && name.indexOf(36) < 0;
            }
        };
    }
}
