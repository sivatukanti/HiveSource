// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.io.IOException;

class ParserGrammar extends Grammar
{
    ParserGrammar(final String s, final Tool tool, final String s2) {
        super(s, tool, s2);
    }
    
    public void generate() throws IOException {
        this.generator.gen(this);
    }
    
    protected String getSuperClass() {
        if (this.debuggingOutput) {
            return "debug.LLkDebuggingParser";
        }
        return "LLkParser";
    }
    
    public void processArguments(final String[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i].equals("-trace")) {
                this.traceRules = true;
                this.antlrTool.setArgOK(i);
            }
            else if (array[i].equals("-traceParser")) {
                this.traceRules = true;
                this.antlrTool.setArgOK(i);
            }
            else if (array[i].equals("-debug")) {
                this.debuggingOutput = true;
                this.antlrTool.setArgOK(i);
            }
        }
    }
    
    public boolean setOption(final String str, final Token token) {
        final String text = token.getText();
        if (str.equals("buildAST")) {
            if (text.equals("true")) {
                this.buildAST = true;
            }
            else if (text.equals("false")) {
                this.buildAST = false;
            }
            else {
                this.antlrTool.error("buildAST option must be true or false", this.getFilename(), token.getLine(), token.getColumn());
            }
            return true;
        }
        if (str.equals("interactive")) {
            if (text.equals("true")) {
                this.interactive = true;
            }
            else if (text.equals("false")) {
                this.interactive = false;
            }
            else {
                this.antlrTool.error("interactive option must be true or false", this.getFilename(), token.getLine(), token.getColumn());
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
