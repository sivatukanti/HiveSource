// 
// Decompiled by Procyon v0.5.36
// 

package javax.el;

public class MethodInfo
{
    private String name;
    private Class<?> returnType;
    private Class<?>[] paramTypes;
    
    public MethodInfo(final String name, final Class<?> returnType, final Class<?>[] paramTypes) {
        this.name = name;
        this.returnType = returnType;
        this.paramTypes = paramTypes;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Class<?> getReturnType() {
        return this.returnType;
    }
    
    public Class<?>[] getParamTypes() {
        return this.paramTypes;
    }
}
