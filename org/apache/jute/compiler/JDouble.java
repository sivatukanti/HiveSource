// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute.compiler;

public class JDouble extends JType
{
    public JDouble() {
        super("double", "double", "double", "double", "Double", "Double", "double", "toDouble");
    }
    
    public String getSignature() {
        return "d";
    }
    
    public String genJavaHashCode(final String fname) {
        final String tmp = "Double.doubleToLongBits(" + fname + ")";
        return "    ret = (int)(" + tmp + "^(" + tmp + ">>>32));\n";
    }
}
