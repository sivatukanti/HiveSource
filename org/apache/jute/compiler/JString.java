// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute.compiler;

public class JString extends JCompType
{
    public JString() {
        super("char *", " ::std::string", "string", "String", "String", "String", "string");
    }
    
    public String getSignature() {
        return "s";
    }
    
    public String genJavaReadWrapper(final String fname, final String tag, final boolean decl) {
        String ret = "";
        if (decl) {
            ret = "    String " + fname + ";\n";
        }
        return ret + "        " + fname + "=a_.readString(\"" + tag + "\");\n";
    }
    
    public String genJavaWriteWrapper(final String fname, final String tag) {
        return "        a_.writeString(" + fname + ",\"" + tag + "\");\n";
    }
}
