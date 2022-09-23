// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.jdo;

import org.datanucleus.enhancer.ClassEnhancer;
import java.lang.reflect.Method;
import javax.jdo.JDOException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.metadata.InterfaceMetaData;
import javax.jdo.JDOUserException;
import javax.jdo.JDOFatalException;
import org.datanucleus.metadata.ClassMetaData;
import java.lang.reflect.Modifier;
import javax.jdo.spi.PersistenceCapable;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.enhancer.EnhancerClassLoader;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.util.Localiser;
import org.datanucleus.ImplementationCreator;
import java.io.Serializable;

public class JDOImplementationCreator implements Serializable, ImplementationCreator
{
    protected static final Localiser LOCALISER;
    protected final MetaDataManager metaDataMgr;
    protected final EnhancerClassLoader loader;
    
    public JDOImplementationCreator(final MetaDataManager mmgr) {
        this.metaDataMgr = mmgr;
        this.loader = new EnhancerClassLoader();
    }
    
    @Override
    public ClassLoader getClassLoader() {
        return this.loader;
    }
    
    @Override
    public Object newInstance(final Class cls, final ClassLoaderResolver clr) {
        try {
            if (PersistenceCapable.class.isAssignableFrom(cls)) {
                if (!Modifier.isAbstract(cls.getModifiers())) {
                    return cls.newInstance();
                }
                final ClassMetaData cmd = (ClassMetaData)this.metaDataMgr.getMetaDataForClass(cls, clr);
                if (cmd == null) {
                    throw new JDOFatalException("Could not find metadata for class " + cls.getName());
                }
                final Object obj = this.newInstance(cmd, clr);
                if (obj == null) {
                    throw new JDOFatalException(JDOImplementationCreator.LOCALISER.msg("ImplementationCreator.InstanceCreateFailed", cls.getName()));
                }
                if (!this.metaDataMgr.hasMetaDataForClass(obj.getClass().getName())) {
                    this.metaDataMgr.registerImplementationOfAbstractClass(cmd, obj.getClass(), clr);
                }
                return obj;
            }
            else {
                final InterfaceMetaData imd = this.metaDataMgr.getMetaDataForInterface(cls, clr);
                if (imd == null) {
                    throw new JDOFatalException("Could not find metadata for class/interface " + cls.getName());
                }
                final Object obj = this.newInstance(imd, clr);
                if (obj == null) {
                    throw new JDOFatalException(JDOImplementationCreator.LOCALISER.msg("ImplementationCreator.InstanceCreateFailed", cls.getName()));
                }
                if (!this.metaDataMgr.hasMetaDataForClass(obj.getClass().getName())) {
                    this.metaDataMgr.registerPersistentInterface(imd, obj.getClass(), clr);
                }
                return obj;
            }
        }
        catch (ClassNotFoundException e) {
            throw new JDOUserException(e.toString(), e);
        }
        catch (InstantiationException e2) {
            throw new JDOUserException(e2.toString(), e2);
        }
        catch (IllegalAccessException e3) {
            throw new JDOUserException(e3.toString(), e3);
        }
    }
    
    protected PersistenceCapable newInstance(final InterfaceMetaData imd, final ClassLoaderResolver clr) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        final Class cls = clr.classForName(imd.getFullClassName());
        final Method[] methods = cls.getDeclaredMethods();
        for (int i = 0; i < methods.length; ++i) {
            final String methodName = methods[i].getName();
            if (!methodName.startsWith("jdo")) {
                String propertyName = methodName;
                if (methodName.startsWith("set")) {
                    propertyName = ClassUtils.getFieldNameForJavaBeanSetter(methodName);
                }
                else if (methodName.startsWith("get")) {
                    propertyName = ClassUtils.getFieldNameForJavaBeanGetter(methodName);
                }
                if (imd.getMetaDataForMember(propertyName) == null) {
                    throw new NucleusUserException(JDOImplementationCreator.LOCALISER.msg("ImplementationCreator.InterfaceMethodUndefined", imd.getFullClassName(), methodName));
                }
            }
        }
        final String implClassName = imd.getName() + "Impl";
        final String implFullClassName = imd.getPackageName() + '.' + implClassName;
        try {
            this.loader.loadClass(implFullClassName);
        }
        catch (ClassNotFoundException e) {
            final JDOImplementationGenerator gen = this.getGenerator(imd, implClassName);
            gen.enhance(clr);
            this.loader.defineClass(implFullClassName, gen.getBytes(), clr);
        }
        final Object instance = this.loader.loadClass(implFullClassName).newInstance();
        if (instance instanceof PersistenceCapable) {
            return (PersistenceCapable)instance;
        }
        final Class[] interfaces = instance.getClass().getInterfaces();
        final StringBuilder implementedInterfacesMsg = new StringBuilder("[");
        String classLoaderPCMsg = "";
        for (int j = 0; j < interfaces.length; ++j) {
            implementedInterfacesMsg.append(interfaces[j].getName());
            if (j < interfaces.length - 1) {
                implementedInterfacesMsg.append(",");
            }
            if (interfaces[j].getName().equals(PersistenceCapable.class.getName())) {
                classLoaderPCMsg = JDOImplementationCreator.LOCALISER.msg("ImplementationCreator.DifferentClassLoader", interfaces[j].getClassLoader(), PersistenceCapable.class.getClassLoader());
            }
        }
        implementedInterfacesMsg.append("]");
        throw new JDOException(JDOImplementationCreator.LOCALISER.msg("ImplementationCreator.NotPCProblem", implFullClassName, classLoaderPCMsg, implementedInterfacesMsg.toString()));
    }
    
    protected PersistenceCapable newInstance(final ClassMetaData cmd, final ClassLoaderResolver clr) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        final Class cls = clr.classForName(cmd.getFullClassName());
        final Method[] methods = cls.getDeclaredMethods();
        for (int i = 0; i < methods.length; ++i) {
            final String methodName = methods[i].getName();
            if (Modifier.isAbstract(methods[i].getModifiers()) && !methodName.startsWith("jdo")) {
                String propertyName = methodName;
                if (methodName.startsWith("set")) {
                    propertyName = ClassUtils.getFieldNameForJavaBeanSetter(methodName);
                }
                else if (methodName.startsWith("get")) {
                    propertyName = ClassUtils.getFieldNameForJavaBeanGetter(methodName);
                }
                if (cmd.getMetaDataForMember(propertyName) == null) {
                    throw new NucleusUserException(JDOImplementationCreator.LOCALISER.msg("ImplementationCreator.AbstractClassMethodUndefined", cmd.getFullClassName(), methodName));
                }
            }
        }
        final String implClassName = cmd.getName() + "Impl";
        final String implFullClassName = cmd.getPackageName() + '.' + implClassName;
        try {
            this.loader.loadClass(implFullClassName);
        }
        catch (ClassNotFoundException e) {
            final JDOImplementationGenerator gen = this.getGenerator(cmd, implClassName);
            gen.enhance(clr);
            this.loader.defineClass(implFullClassName, gen.getBytes(), clr);
        }
        final Object instance = this.loader.loadClass(implFullClassName).newInstance();
        if (instance instanceof PersistenceCapable) {
            return (PersistenceCapable)instance;
        }
        final Class[] interfaces = instance.getClass().getInterfaces();
        final StringBuilder implementedInterfacesMsg = new StringBuilder("[");
        String classLoaderPCMsg = "";
        for (int j = 0; j < interfaces.length; ++j) {
            implementedInterfacesMsg.append(interfaces[j].getName());
            if (j < interfaces.length - 1) {
                implementedInterfacesMsg.append(",");
            }
            if (interfaces[j].getName().equals(PersistenceCapable.class.getName())) {
                classLoaderPCMsg = JDOImplementationCreator.LOCALISER.msg("ImplementationCreator.DifferentClassLoader", interfaces[j].getClassLoader(), PersistenceCapable.class.getClassLoader());
            }
        }
        implementedInterfacesMsg.append("]");
        throw new JDOException(JDOImplementationCreator.LOCALISER.msg("ImplementationCreator.NotPCProblem", implFullClassName, classLoaderPCMsg, implementedInterfacesMsg.toString()));
    }
    
    protected JDOImplementationGenerator getGenerator(final AbstractClassMetaData acmd, final String implClassName) {
        if (acmd instanceof InterfaceMetaData) {
            return new JDOImplementationGenerator((InterfaceMetaData)acmd, implClassName, this.metaDataMgr);
        }
        if (acmd instanceof ClassMetaData) {
            return new JDOImplementationGenerator((ClassMetaData)acmd, implClassName, this.metaDataMgr);
        }
        return null;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassEnhancer.class.getClassLoader());
    }
}
