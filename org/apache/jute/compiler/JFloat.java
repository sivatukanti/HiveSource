// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute.compiler;

public class JFloat extends JType
{
    public JFloat() {
        super("float", "float", "float", "float", "Float", "Float", "float", "toFloat");
    }
    
    public String getSignature() {
        return "f";
    }
    
    public String genJavaHashCode(final String fname) {
        return "    ret = Float.floatToIntBits(" + fname + ");\n";
    }
}
