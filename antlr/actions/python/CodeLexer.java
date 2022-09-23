// 
// Decompiled by Procyon v0.5.36
// 

package antlr.actions.python;

import antlr.NoViableAltForCharException;
import antlr.CharStreamException;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.CharStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.Token;
import java.util.Hashtable;
import antlr.LexerSharedInputState;
import antlr.CharBuffer;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import java.io.InputStream;
import antlr.RecognitionException;
import java.io.Reader;
import java.io.StringReader;
import antlr.collections.impl.BitSet;
import antlr.Tool;
import antlr.TokenStream;
import antlr.CharScanner;

public class CodeLexer extends CharScanner implements CodeLexerTokenTypes, TokenStream
{
    protected int lineOffset;
    private Tool antlrTool;
    public static final BitSet _tokenSet_0;
    public static final BitSet _tokenSet_1;
    
    public CodeLexer(final String s, final String filename, final int line, final Tool antlrTool) {
        this(new StringReader(s));
        this.setLine(line);
        this.setFilename(filename);
        this.antlrTool = antlrTool;
    }
    
    public void setLineOffset(final int line) {
        this.setLine(line);
    }
    
    public void reportError(final RecognitionException obj) {
        this.antlrTool.error("Syntax error in action: " + obj, this.getFilename(), this.getLine(), this.getColumn());
    }
    
    public void reportError(final String s) {
        this.antlrTool.error(s, this.getFilename(), this.getLine(), this.getColumn());
    }
    
    public void reportWarning(final String s) {
        if (this.getFilename() == null) {
            this.antlrTool.warning(s);
        }
        else {
            this.antlrTool.warning(s, this.getFilename(), this.getLine(), this.getColumn());
        }
    }
    
    public CodeLexer(final InputStream inputStream) {
        this(new ByteBuffer(inputStream));
    }
    
    public CodeLexer(final Reader reader) {
        this(new CharBuffer(reader));
    }
    
    public CodeLexer(final InputBuffer inputBuffer) {
        this(new LexerSharedInputState(inputBuffer));
    }
    
    public CodeLexer(final LexerSharedInputState lexerSharedInputState) {
        super(lexerSharedInputState);
        this.lineOffset = 0;
        this.setCaseSensitive(this.caseSensitiveLiterals = true);
        this.literals = new Hashtable();
    }
    
    public Token nextToken() throws TokenStreamException {
        while (true) {
            this.resetText();
            try {
                try {
                    this.mACTION(true);
                    final Token returnToken = this._returnToken;
                    if (this._returnToken == null) {
                        continue;
                    }
                    this._returnToken.setType(this._returnToken.getType());
                    return this._returnToken;
                }
                catch (RecognitionException ex) {
                    throw new TokenStreamRecognitionException(ex);
                }
            }
            catch (CharStreamException ex2) {
                if (ex2 instanceof CharStreamIOException) {
                    throw new TokenStreamIOException(((CharStreamIOException)ex2).io);
                }
                throw new TokenStreamException(ex2.getMessage());
            }
        }
    }
    
    public final void mACTION(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 4;
        while (this.LA(1) >= '\u0003' && this.LA(1) <= '\u00ff') {
            this.mSTUFF(false);
        }
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mSTUFF(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 5;
        if (this.LA(1) == '/' && (this.LA(2) == '*' || this.LA(2) == '/')) {
            this.mCOMMENT(false);
        }
        else if (this.LA(1) == '\r' && this.LA(2) == '\n') {
            this.match("\r\n");
            this.newline();
        }
        else if (this.LA(1) == '/' && CodeLexer._tokenSet_0.member(this.LA(2))) {
            this.match('/');
            this.match(CodeLexer._tokenSet_0);
        }
        else if (this.LA(1) == '\r') {
            this.match('\r');
            this.newline();
        }
        else if (this.LA(1) == '\n') {
            this.match('\n');
            this.newline();
        }
        else {
            if (!CodeLexer._tokenSet_1.member(this.LA(1))) {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            this.match(CodeLexer._tokenSet_1);
        }
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mCOMMENT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 6;
        if (this.LA(1) == '/' && this.LA(2) == '/') {
            this.mSL_COMMENT(false);
        }
        else {
            if (this.LA(1) != '/' || this.LA(2) != '*') {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            this.mML_COMMENT(false);
        }
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mSL_COMMENT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 7;
        final int length2 = this.text.length();
        this.match("//");
        this.text.setLength(length2);
        this.text.append("#");
        while (this.LA(1) != '\n') {
            if (this.LA(1) == '\r') {
                break;
            }
            if (this.LA(1) < '\u0003' || this.LA(1) > '\u00ff' || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') {
                break;
            }
            this.matchNot('\uffff');
        }
        if (this.LA(1) == '\r' && this.LA(2) == '\n') {
            this.match("\r\n");
        }
        else if (this.LA(1) == '\n') {
            this.match('\n');
        }
        else {
            if (this.LA(1) != '\r') {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            this.match('\r');
        }
        this.newline();
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mML_COMMENT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 9;
        final int length2 = this.text.length();
        this.match("/*");
        this.text.setLength(length2);
        this.text.append("#");
        while (true) {
            while (this.LA(1) != '*' || this.LA(2) != '/') {
                if (this.LA(1) == '\r' && this.LA(2) == '\n') {
                    this.match('\r');
                    this.match('\n');
                    final int length3 = this.text.length();
                    this.mIGNWS(false);
                    this.text.setLength(length3);
                    this.newline();
                    this.text.append("# ");
                }
                else if (this.LA(1) == '\r' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.match('\r');
                    final int length4 = this.text.length();
                    this.mIGNWS(false);
                    this.text.setLength(length4);
                    this.newline();
                    this.text.append("# ");
                }
                else if (this.LA(1) == '\n' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.match('\n');
                    final int length5 = this.text.length();
                    this.mIGNWS(false);
                    this.text.setLength(length5);
                    this.newline();
                    this.text.append("# ");
                }
                else {
                    if (this.LA(1) < '\u0003' || this.LA(1) > '\u00ff' || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') {
                        this.text.append("\n");
                        final int length6 = this.text.length();
                        this.match("*/");
                        this.text.setLength(length6);
                        if (b && token == null && n != -1) {
                            token = this.makeToken(n);
                            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
                        }
                        this._returnToken = token;
                        return;
                    }
                    this.matchNot('\uffff');
                }
            }
            continue;
        }
    }
    
    protected final void mIGNWS(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 8;
        while (true) {
            if (this.LA(1) == ' ' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                this.match(' ');
            }
            else {
                if (this.LA(1) != '\t' || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') {
                    break;
                }
                this.match('\t');
            }
        }
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    private static final long[] mk_tokenSet_0() {
        final long[] array = new long[8];
        array[0] = -145135534866440L;
        for (int i = 1; i <= 3; ++i) {
            array[i] = -1L;
        }
        return array;
    }
    
    private static final long[] mk_tokenSet_1() {
        final long[] array = new long[8];
        array[0] = -140737488364552L;
        for (int i = 1; i <= 3; ++i) {
            array[i] = -1L;
        }
        return array;
    }
    
    static {
        _tokenSet_0 = new BitSet(mk_tokenSet_0());
        _tokenSet_1 = new BitSet(mk_tokenSet_1());
    }
}
