// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.BitSet;
import java.util.Hashtable;

public abstract class CharScanner implements TokenStream
{
    static final char NO_CHAR = '\0';
    public static final char EOF_CHAR = '\uffff';
    protected ANTLRStringBuffer text;
    protected boolean saveConsumedInput;
    protected Class tokenObjectClass;
    protected boolean caseSensitive;
    protected boolean caseSensitiveLiterals;
    protected Hashtable literals;
    protected int tabsize;
    protected Token _returnToken;
    protected ANTLRHashString hashString;
    protected LexerSharedInputState inputState;
    protected boolean commitToPath;
    protected int traceDepth;
    
    public CharScanner() {
        this.saveConsumedInput = true;
        this.caseSensitive = true;
        this.caseSensitiveLiterals = true;
        this.tabsize = 8;
        this._returnToken = null;
        this.commitToPath = false;
        this.traceDepth = 0;
        this.text = new ANTLRStringBuffer();
        this.hashString = new ANTLRHashString(this);
        this.setTokenObjectClass("antlr.CommonToken");
    }
    
    public CharScanner(final InputBuffer inputBuffer) {
        this();
        this.inputState = new LexerSharedInputState(inputBuffer);
    }
    
    public CharScanner(final LexerSharedInputState inputState) {
        this();
        this.inputState = inputState;
    }
    
    public void append(final char c) {
        if (this.saveConsumedInput) {
            this.text.append(c);
        }
    }
    
    public void append(final String s) {
        if (this.saveConsumedInput) {
            this.text.append(s);
        }
    }
    
    public void commit() {
        this.inputState.input.commit();
    }
    
    public void consume() throws CharStreamException {
        if (this.inputState.guessing == 0) {
            final char la = this.LA(1);
            if (this.caseSensitive) {
                this.append(la);
            }
            else {
                this.append(this.inputState.input.LA(1));
            }
            if (la == '\t') {
                this.tab();
            }
            else {
                final LexerSharedInputState inputState = this.inputState;
                ++inputState.column;
            }
        }
        this.inputState.input.consume();
    }
    
    public void consumeUntil(final int n) throws CharStreamException {
        while (this.LA(1) != '\uffff' && this.LA(1) != n) {
            this.consume();
        }
    }
    
    public void consumeUntil(final BitSet set) throws CharStreamException {
        while (this.LA(1) != '\uffff' && !set.member(this.LA(1))) {
            this.consume();
        }
    }
    
    public boolean getCaseSensitive() {
        return this.caseSensitive;
    }
    
    public final boolean getCaseSensitiveLiterals() {
        return this.caseSensitiveLiterals;
    }
    
    public int getColumn() {
        return this.inputState.column;
    }
    
    public void setColumn(final int column) {
        this.inputState.column = column;
    }
    
    public boolean getCommitToPath() {
        return this.commitToPath;
    }
    
    public String getFilename() {
        return this.inputState.filename;
    }
    
    public InputBuffer getInputBuffer() {
        return this.inputState.input;
    }
    
    public LexerSharedInputState getInputState() {
        return this.inputState;
    }
    
    public void setInputState(final LexerSharedInputState inputState) {
        this.inputState = inputState;
    }
    
    public int getLine() {
        return this.inputState.line;
    }
    
    public String getText() {
        return this.text.toString();
    }
    
    public Token getTokenObject() {
        return this._returnToken;
    }
    
    public char LA(final int n) throws CharStreamException {
        if (this.caseSensitive) {
            return this.inputState.input.LA(n);
        }
        return this.toLower(this.inputState.input.LA(n));
    }
    
    protected Token makeToken(final int type) {
        try {
            final Token token = this.tokenObjectClass.newInstance();
            token.setType(type);
            token.setColumn(this.inputState.tokenStartColumn);
            token.setLine(this.inputState.tokenStartLine);
            return token;
        }
        catch (InstantiationException ex) {
            this.panic("can't instantiate token: " + this.tokenObjectClass);
        }
        catch (IllegalAccessException ex2) {
            this.panic("Token class is not accessible" + this.tokenObjectClass);
        }
        return Token.badToken;
    }
    
    public int mark() {
        return this.inputState.input.mark();
    }
    
    public void match(final char c) throws MismatchedCharException, CharStreamException {
        if (this.LA(1) != c) {
            throw new MismatchedCharException(this.LA(1), c, false, this);
        }
        this.consume();
    }
    
    public void match(final BitSet set) throws MismatchedCharException, CharStreamException {
        if (!set.member(this.LA(1))) {
            throw new MismatchedCharException(this.LA(1), set, false, this);
        }
        this.consume();
    }
    
    public void match(final String s) throws MismatchedCharException, CharStreamException {
        for (int length = s.length(), i = 0; i < length; ++i) {
            if (this.LA(1) != s.charAt(i)) {
                throw new MismatchedCharException(this.LA(1), s.charAt(i), false, this);
            }
            this.consume();
        }
    }
    
    public void matchNot(final char c) throws MismatchedCharException, CharStreamException {
        if (this.LA(1) == c) {
            throw new MismatchedCharException(this.LA(1), c, true, this);
        }
        this.consume();
    }
    
    public void matchRange(final char c, final char c2) throws MismatchedCharException, CharStreamException {
        if (this.LA(1) < c || this.LA(1) > c2) {
            throw new MismatchedCharException(this.LA(1), c, c2, false, this);
        }
        this.consume();
    }
    
    public void newline() {
        final LexerSharedInputState inputState = this.inputState;
        ++inputState.line;
        this.inputState.column = 1;
    }
    
    public void tab() {
        this.setColumn(((this.getColumn() - 1) / this.tabsize + 1) * this.tabsize + 1);
    }
    
    public void setTabSize(final int tabsize) {
        this.tabsize = tabsize;
    }
    
    public int getTabSize() {
        return this.tabsize;
    }
    
    public void panic() {
        System.err.println("CharScanner: panic");
        Utils.error("");
    }
    
    public void panic(final String str) {
        System.err.println("CharScanner; panic: " + str);
        Utils.error(str);
    }
    
    public void reportError(final RecognitionException x) {
        System.err.println(x);
    }
    
    public void reportError(final String s) {
        if (this.getFilename() == null) {
            System.err.println("error: " + s);
        }
        else {
            System.err.println(this.getFilename() + ": error: " + s);
        }
    }
    
    public void reportWarning(final String s) {
        if (this.getFilename() == null) {
            System.err.println("warning: " + s);
        }
        else {
            System.err.println(this.getFilename() + ": warning: " + s);
        }
    }
    
    public void resetText() {
        this.text.setLength(0);
        this.inputState.tokenStartColumn = this.inputState.column;
        this.inputState.tokenStartLine = this.inputState.line;
    }
    
    public void rewind(final int n) {
        this.inputState.input.rewind(n);
    }
    
    public void setCaseSensitive(final boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }
    
    public void setCommitToPath(final boolean commitToPath) {
        this.commitToPath = commitToPath;
    }
    
    public void setFilename(final String filename) {
        this.inputState.filename = filename;
    }
    
    public void setLine(final int line) {
        this.inputState.line = line;
    }
    
    public void setText(final String s) {
        this.resetText();
        this.text.append(s);
    }
    
    public void setTokenObjectClass(final String str) {
        try {
            this.tokenObjectClass = Utils.loadClass(str);
        }
        catch (ClassNotFoundException ex) {
            this.panic("ClassNotFoundException: " + str);
        }
    }
    
    public int testLiteralsTable(int intValue) {
        this.hashString.setBuffer(this.text.getBuffer(), this.text.length());
        final Integer n = this.literals.get(this.hashString);
        if (n != null) {
            intValue = n;
        }
        return intValue;
    }
    
    public int testLiteralsTable(final String s, int intValue) {
        final Integer n = this.literals.get(new ANTLRHashString(s, this));
        if (n != null) {
            intValue = n;
        }
        return intValue;
    }
    
    public char toLower(final char ch) {
        return Character.toLowerCase(ch);
    }
    
    public void traceIndent() {
        for (int i = 0; i < this.traceDepth; ++i) {
            System.out.print(" ");
        }
    }
    
    public void traceIn(final String str) throws CharStreamException {
        ++this.traceDepth;
        this.traceIndent();
        System.out.println("> lexer " + str + "; c==" + this.LA(1));
    }
    
    public void traceOut(final String str) throws CharStreamException {
        this.traceIndent();
        System.out.println("< lexer " + str + "; c==" + this.LA(1));
        --this.traceDepth;
    }
    
    public void uponEOF() throws TokenStreamException, CharStreamException {
    }
}
