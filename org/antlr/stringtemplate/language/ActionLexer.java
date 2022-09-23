// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate.language;

import java.util.ArrayList;
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

public class ActionLexer extends CharScanner implements ActionParserTokenTypes, TokenStream
{
    public static final BitSet _tokenSet_0;
    public static final BitSet _tokenSet_1;
    public static final BitSet _tokenSet_2;
    public static final BitSet _tokenSet_3;
    public static final BitSet _tokenSet_4;
    public static final BitSet _tokenSet_5;
    public static final BitSet _tokenSet_6;
    
    public ActionLexer(final InputStream in) {
        this(new ByteBuffer(in));
    }
    
    public ActionLexer(final Reader in) {
        this(new CharBuffer(in));
    }
    
    public ActionLexer(final InputBuffer ib) {
        this(new LexerSharedInputState(ib));
    }
    
    public ActionLexer(final LexerSharedInputState state) {
        super(state);
        this.setCaseSensitive(this.caseSensitiveLiterals = true);
        (this.literals = new Hashtable()).put(new ANTLRHashString("super", this), new Integer(32));
        this.literals.put(new ANTLRHashString("if", this), new Integer(8));
        this.literals.put(new ANTLRHashString("first", this), new Integer(26));
        this.literals.put(new ANTLRHashString("last", this), new Integer(28));
        this.literals.put(new ANTLRHashString("rest", this), new Integer(27));
        this.literals.put(new ANTLRHashString("trunc", this), new Integer(31));
        this.literals.put(new ANTLRHashString("strip", this), new Integer(30));
        this.literals.put(new ANTLRHashString("length", this), new Integer(29));
        this.literals.put(new ANTLRHashString("elseif", this), new Integer(18));
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
                            theRetToken = this._returnToken;
                            break;
                        }
                        case '\"': {
                            this.mSTRING(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case '{': {
                            this.mANONYMOUS_TEMPLATE(true);
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
                        case ',': {
                            this.mCOMMA(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case '=': {
                            this.mASSIGN(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case ':': {
                            this.mCOLON(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case '+': {
                            this.mPLUS(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case ';': {
                            this.mSEMI(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case '!': {
                            this.mNOT(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case '\t':
                        case '\n':
                        case '\r':
                        case ' ': {
                            this.mWS(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        default: {
                            if (this.LA(1) == '.' && this.LA(2) == '.') {
                                this.mDOTDOTDOT(true);
                                theRetToken = this._returnToken;
                                break;
                            }
                            if (this.LA(1) == '.') {
                                this.mDOT(true);
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
        int _ttype = 20;
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
                case '_': {
                    this.match('_');
                    continue;
                }
                case '/': {
                    this.match('/');
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
    
    public final void mINT(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 35;
        int _cnt63 = 0;
        while (this.LA(1) >= '0' && this.LA(1) <= '9') {
            this.matchRange('0', '9');
            ++_cnt63;
        }
        if (_cnt63 >= 1) {
            if (_createToken && _token == null && _ttype != -1) {
                _token = this.makeToken(_ttype);
                _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
            }
            this._returnToken = _token;
            return;
        }
        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
    }
    
    public final void mSTRING(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 34;
        int _saveIndex = this.text.length();
        this.match('\"');
        this.text.setLength(_saveIndex);
        while (true) {
            if (this.LA(1) == '\\') {
                this.mESC_CHAR(false, true);
            }
            else {
                if (!ActionLexer._tokenSet_0.member(this.LA(1))) {
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
    
    protected final void mESC_CHAR(final boolean _createToken, final boolean doEscape) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 41;
        char c = '\0';
        this.match('\\');
        if (this.LA(1) == 'n' && this.LA(2) >= '\u0003' && this.LA(2) <= '\ufffe') {
            this.match('n');
            if (this.inputState.guessing == 0 && doEscape) {
                this.text.setLength(_begin);
                this.text.append("\n");
            }
        }
        else if (this.LA(1) == 'r' && this.LA(2) >= '\u0003' && this.LA(2) <= '\ufffe') {
            this.match('r');
            if (this.inputState.guessing == 0 && doEscape) {
                this.text.setLength(_begin);
                this.text.append("\r");
            }
        }
        else if (this.LA(1) == 't' && this.LA(2) >= '\u0003' && this.LA(2) <= '\ufffe') {
            this.match('t');
            if (this.inputState.guessing == 0 && doEscape) {
                this.text.setLength(_begin);
                this.text.append("\t");
            }
        }
        else if (this.LA(1) == 'b' && this.LA(2) >= '\u0003' && this.LA(2) <= '\ufffe') {
            this.match('b');
            if (this.inputState.guessing == 0 && doEscape) {
                this.text.setLength(_begin);
                this.text.append("\b");
            }
        }
        else if (this.LA(1) == 'f' && this.LA(2) >= '\u0003' && this.LA(2) <= '\ufffe') {
            this.match('f');
            if (this.inputState.guessing == 0 && doEscape) {
                this.text.setLength(_begin);
                this.text.append("\f");
            }
        }
        else {
            if (this.LA(1) < '\u0003' || this.LA(1) > '\ufffe' || this.LA(2) < '\u0003' || this.LA(2) > '\ufffe') {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            c = this.LA(1);
            this.matchNot('\uffff');
            if (this.inputState.guessing == 0 && doEscape) {
                this.text.setLength(_begin);
                this.text.append(String.valueOf(c));
            }
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mANONYMOUS_TEMPLATE(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 33;
        List args = null;
        StringTemplateToken t = null;
        int _saveIndex = this.text.length();
        this.match('{');
        this.text.setLength(_saveIndex);
        boolean synPredMatched70 = false;
        if (ActionLexer._tokenSet_1.member(this.LA(1)) && ActionLexer._tokenSet_2.member(this.LA(2))) {
            final int _m70 = this.mark();
            synPredMatched70 = true;
            final LexerSharedInputState inputState = this.inputState;
            ++inputState.guessing;
            try {
                this.mTEMPLATE_ARGS(false);
            }
            catch (RecognitionException pe) {
                synPredMatched70 = false;
            }
            this.rewind(_m70);
            final LexerSharedInputState inputState2 = this.inputState;
            --inputState2.guessing;
        }
        if (synPredMatched70) {
            args = this.mTEMPLATE_ARGS(false);
            if (ActionLexer._tokenSet_3.member(this.LA(1)) && this.LA(2) >= '\u0003' && this.LA(2) <= '\ufffe') {
                _saveIndex = this.text.length();
                this.mWS_CHAR(false);
                this.text.setLength(_saveIndex);
            }
            else if (this.LA(1) < '\u0003' || this.LA(1) > '\ufffe') {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            if (this.inputState.guessing == 0) {
                t = (StringTemplateToken)(_token = new StringTemplateToken(33, new String(this.text.getBuffer(), _begin, this.text.length() - _begin), args));
            }
        }
        else if (this.LA(1) < '\u0003' || this.LA(1) > '\ufffe') {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        while (true) {
            if (this.LA(1) == '\\' && this.LA(2) == '{') {
                _saveIndex = this.text.length();
                this.match('\\');
                this.text.setLength(_saveIndex);
                this.match('{');
            }
            else if (this.LA(1) == '\\' && this.LA(2) == '}') {
                _saveIndex = this.text.length();
                this.match('\\');
                this.text.setLength(_saveIndex);
                this.match('}');
            }
            else if (this.LA(1) == '\\' && this.LA(2) >= '\u0003' && this.LA(2) <= '\ufffe') {
                this.mESC_CHAR(false, false);
            }
            else if (this.LA(1) == '{') {
                this.mNESTED_ANONYMOUS_TEMPLATE(false);
            }
            else {
                if (!ActionLexer._tokenSet_4.member(this.LA(1))) {
                    break;
                }
                this.matchNot('}');
            }
        }
        if (this.inputState.guessing == 0 && t != null) {
            t.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        _saveIndex = this.text.length();
        this.match('}');
        this.text.setLength(_saveIndex);
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    protected final List mTEMPLATE_ARGS(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        final List args = new ArrayList();
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 39;
        Token a = null;
        Token a2 = null;
        switch (this.LA(1)) {
            case '\t':
            case '\n':
            case '\r':
            case ' ': {
                final int _saveIndex = this.text.length();
                this.mWS_CHAR(false);
                this.text.setLength(_saveIndex);
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
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        int _saveIndex = this.text.length();
        this.mID(true);
        this.text.setLength(_saveIndex);
        a = this._returnToken;
        if (this.inputState.guessing == 0) {
            args.add(a.getText());
        }
        while (ActionLexer._tokenSet_5.member(this.LA(1)) && ActionLexer._tokenSet_6.member(this.LA(2))) {
            switch (this.LA(1)) {
                case '\t':
                case '\n':
                case '\r':
                case ' ': {
                    _saveIndex = this.text.length();
                    this.mWS_CHAR(false);
                    this.text.setLength(_saveIndex);
                    break;
                }
                case ',': {
                    break;
                }
                default: {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
            _saveIndex = this.text.length();
            this.match(',');
            this.text.setLength(_saveIndex);
            switch (this.LA(1)) {
                case '\t':
                case '\n':
                case '\r':
                case ' ': {
                    _saveIndex = this.text.length();
                    this.mWS_CHAR(false);
                    this.text.setLength(_saveIndex);
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
                    break;
                }
                default: {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
            _saveIndex = this.text.length();
            this.mID(true);
            this.text.setLength(_saveIndex);
            a2 = this._returnToken;
            if (this.inputState.guessing == 0) {
                args.add(a2.getText());
            }
        }
        switch (this.LA(1)) {
            case '\t':
            case '\n':
            case '\r':
            case ' ': {
                _saveIndex = this.text.length();
                this.mWS_CHAR(false);
                this.text.setLength(_saveIndex);
                break;
            }
            case '|': {
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        _saveIndex = this.text.length();
        this.match('|');
        this.text.setLength(_saveIndex);
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
        return args;
    }
    
    protected final void mWS_CHAR(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 43;
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
                break;
            }
            case '\n': {
                this.match('\n');
                if (this.inputState.guessing == 0) {
                    this.newline();
                    break;
                }
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    protected final void mNESTED_ANONYMOUS_TEMPLATE(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 40;
        this.match('{');
        while (true) {
            if (this.LA(1) == '\\' && this.LA(2) == '{') {
                final int _saveIndex = this.text.length();
                this.match('\\');
                this.text.setLength(_saveIndex);
                this.match('{');
            }
            else if (this.LA(1) == '\\' && this.LA(2) == '}') {
                final int _saveIndex = this.text.length();
                this.match('\\');
                this.text.setLength(_saveIndex);
                this.match('}');
            }
            else if (this.LA(1) == '\\' && this.LA(2) >= '\u0003' && this.LA(2) <= '\ufffe') {
                this.mESC_CHAR(false, false);
            }
            else if (this.LA(1) == '{') {
                this.mNESTED_ANONYMOUS_TEMPLATE(false);
            }
            else {
                if (!ActionLexer._tokenSet_4.member(this.LA(1))) {
                    break;
                }
                this.matchNot('}');
            }
        }
        this.match('}');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mLBRACK(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 36;
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
        final int _ttype = 37;
        this.match(']');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mLPAREN(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 16;
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
        final int _ttype = 17;
        this.match(')');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mCOMMA(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 19;
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
        final int _ttype = 25;
        this.match('.');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mASSIGN(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 21;
        this.match('=');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mCOLON(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 22;
        this.match(':');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mPLUS(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 24;
        this.match('+');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mSEMI(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 15;
        this.match(';');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mNOT(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 23;
        this.match('!');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mDOTDOTDOT(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 38;
        this.match("...");
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mWS(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        int _ttype = 42;
        int _cnt100 = 0;
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
                case '\r': {
                    this.match('\r');
                    break;
                }
                case '\n': {
                    this.match('\n');
                    if (this.inputState.guessing == 0) {
                        this.newline();
                        break;
                    }
                    break;
                }
                default: {
                    if (_cnt100 >= 1) {
                        if (this.inputState.guessing == 0) {
                            _ttype = -1;
                        }
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
            ++_cnt100;
        }
    }
    
    private static final long[] mk_tokenSet_0() {
        final long[] data = new long[2048];
        data[0] = -17179869192L;
        data[1] = -268435457L;
        for (int i = 2; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }
    
    private static final long[] mk_tokenSet_1() {
        final long[] data = new long[1025];
        data[0] = 4294977024L;
        data[1] = 576460745995190270L;
        return data;
    }
    
    private static final long[] mk_tokenSet_2() {
        final long[] data = new long[1025];
        data[0] = 288107235144377856L;
        data[1] = 1729382250602037246L;
        return data;
    }
    
    private static final long[] mk_tokenSet_3() {
        final long[] data = new long[1025];
        data[0] = 4294977024L;
        return data;
    }
    
    private static final long[] mk_tokenSet_4() {
        final long[] data = new long[2048];
        data[0] = -8L;
        data[1] = -2882303761785552897L;
        for (int i = 2; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }
    
    private static final long[] mk_tokenSet_5() {
        final long[] data = new long[1025];
        data[0] = 17596481021440L;
        return data;
    }
    
    private static final long[] mk_tokenSet_6() {
        final long[] data = new long[1025];
        data[0] = 17596481021440L;
        data[1] = 576460745995190270L;
        return data;
    }
    
    static {
        _tokenSet_0 = new BitSet(mk_tokenSet_0());
        _tokenSet_1 = new BitSet(mk_tokenSet_1());
        _tokenSet_2 = new BitSet(mk_tokenSet_2());
        _tokenSet_3 = new BitSet(mk_tokenSet_3());
        _tokenSet_4 = new BitSet(mk_tokenSet_4());
        _tokenSet_5 = new BitSet(mk_tokenSet_5());
        _tokenSet_6 = new BitSet(mk_tokenSet_6());
    }
}
