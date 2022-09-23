// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.util.StringTokenizer;
import antlr.actions.csharp.ActionLexer;
import antlr.collections.impl.BitSet;
import java.util.Enumeration;
import java.io.IOException;
import antlr.collections.impl.Vector;
import java.util.Hashtable;

public class CSharpCodeGenerator extends CodeGenerator
{
    protected int syntacticPredLevel;
    protected boolean genAST;
    protected boolean saveText;
    boolean usingCustomAST;
    String labeledElementType;
    String labeledElementASTType;
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
    private java.util.Vector astTypes;
    private static CSharpNameSpace nameSpace;
    int saveIndexCreateLevel;
    int blockNestingLevel;
    
    public CSharpCodeGenerator() {
        this.syntacticPredLevel = 0;
        this.genAST = false;
        this.saveText = false;
        this.usingCustomAST = false;
        this.treeVariableMap = new Hashtable();
        this.declaredASTVariables = new Hashtable();
        this.astVarNumber = 1;
        this.charFormatter = new CSharpCharFormatter();
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
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genAction(" + obj + ")");
        }
        if (obj.isSemPred) {
            this.genSemPred(obj.actionText, obj.line);
        }
        else {
            if (this.grammar.hasSyntacticPredicate) {
                this.println("if (0==inputState.guessing)");
                this.println("{");
                ++this.tabs;
            }
            final ActionTransInfo actionTransInfo = new ActionTransInfo();
            final String processActionForSpecialSymbols = this.processActionForSpecialSymbols(obj.actionText, obj.getLine(), this.currentRule, actionTransInfo);
            if (actionTransInfo.refRuleRoot != null) {
                this.println(actionTransInfo.refRuleRoot + " = (" + this.labeledElementASTType + ")currentAST.root;");
            }
            this.printAction(processActionForSpecialSymbols);
            if (actionTransInfo.assignToRoot) {
                this.println("currentAST.root = " + actionTransInfo.refRuleRoot + ";");
                this.println("if ( (null != " + actionTransInfo.refRuleRoot + ") && (null != " + actionTransInfo.refRuleRoot + ".getFirstChild()) )");
                ++this.tabs;
                this.println("currentAST.child = " + actionTransInfo.refRuleRoot + ".getFirstChild();");
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
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("gen(" + obj + ")");
        }
        this.println("{");
        ++this.tabs;
        this.genBlockPreamble(obj);
        this.genBlockInitAction(obj);
        final String currentASTResult = this.currentASTResult;
        if (obj.getLabel() != null) {
            this.currentASTResult = obj.getLabel();
        }
        this.grammar.theLLkAnalyzer.deterministic(obj);
        this.genBlockFinish(this.genCommonBlock(obj, true), this.throwNoViable);
        --this.tabs;
        this.println("}");
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
            this.println(obj.getLabel() + " = " + this.lt1Value + ";");
        }
        final boolean saveText = this.saveText;
        this.saveText = (this.saveText && obj.getAutoGenType() == 1);
        this.genMatch(obj);
        this.saveText = saveText;
    }
    
    public void gen(final CharRangeElement charRangeElement) {
        if (charRangeElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(charRangeElement.getLabel() + " = " + this.lt1Value + ";");
        }
        final boolean b = this.grammar instanceof LexerGrammar && (!this.saveText || charRangeElement.getAutoGenType() == 3);
        if (b) {
            this.println("_saveIndex = text.Length;");
        }
        this.println("matchRange(" + OctalToUnicode(charRangeElement.beginText) + "," + OctalToUnicode(charRangeElement.endText) + ");");
        if (b) {
            this.println("text.Length = _saveIndex;");
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
        this.genBody(grammar);
    }
    
    public void gen(final OneOrMoreBlock obj) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("gen+(" + obj + ")");
        }
        this.println("{ // ( ... )+");
        ++this.tabs;
        ++this.blockNestingLevel;
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
        this.println("for (;;)");
        this.println("{");
        ++this.tabs;
        ++this.blockNestingLevel;
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
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("nongreedy (...)+ loop; exit depth is " + obj.exitLookaheadDepth);
            }
            final String lookaheadTestExpression = this.getLookaheadTestExpression(obj.exitCache, n);
            this.println("// nongreedy exit test");
            this.println("if ((" + s + " >= 1) && " + lookaheadTestExpression + ") goto " + str + "_breakloop;");
        }
        this.genBlockFinish(this.genCommonBlock(obj, false), "if (" + s + " >= 1) { goto " + str + "_breakloop; } else { " + this.throwNoViable + "; }");
        this.println(s + "++;");
        --this.tabs;
        if (this.blockNestingLevel-- == this.saveIndexCreateLevel) {
            this.saveIndexCreateLevel = 0;
        }
        this.println("}");
        this._print(str + "_breakloop:");
        this.println(";");
        --this.tabs;
        if (this.blockNestingLevel-- == this.saveIndexCreateLevel) {
            this.saveIndexCreateLevel = 0;
        }
        this.println("}    // ( ... )+");
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
            this.println(obj.getLabel() + " = _t==ASTNULL ? null : " + this.lt1Value + ";");
        }
        if (this.grammar instanceof LexerGrammar && (!this.saveText || obj.getAutoGenType() == 3)) {
            this.declareSaveIndexVariableIfNeeded();
            this.println("_saveIndex = text.Length;");
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
            this.declareSaveIndexVariableIfNeeded();
            this.println("text.Length = _saveIndex;");
        }
        if (this.syntacticPredLevel == 0) {
            final boolean b = this.grammar.hasSyntacticPredicate && ((this.grammar.buildAST && obj.getLabel() != null) || (this.genAST && obj.getAutoGenType() == 1));
            if (b) {
                this.println("if (0 == inputState.guessing)");
                this.println("{");
                ++this.tabs;
            }
            if (this.grammar.buildAST && obj.getLabel() != null) {
                this.println(obj.getLabel() + "_AST = (" + this.labeledElementASTType + ")returnAST;");
            }
            if (this.genAST) {
                switch (obj.getAutoGenType()) {
                    case 1: {
                        if (this.usingCustomAST) {
                            this.println("astFactory.addASTChild(ref currentAST, (AST)returnAST);");
                            break;
                        }
                        this.println("astFactory.addASTChild(ref currentAST, returnAST);");
                        break;
                    }
                    case 2: {
                        this.antlrTool.error("Internal: encountered ^ after rule reference");
                        break;
                    }
                }
            }
            if (this.grammar instanceof LexerGrammar && obj.getLabel() != null) {
                this.println(obj.getLabel() + " = returnToken_;");
            }
            if (b) {
                --this.tabs;
                this.println("}");
            }
        }
        this.genErrorCatchForElement(obj);
    }
    
    public void gen(final StringLiteralElement obj) {
        if (this.DEBUG_CODE_GENERATOR) {
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
            this.println("_t = _t.getNextSibling();");
        }
    }
    
    public void gen(final TokenRangeElement tokenRangeElement) {
        this.genErrorTryForElement(tokenRangeElement);
        if (tokenRangeElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(tokenRangeElement.getLabel() + " = " + this.lt1Value + ";");
        }
        this.genElementAST(tokenRangeElement);
        this.println("matchRange(" + OctalToUnicode(tokenRangeElement.beginText) + "," + OctalToUnicode(tokenRangeElement.endText) + ");");
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
            this.println(obj.getLabel() + " = " + this.lt1Value + ";");
        }
        this.genElementAST(obj);
        this.genMatch(obj);
        this.genErrorCatchForElement(obj);
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _t.getNextSibling();");
        }
    }
    
    public void gen(final TreeElement treeElement) {
        this.println("AST __t" + treeElement.ID + " = _t;");
        if (treeElement.root.getLabel() != null) {
            this.println(treeElement.root.getLabel() + " = (ASTNULL == _t) ? null : (" + this.labeledElementASTType + ")_t;");
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
            this.println("ASTPair __currentAST" + treeElement.ID + " = currentAST.copy();");
            this.println("currentAST.root = currentAST.child;");
            this.println("currentAST.child = null;");
        }
        if (treeElement.root instanceof WildcardElement) {
            this.println("if (null == _t) throw new MismatchedTokenException();");
        }
        else {
            this.genMatch(treeElement.root);
        }
        this.println("_t = _t.getFirstChild();");
        for (int i = 0; i < treeElement.getAlternatives().size(); ++i) {
            for (AlternativeElement alternativeElement = treeElement.getAlternativeAt(i).head; alternativeElement != null; alternativeElement = alternativeElement.next) {
                alternativeElement.generate();
            }
        }
        if (this.grammar.buildAST) {
            this.println("currentAST = __currentAST" + treeElement.ID + ";");
        }
        this.println("_t = __t" + treeElement.ID + ";");
        this.println("_t = _t.getNextSibling();");
    }
    
    public void gen(final TreeWalkerGrammar grammar) throws IOException {
        this.setGrammar(grammar);
        if (!(this.grammar instanceof TreeWalkerGrammar)) {
            this.antlrTool.panic("Internal error generating tree-walker");
        }
        this.genBody(grammar);
    }
    
    public void gen(final WildcardElement wildcardElement) {
        if (wildcardElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(wildcardElement.getLabel() + " = " + this.lt1Value + ";");
        }
        this.genElementAST(wildcardElement);
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("if (null == _t) throw new MismatchedTokenException();");
        }
        else if (this.grammar instanceof LexerGrammar) {
            if (this.grammar instanceof LexerGrammar && (!this.saveText || wildcardElement.getAutoGenType() == 3)) {
                this.declareSaveIndexVariableIfNeeded();
                this.println("_saveIndex = text.Length;");
            }
            this.println("matchNot(EOF/*_CHAR*/);");
            if (this.grammar instanceof LexerGrammar && (!this.saveText || wildcardElement.getAutoGenType() == 3)) {
                this.declareSaveIndexVariableIfNeeded();
                this.println("text.Length = _saveIndex;");
            }
        }
        else {
            this.println("matchNot(" + this.getValueString(1) + ");");
        }
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _t.getNextSibling();");
        }
    }
    
    public void gen(final ZeroOrMoreBlock obj) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("gen*(" + obj + ")");
        }
        this.println("{    // ( ... )*");
        ++this.tabs;
        ++this.blockNestingLevel;
        this.genBlockPreamble(obj);
        String str;
        if (obj.getLabel() != null) {
            str = obj.getLabel();
        }
        else {
            str = "_loop" + obj.ID;
        }
        this.println("for (;;)");
        this.println("{");
        ++this.tabs;
        ++this.blockNestingLevel;
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
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("nongreedy (...)* loop; exit depth is " + obj.exitLookaheadDepth);
            }
            final String lookaheadTestExpression = this.getLookaheadTestExpression(obj.exitCache, n);
            this.println("// nongreedy exit test");
            this.println("if (" + lookaheadTestExpression + ") goto " + str + "_breakloop;");
        }
        this.genBlockFinish(this.genCommonBlock(obj, false), "goto " + str + "_breakloop;");
        --this.tabs;
        if (this.blockNestingLevel-- == this.saveIndexCreateLevel) {
            this.saveIndexCreateLevel = 0;
        }
        this.println("}");
        this._print(str + "_breakloop:");
        this.println(";");
        --this.tabs;
        if (this.blockNestingLevel-- == this.saveIndexCreateLevel) {
            this.saveIndexCreateLevel = 0;
        }
        this.println("}    // ( ... )*");
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
            this.println("try        // for error handling");
            this.println("{");
            ++this.tabs;
        }
        for (AlternativeElement alternativeElement = alternative.head; !(alternativeElement instanceof BlockEndElement); alternativeElement = alternativeElement.next) {
            alternativeElement.generate();
        }
        if (this.genAST) {
            if (alternativeBlock instanceof RuleBlock) {
                final RuleBlock ruleBlock = (RuleBlock)alternativeBlock;
                if (this.usingCustomAST) {
                    this.println(ruleBlock.getRuleName() + "_AST = (" + this.labeledElementASTType + ")currentAST.root;");
                }
                else {
                    this.println(ruleBlock.getRuleName() + "_AST = currentAST.root;");
                }
            }
            else if (alternativeBlock.getLabel() != null) {
                this.antlrTool.warning("Labeled subrules not yet supported", this.grammar.getFilename(), alternativeBlock.getLine(), alternativeBlock.getColumn());
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
    
    protected void genBitsets(final Vector vector, final int n) {
        this.println("");
        for (int i = 0; i < vector.size(); ++i) {
            final BitSet set = (BitSet)vector.elementAt(i);
            set.growToInclude(n);
            this.genBitSet(set, i);
        }
    }
    
    private void genBitSet(final BitSet set, final int n) {
        this.println("private static long[] mk_" + this.getBitsetName(n) + "()");
        this.println("{");
        ++this.tabs;
        final int lengthInLongWords = set.lengthInLongWords();
        if (lengthInLongWords < 8) {
            this.println("long[] data = { " + set.toStringOfWords() + "};");
        }
        else {
            this.println("long[] data = new long[" + lengthInLongWords + "];");
            final long[] packedArray = set.toPackedArray();
            int i = 0;
            while (i < packedArray.length) {
                if (i + 1 == packedArray.length || packedArray[i] != packedArray[i + 1]) {
                    this.println("data[" + i + "]=" + packedArray[i] + "L;");
                    ++i;
                }
                else {
                    int n2;
                    for (n2 = i + 1; n2 < packedArray.length && packedArray[n2] == packedArray[i]; ++n2) {}
                    this.println("for (int i = " + i + "; i<=" + (n2 - 1) + "; i++) { data[i]=" + packedArray[i] + "L; }");
                    i = n2;
                }
            }
        }
        this.println("return data;");
        --this.tabs;
        this.println("}");
        this.println("public static readonly BitSet " + this.getBitsetName(n) + " = new BitSet(" + "mk_" + this.getBitsetName(n) + "()" + ");");
    }
    
    protected String getBitsetName(final int i) {
        return "tokenSet_" + i + "_";
    }
    
    private void genBlockFinish(final CSharpBlockFinishingInfo cSharpBlockFinishingInfo, final String s) {
        if (cSharpBlockFinishingInfo.needAnErrorClause && (cSharpBlockFinishingInfo.generatedAnIf || cSharpBlockFinishingInfo.generatedSwitch)) {
            if (cSharpBlockFinishingInfo.generatedAnIf) {
                this.println("else");
                this.println("{");
            }
            else {
                this.println("{");
            }
            ++this.tabs;
            this.println(s);
            --this.tabs;
            this.println("}");
        }
        if (cSharpBlockFinishingInfo.postscript != null) {
            if (cSharpBlockFinishingInfo.needAnErrorClause && cSharpBlockFinishingInfo.generatedSwitch && !cSharpBlockFinishingInfo.generatedAnIf && s != null) {
                if (s.indexOf("throw") == 0 || s.indexOf("goto") == 0) {
                    this.println(cSharpBlockFinishingInfo.postscript.substring(cSharpBlockFinishingInfo.postscript.indexOf("break;") + 6));
                }
                else {
                    this.println(cSharpBlockFinishingInfo.postscript);
                }
            }
            else {
                this.println(cSharpBlockFinishingInfo.postscript);
            }
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
                                this.println("IToken " + alternativeElement.getLabel() + " = null;");
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
    
    public void genBody(final LexerGrammar lexerGrammar) throws IOException {
        this.setupOutput(this.grammar.getClassName());
        this.genAST = false;
        this.saveText = true;
        this.tabs = 0;
        this.genHeader();
        this.println(this.behavior.getHeaderAction(""));
        if (CSharpCodeGenerator.nameSpace != null) {
            CSharpCodeGenerator.nameSpace.emitDeclarations(this.currentOutput);
        }
        ++this.tabs;
        this.println("// Generate header specific to lexer CSharp file");
        this.println("using System;");
        this.println("using Stream                          = System.IO.Stream;");
        this.println("using TextReader                      = System.IO.TextReader;");
        this.println("using Hashtable                       = System.Collections.Hashtable;");
        this.println("using Comparer                        = System.Collections.Comparer;");
        if (!lexerGrammar.caseSensitiveLiterals) {
            this.println("using CaseInsensitiveHashCodeProvider = System.Collections.CaseInsensitiveHashCodeProvider;");
            this.println("using CaseInsensitiveComparer         = System.Collections.CaseInsensitiveComparer;");
        }
        this.println("");
        this.println("using TokenStreamException            = antlr.TokenStreamException;");
        this.println("using TokenStreamIOException          = antlr.TokenStreamIOException;");
        this.println("using TokenStreamRecognitionException = antlr.TokenStreamRecognitionException;");
        this.println("using CharStreamException             = antlr.CharStreamException;");
        this.println("using CharStreamIOException           = antlr.CharStreamIOException;");
        this.println("using ANTLRException                  = antlr.ANTLRException;");
        this.println("using CharScanner                     = antlr.CharScanner;");
        this.println("using InputBuffer                     = antlr.InputBuffer;");
        this.println("using ByteBuffer                      = antlr.ByteBuffer;");
        this.println("using CharBuffer                      = antlr.CharBuffer;");
        this.println("using Token                           = antlr.Token;");
        this.println("using IToken                          = antlr.IToken;");
        this.println("using CommonToken                     = antlr.CommonToken;");
        this.println("using SemanticException               = antlr.SemanticException;");
        this.println("using RecognitionException            = antlr.RecognitionException;");
        this.println("using NoViableAltForCharException     = antlr.NoViableAltForCharException;");
        this.println("using MismatchedCharException         = antlr.MismatchedCharException;");
        this.println("using TokenStream                     = antlr.TokenStream;");
        this.println("using LexerSharedInputState           = antlr.LexerSharedInputState;");
        this.println("using BitSet                          = antlr.collections.impl.BitSet;");
        this.println(this.grammar.preambleAction.getText());
        String str;
        if (this.grammar.superClass != null) {
            str = this.grammar.superClass;
        }
        else {
            str = "antlr." + this.grammar.getSuperClass();
        }
        if (this.grammar.comment != null) {
            this._println(this.grammar.comment);
        }
        final Token token = this.grammar.options.get("classHeaderPrefix");
        if (token == null) {
            this.print("public ");
        }
        else {
            final String stripFrontBack = StringUtils.stripFrontBack(token.getText(), "\"", "\"");
            if (stripFrontBack == null) {
                this.print("public ");
            }
            else {
                this.print(stripFrontBack + " ");
            }
        }
        this.print("class " + this.grammar.getClassName() + " : " + str);
        this.println(", TokenStream");
        final Token token2 = this.grammar.options.get("classHeaderSuffix");
        if (token2 != null) {
            final String stripFrontBack2 = StringUtils.stripFrontBack(token2.getText(), "\"", "\"");
            if (stripFrontBack2 != null) {
                this.print(", " + stripFrontBack2);
            }
        }
        this.println(" {");
        ++this.tabs;
        this.genTokenDefinitions(this.grammar.tokenManager);
        this.print(this.processActionForSpecialSymbols(this.grammar.classMemberAction.getText(), this.grammar.classMemberAction.getLine(), this.currentRule, null));
        this.println("public " + this.grammar.getClassName() + "(Stream ins) : this(new ByteBuffer(ins))");
        this.println("{");
        this.println("}");
        this.println("");
        this.println("public " + this.grammar.getClassName() + "(TextReader r) : this(new CharBuffer(r))");
        this.println("{");
        this.println("}");
        this.println("");
        this.print("public " + this.grammar.getClassName() + "(InputBuffer ib)");
        if (this.grammar.debuggingOutput) {
            this.println(" : this(new LexerSharedInputState(new antlr.debug.DebuggingInputBuffer(ib)))");
        }
        else {
            this.println(" : this(new LexerSharedInputState(ib))");
        }
        this.println("{");
        this.println("}");
        this.println("");
        this.println("public " + this.grammar.getClassName() + "(LexerSharedInputState state) : base(state)");
        this.println("{");
        ++this.tabs;
        this.println("initialize();");
        --this.tabs;
        this.println("}");
        this.println("private void initialize()");
        this.println("{");
        ++this.tabs;
        if (this.grammar.debuggingOutput) {
            this.println("ruleNames  = _ruleNames;");
            this.println("semPredNames = _semPredNames;");
            this.println("setupDebugging();");
        }
        this.println("caseSensitiveLiterals = " + lexerGrammar.caseSensitiveLiterals + ";");
        this.println("setCaseSensitive(" + lexerGrammar.caseSensitive + ");");
        if (lexerGrammar.caseSensitiveLiterals) {
            this.println("literals = new Hashtable(100, (float) 0.4, null, Comparer.Default);");
        }
        else {
            this.println("literals = new Hashtable(100, (float) 0.4, CaseInsensitiveHashCodeProvider.Default, CaseInsensitiveComparer.Default);");
        }
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
            this.println("literals.Add(" + stringLiteralSymbol.getId() + ", " + stringLiteralSymbol.getTokenType() + ");");
        }
        --this.tabs;
        this.println("}");
        if (this.grammar.debuggingOutput) {
            this.println("private static readonly string[] _ruleNames = new string[] {");
            final Enumeration elements = this.grammar.rules.elements();
            while (elements.hasMoreElements()) {
                final GrammarSymbol grammarSymbol = elements.nextElement();
                if (grammarSymbol instanceof RuleSymbol) {
                    this.println("  \"" + ((RuleSymbol)grammarSymbol).getId() + "\",");
                }
            }
            this.println("};");
        }
        this.genNextToken();
        final Enumeration elements2 = this.grammar.rules.elements();
        int n = 0;
        while (elements2.hasMoreElements()) {
            final RuleSymbol ruleSymbol = elements2.nextElement();
            if (!ruleSymbol.getId().equals("mnextToken")) {
                this.genRule(ruleSymbol, false, n++, this.grammar.tokenManager);
            }
            this.exitIfError();
        }
        if (this.grammar.debuggingOutput) {
            this.genSemPredMap();
        }
        this.genBitsets(this.bitsetsUsed, ((LexerGrammar)this.grammar).charVocabulary.size());
        this.println("");
        --this.tabs;
        this.println("}");
        --this.tabs;
        if (CSharpCodeGenerator.nameSpace != null) {
            CSharpCodeGenerator.nameSpace.emitClosures(this.currentOutput);
        }
        this.currentOutput.close();
        this.currentOutput = null;
    }
    
    public void genInitFactory(final Grammar grammar) {
        if (grammar.buildAST) {
            this.println("static public void initializeASTFactory( ASTFactory factory )");
            this.println("{");
            ++this.tabs;
            this.println("factory.setMaxNodeType(" + grammar.tokenManager.maxTokenType() + ");");
            final Vector vocabulary = grammar.tokenManager.getVocabulary();
            for (int i = 0; i < vocabulary.size(); ++i) {
                final String str = (String)vocabulary.elementAt(i);
                if (str != null) {
                    final TokenSymbol tokenSymbol = grammar.tokenManager.getTokenSymbol(str);
                    if (tokenSymbol != null && tokenSymbol.getASTNodeType() != null) {
                        this.println("factory.setTokenTypeASTNodeType(" + str + ", \"" + tokenSymbol.getASTNodeType() + "\");");
                    }
                }
            }
            --this.tabs;
            this.println("}");
        }
    }
    
    public void genBody(final ParserGrammar parserGrammar) throws IOException {
        this.setupOutput(this.grammar.getClassName());
        this.genAST = this.grammar.buildAST;
        this.tabs = 0;
        this.genHeader();
        this.println(this.behavior.getHeaderAction(""));
        if (CSharpCodeGenerator.nameSpace != null) {
            CSharpCodeGenerator.nameSpace.emitDeclarations(this.currentOutput);
        }
        ++this.tabs;
        this.println("// Generate the header common to all output files.");
        this.println("using System;");
        this.println("");
        this.println("using TokenBuffer              = antlr.TokenBuffer;");
        this.println("using TokenStreamException     = antlr.TokenStreamException;");
        this.println("using TokenStreamIOException   = antlr.TokenStreamIOException;");
        this.println("using ANTLRException           = antlr.ANTLRException;");
        final String superClass = this.grammar.getSuperClass();
        final String[] split = this.split(superClass, ".");
        this.println("using " + split[split.length - 1] + " = antlr." + superClass + ";");
        this.println("using Token                    = antlr.Token;");
        this.println("using IToken                   = antlr.IToken;");
        this.println("using TokenStream              = antlr.TokenStream;");
        this.println("using RecognitionException     = antlr.RecognitionException;");
        this.println("using NoViableAltException     = antlr.NoViableAltException;");
        this.println("using MismatchedTokenException = antlr.MismatchedTokenException;");
        this.println("using SemanticException        = antlr.SemanticException;");
        this.println("using ParserSharedInputState   = antlr.ParserSharedInputState;");
        this.println("using BitSet                   = antlr.collections.impl.BitSet;");
        if (this.genAST) {
            this.println("using AST                      = antlr.collections.AST;");
            this.println("using ASTPair                  = antlr.ASTPair;");
            this.println("using ASTFactory               = antlr.ASTFactory;");
            this.println("using ASTArray                 = antlr.collections.impl.ASTArray;");
        }
        this.println(this.grammar.preambleAction.getText());
        String str;
        if (this.grammar.superClass != null) {
            str = this.grammar.superClass;
        }
        else {
            str = "antlr." + this.grammar.getSuperClass();
        }
        if (this.grammar.comment != null) {
            this._println(this.grammar.comment);
        }
        final Token token = this.grammar.options.get("classHeaderPrefix");
        if (token == null) {
            this.print("public ");
        }
        else {
            final String stripFrontBack = StringUtils.stripFrontBack(token.getText(), "\"", "\"");
            if (stripFrontBack == null) {
                this.print("public ");
            }
            else {
                this.print(stripFrontBack + " ");
            }
        }
        this.println("class " + this.grammar.getClassName() + " : " + str);
        final Token token2 = this.grammar.options.get("classHeaderSuffix");
        if (token2 != null) {
            final String stripFrontBack2 = StringUtils.stripFrontBack(token2.getText(), "\"", "\"");
            if (stripFrontBack2 != null) {
                this.print("              , " + stripFrontBack2);
            }
        }
        this.println("{");
        ++this.tabs;
        this.genTokenDefinitions(this.grammar.tokenManager);
        if (this.grammar.debuggingOutput) {
            this.println("private static readonly string[] _ruleNames = new string[] {");
            ++this.tabs;
            final Enumeration elements = this.grammar.rules.elements();
            while (elements.hasMoreElements()) {
                final GrammarSymbol grammarSymbol = elements.nextElement();
                if (grammarSymbol instanceof RuleSymbol) {
                    this.println("  \"" + ((RuleSymbol)grammarSymbol).getId() + "\",");
                }
            }
            --this.tabs;
            this.println("};");
        }
        this.print(this.processActionForSpecialSymbols(this.grammar.classMemberAction.getText(), this.grammar.classMemberAction.getLine(), this.currentRule, null));
        this.println("");
        this.println("protected void initialize()");
        this.println("{");
        ++this.tabs;
        this.println("tokenNames = tokenNames_;");
        if (this.grammar.buildAST) {
            this.println("initializeFactory();");
        }
        if (this.grammar.debuggingOutput) {
            this.println("ruleNames  = _ruleNames;");
            this.println("semPredNames = _semPredNames;");
            this.println("setupDebugging(tokenBuf);");
        }
        --this.tabs;
        this.println("}");
        this.println("");
        this.println("");
        this.println("protected " + this.grammar.getClassName() + "(TokenBuffer tokenBuf, int k) : base(tokenBuf, k)");
        this.println("{");
        ++this.tabs;
        this.println("initialize();");
        --this.tabs;
        this.println("}");
        this.println("");
        this.println("public " + this.grammar.getClassName() + "(TokenBuffer tokenBuf) : this(tokenBuf," + this.grammar.maxk + ")");
        this.println("{");
        this.println("}");
        this.println("");
        this.println("protected " + this.grammar.getClassName() + "(TokenStream lexer, int k) : base(lexer,k)");
        this.println("{");
        ++this.tabs;
        this.println("initialize();");
        --this.tabs;
        this.println("}");
        this.println("");
        this.println("public " + this.grammar.getClassName() + "(TokenStream lexer) : this(lexer," + this.grammar.maxk + ")");
        this.println("{");
        this.println("}");
        this.println("");
        this.println("public " + this.grammar.getClassName() + "(ParserSharedInputState state) : base(state," + this.grammar.maxk + ")");
        this.println("{");
        ++this.tabs;
        this.println("initialize();");
        --this.tabs;
        this.println("}");
        this.println("");
        this.astTypes = new java.util.Vector(100);
        final Enumeration elements2 = this.grammar.rules.elements();
        int n = 0;
        while (elements2.hasMoreElements()) {
            final GrammarSymbol grammarSymbol2 = elements2.nextElement();
            if (grammarSymbol2 instanceof RuleSymbol) {
                final RuleSymbol ruleSymbol;
                this.genRule(ruleSymbol, (ruleSymbol = (RuleSymbol)grammarSymbol2).references.size() == 0, n++, this.grammar.tokenManager);
            }
            this.exitIfError();
        }
        if (this.usingCustomAST) {
            this.println("public new " + this.labeledElementASTType + " getAST()");
            this.println("{");
            ++this.tabs;
            this.println("return (" + this.labeledElementASTType + ") returnAST;");
            --this.tabs;
            this.println("}");
            this.println("");
        }
        this.println("private void initializeFactory()");
        this.println("{");
        ++this.tabs;
        if (this.grammar.buildAST) {
            this.println("if (astFactory == null)");
            this.println("{");
            ++this.tabs;
            if (this.usingCustomAST) {
                this.println("astFactory = new ASTFactory(\"" + this.labeledElementASTType + "\");");
            }
            else {
                this.println("astFactory = new ASTFactory();");
            }
            --this.tabs;
            this.println("}");
            this.println("initializeASTFactory( astFactory );");
        }
        --this.tabs;
        this.println("}");
        this.genInitFactory(parserGrammar);
        this.genTokenStrings();
        this.genBitsets(this.bitsetsUsed, this.grammar.tokenManager.maxTokenType());
        if (this.grammar.debuggingOutput) {
            this.genSemPredMap();
        }
        this.println("");
        --this.tabs;
        this.println("}");
        --this.tabs;
        if (CSharpCodeGenerator.nameSpace != null) {
            CSharpCodeGenerator.nameSpace.emitClosures(this.currentOutput);
        }
        this.currentOutput.close();
        this.currentOutput = null;
    }
    
    public void genBody(final TreeWalkerGrammar treeWalkerGrammar) throws IOException {
        this.setupOutput(this.grammar.getClassName());
        this.genAST = this.grammar.buildAST;
        this.tabs = 0;
        this.genHeader();
        this.println(this.behavior.getHeaderAction(""));
        if (CSharpCodeGenerator.nameSpace != null) {
            CSharpCodeGenerator.nameSpace.emitDeclarations(this.currentOutput);
        }
        ++this.tabs;
        this.println("// Generate header specific to the tree-parser CSharp file");
        this.println("using System;");
        this.println("");
        this.println("using " + this.grammar.getSuperClass() + " = antlr." + this.grammar.getSuperClass() + ";");
        this.println("using Token                    = antlr.Token;");
        this.println("using IToken                   = antlr.IToken;");
        this.println("using AST                      = antlr.collections.AST;");
        this.println("using RecognitionException     = antlr.RecognitionException;");
        this.println("using ANTLRException           = antlr.ANTLRException;");
        this.println("using NoViableAltException     = antlr.NoViableAltException;");
        this.println("using MismatchedTokenException = antlr.MismatchedTokenException;");
        this.println("using SemanticException        = antlr.SemanticException;");
        this.println("using BitSet                   = antlr.collections.impl.BitSet;");
        this.println("using ASTPair                  = antlr.ASTPair;");
        this.println("using ASTFactory               = antlr.ASTFactory;");
        this.println("using ASTArray                 = antlr.collections.impl.ASTArray;");
        this.println(this.grammar.preambleAction.getText());
        String str;
        if (this.grammar.superClass != null) {
            str = this.grammar.superClass;
        }
        else {
            str = "antlr." + this.grammar.getSuperClass();
        }
        this.println("");
        if (this.grammar.comment != null) {
            this._println(this.grammar.comment);
        }
        final Token token = this.grammar.options.get("classHeaderPrefix");
        if (token == null) {
            this.print("public ");
        }
        else {
            final String stripFrontBack = StringUtils.stripFrontBack(token.getText(), "\"", "\"");
            if (stripFrontBack == null) {
                this.print("public ");
            }
            else {
                this.print(stripFrontBack + " ");
            }
        }
        this.println("class " + this.grammar.getClassName() + " : " + str);
        final Token token2 = this.grammar.options.get("classHeaderSuffix");
        if (token2 != null) {
            final String stripFrontBack2 = StringUtils.stripFrontBack(token2.getText(), "\"", "\"");
            if (stripFrontBack2 != null) {
                this.print("              , " + stripFrontBack2);
            }
        }
        this.println("{");
        ++this.tabs;
        this.genTokenDefinitions(this.grammar.tokenManager);
        this.print(this.processActionForSpecialSymbols(this.grammar.classMemberAction.getText(), this.grammar.classMemberAction.getLine(), this.currentRule, null));
        this.println("public " + this.grammar.getClassName() + "()");
        this.println("{");
        ++this.tabs;
        this.println("tokenNames = tokenNames_;");
        --this.tabs;
        this.println("}");
        this.println("");
        this.astTypes = new java.util.Vector();
        final Enumeration elements = this.grammar.rules.elements();
        int n = 0;
        while (elements.hasMoreElements()) {
            final GrammarSymbol grammarSymbol = elements.nextElement();
            if (grammarSymbol instanceof RuleSymbol) {
                final RuleSymbol ruleSymbol;
                this.genRule(ruleSymbol, (ruleSymbol = (RuleSymbol)grammarSymbol).references.size() == 0, n++, this.grammar.tokenManager);
            }
            this.exitIfError();
        }
        if (this.usingCustomAST) {
            this.println("public new " + this.labeledElementASTType + " getAST()");
            this.println("{");
            ++this.tabs;
            this.println("return (" + this.labeledElementASTType + ") returnAST;");
            --this.tabs;
            this.println("}");
            this.println("");
        }
        this.genInitFactory(this.grammar);
        this.genTokenStrings();
        this.genBitsets(this.bitsetsUsed, this.grammar.tokenManager.maxTokenType());
        --this.tabs;
        this.println("}");
        this.println("");
        --this.tabs;
        if (CSharpCodeGenerator.nameSpace != null) {
            CSharpCodeGenerator.nameSpace.emitClosures(this.currentOutput);
        }
        this.currentOutput.close();
        this.currentOutput = null;
    }
    
    protected void genCases(final BitSet obj) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genCases(" + obj + ")");
        }
        final int[] array = obj.toArray();
        final int n = (this.grammar instanceof LexerGrammar) ? 4 : 1;
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
    
    public CSharpBlockFinishingInfo genCommonBlock(final AlternativeBlock obj, final boolean b) {
        int n = 0;
        boolean b2 = false;
        int n2 = 0;
        final CSharpBlockFinishingInfo cSharpBlockFinishingInfo = new CSharpBlockFinishingInfo();
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genCommonBlock(" + obj + ")");
        }
        final boolean genAST = this.genAST;
        this.genAST = (this.genAST && obj.getAutoGen());
        final boolean saveText = this.saveText;
        this.saveText = (this.saveText && obj.getAutoGen());
        if (obj.not && this.analyzer.subruleCanBeInverted(obj, this.grammar instanceof LexerGrammar)) {
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("special case: ~(subrule)");
            }
            final Lookahead look = this.analyzer.look(1, obj);
            if (obj.getLabel() != null && this.syntacticPredLevel == 0) {
                this.println(obj.getLabel() + " = " + this.lt1Value + ";");
            }
            this.genElementAST(obj);
            String str = "";
            if (this.grammar instanceof TreeWalkerGrammar) {
                if (this.usingCustomAST) {
                    str = "(AST)_t,";
                }
                else {
                    str = "_t,";
                }
            }
            this.println("match(" + str + this.getBitsetName(this.markBitsetForGen(look.fset)) + ");");
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println("_t = _t.getNextSibling();");
            }
            return cSharpBlockFinishingInfo;
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
                return cSharpBlockFinishingInfo;
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
                this.println("if (null == _t)");
                ++this.tabs;
                this.println("_t = ASTNULL;");
                --this.tabs;
            }
            this.println("switch ( " + lookaheadString + " )");
            this.println("{");
            ++this.blockNestingLevel;
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
                        ++this.blockNestingLevel;
                        this.genAlt(alternative2, obj);
                        this.println("break;");
                        if (this.blockNestingLevel-- == this.saveIndexCreateLevel) {
                            this.saveIndexCreateLevel = 0;
                        }
                        --this.tabs;
                        this.println("}");
                    }
                }
            }
            this.println("default:");
            ++this.tabs;
        }
        for (int k = (this.grammar instanceof LexerGrammar) ? this.grammar.maxk : 0; k >= 0; --k) {
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("checking depth " + k);
            }
            for (int l = 0; l < obj.alternatives.size(); ++l) {
                final Alternative alternative3 = obj.getAlternativeAt(l);
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
                            this.println("if " + s);
                            this.println("{");
                        }
                        else {
                            this.println("else if " + s);
                            this.println("{");
                        }
                    }
                    else if (b3 && alternative3.semPred == null && alternative3.synPred == null) {
                        if (n == 0) {
                            this.println("{");
                        }
                        else {
                            this.println("else {");
                        }
                        cSharpBlockFinishingInfo.needAnErrorClause = false;
                    }
                    else {
                        if (alternative3.semPred != null) {
                            final String processActionForSpecialSymbols = this.processActionForSpecialSymbols(alternative3.semPred, obj.line, this.currentRule, new ActionTransInfo());
                            if ((this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar) && this.grammar.debuggingOutput) {
                                s = "(" + s + "&& fireSemanticPredicateEvaluated(antlr.debug.SemanticPredicateEventArgs.PREDICTING," + this.addSemPred(this.charFormatter.escapeString(processActionForSpecialSymbols)) + "," + processActionForSpecialSymbols + "))";
                            }
                            else {
                                s = "(" + s + "&&(" + processActionForSpecialSymbols + "))";
                            }
                        }
                        if (n > 0) {
                            if (alternative3.synPred != null) {
                                this.println("else {");
                                ++this.tabs;
                                ++this.blockNestingLevel;
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
                                this.println("if (_t == null)");
                                ++this.tabs;
                                this.println("_t = ASTNULL;");
                                --this.tabs;
                            }
                            this.println("if " + s);
                            this.println("{");
                        }
                    }
                    ++this.blockNestingLevel;
                    ++n;
                    ++this.tabs;
                    this.genAlt(alternative3, obj);
                    --this.tabs;
                    if (this.blockNestingLevel-- == this.saveIndexCreateLevel) {
                        this.saveIndexCreateLevel = 0;
                    }
                    this.println("}");
                }
            }
        }
        String string = "";
        for (int n4 = 1; n4 <= n2; ++n4) {
            string += "}";
            if (this.blockNestingLevel-- == this.saveIndexCreateLevel) {
                this.saveIndexCreateLevel = 0;
            }
        }
        this.genAST = genAST;
        this.saveText = saveText;
        if (b2) {
            --this.tabs;
            cSharpBlockFinishingInfo.postscript = string + "break; }";
            if (this.blockNestingLevel-- == this.saveIndexCreateLevel) {
                this.saveIndexCreateLevel = 0;
            }
            cSharpBlockFinishingInfo.generatedSwitch = true;
            cSharpBlockFinishingInfo.generatedAnIf = (n > 0);
        }
        else {
            cSharpBlockFinishingInfo.postscript = string;
            cSharpBlockFinishingInfo.generatedSwitch = false;
            cSharpBlockFinishingInfo.generatedAnIf = (n > 0);
        }
        return cSharpBlockFinishingInfo;
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
                this.println(this.labeledElementASTType + " " + string2 + "_in = null;");
            }
            if (b2) {}
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
                            this.println("astFactory.addASTChild(ref currentAST, (AST)" + string2 + ");");
                            break;
                        }
                        this.println("astFactory.addASTChild(ref currentAST, " + string2 + ");");
                        break;
                    }
                    case 2: {
                        if (this.usingCustomAST || (alternativeElement instanceof GrammarAtom && ((GrammarAtom)alternativeElement).getASTNodeType() != null)) {
                            this.println("astFactory.makeASTRoot(ref currentAST, (AST)" + string2 + ");");
                            break;
                        }
                        this.println("astFactory.makeASTRoot(ref currentAST, " + string2 + ");");
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
            this.println("}");
            this.genErrorHandler(exceptionSpec);
        }
    }
    
    private void genErrorHandler(final ExceptionSpec exceptionSpec) {
        for (int i = 0; i < exceptionSpec.handlers.size(); ++i) {
            final ExceptionHandler exceptionHandler = (ExceptionHandler)exceptionSpec.handlers.elementAt(i);
            this.println("catch (" + exceptionHandler.exceptionTypeAndName.getText() + ")");
            this.println("{");
            ++this.tabs;
            if (this.grammar.hasSyntacticPredicate) {
                this.println("if (0 == inputState.guessing)");
                this.println("{");
                ++this.tabs;
            }
            this.printAction(this.processActionForSpecialSymbols(exceptionHandler.action.getText(), exceptionHandler.action.getLine(), this.currentRule, new ActionTransInfo()));
            if (this.grammar.hasSyntacticPredicate) {
                --this.tabs;
                this.println("}");
                this.println("else");
                this.println("{");
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
            this.println("try   // for error handling");
            this.println("{");
            ++this.tabs;
        }
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
        this.println(str2 + " " + str + "_AST = null;");
        this.declaredASTVariables.put(alternativeElement, alternativeElement);
    }
    
    protected void genHeader() {
        this.println("// $ANTLR " + Tool.version + ": " + "\"" + this.antlrTool.fileMinusPath(this.antlrTool.grammarFile) + "\"" + " -> " + "\"" + this.grammar.getClassName() + ".cs\"$");
    }
    
    private void genLiteralsTest() {
        this.println("_ttype = testLiteralsTable(_ttype);");
    }
    
    private void genLiteralsTestForPartialToken() {
        this.println("_ttype = testLiteralsTable(text.ToString(_begin, text.Length-_begin), _ttype);");
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
            if (this.usingCustomAST) {
                s = "(AST)_t,";
            }
            else {
                s = "_t,";
            }
        }
        if (this.grammar instanceof LexerGrammar && (!this.saveText || grammarAtom.getAutoGenType() == 3)) {
            this.declareSaveIndexVariableIfNeeded();
            this.println("_saveIndex = text.Length;");
        }
        this.print(grammarAtom.not ? "matchNot(" : "match(");
        this._print(s);
        if (grammarAtom.atomText.equals("EOF")) {
            this._print("Token.EOF_TYPE");
        }
        else {
            this._print(grammarAtom.atomText);
        }
        this._println(");");
        if (this.grammar instanceof LexerGrammar && (!this.saveText || grammarAtom.getAutoGenType() == 3)) {
            this.declareSaveIndexVariableIfNeeded();
            this.println("text.Length = _saveIndex;");
        }
    }
    
    protected void genMatchUsingAtomTokenType(final GrammarAtom grammarAtom) {
        String str = "";
        if (this.grammar instanceof TreeWalkerGrammar) {
            if (this.usingCustomAST) {
                str = "(AST)_t,";
            }
            else {
                str = "_t,";
            }
        }
        this.println((grammarAtom.not ? "matchNot(" : "match(") + (str + this.getValueString(grammarAtom.getType())) + ");");
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
            this.println("override public IToken nextToken()\t\t\t//throws TokenStreamException");
            this.println("{");
            ++this.tabs;
            this.println("try");
            this.println("{");
            ++this.tabs;
            this.println("uponEOF();");
            --this.tabs;
            this.println("}");
            this.println("catch(CharStreamIOException csioe)");
            this.println("{");
            ++this.tabs;
            this.println("throw new TokenStreamIOException(csioe.io);");
            --this.tabs;
            this.println("}");
            this.println("catch(CharStreamException cse)");
            this.println("{");
            ++this.tabs;
            this.println("throw new TokenStreamException(cse.Message);");
            --this.tabs;
            this.println("}");
            this.println("return new CommonToken(Token.EOF_TYPE, \"\");");
            --this.tabs;
            this.println("}");
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
        this.println("override public IToken nextToken()\t\t\t//throws TokenStreamException");
        this.println("{");
        ++this.tabs;
        this.blockNestingLevel = 1;
        this.saveIndexCreateLevel = 0;
        this.println("IToken theRetToken = null;");
        this._println("tryAgain:");
        this.println("for (;;)");
        this.println("{");
        ++this.tabs;
        this.println("IToken _token = null;");
        this.println("int _ttype = Token.INVALID_TYPE;");
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
        this.println("try     // for char stream error handling");
        this.println("{");
        ++this.tabs;
        this.println("try     // for lexical error handling");
        this.println("{");
        ++this.tabs;
        for (int j = 0; j < nextTokenRule.getAlternatives().size(); ++j) {
            final Alternative alternative = nextTokenRule.getAlternativeAt(j);
            if (alternative.cache[1].containsEpsilon()) {
                this.antlrTool.warning("public lexical rule " + CodeGenerator.decodeLexerRuleName(((RuleRefElement)alternative.head).targetRule) + " is optional (can match \"nothing\")");
            }
        }
        final String property = System.getProperty("line.separator");
        final CSharpBlockFinishingInfo genCommonBlock = this.genCommonBlock(nextTokenRule, false);
        final String string = "if (cached_LA1==EOF_CHAR) { uponEOF(); returnToken_ = makeToken(Token.EOF_TYPE); }" + property + "\t\t\t\t";
        String s;
        if (((LexerGrammar)this.grammar).filterMode) {
            if (filterRule == null) {
                s = string + "\t\t\t\telse" + "\t\t\t\t{" + "\t\t\t\t\tconsume();" + "\t\t\t\t\tgoto tryAgain;" + "\t\t\t\t}";
            }
            else {
                s = string + "\t\t\t\t\telse" + property + "\t\t\t\t\t{" + property + "\t\t\t\t\tcommit();" + property + "\t\t\t\t\ttry {m" + filterRule + "(false);}" + property + "\t\t\t\t\tcatch(RecognitionException e)" + property + "\t\t\t\t\t{" + property + "\t\t\t\t\t\t// catastrophic failure" + property + "\t\t\t\t\t\treportError(e);" + property + "\t\t\t\t\t\tconsume();" + property + "\t\t\t\t\t}" + property + "\t\t\t\t\tgoto tryAgain;" + property + "\t\t\t\t}";
            }
        }
        else {
            s = string + "else {" + this.throwNoViable + "}";
        }
        this.genBlockFinish(genCommonBlock, s);
        if (((LexerGrammar)this.grammar).filterMode && filterRule != null) {
            this.println("commit();");
        }
        this.println("if ( null==returnToken_ ) goto tryAgain; // found SKIP token");
        this.println("_ttype = returnToken_.Type;");
        if (((LexerGrammar)this.grammar).getTestLiterals()) {
            this.genLiteralsTest();
        }
        this.println("returnToken_.Type = _ttype;");
        this.println("return returnToken_;");
        --this.tabs;
        this.println("}");
        this.println("catch (RecognitionException e) {");
        ++this.tabs;
        if (((LexerGrammar)this.grammar).filterMode) {
            if (filterRule == null) {
                this.println("if (!getCommitToPath())");
                this.println("{");
                ++this.tabs;
                this.println("consume();");
                this.println("goto tryAgain;");
                --this.tabs;
                this.println("}");
            }
            else {
                this.println("if (!getCommitToPath())");
                this.println("{");
                ++this.tabs;
                this.println("rewind(_m);");
                this.println("resetText();");
                this.println("try {m" + filterRule + "(false);}");
                this.println("catch(RecognitionException ee) {");
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
            this.println("throw new TokenStreamRecognitionException(e);");
            --this.tabs;
        }
        --this.tabs;
        this.println("}");
        --this.tabs;
        this.println("}");
        this.println("catch (CharStreamException cse) {");
        this.println("\tif ( cse is CharStreamIOException ) {");
        this.println("\t\tthrow new TokenStreamIOException(((CharStreamIOException)cse).io);");
        this.println("\t}");
        this.println("\telse {");
        this.println("\t\tthrow new TokenStreamException(cse.Message);");
        this.println("\t}");
        this.println("}");
        --this.tabs;
        this.println("}");
        --this.tabs;
        this.println("}");
        this.println("");
    }
    
    public void genRule(final RuleSymbol ruleSymbol, final boolean b, final int n, final TokenManager tokenManager) {
        this.tabs = 1;
        if (this.DEBUG_CODE_GENERATOR) {
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
        this.print(ruleSymbol.access + " ");
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
        this._print(" //throws " + this.exceptionThrown);
        if (this.grammar instanceof ParserGrammar) {
            this._print(", TokenStreamException");
        }
        else if (this.grammar instanceof LexerGrammar) {
            this._print(", CharStreamException, TokenStreamException");
        }
        if (block.throwsSpec != null) {
            if (this.grammar instanceof LexerGrammar) {
                this.antlrTool.error("user-defined throws spec not allowed (yet) for lexer rule " + block.ruleName);
            }
            else {
                this._print(", " + block.throwsSpec);
            }
        }
        this._println("");
        this._println("{");
        ++this.tabs;
        if (block.returnAction != null) {
            this.println(block.returnAction + ";");
        }
        this.println(this.commonLocalVars);
        if (this.grammar.traceRules) {
            if (this.grammar instanceof TreeWalkerGrammar) {
                if (this.usingCustomAST) {
                    this.println("traceIn(\"" + ruleSymbol.getId() + "\",(AST)_t);");
                }
                else {
                    this.println("traceIn(\"" + ruleSymbol.getId() + "\",_t);");
                }
            }
            else {
                this.println("traceIn(\"" + ruleSymbol.getId() + "\");");
            }
        }
        if (this.grammar instanceof LexerGrammar) {
            if (ruleSymbol.getId().equals("mEOF")) {
                this.println("_ttype = Token.EOF_TYPE;");
            }
            else {
                this.println("_ttype = " + ruleSymbol.getId().substring(1) + ";");
            }
            this.blockNestingLevel = 1;
            this.saveIndexCreateLevel = 0;
        }
        if (this.grammar.debuggingOutput) {
            if (this.grammar instanceof ParserGrammar) {
                this.println("fireEnterRule(" + n + ",0);");
            }
            else if (this.grammar instanceof LexerGrammar) {
                this.println("fireEnterRule(" + n + ",_ttype);");
            }
        }
        if (this.grammar.debuggingOutput || this.grammar.traceRules) {
            this.println("try { // debugging");
            ++this.tabs;
        }
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println(this.labeledElementASTType + " " + ruleSymbol.getId() + "_AST_in = (" + this.labeledElementASTType + ")_t;");
        }
        if (this.grammar.buildAST) {
            this.println("returnAST = null;");
            this.println("ASTPair currentAST = new ASTPair();");
            this.println(this.labeledElementASTType + " " + ruleSymbol.getId() + "_AST = null;");
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
            this.println("catch (" + this.exceptionThrown + " ex)");
            this.println("{");
            ++this.tabs;
            if (this.grammar.hasSyntacticPredicate) {
                this.println("if (0 == inputState.guessing)");
                this.println("{");
                ++this.tabs;
            }
            this.println("reportError(ex);");
            if (!(this.grammar instanceof TreeWalkerGrammar)) {
                this.println("recover(ex," + this.getBitsetName(this.markBitsetForGen(this.grammar.theLLkAnalyzer.FOLLOW(1, block.endNode).fset)) + ");");
            }
            else {
                this.println("if (null != _t)");
                this.println("{");
                ++this.tabs;
                this.println("_t = _t.getNextSibling();");
                --this.tabs;
                this.println("}");
            }
            if (this.grammar.hasSyntacticPredicate) {
                --this.tabs;
                this.println("}");
                this.println("else");
                this.println("{");
                ++this.tabs;
                this.println("throw ex;");
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
            this.println("retTree_ = _t;");
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
            this.println("if (_createToken && (null == _token) && (_ttype != Token.SKIP))");
            this.println("{");
            ++this.tabs;
            this.println("_token = makeToken(_ttype);");
            this.println("_token.setText(text.ToString(_begin, text.Length-_begin));");
            --this.tabs;
            this.println("}");
            this.println("returnToken_ = _token;");
        }
        if (block.returnAction != null) {
            this.println("return " + this.extractIdOfAction(block.returnAction, block.getLine(), block.getColumn()) + ";");
        }
        if (this.grammar.debuggingOutput || this.grammar.traceRules) {
            --this.tabs;
            this.println("}");
            this.println("finally");
            this.println("{ // debugging");
            ++this.tabs;
            if (this.grammar.debuggingOutput) {
                if (this.grammar instanceof ParserGrammar) {
                    this.println("fireExitRule(" + n + ",0);");
                }
                else if (this.grammar instanceof LexerGrammar) {
                    this.println("fireExitRule(" + n + ",_ttype);");
                }
            }
            if (this.grammar.traceRules) {
                if (this.grammar instanceof TreeWalkerGrammar) {
                    this.println("traceOut(\"" + ruleSymbol.getId() + "\",_t);");
                }
                else {
                    this.println("traceOut(\"" + ruleSymbol.getId() + "\");");
                }
            }
            --this.tabs;
            this.println("}");
        }
        --this.tabs;
        this.println("}");
        this.println("");
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
        this._println(");");
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = retTree_;");
        }
    }
    
    protected void genSemPred(String s, final int n) {
        s = this.processActionForSpecialSymbols(s, n, this.currentRule, new ActionTransInfo());
        final String escapeString = this.charFormatter.escapeString(s);
        if (this.grammar.debuggingOutput && (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar)) {
            s = "fireSemanticPredicateEvaluated(antlr.debug.SemanticPredicateEvent.VALIDATING," + this.addSemPred(escapeString) + "," + s + ")";
        }
        this.println("if (!(" + s + "))");
        this.println("  throw new SemanticException(\"" + escapeString + "\");");
    }
    
    protected void genSemPredMap() {
        final Enumeration elements = this.semPreds.elements();
        this.println("private string[] _semPredNames = {");
        ++this.tabs;
        while (elements.hasMoreElements()) {
            this.println("\"" + elements.nextElement() + "\",");
        }
        --this.tabs;
        this.println("};");
    }
    
    protected void genSynPred(final SynPredBlock obj, final String str) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("gen=>(" + obj + ")");
        }
        this.println("bool synPredMatched" + obj.ID + " = false;");
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("if (_t==null) _t=ASTNULL;");
        }
        this.println("if (" + str + ")");
        this.println("{");
        ++this.tabs;
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("AST __t" + obj.ID + " = _t;");
        }
        else {
            this.println("int _m" + obj.ID + " = mark();");
        }
        this.println("synPredMatched" + obj.ID + " = true;");
        this.println("inputState.guessing++;");
        if (this.grammar.debuggingOutput && (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar)) {
            this.println("fireSyntacticPredicateStarted();");
        }
        ++this.syntacticPredLevel;
        this.println("try {");
        ++this.tabs;
        this.gen(obj);
        --this.tabs;
        this.println("}");
        this.println("catch (" + this.exceptionThrown + ")");
        this.println("{");
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
        this.println("inputState.guessing--;");
        if (this.grammar.debuggingOutput && (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar)) {
            this.println("if (synPredMatched" + obj.ID + ")");
            this.println("  fireSyntacticPredicateSucceeded();");
            this.println("else");
            this.println("  fireSyntacticPredicateFailed();");
        }
        --this.syntacticPredLevel;
        --this.tabs;
        this.println("}");
        this.println("if ( synPredMatched" + obj.ID + " )");
        this.println("{");
    }
    
    public void genTokenStrings() {
        this.println("");
        this.println("public static readonly string[] tokenNames_ = new string[] {");
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
            else if (s.startsWith("\"")) {
                s = StringUtils.stripFrontBack(s, "\"", "\"");
            }
            this.print(this.charFormatter.literalString(s));
            if (i != vocabulary.size() - 1) {
                this._print(",");
            }
            this._println("");
        }
        --this.tabs;
        this.println("};");
    }
    
    protected void genTokenTypes(final TokenManager tokenManager) throws IOException {
        this.setupOutput(tokenManager.getName() + CSharpCodeGenerator.TokenTypesFileSuffix);
        this.tabs = 0;
        this.genHeader();
        this.println(this.behavior.getHeaderAction(""));
        if (CSharpCodeGenerator.nameSpace != null) {
            CSharpCodeGenerator.nameSpace.emitDeclarations(this.currentOutput);
        }
        ++this.tabs;
        this.println("public class " + tokenManager.getName() + CSharpCodeGenerator.TokenTypesFileSuffix);
        this.println("{");
        ++this.tabs;
        this.genTokenDefinitions(tokenManager);
        --this.tabs;
        this.println("}");
        --this.tabs;
        if (CSharpCodeGenerator.nameSpace != null) {
            CSharpCodeGenerator.nameSpace.emitClosures(this.currentOutput);
        }
        this.currentOutput.close();
        this.currentOutput = null;
        this.exitIfError();
    }
    
    protected void genTokenDefinitions(final TokenManager tokenManager) throws IOException {
        final Vector vocabulary = tokenManager.getVocabulary();
        this.println("public const int EOF = 1;");
        this.println("public const int NULL_TREE_LOOKAHEAD = 3;");
        for (int i = 4; i < vocabulary.size(); ++i) {
            final String str = (String)vocabulary.elementAt(i);
            if (str != null) {
                if (str.startsWith("\"")) {
                    final StringLiteralSymbol stringLiteralSymbol = (StringLiteralSymbol)tokenManager.getTokenSymbol(str);
                    if (stringLiteralSymbol == null) {
                        this.antlrTool.panic("String literal " + str + " not in symbol table");
                    }
                    else if (stringLiteralSymbol.label != null) {
                        this.println("public const int " + stringLiteralSymbol.label + " = " + i + ";");
                    }
                    else {
                        final String mangleLiteral = this.mangleLiteral(str);
                        if (mangleLiteral != null) {
                            this.println("public const int " + mangleLiteral + " = " + i + ";");
                            stringLiteralSymbol.label = mangleLiteral;
                        }
                        else {
                            this.println("// " + str + " = " + i);
                        }
                    }
                }
                else if (!str.startsWith("<")) {
                    this.println("public const int " + str + " = " + i + ";");
                }
            }
        }
        this.println("");
    }
    
    public String processStringForASTConstructor(final String str) {
        if (this.usingCustomAST && (this.grammar instanceof TreeWalkerGrammar || this.grammar instanceof ParserGrammar) && !this.grammar.tokenManager.tokenDefined(str)) {
            return "(AST)" + str;
        }
        return str;
    }
    
    public String getASTCreateString(final Vector vector) {
        if (vector.size() == 0) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        sb.append("(" + this.labeledElementASTType + ") astFactory.make(");
        sb.append(vector.elementAt(0));
        for (int i = 1; i < vector.size(); ++i) {
            sb.append(", " + vector.elementAt(i));
        }
        sb.append(")");
        return sb.toString();
    }
    
    public String getASTCreateString(final GrammarAtom grammarAtom, final String s) {
        String s2 = "astFactory.create(" + s + ")";
        if (grammarAtom == null) {
            return this.getASTCreateString(s);
        }
        if (grammarAtom.getASTNodeType() != null) {
            final TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbol(grammarAtom.getText());
            if (tokenSymbol == null || tokenSymbol.getASTNodeType() != grammarAtom.getASTNodeType()) {
                s2 = "(" + grammarAtom.getASTNodeType() + ") astFactory.create(" + s + ", \"" + grammarAtom.getASTNodeType() + "\")";
            }
            else if (tokenSymbol != null && tokenSymbol.getASTNodeType() != null) {
                s2 = "(" + tokenSymbol.getASTNodeType() + ") " + s2;
            }
        }
        else if (this.usingCustomAST) {
            s2 = "(" + this.labeledElementASTType + ") " + s2;
        }
        return s2;
    }
    
    public String getASTCreateString(String str) {
        if (str == null) {
            str = "";
        }
        String s = "astFactory.create(" + str + ")";
        String substring = str;
        final int index = str.indexOf(44);
        if (index != -1) {
            substring = str.substring(0, index);
            if (str.substring(index + 1, str.length()).indexOf(44) != -1) {}
        }
        final TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbol(substring);
        if (null != tokenSymbol && null != tokenSymbol.getASTNodeType()) {
            s = "(" + tokenSymbol.getASTNodeType() + ") " + s;
        }
        else if (this.usingCustomAST) {
            s = "(" + this.labeledElementASTType + ") " + s;
        }
        return s;
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
            return "( true )";
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
                sb.append("||");
            }
            sb.append(lookaheadString);
            sb.append("==");
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
            return "_t.Type";
        }
        if (this.grammar instanceof LexerGrammar) {
            if (i == 1) {
                return "cached_LA1";
            }
            if (i == 2) {
                return "cached_LA2";
            }
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
            if (str == CSharpCodeGenerator.NONUNIQUE) {
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
                this.treeVariableMap.put(s, CSharpCodeGenerator.NONUNIQUE);
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
    
    private void setupGrammarParameters(final Grammar grammar) {
        if (grammar instanceof ParserGrammar || grammar instanceof LexerGrammar || grammar instanceof TreeWalkerGrammar) {
            if (this.antlrTool.nameSpace != null) {
                CSharpCodeGenerator.nameSpace = new CSharpNameSpace(this.antlrTool.nameSpace.getName());
            }
            if (grammar.hasOption("namespace")) {
                final Token option = grammar.getOption("namespace");
                if (option != null) {
                    CSharpCodeGenerator.nameSpace = new CSharpNameSpace(option.getText());
                }
            }
        }
        if (grammar instanceof ParserGrammar) {
            this.labeledElementASTType = "AST";
            if (grammar.hasOption("ASTLabelType")) {
                final Token option2 = grammar.getOption("ASTLabelType");
                if (option2 != null) {
                    final String stripFrontBack = StringUtils.stripFrontBack(option2.getText(), "\"", "\"");
                    if (stripFrontBack != null) {
                        this.usingCustomAST = true;
                        this.labeledElementASTType = stripFrontBack;
                    }
                }
            }
            this.labeledElementType = "IToken ";
            this.labeledElementInit = "null";
            this.commonExtraArgs = "";
            this.commonExtraParams = "";
            this.commonLocalVars = "";
            this.lt1Value = "LT(1)";
            this.exceptionThrown = "RecognitionException";
            this.throwNoViable = "throw new NoViableAltException(LT(1), getFilename());";
        }
        else if (grammar instanceof LexerGrammar) {
            this.labeledElementType = "char ";
            this.labeledElementInit = "'\\0'";
            this.commonExtraArgs = "";
            this.commonExtraParams = "bool _createToken";
            this.commonLocalVars = "int _ttype; IToken _token=null; int _begin=text.Length;";
            this.lt1Value = "cached_LA1";
            this.exceptionThrown = "RecognitionException";
            this.throwNoViable = "throw new NoViableAltForCharException(cached_LA1, getFilename(), getLine(), getColumn());";
        }
        else if (grammar instanceof TreeWalkerGrammar) {
            this.labeledElementASTType = "AST";
            this.labeledElementType = "AST";
            if (grammar.hasOption("ASTLabelType")) {
                final Token option3 = grammar.getOption("ASTLabelType");
                if (option3 != null) {
                    final String stripFrontBack2 = StringUtils.stripFrontBack(option3.getText(), "\"", "\"");
                    if (stripFrontBack2 != null) {
                        this.usingCustomAST = true;
                        this.labeledElementASTType = stripFrontBack2;
                        this.labeledElementType = stripFrontBack2;
                    }
                }
            }
            if (!grammar.hasOption("ASTLabelType")) {
                grammar.setOption("ASTLabelType", new Token(6, "AST"));
            }
            this.labeledElementInit = "null";
            this.commonExtraArgs = "_t";
            this.commonExtraParams = "AST _t";
            this.commonLocalVars = "";
            if (this.usingCustomAST) {
                this.lt1Value = "(_t==ASTNULL) ? null : (" + this.labeledElementASTType + ")_t";
            }
            else {
                this.lt1Value = "_t";
            }
            this.exceptionThrown = "RecognitionException";
            this.throwNoViable = "throw new NoViableAltException(_t);";
        }
        else {
            this.antlrTool.panic("Unknown grammar type");
        }
    }
    
    public void setupOutput(final String str) throws IOException {
        this.currentOutput = this.antlrTool.openOutputFile(str + ".cs");
    }
    
    private static String OctalToUnicode(final String s) {
        if (4 <= s.length() && '\'' == s.charAt(0) && '\\' == s.charAt(1) && '0' <= s.charAt(2) && '7' >= s.charAt(2) && '\'' == s.charAt(s.length() - 1)) {
            return "'\\x" + Integer.toHexString(Integer.valueOf(s.substring(2, s.length() - 1), 8)) + "'";
        }
        return s;
    }
    
    public String getTokenTypesClassName() {
        return new String(this.grammar.tokenManager.getName() + CSharpCodeGenerator.TokenTypesFileSuffix);
    }
    
    private void declareSaveIndexVariableIfNeeded() {
        if (this.saveIndexCreateLevel == 0) {
            this.println("int _saveIndex = 0;");
            this.saveIndexCreateLevel = this.blockNestingLevel;
        }
    }
    
    public String[] split(final String str, final String delim) {
        final StringTokenizer stringTokenizer = new StringTokenizer(str, delim);
        final String[] array = new String[stringTokenizer.countTokens()];
        int n = 0;
        while (stringTokenizer.hasMoreTokens()) {
            array[n] = stringTokenizer.nextToken();
            ++n;
        }
        return array;
    }
    
    static {
        NONUNIQUE = new String();
        CSharpCodeGenerator.nameSpace = null;
    }
}
