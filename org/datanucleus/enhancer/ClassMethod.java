// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer;

import java.util.Arrays;
import org.datanucleus.asm.Type;
import org.datanucleus.asm.ClassVisitor;
import org.datanucleus.asm.MethodVisitor;
import org.datanucleus.util.Localiser;

public abstract class ClassMethod
{
    protected static final Localiser LOCALISER;
    protected ClassEnhancer enhancer;
    protected String methodName;
    protected int access;
    protected Object returnType;
    protected Object[] argTypes;
    protected String[] argNames;
    protected String[] exceptions;
    protected MethodVisitor visitor;
    
    public ClassMethod(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames) {
        this(enhancer, name, access, returnType, argTypes, argNames, null);
    }
    
    public ClassMethod(final ClassEnhancer enhancer, final String name, final int access, final Object returnType, final Object[] argTypes, final String[] argNames, final String[] exceptions) {
        this.enhancer = enhancer;
        this.methodName = name;
        this.access = access;
        this.returnType = returnType;
        this.argTypes = argTypes;
        this.argNames = argNames;
        this.exceptions = exceptions;
    }
    
    public void initialise() {
    }
    
    public void initialise(final ClassVisitor classVisitor) {
        Type type = null;
        Type[] argtypes = null;
        if (this.returnType != null) {
            type = Type.getType((Class<?>)this.returnType);
        }
        else {
            type = Type.VOID_TYPE;
        }
        if (this.argTypes != null) {
            argtypes = new Type[this.argTypes.length];
            for (int i = 0; i < this.argTypes.length; ++i) {
                argtypes[i] = Type.getType((Class<?>)this.argTypes[i]);
            }
        }
        else {
            argtypes = new Type[0];
        }
        final String methodDesc = Type.getMethodDescriptor(type, argtypes);
        this.visitor = classVisitor.visitMethod(this.access, this.methodName, methodDesc, null, this.exceptions);
    }
    
    protected ClassEnhancer getClassEnhancer() {
        return this.enhancer;
    }
    
    public String getDescriptor() {
        final StringBuffer str = new StringBuffer("(");
        if (this.argTypes != null && this.argTypes.length > 0) {
            for (int i = 0; i < this.argTypes.length; ++i) {
                str.append(Type.getDescriptor((Class<?>)this.argTypes[i]));
            }
        }
        str.append(")");
        if (this.returnType != null) {
            str.append(Type.getDescriptor((Class<?>)this.returnType));
        }
        else {
            str.append("V");
        }
        return str.toString();
    }
    
    public EnhancementNamer getNamer() {
        return this.enhancer.getNamer();
    }
    
    public String getName() {
        return this.methodName;
    }
    
    public int getAccess() {
        return this.access;
    }
    
    public abstract void execute();
    
    public void close() {
        if (DataNucleusEnhancer.LOGGER.isDebugEnabled()) {
            final String msg = getMethodAdditionMessage(this.methodName, this.returnType, this.argTypes, this.argNames);
            DataNucleusEnhancer.LOGGER.debug(ClassMethod.LOCALISER.msg("Enhancer.AddMethod", msg));
        }
    }
    
    @Override
    public int hashCode() {
        return this.methodName.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof ClassMethod) {
            final ClassMethod cb = (ClassMethod)o;
            if (cb.methodName.equals(this.methodName)) {
                return Arrays.equals(cb.argTypes, this.argTypes);
            }
        }
        return false;
    }
    
    public static String getMethodAdditionMessage(final String methodName, final Object returnType, final Object[] argTypes, final String[] argNames) {
        final StringBuffer sb = new StringBuffer();
        if (returnType != null) {
            if (returnType instanceof Class) {
                sb.append(((Class)returnType).getName()).append(" ");
            }
            else {
                sb.append(returnType).append(" ");
            }
        }
        else {
            sb.append("void ");
        }
        sb.append(methodName).append("(");
        if (argTypes != null) {
            for (int i = 0; i < argTypes.length; ++i) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(argTypes[i]).append(" ").append(argNames[i]);
            }
        }
        sb.append(")");
        return sb.toString();
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassEnhancer.class.getClassLoader());
    }
}
