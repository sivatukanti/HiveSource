// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.jsp.tagext;

public class FunctionInfo
{
    private String name;
    private String functionClass;
    private String functionSignature;
    
    public FunctionInfo(final String name, final String klass, final String signature) {
        this.name = name;
        this.functionClass = klass;
        this.functionSignature = signature;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getFunctionClass() {
        return this.functionClass;
    }
    
    public String getFunctionSignature() {
        return this.functionSignature;
    }
}
