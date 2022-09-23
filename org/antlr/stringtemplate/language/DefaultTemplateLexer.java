// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate.language;

import antlr.SemanticException;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.CharStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.NoViableAltForCharException;
import antlr.Token;
import java.util.Hashtable;
import antlr.LexerSharedInputState;
import antlr.CharBuffer;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import java.io.InputStream;
import antlr.CharStreamException;
import antlr.RecognitionException;
import java.io.Reader;
import antlr.collections.impl.BitSet;
import org.antlr.stringtemplate.StringTemplate;
import antlr.TokenStream;
import antlr.CharScanner;

public class DefaultTemplateLexer extends CharScanner implements TemplateParserTokenTypes, TokenStream
{
    protected String currentIndent;
    protected StringTemplate self;
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
    public static final BitSet _tokenSet_11;
    public static final BitSet _tokenSet_12;
    
    public DefaultTemplateLexer(final StringTemplate self, final Reader r) {
        this(r);
        this.self = self;
    }
    
    public void reportError(final RecognitionException e) {
        this.self.error("$...$ chunk lexer error", e);
    }
    
    protected boolean upcomingELSE(final int i) throws CharStreamException {
        return this.LA(i) == '$' && this.LA(i + 1) == 'e' && this.LA(i + 2) == 'l' && this.LA(i + 3) == 's' && this.LA(i + 4) == 'e' && this.LA(i + 5) == '$';
    }
    
    protected boolean upcomingENDIF(final int i) throws CharStreamException {
        return this.LA(i) == '$' && this.LA(i + 1) == 'e' && this.LA(i + 2) == 'n' && this.LA(i + 3) == 'd' && this.LA(i + 4) == 'i' && this.LA(i + 5) == 'f' && this.LA(i + 6) == '$';
    }
    
    protected boolean upcomingAtEND(final int i) throws CharStreamException {
        return this.LA(i) == '$' && this.LA(i + 1) == '@' && this.LA(i + 2) == 'e' && this.LA(i + 3) == 'n' && this.LA(i + 4) == 'd' && this.LA(i + 5) == '$';
    }
    
    protected boolean upcomingNewline(final int i) throws CharStreamException {
        return (this.LA(i) == '\r' && this.LA(i + 1) == '\n') || this.LA(i) == '\n';
    }
    
    public DefaultTemplateLexer(final InputStream in) {
        this(new ByteBuffer(in));
    }
    
    public DefaultTemplateLexer(final Reader in) {
        this(new CharBuffer(in));
    }
    
    public DefaultTemplateLexer(final InputBuffer ib) {
        this(new LexerSharedInputState(ib));
    }
    
    public DefaultTemplateLexer(final LexerSharedInputState state) {
        super(state);
        this.currentIndent = null;
        this.setCaseSensitive(this.caseSensitiveLiterals = true);
        this.literals = new Hashtable();
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
                        case '\n':
                        case '\r': {
                            this.mNEWLINE(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        case '$': {
                            this.mACTION(true);
                            theRetToken = this._returnToken;
                            break;
                        }
                        default: {
                            if (DefaultTemplateLexer._tokenSet_0.member(this.LA(1)) && this.LA(1) != '\r' && this.LA(1) != '\n') {
                                this.mLITERAL(true);
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
                    _ttype = this.testLiteralsTable(_ttype);
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
    
    public final void mLITERAL(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        int _ttype = 4;
        Token ind = null;
        if (this.LA(1) == '\r' || this.LA(1) == '\n') {
            throw new SemanticException("LA(1)!='\\r'&&LA(1)!='\\n'");
        }
        int _cnt11 = 0;
        while (true) {
            final int loopStartIndex = this.text.length();
            final int col = this.getColumn();
            if (this.LA(1) == '\\' && this.LA(2) == '$') {
                final int _saveIndex = this.text.length();
                this.match('\\');
                this.text.setLength(_saveIndex);
                this.match('$');
            }
            else if (this.LA(1) == '\\' && this.LA(2) == '\\') {
                final int _saveIndex = this.text.length();
                this.match('\\');
                this.text.setLength(_saveIndex);
                this.match('\\');
            }
            else if (this.LA(1) == '\\' && DefaultTemplateLexer._tokenSet_1.member(this.LA(2))) {
                this.match('\\');
                this.matchNot('$');
            }
            else if (this.LA(1) == '\t' || this.LA(1) == ' ') {
                this.mINDENT(true);
                ind = this._returnToken;
                if (col == 1 && this.LA(1) == '$') {
                    this.currentIndent = ind.getText();
                    this.text.setLength(loopStartIndex);
                }
                else {
                    this.currentIndent = null;
                }
            }
            else {
                if (!DefaultTemplateLexer._tokenSet_0.member(this.LA(1))) {
                    break;
                }
                this.match(DefaultTemplateLexer._tokenSet_0);
            }
            ++_cnt11;
        }
        if (_cnt11 >= 1) {
            if (new String(this.text.getBuffer(), _begin, this.text.length() - _begin).length() == 0) {
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
    
    protected final void mINDENT(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 21;
        int _cnt76 = 0;
        while (true) {
            if (this.LA(1) == ' ') {
                this.match(' ');
            }
            else {
                if (this.LA(1) != '\t') {
                    break;
                }
                this.match('\t');
            }
            ++_cnt76;
        }
        if (_cnt76 >= 1) {
            if (_createToken && _token == null && _ttype != -1) {
                _token = this.makeToken(_ttype);
                _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
            }
            this._returnToken = _token;
            return;
        }
        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
    }
    
    public final void mNEWLINE(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 5;
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
        this.currentIndent = null;
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    public final void mACTION(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        int _ttype = 6;
        final int startCol = this.getColumn();
        if (this.LA(1) == '$' && this.LA(2) == '\\' && this.LA(3) == '\\' && this.LA(4) == '$' && DefaultTemplateLexer._tokenSet_2.member(this.LA(5))) {
            this.mLINE_BREAK(false);
            _ttype = -1;
        }
        else if (this.LA(1) == '$' && this.LA(2) == '\\' && DefaultTemplateLexer._tokenSet_3.member(this.LA(3)) && DefaultTemplateLexer._tokenSet_4.member(this.LA(4))) {
            final StringBuffer buf = new StringBuffer();
            char uc = '\0';
            int _saveIndex = this.text.length();
            this.match('$');
            this.text.setLength(_saveIndex);
            int _cnt16 = 0;
            while (this.LA(1) == '\\') {
                uc = this.mESC_CHAR(false);
                buf.append(uc);
                ++_cnt16;
            }
            if (_cnt16 < 1) {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            _saveIndex = this.text.length();
            this.match('$');
            this.text.setLength(_saveIndex);
            this.text.setLength(_begin);
            this.text.append(buf.toString());
            _ttype = 4;
        }
        else if (this.LA(1) == '$' && this.LA(2) == '!' && this.LA(3) >= '\u0001' && this.LA(3) <= '\ufffe' && this.LA(4) >= '\u0001' && this.LA(4) <= '\ufffe') {
            this.mCOMMENT(false);
            _ttype = -1;
        }
        else {
            if (this.LA(1) != '$' || !DefaultTemplateLexer._tokenSet_1.member(this.LA(2)) || this.LA(3) < '\u0001' || this.LA(3) > '\ufffe') {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            if (this.LA(1) == '$' && this.LA(2) == 'i' && this.LA(3) == 'f' && (this.LA(4) == ' ' || this.LA(4) == '(') && DefaultTemplateLexer._tokenSet_5.member(this.LA(5)) && this.LA(6) >= '\u0001' && this.LA(6) <= '\ufffe' && this.LA(7) >= '\u0001' && this.LA(7) <= '\ufffe') {
                int _saveIndex = this.text.length();
                this.match('$');
                this.text.setLength(_saveIndex);
                this.match("if");
                while (this.LA(1) == ' ') {
                    _saveIndex = this.text.length();
                    this.match(' ');
                    this.text.setLength(_saveIndex);
                }
                this.match("(");
                this.mIF_EXPR(false);
                this.match(")");
                _saveIndex = this.text.length();
                this.match('$');
                this.text.setLength(_saveIndex);
                _ttype = 7;
                if (this.LA(1) == '\n' || this.LA(1) == '\r') {
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
            }
            else if (this.LA(1) == '$' && this.LA(2) == 'e' && this.LA(3) == 'l' && this.LA(4) == 's' && this.LA(5) == 'e' && this.LA(6) == 'i' && this.LA(7) == 'f') {
                int _saveIndex = this.text.length();
                this.match('$');
                this.text.setLength(_saveIndex);
                this.match("elseif");
                while (this.LA(1) == ' ') {
                    _saveIndex = this.text.length();
                    this.match(' ');
                    this.text.setLength(_saveIndex);
                }
                this.match("(");
                this.mIF_EXPR(false);
                this.match(")");
                _saveIndex = this.text.length();
                this.match('$');
                this.text.setLength(_saveIndex);
                _ttype = 8;
                if (this.LA(1) == '\n' || this.LA(1) == '\r') {
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
            }
            else if (this.LA(1) == '$' && this.LA(2) == 'e' && this.LA(3) == 'n' && this.LA(4) == 'd' && this.LA(5) == 'i' && this.LA(6) == 'f' && this.LA(7) == '$') {
                int _saveIndex = this.text.length();
                this.match('$');
                this.text.setLength(_saveIndex);
                this.match("endif");
                _saveIndex = this.text.length();
                this.match('$');
                this.text.setLength(_saveIndex);
                _ttype = 10;
                if ((this.LA(1) == '\n' || this.LA(1) == '\r') && startCol == 1) {
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
            }
            else if (this.LA(1) == '$' && this.LA(2) == 'e' && this.LA(3) == 'l' && this.LA(4) == 's' && this.LA(5) == 'e' && this.LA(6) == '$') {
                int _saveIndex = this.text.length();
                this.match('$');
                this.text.setLength(_saveIndex);
                this.match("else");
                _saveIndex = this.text.length();
                this.match('$');
                this.text.setLength(_saveIndex);
                _ttype = 9;
                if (this.LA(1) == '\n' || this.LA(1) == '\r') {
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
            }
            else if (this.LA(1) == '$' && this.LA(2) == '@' && DefaultTemplateLexer._tokenSet_6.member(this.LA(3)) && this.LA(4) >= '\u0001' && this.LA(4) <= '\ufffe' && this.LA(5) >= '\u0001' && this.LA(5) <= '\ufffe' && this.LA(6) >= '\u0001' && this.LA(6) <= '\ufffe') {
                int _saveIndex = this.text.length();
                this.match('$');
                this.text.setLength(_saveIndex);
                _saveIndex = this.text.length();
                this.match('@');
                this.text.setLength(_saveIndex);
                int _cnt17 = 0;
                while (DefaultTemplateLexer._tokenSet_6.member(this.LA(1))) {
                    this.match(DefaultTemplateLexer._tokenSet_6);
                    ++_cnt17;
                }
                if (_cnt17 < 1) {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
                switch (this.LA(1)) {
                    case '(': {
                        _saveIndex = this.text.length();
                        this.match("()");
                        this.text.setLength(_saveIndex);
                        _saveIndex = this.text.length();
                        this.match('$');
                        this.text.setLength(_saveIndex);
                        _ttype = 11;
                        break;
                    }
                    case '$': {
                        _saveIndex = this.text.length();
                        this.match('$');
                        this.text.setLength(_saveIndex);
                        _ttype = 12;
                        final String t = new String(this.text.getBuffer(), _begin, this.text.length() - _begin);
                        this.text.setLength(_begin);
                        this.text.append(t + "::=");
                        if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\u0001' && this.LA(2) <= '\ufffe' && this.LA(3) >= '\u0001' && this.LA(3) <= '\ufffe') {
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
                        else if (this.LA(1) < '\u0001' || this.LA(1) > '\ufffe' || this.LA(2) < '\u0001' || this.LA(2) > '\ufffe') {
                            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                        }
                        boolean atLeft = false;
                        int _cnt18 = 0;
                        while (this.LA(1) >= '\u0001' && this.LA(1) <= '\ufffe' && this.LA(2) >= '\u0001' && this.LA(2) <= '\ufffe' && !this.upcomingAtEND(1) && (!this.upcomingNewline(1) || !this.upcomingAtEND(2))) {
                            if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\u0001' && this.LA(2) <= '\ufffe') {
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
                                atLeft = true;
                            }
                            else {
                                if (this.LA(1) < '\u0001' || this.LA(1) > '\ufffe' || this.LA(2) < '\u0001' || this.LA(2) > '\ufffe') {
                                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                                }
                                this.matchNot('\uffff');
                                atLeft = false;
                            }
                            ++_cnt18;
                        }
                        if (_cnt18 < 1) {
                            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                        }
                        if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\u0001' && this.LA(2) <= '\ufffe') {
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
                            atLeft = true;
                        }
                        else if (this.LA(1) < '\u0001' || this.LA(1) > '\ufffe') {
                            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                        }
                        if (this.LA(1) == '$' && this.LA(2) == '@') {
                            _saveIndex = this.text.length();
                            this.match("$@end$");
                            this.text.setLength(_saveIndex);
                        }
                        else {
                            if (this.LA(1) < '\u0001' || this.LA(1) > '\ufffe') {
                                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                            }
                            this.matchNot('\uffff');
                            this.self.error("missing region " + t + " $@end$ tag");
                        }
                        if ((this.LA(1) == '\n' || this.LA(1) == '\r') && atLeft) {
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
                            break;
                        }
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
            }
            else {
                if (this.LA(1) != '$' || !DefaultTemplateLexer._tokenSet_1.member(this.LA(2)) || this.LA(3) < '\u0001' || this.LA(3) > '\ufffe') {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
                int _saveIndex = this.text.length();
                this.match('$');
                this.text.setLength(_saveIndex);
                this.mEXPR(false);
                _saveIndex = this.text.length();
                this.match('$');
                this.text.setLength(_saveIndex);
            }
            final ChunkToken t2 = (ChunkToken)(_token = new ChunkToken(_ttype, new String(this.text.getBuffer(), _begin, this.text.length() - _begin), this.currentIndent));
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    protected final void mLINE_BREAK(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 23;
        this.match("$\\\\$");
        switch (this.LA(1)) {
            case '\t':
            case ' ': {
                this.mINDENT(false);
                break;
            }
            case '\n':
            case '\r': {
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
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
        if (this.LA(1) == '\t' || this.LA(1) == ' ') {
            this.mINDENT(false);
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    protected final char mESC_CHAR(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        char uc = '\0';
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 16;
        Token a = null;
        Token b = null;
        Token c = null;
        Token d = null;
        if (this.LA(1) == '\\' && this.LA(2) == 'n') {
            final int _saveIndex = this.text.length();
            this.match("\\n");
            this.text.setLength(_saveIndex);
            uc = '\n';
        }
        else if (this.LA(1) == '\\' && this.LA(2) == 'r') {
            final int _saveIndex = this.text.length();
            this.match("\\r");
            this.text.setLength(_saveIndex);
            uc = '\r';
        }
        else if (this.LA(1) == '\\' && this.LA(2) == 't') {
            final int _saveIndex = this.text.length();
            this.match("\\t");
            this.text.setLength(_saveIndex);
            uc = '\t';
        }
        else if (this.LA(1) == '\\' && this.LA(2) == ' ') {
            final int _saveIndex = this.text.length();
            this.match("\\ ");
            this.text.setLength(_saveIndex);
            uc = ' ';
        }
        else {
            if (this.LA(1) != '\\' || this.LA(2) != 'u') {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            int _saveIndex = this.text.length();
            this.match("\\u");
            this.text.setLength(_saveIndex);
            _saveIndex = this.text.length();
            this.mHEX(true);
            this.text.setLength(_saveIndex);
            a = this._returnToken;
            _saveIndex = this.text.length();
            this.mHEX(true);
            this.text.setLength(_saveIndex);
            b = this._returnToken;
            _saveIndex = this.text.length();
            this.mHEX(true);
            this.text.setLength(_saveIndex);
            c = this._returnToken;
            _saveIndex = this.text.length();
            this.mHEX(true);
            this.text.setLength(_saveIndex);
            d = this._returnToken;
            uc = (char)Integer.parseInt(a.getText() + b.getText() + c.getText() + d.getText(), 16);
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
        return uc;
    }
    
    protected final void mCOMMENT(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 22;
        final int startCol = this.getColumn();
        this.match("$!");
        while (true) {
            while (this.LA(1) != '!' || this.LA(2) != '$') {
                if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\u0001' && this.LA(2) <= '\ufffe' && this.LA(3) >= '\u0001' && this.LA(3) <= '\ufffe') {
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
                    if (this.LA(1) < '\u0001' || this.LA(1) > '\ufffe' || this.LA(2) < '\u0001' || this.LA(2) > '\ufffe' || this.LA(3) < '\u0001' || this.LA(3) > '\ufffe') {
                        this.match("!$");
                        if ((this.LA(1) == '\n' || this.LA(1) == '\r') && startCol == 1) {
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
    
    protected final void mIF_EXPR(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 15;
        int _cnt64 = 0;
        while (true) {
            switch (this.LA(1)) {
                case '\\': {
                    this.mESC(false);
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
                case '{': {
                    this.mSUBTEMPLATE(false);
                    break;
                }
                case '(': {
                    this.mNESTED_PARENS(false);
                    break;
                }
                default: {
                    if (DefaultTemplateLexer._tokenSet_7.member(this.LA(1))) {
                        this.matchNot(')');
                        break;
                    }
                    if (_cnt64 >= 1) {
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
            ++_cnt64;
        }
    }
    
    protected final void mEXPR(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 13;
        int _cnt52 = 0;
        while (true) {
            switch (this.LA(1)) {
                case '\\': {
                    this.mESC(false);
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
                case '{': {
                    this.mSUBTEMPLATE(false);
                    break;
                }
                default: {
                    if ((this.LA(1) == '+' || this.LA(1) == '=') && (this.LA(2) == '\"' || this.LA(2) == '<')) {
                        switch (this.LA(1)) {
                            case '=': {
                                this.match('=');
                                break;
                            }
                            case '+': {
                                this.match('+');
                                break;
                            }
                            default: {
                                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                            }
                        }
                        this.mTEMPLATE(false);
                        break;
                    }
                    if ((this.LA(1) == '+' || this.LA(1) == '=') && this.LA(2) == '{') {
                        switch (this.LA(1)) {
                            case '=': {
                                this.match('=');
                                break;
                            }
                            case '+': {
                                this.match('+');
                                break;
                            }
                            default: {
                                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                            }
                        }
                        this.mSUBTEMPLATE(false);
                        break;
                    }
                    if ((this.LA(1) == '+' || this.LA(1) == '=') && DefaultTemplateLexer._tokenSet_8.member(this.LA(2))) {
                        switch (this.LA(1)) {
                            case '=': {
                                this.match('=');
                                break;
                            }
                            case '+': {
                                this.match('+');
                                break;
                            }
                            default: {
                                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                            }
                        }
                        this.match(DefaultTemplateLexer._tokenSet_8);
                        break;
                    }
                    if (DefaultTemplateLexer._tokenSet_9.member(this.LA(1))) {
                        this.matchNot('$');
                        break;
                    }
                    if (_cnt52 >= 1) {
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
            ++_cnt52;
        }
    }
    
    protected final void mESC(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 17;
        this.match('\\');
        this.matchNot('\uffff');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    protected final void mSUBTEMPLATE(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 19;
        this.match('{');
    Label_0091:
        while (true) {
            switch (this.LA(1)) {
                case '{': {
                    this.mSUBTEMPLATE(false);
                    continue;
                }
                case '\\': {
                    this.mESC(false);
                    continue;
                }
                default: {
                    if (DefaultTemplateLexer._tokenSet_10.member(this.LA(1))) {
                        this.matchNot('}');
                        continue;
                    }
                    break Label_0091;
                }
            }
        }
        this.match('}');
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    protected final void mTEMPLATE(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 14;
        Label_0977: {
            switch (this.LA(1)) {
                case '\"': {
                    this.match('\"');
                    while (true) {
                        if (this.LA(1) == '\\') {
                            this.mESC(false);
                        }
                        else {
                            if (!DefaultTemplateLexer._tokenSet_11.member(this.LA(1))) {
                                break;
                            }
                            this.matchNot('\"');
                        }
                    }
                    this.match('\"');
                    break;
                }
                case '<': {
                    this.match("<<");
                    if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\u0001' && this.LA(2) <= '\ufffe' && this.LA(3) >= '\u0001' && this.LA(3) <= '\ufffe' && this.LA(4) >= '\u0001' && this.LA(4) <= '\ufffe') {
                        switch (this.LA(1)) {
                            case '\r': {
                                final int _saveIndex = this.text.length();
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
                        final int _saveIndex = this.text.length();
                        this.match('\n');
                        this.text.setLength(_saveIndex);
                        this.newline();
                    }
                    else if (this.LA(1) < '\u0001' || this.LA(1) > '\ufffe' || this.LA(2) < '\u0001' || this.LA(2) > '\ufffe' || this.LA(3) < '\u0001' || this.LA(3) > '\ufffe') {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                    while (true) {
                        while (this.LA(1) != '>' || this.LA(2) != '>' || this.LA(3) < '\u0001' || this.LA(3) > '\ufffe') {
                            if (this.LA(1) == '\r' && this.LA(2) == '\n' && this.LA(3) >= '\u0001' && this.LA(3) <= '\ufffe' && this.LA(4) >= '\u0001' && this.LA(4) <= '\ufffe' && this.LA(5) >= '\u0001' && this.LA(5) <= '\ufffe' && this.LA(3) == '>' && this.LA(4) == '>') {
                                int _saveIndex = this.text.length();
                                this.match('\r');
                                this.text.setLength(_saveIndex);
                                _saveIndex = this.text.length();
                                this.match('\n');
                                this.text.setLength(_saveIndex);
                                this.newline();
                            }
                            else if (this.LA(1) == '\n' && this.LA(2) >= '\u0001' && this.LA(2) <= '\ufffe' && this.LA(3) >= '\u0001' && this.LA(3) <= '\ufffe' && this.LA(4) >= '\u0001' && this.LA(4) <= '\ufffe' && this.LA(2) == '>' && this.LA(3) == '>') {
                                final int _saveIndex = this.text.length();
                                this.match('\n');
                                this.text.setLength(_saveIndex);
                                this.newline();
                            }
                            else if ((this.LA(1) == '\n' || this.LA(1) == '\r') && this.LA(2) >= '\u0001' && this.LA(2) <= '\ufffe' && this.LA(3) >= '\u0001' && this.LA(3) <= '\ufffe' && this.LA(4) >= '\u0001' && this.LA(4) <= '\ufffe') {
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
                                if (this.LA(1) < '\u0001' || this.LA(1) > '\ufffe' || this.LA(2) < '\u0001' || this.LA(2) > '\ufffe' || this.LA(3) < '\u0001' || this.LA(3) > '\ufffe' || this.LA(4) < '\u0001' || this.LA(4) > '\ufffe') {
                                    this.match(">>");
                                    break Label_0977;
                                }
                                this.matchNot('\uffff');
                            }
                        }
                        continue;
                    }
                }
                default: {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
        }
        if (_createToken && _token == null && _ttype != -1) {
            _token = this.makeToken(_ttype);
            _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
        }
        this._returnToken = _token;
    }
    
    protected final void mNESTED_PARENS(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 20;
        this.match('(');
        int _cnt73 = 0;
    Label_0095:
        while (true) {
            switch (this.LA(1)) {
                case '(': {
                    this.mNESTED_PARENS(false);
                    break;
                }
                case '\\': {
                    this.mESC(false);
                    break;
                }
                default: {
                    if (DefaultTemplateLexer._tokenSet_12.member(this.LA(1))) {
                        this.matchNot(')');
                        break;
                    }
                    break Label_0095;
                }
            }
            ++_cnt73;
        }
        if (_cnt73 >= 1) {
            this.match(')');
            if (_createToken && _token == null && _ttype != -1) {
                _token = this.makeToken(_ttype);
                _token.setText(new String(this.text.getBuffer(), _begin, this.text.length() - _begin));
            }
            this._returnToken = _token;
            return;
        }
        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
    }
    
    protected final void mHEX(final boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
        Token _token = null;
        final int _begin = this.text.length();
        final int _ttype = 18;
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
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F': {
                this.matchRange('A', 'F');
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
    
    private static final long[] mk_tokenSet_0() {
        final long[] data = new long[2048];
        data[0] = -68719485954L;
        for (int i = 1; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }
    
    private static final long[] mk_tokenSet_1() {
        final long[] data = new long[2048];
        data[0] = -68719476738L;
        for (int i = 1; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }
    
    private static final long[] mk_tokenSet_2() {
        final long[] data = new long[1025];
        data[0] = 4294977024L;
        return data;
    }
    
    private static final long[] mk_tokenSet_3() {
        final long[] data = new long[1025];
        data[0] = 4294967296L;
        data[1] = 14707067533131776L;
        return data;
    }
    
    private static final long[] mk_tokenSet_4() {
        final long[] data = new long[1025];
        data[0] = 287948969894477824L;
        data[1] = 541434314878L;
        return data;
    }
    
    private static final long[] mk_tokenSet_5() {
        final long[] data = new long[2048];
        data[0] = -2199023255554L;
        for (int i = 1; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }
    
    private static final long[] mk_tokenSet_6() {
        final long[] data = new long[2048];
        data[0] = -1168231104514L;
        for (int i = 1; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }
    
    private static final long[] mk_tokenSet_7() {
        final long[] data = new long[2048];
        data[0] = -3298534892546L;
        data[1] = -576460752571858945L;
        for (int i = 2; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }
    
    private static final long[] mk_tokenSet_8() {
        final long[] data = new long[2048];
        data[0] = -1152921521786716162L;
        data[1] = -576460752303423489L;
        for (int i = 2; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }
    
    private static final long[] mk_tokenSet_9() {
        final long[] data = new long[2048];
        data[0] = -2305851874026202114L;
        data[1] = -576460752571858945L;
        for (int i = 2; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }
    
    private static final long[] mk_tokenSet_10() {
        final long[] data = new long[2048];
        data[0] = -2L;
        data[1] = -2882303761785552897L;
        for (int i = 2; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }
    
    private static final long[] mk_tokenSet_11() {
        final long[] data = new long[2048];
        data[0] = -17179869186L;
        data[1] = -268435457L;
        for (int i = 2; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
        return data;
    }
    
    private static final long[] mk_tokenSet_12() {
        final long[] data = new long[2048];
        data[0] = -3298534883330L;
        data[1] = -268435457L;
        for (int i = 2; i <= 1022; ++i) {
            data[i] = -1L;
        }
        data[1023] = Long.MAX_VALUE;
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
        _tokenSet_7 = new BitSet(mk_tokenSet_7());
        _tokenSet_8 = new BitSet(mk_tokenSet_8());
        _tokenSet_9 = new BitSet(mk_tokenSet_9());
        _tokenSet_10 = new BitSet(mk_tokenSet_10());
        _tokenSet_11 = new BitSet(mk_tokenSet_11());
        _tokenSet_12 = new BitSet(mk_tokenSet_12());
    }
}
