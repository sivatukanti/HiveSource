// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.commons;

import org.objectweb.asm.signature.SignatureVisitor;

public class RemappingSignatureAdapter implements SignatureVisitor
{
    private final SignatureVisitor v;
    private final Remapper remapper;
    private String className;
    
    public RemappingSignatureAdapter(final SignatureVisitor v, final Remapper remapper) {
        this.v = v;
        this.remapper = remapper;
    }
    
    public void visitClassType(final String className) {
        this.className = className;
        this.v.visitClassType(this.remapper.mapType(className));
    }
    
    public void visitInnerClassType(final String str) {
        this.className = this.className + '$' + str;
        final String mapType = this.remapper.mapType(this.className);
        this.v.visitInnerClassType(mapType.substring(mapType.lastIndexOf(36) + 1));
    }
    
    public void visitFormalTypeParameter(final String s) {
        this.v.visitFormalTypeParameter(s);
    }
    
    public void visitTypeVariable(final String s) {
        this.v.visitTypeVariable(s);
    }
    
    public SignatureVisitor visitArrayType() {
        this.v.visitArrayType();
        return (SignatureVisitor)this;
    }
    
    public void visitBaseType(final char c) {
        this.v.visitBaseType(c);
    }
    
    public SignatureVisitor visitClassBound() {
        this.v.visitClassBound();
        return (SignatureVisitor)this;
    }
    
    public SignatureVisitor visitExceptionType() {
        this.v.visitExceptionType();
        return (SignatureVisitor)this;
    }
    
    public SignatureVisitor visitInterface() {
        this.v.visitInterface();
        return (SignatureVisitor)this;
    }
    
    public SignatureVisitor visitInterfaceBound() {
        this.v.visitInterfaceBound();
        return (SignatureVisitor)this;
    }
    
    public SignatureVisitor visitParameterType() {
        this.v.visitParameterType();
        return (SignatureVisitor)this;
    }
    
    public SignatureVisitor visitReturnType() {
        this.v.visitReturnType();
        return (SignatureVisitor)this;
    }
    
    public SignatureVisitor visitSuperclass() {
        this.v.visitSuperclass();
        return (SignatureVisitor)this;
    }
    
    public void visitTypeArgument() {
        this.v.visitTypeArgument();
    }
    
    public SignatureVisitor visitTypeArgument(final char c) {
        this.v.visitTypeArgument(c);
        return (SignatureVisitor)this;
    }
    
    public void visitEnd() {
        this.v.visitEnd();
    }
}
