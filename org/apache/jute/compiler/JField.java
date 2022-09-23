// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute.compiler;

public class JField
{
    private JType mType;
    private String mName;
    
    public JField(final JType type, final String name) {
        this.mType = type;
        this.mName = name;
    }
    
    public String getSignature() {
        return this.mType.getSignature();
    }
    
    public String genCppDecl() {
        return this.mType.genCppDecl(this.mName);
    }
    
    public String genCDecl() {
        return this.mType.genCDecl(this.mName);
    }
    
    public String genCsharpDecl() {
        return this.mType.genCsharpDecl(this.mName);
    }
    
    public String genCsharpConstructorParam(final String fname) {
        return this.mType.genCsharpConstructorParam(fname);
    }
    
    public String genJavaDecl() {
        return this.mType.genJavaDecl(this.mName);
    }
    
    public String genJavaConstructorParam(final String fname) {
        return this.mType.genJavaConstructorParam(fname);
    }
    
    public String getName() {
        return this.mName;
    }
    
    public String getCsharpName() {
        return "Id".equals(this.mName) ? "ZKId" : this.mName;
    }
    
    public String getTag() {
        return this.mName;
    }
    
    public JType getType() {
        return this.mType;
    }
    
    public String genCppGetSet(final int fIdx) {
        return this.mType.genCppGetSet(this.mName, fIdx);
    }
    
    public String genCsharpConstructorSet(final String fname) {
        return this.mType.genCsharpConstructorSet(this.mName, fname);
    }
    
    public String genCsharpGetSet(final int fIdx) {
        return this.mType.genCsharpGetSet(this.getCsharpName(), fIdx);
    }
    
    public String genCsharpWriteMethodName() {
        return this.mType.genCsharpWriteMethod(this.getCsharpName(), this.getTag());
    }
    
    public String genCsharpReadMethodName() {
        return this.mType.genCsharpReadMethod(this.getCsharpName(), this.getTag());
    }
    
    public String genCsharpCompareTo() {
        return this.mType.genCsharpCompareTo(this.getCsharpName());
    }
    
    public String genCsharpEquals() {
        return this.mType.genCsharpEquals(this.getCsharpName(), "peer." + this.getCsharpName());
    }
    
    public String genCsharpHashCode() {
        return this.mType.genCsharpHashCode(this.getCsharpName());
    }
    
    public String genJavaGetSet(final int fIdx) {
        return this.mType.genJavaGetSet(this.mName, fIdx);
    }
    
    public String genJavaWriteMethodName() {
        return this.mType.genJavaWriteMethod(this.getName(), this.getTag());
    }
    
    public String genJavaReadMethodName() {
        return this.mType.genJavaReadMethod(this.getName(), this.getTag());
    }
    
    public String genJavaCompareTo() {
        return this.mType.genJavaCompareTo(this.getName());
    }
    
    public String genJavaEquals() {
        return this.mType.genJavaEquals(this.getName(), "peer." + this.getName());
    }
    
    public String genJavaHashCode() {
        return this.mType.genJavaHashCode(this.getName());
    }
    
    public String genJavaConstructorSet(final String fname) {
        return this.mType.genJavaConstructorSet(this.mName, fname);
    }
}
