// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.io.IOException;

class TreeWalkerGrammar extends Grammar
{
    protected boolean transform;
    
    TreeWalkerGrammar(final String s, final Tool tool, final String s2) {
        super(s, tool, s2);
        this.transform = false;
    }
    
    public void generate() throws IOException {
        this.generator.gen(this);
    }
    
    protected String getSuperClass() {
        return "TreeParser";
    }
    
    public void processArguments(final String[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i].equals("-trace")) {
                this.traceRules = true;
                this.antlrTool.setArgOK(i);
            }
            else if (array[i].equals("-traceTreeParser")) {
                this.traceRules = true;
                this.antlrTool.setArgOK(i);
            }
        }
    }
    
    public boolean setOption(final String str, final Token token) {
        if (str.equals("buildAST")) {
            if (token.getText().equals("true")) {
                this.buildAST = true;
            }
            else if (token.getText().equals("false")) {
                this.buildAST = false;
            }
            else {
                this.antlrTool.error("buildAST option must be true or false", this.getFilename(), token.getLine(), token.getColumn());
            }
            return true;
        }
        if (str.equals("ASTLabelType")) {
            super.setOption(str, token);
            return true;
        }
        if (str.equals("className")) {
            super.setOption(str, token);
            return true;
        }
        if (super.setOption(str, token)) {
            return true;
        }
        this.antlrTool.error("Invalid option: " + str, this.getFilename(), token.getLine(), token.getColumn());
        return false;
    }
}
