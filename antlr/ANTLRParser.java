// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.BitSet;

public class ANTLRParser extends LLkParser implements ANTLRTokenTypes
{
    private static final boolean DEBUG_PARSER = false;
    ANTLRGrammarParseBehavior behavior;
    Tool antlrTool;
    protected int blockNesting;
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
    public static final BitSet _tokenSet_9;
    public static final BitSet _tokenSet_10;
    public static final BitSet _tokenSet_11;
    
    public ANTLRParser(final TokenBuffer tokenBuffer, final ANTLRGrammarParseBehavior behavior, final Tool antlrTool) {
        super(tokenBuffer, 1);
        this.blockNesting = -1;
        this.tokenNames = ANTLRParser._tokenNames;
        this.behavior = behavior;
        this.antlrTool = antlrTool;
    }
    
    public void reportError(final String s) {
        this.antlrTool.error(s, this.getFilename(), -1, -1);
    }
    
    public void reportError(final RecognitionException ex) {
        this.reportError(ex, ex.getErrorMessage());
    }
    
    public void reportError(final RecognitionException ex, final String s) {
        this.antlrTool.error(s, ex.getFilename(), ex.getLine(), ex.getColumn());
    }
    
    public void reportWarning(final String s) {
        this.antlrTool.warning(s, this.getFilename(), -1, -1);
    }
    
    private boolean lastInRule() throws TokenStreamException {
        return this.blockNesting == 0 && (this.LA(1) == 16 || this.LA(1) == 39 || this.LA(1) == 21);
    }
    
    private void checkForMissingEndRule(final Token token) {
        if (token.getColumn() == 1) {
            this.antlrTool.warning("did you forget to terminate previous rule?", this.getFilename(), token.getLine(), token.getColumn());
        }
    }
    
    protected ANTLRParser(final TokenBuffer tokenBuffer, final int n) {
        super(tokenBuffer, n);
        this.blockNesting = -1;
        this.tokenNames = ANTLRParser._tokenNames;
    }
    
    public ANTLRParser(final TokenBuffer tokenBuffer) {
        this(tokenBuffer, 2);
    }
    
    protected ANTLRParser(final TokenStream tokenStream, final int n) {
        super(tokenStream, n);
        this.blockNesting = -1;
        this.tokenNames = ANTLRParser._tokenNames;
    }
    
    public ANTLRParser(final TokenStream tokenStream) {
        this(tokenStream, 2);
    }
    
    public ANTLRParser(final ParserSharedInputState parserSharedInputState) {
        super(parserSharedInputState, 2);
        this.blockNesting = -1;
        this.tokenNames = ANTLRParser._tokenNames;
    }
    
    public final void grammar() throws RecognitionException, TokenStreamException {
        Token lt = null;
        try {
            while (this.LA(1) == 5) {
                if (this.inputState.guessing == 0) {
                    lt = null;
                }
                this.match(5);
                switch (this.LA(1)) {
                    case 6: {
                        lt = this.LT(1);
                        this.match(6);
                        break;
                    }
                    case 7: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                final Token lt2 = this.LT(1);
                this.match(7);
                if (this.inputState.guessing == 0) {
                    this.behavior.refHeaderAction(lt, lt2);
                }
            }
            switch (this.LA(1)) {
                case 14: {
                    this.fileOptionsSpec();
                    break;
                }
                case 1:
                case 7:
                case 8:
                case 9:
                case 10: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            while (this.LA(1) >= 7 && this.LA(1) <= 10) {
                this.classDef();
            }
            this.match(1);
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing != 0) {
                throw ex;
            }
            this.reportError(ex, "rule grammar trapped:\n" + ex.toString());
            this.consumeUntil(1);
        }
    }
    
    public final void fileOptionsSpec() throws RecognitionException, TokenStreamException {
        this.match(14);
        while (this.LA(1) == 24 || this.LA(1) == 41) {
            final Token id = this.id();
            this.match(15);
            final Token optionValue = this.optionValue();
            if (this.inputState.guessing == 0) {
                this.behavior.setFileOption(id, optionValue, this.getInputState().filename);
            }
            this.match(16);
        }
        this.match(17);
    }
    
    public final void classDef() throws RecognitionException, TokenStreamException {
        String text = null;
        try {
            switch (this.LA(1)) {
                case 7: {
                    final Token lt = this.LT(1);
                    this.match(7);
                    if (this.inputState.guessing == 0) {
                        this.behavior.refPreambleAction(lt);
                        break;
                    }
                    break;
                }
                case 8:
                case 9:
                case 10: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            switch (this.LA(1)) {
                case 8: {
                    final Token lt2 = this.LT(1);
                    this.match(8);
                    if (this.inputState.guessing == 0) {
                        text = lt2.getText();
                        break;
                    }
                    break;
                }
                case 9:
                case 10: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            boolean b = false;
            if ((this.LA(1) == 9 || this.LA(1) == 10) && (this.LA(2) == 24 || this.LA(2) == 41)) {
                final int mark = this.mark();
                b = true;
                final ParserSharedInputState inputState = this.inputState;
                ++inputState.guessing;
                try {
                    switch (this.LA(1)) {
                        case 9: {
                            this.match(9);
                            break;
                        }
                        case 10: {
                            this.match(10);
                            this.id();
                            this.match(11);
                            this.match(12);
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                }
                catch (RecognitionException ex2) {
                    b = false;
                }
                this.rewind(mark);
                final ParserSharedInputState inputState2 = this.inputState;
                --inputState2.guessing;
            }
            if (b) {
                this.lexerSpec(text);
            }
            else {
                boolean b2 = false;
                if (this.LA(1) == 10 && (this.LA(2) == 24 || this.LA(2) == 41)) {
                    final int mark2 = this.mark();
                    b2 = true;
                    final ParserSharedInputState inputState3 = this.inputState;
                    ++inputState3.guessing;
                    try {
                        this.match(10);
                        this.id();
                        this.match(11);
                        this.match(13);
                    }
                    catch (RecognitionException ex3) {
                        b2 = false;
                    }
                    this.rewind(mark2);
                    final ParserSharedInputState inputState4 = this.inputState;
                    --inputState4.guessing;
                }
                if (b2) {
                    this.treeParserSpec(text);
                }
                else {
                    if (this.LA(1) != 10 || (this.LA(2) != 24 && this.LA(2) != 41)) {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                    this.parserSpec(text);
                }
            }
            this.rules();
            if (this.inputState.guessing == 0) {
                this.behavior.endGrammar();
            }
        }
        catch (RecognitionException ex) {
            if (this.inputState.guessing != 0) {
                throw ex;
            }
            if (ex instanceof NoViableAltException) {
                if (((NoViableAltException)ex).token.getType() == 8) {
                    this.reportError(ex, "JAVADOC comments may only prefix rules and grammars");
                }
                else {
                    this.reportError(ex, "rule classDef trapped:\n" + ex.toString());
                }
            }
            else {
                this.reportError(ex, "rule classDef trapped:\n" + ex.toString());
            }
            this.behavior.abortGrammar();
            int i = 1;
            while (i != 0) {
                this.consume();
                switch (this.LA(1)) {
                    case 1:
                    case 9:
                    case 10: {
                        i = 0;
                        continue;
                    }
                }
            }
        }
    }
    
    public final Token id() throws RecognitionException, TokenStreamException {
        Token token = null;
        switch (this.LA(1)) {
            case 24: {
                final Token lt = this.LT(1);
                this.match(24);
                if (this.inputState.guessing == 0) {
                    token = lt;
                    break;
                }
                break;
            }
            case 41: {
                final Token lt2 = this.LT(1);
                this.match(41);
                if (this.inputState.guessing == 0) {
                    token = lt2;
                    break;
                }
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        return token;
    }
    
    public final void lexerSpec(final String s) throws RecognitionException, TokenStreamException {
        String superClass = null;
        Token token = null;
        Label_0194: {
            switch (this.LA(1)) {
                case 9: {
                    final Token lt = this.LT(1);
                    this.match(9);
                    token = this.id();
                    if (this.inputState.guessing == 0) {
                        this.antlrTool.warning("lexclass' is deprecated; use 'class X extends Lexer'", this.getFilename(), lt.getLine(), lt.getColumn());
                        break;
                    }
                    break;
                }
                case 10: {
                    this.match(10);
                    token = this.id();
                    this.match(11);
                    this.match(12);
                    switch (this.LA(1)) {
                        case 27: {
                            superClass = this.superClass();
                            break Label_0194;
                        }
                        case 16: {
                            break Label_0194;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.startLexer(this.getFilename(), token, superClass, s);
        }
        this.match(16);
        switch (this.LA(1)) {
            case 14: {
                this.lexerOptionsSpec();
                break;
            }
            case 7:
            case 8:
            case 23:
            case 24:
            case 30:
            case 31:
            case 32:
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.endOptions();
        }
        switch (this.LA(1)) {
            case 23: {
                this.tokensSpec();
                break;
            }
            case 7:
            case 8:
            case 24:
            case 30:
            case 31:
            case 32:
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 7: {
                final Token lt2 = this.LT(1);
                this.match(7);
                if (this.inputState.guessing == 0) {
                    this.behavior.refMemberAction(lt2);
                    break;
                }
                break;
            }
            case 8:
            case 24:
            case 30:
            case 31:
            case 32:
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
    }
    
    public final void treeParserSpec(final String s) throws RecognitionException, TokenStreamException {
        String superClass = null;
        this.match(10);
        final Token id = this.id();
        this.match(11);
        this.match(13);
        switch (this.LA(1)) {
            case 27: {
                superClass = this.superClass();
                break;
            }
            case 16: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.startTreeWalker(this.getFilename(), id, superClass, s);
        }
        this.match(16);
        switch (this.LA(1)) {
            case 14: {
                this.treeParserOptionsSpec();
                break;
            }
            case 7:
            case 8:
            case 23:
            case 24:
            case 30:
            case 31:
            case 32:
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.endOptions();
        }
        switch (this.LA(1)) {
            case 23: {
                this.tokensSpec();
                break;
            }
            case 7:
            case 8:
            case 24:
            case 30:
            case 31:
            case 32:
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 7: {
                final Token lt = this.LT(1);
                this.match(7);
                if (this.inputState.guessing == 0) {
                    this.behavior.refMemberAction(lt);
                    break;
                }
                break;
            }
            case 8:
            case 24:
            case 30:
            case 31:
            case 32:
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
    }
    
    public final void parserSpec(final String s) throws RecognitionException, TokenStreamException {
        String superClass = null;
        this.match(10);
        final Token id = this.id();
        Label_0172: {
            switch (this.LA(1)) {
                case 11: {
                    this.match(11);
                    this.match(29);
                    switch (this.LA(1)) {
                        case 27: {
                            superClass = this.superClass();
                            break Label_0172;
                        }
                        case 16: {
                            break Label_0172;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    break;
                }
                case 16: {
                    if (this.inputState.guessing == 0) {
                        this.antlrTool.warning("use 'class X extends Parser'", this.getFilename(), id.getLine(), id.getColumn());
                        break;
                    }
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.startParser(this.getFilename(), id, superClass, s);
        }
        this.match(16);
        switch (this.LA(1)) {
            case 14: {
                this.parserOptionsSpec();
                break;
            }
            case 7:
            case 8:
            case 23:
            case 24:
            case 30:
            case 31:
            case 32:
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.endOptions();
        }
        switch (this.LA(1)) {
            case 23: {
                this.tokensSpec();
                break;
            }
            case 7:
            case 8:
            case 24:
            case 30:
            case 31:
            case 32:
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 7: {
                final Token lt = this.LT(1);
                this.match(7);
                if (this.inputState.guessing == 0) {
                    this.behavior.refMemberAction(lt);
                    break;
                }
                break;
            }
            case 8:
            case 24:
            case 30:
            case 31:
            case 32:
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
    }
    
    public final void rules() throws RecognitionException, TokenStreamException {
        int n = 0;
        while (ANTLRParser._tokenSet_0.member(this.LA(1)) && ANTLRParser._tokenSet_1.member(this.LA(2))) {
            this.rule();
            ++n;
        }
        if (n >= 1) {
            return;
        }
        throw new NoViableAltException(this.LT(1), this.getFilename());
    }
    
    public final Token optionValue() throws RecognitionException, TokenStreamException {
        Token qualifiedID = null;
        switch (this.LA(1)) {
            case 24:
            case 41: {
                qualifiedID = this.qualifiedID();
                break;
            }
            case 6: {
                final Token lt = this.LT(1);
                this.match(6);
                if (this.inputState.guessing == 0) {
                    qualifiedID = lt;
                    break;
                }
                break;
            }
            case 19: {
                final Token lt2 = this.LT(1);
                this.match(19);
                if (this.inputState.guessing == 0) {
                    qualifiedID = lt2;
                    break;
                }
                break;
            }
            case 20: {
                final Token lt3 = this.LT(1);
                this.match(20);
                if (this.inputState.guessing == 0) {
                    qualifiedID = lt3;
                    break;
                }
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        return qualifiedID;
    }
    
    public final void parserOptionsSpec() throws RecognitionException, TokenStreamException {
        this.match(14);
        while (this.LA(1) == 24 || this.LA(1) == 41) {
            final Token id = this.id();
            this.match(15);
            final Token optionValue = this.optionValue();
            if (this.inputState.guessing == 0) {
                this.behavior.setGrammarOption(id, optionValue);
            }
            this.match(16);
        }
        this.match(17);
    }
    
    public final void treeParserOptionsSpec() throws RecognitionException, TokenStreamException {
        this.match(14);
        while (this.LA(1) == 24 || this.LA(1) == 41) {
            final Token id = this.id();
            this.match(15);
            final Token optionValue = this.optionValue();
            if (this.inputState.guessing == 0) {
                this.behavior.setGrammarOption(id, optionValue);
            }
            this.match(16);
        }
        this.match(17);
    }
    
    public final void lexerOptionsSpec() throws RecognitionException, TokenStreamException {
        this.match(14);
        while (true) {
            switch (this.LA(1)) {
                case 18: {
                    this.match(18);
                    this.match(15);
                    final BitSet charSet = this.charSet();
                    this.match(16);
                    if (this.inputState.guessing == 0) {
                        this.behavior.setCharVocabulary(charSet);
                        continue;
                    }
                    continue;
                }
                case 24:
                case 41: {
                    final Token id = this.id();
                    this.match(15);
                    final Token optionValue = this.optionValue();
                    if (this.inputState.guessing == 0) {
                        this.behavior.setGrammarOption(id, optionValue);
                    }
                    this.match(16);
                    continue;
                }
                default: {
                    this.match(17);
                }
            }
        }
    }
    
    public final BitSet charSet() throws RecognitionException, TokenStreamException {
        final BitSet setBlockElement = this.setBlockElement();
        while (this.LA(1) == 21) {
            this.match(21);
            final BitSet setBlockElement2 = this.setBlockElement();
            if (this.inputState.guessing == 0) {
                setBlockElement.orInPlace(setBlockElement2);
            }
        }
        return setBlockElement;
    }
    
    public final void subruleOptionsSpec() throws RecognitionException, TokenStreamException {
        this.match(14);
        while (this.LA(1) == 24 || this.LA(1) == 41) {
            final Token id = this.id();
            this.match(15);
            final Token optionValue = this.optionValue();
            if (this.inputState.guessing == 0) {
                this.behavior.setSubruleOption(id, optionValue);
            }
            this.match(16);
        }
        this.match(17);
    }
    
    public final Token qualifiedID() throws RecognitionException, TokenStreamException {
        Token token = null;
        final StringBuffer sb = new StringBuffer(30);
        Token token2 = this.id();
        if (this.inputState.guessing == 0) {
            sb.append(token2.getText());
        }
        while (this.LA(1) == 50) {
            this.match(50);
            token2 = this.id();
            if (this.inputState.guessing == 0) {
                sb.append('.');
                sb.append(token2.getText());
            }
        }
        if (this.inputState.guessing == 0) {
            token = new CommonToken(24, sb.toString());
            token.setLine(token2.getLine());
        }
        return token;
    }
    
    public final BitSet setBlockElement() throws RecognitionException, TokenStreamException {
        BitSet of = null;
        int tokenTypeForCharLiteral = 0;
        final Token lt = this.LT(1);
        this.match(19);
        if (this.inputState.guessing == 0) {
            tokenTypeForCharLiteral = ANTLRLexer.tokenTypeForCharLiteral(lt.getText());
            of = BitSet.of(tokenTypeForCharLiteral);
        }
        switch (this.LA(1)) {
            case 22: {
                this.match(22);
                final Token lt2 = this.LT(1);
                this.match(19);
                if (this.inputState.guessing == 0) {
                    final int tokenTypeForCharLiteral2 = ANTLRLexer.tokenTypeForCharLiteral(lt2.getText());
                    if (tokenTypeForCharLiteral2 < tokenTypeForCharLiteral) {
                        this.antlrTool.error("Malformed range line ", this.getFilename(), lt.getLine(), lt.getColumn());
                    }
                    for (int i = tokenTypeForCharLiteral + 1; i <= tokenTypeForCharLiteral2; ++i) {
                        of.add(i);
                    }
                    break;
                }
                break;
            }
            case 16:
            case 21: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        return of;
    }
    
    public final void tokensSpec() throws RecognitionException, TokenStreamException {
        Token lt = null;
        this.match(23);
        int n = 0;
        while (this.LA(1) == 6 || this.LA(1) == 24) {
            Label_0361: {
                switch (this.LA(1)) {
                    case 24: {
                        if (this.inputState.guessing == 0) {
                            lt = null;
                        }
                        final Token lt2 = this.LT(1);
                        this.match(24);
                        switch (this.LA(1)) {
                            case 15: {
                                this.match(15);
                                lt = this.LT(1);
                                this.match(6);
                                break;
                            }
                            case 16:
                            case 25: {
                                break;
                            }
                            default: {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                        if (this.inputState.guessing == 0) {
                            this.behavior.defineToken(lt2, lt);
                        }
                        switch (this.LA(1)) {
                            case 25: {
                                this.tokensSpecOptions(lt2);
                                break Label_0361;
                            }
                            case 16: {
                                break Label_0361;
                            }
                            default: {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                        break;
                    }
                    case 6: {
                        final Token lt3 = this.LT(1);
                        this.match(6);
                        if (this.inputState.guessing == 0) {
                            this.behavior.defineToken(null, lt3);
                        }
                        switch (this.LA(1)) {
                            case 25: {
                                this.tokensSpecOptions(lt3);
                                break Label_0361;
                            }
                            case 16: {
                                break Label_0361;
                            }
                            default: {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
            }
            this.match(16);
            ++n;
        }
        if (n >= 1) {
            this.match(17);
            return;
        }
        throw new NoViableAltException(this.LT(1), this.getFilename());
    }
    
    public final void tokensSpecOptions(final Token token) throws RecognitionException, TokenStreamException {
        this.match(25);
        final Token id = this.id();
        this.match(15);
        final Token optionValue = this.optionValue();
        if (this.inputState.guessing == 0) {
            this.behavior.refTokensSpecElementOption(token, id, optionValue);
        }
        while (this.LA(1) == 16) {
            this.match(16);
            final Token id2 = this.id();
            this.match(15);
            final Token optionValue2 = this.optionValue();
            if (this.inputState.guessing == 0) {
                this.behavior.refTokensSpecElementOption(token, id2, optionValue2);
            }
        }
        this.match(26);
    }
    
    public final String superClass() throws RecognitionException, TokenStreamException {
        String stripFrontBack = null;
        this.match(27);
        if (this.inputState.guessing == 0) {
            stripFrontBack = StringUtils.stripFrontBack(this.LT(1).getText(), "\"", "\"");
        }
        this.match(6);
        this.match(28);
        return stripFrontBack;
    }
    
    public final void rule() throws RecognitionException, TokenStreamException {
        String s = "public";
        String text = null;
        boolean b = true;
        this.blockNesting = -1;
        switch (this.LA(1)) {
            case 8: {
                final Token lt = this.LT(1);
                this.match(8);
                if (this.inputState.guessing == 0) {
                    text = lt.getText();
                    break;
                }
                break;
            }
            case 24:
            case 30:
            case 31:
            case 32:
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 30: {
                final Token lt2 = this.LT(1);
                this.match(30);
                if (this.inputState.guessing == 0) {
                    s = lt2.getText();
                    break;
                }
                break;
            }
            case 31: {
                final Token lt3 = this.LT(1);
                this.match(31);
                if (this.inputState.guessing == 0) {
                    s = lt3.getText();
                    break;
                }
                break;
            }
            case 32: {
                final Token lt4 = this.LT(1);
                this.match(32);
                if (this.inputState.guessing == 0) {
                    s = lt4.getText();
                    break;
                }
                break;
            }
            case 24:
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        final Token id = this.id();
        switch (this.LA(1)) {
            case 33: {
                this.match(33);
                if (this.inputState.guessing == 0) {
                    b = false;
                    break;
                }
                break;
            }
            case 7:
            case 14:
            case 34:
            case 35:
            case 36:
            case 37: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.defineRuleName(id, s, b, text);
        }
        switch (this.LA(1)) {
            case 34: {
                final Token lt5 = this.LT(1);
                this.match(34);
                if (this.inputState.guessing == 0) {
                    this.behavior.refArgAction(lt5);
                    break;
                }
                break;
            }
            case 7:
            case 14:
            case 35:
            case 36:
            case 37: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 35: {
                this.match(35);
                final Token lt6 = this.LT(1);
                this.match(34);
                if (this.inputState.guessing == 0) {
                    this.behavior.refReturnAction(lt6);
                    break;
                }
                break;
            }
            case 7:
            case 14:
            case 36:
            case 37: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 37: {
                this.throwsSpec();
                break;
            }
            case 7:
            case 14:
            case 36: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 14: {
                this.ruleOptionsSpec();
                break;
            }
            case 7:
            case 36: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        switch (this.LA(1)) {
            case 7: {
                final Token lt7 = this.LT(1);
                this.match(7);
                if (this.inputState.guessing == 0) {
                    this.behavior.refInitAction(lt7);
                    break;
                }
                break;
            }
            case 36: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        this.match(36);
        this.block();
        this.match(16);
        switch (this.LA(1)) {
            case 39: {
                this.exceptionGroup();
                break;
            }
            case 1:
            case 7:
            case 8:
            case 9:
            case 10:
            case 24:
            case 30:
            case 31:
            case 32:
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.endRule(id.getText());
        }
    }
    
    public final void throwsSpec() throws RecognitionException, TokenStreamException {
        String s = null;
        this.match(37);
        final Token id = this.id();
        if (this.inputState.guessing == 0) {
            s = id.getText();
        }
        while (this.LA(1) == 38) {
            this.match(38);
            final Token id2 = this.id();
            if (this.inputState.guessing == 0) {
                s = s + "," + id2.getText();
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.setUserExceptions(s);
        }
    }
    
    public final void ruleOptionsSpec() throws RecognitionException, TokenStreamException {
        this.match(14);
        while (this.LA(1) == 24 || this.LA(1) == 41) {
            final Token id = this.id();
            this.match(15);
            final Token optionValue = this.optionValue();
            if (this.inputState.guessing == 0) {
                this.behavior.setRuleOption(id, optionValue);
            }
            this.match(16);
        }
        this.match(17);
    }
    
    public final void block() throws RecognitionException, TokenStreamException {
        if (this.inputState.guessing == 0) {
            ++this.blockNesting;
        }
        this.alternative();
        while (this.LA(1) == 21) {
            this.match(21);
            this.alternative();
        }
        if (this.inputState.guessing == 0) {
            --this.blockNesting;
        }
    }
    
    public final void exceptionGroup() throws RecognitionException, TokenStreamException {
        if (this.inputState.guessing == 0) {
            this.behavior.beginExceptionGroup();
        }
        int n = 0;
        while (this.LA(1) == 39) {
            this.exceptionSpec();
            ++n;
        }
        if (n >= 1) {
            if (this.inputState.guessing == 0) {
                this.behavior.endExceptionGroup();
            }
            return;
        }
        throw new NoViableAltException(this.LT(1), this.getFilename());
    }
    
    public final void alternative() throws RecognitionException, TokenStreamException {
        boolean b = true;
        switch (this.LA(1)) {
            case 33: {
                this.match(33);
                if (this.inputState.guessing == 0) {
                    b = false;
                    break;
                }
                break;
            }
            case 6:
            case 7:
            case 16:
            case 19:
            case 21:
            case 24:
            case 27:
            case 28:
            case 39:
            case 41:
            case 42:
            case 43:
            case 44:
            case 50: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.beginAlt(b);
        }
        while (ANTLRParser._tokenSet_2.member(this.LA(1))) {
            this.element();
        }
        switch (this.LA(1)) {
            case 39: {
                this.exceptionSpecNoLabel();
                break;
            }
            case 16:
            case 21:
            case 28: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.endAlt();
        }
    }
    
    public final void element() throws RecognitionException, TokenStreamException {
        this.elementNoOptionSpec();
        switch (this.LA(1)) {
            case 25: {
                this.elementOptionSpec();
                break;
            }
            case 6:
            case 7:
            case 16:
            case 19:
            case 21:
            case 24:
            case 27:
            case 28:
            case 39:
            case 41:
            case 42:
            case 43:
            case 44:
            case 50: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
    }
    
    public final void exceptionSpecNoLabel() throws RecognitionException, TokenStreamException {
        this.match(39);
        if (this.inputState.guessing == 0) {
            this.behavior.beginExceptionSpec(null);
        }
        while (this.LA(1) == 40) {
            this.exceptionHandler();
        }
        if (this.inputState.guessing == 0) {
            this.behavior.endExceptionSpec();
        }
    }
    
    public final void exceptionSpec() throws RecognitionException, TokenStreamException {
        Token token = null;
        this.match(39);
        switch (this.LA(1)) {
            case 34: {
                final Token lt = this.LT(1);
                this.match(34);
                if (this.inputState.guessing == 0) {
                    token = lt;
                    break;
                }
                break;
            }
            case 1:
            case 7:
            case 8:
            case 9:
            case 10:
            case 24:
            case 30:
            case 31:
            case 32:
            case 39:
            case 40:
            case 41: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.beginExceptionSpec(token);
        }
        while (this.LA(1) == 40) {
            this.exceptionHandler();
        }
        if (this.inputState.guessing == 0) {
            this.behavior.endExceptionSpec();
        }
    }
    
    public final void exceptionHandler() throws RecognitionException, TokenStreamException {
        this.match(40);
        final Token lt = this.LT(1);
        this.match(34);
        final Token lt2 = this.LT(1);
        this.match(7);
        if (this.inputState.guessing == 0) {
            this.behavior.refExceptionHandler(lt, lt2);
        }
    }
    
    public final void elementNoOptionSpec() throws RecognitionException, TokenStreamException {
        Token token = null;
        final Token token2 = null;
        Token token3 = null;
        int n = 1;
        Label_2110: {
            switch (this.LA(1)) {
                case 7: {
                    final Token lt = this.LT(1);
                    this.match(7);
                    if (this.inputState.guessing == 0) {
                        this.behavior.refAction(lt);
                        break;
                    }
                    break;
                }
                case 43: {
                    final Token lt2 = this.LT(1);
                    this.match(43);
                    if (this.inputState.guessing == 0) {
                        this.behavior.refSemPred(lt2);
                        break;
                    }
                    break;
                }
                case 44: {
                    this.tree();
                    break;
                }
                default: {
                    if ((this.LA(1) == 24 || this.LA(1) == 41) && this.LA(2) == 15) {
                        final Token id = this.id();
                        this.match(15);
                        if ((this.LA(1) == 24 || this.LA(1) == 41) && this.LA(2) == 36) {
                            token = this.id();
                            this.match(36);
                            if (this.inputState.guessing == 0) {
                                this.checkForMissingEndRule(token);
                            }
                        }
                        else if ((this.LA(1) != 24 && this.LA(1) != 41) || !ANTLRParser._tokenSet_3.member(this.LA(2))) {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                        switch (this.LA(1)) {
                            case 41: {
                                final Token lt3 = this.LT(1);
                                this.match(41);
                                switch (this.LA(1)) {
                                    case 34: {
                                        final Token lt4 = this.LT(1);
                                        this.match(34);
                                        if (this.inputState.guessing == 0) {
                                            token3 = lt4;
                                            break;
                                        }
                                        break;
                                    }
                                    case 6:
                                    case 7:
                                    case 16:
                                    case 19:
                                    case 21:
                                    case 24:
                                    case 25:
                                    case 27:
                                    case 28:
                                    case 33:
                                    case 39:
                                    case 41:
                                    case 42:
                                    case 43:
                                    case 44:
                                    case 50: {
                                        break;
                                    }
                                    default: {
                                        throw new NoViableAltException(this.LT(1), this.getFilename());
                                    }
                                }
                                switch (this.LA(1)) {
                                    case 33: {
                                        this.match(33);
                                        if (this.inputState.guessing == 0) {
                                            n = 3;
                                            break;
                                        }
                                        break;
                                    }
                                    case 6:
                                    case 7:
                                    case 16:
                                    case 19:
                                    case 21:
                                    case 24:
                                    case 25:
                                    case 27:
                                    case 28:
                                    case 39:
                                    case 41:
                                    case 42:
                                    case 43:
                                    case 44:
                                    case 50: {
                                        break;
                                    }
                                    default: {
                                        throw new NoViableAltException(this.LT(1), this.getFilename());
                                    }
                                }
                                if (this.inputState.guessing == 0) {
                                    this.behavior.refRule(id, lt3, token, token3, n);
                                    break Label_2110;
                                }
                                break Label_2110;
                            }
                            case 24: {
                                final Token lt5 = this.LT(1);
                                this.match(24);
                                switch (this.LA(1)) {
                                    case 34: {
                                        final Token lt6 = this.LT(1);
                                        this.match(34);
                                        if (this.inputState.guessing == 0) {
                                            token3 = lt6;
                                            break;
                                        }
                                        break;
                                    }
                                    case 6:
                                    case 7:
                                    case 16:
                                    case 19:
                                    case 21:
                                    case 24:
                                    case 25:
                                    case 27:
                                    case 28:
                                    case 39:
                                    case 41:
                                    case 42:
                                    case 43:
                                    case 44:
                                    case 50: {
                                        break;
                                    }
                                    default: {
                                        throw new NoViableAltException(this.LT(1), this.getFilename());
                                    }
                                }
                                if (this.inputState.guessing == 0) {
                                    this.behavior.refToken(id, lt5, token, token3, false, n, this.lastInRule());
                                    break Label_2110;
                                }
                                break Label_2110;
                            }
                            default: {
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                    }
                    else {
                        if (!ANTLRParser._tokenSet_4.member(this.LA(1)) || !ANTLRParser._tokenSet_5.member(this.LA(2))) {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                        if ((this.LA(1) == 24 || this.LA(1) == 41) && this.LA(2) == 36) {
                            token = this.id();
                            this.match(36);
                            if (this.inputState.guessing == 0) {
                                this.checkForMissingEndRule(token);
                            }
                        }
                        else if (!ANTLRParser._tokenSet_4.member(this.LA(1)) || !ANTLRParser._tokenSet_6.member(this.LA(2))) {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                        switch (this.LA(1)) {
                            case 41: {
                                final Token lt7 = this.LT(1);
                                this.match(41);
                                switch (this.LA(1)) {
                                    case 34: {
                                        final Token lt8 = this.LT(1);
                                        this.match(34);
                                        if (this.inputState.guessing == 0) {
                                            token3 = lt8;
                                            break;
                                        }
                                        break;
                                    }
                                    case 6:
                                    case 7:
                                    case 16:
                                    case 19:
                                    case 21:
                                    case 24:
                                    case 25:
                                    case 27:
                                    case 28:
                                    case 33:
                                    case 39:
                                    case 41:
                                    case 42:
                                    case 43:
                                    case 44:
                                    case 50: {
                                        break;
                                    }
                                    default: {
                                        throw new NoViableAltException(this.LT(1), this.getFilename());
                                    }
                                }
                                switch (this.LA(1)) {
                                    case 33: {
                                        this.match(33);
                                        if (this.inputState.guessing == 0) {
                                            n = 3;
                                            break;
                                        }
                                        break;
                                    }
                                    case 6:
                                    case 7:
                                    case 16:
                                    case 19:
                                    case 21:
                                    case 24:
                                    case 25:
                                    case 27:
                                    case 28:
                                    case 39:
                                    case 41:
                                    case 42:
                                    case 43:
                                    case 44:
                                    case 50: {
                                        break;
                                    }
                                    default: {
                                        throw new NoViableAltException(this.LT(1), this.getFilename());
                                    }
                                }
                                if (this.inputState.guessing == 0) {
                                    this.behavior.refRule(token2, lt7, token, token3, n);
                                    break Label_2110;
                                }
                                break Label_2110;
                            }
                            case 42: {
                                this.match(42);
                                switch (this.LA(1)) {
                                    case 19:
                                    case 24: {
                                        this.notTerminal(token);
                                        break Label_2110;
                                    }
                                    case 27: {
                                        this.ebnf(token, true);
                                        break Label_2110;
                                    }
                                    default: {
                                        throw new NoViableAltException(this.LT(1), this.getFilename());
                                    }
                                }
                                break;
                            }
                            case 27: {
                                this.ebnf(token, false);
                                break Label_2110;
                            }
                            default: {
                                if ((this.LA(1) == 6 || this.LA(1) == 19 || this.LA(1) == 24) && this.LA(2) == 22) {
                                    this.range(token);
                                    break Label_2110;
                                }
                                if (ANTLRParser._tokenSet_7.member(this.LA(1)) && ANTLRParser._tokenSet_8.member(this.LA(2))) {
                                    this.terminal(token);
                                    break Label_2110;
                                }
                                throw new NoViableAltException(this.LT(1), this.getFilename());
                            }
                        }
                    }
                    break;
                }
            }
        }
    }
    
    public final void elementOptionSpec() throws RecognitionException, TokenStreamException {
        this.match(25);
        final Token id = this.id();
        this.match(15);
        final Token optionValue = this.optionValue();
        if (this.inputState.guessing == 0) {
            this.behavior.refElementOption(id, optionValue);
        }
        while (this.LA(1) == 16) {
            this.match(16);
            final Token id2 = this.id();
            this.match(15);
            final Token optionValue2 = this.optionValue();
            if (this.inputState.guessing == 0) {
                this.behavior.refElementOption(id2, optionValue2);
            }
        }
        this.match(26);
    }
    
    public final void range(final Token token) throws RecognitionException, TokenStreamException {
        Token token2 = null;
        Token token3 = null;
        int n = 1;
        switch (this.LA(1)) {
            case 19: {
                final Token lt = this.LT(1);
                this.match(19);
                this.match(22);
                final Token lt2 = this.LT(1);
                this.match(19);
                switch (this.LA(1)) {
                    case 33: {
                        this.match(33);
                        if (this.inputState.guessing == 0) {
                            n = 3;
                            break;
                        }
                        break;
                    }
                    case 6:
                    case 7:
                    case 16:
                    case 19:
                    case 21:
                    case 24:
                    case 25:
                    case 27:
                    case 28:
                    case 39:
                    case 41:
                    case 42:
                    case 43:
                    case 44:
                    case 50: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                if (this.inputState.guessing == 0) {
                    this.behavior.refCharRange(lt, lt2, token, n, this.lastInRule());
                    break;
                }
                break;
            }
            case 6:
            case 24: {
                switch (this.LA(1)) {
                    case 24: {
                        final Token lt3 = this.LT(1);
                        this.match(24);
                        if (this.inputState.guessing == 0) {
                            token2 = lt3;
                            break;
                        }
                        break;
                    }
                    case 6: {
                        final Token lt4 = this.LT(1);
                        this.match(6);
                        if (this.inputState.guessing == 0) {
                            token2 = lt4;
                            break;
                        }
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                this.match(22);
                switch (this.LA(1)) {
                    case 24: {
                        final Token lt5 = this.LT(1);
                        this.match(24);
                        if (this.inputState.guessing == 0) {
                            token3 = lt5;
                            break;
                        }
                        break;
                    }
                    case 6: {
                        final Token lt6 = this.LT(1);
                        this.match(6);
                        if (this.inputState.guessing == 0) {
                            token3 = lt6;
                            break;
                        }
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                final int ast_type_spec = this.ast_type_spec();
                if (this.inputState.guessing == 0) {
                    this.behavior.refTokenRange(token2, token3, token, ast_type_spec, this.lastInRule());
                    break;
                }
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
    }
    
    public final void terminal(final Token token) throws RecognitionException, TokenStreamException {
        int n = 1;
        Token token2 = null;
        switch (this.LA(1)) {
            case 19: {
                final Token lt = this.LT(1);
                this.match(19);
                switch (this.LA(1)) {
                    case 33: {
                        this.match(33);
                        if (this.inputState.guessing == 0) {
                            n = 3;
                            break;
                        }
                        break;
                    }
                    case 6:
                    case 7:
                    case 16:
                    case 19:
                    case 21:
                    case 24:
                    case 25:
                    case 27:
                    case 28:
                    case 39:
                    case 41:
                    case 42:
                    case 43:
                    case 44:
                    case 50: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                if (this.inputState.guessing == 0) {
                    this.behavior.refCharLiteral(lt, token, false, n, this.lastInRule());
                    break;
                }
                break;
            }
            case 24: {
                final Token lt2 = this.LT(1);
                this.match(24);
                final int ast_type_spec = this.ast_type_spec();
                switch (this.LA(1)) {
                    case 34: {
                        final Token lt3 = this.LT(1);
                        this.match(34);
                        if (this.inputState.guessing == 0) {
                            token2 = lt3;
                            break;
                        }
                        break;
                    }
                    case 6:
                    case 7:
                    case 16:
                    case 19:
                    case 21:
                    case 24:
                    case 25:
                    case 27:
                    case 28:
                    case 39:
                    case 41:
                    case 42:
                    case 43:
                    case 44:
                    case 50: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                if (this.inputState.guessing == 0) {
                    this.behavior.refToken(null, lt2, token, token2, false, ast_type_spec, this.lastInRule());
                    break;
                }
                break;
            }
            case 6: {
                final Token lt4 = this.LT(1);
                this.match(6);
                final int ast_type_spec2 = this.ast_type_spec();
                if (this.inputState.guessing == 0) {
                    this.behavior.refStringLiteral(lt4, token, ast_type_spec2, this.lastInRule());
                    break;
                }
                break;
            }
            case 50: {
                final Token lt5 = this.LT(1);
                this.match(50);
                final int ast_type_spec3 = this.ast_type_spec();
                if (this.inputState.guessing == 0) {
                    this.behavior.refWildcard(lt5, token, ast_type_spec3);
                    break;
                }
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
    }
    
    public final void notTerminal(final Token token) throws RecognitionException, TokenStreamException {
        int n = 1;
        switch (this.LA(1)) {
            case 19: {
                final Token lt = this.LT(1);
                this.match(19);
                switch (this.LA(1)) {
                    case 33: {
                        this.match(33);
                        if (this.inputState.guessing == 0) {
                            n = 3;
                            break;
                        }
                        break;
                    }
                    case 6:
                    case 7:
                    case 16:
                    case 19:
                    case 21:
                    case 24:
                    case 25:
                    case 27:
                    case 28:
                    case 39:
                    case 41:
                    case 42:
                    case 43:
                    case 44:
                    case 50: {
                        break;
                    }
                    default: {
                        throw new NoViableAltException(this.LT(1), this.getFilename());
                    }
                }
                if (this.inputState.guessing == 0) {
                    this.behavior.refCharLiteral(lt, token, true, n, this.lastInRule());
                    break;
                }
                break;
            }
            case 24: {
                final Token lt2 = this.LT(1);
                this.match(24);
                final int ast_type_spec = this.ast_type_spec();
                if (this.inputState.guessing == 0) {
                    this.behavior.refToken(null, lt2, token, null, true, ast_type_spec, this.lastInRule());
                    break;
                }
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
    }
    
    public final void ebnf(final Token token, final boolean b) throws RecognitionException, TokenStreamException {
        final Token lt = this.LT(1);
        this.match(27);
        if (this.inputState.guessing == 0) {
            this.behavior.beginSubRule(token, lt, b);
        }
        if (this.LA(1) == 14) {
            this.subruleOptionsSpec();
            switch (this.LA(1)) {
                case 7: {
                    final Token lt2 = this.LT(1);
                    this.match(7);
                    if (this.inputState.guessing == 0) {
                        this.behavior.refInitAction(lt2);
                        break;
                    }
                    break;
                }
                case 36: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            this.match(36);
        }
        else if (this.LA(1) == 7 && this.LA(2) == 36) {
            final Token lt3 = this.LT(1);
            this.match(7);
            if (this.inputState.guessing == 0) {
                this.behavior.refInitAction(lt3);
            }
            this.match(36);
        }
        else if (!ANTLRParser._tokenSet_9.member(this.LA(1)) || !ANTLRParser._tokenSet_10.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.block();
        this.match(28);
        Label_1073: {
            switch (this.LA(1)) {
                case 6:
                case 7:
                case 16:
                case 19:
                case 21:
                case 24:
                case 25:
                case 27:
                case 28:
                case 33:
                case 39:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 50: {
                    switch (this.LA(1)) {
                        case 45: {
                            this.match(45);
                            if (this.inputState.guessing == 0) {
                                this.behavior.optionalSubRule();
                                break;
                            }
                            break;
                        }
                        case 46: {
                            this.match(46);
                            if (this.inputState.guessing == 0) {
                                this.behavior.zeroOrMoreSubRule();
                                break;
                            }
                            break;
                        }
                        case 47: {
                            this.match(47);
                            if (this.inputState.guessing == 0) {
                                this.behavior.oneOrMoreSubRule();
                                break;
                            }
                            break;
                        }
                        case 6:
                        case 7:
                        case 16:
                        case 19:
                        case 21:
                        case 24:
                        case 25:
                        case 27:
                        case 28:
                        case 33:
                        case 39:
                        case 41:
                        case 42:
                        case 43:
                        case 44:
                        case 50: {
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    switch (this.LA(1)) {
                        case 33: {
                            this.match(33);
                            if (this.inputState.guessing == 0) {
                                this.behavior.noASTSubRule();
                                break Label_1073;
                            }
                            break Label_1073;
                        }
                        case 6:
                        case 7:
                        case 16:
                        case 19:
                        case 21:
                        case 24:
                        case 25:
                        case 27:
                        case 28:
                        case 39:
                        case 41:
                        case 42:
                        case 43:
                        case 44:
                        case 50: {
                            break Label_1073;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    break;
                }
                case 48: {
                    this.match(48);
                    if (this.inputState.guessing == 0) {
                        this.behavior.synPred();
                        break;
                    }
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
        }
        if (this.inputState.guessing == 0) {
            this.behavior.endSubRule();
        }
    }
    
    public final void tree() throws RecognitionException, TokenStreamException {
        final Token lt = this.LT(1);
        this.match(44);
        if (this.inputState.guessing == 0) {
            this.behavior.beginTree(lt);
        }
        this.rootNode();
        if (this.inputState.guessing == 0) {
            this.behavior.beginChildList();
        }
        int n = 0;
        while (ANTLRParser._tokenSet_2.member(this.LA(1))) {
            this.element();
            ++n;
        }
        if (n >= 1) {
            if (this.inputState.guessing == 0) {
                this.behavior.endChildList();
            }
            this.match(28);
            if (this.inputState.guessing == 0) {
                this.behavior.endTree();
            }
            return;
        }
        throw new NoViableAltException(this.LT(1), this.getFilename());
    }
    
    public final void rootNode() throws RecognitionException, TokenStreamException {
        Token id = null;
        if ((this.LA(1) == 24 || this.LA(1) == 41) && this.LA(2) == 36) {
            id = this.id();
            this.match(36);
            if (this.inputState.guessing == 0) {
                this.checkForMissingEndRule(id);
            }
        }
        else if (!ANTLRParser._tokenSet_7.member(this.LA(1)) || !ANTLRParser._tokenSet_11.member(this.LA(2))) {
            throw new NoViableAltException(this.LT(1), this.getFilename());
        }
        this.terminal(id);
    }
    
    public final int ast_type_spec() throws RecognitionException, TokenStreamException {
        int n = 1;
        switch (this.LA(1)) {
            case 49: {
                this.match(49);
                if (this.inputState.guessing == 0) {
                    n = 2;
                    break;
                }
                break;
            }
            case 33: {
                this.match(33);
                if (this.inputState.guessing == 0) {
                    n = 3;
                    break;
                }
                break;
            }
            case 6:
            case 7:
            case 16:
            case 19:
            case 21:
            case 24:
            case 25:
            case 27:
            case 28:
            case 34:
            case 39:
            case 41:
            case 42:
            case 43:
            case 44:
            case 50: {
                break;
            }
            default: {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        return n;
    }
    
    private static final long[] mk_tokenSet_0() {
        return new long[] { 2206556225792L, 0L };
    }
    
    private static final long[] mk_tokenSet_1() {
        return new long[] { 2472844214400L, 0L };
    }
    
    private static final long[] mk_tokenSet_2() {
        return new long[] { 1158885407195328L, 0L };
    }
    
    private static final long[] mk_tokenSet_3() {
        return new long[] { 1159461236965568L, 0L };
    }
    
    private static final long[] mk_tokenSet_4() {
        return new long[] { 1132497128128576L, 0L };
    }
    
    private static final long[] mk_tokenSet_5() {
        return new long[] { 1722479914074304L, 0L };
    }
    
    private static final long[] mk_tokenSet_6() {
        return new long[] { 1722411194597568L, 0L };
    }
    
    private static final long[] mk_tokenSet_7() {
        return new long[] { 1125899924144192L, 0L };
    }
    
    private static final long[] mk_tokenSet_8() {
        return new long[] { 1722411190386880L, 0L };
    }
    
    private static final long[] mk_tokenSet_9() {
        return new long[] { 1159444023476416L, 0L };
    }
    
    private static final long[] mk_tokenSet_10() {
        return new long[] { 2251345007067328L, 0L };
    }
    
    private static final long[] mk_tokenSet_11() {
        return new long[] { 1721861130420416L, 0L };
    }
    
    static {
        _tokenNames = new String[] { "<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "\"tokens\"", "\"header\"", "STRING_LITERAL", "ACTION", "DOC_COMMENT", "\"lexclass\"", "\"class\"", "\"extends\"", "\"Lexer\"", "\"TreeParser\"", "OPTIONS", "ASSIGN", "SEMI", "RCURLY", "\"charVocabulary\"", "CHAR_LITERAL", "INT", "OR", "RANGE", "TOKENS", "TOKEN_REF", "OPEN_ELEMENT_OPTION", "CLOSE_ELEMENT_OPTION", "LPAREN", "RPAREN", "\"Parser\"", "\"protected\"", "\"public\"", "\"private\"", "BANG", "ARG_ACTION", "\"returns\"", "COLON", "\"throws\"", "COMMA", "\"exception\"", "\"catch\"", "RULE_REF", "NOT_OP", "SEMPRED", "TREE_BEGIN", "QUESTION", "STAR", "PLUS", "IMPLIES", "CARET", "WILDCARD", "\"options\"", "WS", "COMMENT", "SL_COMMENT", "ML_COMMENT", "ESC", "DIGIT", "XDIGIT", "NESTED_ARG_ACTION", "NESTED_ACTION", "WS_LOOP", "INTERNAL_RULE_REF", "WS_OPT" };
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
    }
}
