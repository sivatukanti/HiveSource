// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute.compiler;

public class JByte extends JType
{
    public JByte() {
        super("char", "int8_t", "byte", "byte", "Byte", "Byte", "byte", "toByte");
    }
    
    public String getSignature() {
        return "b";
    }
}
