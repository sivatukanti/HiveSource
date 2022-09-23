// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute.compiler;

public abstract class JType
{
    private String mCName;
    private String mCppName;
    private String mCsharpName;
    private String mJavaName;
    protected String mMethodSuffix;
    private String mWrapper;
    private String mSharpWrapper;
    private String mUnwrapMethod;
    
    JType(final String cname, final String cppname, final String csharpName, final String javaname, final String suffix, final String wrapper, final String csharpWrapper, final String unwrap) {
        this.mCName = cname;
        this.mCppName = cppname;
        this.mCsharpName = ("Id".equals(csharpName) ? "ZKId" : csharpName);
        this.mJavaName = javaname;
        this.mMethodSuffix = suffix;
        this.mWrapper = wrapper;
        this.mSharpWrapper = csharpWrapper;
        this.mUnwrapMethod = unwrap;
    }
    
    abstract String getSignature();
    
    String genCppDecl(final String fname) {
        return "  " + this.mCppName + " m" + fname + ";\n";
    }
    
    String genCDecl(final String name) {
        return "    " + this.mCName + " " + name + ";\n";
    }
    
    public String genCsharpDecl(final String name) {
        return "  private " + this.mCsharpName + " " + name + ";\n";
    }
    
    String genJavaDecl(final String fname) {
        return "  private " + this.mJavaName + " " + fname + ";\n";
    }
    
    String genJavaConstructorParam(final String fname) {
        return "        " + this.mJavaName + " " + fname;
    }
    
    String genCppGetSet(final String fname, final int fIdx) {
        String getFunc = "  virtual " + this.mCppName + " get" + fname + "() const {\n";
        getFunc = getFunc + "    return m" + fname + ";\n";
        getFunc += "  }\n";
        String setFunc = "  virtual void set" + fname + "(" + this.mCppName + " m_) {\n";
        setFunc = setFunc + "    m" + fname + "=m_; bs_.set(" + fIdx + ");\n";
        setFunc += "  }\n";
        return getFunc + setFunc;
    }
    
    String genCsharpGetSet(final String fname, final int fIdx) {
        final String getFunc = "  public " + this.getCsharpType() + " " + capitalize(fname) + " { get; set; } ";
        return getFunc;
    }
    
    static String capitalize(final String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    
    String genJavaGetSet(final String fname, final int fIdx) {
        String getFunc = "  public " + this.mJavaName + " get" + capitalize(fname) + "() {\n";
        getFunc = getFunc + "    return " + fname + ";\n";
        getFunc += "  }\n";
        String setFunc = "  public void set" + capitalize(fname) + "(" + this.mJavaName + " m_) {\n";
        setFunc = setFunc + "    " + fname + "=m_;\n";
        setFunc += "  }\n";
        return getFunc + setFunc;
    }
    
    String getCType() {
        return this.mCName;
    }
    
    String getCppType() {
        return this.mCppName;
    }
    
    String getCsharpType() {
        return this.mCsharpName;
    }
    
    String getJavaType() {
        return this.mJavaName;
    }
    
    String getJavaWrapperType() {
        return this.mWrapper;
    }
    
    String getCsharpWrapperType() {
        return this.mSharpWrapper;
    }
    
    String getMethodSuffix() {
        return this.mMethodSuffix;
    }
    
    String genJavaWriteMethod(final String fname, final String tag) {
        return "    a_.write" + this.mMethodSuffix + "(" + fname + ",\"" + tag + "\");\n";
    }
    
    String genJavaReadMethod(final String fname, final String tag) {
        return "    " + fname + "=a_.read" + this.mMethodSuffix + "(\"" + tag + "\");\n";
    }
    
    String genJavaReadWrapper(final String fname, final String tag, final boolean decl) {
        String ret = "";
        if (decl) {
            ret = "    " + this.mWrapper + " " + fname + ";\n";
        }
        return ret + "    " + fname + "=new " + this.mWrapper + "(a_.read" + this.mMethodSuffix + "(\"" + tag + "\"));\n";
    }
    
    String genJavaWriteWrapper(final String fname, final String tag) {
        return "        a_.write" + this.mMethodSuffix + "(" + fname + "." + this.mUnwrapMethod + "(),\"" + tag + "\");\n";
    }
    
    String genJavaCompareTo(final String fname) {
        return "    ret = (" + fname + " == peer." + fname + ")? 0 :((" + fname + "<peer." + fname + ")?-1:1);\n";
    }
    
    String genJavaEquals(final String fname, final String peer) {
        return "    ret = (" + fname + "==" + peer + ");\n";
    }
    
    String genJavaHashCode(final String fname) {
        return "    ret = (int)" + fname + ";\n";
    }
    
    String genJavaConstructorSet(final String fname, final String name) {
        return "    this." + fname + "=" + name + ";\n";
    }
    
    String genCsharpWriteMethod(final String fname, final String tag) {
        return "    a_.Write" + this.mMethodSuffix + "(" + capitalize(fname) + ",\"" + tag + "\");\n";
    }
    
    String genCsharpReadMethod(final String fname, final String tag) {
        return "    " + capitalize(fname) + "=a_.Read" + this.mMethodSuffix + "(\"" + tag + "\");\n";
    }
    
    String genCsharpReadWrapper(final String fname, final String tag, final boolean decl) {
        String ret = "";
        if (decl) {
            ret = "    " + this.mWrapper + " " + fname + ";\n";
        }
        return ret + "    " + fname + "=a_.Read" + this.mMethodSuffix + "(\"" + tag + "\");\n";
    }
    
    String genCsharpWriteWrapper(final String fname, final String tag) {
        if (this.mUnwrapMethod == null) {
            return "        a_.Write" + this.mMethodSuffix + "(" + fname + "," + tag + ");\n";
        }
        return "        a_.Write" + this.mMethodSuffix + "(" + fname + "." + this.mUnwrapMethod + "(),\"" + tag + "\");\n";
    }
    
    String genCsharpCompareTo(final String name) {
        return "    ret = (" + capitalize(name) + " == peer." + capitalize(name) + ")? 0 :((" + capitalize(name) + "<peer." + capitalize(name) + ")?-1:1);\n";
    }
    
    String genCsharpEquals(final String name, final String peer) {
        final String[] peerSplit = peer.split("\\.");
        return "    ret = (" + capitalize(name) + "==" + peerSplit[0] + "." + capitalize(peerSplit[1]) + ");\n";
    }
    
    String genCsharpHashCode(final String fname) {
        return "    ret = (int)" + capitalize(fname) + ";\n";
    }
    
    String genCsharpConstructorSet(final String mName, final String fname) {
        return capitalize(fname) + "=" + mName + ";\n";
    }
    
    public String genCsharpConstructorParam(final String fname) {
        return "  " + this.mCsharpName + " " + fname + "\n";
    }
}
