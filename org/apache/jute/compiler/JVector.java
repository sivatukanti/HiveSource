// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute.compiler;

public class JVector extends JCompType
{
    private static int level;
    private JType mElement;
    
    private static String getId(final String id) {
        return id + getLevel();
    }
    
    private static String getLevel() {
        return Integer.toString(JVector.level);
    }
    
    private static void incrLevel() {
        ++JVector.level;
    }
    
    private static void decrLevel() {
        --JVector.level;
    }
    
    public JVector(final JType t) {
        super("struct " + extractVectorName(t), " ::std::vector<" + t.getCppType() + ">", "System.Collections.Generic.List<" + t.getCsharpType() + ">", "java.util.List<" + t.getJavaType() + ">", "Vector", "System.Collections.Generic.List<" + t.getCsharpType() + ">", "java.util.ArrayList<" + t.getJavaType() + ">");
        this.mElement = t;
    }
    
    public String getSignature() {
        return "[" + this.mElement.getSignature() + "]";
    }
    
    public String genJavaCompareTo(final String fname) {
        return "    throw new UnsupportedOperationException(\"comparing " + fname + " is unimplemented\");\n";
    }
    
    public String genJavaReadWrapper(final String fname, final String tag, final boolean decl) {
        final StringBuilder ret = new StringBuilder("");
        if (decl) {
            ret.append("      java.util.List " + fname + ";\n");
        }
        ret.append("    {\n");
        incrLevel();
        ret.append("      Index " + getId("vidx") + " = a_.startVector(\"" + tag + "\");\n");
        ret.append("      if (" + getId("vidx") + "!= null) {");
        ret.append("          " + fname + "=new java.util.ArrayList<" + this.mElement.getJavaType() + ">();\n");
        ret.append("          for (; !" + getId("vidx") + ".done(); " + getId("vidx") + ".incr()) {\n");
        ret.append(this.mElement.genJavaReadWrapper(getId("e"), getId("e"), true));
        ret.append("            " + fname + ".add(" + getId("e") + ");\n");
        ret.append("          }\n");
        ret.append("      }\n");
        ret.append("    a_.endVector(\"" + tag + "\");\n");
        decrLevel();
        ret.append("    }\n");
        return ret.toString();
    }
    
    public String genJavaReadMethod(final String fname, final String tag) {
        return this.genJavaReadWrapper(fname, tag, false);
    }
    
    public String genJavaWriteWrapper(final String fname, final String tag) {
        final StringBuilder ret = new StringBuilder("    {\n");
        incrLevel();
        ret.append("      a_.startVector(" + fname + ",\"" + tag + "\");\n");
        ret.append("      if (" + fname + "!= null) {");
        ret.append("          int " + getId("len") + " = " + fname + ".size();\n");
        ret.append("          for(int " + getId("vidx") + " = 0; " + getId("vidx") + "<" + getId("len") + "; " + getId("vidx") + "++) {\n");
        ret.append("            " + this.mElement.getJavaWrapperType() + " " + getId("e") + " = (" + this.mElement.getJavaWrapperType() + ") " + fname + ".get(" + getId("vidx") + ");\n");
        ret.append(this.mElement.genJavaWriteWrapper(getId("e"), getId("e")));
        ret.append("          }\n");
        ret.append("      }\n");
        ret.append("      a_.endVector(" + fname + ",\"" + tag + "\");\n");
        ret.append("    }\n");
        decrLevel();
        return ret.toString();
    }
    
    public String genJavaWriteMethod(final String fname, final String tag) {
        return this.genJavaWriteWrapper(fname, tag);
    }
    
    public JType getElementType() {
        return this.mElement;
    }
    
    public String genCsharpWriteWrapper(final String fname, final String tag) {
        final StringBuilder ret = new StringBuilder("    {\n");
        incrLevel();
        ret.append("      a_.StartVector(" + JType.capitalize(fname) + ",\"" + tag + "\");\n");
        ret.append("      if (" + JType.capitalize(fname) + "!= null) {");
        ret.append("          int " + getId("len") + " = " + JType.capitalize(fname) + ".Count;\n");
        ret.append("          for(int " + getId("vidx") + " = 0; " + getId("vidx") + "<" + getId("len") + "; " + getId("vidx") + "++) {\n");
        ret.append("            " + this.mElement.getCsharpWrapperType() + " " + getId("e") + " = (" + this.mElement.getCsharpWrapperType() + ") " + JType.capitalize(fname) + "[" + getId("vidx") + "];\n");
        ret.append(this.mElement.genCsharpWriteWrapper(getId("e"), getId("e")));
        ret.append("          }\n");
        ret.append("      }\n");
        ret.append("      a_.EndVector(" + JType.capitalize(fname) + ",\"" + tag + "\");\n");
        ret.append("    }\n");
        decrLevel();
        return ret.toString();
    }
    
    @Override
    String genCsharpWriteMethod(final String fname, final String tag) {
        return this.genCsharpWriteWrapper(fname, tag);
    }
    
    public String genCsharpReadWrapper(final String fname, final String tag, final boolean decl) {
        final StringBuilder ret = new StringBuilder("");
        if (decl) {
            ret.append("      System.Collections.Generic.List<" + this.mElement.getCsharpType() + "> " + JType.capitalize(fname) + ";\n");
        }
        ret.append("    {\n");
        incrLevel();
        ret.append("      IIndex " + getId("vidx") + " = a_.StartVector(\"" + tag + "\");\n");
        ret.append("      if (" + getId("vidx") + "!= null) {");
        ret.append("          " + JType.capitalize(fname) + "=new System.Collections.Generic.List<" + this.mElement.getCsharpType() + ">();\n");
        ret.append("          for (; !" + getId("vidx") + ".Done(); " + getId("vidx") + ".Incr()) {\n");
        ret.append(this.mElement.genCsharpReadWrapper(getId("e"), getId("e"), true));
        ret.append("            " + JType.capitalize(fname) + ".Add(" + getId("e") + ");\n");
        ret.append("          }\n");
        ret.append("      }\n");
        ret.append("    a_.EndVector(\"" + tag + "\");\n");
        decrLevel();
        ret.append("    }\n");
        return ret.toString();
    }
    
    @Override
    String genCsharpReadMethod(final String fname, final String tag) {
        return this.genCsharpReadWrapper(fname, tag, false);
    }
    
    public static String extractVectorName(final JType jvType) {
        return JRecord.extractMethodSuffix(jvType) + "_vector";
    }
    
    static {
        JVector.level = 0;
    }
}
