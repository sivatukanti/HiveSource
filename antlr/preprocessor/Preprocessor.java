// 
// Decompiled by Procyon v0.5.36
// 

package antlr.preprocessor;

import antlr.SemanticException;
import antlr.TokenStreamException;
import antlr.Token;
import antlr.collections.impl.IndexedVector;
import antlr.NoViableAltException;
import antlr.ParserSharedInputState;
import antlr.TokenStream;
import antlr.TokenBuffer;
import antlr.RecognitionException;
import antlr.collections.impl.BitSet;
import antlr.Tool;
import antlr.LLkParser;

public class Preprocessor extends LLkParser implements PreprocessorTokenTypes
{
    private Tool antlrTool;
    public static final String[] _tokenNames;
    public static final BitSet _tokenSet_0;
    public static final BitSet _tokenSet_1;
    public static final BitSet _tokenSet_2;
    public static final BitSet _tokenSet_3;
    public static final BitSet _tokenSet_4;
    public static final BitSet _tokenSet_5;
    public static final BitSet _tokenSet_6;
    public static final BitSet _tokenSet_7;
    public static final BitSet _tokenSet_8;
    
    public void setTool(final Tool antlrTool) {
        if (this.antlrTool == null) {
            this.antlrTool = antlrTool;
            return;
        }
        throw new IllegalStateException("antlr.Tool already registered");
    }
    
    protected Tool getTool() {
        return this.antlrTool;
    }
    
    public void reportError(final String s) {
        if (this.getTool() != null) {
            this.getTool().error(s, this.getFilename(), -1, -1);
        }
        else {
            super.reportError(s);
        }
    }
    
    public void reportError(final RecognitionException ex) {
        if (this.getTool() != null) {
            this.getTool().error(ex.getErrorMessage(), ex.getFilename(), ex.getLine(), ex.getColumn());
        }
        else {
            super.reportError(ex);
        }
    }
    
    public void reportWarning(final String s) {
        if (this.getTool() != null) {
            this.getTool().warning(s, this.getFilename(), -1, -1);
        }
        else {
            super.reportWarning(s);
        }
    }
    
    protected Preprocessor(final TokenBuffer tokenBuffer, final int n) {
        super(tokenBuffer, n);
        this.tokenNames = Preprocessor._tokenNames;
    }
    
    public Preprocessor(final TokenBuffer tokenBuffer) {
        this(tokenBuffer, 1);
    }
    
    protected Preprocessor(final TokenStream tokenStream, final int n) {
        super(tokenStream, n);
        this.tokenNames = Preprocessor._tokenNames;
    }
    
    public Preprocessor(final TokenStream tokenStream) {
        this(tokenStream, 1);
    }
    
    public Preprocessor(final ParserSharedInputState parserSharedInputState) {
        super(parserSharedInputState, 1);
        this.tokenNames = Preprocessor._tokenNames;
    }
    
    public final void grammarFile(final Hierarchy hierarchy, final String fileName) throws RecognitionException, TokenStreamException {
        IndexedVector optionSpec = null;
        try {
            while (this.LA(1) == 5) {
                final Token lt = this.LT(1);
                this.match(5);
                hierarchy.getFile(fileName).addHeaderAction(lt.getText());
            }
            switch (this.LA(1)) {
                case 13: {
                    optionSpec = this.optionSpec(null);
                    break;
                }
                case 1:
                case 7:
                case 8: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            while (this.LA(1) == 7 || this.LA(1) == 8) {
                final Grammar class_def = this.class_def(fileName, hierarchy);
                if (class_def != null && optionSpec != null) {
                    hierarchy.getFile(fileName).setOptions(optionSpec);
                }
                if (class_def != null) {
                    class_def.setFileName(fileName);
                    hierarchy.addGrammar(class_def);
                }
            }
            this.match(1);
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            this.consume();
            this.consumeUntil(Preprocessor._tokenSet_0);
        }
    }
    
    public final IndexedVector optionSpec(final Grammar grammar) throws RecognitionException, TokenStreamException {
        final IndexedVector indexedVector = new IndexedVector();
        try {
            this.match(13);
            while (this.LA(1) == 9) {
                final Token lt = this.LT(1);
                this.match(9);
                final Token lt2 = this.LT(1);
                this.match(14);
                final Option option = new Option(lt.getText(), lt2.getText(), grammar);
                indexedVector.appendElement(option.getName(), option);
                if (grammar != null && lt.getText().equals("importVocab")) {
                    grammar.specifiedVocabulary = true;
                    grammar.importVocab = lt2.getText();
                }
                else {
                    if (grammar == null || !lt.getText().equals("exportVocab")) {
                        continue;
                    }
                    grammar.exportVocab = lt2.getText().substring(0, lt2.getText().length() - 1);
                    grammar.exportVocab = grammar.exportVocab.trim();
                }
            }
            this.match(15);
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            this.consume();
            this.consumeUntil(Preprocessor._tokenSet_1);
        }
        return indexedVector;
    }
    
    public final Grammar class_def(final String s, final Hierarchy hierarchy) throws RecognitionException, TokenStreamException {
        Token lt = null;
        Grammar grammar = null;
        final IndexedVector indexedVector = new IndexedVector(100);
        IndexedVector optionSpec = null;
        String superClass = null;
        try {
            switch (this.LA(1)) {
                case 7: {
                    lt = this.LT(1);
                    this.match(7);
                    break;
                }
                case 8: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            this.match(8);
            final Token lt2 = this.LT(1);
            this.match(9);
            this.match(10);
            final Token lt3 = this.LT(1);
            this.match(9);
            switch (this.LA(1)) {
                case 6: {
                    superClass = this.superClass();
                    break;
                }
                case 11: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            this.match(11);
            grammar = hierarchy.getGrammar(lt2.getText());
            if (grammar != null) {
                grammar = null;
                throw new SemanticException("redefinition of grammar " + lt2.getText(), s, lt2.getLine(), lt2.getColumn());
            }
            grammar = new Grammar(hierarchy.getTool(), lt2.getText(), lt3.getText(), indexedVector);
            grammar.superClass = superClass;
            if (lt != null) {
                grammar.setPreambleAction(lt.getText());
            }
            switch (this.LA(1)) {
                case 13: {
                    optionSpec = this.optionSpec(grammar);
                    break;
                }
                case 7:
                case 9:
                case 12:
                case 16:
                case 17:
                case 18: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            if (grammar != null) {
                grammar.setOptions(optionSpec);
            }
            switch (this.LA(1)) {
                case 12: {
                    final Token lt4 = this.LT(1);
                    this.match(12);
                    grammar.setTokenSection(lt4.getText());
                    break;
                }
                case 7:
                case 9:
                case 16:
                case 17:
                case 18: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            switch (this.LA(1)) {
                case 7: {
                    final Token lt5 = this.LT(1);
                    this.match(7);
                    grammar.setMemberAction(lt5.getText());
                    break;
                }
                case 9:
                case 16:
                case 17:
                case 18: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            int n = 0;
            while (Preprocessor._tokenSet_2.member(this.LA(1))) {
                this.rule(grammar);
                ++n;
            }
            if (n < 1) {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            this.consume();
            this.consumeUntil(Preprocessor._tokenSet_3);
        }
        return grammar;
    }
    
    public final String superClass() throws RecognitionException, TokenStreamException {
        final String text = this.LT(1).getText();
        try {
            this.match(6);
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            this.consume();
            this.consumeUntil(Preprocessor._tokenSet_4);
        }
        return text;
    }
    
    public final void rule(final Grammar grammar) throws RecognitionException, TokenStreamException {
        Token lt = null;
        Token lt2 = null;
        Token lt3 = null;
        IndexedVector optionSpec = null;
        String visibility = null;
        boolean b = false;
        String throwsSpec = "";
        try {
            switch (this.LA(1)) {
                case 16: {
                    this.match(16);
                    visibility = "protected";
                    break;
                }
                case 17: {
                    this.match(17);
                    visibility = "private";
                    break;
                }
                case 18: {
                    this.match(18);
                    visibility = "public";
                    break;
                }
                case 9: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            final Token lt4 = this.LT(1);
            this.match(9);
            switch (this.LA(1)) {
                case 19: {
                    this.match(19);
                    b = true;
                    break;
                }
                case 7:
                case 13:
                case 20:
                case 21:
                case 22:
                case 23: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            switch (this.LA(1)) {
                case 20: {
                    lt = this.LT(1);
                    this.match(20);
                    break;
                }
                case 7:
                case 13:
                case 21:
                case 22:
                case 23: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            switch (this.LA(1)) {
                case 21: {
                    this.match(21);
                    lt2 = this.LT(1);
                    this.match(20);
                    break;
                }
                case 7:
                case 13:
                case 22:
                case 23: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            switch (this.LA(1)) {
                case 23: {
                    throwsSpec = this.throwsSpec();
                    break;
                }
                case 7:
                case 13:
                case 22: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            switch (this.LA(1)) {
                case 13: {
                    optionSpec = this.optionSpec(null);
                    break;
                }
                case 7:
                case 22: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            switch (this.LA(1)) {
                case 7: {
                    lt3 = this.LT(1);
                    this.match(7);
                    break;
                }
                case 22: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            final Token lt5 = this.LT(1);
            this.match(22);
            final Rule rule = new Rule(lt4.getText(), lt5.getText() + this.exceptionGroup(), optionSpec, grammar);
            rule.setThrowsSpec(throwsSpec);
            if (lt != null) {
                rule.setArgs(lt.getText());
            }
            if (lt2 != null) {
                rule.setReturnValue(lt2.getText());
            }
            if (lt3 != null) {
                rule.setInitAction(lt3.getText());
            }
            if (b) {
                rule.setBang();
            }
            rule.setVisibility(visibility);
            if (grammar != null) {
                grammar.addRule(rule);
            }
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            this.consume();
            this.consumeUntil(Preprocessor._tokenSet_5);
        }
    }
    
    public final String throwsSpec() throws RecognitionException, TokenStreamException {
        String s = "throws ";
        try {
            this.match(23);
            final Token lt = this.LT(1);
            this.match(9);
            s += lt.getText();
            while (this.LA(1) == 24) {
                this.match(24);
                final Token lt2 = this.LT(1);
                this.match(9);
                s = s + "," + lt2.getText();
            }
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            this.consume();
            this.consumeUntil(Preprocessor._tokenSet_6);
        }
        return s;
    }
    
    public final String exceptionGroup() throws RecognitionException, TokenStreamException {
        String string = "";
        try {
            while (this.LA(1) == 25) {
                string += this.exceptionSpec();
            }
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            this.consume();
            this.consumeUntil(Preprocessor._tokenSet_5);
        }
        return string;
    }
    
    public final String exceptionSpec() throws RecognitionException, TokenStreamException {
        String s = System.getProperty("line.separator") + "exception ";
        try {
            this.match(25);
            switch (this.LA(1)) {
                case 20: {
                    final Token lt = this.LT(1);
                    this.match(20);
                    s += lt.getText();
                    break;
                }
                case 1:
                case 7:
                case 8:
                case 9:
                case 16:
                case 17:
                case 18:
                case 25:
                case 26: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            while (this.LA(1) == 26) {
                s += this.exceptionHandler();
            }
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            this.consume();
            this.consumeUntil(Preprocessor._tokenSet_7);
        }
        return s;
    }
    
    public final String exceptionHandler() throws RecognitionException, TokenStreamException {
        String string = null;
        try {
            this.match(26);
            final Token lt = this.LT(1);
            this.match(20);
            final Token lt2 = this.LT(1);
            this.match(7);
            string = System.getProperty("line.separator") + "catch " + lt.getText() + " " + lt2.getText();
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            this.consume();
            this.consumeUntil(Preprocessor._tokenSet_8);
        }
        return string;
    }
    
    private static final long[] mk_tokenSet_0() {
        return new long[] { 2L, 0L };
    }
    
    private static final long[] mk_tokenSet_1() {
        return new long[] { 4658050L, 0L };
    }
    
    private static final long[] mk_tokenSet_2() {
        return new long[] { 459264L, 0L };
    }
    
    private static final long[] mk_tokenSet_3() {
        return new long[] { 386L, 0L };
    }
    
    private static final long[] mk_tokenSet_4() {
        return new long[] { 2048L, 0L };
    }
    
    private static final long[] mk_tokenSet_5() {
        return new long[] { 459650L, 0L };
    }
    
    private static final long[] mk_tokenSet_6() {
        return new long[] { 4202624L, 0L };
    }
    
    private static final long[] mk_tokenSet_7() {
        return new long[] { 34014082L, 0L };
    }
    
    private static final long[] mk_tokenSet_8() {
        return new long[] { 101122946L, 0L };
    }
    
    static {
        _tokenNames = new String[] { "<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "\"tokens\"", "HEADER_ACTION", "SUBRULE_BLOCK", "ACTION", "\"class\"", "ID", "\"extends\"", "SEMI", "TOKENS_SPEC", "OPTIONS_START", "ASSIGN_RHS", "RCURLY", "\"protected\"", "\"private\"", "\"public\"", "BANG", "ARG_ACTION", "\"returns\"", "RULE_BLOCK", "\"throws\"", "COMMA", "\"exception\"", "\"catch\"", "ALT", "ELEMENT", "LPAREN", "RPAREN", "ID_OR_KEYWORD", "CURLY_BLOCK_SCARF", "WS", "NEWLINE", "COMMENT", "SL_COMMENT", "ML_COMMENT", "CHAR_LITERAL", "STRING_LITERAL", "ESC", "DIGIT", "XDIGIT" };
        _tokenSet_0 = new BitSet(mk_tokenSet_0());
        _tokenSet_1 = new BitSet(mk_tokenSet_1());
        _tokenSet_2 = new BitSet(mk_tokenSet_2());
        _tokenSet_3 = new BitSet(mk_tokenSet_3());
        _tokenSet_4 = new BitSet(mk_tokenSet_4());
        _tokenSet_5 = new BitSet(mk_tokenSet_5());
        _tokenSet_6 = new BitSet(mk_tokenSet_6());
        _tokenSet_7 = new BitSet(mk_tokenSet_7());
        _tokenSet_8 = new BitSet(mk_tokenSet_8());
    }
}
