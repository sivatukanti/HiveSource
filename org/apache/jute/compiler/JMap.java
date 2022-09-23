// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute.compiler;

public class JMap extends JCompType
{
    private static int level;
    private JType mKey;
    private JType mValue;
    
    private static String getLevel() {
        return Integer.toString(JMap.level);
    }
    
    private static void incrLevel() {
        ++JMap.level;
    }
    
    private static void decrLevel() {
        --JMap.level;
    }
    
    private static String getId(final String id) {
        return id + getLevel();
    }
    
    public JMap(final JType t1, final JType t2) {
        super("#error", " ::std::map<" + t1.getCppType() + "," + t2.getCppType() + ">", "System.Collections.Generic.SortedDictionary<string, string>", "java.util.TreeMap", "Map", "System.Collections.Generic.SortedDictionary<string, string>", "java.util.TreeMap");
        this.mKey = t1;
        this.mValue = t2;
    }
    
    public String getSignature() {
        return "{" + this.mKey.getSignature() + this.mValue.getSignature() + "}";
    }
    
    public String genJavaCompareTo(final String fname) {
        return "    throw new UnsupportedOperationException(\"comparing " + fname + " is unimplemented\");\n";
    }
    
    public String genJavaReadWrapper(final String fname, final String tag, final boolean decl) {
        final StringBuilder ret = new StringBuilder("");
        if (decl) {
            ret.append("    java.util.TreeMap " + fname + ";\n");
        }
        ret.append("    {\n");
        incrLevel();
        ret.append("      org.apache.jute.Index " + getId("midx") + " = a_.startMap(\"" + tag + "\");\n");
        ret.append("      " + fname + "=new java.util.TreeMap();\n");
        ret.append("      for (; !" + getId("midx") + ".done(); " + getId("midx") + ".incr()) {\n");
        ret.append(this.mKey.genJavaReadWrapper(getId("k"), getId("k"), true));
        ret.append(this.mValue.genJavaReadWrapper(getId("v"), getId("v"), true));
        ret.append("        " + fname + ".put(" + getId("k") + "," + getId("v") + ");\n");
        ret.append("      }\n");
        ret.append("    a_.endMap(\"" + tag + "\");\n");
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
        ret.append("      a_.startMap(" + fname + ",\"" + tag + "\");\n");
        ret.append("      java.util.Set " + getId("es") + " = " + fname + ".entrySet();\n");
        ret.append("      for(java.util.Iterator " + getId("midx") + " = " + getId("es") + ".iterator(); " + getId("midx") + ".hasNext(); ) {\n");
        ret.append("        java.util.Map.Entry " + getId("me") + " = (java.util.Map.Entry) " + getId("midx") + ".next();\n");
        ret.append("        " + this.mKey.getJavaWrapperType() + " " + getId("k") + " = (" + this.mKey.getJavaWrapperType() + ") " + getId("me") + ".getKey();\n");
        ret.append("        " + this.mValue.getJavaWrapperType() + " " + getId("v") + " = (" + this.mValue.getJavaWrapperType() + ") " + getId("me") + ".getValue();\n");
        ret.append(this.mKey.genJavaWriteWrapper(getId("k"), getId("k")));
        ret.append(this.mValue.genJavaWriteWrapper(getId("v"), getId("v")));
        ret.append("      }\n");
        ret.append("      a_.endMap(" + fname + ",\"" + tag + "\");\n");
        ret.append("    }\n");
        decrLevel();
        return ret.toString();
    }
    
    public String genJavaWriteMethod(final String fname, final String tag) {
        return this.genJavaWriteWrapper(fname, tag);
    }
    
    public String genCsharpWriteWrapper(final String fname, final int tag) {
        final StringBuilder ret = new StringBuilder("    {\n");
        incrLevel();
        ret.append("      a_.StartMap(" + fname + ",\"" + tag + "\");\n");
        ret.append("      java.util.Set " + getId("es") + " = " + fname + ".entrySet();\n");
        ret.append("      for(java.util.Iterator " + getId("midx") + " = " + getId("es") + ".iterator(); " + getId("midx") + ".hasNext(); ) {\n");
        ret.append("        java.util.Map.Entry " + getId("me") + " = (java.util.Map.Entry) " + getId("midx") + ".next();\n");
        ret.append("        " + this.mKey.getCsharpWrapperType() + " " + getId("k") + " = (" + this.mKey.getCsharpWrapperType() + ") " + getId("me") + ".getKey();\n");
        ret.append("        " + this.mValue.getCsharpWrapperType() + " " + getId("v") + " = (" + this.mValue.getCsharpWrapperType() + ") " + getId("me") + ".getValue();\n");
        ret.append(this.mKey.genCsharpWriteWrapper(getId("k"), getId("k")));
        ret.append(this.mValue.genCsharpWriteWrapper(getId("v"), getId("v")));
        ret.append("      }\n");
        ret.append("      a_.EndMap(" + fname + ",\"" + tag + "\");\n");
        ret.append("    }\n");
        decrLevel();
        return ret.toString();
    }
    
    String genCsharpWriteMethod(final String fname, final int tag) {
        return this.genCsharpWriteWrapper(fname, tag);
    }
    
    public String genCsharpReadWrapper(final String fname, final int tag, final boolean decl) {
        final StringBuilder ret = new StringBuilder("");
        if (decl) {
            ret.append("    System.Collections.SortedDictionary<string,string> " + JType.capitalize(fname) + ";\n");
        }
        ret.append("    {\n");
        incrLevel();
        ret.append("      Org.Apache.Jute.IIndex " + getId("midx") + " = a_.StartMap(\"" + tag + "\");\n");
        ret.append("      " + fname + "= new System.Collections.SortedDictionary<string,string>();\n");
        ret.append("      for (; !" + getId("midx") + ".done(); " + getId("midx") + ".incr()) {\n");
        ret.append(this.mKey.genCsharpReadWrapper(getId("k"), getId("k"), true));
        ret.append(this.mValue.genCsharpReadWrapper(getId("v"), getId("v"), true));
        ret.append("        " + fname + ".Add(" + getId("k") + "," + getId("v") + ");\n");
        ret.append("      }\n");
        ret.append("    a_.EndMap(\"" + tag + "\");\n");
        decrLevel();
        ret.append("    }\n");
        return ret.toString();
    }
    
    String genCsharpReadMethod(final String fname, final int tag) {
        return this.genCsharpReadWrapper(fname, tag, false);
    }
    
    static {
        JMap.level = 0;
    }
}
