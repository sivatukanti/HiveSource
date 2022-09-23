// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.Vector;
import antlr.collections.impl.LList;
import antlr.collections.Stack;

public class MakeGrammar extends DefineGrammarSymbols
{
    protected Stack blocks;
    protected RuleRefElement lastRuleRef;
    protected RuleEndElement ruleEnd;
    protected RuleBlock ruleBlock;
    protected int nested;
    protected boolean grammarError;
    ExceptionSpec currentExceptionSpec;
    
    public MakeGrammar(final Tool tool, final String[] array, final LLkAnalyzer lLkAnalyzer) {
        super(tool, array, lLkAnalyzer);
        this.blocks = new LList();
        this.nested = 0;
        this.grammarError = false;
        this.currentExceptionSpec = null;
    }
    
    public void abortGrammar() {
        String className = "unknown grammar";
        if (this.grammar != null) {
            className = this.grammar.getClassName();
        }
        this.tool.error("aborting grammar '" + className + "' due to errors");
        super.abortGrammar();
    }
    
    protected void addElementToCurrentAlt(final AlternativeElement alternativeElement) {
        alternativeElement.enclosingRuleName = this.ruleBlock.ruleName;
        this.context().addAlternativeElement(alternativeElement);
    }
    
    public void beginAlt(final boolean autoGen) {
        super.beginAlt(autoGen);
        final Alternative alternative = new Alternative();
        alternative.setAutoGen(autoGen);
        this.context().block.addAlternative(alternative);
    }
    
    public void beginChildList() {
        super.beginChildList();
        this.context().block.addAlternative(new Alternative());
    }
    
    public void beginExceptionGroup() {
        super.beginExceptionGroup();
        if (!(this.context().block instanceof RuleBlock)) {
            this.tool.panic("beginExceptionGroup called outside of rule block");
        }
    }
    
    public void beginExceptionSpec(final Token token) {
        if (token != null) {
            token.setText(StringUtils.stripFront(StringUtils.stripBack(token.getText(), " \n\r\t"), " \n\r\t"));
        }
        super.beginExceptionSpec(token);
        this.currentExceptionSpec = new ExceptionSpec(token);
    }
    
    public void beginSubRule(final Token token, final Token token2, final boolean b) {
        super.beginSubRule(token, token2, b);
        this.blocks.push(new BlockContext());
        this.context().block = new AlternativeBlock(this.grammar, token2, b);
        this.context().altNum = 0;
        ++this.nested;
        this.context().blockEnd = new BlockEndElement(this.grammar);
        this.context().blockEnd.block = this.context().block;
        this.labelElement(this.context().block, token);
    }
    
    public void beginTree(final Token token) throws SemanticException {
        if (!(this.grammar instanceof TreeWalkerGrammar)) {
            this.tool.error("Trees only allowed in TreeParser", this.grammar.getFilename(), token.getLine(), token.getColumn());
            throw new SemanticException("Trees only allowed in TreeParser");
        }
        super.beginTree(token);
        this.blocks.push(new TreeBlockContext());
        this.context().block = new TreeElement(this.grammar, token);
        this.context().altNum = 0;
    }
    
    public BlockContext context() {
        if (this.blocks.height() == 0) {
            return null;
        }
        return (BlockContext)this.blocks.top();
    }
    
    public static RuleBlock createNextTokenRule(final Grammar grammar, final Vector vector, final String s) {
        final RuleBlock block = new RuleBlock(grammar, s);
        block.setDefaultErrorHandler(grammar.getDefaultErrorHandler());
        final RuleEndElement ruleEndElement = new RuleEndElement(grammar);
        block.setEndElement(ruleEndElement);
        ruleEndElement.block = block;
        for (int i = 0; i < vector.size(); ++i) {
            final RuleSymbol ruleSymbol = (RuleSymbol)vector.elementAt(i);
            if (!ruleSymbol.isDefined()) {
                grammar.antlrTool.error("Lexer rule " + ruleSymbol.id.substring(1) + " is not defined");
            }
            else if (ruleSymbol.access.equals("public")) {
                final Alternative alternative = new Alternative();
                final Vector alternatives = ruleSymbol.getBlock().getAlternatives();
                if (alternatives != null && alternatives.size() == 1) {
                    final Alternative alternative2 = (Alternative)alternatives.elementAt(0);
                    if (alternative2.semPred != null) {
                        alternative.semPred = alternative2.semPred;
                    }
                }
                final RuleRefElement ruleRefElement = new RuleRefElement(grammar, new CommonToken(41, ruleSymbol.getId()), 1);
                ruleRefElement.setLabel("theRetToken");
                ruleRefElement.enclosingRuleName = "nextToken";
                ruleRefElement.next = ruleEndElement;
                alternative.addElement(ruleRefElement);
                alternative.setAutoGen(true);
                block.addAlternative(alternative);
                ruleSymbol.addReference(ruleRefElement);
            }
        }
        block.setAutoGen(true);
        block.prepareForAnalysis();
        return block;
    }
    
    private AlternativeBlock createOptionalRuleRef(final String s, final Token token) {
        final AlternativeBlock block = new AlternativeBlock(this.grammar, token, false);
        final String encodeLexerRuleName = CodeGenerator.encodeLexerRuleName(s);
        if (!this.grammar.isDefined(encodeLexerRuleName)) {
            this.grammar.define(new RuleSymbol(encodeLexerRuleName));
        }
        final CommonToken commonToken = new CommonToken(24, s);
        commonToken.setLine(token.getLine());
        commonToken.setLine(token.getColumn());
        final RuleRefElement ruleRefElement = new RuleRefElement(this.grammar, commonToken, 1);
        ruleRefElement.enclosingRuleName = this.ruleBlock.ruleName;
        final BlockEndElement blockEndElement = new BlockEndElement(this.grammar);
        blockEndElement.block = block;
        final Alternative alternative = new Alternative(ruleRefElement);
        alternative.addElement(blockEndElement);
        block.addAlternative(alternative);
        final Alternative alternative2 = new Alternative();
        alternative2.addElement(blockEndElement);
        block.addAlternative(alternative2);
        block.prepareForAnalysis();
        return block;
    }
    
    public void defineRuleName(final Token token, final String s, final boolean b, final String s2) throws SemanticException {
        if (token.type == 24) {
            if (!(this.grammar instanceof LexerGrammar)) {
                this.tool.error("Lexical rule " + token.getText() + " defined outside of lexer", this.grammar.getFilename(), token.getLine(), token.getColumn());
                token.setText(token.getText().toLowerCase());
            }
        }
        else if (this.grammar instanceof LexerGrammar) {
            this.tool.error("Lexical rule names must be upper case, '" + token.getText() + "' is not", this.grammar.getFilename(), token.getLine(), token.getColumn());
            token.setText(token.getText().toUpperCase());
        }
        super.defineRuleName(token, s, b, s2);
        String s3 = token.getText();
        if (token.type == 24) {
            s3 = CodeGenerator.encodeLexerRuleName(s3);
        }
        final RuleSymbol ruleSymbol = (RuleSymbol)this.grammar.getSymbol(s3);
        final RuleBlock ruleBlock = new RuleBlock(this.grammar, token.getText(), token.getLine(), b);
        ruleBlock.setDefaultErrorHandler(this.grammar.getDefaultErrorHandler());
        this.ruleBlock = ruleBlock;
        this.blocks.push(new BlockContext());
        ruleSymbol.setBlock((RuleBlock)(this.context().block = ruleBlock));
        ruleBlock.setEndElement(this.ruleEnd = new RuleEndElement(this.grammar));
        this.nested = 0;
    }
    
    public void endAlt() {
        super.endAlt();
        if (this.nested == 0) {
            this.addElementToCurrentAlt(this.ruleEnd);
        }
        else {
            this.addElementToCurrentAlt(this.context().blockEnd);
        }
        final BlockContext context = this.context();
        ++context.altNum;
    }
    
    public void endChildList() {
        super.endChildList();
        final BlockEndElement blockEndElement = new BlockEndElement(this.grammar);
        blockEndElement.block = this.context().block;
        this.addElementToCurrentAlt(blockEndElement);
    }
    
    public void endExceptionGroup() {
        super.endExceptionGroup();
    }
    
    public void endExceptionSpec() {
        super.endExceptionSpec();
        if (this.currentExceptionSpec == null) {
            this.tool.panic("exception processing internal error -- no active exception spec");
        }
        if (this.context().block instanceof RuleBlock) {
            ((RuleBlock)this.context().block).addExceptionSpec(this.currentExceptionSpec);
        }
        else if (this.context().currentAlt().exceptionSpec != null) {
            this.tool.error("Alternative already has an exception specification", this.grammar.getFilename(), this.context().block.getLine(), this.context().block.getColumn());
        }
        else {
            this.context().currentAlt().exceptionSpec = this.currentExceptionSpec;
        }
        this.currentExceptionSpec = null;
    }
    
    public void endGrammar() {
        if (this.grammarError) {
            this.abortGrammar();
        }
        else {
            super.endGrammar();
        }
    }
    
    public void endRule(final String s) {
        super.endRule(s);
        (this.ruleEnd.block = ((BlockContext)this.blocks.pop()).block).prepareForAnalysis();
    }
    
    public void endSubRule() {
        super.endSubRule();
        --this.nested;
        final BlockContext blockContext = (BlockContext)this.blocks.pop();
        final AlternativeBlock block = blockContext.block;
        if (block.not && !(block instanceof SynPredBlock) && !(block instanceof ZeroOrMoreBlock) && !(block instanceof OneOrMoreBlock) && !this.analyzer.subruleCanBeInverted(block, this.grammar instanceof LexerGrammar)) {
            final String property = System.getProperty("line.separator");
            this.tool.error("This subrule cannot be inverted.  Only subrules of the form:" + property + "    (T1|T2|T3...) or" + property + "    ('c1'|'c2'|'c3'...)" + property + "may be inverted (ranges are also allowed).", this.grammar.getFilename(), block.getLine(), block.getColumn());
        }
        if (block instanceof SynPredBlock) {
            final SynPredBlock synPred = (SynPredBlock)block;
            this.context().block.hasASynPred = true;
            this.context().currentAlt().synPred = synPred;
            this.grammar.hasSyntacticPredicate = true;
            synPred.removeTrackingOfRuleRefs(this.grammar);
        }
        else {
            this.addElementToCurrentAlt(block);
        }
        blockContext.blockEnd.block.prepareForAnalysis();
    }
    
    public void endTree() {
        super.endTree();
        this.addElementToCurrentAlt(((BlockContext)this.blocks.pop()).block);
    }
    
    public void hasError() {
        this.grammarError = true;
    }
    
    private void labelElement(final AlternativeElement alternativeElement, final Token token) {
        if (token != null) {
            for (int i = 0; i < this.ruleBlock.labeledElements.size(); ++i) {
                final String label = ((AlternativeElement)this.ruleBlock.labeledElements.elementAt(i)).getLabel();
                if (label != null && label.equals(token.getText())) {
                    this.tool.error("Label '" + token.getText() + "' has already been defined", this.grammar.getFilename(), token.getLine(), token.getColumn());
                    return;
                }
            }
            alternativeElement.setLabel(token.getText());
            this.ruleBlock.labeledElements.appendElement(alternativeElement);
        }
    }
    
    public void noAutoGenSubRule() {
        this.context().block.setAutoGen(false);
    }
    
    public void oneOrMoreSubRule() {
        if (this.context().block.not) {
            this.tool.error("'~' cannot be applied to (...)* subrule", this.grammar.getFilename(), this.context().block.getLine(), this.context().block.getColumn());
        }
        final OneOrMoreBlock oneOrMoreBlock = new OneOrMoreBlock(this.grammar);
        setBlock(oneOrMoreBlock, this.context().block);
        final BlockContext blockContext = (BlockContext)this.blocks.pop();
        this.blocks.push(new BlockContext());
        this.context().block = oneOrMoreBlock;
        this.context().blockEnd = blockContext.blockEnd;
        this.context().blockEnd.block = oneOrMoreBlock;
    }
    
    public void optionalSubRule() {
        if (this.context().block.not) {
            this.tool.error("'~' cannot be applied to (...)? subrule", this.grammar.getFilename(), this.context().block.getLine(), this.context().block.getColumn());
        }
        this.beginAlt(false);
        this.endAlt();
    }
    
    public void refAction(final Token token) {
        super.refAction(token);
        this.context().block.hasAnAction = true;
        this.addElementToCurrentAlt(new ActionElement(this.grammar, token));
    }
    
    public void setUserExceptions(final String throwsSpec) {
        ((RuleBlock)this.context().block).throwsSpec = throwsSpec;
    }
    
    public void refArgAction(final Token token) {
        ((RuleBlock)this.context().block).argAction = token.getText();
    }
    
    public void refCharLiteral(final Token token, final Token token2, final boolean b, final int n, final boolean b2) {
        if (!(this.grammar instanceof LexerGrammar)) {
            this.tool.error("Character literal only valid in lexer", this.grammar.getFilename(), token.getLine(), token.getColumn());
            return;
        }
        super.refCharLiteral(token, token2, b, n, b2);
        final CharLiteralElement charLiteralElement = new CharLiteralElement((LexerGrammar)this.grammar, token, b, n);
        if (!((LexerGrammar)this.grammar).caseSensitive && charLiteralElement.getType() < 128 && Character.toLowerCase((char)charLiteralElement.getType()) != (char)charLiteralElement.getType()) {
            this.tool.warning("Character literal must be lowercase when caseSensitive=false", this.grammar.getFilename(), token.getLine(), token.getColumn());
        }
        this.addElementToCurrentAlt(charLiteralElement);
        this.labelElement(charLiteralElement, token2);
        final String ignoreRule = this.ruleBlock.getIgnoreRule();
        if (!b2 && ignoreRule != null) {
            this.addElementToCurrentAlt(this.createOptionalRuleRef(ignoreRule, token));
        }
    }
    
    public void refCharRange(final Token token, final Token token2, final Token token3, final int n, final boolean b) {
        if (!(this.grammar instanceof LexerGrammar)) {
            this.tool.error("Character range only valid in lexer", this.grammar.getFilename(), token.getLine(), token.getColumn());
            return;
        }
        final int tokenTypeForCharLiteral = ANTLRLexer.tokenTypeForCharLiteral(token.getText());
        final int tokenTypeForCharLiteral2 = ANTLRLexer.tokenTypeForCharLiteral(token2.getText());
        if (tokenTypeForCharLiteral2 < tokenTypeForCharLiteral) {
            this.tool.error("Malformed range.", this.grammar.getFilename(), token.getLine(), token.getColumn());
            return;
        }
        if (!((LexerGrammar)this.grammar).caseSensitive) {
            if (tokenTypeForCharLiteral < 128 && Character.toLowerCase((char)tokenTypeForCharLiteral) != (char)tokenTypeForCharLiteral) {
                this.tool.warning("Character literal must be lowercase when caseSensitive=false", this.grammar.getFilename(), token.getLine(), token.getColumn());
            }
            if (tokenTypeForCharLiteral2 < 128 && Character.toLowerCase((char)tokenTypeForCharLiteral2) != (char)tokenTypeForCharLiteral2) {
                this.tool.warning("Character literal must be lowercase when caseSensitive=false", this.grammar.getFilename(), token2.getLine(), token2.getColumn());
            }
        }
        super.refCharRange(token, token2, token3, n, b);
        final CharRangeElement charRangeElement = new CharRangeElement((LexerGrammar)this.grammar, token, token2, n);
        this.addElementToCurrentAlt(charRangeElement);
        this.labelElement(charRangeElement, token3);
        final String ignoreRule = this.ruleBlock.getIgnoreRule();
        if (!b && ignoreRule != null) {
            this.addElementToCurrentAlt(this.createOptionalRuleRef(ignoreRule, token));
        }
    }
    
    public void refTokensSpecElementOption(final Token token, final Token token2, final Token token3) {
        final TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbol(token.getText());
        if (tokenSymbol == null) {
            this.tool.panic("cannot find " + token.getText() + "in tokens {...}");
        }
        if (token2.getText().equals("AST")) {
            tokenSymbol.setASTNodeType(token3.getText());
        }
        else {
            this.grammar.antlrTool.error("invalid tokens {...} element option:" + token2.getText(), this.grammar.getFilename(), token2.getLine(), token2.getColumn());
        }
    }
    
    public void refElementOption(final Token token, final Token token2) {
        final AlternativeElement currentElement = this.context().currentElement();
        if (currentElement instanceof StringLiteralElement || currentElement instanceof TokenRefElement || currentElement instanceof WildcardElement) {
            ((GrammarAtom)currentElement).setOption(token, token2);
        }
        else {
            this.tool.error("cannot use element option (" + token.getText() + ") for this kind of element", this.grammar.getFilename(), token.getLine(), token.getColumn());
        }
    }
    
    public void refExceptionHandler(final Token token, final Token token2) {
        super.refExceptionHandler(token, token2);
        if (this.currentExceptionSpec == null) {
            this.tool.panic("exception handler processing internal error");
        }
        this.currentExceptionSpec.addHandler(new ExceptionHandler(token, token2));
    }
    
    public void refInitAction(final Token token) {
        super.refAction(token);
        this.context().block.setInitAction(token.getText());
    }
    
    public void refMemberAction(final Token classMemberAction) {
        this.grammar.classMemberAction = classMemberAction;
    }
    
    public void refPreambleAction(final Token token) {
        super.refPreambleAction(token);
    }
    
    public void refReturnAction(final Token token) {
        if (this.grammar instanceof LexerGrammar && ((RuleSymbol)this.grammar.getSymbol(CodeGenerator.encodeLexerRuleName(((RuleBlock)this.context().block).getRuleName()))).access.equals("public")) {
            this.tool.warning("public Lexical rules cannot specify return type", this.grammar.getFilename(), token.getLine(), token.getColumn());
            return;
        }
        ((RuleBlock)this.context().block).returnAction = token.getText();
    }
    
    public void refRule(final Token token, final Token token2, final Token token3, final Token token4, final int n) {
        if (this.grammar instanceof LexerGrammar) {
            if (token2.type != 24) {
                this.tool.error("Parser rule " + token2.getText() + " referenced in lexer");
                return;
            }
            if (n == 2) {
                this.tool.error("AST specification ^ not allowed in lexer", this.grammar.getFilename(), token2.getLine(), token2.getColumn());
            }
        }
        super.refRule(token, token2, token3, token4, n);
        this.lastRuleRef = new RuleRefElement(this.grammar, token2, n);
        if (token4 != null) {
            this.lastRuleRef.setArgs(token4.getText());
        }
        if (token != null) {
            this.lastRuleRef.setIdAssign(token.getText());
        }
        this.addElementToCurrentAlt(this.lastRuleRef);
        String s = token2.getText();
        if (token2.type == 24) {
            s = CodeGenerator.encodeLexerRuleName(s);
        }
        ((RuleSymbol)this.grammar.getSymbol(s)).addReference(this.lastRuleRef);
        this.labelElement(this.lastRuleRef, token3);
    }
    
    public void refSemPred(final Token token) {
        super.refSemPred(token);
        if (this.context().currentAlt().atStart()) {
            this.context().currentAlt().semPred = token.getText();
        }
        else {
            final ActionElement actionElement = new ActionElement(this.grammar, token);
            actionElement.isSemPred = true;
            this.addElementToCurrentAlt(actionElement);
        }
    }
    
    public void refStringLiteral(final Token token, final Token token2, final int n, final boolean b) {
        super.refStringLiteral(token, token2, n, b);
        if (this.grammar instanceof TreeWalkerGrammar && n == 2) {
            this.tool.error("^ not allowed in here for tree-walker", this.grammar.getFilename(), token.getLine(), token.getColumn());
        }
        final StringLiteralElement stringLiteralElement = new StringLiteralElement(this.grammar, token, n);
        if (this.grammar instanceof LexerGrammar && !((LexerGrammar)this.grammar).caseSensitive) {
            for (int i = 1; i < token.getText().length() - 1; ++i) {
                final char char1 = token.getText().charAt(i);
                if (char1 < '\u0080' && Character.toLowerCase(char1) != char1) {
                    this.tool.warning("Characters of string literal must be lowercase when caseSensitive=false", this.grammar.getFilename(), token.getLine(), token.getColumn());
                    break;
                }
            }
        }
        this.addElementToCurrentAlt(stringLiteralElement);
        this.labelElement(stringLiteralElement, token2);
        final String ignoreRule = this.ruleBlock.getIgnoreRule();
        if (!b && ignoreRule != null) {
            this.addElementToCurrentAlt(this.createOptionalRuleRef(ignoreRule, token));
        }
    }
    
    public void refToken(final Token token, final Token token2, final Token token3, final Token token4, final boolean b, final int n, final boolean b2) {
        if (this.grammar instanceof LexerGrammar) {
            if (n == 2) {
                this.tool.error("AST specification ^ not allowed in lexer", this.grammar.getFilename(), token2.getLine(), token2.getColumn());
            }
            if (b) {
                this.tool.error("~TOKEN is not allowed in lexer", this.grammar.getFilename(), token2.getLine(), token2.getColumn());
            }
            this.refRule(token, token2, token3, token4, n);
            final String ignoreRule = this.ruleBlock.getIgnoreRule();
            if (!b2 && ignoreRule != null) {
                this.addElementToCurrentAlt(this.createOptionalRuleRef(ignoreRule, token2));
            }
        }
        else {
            if (token != null) {
                this.tool.error("Assignment from token reference only allowed in lexer", this.grammar.getFilename(), token.getLine(), token.getColumn());
            }
            if (token4 != null) {
                this.tool.error("Token reference arguments only allowed in lexer", this.grammar.getFilename(), token4.getLine(), token4.getColumn());
            }
            super.refToken(token, token2, token3, token4, b, n, b2);
            final TokenRefElement tokenRefElement = new TokenRefElement(this.grammar, token2, b, n);
            this.addElementToCurrentAlt(tokenRefElement);
            this.labelElement(tokenRefElement, token3);
        }
    }
    
    public void refTokenRange(final Token token, final Token token2, final Token token3, final int n, final boolean b) {
        if (this.grammar instanceof LexerGrammar) {
            this.tool.error("Token range not allowed in lexer", this.grammar.getFilename(), token.getLine(), token.getColumn());
            return;
        }
        super.refTokenRange(token, token2, token3, n, b);
        final TokenRangeElement tokenRangeElement = new TokenRangeElement(this.grammar, token, token2, n);
        if (tokenRangeElement.end < tokenRangeElement.begin) {
            this.tool.error("Malformed range.", this.grammar.getFilename(), token.getLine(), token.getColumn());
            return;
        }
        this.addElementToCurrentAlt(tokenRangeElement);
        this.labelElement(tokenRangeElement, token3);
    }
    
    public void refTreeSpecifier(final Token treeSpecifier) {
        this.context().currentAlt().treeSpecifier = treeSpecifier;
    }
    
    public void refWildcard(final Token token, final Token token2, final int n) {
        super.refWildcard(token, token2, n);
        final WildcardElement wildcardElement = new WildcardElement(this.grammar, token, n);
        this.addElementToCurrentAlt(wildcardElement);
        this.labelElement(wildcardElement, token2);
    }
    
    public void reset() {
        super.reset();
        this.blocks = new LList();
        this.lastRuleRef = null;
        this.ruleEnd = null;
        this.ruleBlock = null;
        this.nested = 0;
        this.currentExceptionSpec = null;
        this.grammarError = false;
    }
    
    public void setArgOfRuleRef(final Token argOfRuleRef) {
        super.setArgOfRuleRef(argOfRuleRef);
        this.lastRuleRef.setArgs(argOfRuleRef.getText());
    }
    
    public static void setBlock(final AlternativeBlock alternativeBlock, final AlternativeBlock alternativeBlock2) {
        alternativeBlock.setAlternatives(alternativeBlock2.getAlternatives());
        alternativeBlock.initAction = alternativeBlock2.initAction;
        alternativeBlock.label = alternativeBlock2.label;
        alternativeBlock.hasASynPred = alternativeBlock2.hasASynPred;
        alternativeBlock.hasAnAction = alternativeBlock2.hasAnAction;
        alternativeBlock.warnWhenFollowAmbig = alternativeBlock2.warnWhenFollowAmbig;
        alternativeBlock.generateAmbigWarnings = alternativeBlock2.generateAmbigWarnings;
        alternativeBlock.line = alternativeBlock2.line;
        alternativeBlock.greedy = alternativeBlock2.greedy;
        alternativeBlock.greedySet = alternativeBlock2.greedySet;
    }
    
    public void setRuleOption(final Token token, final Token token2) {
        this.ruleBlock.setOption(token, token2);
    }
    
    public void setSubruleOption(final Token token, final Token token2) {
        this.context().block.setOption(token, token2);
    }
    
    public void synPred() {
        if (this.context().block.not) {
            this.tool.error("'~' cannot be applied to syntactic predicate", this.grammar.getFilename(), this.context().block.getLine(), this.context().block.getColumn());
        }
        final SynPredBlock synPredBlock = new SynPredBlock(this.grammar);
        setBlock(synPredBlock, this.context().block);
        final BlockContext blockContext = (BlockContext)this.blocks.pop();
        this.blocks.push(new BlockContext());
        this.context().block = synPredBlock;
        this.context().blockEnd = blockContext.blockEnd;
        this.context().blockEnd.block = synPredBlock;
    }
    
    public void zeroOrMoreSubRule() {
        if (this.context().block.not) {
            this.tool.error("'~' cannot be applied to (...)+ subrule", this.grammar.getFilename(), this.context().block.getLine(), this.context().block.getColumn());
        }
        final ZeroOrMoreBlock zeroOrMoreBlock = new ZeroOrMoreBlock(this.grammar);
        setBlock(zeroOrMoreBlock, this.context().block);
        final BlockContext blockContext = (BlockContext)this.blocks.pop();
        this.blocks.push(new BlockContext());
        this.context().block = zeroOrMoreBlock;
        this.context().blockEnd = blockContext.blockEnd;
        this.context().blockEnd.block = zeroOrMoreBlock;
    }
}
