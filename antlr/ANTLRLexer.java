// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.util.Hashtable;
import java.io.Reader;
import java.io.InputStream;
import antlr.collections.impl.BitSet;

public class ANTLRLexer extends CharScanner implements ANTLRTokenTypes, TokenStream
{
    public static final BitSet _tokenSet_0;
    public static final BitSet _tokenSet_1;
    public static final BitSet _tokenSet_2;
    public static final BitSet _tokenSet_3;
    public static final BitSet _tokenSet_4;
    public static final BitSet _tokenSet_5;
    
    public static int escapeCharValue(final String s) {
        if (s.charAt(1) != '\\') {
            return 0;
        }
        switch (s.charAt(2)) {
            case 'b': {
                return 8;
            }
            case 'r': {
                return 13;
            }
            case 't': {
                return 9;
            }
            case 'n': {
                return 10;
            }
            case 'f': {
                return 12;
            }
            case '\"': {
                return 34;
            }
            case '\'': {
                return 39;
            }
            case '\\': {
                return 92;
            }
            case 'u': {
                if (s.length() != 8) {
                    return 0;
                }
                return Character.digit(s.charAt(3), 16) * 16 * 16 * 16 + Character.digit(s.charAt(4), 16) * 16 * 16 + Character.digit(s.charAt(5), 16) * 16 + Character.digit(s.charAt(6), 16);
            }
            case '0':
            case '1':
            case '2':
            case '3': {
                if (s.length() > 5 && Character.isDigit(s.charAt(4))) {
                    return (s.charAt(2) - '0') * 8 * 8 + (s.charAt(3) - '0') * 8 + (s.charAt(4) - '0');
                }
                if (s.length() > 4 && Character.isDigit(s.charAt(3))) {
                    return (s.charAt(2) - '0') * 8 + (s.charAt(3) - '0');
                }
                return s.charAt(2) - '0';
            }
            case '4':
            case '5':
            case '6':
            case '7': {
                if (s.length() > 4 && Character.isDigit(s.charAt(3))) {
                    return (s.charAt(2) - '0') * 8 + (s.charAt(3) - '0');
                }
                return s.charAt(2) - '0';
            }
            default: {
                return 0;
            }
        }
    }
    
    public static int tokenTypeForCharLiteral(final String s) {
        if (s.length() > 3) {
            return escapeCharValue(s);
        }
        return s.charAt(1);
    }
    
    public ANTLRLexer(final InputStream inputStream) {
        this(new ByteBuffer(inputStream));
    }
    
    public ANTLRLexer(final Reader reader) {
        this(new CharBuffer(reader));
    }
    
    public ANTLRLexer(final InputBuffer inputBuffer) {
        this(new LexerSharedInputState(inputBuffer));
    }
    
    public ANTLRLexer(final LexerSharedInputState lexerSharedInputState) {
        super(lexerSharedInputState);
        this.setCaseSensitive(this.caseSensitiveLiterals = true);
        (this.literals = new Hashtable()).put(new ANTLRHashString("public", this), new Integer(31));
        this.literals.put(new ANTLRHashString("class", this), new Integer(10));
        this.literals.put(new ANTLRHashString("header", this), new Integer(5));
        this.literals.put(new ANTLRHashString("throws", this), new Integer(37));
        this.literals.put(new ANTLRHashString("lexclass", this), new Integer(9));
        this.literals.put(new ANTLRHashString("catch", this), new Integer(40));
        this.literals.put(new ANTLRHashString("private", this), new Integer(32));
        this.literals.put(new ANTLRHashString("options", this), new Integer(51));
        this.literals.put(new ANTLRHashString("extends", this), new Integer(11));
        this.literals.put(new ANTLRHashString("protected", this), new Integer(30));
        this.literals.put(new ANTLRHashString("TreeParser", this), new Integer(13));
        this.literals.put(new ANTLRHashString("Parser", this), new Integer(29));
        this.literals.put(new ANTLRHashString("Lexer", this), new Integer(12));
        this.literals.put(new ANTLRHashString("returns", this), new Integer(35));
        this.literals.put(new ANTLRHashString("charVocabulary", this), new Integer(18));
        this.literals.put(new ANTLRHashString("tokens", this), new Integer(4));
        this.literals.put(new ANTLRHashString("exception", this), new Integer(39));
    }
    
    public Token nextToken() throws TokenStreamException {
        while (true) {
            this.resetText();
            try {
                try {
                    switch (this.LA(1)) {
                        case '\t':
                        case '\n':
                        case '\r':
                        case ' ': {
                            this.mWS(true);
                            final Token returnToken = this._returnToken;
                            break;
                        }
                        case '/': {
                            this.mCOMMENT(true);
                            final Token returnToken2 = this._returnToken;
                            break;
                        }
                        case '<': {
                            this.mOPEN_ELEMENT_OPTION(true);
                            final Token returnToken3 = this._returnToken;
                            break;
                        }
                        case '>': {
                            this.mCLOSE_ELEMENT_OPTION(true);
                            final Token returnToken4 = this._returnToken;
                            break;
                        }
                        case ',': {
                            this.mCOMMA(true);
                            final Token returnToken5 = this._returnToken;
                            break;
                        }
                        case '?': {
                            this.mQUESTION(true);
                            final Token returnToken6 = this._returnToken;
                            break;
                        }
                        case '#': {
                            this.mTREE_BEGIN(true);
                            final Token returnToken7 = this._returnToken;
                            break;
                        }
                        case '(': {
                            this.mLPAREN(true);
                            final Token returnToken8 = this._returnToken;
                            break;
                        }
                        case ')': {
                            this.mRPAREN(true);
                            final Token returnToken9 = this._returnToken;
                            break;
                        }
                        case ':': {
                            this.mCOLON(true);
                            final Token returnToken10 = this._returnToken;
                            break;
                        }
                        case '*': {
                            this.mSTAR(true);
                            final Token returnToken11 = this._returnToken;
                            break;
                        }
                        case '+': {
                            this.mPLUS(true);
                            final Token returnToken12 = this._returnToken;
                            break;
                        }
                        case ';': {
                            this.mSEMI(true);
                            final Token returnToken13 = this._returnToken;
                            break;
                        }
                        case '^': {
                            this.mCARET(true);
                            final Token returnToken14 = this._returnToken;
                            break;
                        }
                        case '!': {
                            this.mBANG(true);
                            final Token returnToken15 = this._returnToken;
                            break;
                        }
                        case '|': {
                            this.mOR(true);
                            final Token returnToken16 = this._returnToken;
                            break;
                        }
                        case '~': {
                            this.mNOT_OP(true);
                            final Token returnToken17 = this._returnToken;
                            break;
                        }
                        case '}': {
                            this.mRCURLY(true);
                            final Token returnToken18 = this._returnToken;
                            break;
                        }
                        case '\'': {
                            this.mCHAR_LITERAL(true);
                            final Token returnToken19 = this._returnToken;
                            break;
                        }
                        case '\"': {
                            this.mSTRING_LITERAL(true);
                            final Token returnToken20 = this._returnToken;
                            break;
                        }
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9': {
                            this.mINT(true);
                            final Token returnToken21 = this._returnToken;
                            break;
                        }
                        case '[': {
                            this.mARG_ACTION(true);
                            final Token returnToken22 = this._returnToken;
                            break;
                        }
                        case '{': {
                            this.mACTION(true);
                            final Token returnToken23 = this._returnToken;
                            break;
                        }
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                        case 'G':
                        case 'H':
                        case 'I':
                        case 'J':
                        case 'K':
                        case 'L':
                        case 'M':
                        case 'N':
                        case 'O':
                        case 'P':
                        case 'Q':
                        case 'R':
                        case 'S':
                        case 'T':
                        case 'U':
                        case 'V':
                        case 'W':
                        case 'X':
                        case 'Y':
                        case 'Z': {
                            this.mTOKEN_REF(true);
                            final Token returnToken24 = this._returnToken;
                            break;
                        }
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                        case 'g':
                        case 'h':
                        case 'i':
                        case 'j':
                        case 'k':
                        case 'l':
                        case 'm':
                        case 'n':
                        case 'o':
                        case 'p':
                        case 'q':
                        case 'r':
                        case 's':
                        case 't':
                        case 'u':
                        case 'v':
                        case 'w':
                        case 'x':
                        case 'y':
                        case 'z': {
                            this.mRULE_REF(true);
                            final Token returnToken25 = this._returnToken;
                            break;
                        }
                        default: {
                            if (this.LA(1) == '=' && this.LA(2) == '>') {
                                this.mIMPLIES(true);
                                final Token returnToken26 = this._returnToken;
                                break;
                            }
                            if (this.LA(1) == '.' && this.LA(2) == '.') {
                                this.mRANGE(true);
                                final Token returnToken27 = this._returnToken;
                                break;
                            }
                            if (this.LA(1) == '=') {
                                this.mASSIGN(true);
                                final Token returnToken28 = this._returnToken;
                                break;
                            }
                            if (this.LA(1) == '.') {
                                this.mWILDCARD(true);
                                final Token returnToken29 = this._returnToken;
                                break;
                            }
                            if (this.LA(1) == '\uffff') {
                                this.uponEOF();
                                this._returnToken = this.makeToken(1);
                                break;
                            }
                            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                        }
                    }
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
    
    public final void mWS(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        switch (this.LA(1)) {
            case ' ': {
                this.match(' ');
                break;
            }
            case '\t': {
                this.match('\t');
                break;
            }
            case '\n': {
                this.match('\n');
                this.newline();
                break;
            }
            default: {
                if (this.LA(1) == '\r' && this.LA(2) == '\n') {
                    this.match('\r');
                    this.match('\n');
                    this.newline();
                    break;
                }
                if (this.LA(1) == '\r') {
                    this.match('\r');
                    this.newline();
                    break;
                }
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        final int n = -1;
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mCOMMENT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        int type = 53;
        if (this.LA(1) == '/' && this.LA(2) == '/') {
            this.mSL_COMMENT(false);
        }
        else {
            if (this.LA(1) != '/' || this.LA(2) != '*') {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            this.mML_COMMENT(true);
            type = this._returnToken.getType();
        }
        if (type != 8) {
            type = -1;
        }
        if (b && token == null && type != -1) {
            token = this.makeToken(type);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mSL_COMMENT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 54;
        this.match("//");
        while (ANTLRLexer._tokenSet_0.member(this.LA(1))) {
            this.match(ANTLRLexer._tokenSet_0);
        }
        if (this.LA(1) == '\r' && this.LA(2) == '\n') {
            this.match('\r');
            this.match('\n');
        }
        else if (this.LA(1) == '\r') {
            this.match('\r');
        }
        else {
            if (this.LA(1) != '\n') {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            this.match('\n');
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
        int n = 55;
        this.match("/*");
        if (this.LA(1) == '*' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff' && this.LA(2) != '/') {
            this.match('*');
            n = 8;
        }
        else if (this.LA(1) < '\u0003' || this.LA(1) > '\u00ff' || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        while (true) {
            while (this.LA(1) != '*' || this.LA(2) != '/') {
                if (this.LA(1) == '\r' && this.LA(2) == '\n') {
                    this.match('\r');
                    this.match('\n');
                    this.newline();
                }
                else if (this.LA(1) == '\r' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.match('\r');
                    this.newline();
                }
                else if (ANTLRLexer._tokenSet_0.member(this.LA(1)) && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.match(ANTLRLexer._tokenSet_0);
                }
                else {
                    if (this.LA(1) != '\n') {
                        this.match("*/");
                        if (b && token == null && n != -1) {
                            token = this.makeToken(n);
                            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
                        }
                        this._returnToken = token;
                        return;
                    }
                    this.match('\n');
                    this.newline();
                }
            }
            continue;
        }
    }
    
    public final void mOPEN_ELEMENT_OPTION(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 25;
        this.match('<');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mCLOSE_ELEMENT_OPTION(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 26;
        this.match('>');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mCOMMA(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 38;
        this.match(',');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mQUESTION(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 45;
        this.match('?');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mTREE_BEGIN(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 44;
        this.match("#(");
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mLPAREN(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 27;
        this.match('(');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mRPAREN(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 28;
        this.match(')');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mCOLON(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 36;
        this.match(':');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mSTAR(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 46;
        this.match('*');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mPLUS(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 47;
        this.match('+');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mASSIGN(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 15;
        this.match('=');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mIMPLIES(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 48;
        this.match("=>");
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mSEMI(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 16;
        this.match(';');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mCARET(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 49;
        this.match('^');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mBANG(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 33;
        this.match('!');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mOR(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 21;
        this.match('|');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mWILDCARD(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 50;
        this.match('.');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mRANGE(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 22;
        this.match("..");
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mNOT_OP(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 42;
        this.match('~');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mRCURLY(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 17;
        this.match('}');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mCHAR_LITERAL(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 19;
        this.match('\'');
        if (this.LA(1) == '\\') {
            this.mESC(false);
        }
        else {
            if (!ANTLRLexer._tokenSet_1.member(this.LA(1))) {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            this.matchNot('\'');
        }
        this.match('\'');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mESC(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 56;
        this.match('\\');
        switch (this.LA(1)) {
            case 'n': {
                this.match('n');
                break;
            }
            case 'r': {
                this.match('r');
                break;
            }
            case 't': {
                this.match('t');
                break;
            }
            case 'b': {
                this.match('b');
                break;
            }
            case 'f': {
                this.match('f');
                break;
            }
            case 'w': {
                this.match('w');
                break;
            }
            case 'a': {
                this.match('a');
                break;
            }
            case '\"': {
                this.match('\"');
                break;
            }
            case '\'': {
                this.match('\'');
                break;
            }
            case '\\': {
                this.match('\\');
                break;
            }
            case '0':
            case '1':
            case '2':
            case '3': {
                this.matchRange('0', '3');
                if (this.LA(1) >= '0' && this.LA(1) <= '7' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.matchRange('0', '7');
                    if (this.LA(1) >= '0' && this.LA(1) <= '7' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                        this.matchRange('0', '7');
                        break;
                    }
                    if (this.LA(1) >= '\u0003' && this.LA(1) <= '\u00ff') {
                        break;
                    }
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
                else {
                    if (this.LA(1) >= '\u0003' && this.LA(1) <= '\u00ff') {
                        break;
                    }
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
                break;
            }
            case '4':
            case '5':
            case '6':
            case '7': {
                this.matchRange('4', '7');
                if (this.LA(1) >= '0' && this.LA(1) <= '7' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.matchRange('0', '7');
                    break;
                }
                if (this.LA(1) >= '\u0003' && this.LA(1) <= '\u00ff') {
                    break;
                }
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            case 'u': {
                this.match('u');
                this.mXDIGIT(false);
                this.mXDIGIT(false);
                this.mXDIGIT(false);
                this.mXDIGIT(false);
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mSTRING_LITERAL(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 6;
        this.match('\"');
        while (true) {
            if (this.LA(1) == '\\') {
                this.mESC(false);
            }
            else {
                if (!ANTLRLexer._tokenSet_2.member(this.LA(1))) {
                    break;
                }
                this.matchNot('\"');
            }
        }
        this.match('\"');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mXDIGIT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 58;
        switch (this.LA(1)) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': {
                this.matchRange('0', '9');
                break;
            }
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f': {
                this.matchRange('a', 'f');
                break;
            }
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F': {
                this.matchRange('A', 'F');
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mDIGIT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 57;
        this.matchRange('0', '9');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mINT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 20;
        int n2 = 0;
        while (this.LA(1) >= '0' && this.LA(1) <= '9') {
            this.matchRange('0', '9');
            ++n2;
        }
        if (n2 >= 1) {
            if (b && token == null && n != -1) {
                token = this.makeToken(n);
                token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
            }
            this._returnToken = token;
            return;
        }
        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
    }
    
    public final void mARG_ACTION(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 34;
        this.mNESTED_ARG_ACTION(false);
        this.setText(StringUtils.stripFrontBack(this.getText(), "[", "]"));
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mNESTED_ARG_ACTION(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 59;
        this.match('[');
    Label_0210:
        while (true) {
            switch (this.LA(1)) {
                case '[': {
                    this.mNESTED_ARG_ACTION(false);
                    continue;
                }
                case '\n': {
                    this.match('\n');
                    this.newline();
                    continue;
                }
                case '\'': {
                    this.mCHAR_LITERAL(false);
                    continue;
                }
                case '\"': {
                    this.mSTRING_LITERAL(false);
                    continue;
                }
                default: {
                    if (this.LA(1) == '\r' && this.LA(2) == '\n') {
                        this.match('\r');
                        this.match('\n');
                        this.newline();
                        continue;
                    }
                    if (this.LA(1) == '\r' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                        this.match('\r');
                        this.newline();
                        continue;
                    }
                    if (ANTLRLexer._tokenSet_3.member(this.LA(1))) {
                        this.matchNot(']');
                        continue;
                    }
                    break Label_0210;
                }
            }
        }
        this.match(']');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mACTION(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        final int length = this.text.length();
        int n = 7;
        final int line = this.getLine();
        final int column = this.getColumn();
        this.mNESTED_ACTION(false);
        if (this.LA(1) == '?') {
            this.match('?');
            n = 43;
        }
        if (n == 7) {
            this.setText(StringUtils.stripFrontBack(this.getText(), "{", "}"));
        }
        else {
            this.setText(StringUtils.stripFrontBack(this.getText(), "{", "}?"));
        }
        final CommonToken commonToken = new CommonToken(n, new String(this.text.getBuffer(), length, this.text.length() - length));
        commonToken.setLine(line);
        commonToken.setColumn(column);
        Token token = commonToken;
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mNESTED_ACTION(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 60;
        this.match('{');
        while (true) {
            while (this.LA(1) != '}') {
                if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    if (this.LA(1) == '\r' && this.LA(2) == '\n') {
                        this.match('\r');
                        this.match('\n');
                        this.newline();
                    }
                    else if (this.LA(1) == '\r' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                        this.match('\r');
                        this.newline();
                    }
                    else {
                        if (this.LA(1) != '\n') {
                            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                        }
                        this.match('\n');
                        this.newline();
                    }
                }
                else if (this.LA(1) == '{' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.mNESTED_ACTION(false);
                }
                else if (this.LA(1) == '\'' && ANTLRLexer._tokenSet_4.member(this.LA(2))) {
                    this.mCHAR_LITERAL(false);
                }
                else if (this.LA(1) == '/' && (this.LA(2) == '*' || this.LA(2) == '/')) {
                    this.mCOMMENT(false);
                }
                else if (this.LA(1) == '\"' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.mSTRING_LITERAL(false);
                }
                else {
                    if (this.LA(1) < '\u0003' || this.LA(1) > '\u00ff' || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') {
                        this.match('}');
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
    
    public final void mTOKEN_REF(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 24;
        this.matchRange('A', 'Z');
        while (true) {
            switch (this.LA(1)) {
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z': {
                    this.matchRange('a', 'z');
                    continue;
                }
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z': {
                    this.matchRange('A', 'Z');
                    continue;
                }
                case '_': {
                    this.match('_');
                    continue;
                }
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                    this.matchRange('0', '9');
                    continue;
                }
                default: {
                    final int testLiteralsTable = this.testLiteralsTable(n);
                    if (b && token == null && testLiteralsTable != -1) {
                        token = this.makeToken(testLiteralsTable);
                        token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
                    }
                    this._returnToken = token;
                }
            }
        }
    }
    
    public final void mRULE_REF(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        int minternal_RULE_REF;
        final int n = minternal_RULE_REF = this.mINTERNAL_RULE_REF((boolean)(0 != 0));
        if (n == 51) {
            this.mWS_LOOP(false);
            if (this.LA(1) == '{') {
                this.match('{');
                minternal_RULE_REF = 14;
            }
        }
        else if (n == 4) {
            this.mWS_LOOP(false);
            if (this.LA(1) == '{') {
                this.match('{');
                minternal_RULE_REF = 23;
            }
        }
        if (b && token == null && minternal_RULE_REF != -1) {
            token = this.makeToken(minternal_RULE_REF);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final int mINTERNAL_RULE_REF(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 62;
        final int n2 = 41;
        this.matchRange('a', 'z');
        while (true) {
            switch (this.LA(1)) {
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z': {
                    this.matchRange('a', 'z');
                    continue;
                }
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z': {
                    this.matchRange('A', 'Z');
                    continue;
                }
                case '_': {
                    this.match('_');
                    continue;
                }
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                    this.matchRange('0', '9');
                    continue;
                }
                default: {
                    final int testLiteralsTable = this.testLiteralsTable(n2);
                    if (b && token == null && n != -1) {
                        token = this.makeToken(n);
                        token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
                    }
                    this._returnToken = token;
                    return testLiteralsTable;
                }
            }
        }
    }
    
    protected final void mWS_LOOP(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 61;
        while (true) {
            switch (this.LA(1)) {
                case '\t':
                case '\n':
                case '\r':
                case ' ': {
                    this.mWS(false);
                    continue;
                }
                case '/': {
                    this.mCOMMENT(false);
                    continue;
                }
                default: {
                    if (b && token == null && n != -1) {
                        token = this.makeToken(n);
                        token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
                    }
                    this._returnToken = token;
                }
            }
        }
    }
    
    protected final void mWS_OPT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 63;
        if (ANTLRLexer._tokenSet_5.member(this.LA(1))) {
            this.mWS(false);
        }
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    private static final long[] mk_tokenSet_0() {
        final long[] array = new long[8];
        array[0] = -9224L;
        for (int i = 1; i <= 3; ++i) {
            array[i] = -1L;
        }
        return array;
    }
    
    private static final long[] mk_tokenSet_1() {
        final long[] array = new long[8];
        array[0] = -549755813896L;
        array[1] = -268435457L;
        for (int i = 2; i <= 3; ++i) {
            array[i] = -1L;
        }
        return array;
    }
    
    private static final long[] mk_tokenSet_2() {
        final long[] array = new long[8];
        array[0] = -17179869192L;
        array[1] = -268435457L;
        for (int i = 2; i <= 3; ++i) {
            array[i] = -1L;
        }
        return array;
    }
    
    private static final long[] mk_tokenSet_3() {
        final long[] array = new long[8];
        array[0] = -566935692296L;
        array[1] = -671088641L;
        for (int i = 2; i <= 3; ++i) {
            array[i] = -1L;
        }
        return array;
    }
    
    private static final long[] mk_tokenSet_4() {
        final long[] array = new long[8];
        array[0] = -549755813896L;
        for (int i = 1; i <= 3; ++i) {
            array[i] = -1L;
        }
        return array;
    }
    
    private static final long[] mk_tokenSet_5() {
        return new long[] { 4294977024L, 0L, 0L, 0L, 0L };
    }
    
    static {
        _tokenSet_0 = new BitSet(mk_tokenSet_0());
        _tokenSet_1 = new BitSet(mk_tokenSet_1());
        _tokenSet_2 = new BitSet(mk_tokenSet_2());
        _tokenSet_3 = new BitSet(mk_tokenSet_3());
        _tokenSet_4 = new BitSet(mk_tokenSet_4());
        _tokenSet_5 = new BitSet(mk_tokenSet_5());
    }
}
