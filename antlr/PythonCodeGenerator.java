// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.actions.python.CodeLexer;
import antlr.actions.python.ActionLexer;
import antlr.collections.impl.BitSet;
import java.io.IOException;
import java.util.Enumeration;
import java.io.PrintWriter;
import antlr.collections.impl.Vector;
import java.util.Hashtable;

public class PythonCodeGenerator extends CodeGenerator
{
    protected int syntacticPredLevel;
    protected boolean genAST;
    protected boolean saveText;
    String labeledElementType;
    String labeledElementASTType;
    String labeledElementInit;
    String commonExtraArgs;
    String commonExtraParams;
    String commonLocalVars;
    String lt1Value;
    String exceptionThrown;
    String throwNoViable;
    public static final String initHeaderAction = "__init__";
    public static final String mainHeaderAction = "__main__";
    String lexerClassName;
    String parserClassName;
    String treeWalkerClassName;
    RuleBlock currentRule;
    String currentASTResult;
    Hashtable treeVariableMap;
    Hashtable declaredASTVariables;
    int astVarNumber;
    protected static final String NONUNIQUE;
    public static final int caseSizeThreshold = 127;
    private Vector semPreds;
    
    protected void printTabs() {
        for (int i = 0; i < this.tabs; ++i) {
            this.currentOutput.print("    ");
        }
    }
    
    public PythonCodeGenerator() {
        this.syntacticPredLevel = 0;
        this.genAST = false;
        this.saveText = false;
        this.treeVariableMap = new Hashtable();
        this.declaredASTVariables = new Hashtable();
        this.astVarNumber = 1;
        this.charFormatter = new PythonCharFormatter();
        this.DEBUG_CODE_GENERATOR = true;
    }
    
    protected int addSemPred(final String s) {
        this.semPreds.appendElement(s);
        return this.semPreds.size() - 1;
    }
    
    public void exitIfError() {
        if (this.antlrTool.hasError()) {
            this.antlrTool.fatalError("Exiting due to errors.");
        }
    }
    
    protected void checkCurrentOutputStream() {
        try {
            if (this.currentOutput == null) {
                throw new NullPointerException();
            }
        }
        catch (Exception ex) {
            Utils.error("current output is not set");
        }
    }
    
    protected String extractIdOfAction(String s, final int n, final int n2) {
        s = this.removeAssignmentFromDeclaration(s);
        s = s.trim();
        return s;
    }
    
    protected String extractTypeOfAction(final String s, final int n, final int n2) {
        return "";
    }
    
    protected void flushTokens() {
        try {
            int n = 0;
            this.checkCurrentOutputStream();
            this.println("");
            this.println("### import antlr.Token ");
            this.println("from antlr import Token");
            this.println("### >>>The Known Token Types <<<");
            final PrintWriter currentOutput = this.currentOutput;
            final Enumeration<TokenManager> elements = (Enumeration<TokenManager>)this.behavior.tokenManagers.elements();
            while (elements.hasMoreElements()) {
                final TokenManager tokenManager = elements.nextElement();
                if (!tokenManager.isReadOnly()) {
                    if (n == 0) {
                        this.genTokenTypes(tokenManager);
                        n = 1;
                    }
                    this.currentOutput = currentOutput;
                    this.genTokenInterchange(tokenManager);
                    this.currentOutput = currentOutput;
                }
                this.exitIfError();
            }
        }
        catch (Exception ex) {
            this.exitIfError();
        }
        this.checkCurrentOutputStream();
        this.println("");
    }
    
    public void gen() {
        try {
            final Enumeration<Grammar> elements = this.behavior.grammars.elements();
            while (elements.hasMoreElements()) {
                final Grammar grammar = elements.nextElement();
                grammar.setGrammarAnalyzer(this.analyzer);
                grammar.setCodeGenerator(this);
                this.analyzer.setGrammar(grammar);
                this.setupGrammarParameters(grammar);
                grammar.generate();
                this.exitIfError();
            }
        }
        catch (IOException ex) {
            this.antlrTool.reportException(ex, null);
        }
    }
    
    public void gen(final ActionElement actionElement) {
        if (actionElement.isSemPred) {
            this.genSemPred(actionElement.actionText, actionElement.line);
        }
        else {
            if (this.grammar.hasSyntacticPredicate) {
                this.println("if not self.inputState.guessing:");
                ++this.tabs;
            }
            final ActionTransInfo actionTransInfo = new ActionTransInfo();
            final String processActionForSpecialSymbols = this.processActionForSpecialSymbols(actionElement.actionText, actionElement.getLine(), this.currentRule, actionTransInfo);
            if (actionTransInfo.refRuleRoot != null) {
                this.println(actionTransInfo.refRuleRoot + " = currentAST.root");
            }
            this.printAction(processActionForSpecialSymbols);
            if (actionTransInfo.assignToRoot) {
                this.println("currentAST.root = " + actionTransInfo.refRuleRoot + "");
                this.println("if (" + actionTransInfo.refRuleRoot + " != None) and (" + actionTransInfo.refRuleRoot + ".getFirstChild() != None):");
                ++this.tabs;
                this.println("currentAST.child = " + actionTransInfo.refRuleRoot + ".getFirstChild()");
                --this.tabs;
                this.println("else:");
                ++this.tabs;
                this.println("currentAST.child = " + actionTransInfo.refRuleRoot);
                --this.tabs;
                this.println("currentAST.advanceChildToEnd()");
            }
            if (this.grammar.hasSyntacticPredicate) {
                --this.tabs;
            }
        }
    }
    
    public void gen(final AlternativeBlock obj) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("gen(" + obj + ")");
        }
        this.genBlockPreamble(obj);
        this.genBlockInitAction(obj);
        final String currentASTResult = this.currentASTResult;
        if (obj.getLabel() != null) {
            this.currentASTResult = obj.getLabel();
        }
        this.grammar.theLLkAnalyzer.deterministic(obj);
        final int tabs = this.tabs;
        this.genBlockFinish(this.genCommonBlock(obj, true), this.throwNoViable);
        this.tabs = tabs;
        this.currentASTResult = currentASTResult;
    }
    
    public void gen(final BlockEndElement obj) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genRuleEnd(" + obj + ")");
        }
    }
    
    public void gen(final CharLiteralElement obj) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genChar(" + obj + ")");
        }
        if (obj.getLabel() != null) {
            this.println(obj.getLabel() + " = " + this.lt1Value);
        }
        final boolean saveText = this.saveText;
        this.saveText = (this.saveText && obj.getAutoGenType() == 1);
        this.genMatch(obj);
        this.saveText = saveText;
    }
    
    String toString(final boolean b) {
        String s;
        if (b) {
            s = "True";
        }
        else {
            s = "False";
        }
        return s;
    }
    
    public void gen(final CharRangeElement charRangeElement) {
        if (charRangeElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(charRangeElement.getLabel() + " = " + this.lt1Value);
        }
        final boolean b = this.grammar instanceof LexerGrammar && (!this.saveText || charRangeElement.getAutoGenType() == 3);
        if (b) {
            this.println("_saveIndex = self.text.length()");
        }
        this.println("self.matchRange(u" + charRangeElement.beginText + ", u" + charRangeElement.endText + ")");
        if (b) {
            this.println("self.text.setLength(_saveIndex)");
        }
    }
    
    public void gen(final LexerGrammar grammar) throws IOException {
        if (grammar.debuggingOutput) {
            this.semPreds = new Vector();
        }
        this.setGrammar(grammar);
        if (!(this.grammar instanceof LexerGrammar)) {
            this.antlrTool.panic("Internal error generating lexer");
        }
        this.setupOutput(this.grammar.getClassName());
        this.genAST = false;
        this.saveText = true;
        this.tabs = 0;
        this.genHeader();
        this.println("### import antlr and other modules ..");
        this.println("import sys");
        this.println("import antlr");
        this.println("");
        this.println("version = sys.version.split()[0]");
        this.println("if version < '2.2.1':");
        ++this.tabs;
        this.println("False = 0");
        --this.tabs;
        this.println("if version < '2.3':");
        ++this.tabs;
        this.println("True = not False");
        --this.tabs;
        this.println("### header action >>> ");
        this.printActionCode(this.behavior.getHeaderAction(""), 0);
        this.println("### header action <<< ");
        this.println("### preamble action >>> ");
        this.printActionCode(this.grammar.preambleAction.getText(), 0);
        this.println("### preamble action <<< ");
        String s;
        if (this.grammar.superClass != null) {
            s = this.grammar.superClass;
        }
        else {
            s = "antlr." + this.grammar.getSuperClass();
        }
        final Token token = this.grammar.options.get("classHeaderPrefix");
        if (token != null && StringUtils.stripFrontBack(token.getText(), "\"", "\"") != null) {}
        this.println("### >>>The Literals<<<");
        this.println("literals = {}");
        final Enumeration tokenSymbolKeys = this.grammar.tokenManager.getTokenSymbolKeys();
        while (tokenSymbolKeys.hasMoreElements()) {
            final String s2 = tokenSymbolKeys.nextElement();
            if (s2.charAt(0) != '\"') {
                continue;
            }
            final TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbol(s2);
            if (!(tokenSymbol instanceof StringLiteralSymbol)) {
                continue;
            }
            final StringLiteralSymbol stringLiteralSymbol = (StringLiteralSymbol)tokenSymbol;
            this.println("literals[u" + stringLiteralSymbol.getId() + "] = " + stringLiteralSymbol.getTokenType());
        }
        this.println("");
        this.flushTokens();
        this.genJavadocComment(this.grammar);
        this.println("class " + this.lexerClassName + "(" + s + ") :");
        ++this.tabs;
        this.printGrammarAction(this.grammar);
        this.println("def __init__(self, *argv, **kwargs) :");
        ++this.tabs;
        this.println(s + ".__init__(self, *argv, **kwargs)");
        this.println("self.caseSensitiveLiterals = " + this.toString(grammar.caseSensitiveLiterals));
        this.println("self.setCaseSensitive(" + this.toString(grammar.caseSensitive) + ")");
        this.println("self.literals = literals");
        if (this.grammar.debuggingOutput) {
            this.println("ruleNames[] = [");
            final Enumeration elements = this.grammar.rules.elements();
            ++this.tabs;
            while (elements.hasMoreElements()) {
                final GrammarSymbol grammarSymbol = elements.nextElement();
                if (grammarSymbol instanceof RuleSymbol) {
                    this.println("\"" + ((RuleSymbol)grammarSymbol).getId() + "\",");
                }
            }
            --this.tabs;
            this.println("]");
        }
        this.genHeaderInit(this.grammar);
        --this.tabs;
        this.genNextToken();
        this.println("");
        final Enumeration elements2 = this.grammar.rules.elements();
        int n = 0;
        while (elements2.hasMoreElements()) {
            final RuleSymbol ruleSymbol = elements2.nextElement();
            if (!ruleSymbol.getId().equals("mnextToken")) {
                this.genRule(ruleSymbol, false, n++);
            }
            this.exitIfError();
        }
        if (this.grammar.debuggingOutput) {
            this.genSemPredMap();
        }
        this.genBitsets(this.bitsetsUsed, ((LexerGrammar)this.grammar).charVocabulary.size());
        this.println("");
        this.genHeaderMain(this.grammar);
        this.currentOutput.close();
        this.currentOutput = null;
    }
    
    protected void genHeaderMain(final Grammar grammar) {
        String s = this.behavior.getHeaderAction(grammar.getClassName() + "." + "__main__");
        if (isEmpty(s)) {
            s = this.behavior.getHeaderAction("__main__");
        }
        if (isEmpty(s)) {
            if (grammar instanceof LexerGrammar) {
                final int tabs = this.tabs;
                this.tabs = 0;
                this.println("### __main__ header action >>> ");
                this.genLexerTest();
                this.tabs = 0;
                this.println("### __main__ header action <<< ");
                this.tabs = tabs;
            }
        }
        else {
            final int tabs2 = this.tabs;
            this.tabs = 0;
            this.println("");
            this.println("### __main__ header action >>> ");
            this.printMainFunc(s);
            this.tabs = 0;
            this.println("### __main__ header action <<< ");
            this.tabs = tabs2;
        }
    }
    
    protected void genHeaderInit(final Grammar grammar) {
        String s = this.behavior.getHeaderAction(grammar.getClassName() + "." + "__init__");
        if (isEmpty(s)) {
            s = this.behavior.getHeaderAction("__init__");
        }
        if (!isEmpty(s)) {
            final int tabs = this.tabs;
            this.println("### __init__ header action >>> ");
            this.printActionCode(s, 0);
            this.tabs = tabs;
            this.println("### __init__ header action <<< ");
        }
    }
    
    protected void printMainFunc(final String s) {
        final int tabs = this.tabs;
        this.tabs = 0;
        this.println("if __name__ == '__main__':");
        ++this.tabs;
        this.printActionCode(s, 0);
        --this.tabs;
        this.tabs = tabs;
    }
    
    public void gen(final OneOrMoreBlock oneOrMoreBlock) {
        final int tabs = this.tabs;
        this.genBlockPreamble(oneOrMoreBlock);
        String s;
        if (oneOrMoreBlock.getLabel() != null) {
            s = "_cnt_" + oneOrMoreBlock.getLabel();
        }
        else {
            s = "_cnt" + oneOrMoreBlock.ID;
        }
        this.println("" + s + "= 0");
        this.println("while True:");
        ++this.tabs;
        final int tabs2 = this.tabs;
        this.genBlockInitAction(oneOrMoreBlock);
        final String currentASTResult = this.currentASTResult;
        if (oneOrMoreBlock.getLabel() != null) {
            this.currentASTResult = oneOrMoreBlock.getLabel();
        }
        this.grammar.theLLkAnalyzer.deterministic(oneOrMoreBlock);
        boolean b = false;
        int n = this.grammar.maxk;
        if (!oneOrMoreBlock.greedy && oneOrMoreBlock.exitLookaheadDepth <= this.grammar.maxk && oneOrMoreBlock.exitCache[oneOrMoreBlock.exitLookaheadDepth].containsEpsilon()) {
            b = true;
            n = oneOrMoreBlock.exitLookaheadDepth;
        }
        else if (!oneOrMoreBlock.greedy && oneOrMoreBlock.exitLookaheadDepth == Integer.MAX_VALUE) {
            b = true;
        }
        if (b) {
            this.println("### nongreedy (...)+ loop; exit depth is " + oneOrMoreBlock.exitLookaheadDepth);
            final String lookaheadTestExpression = this.getLookaheadTestExpression(oneOrMoreBlock.exitCache, n);
            this.println("### nongreedy exit test");
            this.println("if " + s + " >= 1 and " + lookaheadTestExpression + ":");
            ++this.tabs;
            this.println("break");
            --this.tabs;
        }
        final int tabs3 = this.tabs;
        this.genBlockFinish(this.genCommonBlock(oneOrMoreBlock, false), "break");
        this.tabs = tabs3;
        this.tabs = tabs2;
        this.println(s + " += 1");
        this.tabs = tabs2;
        --this.tabs;
        this.println("if " + s + " < 1:");
        ++this.tabs;
        this.println(this.throwNoViable);
        --this.tabs;
        this.currentASTResult = currentASTResult;
    }
    
    public void gen(final ParserGrammar grammar) throws IOException {
        if (grammar.debuggingOutput) {
            this.semPreds = new Vector();
        }
        this.setGrammar(grammar);
        if (!(this.grammar instanceof ParserGrammar)) {
            this.antlrTool.panic("Internal error generating parser");
        }
        this.setupOutput(this.grammar.getClassName());
        this.genAST = this.grammar.buildAST;
        this.tabs = 0;
        this.genHeader();
        this.println("### import antlr and other modules ..");
        this.println("import sys");
        this.println("import antlr");
        this.println("");
        this.println("version = sys.version.split()[0]");
        this.println("if version < '2.2.1':");
        ++this.tabs;
        this.println("False = 0");
        --this.tabs;
        this.println("if version < '2.3':");
        ++this.tabs;
        this.println("True = not False");
        --this.tabs;
        this.println("### header action >>> ");
        this.printActionCode(this.behavior.getHeaderAction(""), 0);
        this.println("### header action <<< ");
        this.println("### preamble action>>>");
        this.printActionCode(this.grammar.preambleAction.getText(), 0);
        this.println("### preamble action <<<");
        this.flushTokens();
        String s;
        if (this.grammar.superClass != null) {
            s = this.grammar.superClass;
        }
        else {
            s = "antlr." + this.grammar.getSuperClass();
        }
        this.genJavadocComment(this.grammar);
        final Token token = this.grammar.options.get("classHeaderPrefix");
        if (token != null && StringUtils.stripFrontBack(token.getText(), "\"", "\"") != null) {}
        this.print("class " + this.parserClassName + "(" + s);
        this.println("):");
        ++this.tabs;
        if (this.grammar.debuggingOutput) {
            this.println("_ruleNames = [");
            final Enumeration elements = this.grammar.rules.elements();
            ++this.tabs;
            while (elements.hasMoreElements()) {
                final GrammarSymbol grammarSymbol = elements.nextElement();
                if (grammarSymbol instanceof RuleSymbol) {
                    this.println("\"" + ((RuleSymbol)grammarSymbol).getId() + "\",");
                }
            }
            --this.tabs;
            this.println("]");
        }
        this.printGrammarAction(this.grammar);
        this.println("");
        this.println("def __init__(self, *args, **kwargs):");
        ++this.tabs;
        this.println(s + ".__init__(self, *args, **kwargs)");
        this.println("self.tokenNames = _tokenNames");
        if (this.grammar.debuggingOutput) {
            this.println("self.ruleNames  = _ruleNames");
            this.println("self.semPredNames = _semPredNames");
            this.println("self.setupDebugging(self.tokenBuf)");
        }
        if (this.grammar.buildAST) {
            this.println("self.buildTokenTypeASTClassMap()");
            this.println("self.astFactory = antlr.ASTFactory(self.getTokenTypeToASTClassMap())");
            if (this.labeledElementASTType != null) {
                this.println("self.astFactory.setASTNodeClass(" + this.labeledElementASTType + ")");
            }
        }
        this.genHeaderInit(this.grammar);
        this.println("");
        final Enumeration elements2 = this.grammar.rules.elements();
        int n = 0;
        while (elements2.hasMoreElements()) {
            final GrammarSymbol grammarSymbol2 = elements2.nextElement();
            if (grammarSymbol2 instanceof RuleSymbol) {
                final RuleSymbol ruleSymbol;
                this.genRule(ruleSymbol, (ruleSymbol = (RuleSymbol)grammarSymbol2).references.size() == 0, n++);
            }
            this.exitIfError();
        }
        if (this.grammar.buildAST) {
            this.genTokenASTNodeMap();
        }
        this.genTokenStrings();
        this.genBitsets(this.bitsetsUsed, this.grammar.tokenManager.maxTokenType());
        if (this.grammar.debuggingOutput) {
            this.genSemPredMap();
        }
        this.println("");
        this.tabs = 0;
        this.genHeaderMain(this.grammar);
        this.currentOutput.close();
        this.currentOutput = null;
    }
    
    public void gen(final RuleRefElement obj) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genRR(" + obj + ")");
        }
        final RuleSymbol ruleSymbol = (RuleSymbol)this.grammar.getSymbol(obj.targetRule);
        if (ruleSymbol == null || !ruleSymbol.isDefined()) {
            this.antlrTool.error("Rule '" + obj.targetRule + "' is not defined", this.grammar.getFilename(), obj.getLine(), obj.getColumn());
            return;
        }
        if (!(ruleSymbol instanceof RuleSymbol)) {
            this.antlrTool.error("'" + obj.targetRule + "' does not name a grammar rule", this.grammar.getFilename(), obj.getLine(), obj.getColumn());
            return;
        }
        this.genErrorTryForElement(obj);
        if (this.grammar instanceof TreeWalkerGrammar && obj.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(obj.getLabel() + " = antlr.ifelse(_t == antlr.ASTNULL, None, " + this.lt1Value + ")");
        }
        if (this.grammar instanceof LexerGrammar && (!this.saveText || obj.getAutoGenType() == 3)) {
            this.println("_saveIndex = self.text.length()");
        }
        this.printTabs();
        if (obj.idAssign != null) {
            if (ruleSymbol.block.returnAction == null) {
                this.antlrTool.warning("Rule '" + obj.targetRule + "' has no return type", this.grammar.getFilename(), obj.getLine(), obj.getColumn());
            }
            this._print(obj.idAssign + "=");
        }
        else if (!(this.grammar instanceof LexerGrammar) && this.syntacticPredLevel == 0 && ruleSymbol.block.returnAction != null) {
            this.antlrTool.warning("Rule '" + obj.targetRule + "' returns a value", this.grammar.getFilename(), obj.getLine(), obj.getColumn());
        }
        this.GenRuleInvocation(obj);
        if (this.grammar instanceof LexerGrammar && (!this.saveText || obj.getAutoGenType() == 3)) {
            this.println("self.text.setLength(_saveIndex)");
        }
        if (this.syntacticPredLevel == 0) {
            final boolean b = this.grammar.hasSyntacticPredicate && ((this.grammar.buildAST && obj.getLabel() != null) || (this.genAST && obj.getAutoGenType() == 1));
            if (b) {}
            if (this.grammar.buildAST && obj.getLabel() != null) {
                this.println(obj.getLabel() + "_AST = self.returnAST");
            }
            if (this.genAST) {
                switch (obj.getAutoGenType()) {
                    case 1: {
                        this.println("self.addASTChild(currentAST, self.returnAST)");
                        break;
                    }
                    case 2: {
                        this.antlrTool.error("Internal: encountered ^ after rule reference");
                        break;
                    }
                }
            }
            if (this.grammar instanceof LexerGrammar && obj.getLabel() != null) {
                this.println(obj.getLabel() + " = self._returnToken");
            }
            if (b) {}
        }
        this.genErrorCatchForElement(obj);
    }
    
    public void gen(final StringLiteralElement obj) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genString(" + obj + ")");
        }
        if (obj.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(obj.getLabel() + " = " + this.lt1Value + "");
        }
        this.genElementAST(obj);
        final boolean saveText = this.saveText;
        this.saveText = (this.saveText && obj.getAutoGenType() == 1);
        this.genMatch(obj);
        this.saveText = saveText;
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _t.getNextSibling()");
        }
    }
    
    public void gen(final TokenRangeElement tokenRangeElement) {
        this.genErrorTryForElement(tokenRangeElement);
        if (tokenRangeElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(tokenRangeElement.getLabel() + " = " + this.lt1Value);
        }
        this.genElementAST(tokenRangeElement);
        this.println("self.matchRange(u" + tokenRangeElement.beginText + ", u" + tokenRangeElement.endText + ")");
        this.genErrorCatchForElement(tokenRangeElement);
    }
    
    public void gen(final TokenRefElement obj) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genTokenRef(" + obj + ")");
        }
        if (this.grammar instanceof LexerGrammar) {
            this.antlrTool.panic("Token reference found in lexer");
        }
        this.genErrorTryForElement(obj);
        if (obj.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(obj.getLabel() + " = " + this.lt1Value + "");
        }
        this.genElementAST(obj);
        this.genMatch(obj);
        this.genErrorCatchForElement(obj);
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _t.getNextSibling()");
        }
    }
    
    public void gen(final TreeElement treeElement) {
        this.println("_t" + treeElement.ID + " = _t");
        if (treeElement.root.getLabel() != null) {
            this.println(treeElement.root.getLabel() + " = antlr.ifelse(_t == antlr.ASTNULL, None, _t)");
        }
        if (treeElement.root.getAutoGenType() == 3) {
            this.antlrTool.error("Suffixing a root node with '!' is not implemented", this.grammar.getFilename(), treeElement.getLine(), treeElement.getColumn());
            treeElement.root.setAutoGenType(1);
        }
        if (treeElement.root.getAutoGenType() == 2) {
            this.antlrTool.warning("Suffixing a root node with '^' is redundant; already a root", this.grammar.getFilename(), treeElement.getLine(), treeElement.getColumn());
            treeElement.root.setAutoGenType(1);
        }
        this.genElementAST(treeElement.root);
        if (this.grammar.buildAST) {
            this.println("_currentAST" + treeElement.ID + " = currentAST.copy()");
            this.println("currentAST.root = currentAST.child");
            this.println("currentAST.child = None");
        }
        if (treeElement.root instanceof WildcardElement) {
            this.println("if not _t: raise antlr.MismatchedTokenException()");
        }
        else {
            this.genMatch(treeElement.root);
        }
        this.println("_t = _t.getFirstChild()");
        for (int i = 0; i < treeElement.getAlternatives().size(); ++i) {
            for (AlternativeElement alternativeElement = treeElement.getAlternativeAt(i).head; alternativeElement != null; alternativeElement = alternativeElement.next) {
                alternativeElement.generate();
            }
        }
        if (this.grammar.buildAST) {
            this.println("currentAST = _currentAST" + treeElement.ID + "");
        }
        this.println("_t = _t" + treeElement.ID + "");
        this.println("_t = _t.getNextSibling()");
    }
    
    public void gen(final TreeWalkerGrammar grammar) throws IOException {
        this.setGrammar(grammar);
        if (!(this.grammar instanceof TreeWalkerGrammar)) {
            this.antlrTool.panic("Internal error generating tree-walker");
        }
        this.setupOutput(this.grammar.getClassName());
        this.genAST = this.grammar.buildAST;
        this.tabs = 0;
        this.genHeader();
        this.println("### import antlr and other modules ..");
        this.println("import sys");
        this.println("import antlr");
        this.println("");
        this.println("version = sys.version.split()[0]");
        this.println("if version < '2.2.1':");
        ++this.tabs;
        this.println("False = 0");
        --this.tabs;
        this.println("if version < '2.3':");
        ++this.tabs;
        this.println("True = not False");
        --this.tabs;
        this.println("### header action >>> ");
        this.printActionCode(this.behavior.getHeaderAction(""), 0);
        this.println("### header action <<< ");
        this.flushTokens();
        this.println("### user code>>>");
        this.printActionCode(this.grammar.preambleAction.getText(), 0);
        this.println("### user code<<<");
        String s;
        if (this.grammar.superClass != null) {
            s = this.grammar.superClass;
        }
        else {
            s = "antlr." + this.grammar.getSuperClass();
        }
        this.println("");
        final Token token = this.grammar.options.get("classHeaderPrefix");
        if (token != null && StringUtils.stripFrontBack(token.getText(), "\"", "\"") != null) {}
        this.genJavadocComment(this.grammar);
        this.println("class " + this.treeWalkerClassName + "(" + s + "):");
        ++this.tabs;
        this.println("");
        this.println("# ctor ..");
        this.println("def __init__(self, *args, **kwargs):");
        ++this.tabs;
        this.println(s + ".__init__(self, *args, **kwargs)");
        this.println("self.tokenNames = _tokenNames");
        this.genHeaderInit(this.grammar);
        --this.tabs;
        this.println("");
        this.printGrammarAction(this.grammar);
        final Enumeration elements = this.grammar.rules.elements();
        int n = 0;
        while (elements.hasMoreElements()) {
            final GrammarSymbol grammarSymbol = elements.nextElement();
            if (grammarSymbol instanceof RuleSymbol) {
                final RuleSymbol ruleSymbol;
                this.genRule(ruleSymbol, (ruleSymbol = (RuleSymbol)grammarSymbol).references.size() == 0, n++);
            }
            this.exitIfError();
        }
        this.genTokenStrings();
        this.genBitsets(this.bitsetsUsed, this.grammar.tokenManager.maxTokenType());
        this.tabs = 0;
        this.genHeaderMain(this.grammar);
        this.currentOutput.close();
        this.currentOutput = null;
    }
    
    public void gen(final WildcardElement wildcardElement) {
        if (wildcardElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(wildcardElement.getLabel() + " = " + this.lt1Value + "");
        }
        this.genElementAST(wildcardElement);
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("if not _t:");
            ++this.tabs;
            this.println("raise antlr.MismatchedTokenException()");
            --this.tabs;
        }
        else if (this.grammar instanceof LexerGrammar) {
            if (this.grammar instanceof LexerGrammar && (!this.saveText || wildcardElement.getAutoGenType() == 3)) {
                this.println("_saveIndex = self.text.length()");
            }
            this.println("self.matchNot(antlr.EOF_CHAR)");
            if (this.grammar instanceof LexerGrammar && (!this.saveText || wildcardElement.getAutoGenType() == 3)) {
                this.println("self.text.setLength(_saveIndex)");
            }
        }
        else {
            this.println("self.matchNot(" + this.getValueString(1, false) + ")");
        }
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _t.getNextSibling()");
        }
    }
    
    public void gen(final ZeroOrMoreBlock zeroOrMoreBlock) {
        final int tabs = this.tabs;
        this.genBlockPreamble(zeroOrMoreBlock);
        this.println("while True:");
        ++this.tabs;
        final int tabs2 = this.tabs;
        this.genBlockInitAction(zeroOrMoreBlock);
        final String currentASTResult = this.currentASTResult;
        if (zeroOrMoreBlock.getLabel() != null) {
            this.currentASTResult = zeroOrMoreBlock.getLabel();
        }
        this.grammar.theLLkAnalyzer.deterministic(zeroOrMoreBlock);
        boolean b = false;
        int n = this.grammar.maxk;
        if (!zeroOrMoreBlock.greedy && zeroOrMoreBlock.exitLookaheadDepth <= this.grammar.maxk && zeroOrMoreBlock.exitCache[zeroOrMoreBlock.exitLookaheadDepth].containsEpsilon()) {
            b = true;
            n = zeroOrMoreBlock.exitLookaheadDepth;
        }
        else if (!zeroOrMoreBlock.greedy && zeroOrMoreBlock.exitLookaheadDepth == Integer.MAX_VALUE) {
            b = true;
        }
        if (b) {
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("nongreedy (...)* loop; exit depth is " + zeroOrMoreBlock.exitLookaheadDepth);
            }
            final String lookaheadTestExpression = this.getLookaheadTestExpression(zeroOrMoreBlock.exitCache, n);
            this.println("###  nongreedy exit test");
            this.println("if (" + lookaheadTestExpression + "):");
            ++this.tabs;
            this.println("break");
            --this.tabs;
        }
        final int tabs3 = this.tabs;
        this.genBlockFinish(this.genCommonBlock(zeroOrMoreBlock, false), "break");
        this.tabs = tabs3;
        this.tabs = tabs2;
        --this.tabs;
        this.currentASTResult = currentASTResult;
    }
    
    protected void genAlt(final Alternative alternative, final AlternativeBlock alternativeBlock) {
        final boolean genAST = this.genAST;
        this.genAST = (this.genAST && alternative.getAutoGen());
        final boolean saveText = this.saveText;
        this.saveText = (this.saveText && alternative.getAutoGen());
        final Hashtable treeVariableMap = this.treeVariableMap;
        this.treeVariableMap = new Hashtable();
        if (alternative.exceptionSpec != null) {
            this.println("try:");
            ++this.tabs;
        }
        this.println("pass");
        for (AlternativeElement alternativeElement = alternative.head; !(alternativeElement instanceof BlockEndElement); alternativeElement = alternativeElement.next) {
            alternativeElement.generate();
        }
        if (this.genAST) {
            if (alternativeBlock instanceof RuleBlock) {
                final RuleBlock ruleBlock = (RuleBlock)alternativeBlock;
                if (this.grammar.hasSyntacticPredicate) {}
                this.println(ruleBlock.getRuleName() + "_AST = currentAST.root");
                if (this.grammar.hasSyntacticPredicate) {}
            }
            else if (alternativeBlock.getLabel() != null) {
                this.antlrTool.warning("Labeled subrules not yet supported", this.grammar.getFilename(), alternativeBlock.getLine(), alternativeBlock.getColumn());
            }
        }
        if (alternative.exceptionSpec != null) {
            --this.tabs;
            this.genErrorHandler(alternative.exceptionSpec);
        }
        this.genAST = genAST;
        this.saveText = saveText;
        this.treeVariableMap = treeVariableMap;
    }
    
    protected void genBitsets(final Vector vector, final int n) {
        this.println("");
        for (int i = 0; i < vector.size(); ++i) {
            final BitSet set = (BitSet)vector.elementAt(i);
            set.growToInclude(n);
            this.genBitSet(set, i);
        }
    }
    
    private void genBitSet(final BitSet set, final int n) {
        final int tabs = this.tabs;
        this.tabs = 0;
        this.println("");
        this.println("### generate bit set");
        this.println("def mk" + this.getBitsetName(n) + "(): ");
        ++this.tabs;
        final int lengthInLongWords = set.lengthInLongWords();
        if (lengthInLongWords < 8) {
            this.println("### var1");
            this.println("data = [ " + set.toStringOfWords() + "]");
        }
        else {
            this.println("data = [0L] * " + lengthInLongWords + " ### init list");
            final long[] packedArray = set.toPackedArray();
            int i = 0;
            while (i < packedArray.length) {
                if (packedArray[i] == 0L) {
                    ++i;
                }
                else if (i + 1 == packedArray.length || packedArray[i] != packedArray[i + 1]) {
                    this.println("data[" + i + "] =" + packedArray[i] + "L");
                    ++i;
                }
                else {
                    int j;
                    for (j = i + 1; j < packedArray.length && packedArray[j] == packedArray[i]; ++j) {}
                    final long lng = packedArray[i];
                    this.println("for x in xrange(" + i + ", " + j + "):");
                    ++this.tabs;
                    this.println("data[x] = " + lng + "L");
                    --this.tabs;
                    i = j;
                }
            }
        }
        this.println("return data");
        --this.tabs;
        this.println(this.getBitsetName(n) + " = antlr.BitSet(mk" + this.getBitsetName(n) + "())");
        this.tabs = tabs;
    }
    
    private void genBlockFinish(final PythonBlockFinishingInfo pythonBlockFinishingInfo, final String s) {
        if (pythonBlockFinishingInfo.needAnErrorClause && (pythonBlockFinishingInfo.generatedAnIf || pythonBlockFinishingInfo.generatedSwitch)) {
            if (pythonBlockFinishingInfo.generatedAnIf) {
                this.println("else:");
            }
            ++this.tabs;
            this.println(s);
            --this.tabs;
        }
        if (pythonBlockFinishingInfo.postscript != null) {
            this.println(pythonBlockFinishingInfo.postscript);
        }
    }
    
    private void genBlockFinish1(final PythonBlockFinishingInfo pythonBlockFinishingInfo, final String s) {
        if (pythonBlockFinishingInfo.needAnErrorClause && (pythonBlockFinishingInfo.generatedAnIf || pythonBlockFinishingInfo.generatedSwitch)) {
            if (pythonBlockFinishingInfo.generatedAnIf) {
                this.println("else:");
            }
            ++this.tabs;
            this.println(s);
            --this.tabs;
            if (pythonBlockFinishingInfo.generatedAnIf) {}
        }
        if (pythonBlockFinishingInfo.postscript != null) {
            this.println(pythonBlockFinishingInfo.postscript);
        }
    }
    
    protected void genBlockInitAction(final AlternativeBlock alternativeBlock) {
        if (alternativeBlock.initAction != null) {
            this.printAction(this.processActionForSpecialSymbols(alternativeBlock.initAction, alternativeBlock.getLine(), this.currentRule, null));
        }
    }
    
    protected void genBlockPreamble(final AlternativeBlock alternativeBlock) {
        if (alternativeBlock instanceof RuleBlock) {
            final RuleBlock ruleBlock = (RuleBlock)alternativeBlock;
            if (ruleBlock.labeledElements != null) {
                for (int i = 0; i < ruleBlock.labeledElements.size(); ++i) {
                    final AlternativeElement alternativeElement = (AlternativeElement)ruleBlock.labeledElements.elementAt(i);
                    if (alternativeElement instanceof RuleRefElement || (alternativeElement instanceof AlternativeBlock && !(alternativeElement instanceof RuleBlock) && !(alternativeElement instanceof SynPredBlock))) {
                        if (!(alternativeElement instanceof RuleRefElement) && ((AlternativeBlock)alternativeElement).not && this.analyzer.subruleCanBeInverted((AlternativeBlock)alternativeElement, this.grammar instanceof LexerGrammar)) {
                            this.println(alternativeElement.getLabel() + " = " + this.labeledElementInit);
                            if (this.grammar.buildAST) {
                                this.genASTDeclaration(alternativeElement);
                            }
                        }
                        else {
                            if (this.grammar.buildAST) {
                                this.genASTDeclaration(alternativeElement);
                            }
                            if (this.grammar instanceof LexerGrammar) {
                                this.println(alternativeElement.getLabel() + " = None");
                            }
                            if (this.grammar instanceof TreeWalkerGrammar) {
                                this.println(alternativeElement.getLabel() + " = " + this.labeledElementInit);
                            }
                        }
                    }
                    else {
                        this.println(alternativeElement.getLabel() + " = " + this.labeledElementInit);
                        if (this.grammar.buildAST) {
                            if (alternativeElement instanceof GrammarAtom && ((GrammarAtom)alternativeElement).getASTNodeType() != null) {
                                this.genASTDeclaration(alternativeElement, ((GrammarAtom)alternativeElement).getASTNodeType());
                            }
                            else {
                                this.genASTDeclaration(alternativeElement);
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected void genCases(final BitSet obj) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genCases(" + obj + ")");
        }
        final int[] array = obj.toArray();
        final int n = (this.grammar instanceof LexerGrammar) ? 4 : 1;
        this.print("elif la1 and la1 in ");
        if (this.grammar instanceof LexerGrammar) {
            this._print("u'");
            for (int i = 0; i < array.length; ++i) {
                this._print(this.getValueString(array[i], false));
            }
            this._print("':\n");
            return;
        }
        this._print("[");
        for (int j = 0; j < array.length; ++j) {
            this._print(this.getValueString(array[j], false));
            if (j + 1 < array.length) {
                this._print(",");
            }
        }
        this._print("]:\n");
    }
    
    public PythonBlockFinishingInfo genCommonBlock(final AlternativeBlock alternativeBlock, final boolean b) {
        final int tabs = this.tabs;
        int n = 0;
        boolean b2 = false;
        int n2 = 0;
        final PythonBlockFinishingInfo pythonBlockFinishingInfo = new PythonBlockFinishingInfo();
        final boolean genAST = this.genAST;
        this.genAST = (this.genAST && alternativeBlock.getAutoGen());
        final boolean saveText = this.saveText;
        this.saveText = (this.saveText && alternativeBlock.getAutoGen());
        if (alternativeBlock.not && this.analyzer.subruleCanBeInverted(alternativeBlock, this.grammar instanceof LexerGrammar)) {
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("special case: ~(subrule)");
            }
            final Lookahead look = this.analyzer.look(1, alternativeBlock);
            if (alternativeBlock.getLabel() != null && this.syntacticPredLevel == 0) {
                this.println(alternativeBlock.getLabel() + " = " + this.lt1Value);
            }
            this.genElementAST(alternativeBlock);
            String str = "";
            if (this.grammar instanceof TreeWalkerGrammar) {
                str = "_t, ";
            }
            this.println("self.match(" + str + this.getBitsetName(this.markBitsetForGen(look.fset)) + ")");
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println("_t = _t.getNextSibling()");
            }
            return pythonBlockFinishingInfo;
        }
        if (alternativeBlock.getAlternatives().size() == 1) {
            final Alternative alternative = alternativeBlock.getAlternativeAt(0);
            if (alternative.synPred != null) {
                this.antlrTool.warning("Syntactic predicate superfluous for single alternative", this.grammar.getFilename(), alternativeBlock.getAlternativeAt(0).synPred.getLine(), alternativeBlock.getAlternativeAt(0).synPred.getColumn());
            }
            if (b) {
                if (alternative.semPred != null) {
                    this.genSemPred(alternative.semPred, alternativeBlock.line);
                }
                this.genAlt(alternative, alternativeBlock);
                return pythonBlockFinishingInfo;
            }
        }
        int n3 = 0;
        for (int i = 0; i < alternativeBlock.getAlternatives().size(); ++i) {
            if (suitableForCaseExpression(alternativeBlock.getAlternativeAt(i))) {
                ++n3;
            }
        }
        if (n3 >= this.makeSwitchThreshold) {
            final String lookaheadString = this.lookaheadString(1);
            b2 = true;
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println("if not _t:");
                ++this.tabs;
                this.println("_t = antlr.ASTNULL");
                --this.tabs;
            }
            this.println("la1 = " + lookaheadString);
            this.println("if False:");
            ++this.tabs;
            this.println("pass");
            --this.tabs;
            for (int j = 0; j < alternativeBlock.alternatives.size(); ++j) {
                final Alternative alternative2 = alternativeBlock.getAlternativeAt(j);
                if (suitableForCaseExpression(alternative2)) {
                    final Lookahead lookahead = alternative2.cache[1];
                    if (lookahead.fset.degree() == 0 && !lookahead.containsEpsilon()) {
                        this.antlrTool.warning("Alternate omitted due to empty prediction set", this.grammar.getFilename(), alternative2.head.getLine(), alternative2.head.getColumn());
                    }
                    else {
                        this.genCases(lookahead.fset);
                        ++this.tabs;
                        this.genAlt(alternative2, alternativeBlock);
                        --this.tabs;
                    }
                }
            }
            this.println("else:");
            ++this.tabs;
        }
        for (int k = (this.grammar instanceof LexerGrammar) ? this.grammar.maxk : 0; k >= 0; --k) {
            for (int l = 0; l < alternativeBlock.alternatives.size(); ++l) {
                final Alternative alternative3 = alternativeBlock.getAlternativeAt(l);
                if (this.DEBUG_CODE_GENERATOR) {
                    System.out.println("genAlt: " + l);
                }
                if (b2 && suitableForCaseExpression(alternative3)) {
                    if (this.DEBUG_CODE_GENERATOR) {
                        System.out.println("ignoring alt because it was in the switch");
                    }
                }
                else {
                    boolean b3;
                    String s;
                    if (this.grammar instanceof LexerGrammar) {
                        int m = alternative3.lookaheadDepth;
                        if (m == Integer.MAX_VALUE) {
                            m = this.grammar.maxk;
                        }
                        while (m >= 1 && alternative3.cache[m].containsEpsilon()) {
                            --m;
                        }
                        if (m != k) {
                            if (this.DEBUG_CODE_GENERATOR) {
                                System.out.println("ignoring alt because effectiveDepth!=altDepth" + m + "!=" + k);
                            }
                            continue;
                        }
                        else {
                            b3 = this.lookaheadIsEmpty(alternative3, m);
                            s = this.getLookaheadTestExpression(alternative3, m);
                        }
                    }
                    else {
                        b3 = this.lookaheadIsEmpty(alternative3, this.grammar.maxk);
                        s = this.getLookaheadTestExpression(alternative3, this.grammar.maxk);
                    }
                    if (alternative3.cache[1].fset.degree() > 127 && suitableForCaseExpression(alternative3)) {
                        if (n == 0) {
                            this.println("<m1> if " + s + ":");
                        }
                        else {
                            this.println("<m2> elif " + s + ":");
                        }
                    }
                    else if (b3 && alternative3.semPred == null && alternative3.synPred == null) {
                        if (n == 0) {
                            this.println("##<m3> <closing");
                        }
                        else {
                            this.println("else: ## <m4>");
                            ++this.tabs;
                        }
                        pythonBlockFinishingInfo.needAnErrorClause = false;
                    }
                    else {
                        if (alternative3.semPred != null) {
                            final String processActionForSpecialSymbols = this.processActionForSpecialSymbols(alternative3.semPred, alternativeBlock.line, this.currentRule, new ActionTransInfo());
                            if ((this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar) && this.grammar.debuggingOutput) {
                                s = "(" + s + " and fireSemanticPredicateEvaluated(antlr.debug.SemanticPredicateEvent.PREDICTING, " + this.addSemPred(this.charFormatter.escapeString(processActionForSpecialSymbols)) + ", " + processActionForSpecialSymbols + "))";
                            }
                            else {
                                s = "(" + s + " and (" + processActionForSpecialSymbols + "))";
                            }
                        }
                        if (n > 0) {
                            if (alternative3.synPred != null) {
                                this.println("else:");
                                ++this.tabs;
                                this.genSynPred(alternative3.synPred, s);
                                ++n2;
                            }
                            else {
                                this.println("elif " + s + ":");
                            }
                        }
                        else if (alternative3.synPred != null) {
                            this.genSynPred(alternative3.synPred, s);
                        }
                        else {
                            if (this.grammar instanceof TreeWalkerGrammar) {
                                this.println("if not _t:");
                                ++this.tabs;
                                this.println("_t = antlr.ASTNULL");
                                --this.tabs;
                            }
                            this.println("if " + s + ":");
                        }
                    }
                    ++n;
                    ++this.tabs;
                    this.genAlt(alternative3, alternativeBlock);
                    --this.tabs;
                }
            }
        }
        final String s2 = "";
        this.genAST = genAST;
        this.saveText = saveText;
        if (b2) {
            pythonBlockFinishingInfo.postscript = s2;
            pythonBlockFinishingInfo.generatedSwitch = true;
            pythonBlockFinishingInfo.generatedAnIf = (n > 0);
        }
        else {
            pythonBlockFinishingInfo.postscript = s2;
            pythonBlockFinishingInfo.generatedSwitch = false;
            pythonBlockFinishingInfo.generatedAnIf = (n > 0);
        }
        return pythonBlockFinishingInfo;
    }
    
    private static boolean suitableForCaseExpression(final Alternative alternative) {
        return alternative.lookaheadDepth == 1 && alternative.semPred == null && !alternative.cache[1].containsEpsilon() && alternative.cache[1].fset.degree() <= 127;
    }
    
    private void genElementAST(final AlternativeElement alternativeElement) {
        if (this.grammar instanceof TreeWalkerGrammar && !this.grammar.buildAST) {
            if (alternativeElement.getLabel() == null) {
                final String lt1Value = this.lt1Value;
                final String string = "tmp" + this.astVarNumber + "_AST";
                ++this.astVarNumber;
                this.mapTreeVariable(alternativeElement, string);
                this.println(string + "_in = " + lt1Value);
            }
            return;
        }
        if (this.grammar.buildAST && this.syntacticPredLevel == 0) {
            boolean b = this.genAST && (alternativeElement.getLabel() != null || alternativeElement.getAutoGenType() != 3);
            if (alternativeElement.getAutoGenType() != 3 && alternativeElement instanceof TokenRefElement) {
                b = true;
            }
            final boolean b2 = this.grammar.hasSyntacticPredicate && b;
            String s;
            String str;
            if (alternativeElement.getLabel() != null) {
                s = alternativeElement.getLabel();
                str = alternativeElement.getLabel();
            }
            else {
                s = this.lt1Value;
                str = "tmp" + this.astVarNumber;
                ++this.astVarNumber;
            }
            if (b) {
                if (alternativeElement instanceof GrammarAtom) {
                    final GrammarAtom grammarAtom = (GrammarAtom)alternativeElement;
                    if (grammarAtom.getASTNodeType() != null) {
                        this.genASTDeclaration(alternativeElement, str, grammarAtom.getASTNodeType());
                    }
                    else {
                        this.genASTDeclaration(alternativeElement, str, this.labeledElementASTType);
                    }
                }
                else {
                    this.genASTDeclaration(alternativeElement, str, this.labeledElementASTType);
                }
            }
            final String string2 = str + "_AST";
            this.mapTreeVariable(alternativeElement, string2);
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println(string2 + "_in = None");
            }
            if (b2) {}
            if (alternativeElement.getLabel() != null) {
                if (alternativeElement instanceof GrammarAtom) {
                    this.println(string2 + " = " + this.getASTCreateString((GrammarAtom)alternativeElement, s) + "");
                }
                else {
                    this.println(string2 + " = " + this.getASTCreateString(s) + "");
                }
            }
            if (alternativeElement.getLabel() == null && b) {
                final String lt1Value2 = this.lt1Value;
                if (alternativeElement instanceof GrammarAtom) {
                    this.println(string2 + " = " + this.getASTCreateString((GrammarAtom)alternativeElement, lt1Value2) + "");
                }
                else {
                    this.println(string2 + " = " + this.getASTCreateString(lt1Value2) + "");
                }
                if (this.grammar instanceof TreeWalkerGrammar) {
                    this.println(string2 + "_in = " + lt1Value2 + "");
                }
            }
            if (this.genAST) {
                switch (alternativeElement.getAutoGenType()) {
                    case 1: {
                        this.println("self.addASTChild(currentAST, " + string2 + ")");
                        break;
                    }
                    case 2: {
                        this.println("self.makeASTRoot(currentAST, " + string2 + ")");
                        break;
                    }
                }
            }
            if (b2) {}
        }
    }
    
    private void genErrorCatchForElement(final AlternativeElement alternativeElement) {
        if (alternativeElement.getLabel() == null) {
            return;
        }
        String s = alternativeElement.enclosingRuleName;
        if (this.grammar instanceof LexerGrammar) {
            s = CodeGenerator.encodeLexerRuleName(alternativeElement.enclosingRuleName);
        }
        final RuleSymbol ruleSymbol = (RuleSymbol)this.grammar.getSymbol(s);
        if (ruleSymbol == null) {
            this.antlrTool.panic("Enclosing rule not found!");
        }
        final ExceptionSpec exceptionSpec = ruleSymbol.block.findExceptionSpec(alternativeElement.getLabel());
        if (exceptionSpec != null) {
            --this.tabs;
            this.genErrorHandler(exceptionSpec);
        }
    }
    
    private void genErrorHandler(final ExceptionSpec exceptionSpec) {
        for (int i = 0; i < exceptionSpec.handlers.size(); ++i) {
            final ExceptionHandler exceptionHandler = (ExceptionHandler)exceptionSpec.handlers.elementAt(i);
            String substring = "";
            String substring2 = "";
            final String trim = this.removeAssignmentFromDeclaration(exceptionHandler.exceptionTypeAndName.getText()).trim();
            for (int j = trim.length() - 1; j >= 0; --j) {
                if (!Character.isLetterOrDigit(trim.charAt(j)) && trim.charAt(j) != '_') {
                    substring = trim.substring(0, j);
                    substring2 = trim.substring(j + 1);
                    break;
                }
            }
            this.println("except " + substring + ", " + substring2 + ":");
            ++this.tabs;
            if (this.grammar.hasSyntacticPredicate) {
                this.println("if not self.inputState.guessing:");
                ++this.tabs;
            }
            this.printAction(this.processActionForSpecialSymbols(exceptionHandler.action.getText(), exceptionHandler.action.getLine(), this.currentRule, new ActionTransInfo()));
            if (this.grammar.hasSyntacticPredicate) {
                --this.tabs;
                this.println("else:");
                ++this.tabs;
                this.println("raise " + substring2);
                --this.tabs;
            }
            --this.tabs;
        }
    }
    
    private void genErrorTryForElement(final AlternativeElement alternativeElement) {
        if (alternativeElement.getLabel() == null) {
            return;
        }
        String s = alternativeElement.enclosingRuleName;
        if (this.grammar instanceof LexerGrammar) {
            s = CodeGenerator.encodeLexerRuleName(alternativeElement.enclosingRuleName);
        }
        final RuleSymbol ruleSymbol = (RuleSymbol)this.grammar.getSymbol(s);
        if (ruleSymbol == null) {
            this.antlrTool.panic("Enclosing rule not found!");
        }
        if (ruleSymbol.block.findExceptionSpec(alternativeElement.getLabel()) != null) {
            this.println("try: # for error handling");
            ++this.tabs;
        }
    }
    
    protected void genASTDeclaration(final AlternativeElement alternativeElement) {
        this.genASTDeclaration(alternativeElement, this.labeledElementASTType);
    }
    
    protected void genASTDeclaration(final AlternativeElement alternativeElement, final String s) {
        this.genASTDeclaration(alternativeElement, alternativeElement.getLabel(), s);
    }
    
    protected void genASTDeclaration(final AlternativeElement alternativeElement, final String str, final String s) {
        if (this.declaredASTVariables.contains(alternativeElement)) {
            return;
        }
        this.println(str + "_AST = None");
        this.declaredASTVariables.put(alternativeElement, alternativeElement);
    }
    
    protected void genHeader() {
        this.println("### $ANTLR " + Tool.version + ": " + "\"" + this.antlrTool.fileMinusPath(this.antlrTool.grammarFile) + "\"" + " -> " + "\"" + this.grammar.getClassName() + ".py\"$");
    }
    
    protected void genLexerTest() {
        final String className = this.grammar.getClassName();
        this.println("if __name__ == '__main__' :");
        ++this.tabs;
        this.println("import sys");
        this.println("import antlr");
        this.println("import " + className);
        this.println("");
        this.println("### create lexer - shall read from stdin");
        this.println("try:");
        ++this.tabs;
        this.println("for token in " + className + ".Lexer():");
        ++this.tabs;
        this.println("print token");
        this.println("");
        --this.tabs;
        --this.tabs;
        this.println("except antlr.TokenStreamException, e:");
        ++this.tabs;
        this.println("print \"error: exception caught while lexing: \", e");
        --this.tabs;
        --this.tabs;
    }
    
    private void genLiteralsTest() {
        this.println("### option { testLiterals=true } ");
        this.println("_ttype = self.testLiteralsTable(_ttype)");
    }
    
    private void genLiteralsTestForPartialToken() {
        this.println("_ttype = self.testLiteralsTable(self.text.getString(), _begin, self.text.length()-_begin, _ttype)");
    }
    
    protected void genMatch(final BitSet set) {
    }
    
    protected void genMatch(final GrammarAtom obj) {
        if (obj instanceof StringLiteralElement) {
            if (this.grammar instanceof LexerGrammar) {
                this.genMatchUsingAtomText(obj);
            }
            else {
                this.genMatchUsingAtomTokenType(obj);
            }
        }
        else if (obj instanceof CharLiteralElement) {
            if (this.grammar instanceof LexerGrammar) {
                this.genMatchUsingAtomText(obj);
            }
            else {
                this.antlrTool.error("cannot ref character literals in grammar: " + obj);
            }
        }
        else if (obj instanceof TokenRefElement) {
            this.genMatchUsingAtomText(obj);
        }
        else if (obj instanceof WildcardElement) {
            this.gen((WildcardElement)obj);
        }
    }
    
    protected void genMatchUsingAtomText(final GrammarAtom grammarAtom) {
        String s = "";
        if (this.grammar instanceof TreeWalkerGrammar) {
            s = "_t,";
        }
        if (this.grammar instanceof LexerGrammar && (!this.saveText || grammarAtom.getAutoGenType() == 3)) {
            this.println("_saveIndex = self.text.length()");
        }
        this.print(grammarAtom.not ? "self.matchNot(" : "self.match(");
        this._print(s);
        if (grammarAtom.atomText.equals("EOF")) {
            this._print("EOF_TYPE");
        }
        else {
            this._print(grammarAtom.atomText);
        }
        this._println(")");
        if (this.grammar instanceof LexerGrammar && (!this.saveText || grammarAtom.getAutoGenType() == 3)) {
            this.println("self.text.setLength(_saveIndex)");
        }
    }
    
    protected void genMatchUsingAtomTokenType(final GrammarAtom grammarAtom) {
        String str = "";
        if (this.grammar instanceof TreeWalkerGrammar) {
            str = "_t,";
        }
        this.println((grammarAtom.not ? "self.matchNot(" : "self.match(") + (str + this.getValueString(grammarAtom.getType(), true)) + ")");
    }
    
    public void genNextToken() {
        boolean b = false;
        for (int i = 0; i < this.grammar.rules.size(); ++i) {
            final RuleSymbol ruleSymbol = (RuleSymbol)this.grammar.rules.elementAt(i);
            if (ruleSymbol.isDefined() && ruleSymbol.access.equals("public")) {
                b = true;
                break;
            }
        }
        if (!b) {
            this.println("");
            this.println("def nextToken(self): ");
            ++this.tabs;
            this.println("try:");
            ++this.tabs;
            this.println("self.uponEOF()");
            --this.tabs;
            this.println("except antlr.CharStreamIOException, csioe:");
            ++this.tabs;
            this.println("raise antlr.TokenStreamIOException(csioe.io)");
            --this.tabs;
            this.println("except antlr.CharStreamException, cse:");
            ++this.tabs;
            this.println("raise antlr.TokenStreamException(str(cse))");
            --this.tabs;
            this.println("return antlr.CommonToken(type=EOF_TYPE, text=\"\")");
            --this.tabs;
            return;
        }
        final RuleBlock nextTokenRule = MakeGrammar.createNextTokenRule(this.grammar, this.grammar.rules, "nextToken");
        final RuleSymbol ruleSymbol2 = new RuleSymbol("mnextToken");
        ruleSymbol2.setDefined();
        ruleSymbol2.setBlock(nextTokenRule);
        ruleSymbol2.access = "private";
        this.grammar.define(ruleSymbol2);
        this.grammar.theLLkAnalyzer.deterministic(nextTokenRule);
        String filterRule = null;
        if (((LexerGrammar)this.grammar).filterMode) {
            filterRule = ((LexerGrammar)this.grammar).filterRule;
        }
        this.println("");
        this.println("def nextToken(self):");
        ++this.tabs;
        this.println("while True:");
        ++this.tabs;
        this.println("try: ### try again ..");
        ++this.tabs;
        this.println("while True:");
        ++this.tabs;
        final int tabs = this.tabs;
        this.println("_token = None");
        this.println("_ttype = INVALID_TYPE");
        if (((LexerGrammar)this.grammar).filterMode) {
            this.println("self.setCommitToPath(False)");
            if (filterRule != null) {
                if (!this.grammar.isDefined(CodeGenerator.encodeLexerRuleName(filterRule))) {
                    this.grammar.antlrTool.error("Filter rule " + filterRule + " does not exist in this lexer");
                }
                else {
                    final RuleSymbol ruleSymbol3 = (RuleSymbol)this.grammar.getSymbol(CodeGenerator.encodeLexerRuleName(filterRule));
                    if (!ruleSymbol3.isDefined()) {
                        this.grammar.antlrTool.error("Filter rule " + filterRule + " does not exist in this lexer");
                    }
                    else if (ruleSymbol3.access.equals("public")) {
                        this.grammar.antlrTool.error("Filter rule " + filterRule + " must be protected");
                    }
                }
                this.println("_m = self.mark()");
            }
        }
        this.println("self.resetText()");
        this.println("try: ## for char stream error handling");
        ++this.tabs;
        final int tabs2 = this.tabs;
        this.println("try: ##for lexical error handling");
        ++this.tabs;
        final int tabs3 = this.tabs;
        for (int j = 0; j < nextTokenRule.getAlternatives().size(); ++j) {
            final Alternative alternative = nextTokenRule.getAlternativeAt(j);
            if (alternative.cache[1].containsEpsilon()) {
                this.antlrTool.warning("public lexical rule " + CodeGenerator.decodeLexerRuleName(((RuleRefElement)alternative.head).targetRule) + " is optional (can match \"nothing\")");
            }
        }
        System.getProperty("line.separator");
        final PythonBlockFinishingInfo genCommonBlock = this.genCommonBlock(nextTokenRule, false);
        final String s = "";
        String s2;
        if (((LexerGrammar)this.grammar).filterMode) {
            if (filterRule == null) {
                s2 = s + "self.filterdefault(self.LA(1))";
            }
            else {
                s2 = s + "self.filterdefault(self.LA(1), self.m" + filterRule + ", False)";
            }
        }
        else {
            s2 = "self.default(self.LA(1))";
        }
        this.genBlockFinish1(genCommonBlock, s2);
        this.tabs = tabs3;
        if (((LexerGrammar)this.grammar).filterMode && filterRule != null) {
            this.println("self.commit()");
        }
        this.println("if not self._returnToken:");
        ++this.tabs;
        this.println("raise antlr.TryAgain ### found SKIP token");
        --this.tabs;
        if (((LexerGrammar)this.grammar).getTestLiterals()) {
            this.println("### option { testLiterals=true } ");
            this.println("self.testForLiteral(self._returnToken)");
        }
        this.println("### return token to caller");
        this.println("return self._returnToken");
        --this.tabs;
        this.println("### handle lexical errors ....");
        this.println("except antlr.RecognitionException, e:");
        ++this.tabs;
        if (((LexerGrammar)this.grammar).filterMode) {
            if (filterRule == null) {
                this.println("if not self.getCommitToPath():");
                ++this.tabs;
                this.println("self.consume()");
                this.println("raise antlr.TryAgain()");
                --this.tabs;
            }
            else {
                this.println("if not self.getCommitToPath(): ");
                ++this.tabs;
                this.println("self.rewind(_m)");
                this.println("self.resetText()");
                this.println("try:");
                ++this.tabs;
                this.println("self.m" + filterRule + "(False)");
                --this.tabs;
                this.println("except antlr.RecognitionException, ee:");
                ++this.tabs;
                this.println("### horrendous failure: error in filter rule");
                this.println("self.reportError(ee)");
                this.println("self.consume()");
                --this.tabs;
                this.println("raise antlr.TryAgain()");
                --this.tabs;
            }
        }
        if (nextTokenRule.getDefaultErrorHandler()) {
            this.println("self.reportError(e)");
            this.println("self.consume()");
        }
        else {
            this.println("raise antlr.TokenStreamRecognitionException(e)");
        }
        --this.tabs;
        --this.tabs;
        this.println("### handle char stream errors ...");
        this.println("except antlr.CharStreamException,cse:");
        ++this.tabs;
        this.println("if isinstance(cse, antlr.CharStreamIOException):");
        ++this.tabs;
        this.println("raise antlr.TokenStreamIOException(cse.io)");
        --this.tabs;
        this.println("else:");
        ++this.tabs;
        this.println("raise antlr.TokenStreamException(str(cse))");
        --this.tabs;
        --this.tabs;
        --this.tabs;
        --this.tabs;
        this.println("except antlr.TryAgain:");
        ++this.tabs;
        this.println("pass");
        --this.tabs;
        --this.tabs;
    }
    
    public void genRule(final RuleSymbol ruleSymbol, final boolean b, final int n) {
        this.tabs = 1;
        if (!ruleSymbol.isDefined()) {
            this.antlrTool.error("undefined rule: " + ruleSymbol.getId());
            return;
        }
        final RuleBlock block = ruleSymbol.getBlock();
        this.currentRule = block;
        this.currentASTResult = ruleSymbol.getId();
        this.declaredASTVariables.clear();
        final boolean genAST = this.genAST;
        this.genAST = (this.genAST && block.getAutoGen());
        this.saveText = block.getAutoGen();
        this.genJavadocComment(ruleSymbol);
        this.print("def " + ruleSymbol.getId() + "(");
        this._print(this.commonExtraParams);
        if (this.commonExtraParams.length() != 0 && block.argAction != null) {
            this._print(",");
        }
        if (block.argAction != null) {
            this._println("");
            ++this.tabs;
            this.println(block.argAction);
            --this.tabs;
            this.print("):");
        }
        else {
            this._print("):");
        }
        this.println("");
        ++this.tabs;
        if (block.returnAction != null) {
            if (block.returnAction.indexOf(61) >= 0) {
                this.println(block.returnAction);
            }
            else {
                this.println(this.extractIdOfAction(block.returnAction, block.getLine(), block.getColumn()) + " = None");
            }
        }
        this.println(this.commonLocalVars);
        if (this.grammar.traceRules) {
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println("self.traceIn(\"" + ruleSymbol.getId() + "\",_t)");
            }
            else {
                this.println("self.traceIn(\"" + ruleSymbol.getId() + "\")");
            }
        }
        if (this.grammar instanceof LexerGrammar) {
            if (ruleSymbol.getId().equals("mEOF")) {
                this.println("_ttype = EOF_TYPE");
            }
            else {
                this.println("_ttype = " + ruleSymbol.getId().substring(1));
            }
            this.println("_saveIndex = 0");
        }
        if (this.grammar.debuggingOutput) {
            if (this.grammar instanceof ParserGrammar) {
                this.println("self.fireEnterRule(" + n + ", 0)");
            }
            else if (this.grammar instanceof LexerGrammar) {
                this.println("self.fireEnterRule(" + n + ", _ttype)");
            }
        }
        if (this.grammar.debuggingOutput || this.grammar.traceRules) {
            this.println("try: ### debugging");
            ++this.tabs;
        }
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println(ruleSymbol.getId() + "_AST_in = None");
            this.println("if _t != antlr.ASTNULL:");
            ++this.tabs;
            this.println(ruleSymbol.getId() + "_AST_in = _t");
            --this.tabs;
        }
        if (this.grammar.buildAST) {
            this.println("self.returnAST = None");
            this.println("currentAST = antlr.ASTPair()");
            this.println(ruleSymbol.getId() + "_AST = None");
        }
        this.genBlockPreamble(block);
        this.genBlockInitAction(block);
        final ExceptionSpec exceptionSpec = block.findExceptionSpec("");
        if (exceptionSpec != null || block.getDefaultErrorHandler()) {
            this.println("try:      ## for error handling");
            ++this.tabs;
        }
        final int tabs = this.tabs;
        if (block.alternatives.size() == 1) {
            final Alternative alternative = block.getAlternativeAt(0);
            final String semPred = alternative.semPred;
            if (semPred != null) {
                this.genSemPred(semPred, this.currentRule.line);
            }
            if (alternative.synPred != null) {
                this.antlrTool.warning("Syntactic predicate ignored for single alternative", this.grammar.getFilename(), alternative.synPred.getLine(), alternative.synPred.getColumn());
            }
            this.genAlt(alternative, block);
        }
        else {
            this.grammar.theLLkAnalyzer.deterministic(block);
            this.genBlockFinish(this.genCommonBlock(block, false), this.throwNoViable);
        }
        this.tabs = tabs;
        if (exceptionSpec != null || block.getDefaultErrorHandler()) {
            --this.tabs;
            this.println("");
        }
        if (exceptionSpec != null) {
            this.genErrorHandler(exceptionSpec);
        }
        else if (block.getDefaultErrorHandler()) {
            this.println("except " + this.exceptionThrown + ", ex:");
            ++this.tabs;
            if (this.grammar.hasSyntacticPredicate) {
                this.println("if not self.inputState.guessing:");
                ++this.tabs;
            }
            this.println("self.reportError(ex)");
            if (!(this.grammar instanceof TreeWalkerGrammar)) {
                final String bitsetName = this.getBitsetName(this.markBitsetForGen(this.grammar.theLLkAnalyzer.FOLLOW(1, block.endNode).fset));
                this.println("self.consume()");
                this.println("self.consumeUntil(" + bitsetName + ")");
            }
            else {
                this.println("if _t:");
                ++this.tabs;
                this.println("_t = _t.getNextSibling()");
                --this.tabs;
            }
            if (this.grammar.hasSyntacticPredicate) {
                --this.tabs;
                this.println("else:");
                ++this.tabs;
                this.println("raise ex");
                --this.tabs;
            }
            --this.tabs;
            this.println("");
        }
        if (this.grammar.buildAST) {
            this.println("self.returnAST = " + ruleSymbol.getId() + "_AST");
        }
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("self._retTree = _t");
        }
        if (block.getTestLiterals()) {
            if (ruleSymbol.access.equals("protected")) {
                this.genLiteralsTestForPartialToken();
            }
            else {
                this.genLiteralsTest();
            }
        }
        if (this.grammar instanceof LexerGrammar) {
            this.println("self.set_return_token(_createToken, _token, _ttype, _begin)");
        }
        if (block.returnAction != null) {
            this.println("return " + this.extractIdOfAction(block.returnAction, block.getLine(), block.getColumn()) + "");
        }
        if (this.grammar.debuggingOutput || this.grammar.traceRules) {
            --this.tabs;
            this.println("finally:  ### debugging");
            ++this.tabs;
            if (this.grammar.debuggingOutput) {
                if (this.grammar instanceof ParserGrammar) {
                    this.println("self.fireExitRule(" + n + ", 0)");
                }
                else if (this.grammar instanceof LexerGrammar) {
                    this.println("self.fireExitRule(" + n + ", _ttype)");
                }
            }
            if (this.grammar.traceRules) {
                if (this.grammar instanceof TreeWalkerGrammar) {
                    this.println("self.traceOut(\"" + ruleSymbol.getId() + "\", _t)");
                }
                else {
                    this.println("self.traceOut(\"" + ruleSymbol.getId() + "\")");
                }
            }
            --this.tabs;
        }
        --this.tabs;
        this.println("");
        this.genAST = genAST;
    }
    
    private void GenRuleInvocation(final RuleRefElement ruleRefElement) {
        this._print("self." + ruleRefElement.targetRule + "(");
        if (this.grammar instanceof LexerGrammar) {
            if (ruleRefElement.getLabel() != null) {
                this._print("True");
            }
            else {
                this._print("False");
            }
            if (this.commonExtraArgs.length() != 0 || ruleRefElement.args != null) {
                this._print(", ");
            }
        }
        this._print(this.commonExtraArgs);
        if (this.commonExtraArgs.length() != 0 && ruleRefElement.args != null) {
            this._print(", ");
        }
        final RuleSymbol ruleSymbol = (RuleSymbol)this.grammar.getSymbol(ruleRefElement.targetRule);
        if (ruleRefElement.args != null) {
            final ActionTransInfo actionTransInfo = new ActionTransInfo();
            final String processActionForSpecialSymbols = this.processActionForSpecialSymbols(ruleRefElement.args, 0, this.currentRule, actionTransInfo);
            if (actionTransInfo.assignToRoot || actionTransInfo.refRuleRoot != null) {
                this.antlrTool.error("Arguments of rule reference '" + ruleRefElement.targetRule + "' cannot set or ref #" + this.currentRule.getRuleName(), this.grammar.getFilename(), ruleRefElement.getLine(), ruleRefElement.getColumn());
            }
            this._print(processActionForSpecialSymbols);
            if (ruleSymbol.block.argAction == null) {
                this.antlrTool.warning("Rule '" + ruleRefElement.targetRule + "' accepts no arguments", this.grammar.getFilename(), ruleRefElement.getLine(), ruleRefElement.getColumn());
            }
        }
        else if (ruleSymbol.block.argAction != null) {
            this.antlrTool.warning("Missing parameters on reference to rule " + ruleRefElement.targetRule, this.grammar.getFilename(), ruleRefElement.getLine(), ruleRefElement.getColumn());
        }
        this._println(")");
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = self._retTree");
        }
    }
    
    protected void genSemPred(String s, final int n) {
        s = this.processActionForSpecialSymbols(s, n, this.currentRule, new ActionTransInfo());
        final String escapeString = this.charFormatter.escapeString(s);
        if (this.grammar.debuggingOutput && (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar)) {
            s = "fireSemanticPredicateEvaluated(antlr.debug.SemanticPredicateEvent.VALIDATING," + this.addSemPred(escapeString) + ", " + s + ")";
        }
        this.println("if not " + s + ":");
        ++this.tabs;
        this.println("raise antlr.SemanticException(\"" + escapeString + "\")");
        --this.tabs;
    }
    
    protected void genSemPredMap() {
        final Enumeration elements = this.semPreds.elements();
        this.println("_semPredNames = [");
        ++this.tabs;
        while (elements.hasMoreElements()) {
            this.println("\"" + elements.nextElement() + "\",");
        }
        --this.tabs;
        this.println("]");
    }
    
    protected void genSynPred(final SynPredBlock obj, final String str) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("gen=>(" + obj + ")");
        }
        this.println("synPredMatched" + obj.ID + " = False");
        this.println("if " + str + ":");
        ++this.tabs;
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t" + obj.ID + " = _t");
        }
        else {
            this.println("_m" + obj.ID + " = self.mark()");
        }
        this.println("synPredMatched" + obj.ID + " = True");
        this.println("self.inputState.guessing += 1");
        if (this.grammar.debuggingOutput && (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar)) {
            this.println("self.fireSyntacticPredicateStarted()");
        }
        ++this.syntacticPredLevel;
        this.println("try:");
        ++this.tabs;
        this.gen(obj);
        --this.tabs;
        this.println("except " + this.exceptionThrown + ", pe:");
        ++this.tabs;
        this.println("synPredMatched" + obj.ID + " = False");
        --this.tabs;
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _t" + obj.ID + "");
        }
        else {
            this.println("self.rewind(_m" + obj.ID + ")");
        }
        this.println("self.inputState.guessing -= 1");
        if (this.grammar.debuggingOutput && (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar)) {
            this.println("if synPredMatched" + obj.ID + ":");
            ++this.tabs;
            this.println("self.fireSyntacticPredicateSucceeded()");
            --this.tabs;
            this.println("else:");
            ++this.tabs;
            this.println("self.fireSyntacticPredicateFailed()");
            --this.tabs;
        }
        --this.syntacticPredLevel;
        --this.tabs;
        this.println("if synPredMatched" + obj.ID + ":");
    }
    
    public void genTokenStrings() {
        final int tabs = this.tabs;
        this.tabs = 0;
        this.println("");
        this.println("_tokenNames = [");
        ++this.tabs;
        final Vector vocabulary = this.grammar.tokenManager.getVocabulary();
        for (int i = 0; i < vocabulary.size(); ++i) {
            String s = (String)vocabulary.elementAt(i);
            if (s == null) {
                s = "<" + String.valueOf(i) + ">";
            }
            if (!s.startsWith("\"") && !s.startsWith("<")) {
                final TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbol(s);
                if (tokenSymbol != null && tokenSymbol.getParaphrase() != null) {
                    s = StringUtils.stripFrontBack(tokenSymbol.getParaphrase(), "\"", "\"");
                }
            }
            this.print(this.charFormatter.literalString(s));
            if (i != vocabulary.size() - 1) {
                this._print(", ");
            }
            this._println("");
        }
        --this.tabs;
        this.println("]");
        this.tabs = tabs;
    }
    
    protected void genTokenASTNodeMap() {
        this.println("");
        this.println("def buildTokenTypeASTClassMap(self):");
        ++this.tabs;
        int n = 0;
        int n2 = 0;
        final Vector vocabulary = this.grammar.tokenManager.getVocabulary();
        for (int i = 0; i < vocabulary.size(); ++i) {
            final String s = (String)vocabulary.elementAt(i);
            if (s != null) {
                final TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbol(s);
                if (tokenSymbol != null && tokenSymbol.getASTNodeType() != null) {
                    ++n2;
                    if (n == 0) {
                        this.println("self.tokenTypeToASTClassMap = {}");
                        n = 1;
                    }
                    this.println("self.tokenTypeToASTClassMap[" + tokenSymbol.getTokenType() + "] = " + tokenSymbol.getASTNodeType());
                }
            }
        }
        if (n2 == 0) {
            this.println("self.tokenTypeToASTClassMap = None");
        }
        --this.tabs;
    }
    
    protected void genTokenTypes(final TokenManager tokenManager) throws IOException {
        this.tabs = 0;
        final Vector vocabulary = tokenManager.getVocabulary();
        this.println("SKIP                = antlr.SKIP");
        this.println("INVALID_TYPE        = antlr.INVALID_TYPE");
        this.println("EOF_TYPE            = antlr.EOF_TYPE");
        this.println("EOF                 = antlr.EOF");
        this.println("NULL_TREE_LOOKAHEAD = antlr.NULL_TREE_LOOKAHEAD");
        this.println("MIN_USER_TYPE       = antlr.MIN_USER_TYPE");
        for (int i = 4; i < vocabulary.size(); ++i) {
            final String str = (String)vocabulary.elementAt(i);
            if (str != null) {
                if (str.startsWith("\"")) {
                    final StringLiteralSymbol stringLiteralSymbol = (StringLiteralSymbol)tokenManager.getTokenSymbol(str);
                    if (stringLiteralSymbol == null) {
                        this.antlrTool.panic("String literal " + str + " not in symbol table");
                    }
                    if (stringLiteralSymbol.label != null) {
                        this.println(stringLiteralSymbol.label + " = " + i);
                    }
                    else {
                        final String mangleLiteral = this.mangleLiteral(str);
                        if (mangleLiteral != null) {
                            this.println(mangleLiteral + " = " + i);
                            stringLiteralSymbol.label = mangleLiteral;
                        }
                        else {
                            this.println("### " + str + " = " + i);
                        }
                    }
                }
                else if (!str.startsWith("<")) {
                    this.println(str + " = " + i);
                }
            }
        }
        --this.tabs;
        this.exitIfError();
    }
    
    public String getASTCreateString(final Vector vector) {
        if (vector.size() == 0) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        sb.append("antlr.make(");
        for (int i = 0; i < vector.size(); ++i) {
            sb.append(vector.elementAt(i));
            if (i + 1 < vector.size()) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
    
    public String getASTCreateString(final GrammarAtom grammarAtom, final String str) {
        if (grammarAtom != null && grammarAtom.getASTNodeType() != null) {
            return "self.astFactory.create(" + str + ", " + grammarAtom.getASTNodeType() + ")";
        }
        return this.getASTCreateString(str);
    }
    
    public String getASTCreateString(String s) {
        if (s == null) {
            s = "";
        }
        int n = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == ',') {
                ++n;
            }
        }
        if (n >= 2) {
            return "self.astFactory.create(" + s + ")";
        }
        final int index = s.indexOf(44);
        s.lastIndexOf(44);
        String substring = s;
        if (n > 0) {
            substring = s.substring(0, index);
        }
        final TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbol(substring);
        if (tokenSymbol != null) {
            final String astNodeType = tokenSymbol.getASTNodeType();
            String str = "";
            if (n == 0) {
                str = ", \"\"";
            }
            if (astNodeType != null) {
                return "self.astFactory.create(" + s + str + ", " + astNodeType + ")";
            }
        }
        if (this.labeledElementASTType.equals("AST")) {
            return "self.astFactory.create(" + s + ")";
        }
        return "self.astFactory.create(" + s + ")";
    }
    
    protected String getLookaheadTestExpression(final Lookahead[] array, final int n) {
        final StringBuffer sb = new StringBuffer(100);
        int n2 = 1;
        sb.append("(");
        for (int i = 1; i <= n; ++i) {
            final BitSet fset = array[i].fset;
            if (n2 == 0) {
                sb.append(") and (");
            }
            n2 = 0;
            if (array[i].containsEpsilon()) {
                sb.append("True");
            }
            else {
                sb.append(this.getLookaheadTestTerm(i, fset));
            }
        }
        sb.append(")");
        return sb.toString();
    }
    
    protected String getLookaheadTestExpression(final Alternative alternative, final int n) {
        int n2 = alternative.lookaheadDepth;
        if (n2 == Integer.MAX_VALUE) {
            n2 = this.grammar.maxk;
        }
        if (n == 0) {
            return "True";
        }
        return this.getLookaheadTestExpression(alternative.cache, n2);
    }
    
    protected String getLookaheadTestTerm(final int n, final BitSet set) {
        final String lookaheadString = this.lookaheadString(n);
        final int[] array = set.toArray();
        if (CodeGenerator.elementsAreRange(array)) {
            return this.getRangeExpression(n, array);
        }
        final int degree = set.degree();
        if (degree == 0) {
            return "True";
        }
        if (degree >= this.bitsetTestThreshold) {
            return this.getBitsetName(this.markBitsetForGen(set)) + ".member(" + lookaheadString + ")";
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            final String valueString = this.getValueString(array[i], true);
            if (i > 0) {
                sb.append(" or ");
            }
            sb.append(lookaheadString);
            sb.append("==");
            sb.append(valueString);
        }
        sb.toString();
        return sb.toString();
    }
    
    public String getRangeExpression(final int n, final int[] array) {
        if (!CodeGenerator.elementsAreRange(array)) {
            this.antlrTool.panic("getRangeExpression called with non-range");
        }
        return "(" + this.lookaheadString(n) + " >= " + this.getValueString(array[0], true) + " and " + this.lookaheadString(n) + " <= " + this.getValueString(array[array.length - 1], true) + ")";
    }
    
    private String getValueString(final int n, final boolean b) {
        if (this.grammar instanceof LexerGrammar) {
            String str = this.charFormatter.literalChar(n);
            if (b) {
                str = "u'" + str + "'";
            }
            return str;
        }
        final TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbolAt(n);
        if (tokenSymbol == null) {
            return "" + n;
        }
        final String id = tokenSymbol.getId();
        if (!(tokenSymbol instanceof StringLiteralSymbol)) {
            return id;
        }
        final String label = ((StringLiteralSymbol)tokenSymbol).getLabel();
        String s;
        if (label != null) {
            s = label;
        }
        else {
            s = this.mangleLiteral(id);
            if (s == null) {
                s = String.valueOf(n);
            }
        }
        return s;
    }
    
    protected boolean lookaheadIsEmpty(final Alternative alternative, final int n) {
        int n2 = alternative.lookaheadDepth;
        if (n2 == Integer.MAX_VALUE) {
            n2 = this.grammar.maxk;
        }
        for (int n3 = 1; n3 <= n2 && n3 <= n; ++n3) {
            if (alternative.cache[n3].fset.degree() != 0) {
                return false;
            }
        }
        return true;
    }
    
    private String lookaheadString(final int i) {
        if (this.grammar instanceof TreeWalkerGrammar) {
            return "_t.getType()";
        }
        return "self.LA(" + i + ")";
    }
    
    private String mangleLiteral(final String s) {
        String str = this.antlrTool.literalsPrefix;
        for (int i = 1; i < s.length() - 1; ++i) {
            if (!Character.isLetter(s.charAt(i)) && s.charAt(i) != '_') {
                return null;
            }
            str += s.charAt(i);
        }
        if (this.antlrTool.upperCaseMangledLiterals) {
            str = str.toUpperCase();
        }
        return str;
    }
    
    public String mapTreeId(final String s, final ActionTransInfo actionTransInfo) {
        if (this.currentRule == null) {
            return s;
        }
        boolean b = false;
        String substring = s;
        if (this.grammar instanceof TreeWalkerGrammar) {
            if (!this.grammar.buildAST) {
                b = true;
            }
            else if (substring.length() > 3 && substring.lastIndexOf("_in") == substring.length() - 3) {
                substring = substring.substring(0, substring.length() - 3);
                b = true;
            }
        }
        for (int i = 0; i < this.currentRule.labeledElements.size(); ++i) {
            if (((AlternativeElement)this.currentRule.labeledElements.elementAt(i)).getLabel().equals(substring)) {
                return b ? substring : (substring + "_AST");
            }
        }
        final String str = this.treeVariableMap.get(substring);
        if (str != null) {
            if (str == PythonCodeGenerator.NONUNIQUE) {
                this.antlrTool.error("Ambiguous reference to AST element " + substring + " in rule " + this.currentRule.getRuleName());
                return null;
            }
            if (str.equals(this.currentRule.getRuleName())) {
                this.antlrTool.error("Ambiguous reference to AST element " + substring + " in rule " + this.currentRule.getRuleName());
                return null;
            }
            return b ? (str + "_in") : str;
        }
        else {
            if (substring.equals(this.currentRule.getRuleName())) {
                final String refRuleRoot = b ? (substring + "_AST_in") : (substring + "_AST");
                if (actionTransInfo != null && !b) {
                    actionTransInfo.refRuleRoot = refRuleRoot;
                }
                return refRuleRoot;
            }
            return substring;
        }
    }
    
    private void mapTreeVariable(final AlternativeElement alternativeElement, final String value) {
        if (alternativeElement instanceof TreeElement) {
            this.mapTreeVariable(((TreeElement)alternativeElement).root, value);
            return;
        }
        String s = null;
        if (alternativeElement.getLabel() == null) {
            if (alternativeElement instanceof TokenRefElement) {
                s = ((TokenRefElement)alternativeElement).atomText;
            }
            else if (alternativeElement instanceof RuleRefElement) {
                s = ((RuleRefElement)alternativeElement).targetRule;
            }
        }
        if (s != null) {
            if (this.treeVariableMap.get(s) != null) {
                this.treeVariableMap.remove(s);
                this.treeVariableMap.put(s, PythonCodeGenerator.NONUNIQUE);
            }
            else {
                this.treeVariableMap.put(s, value);
            }
        }
    }
    
    protected String processActionForSpecialSymbols(String text, final int lineOffset, final RuleBlock ruleBlock, final ActionTransInfo actionTransInfo) {
        if (text == null || text.length() == 0) {
            return null;
        }
        if (isEmpty(text)) {
            return "";
        }
        if (this.grammar == null) {
            return text;
        }
        final ActionLexer actionLexer = new ActionLexer(text, ruleBlock, this, actionTransInfo);
        actionLexer.setLineOffset(lineOffset);
        actionLexer.setFilename(this.grammar.getFilename());
        actionLexer.setTool(this.antlrTool);
        try {
            actionLexer.mACTION(true);
            text = actionLexer.getTokenObject().getText();
        }
        catch (RecognitionException ex) {
            actionLexer.reportError(ex);
        }
        catch (TokenStreamException ex2) {
            this.antlrTool.panic("Error reading action:" + text);
        }
        catch (CharStreamException ex3) {
            this.antlrTool.panic("Error reading action:" + text);
        }
        return text;
    }
    
    static boolean isEmpty(final String s) {
        boolean b = true;
        for (int index = 0; b && index < s.length(); ++index) {
            switch (s.charAt(index)) {
                case '\t':
                case '\n':
                case '\f':
                case '\r':
                case ' ': {
                    break;
                }
                default: {
                    b = false;
                    break;
                }
            }
        }
        return b;
    }
    
    protected String processActionCode(String text, final int n) {
        if (text == null || isEmpty(text)) {
            return "";
        }
        final CodeLexer codeLexer = new CodeLexer(text, this.grammar.getFilename(), n, this.antlrTool);
        try {
            codeLexer.mACTION(true);
            text = codeLexer.getTokenObject().getText();
        }
        catch (RecognitionException ex) {
            codeLexer.reportError(ex);
        }
        catch (TokenStreamException ex2) {
            this.antlrTool.panic("Error reading action:" + text);
        }
        catch (CharStreamException ex3) {
            this.antlrTool.panic("Error reading action:" + text);
        }
        return text;
    }
    
    protected void printActionCode(String processActionCode, final int n) {
        processActionCode = this.processActionCode(processActionCode, n);
        this.printAction(processActionCode);
    }
    
    private void setupGrammarParameters(final Grammar grammar) {
        if (grammar instanceof ParserGrammar) {
            this.labeledElementASTType = "";
            if (grammar.hasOption("ASTLabelType")) {
                final Token option = grammar.getOption("ASTLabelType");
                if (option != null) {
                    final String stripFrontBack = StringUtils.stripFrontBack(option.getText(), "\"", "\"");
                    if (stripFrontBack != null) {
                        this.labeledElementASTType = stripFrontBack;
                    }
                }
            }
            this.labeledElementType = "";
            this.labeledElementInit = "None";
            this.commonExtraArgs = "";
            this.commonExtraParams = "self";
            this.commonLocalVars = "";
            this.lt1Value = "self.LT(1)";
            this.exceptionThrown = "antlr.RecognitionException";
            this.throwNoViable = "raise antlr.NoViableAltException(self.LT(1), self.getFilename())";
            this.parserClassName = "Parser";
            if (grammar.hasOption("className")) {
                final Token option2 = grammar.getOption("className");
                if (option2 != null) {
                    final String stripFrontBack2 = StringUtils.stripFrontBack(option2.getText(), "\"", "\"");
                    if (stripFrontBack2 != null) {
                        this.parserClassName = stripFrontBack2;
                    }
                }
            }
            return;
        }
        if (grammar instanceof LexerGrammar) {
            this.labeledElementType = "char ";
            this.labeledElementInit = "'\\0'";
            this.commonExtraArgs = "";
            this.commonExtraParams = "self, _createToken";
            this.commonLocalVars = "_ttype = 0\n        _token = None\n        _begin = self.text.length()";
            this.lt1Value = "self.LA(1)";
            this.exceptionThrown = "antlr.RecognitionException";
            this.throwNoViable = "self.raise_NoViableAlt(self.LA(1))";
            this.lexerClassName = "Lexer";
            if (grammar.hasOption("className")) {
                final Token option3 = grammar.getOption("className");
                if (option3 != null) {
                    final String stripFrontBack3 = StringUtils.stripFrontBack(option3.getText(), "\"", "\"");
                    if (stripFrontBack3 != null) {
                        this.lexerClassName = stripFrontBack3;
                    }
                }
            }
            return;
        }
        if (grammar instanceof TreeWalkerGrammar) {
            this.labeledElementASTType = "";
            this.labeledElementType = "";
            if (grammar.hasOption("ASTLabelType")) {
                final Token option4 = grammar.getOption("ASTLabelType");
                if (option4 != null) {
                    final String stripFrontBack4 = StringUtils.stripFrontBack(option4.getText(), "\"", "\"");
                    if (stripFrontBack4 != null) {
                        this.labeledElementASTType = stripFrontBack4;
                        this.labeledElementType = stripFrontBack4;
                    }
                }
            }
            if (!grammar.hasOption("ASTLabelType")) {
                grammar.setOption("ASTLabelType", new Token(6, "<4>AST"));
            }
            this.labeledElementInit = "None";
            this.commonExtraArgs = "_t";
            this.commonExtraParams = "self, _t";
            this.commonLocalVars = "";
            this.lt1Value = "_t";
            this.exceptionThrown = "antlr.RecognitionException";
            this.throwNoViable = "raise antlr.NoViableAltException(_t)";
            this.treeWalkerClassName = "Walker";
            if (grammar.hasOption("className")) {
                final Token option5 = grammar.getOption("className");
                if (option5 != null) {
                    final String stripFrontBack5 = StringUtils.stripFrontBack(option5.getText(), "\"", "\"");
                    if (stripFrontBack5 != null) {
                        this.treeWalkerClassName = stripFrontBack5;
                    }
                }
            }
            return;
        }
        this.antlrTool.panic("Unknown grammar type");
    }
    
    public void setupOutput(final String str) throws IOException {
        this.currentOutput = this.antlrTool.openOutputFile(str + ".py");
    }
    
    protected boolean isspace(final char c) {
        boolean b = true;
        switch (c) {
            case '\t':
            case '\n':
            case '\r':
            case ' ': {
                break;
            }
            default: {
                b = false;
                break;
            }
        }
        return b;
    }
    
    protected void _printAction(final String s) {
        if (s == null) {
            return;
        }
        int index = 0;
        final int length = s.length();
        int n = 0;
        int n2 = 1;
        while (index < length && n2 != 0) {
            switch (s.charAt(index++)) {
                case '\n': {
                    n = index;
                    continue;
                }
                case '\r': {
                    if (index <= length && s.charAt(index) == '\n') {
                        ++index;
                    }
                    n = index;
                    continue;
                }
                case ' ': {
                    continue;
                }
                default: {
                    n2 = 0;
                    continue;
                }
            }
        }
        if (n2 == 0) {
            --index;
        }
        final int n3 = index - n;
        int index2;
        for (index2 = length - 1; index2 > index && this.isspace(s.charAt(index2)); --index2) {}
        int n4 = 0;
        for (int i = index; i <= index2; ++i) {
            final char char1 = s.charAt(i);
            switch (char1) {
                case 10: {
                    n4 = 1;
                    break;
                }
                case 13: {
                    n4 = 1;
                    if (i + 1 <= index2 && s.charAt(i + 1) == '\n') {
                        ++i;
                        break;
                    }
                    break;
                }
                case 9: {
                    System.err.println("warning: tab characters used in Python action");
                    this.currentOutput.print("        ");
                    break;
                }
                case 32: {
                    this.currentOutput.print(" ");
                    break;
                }
                default: {
                    this.currentOutput.print(char1);
                    break;
                }
            }
            if (n4 != 0) {
                this.currentOutput.print("\n");
                this.printTabs();
                int n5 = 0;
                n4 = 0;
                for (++i; i <= index2; ++i) {
                    final char char2 = s.charAt(i);
                    if (!this.isspace(char2)) {
                        --i;
                        break;
                    }
                    switch (char2) {
                        case '\n': {
                            n4 = 1;
                            break;
                        }
                        case '\r': {
                            if (i + 1 <= index2 && s.charAt(i + 1) == '\n') {
                                ++i;
                            }
                            n4 = 1;
                            break;
                        }
                    }
                    if (n4 != 0) {
                        this.currentOutput.print("\n");
                        this.printTabs();
                        n5 = 0;
                        n4 = 0;
                    }
                    else {
                        if (n5 >= n3) {
                            break;
                        }
                        ++n5;
                    }
                }
            }
        }
        this.currentOutput.println();
    }
    
    protected void od(final String s, final int n, final int n2, final String x) {
        System.out.println(x);
        for (int i = n; i <= n2; ++i) {
            final char char1 = s.charAt(i);
            switch (char1) {
                case 10: {
                    System.out.print(" nl ");
                    break;
                }
                case 9: {
                    System.out.print(" ht ");
                    break;
                }
                case 32: {
                    System.out.print(" sp ");
                    break;
                }
                default: {
                    System.out.print(" " + char1 + " ");
                    break;
                }
            }
        }
        System.out.println("");
    }
    
    protected void printAction(final String s) {
        if (s != null) {
            this.printTabs();
            this._printAction(s);
        }
    }
    
    protected void printGrammarAction(final Grammar grammar) {
        this.println("### user action >>>");
        this.printAction(this.processActionForSpecialSymbols(grammar.classMemberAction.getText(), grammar.classMemberAction.getLine(), this.currentRule, null));
        this.println("### user action <<<");
    }
    
    protected void _printJavadoc(final String s) {
        final int length = s.length();
        final int n = 0;
        int n2 = 0;
        this.currentOutput.print("\n");
        this.printTabs();
        this.currentOutput.print("###");
        for (int i = n; i < length; ++i) {
            final char char1 = s.charAt(i);
            switch (char1) {
                case 10: {
                    n2 = 1;
                    break;
                }
                case 13: {
                    n2 = 1;
                    if (i + 1 <= length && s.charAt(i + 1) == '\n') {
                        ++i;
                        break;
                    }
                    break;
                }
                case 9: {
                    this.currentOutput.print("\t");
                    break;
                }
                case 32: {
                    this.currentOutput.print(" ");
                    break;
                }
                default: {
                    this.currentOutput.print(char1);
                    break;
                }
            }
            if (n2 != 0) {
                this.currentOutput.print("\n");
                this.printTabs();
                this.currentOutput.print("###");
                n2 = 0;
            }
        }
        this.currentOutput.println();
    }
    
    protected void genJavadocComment(final Grammar grammar) {
        if (grammar.comment != null) {
            this._printJavadoc(grammar.comment);
        }
    }
    
    protected void genJavadocComment(final RuleSymbol ruleSymbol) {
        if (ruleSymbol.comment != null) {
            this._printJavadoc(ruleSymbol.comment);
        }
    }
    
    static {
        NONUNIQUE = new String();
    }
}
