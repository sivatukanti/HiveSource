// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.AST;

public class CommonAST extends BaseAST
{
    int ttype;
    String text;
    
    public String getText() {
        return this.text;
    }
    
    public int getType() {
        return this.ttype;
    }
    
    public void initialize(final int type, final String text) {
        this.setType(type);
        this.setText(text);
    }
    
    public void initialize(final AST ast) {
        this.setText(ast.getText());
        this.setType(ast.getType());
    }
    
    public CommonAST() {
        this.ttype = 0;
    }
    
    public CommonAST(final Token token) {
        this.ttype = 0;
        this.initialize(token);
    }
    
    public void initialize(final Token token) {
        this.setText(token.getText());
        this.setType(token.getType());
    }
    
    public void setText(final String text) {
        this.text = text;
    }
    
    public void setType(final int ttype) {
        this.ttype = ttype;
    }
}
