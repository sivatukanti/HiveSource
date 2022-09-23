// 
// Decompiled by Procyon v0.5.36
// 

package antlr.preprocessor;

import java.io.FileNotFoundException;
import antlr.ANTLRException;
import antlr.TokenStreamException;
import antlr.TokenStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Enumeration;
import antlr.collections.impl.IndexedVector;
import antlr.Tool;
import java.util.Hashtable;

public class Hierarchy
{
    protected Grammar LexerRoot;
    protected Grammar ParserRoot;
    protected Grammar TreeParserRoot;
    protected Hashtable symbols;
    protected Hashtable files;
    protected Tool antlrTool;
    
    public Hierarchy(final Tool antlrTool) {
        this.LexerRoot = null;
        this.ParserRoot = null;
        this.TreeParserRoot = null;
        this.antlrTool = antlrTool;
        this.LexerRoot = new Grammar(antlrTool, "Lexer", null, null);
        this.ParserRoot = new Grammar(antlrTool, "Parser", null, null);
        this.TreeParserRoot = new Grammar(antlrTool, "TreeParser", null, null);
        this.symbols = new Hashtable(10);
        this.files = new Hashtable(10);
        this.LexerRoot.setPredefined(true);
        this.ParserRoot.setPredefined(true);
        this.TreeParserRoot.setPredefined(true);
        this.symbols.put(this.LexerRoot.getName(), this.LexerRoot);
        this.symbols.put(this.ParserRoot.getName(), this.ParserRoot);
        this.symbols.put(this.TreeParserRoot.getName(), this.TreeParserRoot);
    }
    
    public void addGrammar(final Grammar value) {
        value.setHierarchy(this);
        this.symbols.put(value.getName(), value);
        this.getFile(value.getFileName()).addGrammar(value);
    }
    
    public void addGrammarFile(final GrammarFile value) {
        this.files.put(value.getName(), value);
    }
    
    public void expandGrammarsInFile(final String s) {
        final Enumeration elements = this.getFile(s).getGrammars().elements();
        while (elements.hasMoreElements()) {
            elements.nextElement().expandInPlace();
        }
    }
    
    public Grammar findRoot(final Grammar grammar) {
        if (grammar.getSuperGrammarName() == null) {
            return grammar;
        }
        final Grammar superGrammar = grammar.getSuperGrammar();
        if (superGrammar == null) {
            return grammar;
        }
        return this.findRoot(superGrammar);
    }
    
    public GrammarFile getFile(final String key) {
        return this.files.get(key);
    }
    
    public Grammar getGrammar(final String key) {
        return this.symbols.get(key);
    }
    
    public static String optionsToString(final IndexedVector indexedVector) {
        String s = "options {" + System.getProperty("line.separator");
        final Enumeration elements = indexedVector.elements();
        while (elements.hasMoreElements()) {
            s = s + elements.nextElement() + System.getProperty("line.separator");
        }
        return s + "}" + System.getProperty("line.separator") + System.getProperty("line.separator");
    }
    
    public void readGrammarFile(final String filename) throws FileNotFoundException {
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
        this.addGrammarFile(new GrammarFile(this.antlrTool, filename));
        final PreprocessorLexer preprocessorLexer = new PreprocessorLexer(bufferedReader);
        preprocessorLexer.setFilename(filename);
        final Preprocessor preprocessor = new Preprocessor(preprocessorLexer);
        preprocessor.setTool(this.antlrTool);
        preprocessor.setFilename(filename);
        try {
            preprocessor.grammarFile(this, filename);
        }
        catch (TokenStreamException obj) {
            this.antlrTool.toolError("Token stream error reading grammar(s):\n" + obj);
        }
        catch (ANTLRException obj2) {
            this.antlrTool.toolError("error reading grammar(s):\n" + obj2);
        }
    }
    
    public boolean verifyThatHierarchyIsComplete() {
        boolean b = true;
        final Enumeration<Grammar> elements = (Enumeration<Grammar>)this.symbols.elements();
        while (elements.hasMoreElements()) {
            final Grammar grammar = elements.nextElement();
            if (grammar.getSuperGrammarName() == null) {
                continue;
            }
            if (grammar.getSuperGrammar() != null) {
                continue;
            }
            this.antlrTool.toolError("grammar " + grammar.getSuperGrammarName() + " not defined");
            b = false;
            this.symbols.remove(grammar.getName());
        }
        if (!b) {
            return false;
        }
        final Enumeration<Grammar> elements2 = (Enumeration<Grammar>)this.symbols.elements();
        while (elements2.hasMoreElements()) {
            final Grammar grammar2 = elements2.nextElement();
            if (grammar2.getSuperGrammarName() == null) {
                continue;
            }
            grammar2.setType(this.findRoot(grammar2).getName());
        }
        return true;
    }
    
    public Tool getTool() {
        return this.antlrTool;
    }
    
    public void setTool(final Tool antlrTool) {
        this.antlrTool = antlrTool;
    }
}
