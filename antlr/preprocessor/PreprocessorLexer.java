// 
// Decompiled by Procyon v0.5.36
// 

package antlr.preprocessor;

import antlr.CharStreamException;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.CharStreamIOException;
import antlr.RecognitionException;
import antlr.TokenStreamRecognitionException;
import antlr.NoViableAltForCharException;
import antlr.Token;
import antlr.ANTLRHashString;
import java.util.Hashtable;
import antlr.LexerSharedInputState;
import antlr.CharBuffer;
import java.io.Reader;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import java.io.InputStream;
import antlr.collections.impl.BitSet;
import antlr.TokenStream;
import antlr.CharScanner;

public class PreprocessorLexer extends CharScanner implements PreprocessorTokenTypes, TokenStream
{
    public static final BitSet _tokenSet_0;
    public static final BitSet _tokenSet_1;
    public static final BitSet _tokenSet_2;
    public static final BitSet _tokenSet_3;
    public static final BitSet _tokenSet_4;
    public static final BitSet _tokenSet_5;
    public static final BitSet _tokenSet_6;
    public static final BitSet _tokenSet_7;
    public static final BitSet _tokenSet_8;
    public static final BitSet _tokenSet_9;
    public static final BitSet _tokenSet_10;
    
    public PreprocessorLexer(final InputStream inputStream) {
        this(new ByteBuffer(inputStream));
    }
    
    public PreprocessorLexer(final Reader reader) {
        this(new CharBuffer(reader));
    }
    
    public PreprocessorLexer(final InputBuffer inputBuffer) {
        this(new LexerSharedInputState(inputBuffer));
    }
    
    public PreprocessorLexer(final LexerSharedInputState lexerSharedInputState) {
        super(lexerSharedInputState);
        this.setCaseSensitive(this.caseSensitiveLiterals = true);
        (this.literals = new Hashtable()).put(new ANTLRHashString("public", this), new Integer(18));
        this.literals.put(new ANTLRHashString("class", this), new Integer(8));
        this.literals.put(new ANTLRHashString("throws", this), new Integer(23));
        this.literals.put(new ANTLRHashString("catch", this), new Integer(26));
        this.literals.put(new ANTLRHashString("private", this), new Integer(17));
        this.literals.put(new ANTLRHashString("extends", this), new Integer(10));
        this.literals.put(new ANTLRHashString("protected", this), new Integer(16));
        this.literals.put(new ANTLRHashString("returns", this), new Integer(21));
        this.literals.put(new ANTLRHashString("tokens", this), new Integer(4));
        this.literals.put(new ANTLRHashString("exception", this), new Integer(25));
    }
    
    public Token nextToken() throws TokenStreamException {
        while (true) {
            this.resetText();
            try {
                try {
                    switch (this.LA(1)) {
                        case ':': {
                            this.mRULE_BLOCK(true);
                            final Token returnToken = this._returnToken;
                            break;
                        }
                        case '\t':
                        case '\n':
                        case '\r':
                        case ' ': {
                            this.mWS(true);
                            final Token returnToken2 = this._returnToken;
                            break;
                        }
                        case '/': {
                            this.mCOMMENT(true);
                            final Token returnToken3 = this._returnToken;
                            break;
                        }
                        case '{': {
                            this.mACTION(true);
                            final Token returnToken4 = this._returnToken;
                            break;
                        }
                        case '\"': {
                            this.mSTRING_LITERAL(true);
                            final Token returnToken5 = this._returnToken;
                            break;
                        }
                        case '\'': {
                            this.mCHAR_LITERAL(true);
                            final Token returnToken6 = this._returnToken;
                            break;
                        }
                        case '!': {
                            this.mBANG(true);
                            final Token returnToken7 = this._returnToken;
                            break;
                        }
                        case ';': {
                            this.mSEMI(true);
                            final Token returnToken8 = this._returnToken;
                            break;
                        }
                        case ',': {
                            this.mCOMMA(true);
                            final Token returnToken9 = this._returnToken;
                            break;
                        }
                        case '}': {
                            this.mRCURLY(true);
                            final Token returnToken10 = this._returnToken;
                            break;
                        }
                        case ')': {
                            this.mRPAREN(true);
                            final Token returnToken11 = this._returnToken;
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
                        case '_':
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
                            this.mID_OR_KEYWORD(true);
                            final Token returnToken12 = this._returnToken;
                            break;
                        }
                        case '=': {
                            this.mASSIGN_RHS(true);
                            final Token returnToken13 = this._returnToken;
                            break;
                        }
                        case '[': {
                            this.mARG_ACTION(true);
                            final Token returnToken14 = this._returnToken;
                            break;
                        }
                        default: {
                            if (this.LA(1) == '(' && PreprocessorLexer._tokenSet_0.member(this.LA(2))) {
                                this.mSUBRULE_BLOCK(true);
                                final Token returnToken15 = this._returnToken;
                                break;
                            }
                            if (this.LA(1) == '(') {
                                this.mLPAREN(true);
                                final Token returnToken16 = this._returnToken;
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
                    this._returnToken.setType(this.testLiteralsTable(this._returnToken.getType()));
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
    
    public final void mRULE_BLOCK(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 22;
        this.match(':');
        if (PreprocessorLexer._tokenSet_1.member(this.LA(1)) && PreprocessorLexer._tokenSet_2.member(this.LA(2))) {
            final int length2 = this.text.length();
            this.mWS(false);
            this.text.setLength(length2);
        }
        else if (!PreprocessorLexer._tokenSet_2.member(this.LA(1))) {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        this.mALT(false);
        switch (this.LA(1)) {
            case '\t':
            case '\n':
            case '\r':
            case ' ': {
                final int length3 = this.text.length();
                this.mWS(false);
                this.text.setLength(length3);
                break;
            }
            case ';':
            case '|': {
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        while (this.LA(1) == '|') {
            this.match('|');
            if (PreprocessorLexer._tokenSet_1.member(this.LA(1)) && PreprocessorLexer._tokenSet_2.member(this.LA(2))) {
                final int length4 = this.text.length();
                this.mWS(false);
                this.text.setLength(length4);
            }
            else if (!PreprocessorLexer._tokenSet_2.member(this.LA(1))) {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            this.mALT(false);
            switch (this.LA(1)) {
                case '\t':
                case '\n':
                case '\r':
                case ' ': {
                    final int length5 = this.text.length();
                    this.mWS(false);
                    this.text.setLength(length5);
                    continue;
                }
                case ';':
                case '|': {
                    continue;
                }
                default: {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
        }
        this.match(';');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mWS(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        int n = 0;
        while (true) {
            if (this.LA(1) == ' ') {
                this.match(' ');
            }
            else if (this.LA(1) == '\t') {
                this.match('\t');
            }
            else {
                if (this.LA(1) != '\n' && this.LA(1) != '\r') {
                    break;
                }
                this.mNEWLINE(false);
            }
            ++n;
        }
        if (n >= 1) {
            final int n2 = -1;
            if (b && token == null && n2 != -1) {
                token = this.makeToken(n2);
                token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
            }
            this._returnToken = token;
            return;
        }
        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
    }
    
    protected final void mALT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 27;
        while (PreprocessorLexer._tokenSet_3.member(this.LA(1)) && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
            this.mELEMENT(false);
        }
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mSUBRULE_BLOCK(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 6;
        this.match('(');
        if (PreprocessorLexer._tokenSet_1.member(this.LA(1)) && PreprocessorLexer._tokenSet_0.member(this.LA(2))) {
            this.mWS(false);
        }
        else if (!PreprocessorLexer._tokenSet_0.member(this.LA(1))) {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        this.mALT(false);
        while (PreprocessorLexer._tokenSet_4.member(this.LA(1)) && PreprocessorLexer._tokenSet_0.member(this.LA(2))) {
            switch (this.LA(1)) {
                case '\t':
                case '\n':
                case '\r':
                case ' ': {
                    this.mWS(false);
                    break;
                }
                case '|': {
                    break;
                }
                default: {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
            this.match('|');
            if (PreprocessorLexer._tokenSet_1.member(this.LA(1)) && PreprocessorLexer._tokenSet_0.member(this.LA(2))) {
                this.mWS(false);
            }
            else if (!PreprocessorLexer._tokenSet_0.member(this.LA(1))) {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            this.mALT(false);
        }
        switch (this.LA(1)) {
            case '\t':
            case '\n':
            case '\r':
            case ' ': {
                this.mWS(false);
                break;
            }
            case ')': {
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        this.match(')');
        if (this.LA(1) == '=' && this.LA(2) == '>') {
            this.match("=>");
        }
        else if (this.LA(1) == '*') {
            this.match('*');
        }
        else if (this.LA(1) == '+') {
            this.match('+');
        }
        else if (this.LA(1) == '?') {
            this.match('?');
        }
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mELEMENT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 28;
        switch (this.LA(1)) {
            case '/': {
                this.mCOMMENT(false);
                break;
            }
            case '{': {
                this.mACTION(false);
                break;
            }
            case '\"': {
                this.mSTRING_LITERAL(false);
                break;
            }
            case '\'': {
                this.mCHAR_LITERAL(false);
                break;
            }
            case '(': {
                this.mSUBRULE_BLOCK(false);
                break;
            }
            case '\n':
            case '\r': {
                this.mNEWLINE(false);
                break;
            }
            default: {
                if (PreprocessorLexer._tokenSet_5.member(this.LA(1))) {
                    this.match(PreprocessorLexer._tokenSet_5);
                    break;
                }
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mCOMMENT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        if (this.LA(1) == '/' && this.LA(2) == '/') {
            this.mSL_COMMENT(false);
        }
        else {
            if (this.LA(1) != '/' || this.LA(2) != '*') {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            this.mML_COMMENT(false);
        }
        final int n = -1;
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mACTION(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 7;
        this.match('{');
        while (true) {
            while (this.LA(1) != '}') {
                if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.mNEWLINE(false);
                }
                else if (this.LA(1) == '{' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.mACTION(false);
                }
                else if (this.LA(1) == '\'' && PreprocessorLexer._tokenSet_6.member(this.LA(2))) {
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
    
    public final void mSTRING_LITERAL(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 39;
        this.match('\"');
        while (true) {
            if (this.LA(1) == '\\') {
                this.mESC(false);
            }
            else {
                if (!PreprocessorLexer._tokenSet_7.member(this.LA(1))) {
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
    
    public final void mCHAR_LITERAL(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 38;
        this.match('\'');
        if (this.LA(1) == '\\') {
            this.mESC(false);
        }
        else {
            if (!PreprocessorLexer._tokenSet_8.member(this.LA(1))) {
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
    
    protected final void mNEWLINE(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 34;
        if (this.LA(1) == '\r' && this.LA(2) == '\n') {
            this.match('\r');
            this.match('\n');
            this.newline();
        }
        else if (this.LA(1) == '\r') {
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
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mBANG(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 19;
        this.match('!');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mSEMI(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 11;
        this.match(';');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mCOMMA(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 24;
        this.match(',');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mRCURLY(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 15;
        this.match('}');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mLPAREN(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 29;
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
        final int n = 30;
        this.match(')');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    public final void mID_OR_KEYWORD(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        this.mID(true);
        final Token returnToken = this._returnToken;
        int type = returnToken.getType();
        Label_0616: {
            if (PreprocessorLexer._tokenSet_9.member(this.LA(1)) && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff' && returnToken.getText().equals("header")) {
                if (PreprocessorLexer._tokenSet_1.member(this.LA(1)) && PreprocessorLexer._tokenSet_9.member(this.LA(2))) {
                    this.mWS(false);
                }
                else if (!PreprocessorLexer._tokenSet_9.member(this.LA(1)) || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
                switch (this.LA(1)) {
                    case '\"': {
                        this.mSTRING_LITERAL(false);
                        break;
                    }
                    case '\t':
                    case '\n':
                    case '\r':
                    case ' ':
                    case '/':
                    case '{': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
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
                            this.mACTION(false);
                            type = 5;
                            break Label_0616;
                        }
                    }
                }
            }
            else if (PreprocessorLexer._tokenSet_10.member(this.LA(1)) && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff' && returnToken.getText().equals("tokens")) {
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
                            this.mCURLY_BLOCK_SCARF(false);
                            type = 12;
                            break Label_0616;
                        }
                    }
                }
            }
            else if (PreprocessorLexer._tokenSet_10.member(this.LA(1)) && returnToken.getText().equals("options")) {
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
                            this.match('{');
                            type = 13;
                            break Label_0616;
                        }
                    }
                }
            }
        }
        if (b && token == null && type != -1) {
            token = this.makeToken(type);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mID(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 9;
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
            case '_': {
                this.match('_');
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
                    final int testLiteralsTable = this.testLiteralsTable(new String(this.text.getBuffer(), length, this.text.length() - length), n);
                    if (b && token == null && testLiteralsTable != -1) {
                        token = this.makeToken(testLiteralsTable);
                        token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
                    }
                    this._returnToken = token;
                }
            }
        }
    }
    
    protected final void mCURLY_BLOCK_SCARF(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 32;
        this.match('{');
        while (true) {
            while (this.LA(1) != '}') {
                if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.mNEWLINE(false);
                }
                else if (this.LA(1) == '\"' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.mSTRING_LITERAL(false);
                }
                else if (this.LA(1) == '\'' && PreprocessorLexer._tokenSet_6.member(this.LA(2))) {
                    this.mCHAR_LITERAL(false);
                }
                else if (this.LA(1) == '/' && (this.LA(2) == '*' || this.LA(2) == '/')) {
                    this.mCOMMENT(false);
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
    
    public final void mASSIGN_RHS(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 14;
        final int length2 = this.text.length();
        this.match('=');
        this.text.setLength(length2);
        while (true) {
            while (this.LA(1) != ';') {
                if (this.LA(1) == '\"' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.mSTRING_LITERAL(false);
                }
                else if (this.LA(1) == '\'' && PreprocessorLexer._tokenSet_6.member(this.LA(2))) {
                    this.mCHAR_LITERAL(false);
                }
                else if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.mNEWLINE(false);
                }
                else {
                    if (this.LA(1) < '\u0003' || this.LA(1) > '\u00ff' || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') {
                        this.match(';');
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
    
    protected final void mSL_COMMENT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 36;
        this.match("//");
        while (this.LA(1) != '\n') {
            if (this.LA(1) == '\r') {
                break;
            }
            if (this.LA(1) < '\u0003' || this.LA(1) > '\u00ff' || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') {
                break;
            }
            this.matchNot('\uffff');
        }
        this.mNEWLINE(false);
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mML_COMMENT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 37;
        this.match("/*");
        while (true) {
            while (this.LA(1) != '*' || this.LA(2) != '/') {
                if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.mNEWLINE(false);
                }
                else {
                    if (this.LA(1) < '\u0003' || this.LA(1) > '\u00ff' || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') {
                        this.match("*/");
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
    
    protected final void mESC(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 40;
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
        final int n = 41;
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
        final int n = 42;
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
    
    public final void mARG_ACTION(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 20;
        this.match('[');
        while (true) {
            while (this.LA(1) != ']') {
                if (this.LA(1) == '[' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.mARG_ACTION(false);
                }
                else if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.mNEWLINE(false);
                }
                else if (this.LA(1) == '\'' && PreprocessorLexer._tokenSet_6.member(this.LA(2))) {
                    this.mCHAR_LITERAL(false);
                }
                else if (this.LA(1) == '\"' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
                    this.mSTRING_LITERAL(false);
                }
                else {
                    if (this.LA(1) < '\u0003' || this.LA(1) > '\u00ff' || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') {
                        this.match(']');
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
    
    private static final long[] mk_tokenSet_0() {
        final long[] array = new long[8];
        array[0] = -576460752303423496L;
        for (int i = 1; i <= 3; ++i) {
            array[i] = -1L;
        }
        return array;
    }
    
    private static final long[] mk_tokenSet_1() {
        return new long[] { 4294977024L, 0L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_2() {
        final long[] array = new long[8];
        array[0] = -2199023255560L;
        for (int i = 1; i <= 3; ++i) {
            array[i] = -1L;
        }
        return array;
    }
    
    private static final long[] mk_tokenSet_3() {
        final long[] array = new long[8];
        array[0] = -576462951326679048L;
        for (int i = 1; i <= 3; ++i) {
            array[i] = -1L;
        }
        return array;
    }
    
    private static final long[] mk_tokenSet_4() {
        return new long[] { 4294977024L, 1152921504606846976L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_5() {
        final long[] array = new long[8];
        array[0] = -576605355262354440L;
        array[1] = -576460752303423489L;
        for (int i = 2; i <= 3; ++i) {
            array[i] = -1L;
        }
        return array;
    }
    
    private static final long[] mk_tokenSet_6() {
        final long[] array = new long[8];
        array[0] = -549755813896L;
        for (int i = 1; i <= 3; ++i) {
            array[i] = -1L;
        }
        return array;
    }
    
    private static final long[] mk_tokenSet_7() {
        final long[] array = new long[8];
        array[0] = -17179869192L;
        array[1] = -268435457L;
        for (int i = 2; i <= 3; ++i) {
            array[i] = -1L;
        }
        return array;
    }
    
    private static final long[] mk_tokenSet_8() {
        final long[] array = new long[8];
        array[0] = -549755813896L;
        array[1] = -268435457L;
        for (int i = 2; i <= 3; ++i) {
            array[i] = -1L;
        }
        return array;
    }
    
    private static final long[] mk_tokenSet_9() {
        return new long[] { 140758963201536L, 576460752303423488L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_10() {
        return new long[] { 140741783332352L, 576460752303423488L, 0L, 0L, 0L };
    }
    
    static {
        _tokenSet_0 = new BitSet(mk_tokenSet_0());
        _tokenSet_1 = new BitSet(mk_tokenSet_1());
        _tokenSet_2 = new BitSet(mk_tokenSet_2());
        _tokenSet_3 = new BitSet(mk_tokenSet_3());
        _tokenSet_4 = new BitSet(mk_tokenSet_4());
        _tokenSet_5 = new BitSet(mk_tokenSet_5());
        _tokenSet_6 = new BitSet(mk_tokenSet_6());
        _tokenSet_7 = new BitSet(mk_tokenSet_7());
        _tokenSet_8 = new BitSet(mk_tokenSet_8());
        _tokenSet_9 = new BitSet(mk_tokenSet_9());
        _tokenSet_10 = new BitSet(mk_tokenSet_10());
    }
}
