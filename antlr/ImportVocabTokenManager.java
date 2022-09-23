// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

class ImportVocabTokenManager extends SimpleTokenManager implements Cloneable
{
    private String filename;
    protected Grammar grammar;
    
    ImportVocabTokenManager(final Grammar grammar, final String filename, final String s, final Tool tool) {
        super(s, tool);
        this.grammar = grammar;
        this.filename = filename;
        File file = new File(this.filename);
        if (!file.exists()) {
            file = new File(this.antlrTool.getOutputDirectory(), this.filename);
            if (!file.exists()) {
                this.antlrTool.panic("Cannot find importVocab file '" + this.filename + "'");
            }
        }
        this.setReadOnly(true);
        try {
            final ANTLRTokdefParser antlrTokdefParser = new ANTLRTokdefParser(new ANTLRTokdefLexer(new BufferedReader(new FileReader(file))));
            antlrTokdefParser.setTool(this.antlrTool);
            antlrTokdefParser.setFilename(this.filename);
            antlrTokdefParser.file(this);
        }
        catch (FileNotFoundException ex2) {
            this.antlrTool.panic("Cannot find importVocab file '" + this.filename + "'");
        }
        catch (RecognitionException ex) {
            this.antlrTool.panic("Error parsing importVocab file '" + this.filename + "': " + ex.toString());
        }
        catch (TokenStreamException ex3) {
            this.antlrTool.panic("Error reading importVocab file '" + this.filename + "'");
        }
    }
    
    public Object clone() {
        final ImportVocabTokenManager importVocabTokenManager = (ImportVocabTokenManager)super.clone();
        importVocabTokenManager.filename = this.filename;
        importVocabTokenManager.grammar = this.grammar;
        return importVocabTokenManager;
    }
    
    public void define(final TokenSymbol tokenSymbol) {
        super.define(tokenSymbol);
    }
    
    public void define(final String s, final int tokenType) {
        TokenSymbol tokenSymbol;
        if (s.startsWith("\"")) {
            tokenSymbol = new StringLiteralSymbol(s);
        }
        else {
            tokenSymbol = new TokenSymbol(s);
        }
        tokenSymbol.setTokenType(tokenType);
        super.define(tokenSymbol);
        this.maxToken = ((tokenType + 1 > this.maxToken) ? (tokenType + 1) : this.maxToken);
    }
    
    public boolean isReadOnly() {
        return this.readOnly;
    }
    
    public int nextTokenType() {
        return super.nextTokenType();
    }
}
