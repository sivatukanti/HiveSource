// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute.compiler;

public class JBoolean extends JType
{
    public JBoolean() {
        super("int32_t", "bool", "bool", "boolean", "Bool", "Boolean", "bool", "toBoolean");
    }
    
    public String getSignature() {
        return "z";
    }
    
    public String genJavaCompareTo(final String fname) {
        return "    ret = (" + fname + " == peer." + fname + ")? 0 : (" + fname + "?1:-1);\n";
    }
    
    public String genJavaHashCode(final String fname) {
        return "     ret = (" + fname + ")?0:1;\n";
    }
    
    @Override
    String genCsharpHashCode(final String fname) {
        return "     ret = (" + JType.capitalize(fname) + ")?0:1;\n";
    }
    
    @Override
    String genCsharpCompareTo(final String name) {
        return "    ret = (" + JType.capitalize(name) + " == peer." + JType.capitalize(name) + ")? 0 : (" + JType.capitalize(name) + "?1:-1);\n";
    }
}
