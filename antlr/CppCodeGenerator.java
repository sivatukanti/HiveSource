// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.actions.cpp.ActionLexer;
import antlr.collections.impl.BitSet;
import java.util.Enumeration;
import java.io.IOException;
import antlr.collections.impl.Vector;
import java.util.Hashtable;

public class CppCodeGenerator extends CodeGenerator
{
    boolean DEBUG_CPP_CODE_GENERATOR;
    protected int syntacticPredLevel;
    protected boolean genAST;
    protected boolean saveText;
    protected boolean genHashLines;
    protected boolean noConstructors;
    protected int outputLine;
    protected String outputFile;
    boolean usingCustomAST;
    String labeledElementType;
    String labeledElementASTType;
    String labeledElementASTInit;
    String labeledElementInit;
    String commonExtraArgs;
    String commonExtraParams;
    String commonLocalVars;
    String lt1Value;
    String exceptionThrown;
    String throwNoViable;
    RuleBlock currentRule;
    String currentASTResult;
    Hashtable treeVariableMap;
    Hashtable declaredASTVariables;
    int astVarNumber;
    protected static final String NONUNIQUE;
    public static final int caseSizeThreshold = 127;
    private Vector semPreds;
    private Vector astTypes;
    private static String namespaceStd;
    private static String namespaceAntlr;
    private static NameSpace nameSpace;
    private static final String preIncludeCpp = "pre_include_cpp";
    private static final String preIncludeHpp = "pre_include_hpp";
    private static final String postIncludeCpp = "post_include_cpp";
    private static final String postIncludeHpp = "post_include_hpp";
    
    public CppCodeGenerator() {
        this.DEBUG_CPP_CODE_GENERATOR = false;
        this.syntacticPredLevel = 0;
        this.genAST = false;
        this.saveText = false;
        this.genHashLines = true;
        this.noConstructors = false;
        this.usingCustomAST = false;
        this.treeVariableMap = new Hashtable();
        this.declaredASTVariables = new Hashtable();
        this.astVarNumber = 1;
        this.charFormatter = new CppCharFormatter();
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
    
    protected int countLines(final String s) {
        int n = 0;
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '\n') {
                ++n;
            }
        }
        return n;
    }
    
    protected void _print(final String s) {
        if (s != null) {
            this.outputLine += this.countLines(s);
            this.currentOutput.print(s);
        }
    }
    
    protected void _printAction(final String s) {
        if (s != null) {
            this.outputLine += this.countLines(s) + 1;
            super._printAction(s);
        }
    }
    
    public void printAction(final Token token) {
        if (token != null) {
            this.genLineNo(token.getLine());
            this.printTabs();
            this._printAction(this.processActionForSpecialSymbols(token.getText(), token.getLine(), null, null));
            this.genLineNo2();
        }
    }
    
    public void printHeaderAction(final String key) {
        final Token token = this.behavior.headerActions.get(key);
        if (token != null) {
            this.genLineNo(token.getLine());
            this.println(this.processActionForSpecialSymbols(token.getText(), token.getLine(), null, null));
            this.genLineNo2();
        }
    }
    
    protected void _println(final String x) {
        if (x != null) {
            this.outputLine += this.countLines(x) + 1;
            this.currentOutput.println(x);
        }
    }
    
    protected void println(final String x) {
        if (x != null) {
            this.printTabs();
            this.outputLine += this.countLines(x) + 1;
            this.currentOutput.println(x);
        }
    }
    
    public void genLineNo(int i) {
        if (i == 0) {
            ++i;
        }
        if (this.genHashLines) {
            this._println("#line " + i + " \"" + this.antlrTool.fileMinusPath(this.antlrTool.grammarFile) + "\"");
        }
    }
    
    public void genLineNo(final GrammarElement grammarElement) {
        if (grammarElement != null) {
            this.genLineNo(grammarElement.getLine());
        }
    }
    
    public void genLineNo(final Token token) {
        if (token != null) {
            this.genLineNo(token.getLine());
        }
    }
    
    public void genLineNo2() {
        if (this.genHashLines) {
            this._println("#line " + (this.outputLine + 1) + " \"" + this.outputFile + "\"");
        }
    }
    
    private boolean charIsDigit(final String s, final int index) {
        return index < s.length() && Character.isDigit(s.charAt(index));
    }
    
    private String convertJavaToCppString(final String s, final boolean b) {
        String s2 = new String();
        int i = 0;
        int j = 0;
        if (b) {
            if (!s.startsWith("'") || !s.endsWith("'")) {
                this.antlrTool.error("Invalid character literal: '" + s + "'");
            }
        }
        else if (!s.startsWith("\"") || !s.endsWith("\"")) {
            this.antlrTool.error("Invalid character string: '" + s + "'");
        }
        final String substring = s.substring(1, s.length() - 1);
        String str = "";
        int k = 255;
        if (this.grammar instanceof LexerGrammar) {
            k = ((LexerGrammar)this.grammar).charVocabulary.size() - 1;
            if (k > 255) {
                str = "L";
            }
        }
        while (i < substring.length()) {
            Label_0918: {
                if (substring.charAt(i) == '\\') {
                    if (substring.length() == i + 1) {
                        this.antlrTool.error("Invalid escape in char literal: '" + s + "' looking at '" + substring.substring(i) + "'");
                    }
                    switch (substring.charAt(i + 1)) {
                        case 'a': {
                            j = 7;
                            i += 2;
                            break Label_0918;
                        }
                        case 'b': {
                            j = 8;
                            i += 2;
                            break Label_0918;
                        }
                        case 't': {
                            j = 9;
                            i += 2;
                            break Label_0918;
                        }
                        case 'n': {
                            j = 10;
                            i += 2;
                            break Label_0918;
                        }
                        case 'f': {
                            j = 12;
                            i += 2;
                            break Label_0918;
                        }
                        case 'r': {
                            j = 13;
                            i += 2;
                            break Label_0918;
                        }
                        case '\"':
                        case '\'':
                        case '\\': {
                            j = substring.charAt(i + 1);
                            i += 2;
                            break Label_0918;
                        }
                        case 'u': {
                            if (i + 5 < substring.length()) {
                                j = Character.digit(substring.charAt(i + 2), 16) * 16 * 16 * 16 + Character.digit(substring.charAt(i + 3), 16) * 16 * 16 + Character.digit(substring.charAt(i + 4), 16) * 16 + Character.digit(substring.charAt(i + 5), 16);
                                i += 6;
                                break Label_0918;
                            }
                            this.antlrTool.error("Invalid escape in char literal: '" + s + "' looking at '" + substring.substring(i) + "'");
                            break Label_0918;
                        }
                        case '0':
                        case '1':
                        case '2':
                        case '3': {
                            if (!this.charIsDigit(substring, i + 2)) {
                                j = substring.charAt(i + 1) - '0';
                                i += 2;
                                break Label_0918;
                            }
                            if (this.charIsDigit(substring, i + 3)) {
                                j = (substring.charAt(i + 1) - '0') * 8 * 8 + (substring.charAt(i + 2) - '0') * 8 + (substring.charAt(i + 3) - '0');
                                i += 4;
                                break Label_0918;
                            }
                            j = (substring.charAt(i + 1) - '0') * 8 + (substring.charAt(i + 2) - '0');
                            i += 3;
                            break Label_0918;
                        }
                        case '4':
                        case '5':
                        case '6':
                        case '7': {
                            if (this.charIsDigit(substring, i + 2)) {
                                final int n = (substring.charAt(i + 1) - '0') * 8 + (substring.charAt(i + 2) - '0');
                                i += 3;
                                break;
                            }
                            final int n2 = substring.charAt(i + 1) - '0';
                            i += 2;
                            break;
                        }
                    }
                    this.antlrTool.error("Unhandled escape in char literal: '" + s + "' looking at '" + substring.substring(i) + "'");
                    j = 0;
                }
                else {
                    j = substring.charAt(i++);
                }
            }
            if (this.grammar instanceof LexerGrammar && j > k) {
                String str2;
                if (32 <= j && j < 127) {
                    str2 = this.charFormatter.escapeChar(j, true);
                }
                else {
                    str2 = "0x" + Integer.toString(j, 16);
                }
                this.antlrTool.error("Character out of range in " + (b ? "char literal" : "string constant") + ": '" + substring + "'");
                this.antlrTool.error("Vocabulary size: " + k + " Character " + str2);
            }
            if (b) {
                if (i != substring.length()) {
                    this.antlrTool.error("Invalid char literal: '" + s + "'");
                }
                if (k <= 255) {
                    if (j <= 255 && (j & 0x80) != 0x0) {
                        s2 = "static_cast<unsigned char>('" + this.charFormatter.escapeChar(j, true) + "')";
                    }
                    else {
                        s2 = "'" + this.charFormatter.escapeChar(j, true) + "'";
                    }
                }
                else {
                    s2 = "L'" + this.charFormatter.escapeChar(j, true) + "'";
                }
            }
            else {
                s2 += this.charFormatter.escapeChar(j, true);
            }
        }
        if (!b) {
            s2 = str + "\"" + s2 + "\"";
        }
        return s2;
    }
    
    public void gen() {
        try {
            final Enumeration<Grammar> elements = this.behavior.grammars.elements();
            while (elements.hasMoreElements()) {
                final Grammar grammar = elements.nextElement();
                if (grammar.debuggingOutput) {
                    this.antlrTool.error(grammar.getFilename() + ": C++ mode does not support -debug");
                }
                grammar.setGrammarAnalyzer(this.analyzer);
                grammar.setCodeGenerator(this);
                this.analyzer.setGrammar(grammar);
                this.setupGrammarParameters(grammar);
                grammar.generate();
                this.exitIfError();
            }
            final Enumeration<TokenManager> elements2 = this.behavior.tokenManagers.elements();
            while (elements2.hasMoreElements()) {
                final TokenManager tokenManager = elements2.nextElement();
                if (!tokenManager.isReadOnly()) {
                    this.genTokenTypes(tokenManager);
                    this.genTokenInterchange(tokenManager);
                }
                this.exitIfError();
            }
        }
        catch (IOException ex) {
            this.antlrTool.reportException(ex, null);
        }
    }
    
    public void gen(final ActionElement obj) {
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("genAction(" + obj + ")");
        }
        if (obj.isSemPred) {
            this.genSemPred(obj.actionText, obj.line);
        }
        else {
            if (this.grammar.hasSyntacticPredicate) {
                this.println("if ( inputState->guessing==0 ) {");
                ++this.tabs;
            }
            final ActionTransInfo actionTransInfo = new ActionTransInfo();
            final String processActionForSpecialSymbols = this.processActionForSpecialSymbols(obj.actionText, obj.getLine(), this.currentRule, actionTransInfo);
            if (actionTransInfo.refRuleRoot != null) {
                this.println(actionTransInfo.refRuleRoot + " = " + this.labeledElementASTType + "(currentAST.root);");
            }
            this.genLineNo(obj);
            this.printAction(processActionForSpecialSymbols);
            this.genLineNo2();
            if (actionTransInfo.assignToRoot) {
                this.println("currentAST.root = " + actionTransInfo.refRuleRoot + ";");
                this.println("if ( " + actionTransInfo.refRuleRoot + "!=" + this.labeledElementASTInit + " &&");
                ++this.tabs;
                this.println(actionTransInfo.refRuleRoot + "->getFirstChild() != " + this.labeledElementASTInit + " )");
                this.println("  currentAST.child = " + actionTransInfo.refRuleRoot + "->getFirstChild();");
                --this.tabs;
                this.println("else");
                ++this.tabs;
                this.println("currentAST.child = " + actionTransInfo.refRuleRoot + ";");
                --this.tabs;
                this.println("currentAST.advanceChildToEnd();");
            }
            if (this.grammar.hasSyntacticPredicate) {
                --this.tabs;
                this.println("}");
            }
        }
    }
    
    public void gen(final AlternativeBlock obj) {
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("gen(" + obj + ")");
        }
        this.println("{");
        this.genBlockPreamble(obj);
        this.genBlockInitAction(obj);
        final String currentASTResult = this.currentASTResult;
        if (obj.getLabel() != null) {
            this.currentASTResult = obj.getLabel();
        }
        this.grammar.theLLkAnalyzer.deterministic(obj);
        this.genBlockFinish(this.genCommonBlock(obj, true), this.throwNoViable);
        this.println("}");
        this.currentASTResult = currentASTResult;
    }
    
    public void gen(final BlockEndElement obj) {
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("genRuleEnd(" + obj + ")");
        }
    }
    
    public void gen(final CharLiteralElement charLiteralElement) {
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("genChar(" + charLiteralElement + ")");
        }
        if (!(this.grammar instanceof LexerGrammar)) {
            this.antlrTool.error("cannot ref character literals in grammar: " + charLiteralElement);
        }
        if (charLiteralElement.getLabel() != null) {
            this.println(charLiteralElement.getLabel() + " = " + this.lt1Value + ";");
        }
        final boolean saveText = this.saveText;
        this.saveText = (this.saveText && charLiteralElement.getAutoGenType() == 1);
        if (!this.saveText || charLiteralElement.getAutoGenType() == 3) {
            this.println("_saveIndex = text.length();");
        }
        this.print(charLiteralElement.not ? "matchNot(" : "match(");
        this._print(this.convertJavaToCppString(charLiteralElement.atomText, true));
        this._println(" /* charlit */ );");
        if (!this.saveText || charLiteralElement.getAutoGenType() == 3) {
            this.println("text.erase(_saveIndex);");
        }
        this.saveText = saveText;
    }
    
    public void gen(final CharRangeElement obj) {
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("genCharRangeElement(" + obj.beginText + ".." + obj.endText + ")");
        }
        if (!(this.grammar instanceof LexerGrammar)) {
            this.antlrTool.error("cannot ref character range in grammar: " + obj);
        }
        if (obj.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(obj.getLabel() + " = " + this.lt1Value + ";");
        }
        final boolean b = this.grammar instanceof LexerGrammar && (!this.saveText || obj.getAutoGenType() == 3);
        if (b) {
            this.println("_saveIndex=text.length();");
        }
        this.println("matchRange(" + this.convertJavaToCppString(obj.beginText, true) + "," + this.convertJavaToCppString(obj.endText, true) + ");");
        if (b) {
            this.println("text.erase(_saveIndex);");
        }
    }
    
    public void gen(final LexerGrammar grammar) throws IOException {
        if (grammar.debuggingOutput) {
            this.semPreds = new Vector();
        }
        if (grammar.charVocabulary.size() > 256) {
            this.antlrTool.warning(grammar.getFilename() + ": Vocabularies of this size still experimental in C++ mode (vocabulary size now: " + grammar.charVocabulary.size() + ")");
        }
        this.setGrammar(grammar);
        if (!(this.grammar instanceof LexerGrammar)) {
            this.antlrTool.panic("Internal error generating lexer");
        }
        this.genBody(grammar);
        this.genInclude(grammar);
    }
    
    public void gen(final OneOrMoreBlock obj) {
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("gen+(" + obj + ")");
        }
        this.println("{ // ( ... )+");
        this.genBlockPreamble(obj);
        String s;
        if (obj.getLabel() != null) {
            s = "_cnt_" + obj.getLabel();
        }
        else {
            s = "_cnt" + obj.ID;
        }
        this.println("int " + s + "=0;");
        String str;
        if (obj.getLabel() != null) {
            str = obj.getLabel();
        }
        else {
            str = "_loop" + obj.ID;
        }
        this.println("for (;;) {");
        ++this.tabs;
        this.genBlockInitAction(obj);
        final String currentASTResult = this.currentASTResult;
        if (obj.getLabel() != null) {
            this.currentASTResult = obj.getLabel();
        }
        this.grammar.theLLkAnalyzer.deterministic(obj);
        boolean b = false;
        int n = this.grammar.maxk;
        if (!obj.greedy && obj.exitLookaheadDepth <= this.grammar.maxk && obj.exitCache[obj.exitLookaheadDepth].containsEpsilon()) {
            b = true;
            n = obj.exitLookaheadDepth;
        }
        else if (!obj.greedy && obj.exitLookaheadDepth == Integer.MAX_VALUE) {
            b = true;
        }
        if (b) {
            if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
                System.out.println("nongreedy (...)+ loop; exit depth is " + obj.exitLookaheadDepth);
            }
            final String lookaheadTestExpression = this.getLookaheadTestExpression(obj.exitCache, n);
            this.println("// nongreedy exit test");
            this.println("if ( " + s + ">=1 && " + lookaheadTestExpression + ") goto " + str + ";");
        }
        this.genBlockFinish(this.genCommonBlock(obj, false), "if ( " + s + ">=1 ) { goto " + str + "; } else {" + this.throwNoViable + "}");
        this.println(s + "++;");
        --this.tabs;
        this.println("}");
        this.println(str + ":;");
        this.println("}  // ( ... )+");
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
        this.genBody(grammar);
        this.genInclude(grammar);
    }
    
    public void gen(final RuleRefElement obj) {
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
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
            this.println(obj.getLabel() + " = (_t == ASTNULL) ? " + this.labeledElementASTInit + " : " + this.lt1Value + ";");
        }
        if (this.grammar instanceof LexerGrammar && (!this.saveText || obj.getAutoGenType() == 3)) {
            this.println("_saveIndex = text.length();");
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
            this.println("text.erase(_saveIndex);");
        }
        if (this.syntacticPredLevel == 0) {
            final boolean b = this.grammar.hasSyntacticPredicate && ((this.grammar.buildAST && obj.getLabel() != null) || (this.genAST && obj.getAutoGenType() == 1));
            if (b) {
                this.println("if (inputState->guessing==0) {");
                ++this.tabs;
            }
            if (this.grammar.buildAST && obj.getLabel() != null) {
                this.println(obj.getLabel() + "_AST = returnAST;");
            }
            if (this.genAST) {
                switch (obj.getAutoGenType()) {
                    case 1: {
                        if (this.usingCustomAST) {
                            this.println("astFactory->addASTChild(currentAST, " + CppCodeGenerator.namespaceAntlr + "RefAST(returnAST));");
                            break;
                        }
                        this.println("astFactory->addASTChild( currentAST, returnAST );");
                        break;
                    }
                    case 2: {
                        this.antlrTool.error("Internal: encountered ^ after rule reference");
                        break;
                    }
                }
            }
            if (this.grammar instanceof LexerGrammar && obj.getLabel() != null) {
                this.println(obj.getLabel() + "=_returnToken;");
            }
            if (b) {
                --this.tabs;
                this.println("}");
            }
        }
        this.genErrorCatchForElement(obj);
    }
    
    public void gen(final StringLiteralElement obj) {
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("genString(" + obj + ")");
        }
        if (obj.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(obj.getLabel() + " = " + this.lt1Value + ";");
        }
        this.genElementAST(obj);
        final boolean saveText = this.saveText;
        this.saveText = (this.saveText && obj.getAutoGenType() == 1);
        this.genMatch(obj);
        this.saveText = saveText;
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _t->getNextSibling();");
        }
    }
    
    public void gen(final TokenRangeElement tokenRangeElement) {
        this.genErrorTryForElement(tokenRangeElement);
        if (tokenRangeElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(tokenRangeElement.getLabel() + " = " + this.lt1Value + ";");
        }
        this.genElementAST(tokenRangeElement);
        this.println("matchRange(" + tokenRangeElement.beginText + "," + tokenRangeElement.endText + ");");
        this.genErrorCatchForElement(tokenRangeElement);
    }
    
    public void gen(final TokenRefElement obj) {
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("genTokenRef(" + obj + ")");
        }
        if (this.grammar instanceof LexerGrammar) {
            this.antlrTool.panic("Token reference found in lexer");
        }
        this.genErrorTryForElement(obj);
        if (obj.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(obj.getLabel() + " = " + this.lt1Value + ";");
        }
        this.genElementAST(obj);
        this.genMatch(obj);
        this.genErrorCatchForElement(obj);
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _t->getNextSibling();");
        }
    }
    
    public void gen(final TreeElement treeElement) {
        this.println(this.labeledElementType + " __t" + treeElement.ID + " = _t;");
        if (treeElement.root.getLabel() != null) {
            this.println(treeElement.root.getLabel() + " = (_t == " + this.labeledElementType + "(ASTNULL)) ? " + this.labeledElementASTInit + " : _t;");
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
            this.println(CppCodeGenerator.namespaceAntlr + "ASTPair __currentAST" + treeElement.ID + " = currentAST;");
            this.println("currentAST.root = currentAST.child;");
            this.println("currentAST.child = " + this.labeledElementASTInit + ";");
        }
        if (treeElement.root instanceof WildcardElement) {
            this.println("if ( _t == ASTNULL ) throw " + CppCodeGenerator.namespaceAntlr + "MismatchedTokenException();");
        }
        else {
            this.genMatch(treeElement.root);
        }
        this.println("_t = _t->getFirstChild();");
        for (int i = 0; i < treeElement.getAlternatives().size(); ++i) {
            for (AlternativeElement alternativeElement = treeElement.getAlternativeAt(i).head; alternativeElement != null; alternativeElement = alternativeElement.next) {
                alternativeElement.generate();
            }
        }
        if (this.grammar.buildAST) {
            this.println("currentAST = __currentAST" + treeElement.ID + ";");
        }
        this.println("_t = __t" + treeElement.ID + ";");
        this.println("_t = _t->getNextSibling();");
    }
    
    public void gen(final TreeWalkerGrammar grammar) throws IOException {
        this.setGrammar(grammar);
        if (!(this.grammar instanceof TreeWalkerGrammar)) {
            this.antlrTool.panic("Internal error generating tree-walker");
        }
        this.genBody(grammar);
        this.genInclude(grammar);
    }
    
    public void gen(final WildcardElement wildcardElement) {
        if (wildcardElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(wildcardElement.getLabel() + " = " + this.lt1Value + ";");
        }
        this.genElementAST(wildcardElement);
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("if ( _t == " + this.labeledElementASTInit + " ) throw " + CppCodeGenerator.namespaceAntlr + "MismatchedTokenException();");
        }
        else if (this.grammar instanceof LexerGrammar) {
            if (this.grammar instanceof LexerGrammar && (!this.saveText || wildcardElement.getAutoGenType() == 3)) {
                this.println("_saveIndex = text.length();");
            }
            this.println("matchNot(EOF/*_CHAR*/);");
            if (this.grammar instanceof LexerGrammar && (!this.saveText || wildcardElement.getAutoGenType() == 3)) {
                this.println("text.erase(_saveIndex);");
            }
        }
        else {
            this.println("matchNot(" + this.getValueString(1) + ");");
        }
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _t->getNextSibling();");
        }
    }
    
    public void gen(final ZeroOrMoreBlock obj) {
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("gen*(" + obj + ")");
        }
        this.println("{ // ( ... )*");
        this.genBlockPreamble(obj);
        String str;
        if (obj.getLabel() != null) {
            str = obj.getLabel();
        }
        else {
            str = "_loop" + obj.ID;
        }
        this.println("for (;;) {");
        ++this.tabs;
        this.genBlockInitAction(obj);
        final String currentASTResult = this.currentASTResult;
        if (obj.getLabel() != null) {
            this.currentASTResult = obj.getLabel();
        }
        this.grammar.theLLkAnalyzer.deterministic(obj);
        boolean b = false;
        int n = this.grammar.maxk;
        if (!obj.greedy && obj.exitLookaheadDepth <= this.grammar.maxk && obj.exitCache[obj.exitLookaheadDepth].containsEpsilon()) {
            b = true;
            n = obj.exitLookaheadDepth;
        }
        else if (!obj.greedy && obj.exitLookaheadDepth == Integer.MAX_VALUE) {
            b = true;
        }
        if (b) {
            if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
                System.out.println("nongreedy (...)* loop; exit depth is " + obj.exitLookaheadDepth);
            }
            final String lookaheadTestExpression = this.getLookaheadTestExpression(obj.exitCache, n);
            this.println("// nongreedy exit test");
            this.println("if (" + lookaheadTestExpression + ") goto " + str + ";");
        }
        this.genBlockFinish(this.genCommonBlock(obj, false), "goto " + str + ";");
        --this.tabs;
        this.println("}");
        this.println(str + ":;");
        this.println("} // ( ... )*");
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
            this.println("try {      // for error handling");
            ++this.tabs;
        }
        for (AlternativeElement alternativeElement = alternative.head; !(alternativeElement instanceof BlockEndElement); alternativeElement = alternativeElement.next) {
            alternativeElement.generate();
        }
        if (this.genAST) {
            if (alternativeBlock instanceof RuleBlock) {
                final RuleBlock ruleBlock = (RuleBlock)alternativeBlock;
                if (this.usingCustomAST) {
                    this.println(ruleBlock.getRuleName() + "_AST = " + this.labeledElementASTType + "(currentAST.root);");
                }
                else {
                    this.println(ruleBlock.getRuleName() + "_AST = currentAST.root;");
                }
            }
            else if (alternativeBlock.getLabel() != null) {
                this.antlrTool.warning("Labeled subrules are not implemented", this.grammar.getFilename(), alternativeBlock.getLine(), alternativeBlock.getColumn());
            }
        }
        if (alternative.exceptionSpec != null) {
            --this.tabs;
            this.println("}");
            this.genErrorHandler(alternative.exceptionSpec);
        }
        this.genAST = genAST;
        this.saveText = saveText;
        this.treeVariableMap = treeVariableMap;
    }
    
    protected void genBitsets(final Vector vector, final int n, final String s) {
        final TokenManager tokenManager = this.grammar.tokenManager;
        this.println("");
        for (int i = 0; i < vector.size(); ++i) {
            final BitSet set = (BitSet)vector.elementAt(i);
            set.growToInclude(n);
            this.println("const unsigned long " + s + this.getBitsetName(i) + "_data_" + "[] = { " + set.toStringOfHalfWords() + " };");
            String str = "// ";
            for (int j = 0; j < tokenManager.getVocabulary().size(); ++j) {
                if (set.member(j)) {
                    if (this.grammar instanceof LexerGrammar) {
                        if (32 <= j && j < 127 && j != 92) {
                            str = str + this.charFormatter.escapeChar(j, true) + " ";
                        }
                        else {
                            str = str + "0x" + Integer.toString(j, 16) + " ";
                        }
                    }
                    else {
                        str = str + tokenManager.getTokenStringAt(j) + " ";
                    }
                    if (str.length() > 70) {
                        this.println(str);
                        str = "// ";
                    }
                }
            }
            if (str != "// ") {
                this.println(str);
            }
            this.println("const " + CppCodeGenerator.namespaceAntlr + "BitSet " + s + this.getBitsetName(i) + "(" + this.getBitsetName(i) + "_data_," + set.size() / 32 + ");");
        }
    }
    
    protected void genBitsetsHeader(final Vector vector, final int n) {
        this.println("");
        for (int i = 0; i < vector.size(); ++i) {
            ((BitSet)vector.elementAt(i)).growToInclude(n);
            this.println("static const unsigned long " + this.getBitsetName(i) + "_data_" + "[];");
            this.println("static const " + CppCodeGenerator.namespaceAntlr + "BitSet " + this.getBitsetName(i) + ";");
        }
    }
    
    private void genBlockFinish(final CppBlockFinishingInfo cppBlockFinishingInfo, final String s) {
        if (cppBlockFinishingInfo.needAnErrorClause && (cppBlockFinishingInfo.generatedAnIf || cppBlockFinishingInfo.generatedSwitch)) {
            if (cppBlockFinishingInfo.generatedAnIf) {
                this.println("else {");
            }
            else {
                this.println("{");
            }
            ++this.tabs;
            this.println(s);
            --this.tabs;
            this.println("}");
        }
        if (cppBlockFinishingInfo.postscript != null) {
            this.println(cppBlockFinishingInfo.postscript);
        }
    }
    
    protected void genBlockInitAction(final AlternativeBlock alternativeBlock) {
        if (alternativeBlock.initAction != null) {
            this.genLineNo(alternativeBlock);
            this.printAction(this.processActionForSpecialSymbols(alternativeBlock.initAction, alternativeBlock.line, this.currentRule, null));
            this.genLineNo2();
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
                            this.println(this.labeledElementType + " " + alternativeElement.getLabel() + " = " + this.labeledElementInit + ";");
                            if (this.grammar.buildAST) {
                                this.genASTDeclaration(alternativeElement);
                            }
                        }
                        else {
                            if (this.grammar.buildAST) {
                                this.genASTDeclaration(alternativeElement);
                            }
                            if (this.grammar instanceof LexerGrammar) {
                                this.println(CppCodeGenerator.namespaceAntlr + "RefToken " + alternativeElement.getLabel() + ";");
                            }
                            if (this.grammar instanceof TreeWalkerGrammar) {
                                this.println(this.labeledElementType + " " + alternativeElement.getLabel() + " = " + this.labeledElementInit + ";");
                            }
                        }
                    }
                    else {
                        this.println(this.labeledElementType + " " + alternativeElement.getLabel() + " = " + this.labeledElementInit + ";");
                        if (this.grammar.buildAST) {
                            if (alternativeElement instanceof GrammarAtom && ((GrammarAtom)alternativeElement).getASTNodeType() != null) {
                                this.genASTDeclaration(alternativeElement, "Ref" + ((GrammarAtom)alternativeElement).getASTNodeType());
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
    
    public void genBody(final LexerGrammar lexerGrammar) throws IOException {
        this.outputFile = this.grammar.getClassName() + ".cpp";
        this.outputLine = 1;
        this.currentOutput = this.antlrTool.openOutputFile(this.outputFile);
        this.genAST = false;
        this.saveText = true;
        this.tabs = 0;
        this.genHeader(this.outputFile);
        this.printHeaderAction("pre_include_cpp");
        this.println("#include \"" + this.grammar.getClassName() + ".hpp\"");
        this.println("#include <antlr/CharBuffer.hpp>");
        this.println("#include <antlr/TokenStreamException.hpp>");
        this.println("#include <antlr/TokenStreamIOException.hpp>");
        this.println("#include <antlr/TokenStreamRecognitionException.hpp>");
        this.println("#include <antlr/CharStreamException.hpp>");
        this.println("#include <antlr/CharStreamIOException.hpp>");
        this.println("#include <antlr/NoViableAltForCharException.hpp>");
        if (this.grammar.debuggingOutput) {
            this.println("#include <antlr/DebuggingInputBuffer.hpp>");
        }
        this.println("");
        this.printHeaderAction("post_include_cpp");
        if (CppCodeGenerator.nameSpace != null) {
            CppCodeGenerator.nameSpace.emitDeclarations(this.currentOutput);
        }
        this.printAction(this.grammar.preambleAction);
        String str;
        if (this.grammar.superClass != null) {
            str = this.grammar.superClass;
        }
        else {
            String str2 = this.grammar.getSuperClass();
            if (str2.lastIndexOf(46) != -1) {
                str2 = str2.substring(str2.lastIndexOf(46) + 1);
            }
            str = CppCodeGenerator.namespaceAntlr + str2;
        }
        if (this.noConstructors) {
            this.println("#if 0");
            this.println("// constructor creation turned of with 'noConstructor' option");
        }
        this.println(this.grammar.getClassName() + "::" + this.grammar.getClassName() + "(" + CppCodeGenerator.namespaceStd + "istream& in)");
        ++this.tabs;
        if (this.grammar.debuggingOutput) {
            this.println(": " + str + "(new " + CppCodeGenerator.namespaceAntlr + "DebuggingInputBuffer(new " + CppCodeGenerator.namespaceAntlr + "CharBuffer(in))," + lexerGrammar.caseSensitive + ")");
        }
        else {
            this.println(": " + str + "(new " + CppCodeGenerator.namespaceAntlr + "CharBuffer(in)," + lexerGrammar.caseSensitive + ")");
        }
        --this.tabs;
        this.println("{");
        ++this.tabs;
        if (this.grammar.debuggingOutput) {
            this.println("setRuleNames(_ruleNames);");
            this.println("setSemPredNames(_semPredNames);");
            this.println("setupDebugging();");
        }
        this.println("initLiterals();");
        --this.tabs;
        this.println("}");
        this.println("");
        this.println(this.grammar.getClassName() + "::" + this.grammar.getClassName() + "(" + CppCodeGenerator.namespaceAntlr + "InputBuffer& ib)");
        ++this.tabs;
        if (this.grammar.debuggingOutput) {
            this.println(": " + str + "(new " + CppCodeGenerator.namespaceAntlr + "DebuggingInputBuffer(ib)," + lexerGrammar.caseSensitive + ")");
        }
        else {
            this.println(": " + str + "(ib," + lexerGrammar.caseSensitive + ")");
        }
        --this.tabs;
        this.println("{");
        ++this.tabs;
        if (this.grammar.debuggingOutput) {
            this.println("setRuleNames(_ruleNames);");
            this.println("setSemPredNames(_semPredNames);");
            this.println("setupDebugging();");
        }
        this.println("initLiterals();");
        --this.tabs;
        this.println("}");
        this.println("");
        this.println(this.grammar.getClassName() + "::" + this.grammar.getClassName() + "(const " + CppCodeGenerator.namespaceAntlr + "LexerSharedInputState& state)");
        ++this.tabs;
        this.println(": " + str + "(state," + lexerGrammar.caseSensitive + ")");
        --this.tabs;
        this.println("{");
        ++this.tabs;
        if (this.grammar.debuggingOutput) {
            this.println("setRuleNames(_ruleNames);");
            this.println("setSemPredNames(_semPredNames);");
            this.println("setupDebugging();");
        }
        this.println("initLiterals();");
        --this.tabs;
        this.println("}");
        this.println("");
        if (this.noConstructors) {
            this.println("// constructor creation turned of with 'noConstructor' option");
            this.println("#endif");
        }
        this.println("void " + this.grammar.getClassName() + "::initLiterals()");
        this.println("{");
        ++this.tabs;
        final Enumeration tokenSymbolKeys = this.grammar.tokenManager.getTokenSymbolKeys();
        while (tokenSymbolKeys.hasMoreElements()) {
            final String s = tokenSymbolKeys.nextElement();
            if (s.charAt(0) != '\"') {
                continue;
            }
            final TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbol(s);
            if (!(tokenSymbol instanceof StringLiteralSymbol)) {
                continue;
            }
            final StringLiteralSymbol stringLiteralSymbol = (StringLiteralSymbol)tokenSymbol;
            this.println("literals[" + stringLiteralSymbol.getId() + "] = " + stringLiteralSymbol.getTokenType() + ";");
        }
        --this.tabs;
        this.println("}");
        if (this.grammar.debuggingOutput) {
            this.println("const char* " + this.grammar.getClassName() + "::_ruleNames[] = {");
            ++this.tabs;
            final Enumeration elements = this.grammar.rules.elements();
            while (elements.hasMoreElements()) {
                final GrammarSymbol grammarSymbol = elements.nextElement();
                if (grammarSymbol instanceof RuleSymbol) {
                    this.println("\"" + ((RuleSymbol)grammarSymbol).getId() + "\",");
                }
            }
            this.println("0");
            --this.tabs;
            this.println("};");
        }
        this.genNextToken();
        final Enumeration elements2 = this.grammar.rules.elements();
        int n = 0;
        while (elements2.hasMoreElements()) {
            final RuleSymbol ruleSymbol = elements2.nextElement();
            if (!ruleSymbol.getId().equals("mnextToken")) {
                this.genRule(ruleSymbol, false, n++, this.grammar.getClassName() + "::");
            }
            this.exitIfError();
        }
        if (this.grammar.debuggingOutput) {
            this.genSemPredMap(this.grammar.getClassName() + "::");
        }
        this.genBitsets(this.bitsetsUsed, ((LexerGrammar)this.grammar).charVocabulary.size(), this.grammar.getClassName() + "::");
        this.println("");
        if (CppCodeGenerator.nameSpace != null) {
            CppCodeGenerator.nameSpace.emitClosures(this.currentOutput);
        }
        this.currentOutput.close();
        this.currentOutput = null;
    }
    
    public void genInitFactory(final Grammar grammar) {
        String str = "factory ";
        if (!grammar.buildAST) {
            str = "";
        }
        this.println("void " + grammar.getClassName() + "::initializeASTFactory( " + CppCodeGenerator.namespaceAntlr + "ASTFactory& " + str + ")");
        this.println("{");
        ++this.tabs;
        if (grammar.buildAST) {
            final TokenManager tokenManager = this.grammar.tokenManager;
            final Enumeration tokenSymbolKeys = tokenManager.getTokenSymbolKeys();
            while (tokenSymbolKeys.hasMoreElements()) {
                final String str2 = tokenSymbolKeys.nextElement();
                final TokenSymbol tokenSymbol = tokenManager.getTokenSymbol(str2);
                if (tokenSymbol.getASTNodeType() != null) {
                    this.astTypes.ensureCapacity(tokenSymbol.getTokenType());
                    final String s = (String)this.astTypes.elementAt(tokenSymbol.getTokenType());
                    if (s == null) {
                        this.astTypes.setElementAt(tokenSymbol.getASTNodeType(), tokenSymbol.getTokenType());
                    }
                    else {
                        if (tokenSymbol.getASTNodeType().equals(s)) {
                            continue;
                        }
                        this.antlrTool.warning("Token " + str2 + " taking most specific AST type", this.grammar.getFilename(), 1, 1);
                        this.antlrTool.warning("  using " + s + " ignoring " + tokenSymbol.getASTNodeType(), this.grammar.getFilename(), 1, 1);
                    }
                }
            }
            for (int i = 0; i < this.astTypes.size(); ++i) {
                final String s2 = (String)this.astTypes.elementAt(i);
                if (s2 != null) {
                    this.println("factory.registerFactory(" + i + ", \"" + s2 + "\", " + s2 + "::factory);");
                }
            }
            this.println("factory.setMaxNodeType(" + this.grammar.tokenManager.maxTokenType() + ");");
        }
        --this.tabs;
        this.println("}");
    }
    
    public void genBody(final ParserGrammar parserGrammar) throws IOException {
        this.outputFile = this.grammar.getClassName() + ".cpp";
        this.outputLine = 1;
        this.currentOutput = this.antlrTool.openOutputFile(this.outputFile);
        this.genAST = this.grammar.buildAST;
        this.tabs = 0;
        this.genHeader(this.outputFile);
        this.printHeaderAction("pre_include_cpp");
        this.println("#include \"" + this.grammar.getClassName() + ".hpp\"");
        this.println("#include <antlr/NoViableAltException.hpp>");
        this.println("#include <antlr/SemanticException.hpp>");
        this.println("#include <antlr/ASTFactory.hpp>");
        this.printHeaderAction("post_include_cpp");
        if (CppCodeGenerator.nameSpace != null) {
            CppCodeGenerator.nameSpace.emitDeclarations(this.currentOutput);
        }
        this.printAction(this.grammar.preambleAction);
        String str;
        if (this.grammar.superClass != null) {
            str = this.grammar.superClass;
        }
        else {
            String str2 = this.grammar.getSuperClass();
            if (str2.lastIndexOf(46) != -1) {
                str2 = str2.substring(str2.lastIndexOf(46) + 1);
            }
            str = CppCodeGenerator.namespaceAntlr + str2;
        }
        if (this.grammar.debuggingOutput) {
            this.println("const char* " + this.grammar.getClassName() + "::_ruleNames[] = {");
            ++this.tabs;
            final Enumeration elements = this.grammar.rules.elements();
            while (elements.hasMoreElements()) {
                final GrammarSymbol grammarSymbol = elements.nextElement();
                if (grammarSymbol instanceof RuleSymbol) {
                    this.println("\"" + ((RuleSymbol)grammarSymbol).getId() + "\",");
                }
            }
            this.println("0");
            --this.tabs;
            this.println("};");
        }
        if (this.noConstructors) {
            this.println("#if 0");
            this.println("// constructor creation turned of with 'noConstructor' option");
        }
        this.print(this.grammar.getClassName() + "::" + this.grammar.getClassName());
        this.println("(" + CppCodeGenerator.namespaceAntlr + "TokenBuffer& tokenBuf, int k)");
        this.println(": " + str + "(tokenBuf,k)");
        this.println("{");
        this.println("}");
        this.println("");
        this.print(this.grammar.getClassName() + "::" + this.grammar.getClassName());
        this.println("(" + CppCodeGenerator.namespaceAntlr + "TokenBuffer& tokenBuf)");
        this.println(": " + str + "(tokenBuf," + this.grammar.maxk + ")");
        this.println("{");
        this.println("}");
        this.println("");
        this.print(this.grammar.getClassName() + "::" + this.grammar.getClassName());
        this.println("(" + CppCodeGenerator.namespaceAntlr + "TokenStream& lexer, int k)");
        this.println(": " + str + "(lexer,k)");
        this.println("{");
        this.println("}");
        this.println("");
        this.print(this.grammar.getClassName() + "::" + this.grammar.getClassName());
        this.println("(" + CppCodeGenerator.namespaceAntlr + "TokenStream& lexer)");
        this.println(": " + str + "(lexer," + this.grammar.maxk + ")");
        this.println("{");
        this.println("}");
        this.println("");
        this.print(this.grammar.getClassName() + "::" + this.grammar.getClassName());
        this.println("(const " + CppCodeGenerator.namespaceAntlr + "ParserSharedInputState& state)");
        this.println(": " + str + "(state," + this.grammar.maxk + ")");
        this.println("{");
        this.println("}");
        this.println("");
        if (this.noConstructors) {
            this.println("// constructor creation turned of with 'noConstructor' option");
            this.println("#endif");
        }
        this.astTypes = new Vector();
        final Enumeration elements2 = this.grammar.rules.elements();
        int n = 0;
        while (elements2.hasMoreElements()) {
            final GrammarSymbol grammarSymbol2 = elements2.nextElement();
            if (grammarSymbol2 instanceof RuleSymbol) {
                final RuleSymbol ruleSymbol;
                this.genRule(ruleSymbol, (ruleSymbol = (RuleSymbol)grammarSymbol2).references.size() == 0, n++, this.grammar.getClassName() + "::");
            }
            this.exitIfError();
        }
        this.genInitFactory(parserGrammar);
        this.genTokenStrings(this.grammar.getClassName() + "::");
        this.genBitsets(this.bitsetsUsed, this.grammar.tokenManager.maxTokenType(), this.grammar.getClassName() + "::");
        if (this.grammar.debuggingOutput) {
            this.genSemPredMap(this.grammar.getClassName() + "::");
        }
        this.println("");
        this.println("");
        if (CppCodeGenerator.nameSpace != null) {
            CppCodeGenerator.nameSpace.emitClosures(this.currentOutput);
        }
        this.currentOutput.close();
        this.currentOutput = null;
    }
    
    public void genBody(final TreeWalkerGrammar treeWalkerGrammar) throws IOException {
        this.outputFile = this.grammar.getClassName() + ".cpp";
        this.outputLine = 1;
        this.currentOutput = this.antlrTool.openOutputFile(this.outputFile);
        this.genAST = this.grammar.buildAST;
        this.tabs = 0;
        this.genHeader(this.outputFile);
        this.printHeaderAction("pre_include_cpp");
        this.println("#include \"" + this.grammar.getClassName() + ".hpp\"");
        this.println("#include <antlr/Token.hpp>");
        this.println("#include <antlr/AST.hpp>");
        this.println("#include <antlr/NoViableAltException.hpp>");
        this.println("#include <antlr/MismatchedTokenException.hpp>");
        this.println("#include <antlr/SemanticException.hpp>");
        this.println("#include <antlr/BitSet.hpp>");
        this.printHeaderAction("post_include_cpp");
        if (CppCodeGenerator.nameSpace != null) {
            CppCodeGenerator.nameSpace.emitDeclarations(this.currentOutput);
        }
        this.printAction(this.grammar.preambleAction);
        if (this.grammar.superClass != null) {
            final String superClass = this.grammar.superClass;
        }
        else {
            String str = this.grammar.getSuperClass();
            if (str.lastIndexOf(46) != -1) {
                str = str.substring(str.lastIndexOf(46) + 1);
            }
            new StringBuffer().append(CppCodeGenerator.namespaceAntlr).append(str).toString();
        }
        if (this.noConstructors) {
            this.println("#if 0");
            this.println("// constructor creation turned of with 'noConstructor' option");
        }
        this.println(this.grammar.getClassName() + "::" + this.grammar.getClassName() + "()");
        this.println("\t: " + CppCodeGenerator.namespaceAntlr + "TreeParser() {");
        ++this.tabs;
        --this.tabs;
        this.println("}");
        if (this.noConstructors) {
            this.println("// constructor creation turned of with 'noConstructor' option");
            this.println("#endif");
        }
        this.println("");
        this.astTypes = new Vector();
        final Enumeration elements = this.grammar.rules.elements();
        int n = 0;
        while (elements.hasMoreElements()) {
            final GrammarSymbol grammarSymbol = elements.nextElement();
            if (grammarSymbol instanceof RuleSymbol) {
                final RuleSymbol ruleSymbol;
                this.genRule(ruleSymbol, (ruleSymbol = (RuleSymbol)grammarSymbol).references.size() == 0, n++, this.grammar.getClassName() + "::");
            }
            this.exitIfError();
        }
        this.genInitFactory(this.grammar);
        this.genTokenStrings(this.grammar.getClassName() + "::");
        this.genBitsets(this.bitsetsUsed, this.grammar.tokenManager.maxTokenType(), this.grammar.getClassName() + "::");
        this.println("");
        this.println("");
        if (CppCodeGenerator.nameSpace != null) {
            CppCodeGenerator.nameSpace.emitClosures(this.currentOutput);
        }
        this.currentOutput.close();
        this.currentOutput = null;
    }
    
    protected void genCases(final BitSet obj) {
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("genCases(" + obj + ")");
        }
        final int[] array = obj.toArray();
        final int n = 1;
        int n2 = 1;
        boolean b = true;
        for (int i = 0; i < array.length; ++i) {
            if (n2 == 1) {
                this.print("");
            }
            else {
                this._print("  ");
            }
            this._print("case " + this.getValueString(array[i]) + ":");
            if (n2 == n) {
                this._println("");
                b = true;
                n2 = 1;
            }
            else {
                ++n2;
                b = false;
            }
        }
        if (!b) {
            this._println("");
        }
    }
    
    public CppBlockFinishingInfo genCommonBlock(final AlternativeBlock obj, final boolean b) {
        int n = 0;
        boolean b2 = false;
        int n2 = 0;
        final CppBlockFinishingInfo cppBlockFinishingInfo = new CppBlockFinishingInfo();
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("genCommonBlk(" + obj + ")");
        }
        final boolean genAST = this.genAST;
        this.genAST = (this.genAST && obj.getAutoGen());
        final boolean saveText = this.saveText;
        this.saveText = (this.saveText && obj.getAutoGen());
        if (obj.not && this.analyzer.subruleCanBeInverted(obj, this.grammar instanceof LexerGrammar)) {
            final Lookahead look = this.analyzer.look(1, obj);
            if (obj.getLabel() != null && this.syntacticPredLevel == 0) {
                this.println(obj.getLabel() + " = " + this.lt1Value + ";");
            }
            this.genElementAST(obj);
            String string = "";
            if (this.grammar instanceof TreeWalkerGrammar) {
                if (this.usingCustomAST) {
                    string = CppCodeGenerator.namespaceAntlr + "RefAST" + "(_t),";
                }
                else {
                    string = "_t,";
                }
            }
            this.println("match(" + string + this.getBitsetName(this.markBitsetForGen(look.fset)) + ");");
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println("_t = _t->getNextSibling();");
            }
            return cppBlockFinishingInfo;
        }
        if (obj.getAlternatives().size() == 1) {
            final Alternative alternative = obj.getAlternativeAt(0);
            if (alternative.synPred != null) {
                this.antlrTool.warning("Syntactic predicate superfluous for single alternative", this.grammar.getFilename(), obj.getAlternativeAt(0).synPred.getLine(), obj.getAlternativeAt(0).synPred.getColumn());
            }
            if (b) {
                if (alternative.semPred != null) {
                    this.genSemPred(alternative.semPred, obj.line);
                }
                this.genAlt(alternative, obj);
                return cppBlockFinishingInfo;
            }
        }
        int n3 = 0;
        for (int i = 0; i < obj.getAlternatives().size(); ++i) {
            if (suitableForCaseExpression(obj.getAlternativeAt(i))) {
                ++n3;
            }
        }
        if (n3 >= this.makeSwitchThreshold) {
            final String lookaheadString = this.lookaheadString(1);
            b2 = true;
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println("if (_t == " + this.labeledElementASTInit + " )");
                ++this.tabs;
                this.println("_t = ASTNULL;");
                --this.tabs;
            }
            this.println("switch ( " + lookaheadString + ") {");
            for (int j = 0; j < obj.alternatives.size(); ++j) {
                final Alternative alternative2 = obj.getAlternativeAt(j);
                if (suitableForCaseExpression(alternative2)) {
                    final Lookahead lookahead = alternative2.cache[1];
                    if (lookahead.fset.degree() == 0 && !lookahead.containsEpsilon()) {
                        this.antlrTool.warning("Alternate omitted due to empty prediction set", this.grammar.getFilename(), alternative2.head.getLine(), alternative2.head.getColumn());
                    }
                    else {
                        this.genCases(lookahead.fset);
                        this.println("{");
                        ++this.tabs;
                        this.genAlt(alternative2, obj);
                        this.println("break;");
                        --this.tabs;
                        this.println("}");
                    }
                }
            }
            this.println("default:");
            ++this.tabs;
        }
        for (int k = (this.grammar instanceof LexerGrammar) ? this.grammar.maxk : 0; k >= 0; --k) {
            if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
                System.out.println("checking depth " + k);
            }
            for (int l = 0; l < obj.alternatives.size(); ++l) {
                final Alternative alternative3 = obj.getAlternativeAt(l);
                if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
                    System.out.println("genAlt: " + l);
                }
                if (b2 && suitableForCaseExpression(alternative3)) {
                    if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
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
                            if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
                                System.out.println("ignoring alt because effectiveDepth!=altDepth;" + m + "!=" + k);
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
                            if (this.grammar instanceof TreeWalkerGrammar) {
                                this.println("if (_t == " + this.labeledElementASTInit + " )");
                                ++this.tabs;
                                this.println("_t = ASTNULL;");
                                --this.tabs;
                            }
                            this.println("if " + s + " {");
                        }
                        else {
                            this.println("else if " + s + " {");
                        }
                    }
                    else if (b3 && alternative3.semPred == null && alternative3.synPred == null) {
                        if (n == 0) {
                            this.println("{");
                        }
                        else {
                            this.println("else {");
                        }
                        cppBlockFinishingInfo.needAnErrorClause = false;
                    }
                    else {
                        if (alternative3.semPred != null) {
                            final String processActionForSpecialSymbols = this.processActionForSpecialSymbols(alternative3.semPred, obj.line, this.currentRule, new ActionTransInfo());
                            if (this.grammar.debuggingOutput && (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar)) {
                                s = "(" + s + "&& fireSemanticPredicateEvaluated(antlr.debug.SemanticPredicateEvent.PREDICTING," + this.addSemPred(this.charFormatter.escapeString(processActionForSpecialSymbols)) + "," + processActionForSpecialSymbols + "))";
                            }
                            else {
                                s = "(" + s + "&&(" + processActionForSpecialSymbols + "))";
                            }
                        }
                        if (n > 0) {
                            if (alternative3.synPred != null) {
                                this.println("else {");
                                ++this.tabs;
                                this.genSynPred(alternative3.synPred, s);
                                ++n2;
                            }
                            else {
                                this.println("else if " + s + " {");
                            }
                        }
                        else if (alternative3.synPred != null) {
                            this.genSynPred(alternative3.synPred, s);
                        }
                        else {
                            if (this.grammar instanceof TreeWalkerGrammar) {
                                this.println("if (_t == " + this.labeledElementASTInit + " )");
                                ++this.tabs;
                                this.println("_t = ASTNULL;");
                                --this.tabs;
                            }
                            this.println("if " + s + " {");
                        }
                    }
                    ++n;
                    ++this.tabs;
                    this.genAlt(alternative3, obj);
                    --this.tabs;
                    this.println("}");
                }
            }
        }
        String string2 = "";
        for (int n4 = 1; n4 <= n2; ++n4) {
            --this.tabs;
            string2 += "}";
        }
        this.genAST = genAST;
        this.saveText = saveText;
        if (b2) {
            --this.tabs;
            cppBlockFinishingInfo.postscript = string2 + "}";
            cppBlockFinishingInfo.generatedSwitch = true;
            cppBlockFinishingInfo.generatedAnIf = (n > 0);
        }
        else {
            cppBlockFinishingInfo.postscript = string2;
            cppBlockFinishingInfo.generatedSwitch = false;
            cppBlockFinishingInfo.generatedAnIf = (n > 0);
        }
        return cppBlockFinishingInfo;
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
                this.println(this.labeledElementASTType + " " + string + "_in = " + lt1Value + ";");
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
                        this.genASTDeclaration(alternativeElement, str, "Ref" + grammarAtom.getASTNodeType());
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
                this.println(this.labeledElementASTType + " " + string2 + "_in = " + this.labeledElementASTInit + ";");
            }
            if (b2) {
                this.println("if ( inputState->guessing == 0 ) {");
                ++this.tabs;
            }
            if (alternativeElement.getLabel() != null) {
                if (alternativeElement instanceof GrammarAtom) {
                    this.println(string2 + " = " + this.getASTCreateString((GrammarAtom)alternativeElement, s) + ";");
                }
                else {
                    this.println(string2 + " = " + this.getASTCreateString(s) + ";");
                }
            }
            if (alternativeElement.getLabel() == null && b) {
                final String lt1Value2 = this.lt1Value;
                if (alternativeElement instanceof GrammarAtom) {
                    this.println(string2 + " = " + this.getASTCreateString((GrammarAtom)alternativeElement, lt1Value2) + ";");
                }
                else {
                    this.println(string2 + " = " + this.getASTCreateString(lt1Value2) + ";");
                }
                if (this.grammar instanceof TreeWalkerGrammar) {
                    this.println(string2 + "_in = " + lt1Value2 + ";");
                }
            }
            if (this.genAST) {
                switch (alternativeElement.getAutoGenType()) {
                    case 1: {
                        if (this.usingCustomAST || (alternativeElement instanceof GrammarAtom && ((GrammarAtom)alternativeElement).getASTNodeType() != null)) {
                            this.println("astFactory->addASTChild(currentAST, " + CppCodeGenerator.namespaceAntlr + "RefAST(" + string2 + "));");
                            break;
                        }
                        this.println("astFactory->addASTChild(currentAST, " + string2 + ");");
                        break;
                    }
                    case 2: {
                        if (this.usingCustomAST || (alternativeElement instanceof GrammarAtom && ((GrammarAtom)alternativeElement).getASTNodeType() != null)) {
                            this.println("astFactory->makeASTRoot(currentAST, " + CppCodeGenerator.namespaceAntlr + "RefAST(" + string2 + "));");
                            break;
                        }
                        this.println("astFactory->makeASTRoot(currentAST, " + string2 + ");");
                        break;
                    }
                }
            }
            if (b2) {
                --this.tabs;
                this.println("}");
            }
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
            this.println("}");
            this.genErrorHandler(exceptionSpec);
        }
    }
    
    private void genErrorHandler(final ExceptionSpec exceptionSpec) {
        for (int i = 0; i < exceptionSpec.handlers.size(); ++i) {
            final ExceptionHandler exceptionHandler = (ExceptionHandler)exceptionSpec.handlers.elementAt(i);
            this.println("catch (" + exceptionHandler.exceptionTypeAndName.getText() + ") {");
            ++this.tabs;
            if (this.grammar.hasSyntacticPredicate) {
                this.println("if (inputState->guessing==0) {");
                ++this.tabs;
            }
            final ActionTransInfo actionTransInfo = new ActionTransInfo();
            this.genLineNo(exceptionHandler.action);
            this.printAction(this.processActionForSpecialSymbols(exceptionHandler.action.getText(), exceptionHandler.action.getLine(), this.currentRule, actionTransInfo));
            this.genLineNo2();
            if (this.grammar.hasSyntacticPredicate) {
                --this.tabs;
                this.println("} else {");
                ++this.tabs;
                this.println("throw;");
                --this.tabs;
                this.println("}");
            }
            --this.tabs;
            this.println("}");
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
            this.println("try { // for error handling");
            ++this.tabs;
        }
    }
    
    protected void genHeader(final String str) {
        final StringBuffer append = new StringBuffer().append("/* $ANTLR ");
        final Tool antlrTool = this.antlrTool;
        this.println(append.append(Tool.version).append(": ").append("\"").append(this.antlrTool.fileMinusPath(this.antlrTool.grammarFile)).append("\"").append(" -> ").append("\"").append(str).append("\"$ */").toString());
    }
    
    public void genInclude(final LexerGrammar lexerGrammar) throws IOException {
        this.outputFile = this.grammar.getClassName() + ".hpp";
        this.outputLine = 1;
        this.currentOutput = this.antlrTool.openOutputFile(this.outputFile);
        this.genAST = false;
        this.saveText = true;
        this.tabs = 0;
        this.println("#ifndef INC_" + this.grammar.getClassName() + "_hpp_");
        this.println("#define INC_" + this.grammar.getClassName() + "_hpp_");
        this.println("");
        this.printHeaderAction("pre_include_hpp");
        this.println("#include <antlr/config.hpp>");
        this.genHeader(this.outputFile);
        this.println("#include <antlr/CommonToken.hpp>");
        this.println("#include <antlr/InputBuffer.hpp>");
        this.println("#include <antlr/BitSet.hpp>");
        this.println("#include \"" + this.grammar.tokenManager.getName() + CppCodeGenerator.TokenTypesFileSuffix + ".hpp\"");
        String str;
        if (this.grammar.superClass != null) {
            str = this.grammar.superClass;
            this.println("\n// Include correct superclass header with a header statement for example:");
            this.println("// header \"post_include_hpp\" {");
            this.println("// #include \"" + str + ".hpp\"");
            this.println("// }");
            this.println("// Or....");
            this.println("// header {");
            this.println("// #include \"" + str + ".hpp\"");
            this.println("// }\n");
        }
        else {
            String s = this.grammar.getSuperClass();
            if (s.lastIndexOf(46) != -1) {
                s = s.substring(s.lastIndexOf(46) + 1);
            }
            this.println("#include <antlr/" + s + ".hpp>");
            str = CppCodeGenerator.namespaceAntlr + s;
        }
        this.printHeaderAction("post_include_hpp");
        if (CppCodeGenerator.nameSpace != null) {
            CppCodeGenerator.nameSpace.emitDeclarations(this.currentOutput);
        }
        this.printHeaderAction("");
        if (this.grammar.comment != null) {
            this._println(this.grammar.comment);
        }
        this.print("class CUSTOM_API " + this.grammar.getClassName() + " : public " + str);
        this.println(", public " + this.grammar.tokenManager.getName() + CppCodeGenerator.TokenTypesFileSuffix);
        final Token token = this.grammar.options.get("classHeaderSuffix");
        if (token != null) {
            final String stripFrontBack = StringUtils.stripFrontBack(token.getText(), "\"", "\"");
            if (stripFrontBack != null) {
                this.print(", " + stripFrontBack);
            }
        }
        this.println("{");
        if (this.grammar.classMemberAction != null) {
            this.genLineNo(this.grammar.classMemberAction);
            this.print(this.processActionForSpecialSymbols(this.grammar.classMemberAction.getText(), this.grammar.classMemberAction.getLine(), this.currentRule, null));
            this.genLineNo2();
        }
        this.tabs = 0;
        this.println("private:");
        this.tabs = 1;
        this.println("void initLiterals();");
        this.tabs = 0;
        this.println("public:");
        this.tabs = 1;
        this.println("bool getCaseSensitiveLiterals() const");
        this.println("{");
        ++this.tabs;
        this.println("return " + lexerGrammar.caseSensitiveLiterals + ";");
        --this.tabs;
        this.println("}");
        this.tabs = 0;
        this.println("public:");
        this.tabs = 1;
        if (this.noConstructors) {
            this.tabs = 0;
            this.println("#if 0");
            this.println("// constructor creation turned of with 'noConstructor' option");
            this.tabs = 1;
        }
        this.println(this.grammar.getClassName() + "(" + CppCodeGenerator.namespaceStd + "istream& in);");
        this.println(this.grammar.getClassName() + "(" + CppCodeGenerator.namespaceAntlr + "InputBuffer& ib);");
        this.println(this.grammar.getClassName() + "(const " + CppCodeGenerator.namespaceAntlr + "LexerSharedInputState& state);");
        if (this.noConstructors) {
            this.tabs = 0;
            this.println("// constructor creation turned of with 'noConstructor' option");
            this.println("#endif");
            this.tabs = 1;
        }
        this.println(CppCodeGenerator.namespaceAntlr + "RefToken nextToken();");
        final Enumeration elements = this.grammar.rules.elements();
        while (elements.hasMoreElements()) {
            final RuleSymbol ruleSymbol = elements.nextElement();
            if (!ruleSymbol.getId().equals("mnextToken")) {
                this.genRuleHeader(ruleSymbol, false);
            }
            this.exitIfError();
        }
        this.tabs = 0;
        this.println("private:");
        this.tabs = 1;
        if (this.grammar.debuggingOutput) {
            this.println("static const char* _ruleNames[];");
        }
        if (this.grammar.debuggingOutput) {
            this.println("static const char* _semPredNames[];");
        }
        this.genBitsetsHeader(this.bitsetsUsed, ((LexerGrammar)this.grammar).charVocabulary.size());
        this.tabs = 0;
        this.println("};");
        this.println("");
        if (CppCodeGenerator.nameSpace != null) {
            CppCodeGenerator.nameSpace.emitClosures(this.currentOutput);
        }
        this.println("#endif /*INC_" + this.grammar.getClassName() + "_hpp_*/");
        this.currentOutput.close();
        this.currentOutput = null;
    }
    
    public void genInclude(final ParserGrammar parserGrammar) throws IOException {
        this.outputFile = this.grammar.getClassName() + ".hpp";
        this.outputLine = 1;
        this.currentOutput = this.antlrTool.openOutputFile(this.outputFile);
        this.genAST = this.grammar.buildAST;
        this.tabs = 0;
        this.println("#ifndef INC_" + this.grammar.getClassName() + "_hpp_");
        this.println("#define INC_" + this.grammar.getClassName() + "_hpp_");
        this.println("");
        this.printHeaderAction("pre_include_hpp");
        this.println("#include <antlr/config.hpp>");
        this.genHeader(this.outputFile);
        this.println("#include <antlr/TokenStream.hpp>");
        this.println("#include <antlr/TokenBuffer.hpp>");
        this.println("#include \"" + this.grammar.tokenManager.getName() + CppCodeGenerator.TokenTypesFileSuffix + ".hpp\"");
        String str;
        if (this.grammar.superClass != null) {
            str = this.grammar.superClass;
            this.println("\n// Include correct superclass header with a header statement for example:");
            this.println("// header \"post_include_hpp\" {");
            this.println("// #include \"" + str + ".hpp\"");
            this.println("// }");
            this.println("// Or....");
            this.println("// header {");
            this.println("// #include \"" + str + ".hpp\"");
            this.println("// }\n");
        }
        else {
            String s = this.grammar.getSuperClass();
            if (s.lastIndexOf(46) != -1) {
                s = s.substring(s.lastIndexOf(46) + 1);
            }
            this.println("#include <antlr/" + s + ".hpp>");
            str = CppCodeGenerator.namespaceAntlr + s;
        }
        this.println("");
        this.printHeaderAction("post_include_hpp");
        if (CppCodeGenerator.nameSpace != null) {
            CppCodeGenerator.nameSpace.emitDeclarations(this.currentOutput);
        }
        this.printHeaderAction("");
        if (this.grammar.comment != null) {
            this._println(this.grammar.comment);
        }
        this.print("class CUSTOM_API " + this.grammar.getClassName() + " : public " + str);
        this.println(", public " + this.grammar.tokenManager.getName() + CppCodeGenerator.TokenTypesFileSuffix);
        final Token token = this.grammar.options.get("classHeaderSuffix");
        if (token != null) {
            final String stripFrontBack = StringUtils.stripFrontBack(token.getText(), "\"", "\"");
            if (stripFrontBack != null) {
                this.print(", " + stripFrontBack);
            }
        }
        this.println("{");
        if (this.grammar.debuggingOutput) {
            this.println("public: static const char* _ruleNames[];");
        }
        if (this.grammar.classMemberAction != null) {
            this.genLineNo(this.grammar.classMemberAction.getLine());
            this.print(this.processActionForSpecialSymbols(this.grammar.classMemberAction.getText(), this.grammar.classMemberAction.getLine(), this.currentRule, null));
            this.genLineNo2();
        }
        this.println("public:");
        this.tabs = 1;
        this.println("void initializeASTFactory( " + CppCodeGenerator.namespaceAntlr + "ASTFactory& factory );");
        this.tabs = 0;
        if (this.noConstructors) {
            this.println("#if 0");
            this.println("// constructor creation turned of with 'noConstructor' option");
        }
        this.println("protected:");
        this.tabs = 1;
        this.println(this.grammar.getClassName() + "(" + CppCodeGenerator.namespaceAntlr + "TokenBuffer& tokenBuf, int k);");
        this.tabs = 0;
        this.println("public:");
        this.tabs = 1;
        this.println(this.grammar.getClassName() + "(" + CppCodeGenerator.namespaceAntlr + "TokenBuffer& tokenBuf);");
        this.tabs = 0;
        this.println("protected:");
        this.tabs = 1;
        this.println(this.grammar.getClassName() + "(" + CppCodeGenerator.namespaceAntlr + "TokenStream& lexer, int k);");
        this.tabs = 0;
        this.println("public:");
        this.tabs = 1;
        this.println(this.grammar.getClassName() + "(" + CppCodeGenerator.namespaceAntlr + "TokenStream& lexer);");
        this.println(this.grammar.getClassName() + "(const " + CppCodeGenerator.namespaceAntlr + "ParserSharedInputState& state);");
        if (this.noConstructors) {
            this.tabs = 0;
            this.println("// constructor creation turned of with 'noConstructor' option");
            this.println("#endif");
            this.tabs = 1;
        }
        this.println("int getNumTokens() const");
        this.println("{");
        ++this.tabs;
        this.println("return " + this.grammar.getClassName() + "::NUM_TOKENS;");
        --this.tabs;
        this.println("}");
        this.println("const char* getTokenName( int type ) const");
        this.println("{");
        ++this.tabs;
        this.println("if( type > getNumTokens() ) return 0;");
        this.println("return " + this.grammar.getClassName() + "::tokenNames[type];");
        --this.tabs;
        this.println("}");
        this.println("const char* const* getTokenNames() const");
        this.println("{");
        ++this.tabs;
        this.println("return " + this.grammar.getClassName() + "::tokenNames;");
        --this.tabs;
        this.println("}");
        final Enumeration elements = this.grammar.rules.elements();
        while (elements.hasMoreElements()) {
            final GrammarSymbol grammarSymbol = elements.nextElement();
            if (grammarSymbol instanceof RuleSymbol) {
                final RuleSymbol ruleSymbol = (RuleSymbol)grammarSymbol;
                this.genRuleHeader(ruleSymbol, ruleSymbol.references.size() == 0);
            }
            this.exitIfError();
        }
        this.tabs = 0;
        this.println("public:");
        this.tabs = 1;
        this.println(CppCodeGenerator.namespaceAntlr + "RefAST getAST()");
        this.println("{");
        if (this.usingCustomAST) {
            ++this.tabs;
            this.println("return " + CppCodeGenerator.namespaceAntlr + "RefAST(returnAST);");
            --this.tabs;
        }
        else {
            ++this.tabs;
            this.println("return returnAST;");
            --this.tabs;
        }
        this.println("}");
        this.println("");
        this.tabs = 0;
        this.println("protected:");
        this.tabs = 1;
        this.println(this.labeledElementASTType + " returnAST;");
        this.tabs = 0;
        this.println("private:");
        this.tabs = 1;
        this.println("static const char* tokenNames[];");
        this._println("#ifndef NO_STATIC_CONSTS");
        this.println("static const int NUM_TOKENS = " + this.grammar.tokenManager.getVocabulary().size() + ";");
        this._println("#else");
        this.println("enum {");
        this.println("\tNUM_TOKENS = " + this.grammar.tokenManager.getVocabulary().size());
        this.println("};");
        this._println("#endif");
        this.genBitsetsHeader(this.bitsetsUsed, this.grammar.tokenManager.maxTokenType());
        if (this.grammar.debuggingOutput) {
            this.println("static const char* _semPredNames[];");
        }
        this.tabs = 0;
        this.println("};");
        this.println("");
        if (CppCodeGenerator.nameSpace != null) {
            CppCodeGenerator.nameSpace.emitClosures(this.currentOutput);
        }
        this.println("#endif /*INC_" + this.grammar.getClassName() + "_hpp_*/");
        this.currentOutput.close();
        this.currentOutput = null;
    }
    
    public void genInclude(final TreeWalkerGrammar treeWalkerGrammar) throws IOException {
        this.outputFile = this.grammar.getClassName() + ".hpp";
        this.outputLine = 1;
        this.currentOutput = this.antlrTool.openOutputFile(this.outputFile);
        this.genAST = this.grammar.buildAST;
        this.tabs = 0;
        this.println("#ifndef INC_" + this.grammar.getClassName() + "_hpp_");
        this.println("#define INC_" + this.grammar.getClassName() + "_hpp_");
        this.println("");
        this.printHeaderAction("pre_include_hpp");
        this.println("#include <antlr/config.hpp>");
        this.println("#include \"" + this.grammar.tokenManager.getName() + CppCodeGenerator.TokenTypesFileSuffix + ".hpp\"");
        this.genHeader(this.outputFile);
        String str;
        if (this.grammar.superClass != null) {
            str = this.grammar.superClass;
            this.println("\n// Include correct superclass header with a header statement for example:");
            this.println("// header \"post_include_hpp\" {");
            this.println("// #include \"" + str + ".hpp\"");
            this.println("// }");
            this.println("// Or....");
            this.println("// header {");
            this.println("// #include \"" + str + ".hpp\"");
            this.println("// }\n");
        }
        else {
            String s = this.grammar.getSuperClass();
            if (s.lastIndexOf(46) != -1) {
                s = s.substring(s.lastIndexOf(46) + 1);
            }
            this.println("#include <antlr/" + s + ".hpp>");
            str = CppCodeGenerator.namespaceAntlr + s;
        }
        this.println("");
        this.printHeaderAction("post_include_hpp");
        if (CppCodeGenerator.nameSpace != null) {
            CppCodeGenerator.nameSpace.emitDeclarations(this.currentOutput);
        }
        this.printHeaderAction("");
        if (this.grammar.comment != null) {
            this._println(this.grammar.comment);
        }
        this.print("class CUSTOM_API " + this.grammar.getClassName() + " : public " + str);
        this.println(", public " + this.grammar.tokenManager.getName() + CppCodeGenerator.TokenTypesFileSuffix);
        final Token token = this.grammar.options.get("classHeaderSuffix");
        if (token != null) {
            final String stripFrontBack = StringUtils.stripFrontBack(token.getText(), "\"", "\"");
            if (stripFrontBack != null) {
                this.print(", " + stripFrontBack);
            }
        }
        this.println("{");
        if (this.grammar.classMemberAction != null) {
            this.genLineNo(this.grammar.classMemberAction.getLine());
            this.print(this.processActionForSpecialSymbols(this.grammar.classMemberAction.getText(), this.grammar.classMemberAction.getLine(), this.currentRule, null));
            this.genLineNo2();
        }
        this.tabs = 0;
        this.println("public:");
        if (this.noConstructors) {
            this.println("#if 0");
            this.println("// constructor creation turned of with 'noConstructor' option");
        }
        this.tabs = 1;
        this.println(this.grammar.getClassName() + "();");
        if (this.noConstructors) {
            this.tabs = 0;
            this.println("#endif");
            this.tabs = 1;
        }
        this.println("static void initializeASTFactory( " + CppCodeGenerator.namespaceAntlr + "ASTFactory& factory );");
        this.println("int getNumTokens() const");
        this.println("{");
        ++this.tabs;
        this.println("return " + this.grammar.getClassName() + "::NUM_TOKENS;");
        --this.tabs;
        this.println("}");
        this.println("const char* getTokenName( int type ) const");
        this.println("{");
        ++this.tabs;
        this.println("if( type > getNumTokens() ) return 0;");
        this.println("return " + this.grammar.getClassName() + "::tokenNames[type];");
        --this.tabs;
        this.println("}");
        this.println("const char* const* getTokenNames() const");
        this.println("{");
        ++this.tabs;
        this.println("return " + this.grammar.getClassName() + "::tokenNames;");
        --this.tabs;
        this.println("}");
        final Enumeration elements = this.grammar.rules.elements();
        while (elements.hasMoreElements()) {
            final GrammarSymbol grammarSymbol = elements.nextElement();
            if (grammarSymbol instanceof RuleSymbol) {
                final RuleSymbol ruleSymbol = (RuleSymbol)grammarSymbol;
                this.genRuleHeader(ruleSymbol, ruleSymbol.references.size() == 0);
            }
            this.exitIfError();
        }
        this.tabs = 0;
        this.println("public:");
        this.tabs = 1;
        this.println(CppCodeGenerator.namespaceAntlr + "RefAST getAST()");
        this.println("{");
        if (this.usingCustomAST) {
            ++this.tabs;
            this.println("return " + CppCodeGenerator.namespaceAntlr + "RefAST(returnAST);");
            --this.tabs;
        }
        else {
            ++this.tabs;
            this.println("return returnAST;");
            --this.tabs;
        }
        this.println("}");
        this.println("");
        this.tabs = 0;
        this.println("protected:");
        this.tabs = 1;
        this.println(this.labeledElementASTType + " returnAST;");
        this.println(this.labeledElementASTType + " _retTree;");
        this.tabs = 0;
        this.println("private:");
        this.tabs = 1;
        this.println("static const char* tokenNames[];");
        this._println("#ifndef NO_STATIC_CONSTS");
        this.println("static const int NUM_TOKENS = " + this.grammar.tokenManager.getVocabulary().size() + ";");
        this._println("#else");
        this.println("enum {");
        this.println("\tNUM_TOKENS = " + this.grammar.tokenManager.getVocabulary().size());
        this.println("};");
        this._println("#endif");
        this.genBitsetsHeader(this.bitsetsUsed, this.grammar.tokenManager.maxTokenType());
        this.tabs = 0;
        this.println("};");
        this.println("");
        if (CppCodeGenerator.nameSpace != null) {
            CppCodeGenerator.nameSpace.emitClosures(this.currentOutput);
        }
        this.println("#endif /*INC_" + this.grammar.getClassName() + "_hpp_*/");
        this.currentOutput.close();
        this.currentOutput = null;
    }
    
    protected void genASTDeclaration(final AlternativeElement alternativeElement) {
        this.genASTDeclaration(alternativeElement, this.labeledElementASTType);
    }
    
    protected void genASTDeclaration(final AlternativeElement alternativeElement, final String s) {
        this.genASTDeclaration(alternativeElement, alternativeElement.getLabel(), s);
    }
    
    protected void genASTDeclaration(final AlternativeElement alternativeElement, final String str, final String str2) {
        if (this.declaredASTVariables.contains(alternativeElement)) {
            return;
        }
        String str3 = this.labeledElementASTInit;
        if (alternativeElement instanceof GrammarAtom && ((GrammarAtom)alternativeElement).getASTNodeType() != null) {
            str3 = "Ref" + ((GrammarAtom)alternativeElement).getASTNodeType() + "(" + this.labeledElementASTInit + ")";
        }
        this.println(str2 + " " + str + "_AST = " + str3 + ";");
        this.declaredASTVariables.put(alternativeElement, alternativeElement);
    }
    
    private void genLiteralsTest() {
        this.println("_ttype = testLiteralsTable(_ttype);");
    }
    
    private void genLiteralsTestForPartialToken() {
        this.println("_ttype = testLiteralsTable(text.substr(_begin, text.length()-_begin),_ttype);");
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
            this.antlrTool.error("cannot ref character literals in grammar: " + obj);
        }
        else if (obj instanceof TokenRefElement) {
            this.genMatchUsingAtomTokenType(obj);
        }
        else if (obj instanceof WildcardElement) {
            this.gen((WildcardElement)obj);
        }
    }
    
    protected void genMatchUsingAtomText(final GrammarAtom grammarAtom) {
        String string = "";
        if (this.grammar instanceof TreeWalkerGrammar) {
            if (this.usingCustomAST) {
                string = CppCodeGenerator.namespaceAntlr + "RefAST" + "(_t),";
            }
            else {
                string = "_t,";
            }
        }
        if (this.grammar instanceof LexerGrammar && (!this.saveText || grammarAtom.getAutoGenType() == 3)) {
            this.println("_saveIndex = text.length();");
        }
        this.print(grammarAtom.not ? "matchNot(" : "match(");
        this._print(string);
        if (grammarAtom.atomText.equals("EOF")) {
            this._print(CppCodeGenerator.namespaceAntlr + "Token::EOF_TYPE");
        }
        else if (this.grammar instanceof LexerGrammar) {
            this._print(this.convertJavaToCppString(grammarAtom.atomText, false));
        }
        else {
            this._print(grammarAtom.atomText);
        }
        this._println(");");
        if (this.grammar instanceof LexerGrammar && (!this.saveText || grammarAtom.getAutoGenType() == 3)) {
            this.println("text.erase(_saveIndex);");
        }
    }
    
    protected void genMatchUsingAtomTokenType(final GrammarAtom grammarAtom) {
        String string = "";
        if (this.grammar instanceof TreeWalkerGrammar) {
            if (this.usingCustomAST) {
                string = CppCodeGenerator.namespaceAntlr + "RefAST" + "(_t),";
            }
            else {
                string = "_t,";
            }
        }
        this.println((grammarAtom.not ? "matchNot(" : "match(") + (string + this.getValueString(grammarAtom.getType())) + ");");
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
            this.println(CppCodeGenerator.namespaceAntlr + "RefToken " + this.grammar.getClassName() + "::nextToken() { return " + CppCodeGenerator.namespaceAntlr + "RefToken(new " + CppCodeGenerator.namespaceAntlr + "CommonToken(" + CppCodeGenerator.namespaceAntlr + "Token::EOF_TYPE, \"\")); }");
            this.println("");
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
        this.println(CppCodeGenerator.namespaceAntlr + "RefToken " + this.grammar.getClassName() + "::nextToken()");
        this.println("{");
        ++this.tabs;
        this.println(CppCodeGenerator.namespaceAntlr + "RefToken theRetToken;");
        this.println("for (;;) {");
        ++this.tabs;
        this.println(CppCodeGenerator.namespaceAntlr + "RefToken theRetToken;");
        this.println("int _ttype = " + CppCodeGenerator.namespaceAntlr + "Token::INVALID_TYPE;");
        if (((LexerGrammar)this.grammar).filterMode) {
            this.println("setCommitToPath(false);");
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
                this.println("int _m;");
                this.println("_m = mark();");
            }
        }
        this.println("resetText();");
        this.println("try {   // for lexical and char stream error handling");
        ++this.tabs;
        for (int j = 0; j < nextTokenRule.getAlternatives().size(); ++j) {
            if (nextTokenRule.getAlternativeAt(j).cache[1].containsEpsilon()) {
                this.antlrTool.warning("found optional path in nextToken()");
            }
        }
        final String property = System.getProperty("line.separator");
        final CppBlockFinishingInfo genCommonBlock = this.genCommonBlock(nextTokenRule, false);
        final String string = "if (LA(1)==EOF_CHAR)" + property + "\t\t\t\t{" + property + "\t\t\t\t\tuponEOF();" + property + "\t\t\t\t\t_returnToken = makeToken(" + CppCodeGenerator.namespaceAntlr + "Token::EOF_TYPE);" + property + "\t\t\t\t}" + property + "\t\t\t\t";
        String s;
        if (((LexerGrammar)this.grammar).filterMode) {
            if (filterRule == null) {
                s = string + "else {consume(); goto tryAgain;}";
            }
            else {
                s = string + "else {" + property + "\t\t\t\t\tcommit();" + property + "\t\t\t\t\ttry {m" + filterRule + "(false);}" + property + "\t\t\t\t\tcatch(" + CppCodeGenerator.namespaceAntlr + "RecognitionException& e) {" + property + "\t\t\t\t\t\t// catastrophic failure" + property + "\t\t\t\t\t\treportError(e);" + property + "\t\t\t\t\t\tconsume();" + property + "\t\t\t\t\t}" + property + "\t\t\t\t\tgoto tryAgain;" + property + "\t\t\t\t}";
            }
        }
        else {
            s = string + "else {" + this.throwNoViable + "}";
        }
        this.genBlockFinish(genCommonBlock, s);
        if (((LexerGrammar)this.grammar).filterMode && filterRule != null) {
            this.println("commit();");
        }
        this.println("if ( !_returnToken )" + property + "\t\t\t\tgoto tryAgain; // found SKIP token" + property);
        this.println("_ttype = _returnToken->getType();");
        if (((LexerGrammar)this.grammar).getTestLiterals()) {
            this.genLiteralsTest();
        }
        this.println("_returnToken->setType(_ttype);");
        this.println("return _returnToken;");
        --this.tabs;
        this.println("}");
        this.println("catch (" + CppCodeGenerator.namespaceAntlr + "RecognitionException& e) {");
        ++this.tabs;
        if (((LexerGrammar)this.grammar).filterMode) {
            if (filterRule == null) {
                this.println("if ( !getCommitToPath() ) {");
                ++this.tabs;
                this.println("consume();");
                this.println("goto tryAgain;");
                --this.tabs;
                this.println("}");
            }
            else {
                this.println("if ( !getCommitToPath() ) {");
                ++this.tabs;
                this.println("rewind(_m);");
                this.println("resetText();");
                this.println("try {m" + filterRule + "(false);}");
                this.println("catch(" + CppCodeGenerator.namespaceAntlr + "RecognitionException& ee) {");
                this.println("\t// horrendous failure: error in filter rule");
                this.println("\treportError(ee);");
                this.println("\tconsume();");
                this.println("}");
                --this.tabs;
                this.println("}");
                this.println("else");
            }
        }
        if (nextTokenRule.getDefaultErrorHandler()) {
            this.println("{");
            ++this.tabs;
            this.println("reportError(e);");
            this.println("consume();");
            --this.tabs;
            this.println("}");
        }
        else {
            ++this.tabs;
            this.println("throw " + CppCodeGenerator.namespaceAntlr + "TokenStreamRecognitionException(e);");
            --this.tabs;
        }
        --this.tabs;
        this.println("}");
        this.println("catch (" + CppCodeGenerator.namespaceAntlr + "CharStreamIOException& csie) {");
        this.println("\tthrow " + CppCodeGenerator.namespaceAntlr + "TokenStreamIOException(csie.io);");
        this.println("}");
        this.println("catch (" + CppCodeGenerator.namespaceAntlr + "CharStreamException& cse) {");
        this.println("\tthrow " + CppCodeGenerator.namespaceAntlr + "TokenStreamException(cse.getMessage());");
        this.println("}");
        this._println("tryAgain:;");
        --this.tabs;
        this.println("}");
        --this.tabs;
        this.println("}");
        this.println("");
    }
    
    public void genRule(final RuleSymbol ruleSymbol, final boolean b, final int n, final String str) {
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("genRule(" + ruleSymbol.getId() + ")");
        }
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
        if (ruleSymbol.comment != null) {
            this._println(ruleSymbol.comment);
        }
        if (block.returnAction != null) {
            this._print(this.extractTypeOfAction(block.returnAction, block.getLine(), block.getColumn()) + " ");
        }
        else {
            this._print("void ");
        }
        this._print(str + ruleSymbol.getId() + "(");
        this._print(this.commonExtraParams);
        if (this.commonExtraParams.length() != 0 && block.argAction != null) {
            this._print(",");
        }
        if (block.argAction != null) {
            this._println("");
            ++this.tabs;
            String str2 = block.argAction;
            String s = "";
            String s2 = "";
            int n2 = str2.indexOf(61);
            if (n2 != -1) {
                int index = 0;
                while (index != -1 && n2 != -1) {
                    s = s + s2 + str2.substring(0, n2).trim();
                    s2 = ", ";
                    index = str2.indexOf(44, n2);
                    if (index != -1) {
                        str2 = str2.substring(index + 1).trim();
                        n2 = str2.indexOf(61);
                        if (n2 != -1) {
                            continue;
                        }
                        s = s + s2 + str2;
                    }
                }
            }
            else {
                s = str2;
            }
            this.println(s);
            --this.tabs;
            this.print(") ");
        }
        else {
            this._print(") ");
        }
        this._println("{");
        ++this.tabs;
        if (this.grammar.traceRules) {
            if (this.grammar instanceof TreeWalkerGrammar) {
                if (this.usingCustomAST) {
                    this.println("Tracer traceInOut(this,\"" + ruleSymbol.getId() + "\"," + CppCodeGenerator.namespaceAntlr + "RefAST" + "(_t));");
                }
                else {
                    this.println("Tracer traceInOut(this,\"" + ruleSymbol.getId() + "\",_t);");
                }
            }
            else {
                this.println("Tracer traceInOut(this, \"" + ruleSymbol.getId() + "\");");
            }
        }
        if (block.returnAction != null) {
            this.genLineNo(block);
            this.println(block.returnAction + ";");
            this.genLineNo2();
        }
        if (!this.commonLocalVars.equals("")) {
            this.println(this.commonLocalVars);
        }
        if (this.grammar instanceof LexerGrammar) {
            if (ruleSymbol.getId().equals("mEOF")) {
                this.println("_ttype = " + CppCodeGenerator.namespaceAntlr + "Token::EOF_TYPE;");
            }
            else {
                this.println("_ttype = " + ruleSymbol.getId().substring(1) + ";");
            }
            this.println(CppCodeGenerator.namespaceStd + "string::size_type _saveIndex;");
        }
        if (this.grammar.debuggingOutput) {
            if (this.grammar instanceof ParserGrammar) {
                this.println("fireEnterRule(" + n + ",0);");
            }
            else if (this.grammar instanceof LexerGrammar) {
                this.println("fireEnterRule(" + n + ",_ttype);");
            }
        }
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println(this.labeledElementASTType + " " + ruleSymbol.getId() + "_AST_in = (_t == " + this.labeledElementASTType + "(ASTNULL)) ? " + this.labeledElementASTInit + " : _t;");
        }
        if (this.grammar.buildAST) {
            this.println("returnAST = " + this.labeledElementASTInit + ";");
            this.println(CppCodeGenerator.namespaceAntlr + "ASTPair currentAST;");
            this.println(this.labeledElementASTType + " " + ruleSymbol.getId() + "_AST = " + this.labeledElementASTInit + ";");
        }
        this.genBlockPreamble(block);
        this.genBlockInitAction(block);
        this.println("");
        final ExceptionSpec exceptionSpec = block.findExceptionSpec("");
        if (exceptionSpec != null || block.getDefaultErrorHandler()) {
            this.println("try {      // for error handling");
            ++this.tabs;
        }
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
        if (exceptionSpec != null || block.getDefaultErrorHandler()) {
            --this.tabs;
            this.println("}");
        }
        if (exceptionSpec != null) {
            this.genErrorHandler(exceptionSpec);
        }
        else if (block.getDefaultErrorHandler()) {
            this.println("catch (" + this.exceptionThrown + "& ex) {");
            ++this.tabs;
            if (this.grammar.hasSyntacticPredicate) {
                this.println("if( inputState->guessing == 0 ) {");
                ++this.tabs;
            }
            this.println("reportError(ex);");
            if (!(this.grammar instanceof TreeWalkerGrammar)) {
                this.println("recover(ex," + this.getBitsetName(this.markBitsetForGen(this.grammar.theLLkAnalyzer.FOLLOW(1, block.endNode).fset)) + ");");
            }
            else {
                this.println("if ( _t != " + this.labeledElementASTInit + " )");
                ++this.tabs;
                this.println("_t = _t->getNextSibling();");
                --this.tabs;
            }
            if (this.grammar.hasSyntacticPredicate) {
                --this.tabs;
                this.println("} else {");
                ++this.tabs;
                this.println("throw;");
                --this.tabs;
                this.println("}");
            }
            --this.tabs;
            this.println("}");
        }
        if (this.grammar.buildAST) {
            this.println("returnAST = " + ruleSymbol.getId() + "_AST;");
        }
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_retTree = _t;");
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
            this.println("if ( _createToken && _token==" + CppCodeGenerator.namespaceAntlr + "nullToken && _ttype!=" + CppCodeGenerator.namespaceAntlr + "Token::SKIP ) {");
            this.println("   _token = makeToken(_ttype);");
            this.println("   _token->setText(text.substr(_begin, text.length()-_begin));");
            this.println("}");
            this.println("_returnToken = _token;");
            this.println("_saveIndex=0;");
        }
        if (block.returnAction != null) {
            this.println("return " + this.extractIdOfAction(block.returnAction, block.getLine(), block.getColumn()) + ";");
        }
        --this.tabs;
        this.println("}");
        this.println("");
        this.genAST = genAST;
    }
    
    public void genRuleHeader(final RuleSymbol ruleSymbol, final boolean b) {
        this.tabs = 1;
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("genRuleHeader(" + ruleSymbol.getId() + ")");
        }
        if (!ruleSymbol.isDefined()) {
            this.antlrTool.error("undefined rule: " + ruleSymbol.getId());
            return;
        }
        final RuleBlock block = ruleSymbol.getBlock();
        this.currentRule = block;
        this.currentASTResult = ruleSymbol.getId();
        final boolean genAST = this.genAST;
        this.genAST = (this.genAST && block.getAutoGen());
        this.saveText = block.getAutoGen();
        this.print(ruleSymbol.access + ": ");
        if (block.returnAction != null) {
            this._print(this.extractTypeOfAction(block.returnAction, block.getLine(), block.getColumn()) + " ");
        }
        else {
            this._print("void ");
        }
        this._print(ruleSymbol.getId() + "(");
        this._print(this.commonExtraParams);
        if (this.commonExtraParams.length() != 0 && block.argAction != null) {
            this._print(",");
        }
        if (block.argAction != null) {
            this._println("");
            ++this.tabs;
            this.println(block.argAction);
            --this.tabs;
            this.print(")");
        }
        else {
            this._print(")");
        }
        this._println(";");
        --this.tabs;
        this.genAST = genAST;
    }
    
    private void GenRuleInvocation(final RuleRefElement ruleRefElement) {
        this._print(ruleRefElement.targetRule + "(");
        if (this.grammar instanceof LexerGrammar) {
            if (ruleRefElement.getLabel() != null) {
                this._print("true");
            }
            else {
                this._print("false");
            }
            if (this.commonExtraArgs.length() != 0 || ruleRefElement.args != null) {
                this._print(",");
            }
        }
        this._print(this.commonExtraArgs);
        if (this.commonExtraArgs.length() != 0 && ruleRefElement.args != null) {
            this._print(",");
        }
        final RuleSymbol ruleSymbol = (RuleSymbol)this.grammar.getSymbol(ruleRefElement.targetRule);
        if (ruleRefElement.args != null) {
            final ActionTransInfo actionTransInfo = new ActionTransInfo();
            final String processActionForSpecialSymbols = this.processActionForSpecialSymbols(ruleRefElement.args, ruleRefElement.line, this.currentRule, actionTransInfo);
            if (actionTransInfo.assignToRoot || actionTransInfo.refRuleRoot != null) {
                this.antlrTool.error("Arguments of rule reference '" + ruleRefElement.targetRule + "' cannot set or ref #" + this.currentRule.getRuleName() + " on line " + ruleRefElement.getLine());
            }
            this._print(processActionForSpecialSymbols);
            if (ruleSymbol.block.argAction == null) {
                this.antlrTool.warning("Rule '" + ruleRefElement.targetRule + "' accepts no arguments", this.grammar.getFilename(), ruleRefElement.getLine(), ruleRefElement.getColumn());
            }
        }
        this._println(");");
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _retTree;");
        }
    }
    
    protected void genSemPred(String s, final int n) {
        s = this.processActionForSpecialSymbols(s, n, this.currentRule, new ActionTransInfo());
        final String escapeString = this.charFormatter.escapeString(s);
        if (this.grammar.debuggingOutput && (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar)) {
            s = "fireSemanticPredicateEvaluated(antlr.debug.SemanticPredicateEvent.VALIDATING," + this.addSemPred(escapeString) + "," + s + ")";
        }
        this.println("if (!(" + s + "))");
        ++this.tabs;
        this.println("throw " + CppCodeGenerator.namespaceAntlr + "SemanticException(\"" + escapeString + "\");");
        --this.tabs;
    }
    
    protected void genSemPredMap(final String str) {
        final Enumeration elements = this.semPreds.elements();
        this.println("const char* " + str + "_semPredNames[] = {");
        ++this.tabs;
        while (elements.hasMoreElements()) {
            this.println("\"" + elements.nextElement() + "\",");
        }
        this.println("0");
        --this.tabs;
        this.println("};");
    }
    
    protected void genSynPred(final SynPredBlock obj, final String str) {
        if (this.DEBUG_CODE_GENERATOR || this.DEBUG_CPP_CODE_GENERATOR) {
            System.out.println("gen=>(" + obj + ")");
        }
        this.println("bool synPredMatched" + obj.ID + " = false;");
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("if (_t == " + this.labeledElementASTInit + " )");
            ++this.tabs;
            this.println("_t = ASTNULL;");
            --this.tabs;
        }
        this.println("if (" + str + ") {");
        ++this.tabs;
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println(this.labeledElementType + " __t" + obj.ID + " = _t;");
        }
        else {
            this.println("int _m" + obj.ID + " = mark();");
        }
        this.println("synPredMatched" + obj.ID + " = true;");
        this.println("inputState->guessing++;");
        if (this.grammar.debuggingOutput && (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar)) {
            this.println("fireSyntacticPredicateStarted();");
        }
        ++this.syntacticPredLevel;
        this.println("try {");
        ++this.tabs;
        this.gen(obj);
        --this.tabs;
        this.println("}");
        this.println("catch (" + this.exceptionThrown + "& pe) {");
        ++this.tabs;
        this.println("synPredMatched" + obj.ID + " = false;");
        --this.tabs;
        this.println("}");
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = __t" + obj.ID + ";");
        }
        else {
            this.println("rewind(_m" + obj.ID + ");");
        }
        this.println("inputState->guessing--;");
        if (this.grammar.debuggingOutput && (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar)) {
            this.println("if (synPredMatched" + obj.ID + ")");
            this.println("  fireSyntacticPredicateSucceeded();");
            this.println("else");
            this.println("  fireSyntacticPredicateFailed();");
        }
        --this.syntacticPredLevel;
        --this.tabs;
        this.println("}");
        this.println("if ( synPredMatched" + obj.ID + " ) {");
    }
    
    public void genTokenStrings(final String str) {
        this.println("const char* " + str + "tokenNames[] = {");
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
            this._println(",");
        }
        this.println("0");
        --this.tabs;
        this.println("};");
    }
    
    protected void genTokenTypes(final TokenManager tokenManager) throws IOException {
        this.outputFile = tokenManager.getName() + CppCodeGenerator.TokenTypesFileSuffix + ".hpp";
        this.outputLine = 1;
        this.currentOutput = this.antlrTool.openOutputFile(this.outputFile);
        this.tabs = 0;
        this.println("#ifndef INC_" + tokenManager.getName() + CppCodeGenerator.TokenTypesFileSuffix + "_hpp_");
        this.println("#define INC_" + tokenManager.getName() + CppCodeGenerator.TokenTypesFileSuffix + "_hpp_");
        this.println("");
        if (CppCodeGenerator.nameSpace != null) {
            CppCodeGenerator.nameSpace.emitDeclarations(this.currentOutput);
        }
        this.genHeader(this.outputFile);
        this.println("");
        this.println("#ifndef CUSTOM_API");
        this.println("# define CUSTOM_API");
        this.println("#endif");
        this.println("");
        this.println("#ifdef __cplusplus");
        this.println("struct CUSTOM_API " + tokenManager.getName() + CppCodeGenerator.TokenTypesFileSuffix + " {");
        this.println("#endif");
        ++this.tabs;
        this.println("enum {");
        ++this.tabs;
        final Vector vocabulary = tokenManager.getVocabulary();
        this.println("EOF_ = 1,");
        for (int i = 4; i < vocabulary.size(); ++i) {
            final String str = (String)vocabulary.elementAt(i);
            if (str != null) {
                if (str.startsWith("\"")) {
                    final StringLiteralSymbol stringLiteralSymbol = (StringLiteralSymbol)tokenManager.getTokenSymbol(str);
                    if (stringLiteralSymbol == null) {
                        this.antlrTool.panic("String literal " + str + " not in symbol table");
                    }
                    else if (stringLiteralSymbol.label != null) {
                        this.println(stringLiteralSymbol.label + " = " + i + ",");
                    }
                    else {
                        final String mangleLiteral = this.mangleLiteral(str);
                        if (mangleLiteral != null) {
                            this.println(mangleLiteral + " = " + i + ",");
                            stringLiteralSymbol.label = mangleLiteral;
                        }
                        else {
                            this.println("// " + str + " = " + i);
                        }
                    }
                }
                else if (!str.startsWith("<")) {
                    this.println(str + " = " + i + ",");
                }
            }
        }
        this.println("NULL_TREE_LOOKAHEAD = 3");
        --this.tabs;
        this.println("};");
        --this.tabs;
        this.println("#ifdef __cplusplus");
        this.println("};");
        this.println("#endif");
        if (CppCodeGenerator.nameSpace != null) {
            CppCodeGenerator.nameSpace.emitClosures(this.currentOutput);
        }
        this.println("#endif /*INC_" + tokenManager.getName() + CppCodeGenerator.TokenTypesFileSuffix + "_hpp_*/");
        this.currentOutput.close();
        this.currentOutput = null;
        this.exitIfError();
    }
    
    public String processStringForASTConstructor(final String str) {
        if (this.usingCustomAST && (this.grammar instanceof TreeWalkerGrammar || this.grammar instanceof ParserGrammar) && !this.grammar.tokenManager.tokenDefined(str)) {
            return CppCodeGenerator.namespaceAntlr + "RefAST(" + str + ")";
        }
        return str;
    }
    
    public String getASTCreateString(final Vector vector) {
        if (vector.size() == 0) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        sb.append(this.labeledElementASTType + "(astFactory->make((new " + CppCodeGenerator.namespaceAntlr + "ASTArray(" + vector.size() + "))");
        for (int i = 0; i < vector.size(); ++i) {
            sb.append("->add(" + vector.elementAt(i) + ")");
        }
        sb.append("))");
        return sb.toString();
    }
    
    public String getASTCreateString(final GrammarAtom grammarAtom, final String str) {
        if (grammarAtom != null && grammarAtom.getASTNodeType() != null) {
            this.astTypes.ensureCapacity(grammarAtom.getType());
            final String str2 = (String)this.astTypes.elementAt(grammarAtom.getType());
            if (str2 == null) {
                this.astTypes.setElementAt(grammarAtom.getASTNodeType(), grammarAtom.getType());
            }
            else if (!grammarAtom.getASTNodeType().equals(str2)) {
                this.antlrTool.warning("Attempt to redefine AST type for " + grammarAtom.getText(), this.grammar.getFilename(), grammarAtom.getLine(), grammarAtom.getColumn());
                this.antlrTool.warning(" from \"" + str2 + "\" to \"" + grammarAtom.getASTNodeType() + "\" sticking to \"" + str2 + "\"", this.grammar.getFilename(), grammarAtom.getLine(), grammarAtom.getColumn());
            }
            else {
                this.astTypes.setElementAt(grammarAtom.getASTNodeType(), grammarAtom.getType());
            }
            return "astFactory->create(" + str + ")";
        }
        boolean tokenDefined = false;
        if (str.indexOf(44) != -1) {
            tokenDefined = this.grammar.tokenManager.tokenDefined(str.substring(0, str.indexOf(44)));
        }
        if (this.usingCustomAST && this.grammar instanceof TreeWalkerGrammar && !this.grammar.tokenManager.tokenDefined(str) && !tokenDefined) {
            return "astFactory->create(" + CppCodeGenerator.namespaceAntlr + "RefAST(" + str + "))";
        }
        return "astFactory->create(" + str + ")";
    }
    
    public String getASTCreateString(final String s) {
        if (this.usingCustomAST) {
            return this.labeledElementASTType + "(astFactory->create(" + CppCodeGenerator.namespaceAntlr + "RefAST(" + s + ")))";
        }
        return "astFactory->create(" + s + ")";
    }
    
    protected String getLookaheadTestExpression(final Lookahead[] array, final int n) {
        final StringBuffer sb = new StringBuffer(100);
        int n2 = 1;
        sb.append("(");
        for (int i = 1; i <= n; ++i) {
            final BitSet fset = array[i].fset;
            if (n2 == 0) {
                sb.append(") && (");
            }
            n2 = 0;
            if (array[i].containsEpsilon()) {
                sb.append("true");
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
            return "true";
        }
        return "(" + this.getLookaheadTestExpression(alternative.cache, n2) + ")";
    }
    
    protected String getLookaheadTestTerm(final int n, final BitSet set) {
        final String lookaheadString = this.lookaheadString(n);
        final int[] array = set.toArray();
        if (CodeGenerator.elementsAreRange(array)) {
            return this.getRangeExpression(n, array);
        }
        final int degree = set.degree();
        if (degree == 0) {
            return "true";
        }
        if (degree >= this.bitsetTestThreshold) {
            return this.getBitsetName(this.markBitsetForGen(set)) + ".member(" + lookaheadString + ")";
        }
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            final String valueString = this.getValueString(array[i]);
            if (i > 0) {
                sb.append(" || ");
            }
            sb.append(lookaheadString);
            sb.append(" == ");
            sb.append(valueString);
        }
        return sb.toString();
    }
    
    public String getRangeExpression(final int n, final int[] array) {
        if (!CodeGenerator.elementsAreRange(array)) {
            this.antlrTool.panic("getRangeExpression called with non-range");
        }
        return "(" + this.lookaheadString(n) + " >= " + this.getValueString(array[0]) + " && " + this.lookaheadString(n) + " <= " + this.getValueString(array[array.length - 1]) + ")";
    }
    
    private String getValueString(final int n) {
        String s;
        if (this.grammar instanceof LexerGrammar) {
            s = this.charFormatter.literalChar(n);
        }
        else {
            final TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbolAt(n);
            if (tokenSymbol == null) {
                return "" + n;
            }
            final String id = tokenSymbol.getId();
            if (tokenSymbol instanceof StringLiteralSymbol) {
                final String label = ((StringLiteralSymbol)tokenSymbol).getLabel();
                if (label != null) {
                    s = label;
                }
                else {
                    s = this.mangleLiteral(id);
                    if (s == null) {
                        s = String.valueOf(n);
                    }
                }
            }
            else if (id.equals("EOF")) {
                s = CppCodeGenerator.namespaceAntlr + "Token::EOF_TYPE";
            }
            else {
                s = id;
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
            return "_t->getType()";
        }
        return "LA(" + i + ")";
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
            if (substring.length() > 3 && substring.lastIndexOf("_in") == substring.length() - 3) {
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
            if (str == CppCodeGenerator.NONUNIQUE) {
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
                this.treeVariableMap.put(s, CppCodeGenerator.NONUNIQUE);
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
        if (this.grammar == null) {
            return text;
        }
        if ((this.grammar.buildAST && text.indexOf(35) != -1) || this.grammar instanceof TreeWalkerGrammar || ((this.grammar instanceof LexerGrammar || this.grammar instanceof ParserGrammar) && text.indexOf(36) != -1)) {
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
                return text;
            }
            catch (TokenStreamException ex2) {
                this.antlrTool.panic("Error reading action:" + text);
                return text;
            }
            catch (CharStreamException ex3) {
                this.antlrTool.panic("Error reading action:" + text);
                return text;
            }
        }
        return text;
    }
    
    private String fixNameSpaceOption(String str) {
        str = StringUtils.stripFrontBack(str, "\"", "\"");
        if (str.length() > 2 && !str.substring(str.length() - 2, str.length()).equals("::")) {
            str += "::";
        }
        return str;
    }
    
    private void setupGrammarParameters(final Grammar grammar) {
        if (grammar instanceof ParserGrammar || grammar instanceof LexerGrammar || grammar instanceof TreeWalkerGrammar) {
            if (this.antlrTool.nameSpace != null) {
                CppCodeGenerator.nameSpace = this.antlrTool.nameSpace;
            }
            if (this.antlrTool.namespaceStd != null) {
                CppCodeGenerator.namespaceStd = this.fixNameSpaceOption(this.antlrTool.namespaceStd);
            }
            if (this.antlrTool.namespaceAntlr != null) {
                CppCodeGenerator.namespaceAntlr = this.fixNameSpaceOption(this.antlrTool.namespaceAntlr);
            }
            this.genHashLines = this.antlrTool.genHashLines;
            if (grammar.hasOption("namespace")) {
                final Token option = grammar.getOption("namespace");
                if (option != null) {
                    CppCodeGenerator.nameSpace = new NameSpace(option.getText());
                }
            }
            if (grammar.hasOption("namespaceAntlr")) {
                final Token option2 = grammar.getOption("namespaceAntlr");
                if (option2 != null) {
                    String s = StringUtils.stripFrontBack(option2.getText(), "\"", "\"");
                    if (s != null) {
                        if (s.length() > 2 && !s.substring(s.length() - 2, s.length()).equals("::")) {
                            s += "::";
                        }
                        CppCodeGenerator.namespaceAntlr = s;
                    }
                }
            }
            if (grammar.hasOption("namespaceStd")) {
                final Token option3 = grammar.getOption("namespaceStd");
                if (option3 != null) {
                    String s2 = StringUtils.stripFrontBack(option3.getText(), "\"", "\"");
                    if (s2 != null) {
                        if (s2.length() > 2 && !s2.substring(s2.length() - 2, s2.length()).equals("::")) {
                            s2 += "::";
                        }
                        CppCodeGenerator.namespaceStd = s2;
                    }
                }
            }
            if (grammar.hasOption("genHashLines")) {
                final Token option4 = grammar.getOption("genHashLines");
                if (option4 != null) {
                    this.genHashLines = StringUtils.stripFrontBack(option4.getText(), "\"", "\"").equals("true");
                }
            }
            this.noConstructors = this.antlrTool.noConstructors;
            if (grammar.hasOption("noConstructors")) {
                final Token option5 = grammar.getOption("noConstructors");
                if (option5 != null && !option5.getText().equals("true") && !option5.getText().equals("false")) {
                    this.antlrTool.error("noConstructors option must be true or false", this.antlrTool.getGrammarFile(), option5.getLine(), option5.getColumn());
                }
                this.noConstructors = option5.getText().equals("true");
            }
        }
        if (grammar instanceof ParserGrammar) {
            this.labeledElementASTType = CppCodeGenerator.namespaceAntlr + "RefAST";
            this.labeledElementASTInit = CppCodeGenerator.namespaceAntlr + "nullAST";
            if (grammar.hasOption("ASTLabelType")) {
                final Token option6 = grammar.getOption("ASTLabelType");
                if (option6 != null) {
                    final String stripFrontBack = StringUtils.stripFrontBack(option6.getText(), "\"", "\"");
                    if (stripFrontBack != null) {
                        this.usingCustomAST = true;
                        this.labeledElementASTType = stripFrontBack;
                        this.labeledElementASTInit = stripFrontBack + "(" + CppCodeGenerator.namespaceAntlr + "nullAST)";
                    }
                }
            }
            this.labeledElementType = CppCodeGenerator.namespaceAntlr + "RefToken ";
            this.labeledElementInit = CppCodeGenerator.namespaceAntlr + "nullToken";
            this.commonExtraArgs = "";
            this.commonExtraParams = "";
            this.commonLocalVars = "";
            this.lt1Value = "LT(1)";
            this.exceptionThrown = CppCodeGenerator.namespaceAntlr + "RecognitionException";
            this.throwNoViable = "throw " + CppCodeGenerator.namespaceAntlr + "NoViableAltException(LT(1), getFilename());";
        }
        else if (grammar instanceof LexerGrammar) {
            this.labeledElementType = "char ";
            this.labeledElementInit = "'\\0'";
            this.commonExtraArgs = "";
            this.commonExtraParams = "bool _createToken";
            this.commonLocalVars = "int _ttype; " + CppCodeGenerator.namespaceAntlr + "RefToken _token; " + CppCodeGenerator.namespaceStd + "string::size_type _begin = text.length();";
            this.lt1Value = "LA(1)";
            this.exceptionThrown = CppCodeGenerator.namespaceAntlr + "RecognitionException";
            this.throwNoViable = "throw " + CppCodeGenerator.namespaceAntlr + "NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());";
        }
        else if (grammar instanceof TreeWalkerGrammar) {
            this.labeledElementInit = CppCodeGenerator.namespaceAntlr + "nullAST";
            this.labeledElementASTInit = CppCodeGenerator.namespaceAntlr + "nullAST";
            this.labeledElementASTType = CppCodeGenerator.namespaceAntlr + "RefAST";
            this.labeledElementType = CppCodeGenerator.namespaceAntlr + "RefAST";
            this.commonExtraParams = CppCodeGenerator.namespaceAntlr + "RefAST _t";
            this.throwNoViable = "throw " + CppCodeGenerator.namespaceAntlr + "NoViableAltException(_t);";
            this.lt1Value = "_t";
            if (grammar.hasOption("ASTLabelType")) {
                final Token option7 = grammar.getOption("ASTLabelType");
                if (option7 != null) {
                    final String stripFrontBack2 = StringUtils.stripFrontBack(option7.getText(), "\"", "\"");
                    if (stripFrontBack2 != null) {
                        this.usingCustomAST = true;
                        this.labeledElementASTType = stripFrontBack2;
                        this.labeledElementType = stripFrontBack2;
                        this.labeledElementInit = stripFrontBack2 + "(" + CppCodeGenerator.namespaceAntlr + "nullAST)";
                        this.labeledElementASTInit = this.labeledElementInit;
                        this.commonExtraParams = stripFrontBack2 + " _t";
                        this.throwNoViable = "throw " + CppCodeGenerator.namespaceAntlr + "NoViableAltException(" + CppCodeGenerator.namespaceAntlr + "RefAST(_t));";
                        this.lt1Value = "_t";
                    }
                }
            }
            if (!grammar.hasOption("ASTLabelType")) {
                grammar.setOption("ASTLabelType", new Token(6, CppCodeGenerator.namespaceAntlr + "RefAST"));
            }
            this.commonExtraArgs = "_t";
            this.commonLocalVars = "";
            this.exceptionThrown = CppCodeGenerator.namespaceAntlr + "RecognitionException";
        }
        else {
            this.antlrTool.panic("Unknown grammar type");
        }
    }
    
    static {
        NONUNIQUE = new String();
        CppCodeGenerator.namespaceStd = "ANTLR_USE_NAMESPACE(std)";
        CppCodeGenerator.namespaceAntlr = "ANTLR_USE_NAMESPACE(antlr)";
        CppCodeGenerator.nameSpace = null;
    }
}
