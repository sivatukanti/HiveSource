// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate.language;

import java.util.List;
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

public class GroupLexer extends CharScanner implements GroupParserTokenTypes, TokenStream
{
    public static final BitSet _tokenSet_0;
    public static final BitSet _tokenSet_1;
    public static final BitSet _tokenSet_2;
    
    public GroupLexer(final InputStream in) {
        this(new ByteBuffer(in));
    }
    
    public GroupLexer(final Reader in) {
        this(new CharBuffer(in));
    }
    
    public GroupLexer(final InputBuffer ib) {
        this(new LexerSharedInputState(ib));
    }
    
    public GroupLexer(final LexerSharedInputState state) {
        super(state);
        this.setCaseSensitive(this.caseSensitiveLiterals = true);
        (this.literals = new Hashtable()).put(new ANTLRHashString("default", this), new Integer(21));
        this.literals.put(new ANTLRHashString("group", this), new Integer(4));
        this.literals.put(new ANTLRHashString("implements", this), new Integer(7));
    }
    
    public Token nextToken() throws TokenStreamException {
        Token theRetToken = null;
        while (true) {
            final Token _token = null;
            int _ttype = 0;
            this.resetText();
            try {
                try {
                    switch (this.LA(1)) {
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
                            this.mID(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case '\"': {
                            this.mSTRING(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case '<': {
                            this.mBIGSTRING(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case '{': {
                            this.mANONYMOUS_TEMPLATE(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case '@': {
                            this.mAT(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case '(': {
                            this.mLPAREN(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case ')': {
                            this.mRPAREN(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case '[': {
                            this.mLBRACK(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case ']': {
                            this.mRBRACK(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case ',': {
                            this.mCOMMA(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case '.': {
                            this.mDOT(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case ';': {
                            this.mSEMI(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case '*': {
                            this.mSTAR(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case '+': {
                            this.mPLUS(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case '=': {
                            this.mASSIGN(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case '?': {
                            this.mOPTIONAL(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case '\t':
                        case '\n':
                        case '\f':
                        case '\r':
                        case ' ': {
                            this.mWS(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        default: {
                            if (this.LA(1) == ':' && this.LA(2) == ':') {
                                this.mDEFINED_TO_BE(true);
                                theRetToken = this._returnToken;
                                break;
                            }
                            if (this.LA(1) == '/' && this.LA(2) == '/') {
                                this.mSL_COMMENT(true);
                                theRetToken = this._returnToken;
                                break;
                            }
                            if (this.LA(1) == '/' && this.LA(2) == '*') {
                                this.mML_COMMENT(true);
                                theRetToken = this._returnToken;
                                break;
                            }
                            if (this.LA(1) == ':') {
                                this.mCOLON(true);
                                theRetToken = this._returnToken;
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
                    _ttype = this._returnToken.getType();
                    this._returnToken.setType(_ttype);
                    return this._returnToken;
                }
                catch (RecognitionException e) {
                    throw new TokenStreamRecognitionException(e);
                }
            }
            catch (CharStreamException cse) {
                if (cse instanceof CharStreamIOException) {
                    throw new TokenStreamIOException(((CharStreamIOException)cse).io);
                }
                throw new TokenStreamException(cse.getMessage());
            }
        }
    }
    
    public final void mID(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        int _ttype = 5;
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
                case '-': {
                    this.match('-');
                    continue;
                }
                case '_': {
                    this.match('_');
                    continue;
                }
                default: {
                    _ttype = this.testLiteralsTable(_ttype);
                    if (_createToken && _token == null && _ttype != -1) {
                        _token = this.makeToken(_ttype);
                        _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
                    }
                    this._returnToken = _token;
                }
            }
        }
    }
    
    public final void mSTRING(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 15;
        int _saveIndex = this.text.length();
        this.match('\"');
        this.text.setLength(_saveIndex);
        while (true) {
            if (this.LA(1) == '\\' && this.LA(2) == '\"') {
                _saveIndex = this.text.length();
                this.match('\\');
                this.text.setLength(_saveIndex);
                this.match('\"');
            }
            else if (this.LA(1) == '\\' && GroupLexer._tokenSet_0.member(this.LA(2))) {
                this.match('\\');
                this.matchNot('\"');
            }
            else {
                if (!GroupLexer._tokenSet_1.member(this.LA(1))) {
                    break;
                }
                this.matchNot('\"');
            }
        }
        _saveIndex = this.text.length();
        this.match('\"');
        this.text.setLength(_saveIndex);
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mBIGSTRING(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 16;
        int _saveIndex = this.text.length();
        this.match("<<");
        this.text.setLength(_saveIndex);
        if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\0' && this.LA(2) <= '\ufffe') {
            switch (this.LA(1)) {
                case '\r': {
                    _saveIndex = this.text.length();
                    this.match('\r');
                    this.text.setLength(_saveIndex);
                    break;
                }
                case '\n': {
                    break;
                }
                default: {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
            _saveIndex = this.text.length();
            this.match('\n');
            this.text.setLength(_saveIndex);
            this.newline();
        }
        else if (this.LA(1) < '\0' || this.LA(1) > '\ufffe' || this.LA(2) < '\0' || this.LA(2) > '\ufffe') {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        while (true) {
            while (this.LA(1) != '>' || this.LA(2) != '>') {
                if (this.LA(1) == '\r' && this.LA(2) == '\n' && this.LA(3) == '>' && this.LA(4) == '>') {
                    _saveIndex = this.text.length();
                    this.match('\r');
                    this.text.setLength(_saveIndex);
                    _saveIndex = this.text.length();
                    this.match('\n');
                    this.text.setLength(_saveIndex);
                    this.newline();
                }
                else if (this.LA(1) == '\n' && this.LA(2) >= '\0' && this.LA(2) <= '\ufffe' && this.LA(2) == '>' && this.LA(3) == '>') {
                    _saveIndex = this.text.length();
                    this.match('\n');
                    this.text.setLength(_saveIndex);
                    this.newline();
                }
                else if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\0' && this.LA(2) <= '\ufffe') {
                    switch (this.LA(1)) {
                        case '\r': {
                            this.match('\r');
                            break;
                        }
                        case '\n': {
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                        }
                    }
                    this.match('\n');
                    this.newline();
                }
                else if (this.LA(1) == '\\' && this.LA(2) == '>') {
                    _saveIndex = this.text.length();
                    this.match('\\');
                    this.text.setLength(_saveIndex);
                    this.match('>');
                }
                else {
                    if (this.LA(1) < '\0' || this.LA(1) > '\ufffe' || this.LA(2) < '\0' || this.LA(2) > '\ufffe') {
                        _saveIndex = this.text.length();
                        this.match(">>");
                        this.text.setLength(_saveIndex);
                        if (_createToken && _token == null && _ttype != -1) {
                            _token = this.makeToken(_ttype);
                            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
                        }
                        this._returnToken = _token;
                        return;
                    }
                    this.matchNot('\uffff');
                }
            }
            continue;
        }
    }
    
    public final void mANONYMOUS_TEMPLATE(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 18;
        final List args = null;
        final StringTemplateToken t = null;
        int _saveIndex = this.text.length();
        this.match('{');
        this.text.setLength(_saveIndex);
        while (true) {
            while (this.LA(1) != '}') {
                if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\0' && this.LA(2) <= '\ufffe') {
                    switch (this.LA(1)) {
                        case '\r': {
                            this.match('\r');
                            break;
                        }
                        case '\n': {
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                        }
                    }
                    this.match('\n');
                    this.newline();
                }
                else if (this.LA(1) == '\\' && this.LA(2) == '}') {
                    _saveIndex = this.text.length();
                    this.match('\\');
                    this.text.setLength(_saveIndex);
                    this.match('}');
                }
                else {
                    if (this.LA(1) < '\0' || this.LA(1) > '\ufffe' || this.LA(2) < '\0' || this.LA(2) > '\ufffe') {
                        _saveIndex = this.text.length();
                        this.match('}');
                        this.text.setLength(_saveIndex);
                        if (_createToken && _token == null && _ttype != -1) {
                            _token = this.makeToken(_ttype);
                            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
                        }
                        this._returnToken = _token;
                        return;
                    }
                    this.matchNot('\uffff');
                }
            }
            continue;
        }
    }
    
    public final void mAT(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 10;
        this.match('@');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mLPAREN(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 12;
        this.match('(');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mRPAREN(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 13;
        this.match(')');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mLBRACK(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 19;
        this.match('[');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mRBRACK(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 20;
        this.match(']');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mCOMMA(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 8;
        this.match(',');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mDOT(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 11;
        this.match('.');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mDEFINED_TO_BE(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 14;
        this.match("::=");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mSEMI(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 9;
        this.match(';');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mCOLON(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 6;
        this.match(':');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mSTAR(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 22;
        this.match('*');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mPLUS(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 23;
        this.match('+');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mASSIGN(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 17;
        this.match('=');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mOPTIONAL(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 24;
        this.match('?');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mSL_COMMENT(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        int _ttype = 25;
        this.match("//");
        while (GroupLexer._tokenSet_2.member(this.LA(1))) {
            this.match(GroupLexer._tokenSet_2);
        }
        if (this.LA(1) == '\n' || this.LA(1) == '\r') {
            switch (this.LA(1)) {
                case '\r': {
                    this.match('\r');
                    break;
                }
                case '\n': {
                    break;
                }
                default: {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
            this.match('\n');
        }
        _ttype = -1;
        this.newline();
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mML_COMMENT(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        int _ttype = 26;
        this.match("/*");
        while (true) {
            while (this.LA(1) != '*' || this.LA(2) != '/') {
                if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\0' && this.LA(2) <= '\ufffe') {
                    switch (this.LA(1)) {
                        case '\r': {
                            this.match('\r');
                            break;
                        }
                        case '\n': {
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                        }
                    }
                    this.match('\n');
                    this.newline();
                }
                else {
                    if (this.LA(1) < '\0' || this.LA(1) > '\ufffe' || this.LA(2) < '\0' || this.LA(2) > '\ufffe') {
                        this.match("*/");
                        _ttype = -1;
                        if (_createToken && _token == null && _ttype != -1) {
                            _token = this.makeToken(_ttype);
                            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
                        }
                        this._returnToken = _token;
                        return;
                    }
                    this.matchNot('\uffff');
                }
            }
            continue;
        }
    }
    
    public final void mWS(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        int _ttype = 27;
        int _cnt70 = 0;
        while (true) {
            switch (this.LA(1)) {
                case ' ': {
                    this.match(' ');
                    break;
                }
                case '\t': {
                    this.match('\t');
                    break;
                }
                case '\f': {
                    this.match('\f');
                    break;
                }
                case '\n':
                case '\r': {
                    switch (this.LA(1)) {
                        case '\r': {
                            this.match('\r');
                            break;
                        }
                        case '\n': {
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                        }
                    }
                    this.match('\n');
                    this.newline();
                    break;
                }
                default: {
                    if (_cnt70 >= 1) {
                        _ttype = -1;
                        if (_createToken && _token == null && _ttype != -1) {
                            _token = this.makeToken(_ttype);
                            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
                        }
                        this._returnToken = _token;
                        return;
                    }
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
            ++_cnt70;
        }
    }
    
    private static final long[] mk_tokenSet_0() {
        final long[] data = new long[2048];
        data[0] = -17179869185L;
        for (int i = 1; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }
    
    private static final long[] mk_tokenSet_1() {
        final long[] data = new long[2048];
        data[0] = -17179869185L;
        data[1] = -268435457L;
        for (int i = 2; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }
    
    private static final long[] mk_tokenSet_2() {
        final long[] data = new long[2048];
        data[0] = -9217L;
        for (int i = 1; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }
    
    static {
        _tokenSet_0 = new BitSet(mk_tokenSet_0());
        _tokenSet_1 = new BitSet(mk_tokenSet_1());
        _tokenSet_2 = new BitSet(mk_tokenSet_2());
    }
}
