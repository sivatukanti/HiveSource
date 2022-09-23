// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.jsp.tagext;

public class VariableInfo
{
    public static final int NESTED = 0;
    public static final int AT_BEGIN = 1;
    public static final int AT_END = 2;
    private String varName;
    private String className;
    private boolean declare;
    private int scope;
    
    public VariableInfo(final String varName, final String className, final boolean declare, final int scope) {
        this.varName = varName;
        this.className = className;
        this.declare = declare;
        this.scope = scope;
    }
    
    public String getVarName() {
        return this.varName;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public boolean getDeclare() {
        return this.declare;
    }
    
    public int getScope() {
        return this.scope;
    }
}
