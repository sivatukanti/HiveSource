// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.util.Hashtable;
import java.io.Reader;
import java.io.InputStream;
import antlr.collections.impl.BitSet;

public class ANTLRTokdefLexer extends CharScanner implements ANTLRTokdefParserTokenTypes, TokenStream
{
    public static final BitSet _tokenSet_0;
    public static final BitSet _tokenSet_1;
    public static final BitSet _tokenSet_2;
    public static final BitSet _tokenSet_3;
    
    public ANTLRTokdefLexer(final InputStream inputStream) {
        this(new ByteBuffer(inputStream));
    }
    
    public ANTLRTokdefLexer(final Reader reader) {
        this(new CharBuffer(reader));
    }
    
    public ANTLRTokdefLexer(final InputBuffer inputBuffer) {
        this(new LexerSharedInputState(inputBuffer));
    }
    
    public ANTLRTokdefLexer(final LexerSharedInputState lexerSharedInputState) {
        super(lexerSharedInputState);
        this.setCaseSensitive(this.caseSensitiveLiterals = true);
        this.literals = new Hashtable();
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
                        case '(': {
                            this.mLPAREN(true);
                            final Token returnToken2 = this._returnToken;
                            break;
                        }
                        case ')': {
                            this.mRPAREN(true);
                            final Token returnToken3 = this._returnToken;
                            break;
                        }
                        case '=': {
                            this.mASSIGN(true);
                            final Token returnToken4 = this._returnToken;
                            break;
                        }
                        case '\"': {
                            this.mSTRING(true);
                            final Token returnToken5 = this._returnToken;
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
                        case 'Z':
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
                            this.mID(true);
                            final Token returnToken6 = this._returnToken;
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
                            final Token returnToken7 = this._returnToken;
                            break;
                        }
                        default: {
                            if (this.LA(1) == '/' && this.LA(2) == '/') {
                                this.mSL_COMMENT(true);
                                final Token returnToken8 = this._returnToken;
                                break;
                            }
                            if (this.LA(1) == '/' && this.LA(2) == '*') {
                                this.mML_COMMENT(true);
                                final Token returnToken9 = this._returnToken;
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
            case '\r': {
                this.match('\r');
                if (this.LA(1) == '\n') {
                    this.match('\n');
                }
                this.newline();
                break;
            }
            case '\n': {
                this.match('\n');
                this.newline();
                break;
            }
            default: {
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
    
    public final void mSL_COMMENT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        this.match("//");
        while (ANTLRTokdefLexer._tokenSet_0.member(this.LA(1))) {
            this.match(ANTLRTokdefLexer._tokenSet_0);
        }
        switch (this.LA(1)) {
            case '\n': {
                this.match('\n');
                break;
            }
            case '\r': {
                this.match('\r');
                if (this.LA(1) == '\n') {
                    this.match('\n');
                    break;
                }
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        final int n = -1;
        this.newline();
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mML_COMMENT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        this.match("/*");
        while (true) {
            if (this.LA(1) == '*' && ANTLRTokdefLexer._tokenSet_1.member(this.LA(2))) {
                this.match('*');
                this.matchNot('/');
            }
            else if (this.LA(1) == '\n') {
                this.match('\n');
                this.newline();
            }
            else {
                if (!ANTLRTokdefLexer._tokenSet_2.member(this.LA(1))) {
                    break;
                }
                this.matchNot('*');
            }
        }
        this.match("*/");
        final int n = -1;
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mLPAREN(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 7;
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
        final int n = 8;
        this.match(')');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mASSIGN(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 6;
        this.match('=');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mSTRING(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 5;
        this.match('\"');
        while (true) {
            if (this.LA(1) == '\\') {
                this.mESC(false);
            }
            else {
                if (!ANTLRTokdefLexer._tokenSet_3.member(this.LA(1))) {
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
    
    protected final void mESC(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 13;
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
                if (this.LA(1) >= '0' && this.LA(1) <= '9' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.mDIGIT(false);
                    if (this.LA(1) >= '0' && this.LA(1) <= '9' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                        this.mDIGIT(false);
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
                if (this.LA(1) >= '0' && this.LA(1) <= '9' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.mDIGIT(false);
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
    
    protected final void mDIGIT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 14;
        this.matchRange('0', '9');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mXDIGIT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 15;
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
    
    public final void mID(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 4;
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
                this.matchRange('A', 'Z');
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
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
                    if (b && token == null && n != -1) {
                        token = this.makeToken(n);
                        token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
                    }
                    this._returnToken = token;
                }
            }
        }
    }
    
    public final void mINT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 9;
        int n2 = 0;
        while (this.LA(1) >= '0' && this.LA(1) <= '9') {
            this.mDIGIT(false);
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
        array[0] = -140737488355336L;
        for (int i = 1; i <= 3; ++i) {
            array[i] = -1L;
        }
        return array;
    }
    
    private static final long[] mk_tokenSet_2() {
        final long[] array = new long[8];
        array[0] = -4398046512136L;
        for (int i = 1; i <= 3; ++i) {
            array[i] = -1L;
        }
        return array;
    }
    
    private static final long[] mk_tokenSet_3() {
        final long[] array = new long[8];
        array[0] = -17179869192L;
        array[1] = -268435457L;
        for (int i = 2; i <= 3; ++i) {
            array[i] = -1L;
        }
        return array;
    }
    
    static {
        _tokenSet_0 = new BitSet(mk_tokenSet_0());
        _tokenSet_1 = new BitSet(mk_tokenSet_1());
        _tokenSet_2 = new BitSet(mk_tokenSet_2());
        _tokenSet_3 = new BitSet(mk_tokenSet_3());
    }
}
