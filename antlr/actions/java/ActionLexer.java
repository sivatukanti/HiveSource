// 
// Decompiled by Procyon v0.5.36
// 

package antlr.actions.java;

import antlr.collections.impl.Vector;
import antlr.CharStreamException;
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
import antlr.RecognitionException;
import java.io.Reader;
import java.io.StringReader;
import antlr.collections.impl.BitSet;
import antlr.ActionTransInfo;
import antlr.Tool;
import antlr.CodeGenerator;
import antlr.RuleBlock;
import antlr.TokenStream;
import antlr.CharScanner;

public class ActionLexer extends CharScanner implements ActionLexerTokenTypes, TokenStream
{
    protected RuleBlock currentRule;
    protected CodeGenerator generator;
    protected int lineOffset;
    private Tool antlrTool;
    ActionTransInfo transInfo;
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
    public static final BitSet _tokenSet_13;
    public static final BitSet _tokenSet_14;
    public static final BitSet _tokenSet_15;
    public static final BitSet _tokenSet_16;
    public static final BitSet _tokenSet_17;
    public static final BitSet _tokenSet_18;
    public static final BitSet _tokenSet_19;
    public static final BitSet _tokenSet_20;
    public static final BitSet _tokenSet_21;
    public static final BitSet _tokenSet_22;
    public static final BitSet _tokenSet_23;
    public static final BitSet _tokenSet_24;
    public static final BitSet _tokenSet_25;
    
    public ActionLexer(final String s, final RuleBlock currentRule, final CodeGenerator generator, final ActionTransInfo transInfo) {
        this(new StringReader(s));
        this.currentRule = currentRule;
        this.generator = generator;
        this.transInfo = transInfo;
    }
    
    public void setLineOffset(final int line) {
        this.setLine(line);
    }
    
    public void setTool(final Tool antlrTool) {
        this.antlrTool = antlrTool;
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
    
    public ActionLexer(final InputStream inputStream) {
        this(new ByteBuffer(inputStream));
    }
    
    public ActionLexer(final Reader reader) {
        this(new CharBuffer(reader));
    }
    
    public ActionLexer(final InputBuffer inputBuffer) {
        this(new LexerSharedInputState(inputBuffer));
    }
    
    public ActionLexer(final LexerSharedInputState lexerSharedInputState) {
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
                    if (this.LA(1) >= '\u0003' && this.LA(1) <= '\u00ff') {
                        this.mACTION(true);
                        final Token returnToken = this._returnToken;
                    }
                    else {
                        if (this.LA(1) != '\uffff') {
                            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                        }
                        this.uponEOF();
                        this._returnToken = this.makeToken(1);
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
    
    public final void mACTION(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 4;
        int n2 = 0;
    Label_0086:
        while (true) {
            switch (this.LA(1)) {
                case '#': {
                    this.mAST_ITEM(false);
                    break;
                }
                case '$': {
                    this.mTEXT_ITEM(false);
                    break;
                }
                default: {
                    if (ActionLexer._tokenSet_0.member(this.LA(1))) {
                        this.mSTUFF(false);
                        break;
                    }
                    break Label_0086;
                }
            }
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
    
    protected final void mSTUFF(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 5;
        switch (this.LA(1)) {
            case '\"': {
                this.mSTRING(false);
                break;
            }
            case '\'': {
                this.mCHAR(false);
                break;
            }
            case '\n': {
                this.match('\n');
                this.newline();
                break;
            }
            default: {
                if (this.LA(1) == '/' && (this.LA(2) == '*' || this.LA(2) == '/')) {
                    this.mCOMMENT(false);
                    break;
                }
                if (this.LA(1) == '\r' && this.LA(2) == '\n') {
                    this.match("\r\n");
                    this.newline();
                    break;
                }
                if (this.LA(1) == '/' && ActionLexer._tokenSet_1.member(this.LA(2))) {
                    this.match('/');
                    this.match(ActionLexer._tokenSet_1);
                    break;
                }
                if (this.LA(1) == '\r') {
                    this.match('\r');
                    this.newline();
                    break;
                }
                if (ActionLexer._tokenSet_2.member(this.LA(1))) {
                    this.match(ActionLexer._tokenSet_2);
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
    
    protected final void mAST_ITEM(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 6;
        if (this.LA(1) == '#' && this.LA(2) == '(') {
            final int length2 = this.text.length();
            this.match('#');
            this.text.setLength(length2);
            this.mTREE(true);
            final Token returnToken = this._returnToken;
        }
        else if (this.LA(1) == '#' && ActionLexer._tokenSet_3.member(this.LA(2))) {
            final int length3 = this.text.length();
            this.match('#');
            this.text.setLength(length3);
            this.mID(true);
            final String mapTreeId = this.generator.mapTreeId(this._returnToken.getText(), this.transInfo);
            if (mapTreeId != null) {
                this.text.setLength(length);
                this.text.append(mapTreeId);
            }
            if (ActionLexer._tokenSet_4.member(this.LA(1))) {
                this.mWS(false);
            }
            if (this.LA(1) == '=') {
                this.mVAR_ASSIGN(false);
            }
        }
        else if (this.LA(1) == '#' && this.LA(2) == '[') {
            final int length4 = this.text.length();
            this.match('#');
            this.text.setLength(length4);
            this.mAST_CONSTRUCTOR(true);
            final Token returnToken2 = this._returnToken;
        }
        else {
            if (this.LA(1) != '#' || this.LA(2) != '#') {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            this.match("##");
            final String string = this.currentRule.getRuleName() + "_AST";
            this.text.setLength(length);
            this.text.append(string);
            if (this.transInfo != null) {
                this.transInfo.refRuleRoot = string;
            }
            if (ActionLexer._tokenSet_4.member(this.LA(1))) {
                this.mWS(false);
            }
            if (this.LA(1) == '=') {
                this.mVAR_ASSIGN(false);
            }
        }
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mTEXT_ITEM(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 7;
        Token returnToken = null;
        Token returnToken2 = null;
        if (this.LA(1) == '$' && this.LA(2) == 'F' && this.LA(3) == 'O') {
            this.match("$FOLLOW");
            if (ActionLexer._tokenSet_5.member(this.LA(1)) && ActionLexer._tokenSet_6.member(this.LA(2)) && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                switch (this.LA(1)) {
                    case '\t':
                    case '\n':
                    case '\r':
                    case ' ': {
                        this.mWS(false);
                        break;
                    }
                    case '(': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
                this.match('(');
                this.mTEXT_ARG(true);
                returnToken = this._returnToken;
                this.match(')');
            }
            String str = this.currentRule.getRuleName();
            if (returnToken != null) {
                str = returnToken.getText();
            }
            final String followBitSet = this.generator.getFOLLOWBitSet(str, 1);
            if (followBitSet == null) {
                this.reportError("$FOLLOW(" + str + ")" + ": unknown rule or bad lookahead computation");
            }
            else {
                this.text.setLength(length);
                this.text.append(followBitSet);
            }
        }
        else if (this.LA(1) == '$' && this.LA(2) == 'F' && this.LA(3) == 'I') {
            this.match("$FIRST");
            if (ActionLexer._tokenSet_5.member(this.LA(1)) && ActionLexer._tokenSet_6.member(this.LA(2)) && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                switch (this.LA(1)) {
                    case '\t':
                    case '\n':
                    case '\r':
                    case ' ': {
                        this.mWS(false);
                        break;
                    }
                    case '(': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
                this.match('(');
                this.mTEXT_ARG(true);
                returnToken2 = this._returnToken;
                this.match(')');
            }
            String str2 = this.currentRule.getRuleName();
            if (returnToken2 != null) {
                str2 = returnToken2.getText();
            }
            final String firstBitSet = this.generator.getFIRSTBitSet(str2, 1);
            if (firstBitSet == null) {
                this.reportError("$FIRST(" + str2 + ")" + ": unknown rule or bad lookahead computation");
            }
            else {
                this.text.setLength(length);
                this.text.append(firstBitSet);
            }
        }
        else if (this.LA(1) == '$' && this.LA(2) == 'a') {
            this.match("$append");
            switch (this.LA(1)) {
                case '\t':
                case '\n':
                case '\r':
                case ' ': {
                    this.mWS(false);
                    break;
                }
                case '(': {
                    break;
                }
                default: {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
            this.match('(');
            this.mTEXT_ARG(true);
            final Token returnToken3 = this._returnToken;
            this.match(')');
            final String string = "text.append(" + returnToken3.getText() + ")";
            this.text.setLength(length);
            this.text.append(string);
        }
        else if (this.LA(1) == '$' && this.LA(2) == 's') {
            this.match("$set");
            if (this.LA(1) == 'T' && this.LA(2) == 'e') {
                this.match("Text");
                switch (this.LA(1)) {
                    case '\t':
                    case '\n':
                    case '\r':
                    case ' ': {
                        this.mWS(false);
                        break;
                    }
                    case '(': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
                this.match('(');
                this.mTEXT_ARG(true);
                final Token returnToken4 = this._returnToken;
                this.match(')');
                final String string2 = "text.setLength(_begin); text.append(" + returnToken4.getText() + ")";
                this.text.setLength(length);
                this.text.append(string2);
            }
            else if (this.LA(1) == 'T' && this.LA(2) == 'o') {
                this.match("Token");
                switch (this.LA(1)) {
                    case '\t':
                    case '\n':
                    case '\r':
                    case ' ': {
                        this.mWS(false);
                        break;
                    }
                    case '(': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
                this.match('(');
                this.mTEXT_ARG(true);
                final Token returnToken5 = this._returnToken;
                this.match(')');
                final String string3 = "_token = " + returnToken5.getText();
                this.text.setLength(length);
                this.text.append(string3);
            }
            else {
                if (this.LA(1) != 'T' || this.LA(2) != 'y') {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
                this.match("Type");
                switch (this.LA(1)) {
                    case '\t':
                    case '\n':
                    case '\r':
                    case ' ': {
                        this.mWS(false);
                        break;
                    }
                    case '(': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
                this.match('(');
                this.mTEXT_ARG(true);
                final Token returnToken6 = this._returnToken;
                this.match(')');
                final String string4 = "_ttype = " + returnToken6.getText();
                this.text.setLength(length);
                this.text.append(string4);
            }
        }
        else {
            if (this.LA(1) != '$' || this.LA(2) != 'g') {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            this.match("$getText");
            this.text.setLength(length);
            this.text.append("new String(text.getBuffer(),_begin,text.length()-_begin)");
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
        final int n = 19;
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
    
    protected final void mSTRING(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 23;
        this.match('\"');
        while (true) {
            if (this.LA(1) == '\\') {
                this.mESC(false);
            }
            else {
                if (!ActionLexer._tokenSet_7.member(this.LA(1))) {
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
    
    protected final void mCHAR(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 22;
        this.match('\'');
        if (this.LA(1) == '\\') {
            this.mESC(false);
        }
        else {
            if (!ActionLexer._tokenSet_8.member(this.LA(1))) {
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
    
    protected final void mTREE(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 8;
        final StringBuffer sb = new StringBuffer();
        final Vector vector = new Vector(10);
        final int length2 = this.text.length();
        this.match('(');
        this.text.setLength(length2);
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
            case '\"':
            case '#':
            case '(':
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
            case '[':
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
        final int length4 = this.text.length();
        this.mTREE_ELEMENT(true);
        this.text.setLength(length4);
        vector.appendElement(this._returnToken.getText());
        switch (this.LA(1)) {
            case '\t':
            case '\n':
            case '\r':
            case ' ': {
                final int length5 = this.text.length();
                this.mWS(false);
                this.text.setLength(length5);
                break;
            }
            case ')':
            case ',': {
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        while (this.LA(1) == ',') {
            final int length6 = this.text.length();
            this.match(',');
            this.text.setLength(length6);
            switch (this.LA(1)) {
                case '\t':
                case '\n':
                case '\r':
                case ' ': {
                    final int length7 = this.text.length();
                    this.mWS(false);
                    this.text.setLength(length7);
                    break;
                }
                case '\"':
                case '#':
                case '(':
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
                case '[':
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
            final int length8 = this.text.length();
            this.mTREE_ELEMENT(true);
            this.text.setLength(length8);
            vector.appendElement(this._returnToken.getText());
            switch (this.LA(1)) {
                case '\t':
                case '\n':
                case '\r':
                case ' ': {
                    final int length9 = this.text.length();
                    this.mWS(false);
                    this.text.setLength(length9);
                    continue;
                }
                case ')':
                case ',': {
                    continue;
                }
                default: {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
        }
        this.text.setLength(length);
        this.text.append(this.generator.getASTCreateString(vector));
        final int length10 = this.text.length();
        this.match(')');
        this.text.setLength(length10);
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mID(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 17;
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
        while (ActionLexer._tokenSet_9.member(this.LA(1))) {
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
                default: {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
        }
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mWS(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 28;
        int n2 = 0;
        while (true) {
            if (this.LA(1) == '\r' && this.LA(2) == '\n') {
                this.match('\r');
                this.match('\n');
                this.newline();
            }
            else if (this.LA(1) == ' ') {
                this.match(' ');
            }
            else if (this.LA(1) == '\t') {
                this.match('\t');
            }
            else if (this.LA(1) == '\r') {
                this.match('\r');
                this.newline();
            }
            else {
                if (this.LA(1) != '\n') {
                    break;
                }
                this.match('\n');
                this.newline();
            }
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
    
    protected final void mVAR_ASSIGN(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 18;
        this.match('=');
        if (this.LA(1) != '=' && this.transInfo != null && this.transInfo.refRuleRoot != null) {
            this.transInfo.assignToRoot = true;
        }
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mAST_CONSTRUCTOR(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 10;
        Token returnToken = null;
        Token returnToken2 = null;
        final int length2 = this.text.length();
        this.match('[');
        this.text.setLength(length2);
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
            case '\"':
            case '#':
            case '(':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
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
            case '[':
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
        final int length4 = this.text.length();
        this.mAST_CTOR_ELEMENT(true);
        this.text.setLength(length4);
        final Token returnToken3 = this._returnToken;
        switch (this.LA(1)) {
            case '\t':
            case '\n':
            case '\r':
            case ' ': {
                final int length5 = this.text.length();
                this.mWS(false);
                this.text.setLength(length5);
                break;
            }
            case ',':
            case ']': {
                break;
            }
            default: {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        if (this.LA(1) == ',' && ActionLexer._tokenSet_10.member(this.LA(2)) && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
            final int length6 = this.text.length();
            this.match(',');
            this.text.setLength(length6);
            switch (this.LA(1)) {
                case '\t':
                case '\n':
                case '\r':
                case ' ': {
                    final int length7 = this.text.length();
                    this.mWS(false);
                    this.text.setLength(length7);
                    break;
                }
                case '\"':
                case '#':
                case '(':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
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
                case '[':
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
            final int length8 = this.text.length();
            this.mAST_CTOR_ELEMENT(true);
            this.text.setLength(length8);
            returnToken = this._returnToken;
            switch (this.LA(1)) {
                case '\t':
                case '\n':
                case '\r':
                case ' ': {
                    final int length9 = this.text.length();
                    this.mWS(false);
                    this.text.setLength(length9);
                    break;
                }
                case ',':
                case ']': {
                    break;
                }
                default: {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
        }
        else if (this.LA(1) != ',') {
            if (this.LA(1) != ']') {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        Label_2270: {
            switch (this.LA(1)) {
                case ',': {
                    final int length10 = this.text.length();
                    this.match(',');
                    this.text.setLength(length10);
                    switch (this.LA(1)) {
                        case '\t':
                        case '\n':
                        case '\r':
                        case ' ': {
                            final int length11 = this.text.length();
                            this.mWS(false);
                            this.text.setLength(length11);
                            break;
                        }
                        case '\"':
                        case '#':
                        case '(':
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
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
                        case '[':
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
                    final int length12 = this.text.length();
                    this.mAST_CTOR_ELEMENT(true);
                    this.text.setLength(length12);
                    returnToken2 = this._returnToken;
                    switch (this.LA(1)) {
                        case '\t':
                        case '\n':
                        case '\r':
                        case ' ': {
                            final int length13 = this.text.length();
                            this.mWS(false);
                            this.text.setLength(length13);
                            break Label_2270;
                        }
                        case ']': {
                            break Label_2270;
                        }
                        default: {
                            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                        }
                    }
                    break;
                }
                case ']': {
                    break;
                }
                default: {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
        }
        final int length14 = this.text.length();
        this.match(']');
        this.text.setLength(length14);
        String s = returnToken3.getText();
        if (returnToken != null) {
            s = s + "," + returnToken.getText();
        }
        if (returnToken2 != null) {
            s = s + "," + returnToken2.getText();
        }
        this.text.setLength(length);
        this.text.append(this.generator.getASTCreateString(null, s));
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mTEXT_ARG(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 13;
        switch (this.LA(1)) {
            case '\t':
            case '\n':
            case '\r':
            case ' ': {
                this.mWS(false);
                break;
            }
            case '\"':
            case '$':
            case '\'':
            case '+':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
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
        int n2 = 0;
        while (ActionLexer._tokenSet_11.member(this.LA(1)) && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
            this.mTEXT_ARG_ELEMENT(false);
            if (ActionLexer._tokenSet_4.member(this.LA(1)) && ActionLexer._tokenSet_12.member(this.LA(2))) {
                this.mWS(false);
            }
            else if (!ActionLexer._tokenSet_12.member(this.LA(1))) {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
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
    
    protected final void mTREE_ELEMENT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 9;
        switch (this.LA(1)) {
            case '(': {
                this.mTREE(false);
                break;
            }
            case '[': {
                this.mAST_CONSTRUCTOR(false);
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
                this.mID_ELEMENT(false);
                break;
            }
            case '\"': {
                this.mSTRING(false);
                break;
            }
            default: {
                if (this.LA(1) == '#' && this.LA(2) == '(') {
                    final int length2 = this.text.length();
                    this.match('#');
                    this.text.setLength(length2);
                    this.mTREE(false);
                    break;
                }
                if (this.LA(1) == '#' && this.LA(2) == '[') {
                    final int length3 = this.text.length();
                    this.match('#');
                    this.text.setLength(length3);
                    this.mAST_CONSTRUCTOR(false);
                    break;
                }
                if (this.LA(1) == '#' && ActionLexer._tokenSet_3.member(this.LA(2))) {
                    final int length4 = this.text.length();
                    this.match('#');
                    this.text.setLength(length4);
                    final boolean mid_ELEMENT = this.mID_ELEMENT(true);
                    final Token returnToken = this._returnToken;
                    if (!mid_ELEMENT) {
                        final String mapTreeId = this.generator.mapTreeId(returnToken.getText(), null);
                        this.text.setLength(length);
                        this.text.append(mapTreeId);
                        break;
                    }
                    break;
                }
                else {
                    if (this.LA(1) == '#' && this.LA(2) == '#') {
                        this.match("##");
                        final String string = this.currentRule.getRuleName() + "_AST";
                        this.text.setLength(length);
                        this.text.append(string);
                        break;
                    }
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
                break;
            }
        }
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final boolean mID_ELEMENT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        boolean b2 = false;
        Token token = null;
        final int length = this.text.length();
        final int n = 12;
        this.mID(true);
        final Token returnToken = this._returnToken;
        if (ActionLexer._tokenSet_4.member(this.LA(1)) && ActionLexer._tokenSet_13.member(this.LA(2))) {
            final int length2 = this.text.length();
            this.mWS(false);
            this.text.setLength(length2);
        }
        else if (!ActionLexer._tokenSet_13.member(this.LA(1))) {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        switch (this.LA(1)) {
            case '(': {
                this.match('(');
                if (ActionLexer._tokenSet_4.member(this.LA(1)) && ActionLexer._tokenSet_14.member(this.LA(2)) && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                    final int length3 = this.text.length();
                    this.mWS(false);
                    this.text.setLength(length3);
                }
                else if (!ActionLexer._tokenSet_14.member(this.LA(1)) || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
                switch (this.LA(1)) {
                    case '\"':
                    case '#':
                    case '\'':
                    case '(':
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
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
                    case '[':
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
                        this.mARG(false);
                        while (this.LA(1) == ',') {
                            this.match(',');
                            switch (this.LA(1)) {
                                case '\t':
                                case '\n':
                                case '\r':
                                case ' ': {
                                    final int length4 = this.text.length();
                                    this.mWS(false);
                                    this.text.setLength(length4);
                                    break;
                                }
                                case '\"':
                                case '#':
                                case '\'':
                                case '(':
                                case '0':
                                case '1':
                                case '2':
                                case '3':
                                case '4':
                                case '5':
                                case '6':
                                case '7':
                                case '8':
                                case '9':
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
                                case '[':
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
                            this.mARG(false);
                        }
                        break;
                    }
                    case '\t':
                    case '\n':
                    case '\r':
                    case ' ':
                    case ')': {
                        break;
                    }
                    default: {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                }
                switch (this.LA(1)) {
                    case '\t':
                    case '\n':
                    case '\r':
                    case ' ': {
                        final int length5 = this.text.length();
                        this.mWS(false);
                        this.text.setLength(length5);
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
                break;
            }
            case '[': {
                int n2 = 0;
                while (this.LA(1) == '[') {
                    this.match('[');
                    switch (this.LA(1)) {
                        case '\t':
                        case '\n':
                        case '\r':
                        case ' ': {
                            final int length6 = this.text.length();
                            this.mWS(false);
                            this.text.setLength(length6);
                            break;
                        }
                        case '\"':
                        case '#':
                        case '\'':
                        case '(':
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
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
                        case '[':
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
                    this.mARG(false);
                    switch (this.LA(1)) {
                        case '\t':
                        case '\n':
                        case '\r':
                        case ' ': {
                            final int length7 = this.text.length();
                            this.mWS(false);
                            this.text.setLength(length7);
                            break;
                        }
                        case ']': {
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                        }
                    }
                    this.match(']');
                    ++n2;
                }
                if (n2 >= 1) {
                    break;
                }
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            case '.': {
                this.match('.');
                this.mID_ELEMENT(false);
                break;
            }
            case '\t':
            case '\n':
            case '\r':
            case ' ':
            case ')':
            case '*':
            case '+':
            case ',':
            case '-':
            case '/':
            case '=':
            case ']': {
                b2 = true;
                final String mapTreeId = this.generator.mapTreeId(returnToken.getText(), this.transInfo);
                this.text.setLength(length);
                this.text.append(mapTreeId);
                if (ActionLexer._tokenSet_15.member(this.LA(1)) && ActionLexer._tokenSet_16.member(this.LA(2)) && this.transInfo != null && this.transInfo.refRuleRoot != null) {
                    switch (this.LA(1)) {
                        case '\t':
                        case '\n':
                        case '\r':
                        case ' ': {
                            this.mWS(false);
                            break;
                        }
                        case '=': {
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                        }
                    }
                    this.mVAR_ASSIGN(false);
                    break;
                }
                if (ActionLexer._tokenSet_17.member(this.LA(1))) {
                    break;
                }
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
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
        return b2;
    }
    
    protected final void mAST_CTOR_ELEMENT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 11;
        if (this.LA(1) == '\"' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff' && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
            this.mSTRING(false);
        }
        else if (ActionLexer._tokenSet_18.member(this.LA(1)) && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff') {
            this.mTREE_ELEMENT(false);
        }
        else {
            if (this.LA(1) < '0' || this.LA(1) > '9') {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            this.mINT(false);
        }
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mINT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 26;
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
    
    protected final void mARG(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 16;
        switch (this.LA(1)) {
            case '\'': {
                this.mCHAR(false);
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
                this.mINT_OR_FLOAT(false);
                break;
            }
            default: {
                if (ActionLexer._tokenSet_18.member(this.LA(1)) && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff' && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                    this.mTREE_ELEMENT(false);
                    break;
                }
                if (this.LA(1) == '\"' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff' && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                    this.mSTRING(false);
                    break;
                }
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
        }
        while (ActionLexer._tokenSet_19.member(this.LA(1)) && ActionLexer._tokenSet_20.member(this.LA(2)) && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
            switch (this.LA(1)) {
                case '\t':
                case '\n':
                case '\r':
                case ' ': {
                    this.mWS(false);
                    break;
                }
                case '*':
                case '+':
                case '-':
                case '/': {
                    break;
                }
                default: {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
            switch (this.LA(1)) {
                case '+': {
                    this.match('+');
                    break;
                }
                case '-': {
                    this.match('-');
                    break;
                }
                case '*': {
                    this.match('*');
                    break;
                }
                case '/': {
                    this.match('/');
                    break;
                }
                default: {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
            }
            switch (this.LA(1)) {
                case '\t':
                case '\n':
                case '\r':
                case ' ': {
                    this.mWS(false);
                    break;
                }
                case '\"':
                case '#':
                case '\'':
                case '(':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
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
                case '[':
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
            this.mARG(false);
        }
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mTEXT_ARG_ELEMENT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 14;
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
                this.mTEXT_ARG_ID_ELEMENT(false);
                break;
            }
            case '\"': {
                this.mSTRING(false);
                break;
            }
            case '\'': {
                this.mCHAR(false);
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
                this.mINT_OR_FLOAT(false);
                break;
            }
            case '$': {
                this.mTEXT_ITEM(false);
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
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mTEXT_ARG_ID_ELEMENT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 15;
        this.mID(true);
        final Token returnToken = this._returnToken;
        if (ActionLexer._tokenSet_4.member(this.LA(1)) && ActionLexer._tokenSet_21.member(this.LA(2))) {
            final int length2 = this.text.length();
            this.mWS(false);
            this.text.setLength(length2);
        }
        else if (!ActionLexer._tokenSet_21.member(this.LA(1))) {
            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
        }
        switch (this.LA(1)) {
            case '(': {
                this.match('(');
                if (ActionLexer._tokenSet_4.member(this.LA(1)) && ActionLexer._tokenSet_22.member(this.LA(2)) && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                    final int length3 = this.text.length();
                    this.mWS(false);
                    this.text.setLength(length3);
                }
                else if (!ActionLexer._tokenSet_22.member(this.LA(1)) || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff') {
                    throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                }
                while (ActionLexer._tokenSet_23.member(this.LA(1)) && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff' && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                    this.mTEXT_ARG(false);
                    while (this.LA(1) == ',') {
                        this.match(',');
                        this.mTEXT_ARG(false);
                    }
                }
                switch (this.LA(1)) {
                    case '\t':
                    case '\n':
                    case '\r':
                    case ' ': {
                        final int length4 = this.text.length();
                        this.mWS(false);
                        this.text.setLength(length4);
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
                break;
            }
            case '[': {
                int n2 = 0;
                while (this.LA(1) == '[') {
                    this.match('[');
                    if (ActionLexer._tokenSet_4.member(this.LA(1)) && ActionLexer._tokenSet_23.member(this.LA(2)) && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                        final int length5 = this.text.length();
                        this.mWS(false);
                        this.text.setLength(length5);
                    }
                    else if (!ActionLexer._tokenSet_23.member(this.LA(1)) || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff' || this.LA(3) < '\u0003' || this.LA(3) > '\u00ff') {
                        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                    }
                    this.mTEXT_ARG(false);
                    switch (this.LA(1)) {
                        case '\t':
                        case '\n':
                        case '\r':
                        case ' ': {
                            final int length6 = this.text.length();
                            this.mWS(false);
                            this.text.setLength(length6);
                            break;
                        }
                        case ']': {
                            break;
                        }
                        default: {
                            throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
                        }
                    }
                    this.match(']');
                    ++n2;
                }
                if (n2 >= 1) {
                    break;
                }
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            case '.': {
                this.match('.');
                this.mTEXT_ARG_ID_ELEMENT(false);
                break;
            }
            case '\t':
            case '\n':
            case '\r':
            case ' ':
            case '\"':
            case '$':
            case '\'':
            case ')':
            case '+':
            case ',':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
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
            case ']':
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
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    protected final void mINT_OR_FLOAT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 27;
        int n2 = 0;
        while (this.LA(1) >= '0' && this.LA(1) <= '9' && ActionLexer._tokenSet_24.member(this.LA(2))) {
            this.mDIGIT(false);
            ++n2;
        }
        if (n2 >= 1) {
            if (this.LA(1) == 'L' && ActionLexer._tokenSet_25.member(this.LA(2))) {
                this.match('L');
            }
            else if (this.LA(1) == 'l' && ActionLexer._tokenSet_25.member(this.LA(2))) {
                this.match('l');
            }
            else if (this.LA(1) == '.') {
                this.match('.');
                while (this.LA(1) >= '0' && this.LA(1) <= '9' && ActionLexer._tokenSet_25.member(this.LA(2))) {
                    this.mDIGIT(false);
                }
            }
            else if (!ActionLexer._tokenSet_25.member(this.LA(1))) {
                throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
            }
            if (b && token == null && n != -1) {
                token = this.makeToken(n);
                token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
            }
            this._returnToken = token;
            return;
        }
        throw new NoViableAltForCharException(this.LA(1), this.getFilename(), this.getLine(), this.getColumn());
    }
    
    protected final void mSL_COMMENT(final boolean b) throws RecognitionException, CharStreamException, TokenStreamException {
        Token token = null;
        final int length = this.text.length();
        final int n = 20;
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
        final int n = 21;
        this.match("/*");
        while (true) {
            while (this.LA(1) != '*' || this.LA(2) != '/') {
                if (this.LA(1) == '\r' && this.LA(2) == '\n' && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                    this.match('\r');
                    this.match('\n');
                    this.newline();
                }
                else if (this.LA(1) == '\r' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff' && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                    this.match('\r');
                    this.newline();
                }
                else if (this.LA(1) == '\n' && this.LA(2) >= '\u0003' && this.LA(2) <= '\u00ff' && this.LA(3) >= '\u0003' && this.LA(3) <= '\u00ff') {
                    this.match('\n');
                    this.newline();
                }
                else {
                    if (this.LA(1) < '\u0003' || this.LA(1) > '\u00ff' || this.LA(2) < '\u0003' || this.LA(2) > '\u00ff' || this.LA(3) < '\u0003' || this.LA(3) > '\u00ff') {
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
        final int n = 24;
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
        final int n = 25;
        this.matchRange('0', '9');
        if (b && token == null && n != -1) {
            token = this.makeToken(n);
            token.setText(new String(this.text.getBuffer(), length, this.text.length() - length));
        }
        this._returnToken = token;
    }
    
    private static final long[] mk_tokenSet_0() {
        final long[] array = new long[8];
        array[0] = -103079215112L;
        for (int i = 1; i <= 3; ++i) {
            array[i] = -1L;
        }
        return array;
    }
    
    private static final long[] mk_tokenSet_1() {
        final long[] array = new long[8];
        array[0] = -145135534866440L;
        for (int i = 1; i <= 3; ++i) {
            array[i] = -1L;
        }
        return array;
    }
    
    private static final long[] mk_tokenSet_2() {
        final long[] array = new long[8];
        array[0] = -141407503262728L;
        for (int i = 1; i <= 3; ++i) {
            array[i] = -1L;
        }
        return array;
    }
    
    private static final long[] mk_tokenSet_3() {
        return new long[] { 0L, 576460745995190270L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_4() {
        return new long[] { 4294977024L, 0L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_5() {
        return new long[] { 1103806604800L, 0L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_6() {
        return new long[] { 287959436729787904L, 576460745995190270L, 0L, 0L, 0L };
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
        return new long[] { 287948901175001088L, 576460745995190270L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_10() {
        return new long[] { 287950056521213440L, 576460746129407998L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_11() {
        return new long[] { 287958332923183104L, 576460745995190270L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_12() {
        return new long[] { 287978128427460096L, 576460746532061182L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_13() {
        return new long[] { 2306123388973753856L, 671088640L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_14() {
        return new long[] { 287952805300282880L, 576460746129407998L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_15() {
        return new long[] { 2305843013508670976L, 0L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_16() {
        return new long[] { 2306051920717948416L, 536870912L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_17() {
        return new long[] { 208911504254464L, 536870912L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_18() {
        return new long[] { 1151051235328L, 576460746129407998L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_19() {
        return new long[] { 189120294954496L, 0L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_20() {
        return new long[] { 288139722277004800L, 576460746129407998L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_21() {
        return new long[] { 288049596683265536L, 576460746666278910L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_22() {
        return new long[] { 287960536241415680L, 576460745995190270L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_23() {
        return new long[] { 287958337218160128L, 576460745995190270L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_24() {
        return new long[] { 288228817078593024L, 576460746532061182L, 0L, 0L, 0L };
    }
    
    private static final long[] mk_tokenSet_25() {
        return new long[] { 288158448334415360L, 576460746532061182L, 0L, 0L, 0L };
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
        _tokenSet_13 = new BitSet(mk_tokenSet_13());
        _tokenSet_14 = new BitSet(mk_tokenSet_14());
        _tokenSet_15 = new BitSet(mk_tokenSet_15());
        _tokenSet_16 = new BitSet(mk_tokenSet_16());
        _tokenSet_17 = new BitSet(mk_tokenSet_17());
        _tokenSet_18 = new BitSet(mk_tokenSet_18());
        _tokenSet_19 = new BitSet(mk_tokenSet_19());
        _tokenSet_20 = new BitSet(mk_tokenSet_20());
        _tokenSet_21 = new BitSet(mk_tokenSet_21());
        _tokenSet_22 = new BitSet(mk_tokenSet_22());
        _tokenSet_23 = new BitSet(mk_tokenSet_23());
        _tokenSet_24 = new BitSet(mk_tokenSet_24());
        _tokenSet_25 = new BitSet(mk_tokenSet_25());
    }
}
