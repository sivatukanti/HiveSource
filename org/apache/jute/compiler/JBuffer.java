// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute.compiler;

public class JBuffer extends JCompType
{
    public JBuffer() {
        super("struct buffer", " ::std::string", "byte[]", "byte[]", "Buffer", "byte[]", "byte[]");
    }
    
    public String genCppGetSet(final String fname, final int fIdx) {
        String cgetFunc = "  virtual const " + this.getCppType() + "& get" + fname + "() const {\n";
        cgetFunc = cgetFunc + "    return m" + fname + ";\n";
        cgetFunc += "  }\n";
        String getFunc = "  virtual " + this.getCppType() + "& get" + fname + "() {\n";
        getFunc = getFunc + "    bs_.set(" + fIdx + ");return m" + fname + ";\n";
        getFunc += "  }\n";
        return cgetFunc + getFunc;
    }
    
    public String getSignature() {
        return "B";
    }
    
    public String genJavaReadWrapper(final String fname, final String tag, final boolean decl) {
        String ret = "";
        if (decl) {
            ret = "    byte[] " + fname + ";\n";
        }
        return ret + "        " + fname + "=a_.readBuffer(\"" + tag + "\");\n";
    }
    
    public String genJavaWriteWrapper(final String fname, final String tag) {
        return "        a_.writeBuffer(" + fname + ",\"" + tag + "\");\n";
    }
    
    public String genJavaCompareTo(final String fname, final String other) {
        final StringBuilder sb = new StringBuilder();
        sb.append("    {\n");
        sb.append("      byte[] my = " + fname + ";\n");
        sb.append("      byte[] ur = " + other + ";\n");
        sb.append("      ret = org.apache.jute.Utils.compareBytes(my,0,my.length,ur,0,ur.length);\n");
        sb.append("    }\n");
        return sb.toString();
    }
    
    public String genJavaCompareTo(final String fname) {
        return this.genJavaCompareTo(fname, "peer." + fname);
    }
    
    public String genJavaCompareToWrapper(final String fname, final String other) {
        return "    " + this.genJavaCompareTo(fname, other);
    }
    
    public String genJavaEquals(final String fname, final String peer) {
        return "    ret = org.apache.jute.Utils.bufEquals(" + fname + "," + peer + ");\n";
    }
    
    public String genJavaHashCode(final String fname) {
        return "    ret = java.util.Arrays.toString(" + fname + ").hashCode();\n";
    }
    
    public String genJavaSlurpBytes(final String b, final String s, final String l) {
        final StringBuilder sb = new StringBuilder();
        sb.append("        {\n");
        sb.append("           int i = org.apache.jute.Utils.readVInt(" + b + ", " + s + ");\n");
        sb.append("           int z = WritableUtils.getVIntSize(i);\n");
        sb.append("           " + s + " += z+i; " + l + " -= (z+i);\n");
        sb.append("        }\n");
        return sb.toString();
    }
    
    public String genJavaCompareBytes() {
        final StringBuilder sb = new StringBuilder();
        sb.append("        {\n");
        sb.append("           int i1 = org.apache.jute.Utils.readVInt(b1, s1);\n");
        sb.append("           int i2 = org.apache.jute.Utils.readVInt(b2, s2);\n");
        sb.append("           int z1 = WritableUtils.getVIntSize(i1);\n");
        sb.append("           int z2 = WritableUtils.getVIntSize(i2);\n");
        sb.append("           s1+=z1; s2+=z2; l1-=z1; l2-=z2;\n");
        sb.append("           int r1 = org.apache.jute.Utils.compareBytes(b1,s1,l1,b2,s2,l2);\n");
        sb.append("           if (r1 != 0) { return (r1<0)?-1:0; }\n");
        sb.append("           s1+=i1; s2+=i2; l1-=i1; l1-=i2;\n");
        sb.append("        }\n");
        return sb.toString();
    }
}
