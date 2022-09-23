// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.jsp.tagext;

public class TagVariableInfo
{
    private String nameGiven;
    private String nameFromAttribute;
    private String className;
    private boolean declare;
    private int scope;
    
    public TagVariableInfo(final String nameGiven, final String nameFromAttribute, final String className, final boolean declare, final int scope) {
        this.nameGiven = nameGiven;
        this.nameFromAttribute = nameFromAttribute;
        this.className = className;
        this.declare = declare;
        this.scope = scope;
    }
    
    public String getNameGiven() {
        return this.nameGiven;
    }
    
    public String getNameFromAttribute() {
        return this.nameFromAttribute;
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
