// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.io.IOException;
import antlr.collections.impl.BitSet;

class LexerGrammar extends Grammar
{
    protected BitSet charVocabulary;
    protected boolean testLiterals;
    protected boolean caseSensitiveLiterals;
    protected boolean caseSensitive;
    protected boolean filterMode;
    protected String filterRule;
    
    LexerGrammar(final String s, final Tool tool, final String s2) {
        super(s, tool, s2);
        this.testLiterals = true;
        this.caseSensitiveLiterals = true;
        this.caseSensitive = true;
        this.filterMode = false;
        this.filterRule = null;
        final BitSet charVocabulary = new BitSet();
        for (int i = 0; i <= 127; ++i) {
            charVocabulary.add(i);
        }
        this.setCharVocabulary(charVocabulary);
        this.defaultErrorHandler = false;
    }
    
    public void generate() throws IOException {
        this.generator.gen(this);
    }
    
    public String getSuperClass() {
        if (this.debuggingOutput) {
            return "debug.DebuggingCharScanner";
        }
        return "CharScanner";
    }
    
    public boolean getTestLiterals() {
        return this.testLiterals;
    }
    
    public void processArguments(final String[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i].equals("-trace")) {
                this.traceRules = true;
                this.antlrTool.setArgOK(i);
            }
            else if (array[i].equals("-traceLexer")) {
                this.traceRules = true;
                this.antlrTool.setArgOK(i);
            }
            else if (array[i].equals("-debug")) {
                this.debuggingOutput = true;
                this.antlrTool.setArgOK(i);
            }
        }
    }
    
    public void setCharVocabulary(final BitSet charVocabulary) {
        this.charVocabulary = charVocabulary;
    }
    
    public boolean setOption(final String str, final Token token) {
        final String text = token.getText();
        if (str.equals("buildAST")) {
            this.antlrTool.warning("buildAST option is not valid for lexer", this.getFilename(), token.getLine(), token.getColumn());
            return true;
        }
        if (str.equals("testLiterals")) {
            if (text.equals("true")) {
                this.testLiterals = true;
            }
            else if (text.equals("false")) {
                this.testLiterals = false;
            }
            else {
                this.antlrTool.warning("testLiterals option must be true or false", this.getFilename(), token.getLine(), token.getColumn());
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
        if (str.equals("caseSensitive")) {
            if (text.equals("true")) {
                this.caseSensitive = true;
            }
            else if (text.equals("false")) {
                this.caseSensitive = false;
            }
            else {
                this.antlrTool.warning("caseSensitive option must be true or false", this.getFilename(), token.getLine(), token.getColumn());
            }
            return true;
        }
        if (str.equals("caseSensitiveLiterals")) {
            if (text.equals("true")) {
                this.caseSensitiveLiterals = true;
            }
            else if (text.equals("false")) {
                this.caseSensitiveLiterals = false;
            }
            else {
                this.antlrTool.warning("caseSensitiveLiterals option must be true or false", this.getFilename(), token.getLine(), token.getColumn());
            }
            return true;
        }
        if (str.equals("filter")) {
            if (text.equals("true")) {
                this.filterMode = true;
            }
            else if (text.equals("false")) {
                this.filterMode = false;
            }
            else if (token.getType() == 24) {
                this.filterMode = true;
                this.filterRule = text;
            }
            else {
                this.antlrTool.warning("filter option must be true, false, or a lexer rule name", this.getFilename(), token.getLine(), token.getColumn());
            }
            return true;
        }
        if (str.equals("longestPossible")) {
            this.antlrTool.warning("longestPossible option has been deprecated; ignoring it...", this.getFilename(), token.getLine(), token.getColumn());
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
