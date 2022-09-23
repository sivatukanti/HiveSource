// 
// Decompiled by Procyon v0.5.36
// 

package antlr.collections;

import antlr.Token;

public interface AST
{
    void addChild(final AST p0);
    
    boolean equals(final AST p0);
    
    boolean equalsList(final AST p0);
    
    boolean equalsListPartial(final AST p0);
    
    boolean equalsTree(final AST p0);
    
    boolean equalsTreePartial(final AST p0);
    
    ASTEnumeration findAll(final AST p0);
    
    ASTEnumeration findAllPartial(final AST p0);
    
    AST getFirstChild();
    
    AST getNextSibling();
    
    String getText();
    
    int getType();
    
    int getLine();
    
    int getColumn();
    
    int getNumberOfChildren();
    
    void initialize(final int p0, final String p1);
    
    void initialize(final AST p0);
    
    void initialize(final Token p0);
    
    void setFirstChild(final AST p0);
    
    void setNextSibling(final AST p0);
    
    void setText(final String p0);
    
    void setType(final int p0);
    
    String toString();
    
    String toStringList();
    
    String toStringTree();
}
