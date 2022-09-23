// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute.compiler;

public class JLong extends JType
{
    public JLong() {
        super("int64_t", "int64_t", "long", "long", "Long", "Long", "long", "toLong");
    }
    
    public String getSignature() {
        return "l";
    }
    
    public String genJavaHashCode(final String fname) {
        return "    ret = (int) (" + fname + "^(" + fname + ">>>32));\n";
    }
}
