// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.ASTEnumeration;
import antlr.collections.AST;

public class ASTNULLType implements AST
{
    public void addChild(final AST ast) {
    }
    
    public boolean equals(final AST ast) {
        return false;
    }
    
    public boolean equalsList(final AST ast) {
        return false;
    }
    
    public boolean equalsListPartial(final AST ast) {
        return false;
    }
    
    public boolean equalsTree(final AST ast) {
        return false;
    }
    
    public boolean equalsTreePartial(final AST ast) {
        return false;
    }
    
    public ASTEnumeration findAll(final AST ast) {
        return null;
    }
    
    public ASTEnumeration findAllPartial(final AST ast) {
        return null;
    }
    
    public AST getFirstChild() {
        return this;
    }
    
    public AST getNextSibling() {
        return this;
    }
    
    public String getText() {
        return "<ASTNULL>";
    }
    
    public int getType() {
        return 3;
    }
    
    public int getLine() {
        return 0;
    }
    
    public int getColumn() {
        return 0;
    }
    
    public int getNumberOfChildren() {
        return 0;
    }
    
    public void initialize(final int n, final String s) {
    }
    
    public void initialize(final AST ast) {
    }
    
    public void initialize(final Token token) {
    }
    
    public void setFirstChild(final AST ast) {
    }
    
    public void setNextSibling(final AST ast) {
    }
    
    public void setText(final String s) {
    }
    
    public void setType(final int n) {
    }
    
    public String toString() {
        return this.getText();
    }
    
    public String toStringList() {
        return this.getText();
    }
    
    public String toStringTree() {
        return this.getText();
    }
}
