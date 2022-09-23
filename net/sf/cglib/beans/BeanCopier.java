// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.beans;

import net.sf.cglib.core.MethodInfo;
import net.sf.cglib.core.Local;
import java.util.Map;
import net.sf.cglib.core.CodeEmitter;
import java.lang.reflect.Member;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.core.EmitUtils;
import net.sf.cglib.core.ClassEmitter;
import org.objectweb.asm.ClassVisitor;
import java.lang.reflect.Modifier;
import net.sf.cglib.core.AbstractClassGenerator;
import net.sf.cglib.core.Constants;
import net.sf.cglib.core.TypeUtils;
import net.sf.cglib.core.KeyFactory;
import net.sf.cglib.core.Converter;
import net.sf.cglib.core.Signature;
import org.objectweb.asm.Type;

public abstract class BeanCopier
{
    private static final BeanCopierKey KEY_FACTORY;
    private static final Type CONVERTER;
    private static final Type BEAN_COPIER;
    private static final Signature COPY;
    private static final Signature CONVERT;
    
    public static BeanCopier create(final Class source, final Class target, final boolean useConverter) {
        final Generator gen = new Generator();
        gen.setSource(source);
        gen.setTarget(target);
        gen.setUseConverter(useConverter);
        return gen.create();
    }
    
    public abstract void copy(final Object p0, final Object p1, final Converter p2);
    
    static {
        KEY_FACTORY = (BeanCopierKey)KeyFactory.create(BeanCopierKey.class);
        CONVERTER = TypeUtils.parseType("net.sf.cglib.core.Converter");
        BEAN_COPIER = TypeUtils.parseType("net.sf.cglib.beans.BeanCopier");
        COPY = new Signature("copy", Type.VOID_TYPE, new Type[] { Constants.TYPE_OBJECT, Constants.TYPE_OBJECT, BeanCopier.CONVERTER });
        CONVERT = TypeUtils.parseSignature("Object convert(Object, Class, Object)");
    }
    
    public static class Generator extends AbstractClassGenerator
    {
        private static final Source SOURCE;
        private Class source;
        private Class target;
        private boolean useConverter;
        
        public Generator() {
            super(Generator.SOURCE);
        }
        
        public void setSource(final Class source) {
            if (!Modifier.isPublic(source.getModifiers())) {
                this.setNamePrefix(source.getName());
            }
            this.source = source;
        }
        
        public void setTarget(final Class target) {
            if (!Modifier.isPublic(target.getModifiers())) {
                this.setNamePrefix(target.getName());
            }
            this.target = target;
        }
        
        public void setUseConverter(final boolean useConverter) {
            this.useConverter = useConverter;
        }
        
        protected ClassLoader getDefaultClassLoader() {
            return this.source.getClassLoader();
        }
        
        public BeanCopier create() {
            final Object key = BeanCopier.KEY_FACTORY.newInstance(this.source.getName(), this.target.getName(), this.useConverter);
            return (BeanCopier)super.create(key);
        }
        
        public void generateClass(final ClassVisitor v) {
            final Type sourceType = Type.getType(this.source);
            final Type targetType = Type.getType(this.target);
            final ClassEmitter ce = new ClassEmitter(v);
            ce.begin_class(46, 1, this.getClassName(), BeanCopier.BEAN_COPIER, null, "<generated>");
            EmitUtils.null_constructor(ce);
            final CodeEmitter e = ce.begin_method(1, BeanCopier.COPY, null);
            final PropertyDescriptor[] getters = ReflectUtils.getBeanGetters(this.source);
            final PropertyDescriptor[] setters = ReflectUtils.getBeanGetters(this.target);
            final Map names = new HashMap();
            for (int i = 0; i < getters.length; ++i) {
                names.put(getters[i].getName(), getters[i]);
            }
            final Local targetLocal = e.make_local();
            final Local sourceLocal = e.make_local();
            if (this.useConverter) {
                e.load_arg(1);
                e.checkcast(targetType);
                e.store_local(targetLocal);
                e.load_arg(0);
                e.checkcast(sourceType);
                e.store_local(sourceLocal);
            }
            else {
                e.load_arg(1);
                e.checkcast(targetType);
                e.load_arg(0);
                e.checkcast(sourceType);
            }
            for (int j = 0; j < setters.length; ++j) {
                final PropertyDescriptor setter = setters[j];
                final PropertyDescriptor getter = names.get(setter.getName());
                if (getter != null) {
                    final MethodInfo read = ReflectUtils.getMethodInfo(getter.getReadMethod());
                    final MethodInfo write = ReflectUtils.getMethodInfo(setter.getWriteMethod());
                    if (this.useConverter) {
                        final Type setterType = write.getSignature().getArgumentTypes()[0];
                        e.load_local(targetLocal);
                        e.load_arg(2);
                        e.load_local(sourceLocal);
                        e.invoke(read);
                        e.box(read.getSignature().getReturnType());
                        EmitUtils.load_class(e, setterType);
                        e.push(write.getSignature().getName());
                        e.invoke_interface(BeanCopier.CONVERTER, BeanCopier.CONVERT);
                        e.unbox_or_zero(setterType);
                        e.invoke(write);
                    }
                    else if (compatible(getter, setter)) {
                        e.dup2();
                        e.invoke(read);
                        e.invoke(write);
                    }
                }
            }
            e.return_value();
            e.end_method();
            ce.end_class();
        }
        
        private static boolean compatible(final PropertyDescriptor getter, final PropertyDescriptor setter) {
            return setter.getPropertyType().isAssignableFrom(getter.getPropertyType());
        }
        
        protected Object firstInstance(final Class type) {
            return ReflectUtils.newInstance(type);
        }
        
        protected Object nextInstance(final Object instance) {
            return instance;
        }
        
        static {
            SOURCE = new Source(BeanCopier.class.getName());
        }
    }
    
    interface BeanCopierKey
    {
        Object newInstance(final String p0, final String p1, final boolean p2);
    }
}
