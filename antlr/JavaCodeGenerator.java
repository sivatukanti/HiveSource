// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.actions.java.ActionLexer;
import antlr.collections.impl.BitSet;
import java.util.Enumeration;
import java.io.IOException;
import antlr.collections.impl.Vector;
import java.util.Hashtable;

public class JavaCodeGenerator extends CodeGenerator
{
    public static final int NO_MAPPING = -999;
    public static final int CONTINUE_LAST_MAPPING = -888;
    private JavaCodeGeneratorPrintWriterManager printWriterManager;
    private int defaultLine;
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
    RuleBlock currentRule;
    String currentASTResult;
    Hashtable treeVariableMap;
    Hashtable declaredASTVariables;
    int astVarNumber;
    protected static final String NONUNIQUE;
    public static final int caseSizeThreshold = 127;
    private Vector semPreds;
    
    public JavaCodeGenerator() {
        this.defaultLine = -999;
        this.syntacticPredLevel = 0;
        this.genAST = false;
        this.saveText = false;
        this.treeVariableMap = new Hashtable();
        this.declaredASTVariables = new Hashtable();
        this.astVarNumber = 1;
        this.charFormatter = new JavaCharFormatter();
    }
    
    protected void printAction(final String s) {
        this.printAction(s, this.defaultLine);
    }
    
    protected void printAction(final String s, final int n) {
        this.getPrintWriterManager().startMapping(n);
        super.printAction(s);
        this.getPrintWriterManager().endMapping();
    }
    
    public void println(final String s) {
        this.println(s, this.defaultLine);
    }
    
    public void println(final String s, final int n) {
        if (n > 0 || n == -888) {
            this.getPrintWriterManager().startSingleSourceLineMapping(n);
        }
        super.println(s);
        if (n > 0 || n == -888) {
            this.getPrintWriterManager().endMapping();
        }
    }
    
    protected void print(final String s) {
        this.print(s, this.defaultLine);
    }
    
    protected void print(final String s, final int n) {
        if (n > 0 || n == -888) {
            this.getPrintWriterManager().startMapping(n);
        }
        super.print(s);
        if (n > 0 || n == -888) {
            this.getPrintWriterManager().endMapping();
        }
    }
    
    protected void _print(final String s) {
        this._print(s, this.defaultLine);
    }
    
    protected void _print(final String s, final int n) {
        if (n > 0 || n == -888) {
            this.getPrintWriterManager().startMapping(n);
        }
        super._print(s);
        if (n > 0 || n == -888) {
            this.getPrintWriterManager().endMapping();
        }
    }
    
    protected void _println(final String s) {
        this._println(s, this.defaultLine);
    }
    
    protected void _println(final String s, final int n) {
        if (n > 0 || n == -888) {
            this.getPrintWriterManager().startMapping(n);
        }
        super._println(s);
        if (n > 0 || n == -888) {
            this.getPrintWriterManager().endMapping();
        }
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
        final int defaultLine = this.defaultLine;
        try {
            this.defaultLine = obj.getLine();
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("genAction(" + obj + ")");
            }
            if (obj.isSemPred) {
                this.genSemPred(obj.actionText, obj.line);
            }
            else {
                if (this.grammar.hasSyntacticPredicate) {
                    this.println("if ( inputState.guessing==0 ) {");
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
                    this.println("currentAST.child = " + actionTransInfo.refRuleRoot + "!=null &&" + actionTransInfo.refRuleRoot + ".getFirstChild()!=null ?", -999);
                    ++this.tabs;
                    this.println(actionTransInfo.refRuleRoot + ".getFirstChild() : " + actionTransInfo.refRuleRoot + ";");
                    --this.tabs;
                    this.println("currentAST.advanceChildToEnd();");
                }
                if (this.grammar.hasSyntacticPredicate) {
                    --this.tabs;
                    this.println("}", -999);
                }
            }
        }
        finally {
            this.defaultLine = defaultLine;
        }
    }
    
    public void gen(final AlternativeBlock obj) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("gen(" + obj + ")");
        }
        this.println("{", -999);
        this.genBlockPreamble(obj);
        this.genBlockInitAction(obj);
        final String currentASTResult = this.currentASTResult;
        if (obj.getLabel() != null) {
            this.currentASTResult = obj.getLabel();
        }
        this.grammar.theLLkAnalyzer.deterministic(obj);
        this.genBlockFinish(this.genCommonBlock(obj, true), this.throwNoViable, obj.getLine());
        this.println("}", -999);
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
            this.println(obj.getLabel() + " = " + this.lt1Value + ";", obj.getLine());
        }
        final boolean saveText = this.saveText;
        this.saveText = (this.saveText && obj.getAutoGenType() == 1);
        this.genMatch(obj);
        this.saveText = saveText;
    }
    
    public void gen(final CharRangeElement charRangeElement) {
        final int defaultLine = this.defaultLine;
        try {
            this.defaultLine = charRangeElement.getLine();
            if (charRangeElement.getLabel() != null && this.syntacticPredLevel == 0) {
                this.println(charRangeElement.getLabel() + " = " + this.lt1Value + ";");
            }
            final boolean b = this.grammar instanceof LexerGrammar && (!this.saveText || charRangeElement.getAutoGenType() == 3);
            if (b) {
                this.println("_saveIndex=text.length();");
            }
            this.println("matchRange(" + charRangeElement.beginText + "," + charRangeElement.endText + ");");
            if (b) {
                this.println("text.setLength(_saveIndex);");
            }
        }
        finally {
            this.defaultLine = defaultLine;
        }
    }
    
    public void gen(final LexerGrammar grammar) throws IOException {
        final int defaultLine = this.defaultLine;
        try {
            this.defaultLine = -999;
            if (grammar.debuggingOutput) {
                this.semPreds = new Vector();
            }
            this.setGrammar(grammar);
            if (!(this.grammar instanceof LexerGrammar)) {
                this.antlrTool.panic("Internal error generating lexer");
            }
            this.currentOutput = this.getPrintWriterManager().setupOutput(this.antlrTool, this.grammar);
            this.genAST = false;
            this.saveText = true;
            this.tabs = 0;
            this.genHeader();
            try {
                this.defaultLine = this.behavior.getHeaderActionLine("");
                this.println(this.behavior.getHeaderAction(""));
            }
            finally {
                this.defaultLine = -999;
            }
            this.println("import java.io.InputStream;");
            this.println("import antlr.TokenStreamException;");
            this.println("import antlr.TokenStreamIOException;");
            this.println("import antlr.TokenStreamRecognitionException;");
            this.println("import antlr.CharStreamException;");
            this.println("import antlr.CharStreamIOException;");
            this.println("import antlr.ANTLRException;");
            this.println("import java.io.Reader;");
            this.println("import java.util.Hashtable;");
            this.println("import antlr." + this.grammar.getSuperClass() + ";");
            this.println("import antlr.InputBuffer;");
            this.println("import antlr.ByteBuffer;");
            this.println("import antlr.CharBuffer;");
            this.println("import antlr.Token;");
            this.println("import antlr.CommonToken;");
            this.println("import antlr.RecognitionException;");
            this.println("import antlr.NoViableAltForCharException;");
            this.println("import antlr.MismatchedCharException;");
            this.println("import antlr.TokenStream;");
            this.println("import antlr.ANTLRHashString;");
            this.println("import antlr.LexerSharedInputState;");
            this.println("import antlr.collections.impl.BitSet;");
            this.println("import antlr.SemanticException;");
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
            String str2 = "public";
            final Token token = this.grammar.options.get("classHeaderPrefix");
            if (token != null) {
                final String stripFrontBack = StringUtils.stripFrontBack(token.getText(), "\"", "\"");
                if (stripFrontBack != null) {
                    str2 = stripFrontBack;
                }
            }
            this.print(str2 + " ");
            this.print("class " + this.grammar.getClassName() + " extends " + str);
            this.println(" implements " + this.grammar.tokenManager.getName() + JavaCodeGenerator.TokenTypesFileSuffix + ", TokenStream");
            final Token token2 = this.grammar.options.get("classHeaderSuffix");
            if (token2 != null) {
                final String stripFrontBack2 = StringUtils.stripFrontBack(token2.getText(), "\"", "\"");
                if (stripFrontBack2 != null) {
                    this.print(", " + stripFrontBack2);
                }
            }
            this.println(" {");
            this.print(this.processActionForSpecialSymbols(this.grammar.classMemberAction.getText(), this.grammar.classMemberAction.getLine(), this.currentRule, null), this.grammar.classMemberAction.getLine());
            this.println("public " + this.grammar.getClassName() + "(InputStream in) {");
            ++this.tabs;
            this.println("this(new ByteBuffer(in));");
            --this.tabs;
            this.println("}");
            this.println("public " + this.grammar.getClassName() + "(Reader in) {");
            ++this.tabs;
            this.println("this(new CharBuffer(in));");
            --this.tabs;
            this.println("}");
            this.println("public " + this.grammar.getClassName() + "(InputBuffer ib) {");
            ++this.tabs;
            if (this.grammar.debuggingOutput) {
                this.println("this(new LexerSharedInputState(new antlr.debug.DebuggingInputBuffer(ib)));");
            }
            else {
                this.println("this(new LexerSharedInputState(ib));");
            }
            --this.tabs;
            this.println("}");
            this.println("public " + this.grammar.getClassName() + "(LexerSharedInputState state) {");
            ++this.tabs;
            this.println("super(state);");
            if (this.grammar.debuggingOutput) {
                this.println("  ruleNames  = _ruleNames;");
                this.println("  semPredNames = _semPredNames;");
                this.println("  setupDebugging();");
            }
            this.println("caseSensitiveLiterals = " + grammar.caseSensitiveLiterals + ";");
            this.println("setCaseSensitive(" + grammar.caseSensitive + ");");
            this.println("literals = new Hashtable();");
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
                this.println("literals.put(new ANTLRHashString(" + stringLiteralSymbol.getId() + ", this), new Integer(" + stringLiteralSymbol.getTokenType() + "));");
            }
            --this.tabs;
            this.println("}");
            if (this.grammar.debuggingOutput) {
                this.println("private static final String _ruleNames[] = {");
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
                    this.genRule(ruleSymbol, false, n++);
                }
                this.exitIfError();
            }
            if (this.grammar.debuggingOutput) {
                this.genSemPredMap();
            }
            this.genBitsets(this.bitsetsUsed, ((LexerGrammar)this.grammar).charVocabulary.size());
            this.println("");
            this.println("}");
            this.getPrintWriterManager().finishOutput();
        }
        finally {
            this.defaultLine = defaultLine;
        }
    }
    
    public void gen(final OneOrMoreBlock obj) {
        final int defaultLine = this.defaultLine;
        try {
            this.defaultLine = obj.getLine();
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("gen+(" + obj + ")");
            }
            this.println("{", -999);
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
            this.println(str + ":");
            this.println("do {");
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
                if (this.DEBUG_CODE_GENERATOR) {
                    System.out.println("nongreedy (...)+ loop; exit depth is " + obj.exitLookaheadDepth);
                }
                final String lookaheadTestExpression = this.getLookaheadTestExpression(obj.exitCache, n);
                this.println("// nongreedy exit test", -999);
                this.println("if ( " + s + ">=1 && " + lookaheadTestExpression + ") break " + str + ";", -888);
            }
            this.genBlockFinish(this.genCommonBlock(obj, false), "if ( " + s + ">=1 ) { break " + str + "; } else {" + this.throwNoViable + "}", obj.getLine());
            this.println(s + "++;");
            --this.tabs;
            this.println("} while (true);");
            this.println("}");
            this.currentASTResult = currentASTResult;
        }
        finally {
            this.defaultLine = defaultLine;
        }
    }
    
    public void gen(final ParserGrammar grammar) throws IOException {
        final int defaultLine = this.defaultLine;
        try {
            this.defaultLine = -999;
            if (grammar.debuggingOutput) {
                this.semPreds = new Vector();
            }
            this.setGrammar(grammar);
            if (!(this.grammar instanceof ParserGrammar)) {
                this.antlrTool.panic("Internal error generating parser");
            }
            this.currentOutput = this.getPrintWriterManager().setupOutput(this.antlrTool, this.grammar);
            this.genAST = this.grammar.buildAST;
            this.tabs = 0;
            this.genHeader();
            try {
                this.defaultLine = this.behavior.getHeaderActionLine("");
                this.println(this.behavior.getHeaderAction(""));
            }
            finally {
                this.defaultLine = -999;
            }
            this.println("import antlr.TokenBuffer;");
            this.println("import antlr.TokenStreamException;");
            this.println("import antlr.TokenStreamIOException;");
            this.println("import antlr.ANTLRException;");
            this.println("import antlr." + this.grammar.getSuperClass() + ";");
            this.println("import antlr.Token;");
            this.println("import antlr.TokenStream;");
            this.println("import antlr.RecognitionException;");
            this.println("import antlr.NoViableAltException;");
            this.println("import antlr.MismatchedTokenException;");
            this.println("import antlr.SemanticException;");
            this.println("import antlr.ParserSharedInputState;");
            this.println("import antlr.collections.impl.BitSet;");
            if (this.genAST) {
                this.println("import antlr.collections.AST;");
                this.println("import java.util.Hashtable;");
                this.println("import antlr.ASTFactory;");
                this.println("import antlr.ASTPair;");
                this.println("import antlr.collections.impl.ASTArray;");
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
            String str2 = "public";
            final Token token = this.grammar.options.get("classHeaderPrefix");
            if (token != null) {
                final String stripFrontBack = StringUtils.stripFrontBack(token.getText(), "\"", "\"");
                if (stripFrontBack != null) {
                    str2 = stripFrontBack;
                }
            }
            this.print(str2 + " ");
            this.print("class " + this.grammar.getClassName() + " extends " + str);
            this.println("       implements " + this.grammar.tokenManager.getName() + JavaCodeGenerator.TokenTypesFileSuffix);
            final Token token2 = this.grammar.options.get("classHeaderSuffix");
            if (token2 != null) {
                final String stripFrontBack2 = StringUtils.stripFrontBack(token2.getText(), "\"", "\"");
                if (stripFrontBack2 != null) {
                    this.print(", " + stripFrontBack2);
                }
            }
            this.println(" {");
            if (this.grammar.debuggingOutput) {
                this.println("private static final String _ruleNames[] = {");
                final Enumeration elements = this.grammar.rules.elements();
                while (elements.hasMoreElements()) {
                    final GrammarSymbol grammarSymbol = elements.nextElement();
                    if (grammarSymbol instanceof RuleSymbol) {
                        this.println("  \"" + ((RuleSymbol)grammarSymbol).getId() + "\",");
                    }
                }
                this.println("};");
            }
            this.print(this.processActionForSpecialSymbols(this.grammar.classMemberAction.getText(), this.grammar.classMemberAction.getLine(), this.currentRule, null), this.grammar.classMemberAction.getLine());
            this.println("");
            this.println("protected " + this.grammar.getClassName() + "(TokenBuffer tokenBuf, int k) {");
            this.println("  super(tokenBuf,k);");
            this.println("  tokenNames = _tokenNames;");
            if (this.grammar.debuggingOutput) {
                this.println("  ruleNames  = _ruleNames;");
                this.println("  semPredNames = _semPredNames;");
                this.println("  setupDebugging(tokenBuf);");
            }
            if (this.grammar.buildAST) {
                this.println("  buildTokenTypeASTClassMap();");
                this.println("  astFactory = new ASTFactory(getTokenTypeToASTClassMap());");
            }
            this.println("}");
            this.println("");
            this.println("public " + this.grammar.getClassName() + "(TokenBuffer tokenBuf) {");
            this.println("  this(tokenBuf," + this.grammar.maxk + ");");
            this.println("}");
            this.println("");
            this.println("protected " + this.grammar.getClassName() + "(TokenStream lexer, int k) {");
            this.println("  super(lexer,k);");
            this.println("  tokenNames = _tokenNames;");
            if (this.grammar.debuggingOutput) {
                this.println("  ruleNames  = _ruleNames;");
                this.println("  semPredNames = _semPredNames;");
                this.println("  setupDebugging(lexer);");
            }
            if (this.grammar.buildAST) {
                this.println("  buildTokenTypeASTClassMap();");
                this.println("  astFactory = new ASTFactory(getTokenTypeToASTClassMap());");
            }
            this.println("}");
            this.println("");
            this.println("public " + this.grammar.getClassName() + "(TokenStream lexer) {");
            this.println("  this(lexer," + this.grammar.maxk + ");");
            this.println("}");
            this.println("");
            this.println("public " + this.grammar.getClassName() + "(ParserSharedInputState state) {");
            this.println("  super(state," + this.grammar.maxk + ");");
            this.println("  tokenNames = _tokenNames;");
            if (this.grammar.buildAST) {
                this.println("  buildTokenTypeASTClassMap();");
                this.println("  astFactory = new ASTFactory(getTokenTypeToASTClassMap());");
            }
            this.println("}");
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
            this.genTokenStrings();
            if (this.grammar.buildAST) {
                this.genTokenASTNodeMap();
            }
            this.genBitsets(this.bitsetsUsed, this.grammar.tokenManager.maxTokenType());
            if (this.grammar.debuggingOutput) {
                this.genSemPredMap();
            }
            this.println("");
            this.println("}");
            this.getPrintWriterManager().finishOutput();
        }
        finally {
            this.defaultLine = defaultLine;
        }
    }
    
    public void gen(final RuleRefElement obj) {
        final int defaultLine = this.defaultLine;
        try {
            this.defaultLine = obj.getLine();
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
                this.println("_saveIndex=text.length();");
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
                this.println("text.setLength(_saveIndex);");
            }
            if (this.syntacticPredLevel == 0) {
                final boolean b = this.grammar.hasSyntacticPredicate && ((this.grammar.buildAST && obj.getLabel() != null) || (this.genAST && obj.getAutoGenType() == 1));
                if (b) {}
                if (this.grammar.buildAST && obj.getLabel() != null) {
                    this.println(obj.getLabel() + "_AST = (" + this.labeledElementASTType + ")returnAST;");
                }
                if (this.genAST) {
                    switch (obj.getAutoGenType()) {
                        case 1: {
                            this.println("astFactory.addASTChild(currentAST, returnAST);");
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
                if (b) {}
            }
            this.genErrorCatchForElement(obj);
        }
        finally {
            this.defaultLine = defaultLine;
        }
    }
    
    public void gen(final StringLiteralElement obj) {
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genString(" + obj + ")");
        }
        if (obj.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(obj.getLabel() + " = " + this.lt1Value + ";", obj.getLine());
        }
        this.genElementAST(obj);
        final boolean saveText = this.saveText;
        this.saveText = (this.saveText && obj.getAutoGenType() == 1);
        this.genMatch(obj);
        this.saveText = saveText;
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _t.getNextSibling();", obj.getLine());
        }
    }
    
    public void gen(final TokenRangeElement tokenRangeElement) {
        this.genErrorTryForElement(tokenRangeElement);
        if (tokenRangeElement.getLabel() != null && this.syntacticPredLevel == 0) {
            this.println(tokenRangeElement.getLabel() + " = " + this.lt1Value + ";", tokenRangeElement.getLine());
        }
        this.genElementAST(tokenRangeElement);
        this.println("matchRange(" + tokenRangeElement.beginText + "," + tokenRangeElement.endText + ");", tokenRangeElement.getLine());
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
            this.println(obj.getLabel() + " = " + this.lt1Value + ";", obj.getLine());
        }
        this.genElementAST(obj);
        this.genMatch(obj);
        this.genErrorCatchForElement(obj);
        if (this.grammar instanceof TreeWalkerGrammar) {
            this.println("_t = _t.getNextSibling();", obj.getLine());
        }
    }
    
    public void gen(final TreeElement treeElement) {
        final int defaultLine = this.defaultLine;
        try {
            this.defaultLine = treeElement.getLine();
            this.println("AST __t" + treeElement.ID + " = _t;");
            if (treeElement.root.getLabel() != null) {
                this.println(treeElement.root.getLabel() + " = _t==ASTNULL ? null :(" + this.labeledElementASTType + ")_t;", treeElement.root.getLine());
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
                this.println("if ( _t==null ) throw new MismatchedTokenException();", treeElement.root.getLine());
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
        finally {
            this.defaultLine = defaultLine;
        }
    }
    
    public void gen(final TreeWalkerGrammar grammar) throws IOException {
        final int defaultLine = this.defaultLine;
        try {
            this.defaultLine = -999;
            this.setGrammar(grammar);
            if (!(this.grammar instanceof TreeWalkerGrammar)) {
                this.antlrTool.panic("Internal error generating tree-walker");
            }
            this.currentOutput = this.getPrintWriterManager().setupOutput(this.antlrTool, this.grammar);
            this.genAST = this.grammar.buildAST;
            this.tabs = 0;
            this.genHeader();
            try {
                this.defaultLine = this.behavior.getHeaderActionLine("");
                this.println(this.behavior.getHeaderAction(""));
            }
            finally {
                this.defaultLine = -999;
            }
            this.println("import antlr." + this.grammar.getSuperClass() + ";");
            this.println("import antlr.Token;");
            this.println("import antlr.collections.AST;");
            this.println("import antlr.RecognitionException;");
            this.println("import antlr.ANTLRException;");
            this.println("import antlr.NoViableAltException;");
            this.println("import antlr.MismatchedTokenException;");
            this.println("import antlr.SemanticException;");
            this.println("import antlr.collections.impl.BitSet;");
            this.println("import antlr.ASTPair;");
            this.println("import antlr.collections.impl.ASTArray;");
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
            String str2 = "public";
            final Token token = this.grammar.options.get("classHeaderPrefix");
            if (token != null) {
                final String stripFrontBack = StringUtils.stripFrontBack(token.getText(), "\"", "\"");
                if (stripFrontBack != null) {
                    str2 = stripFrontBack;
                }
            }
            this.print(str2 + " ");
            this.print("class " + this.grammar.getClassName() + " extends " + str);
            this.println("       implements " + this.grammar.tokenManager.getName() + JavaCodeGenerator.TokenTypesFileSuffix);
            final Token token2 = this.grammar.options.get("classHeaderSuffix");
            if (token2 != null) {
                final String stripFrontBack2 = StringUtils.stripFrontBack(token2.getText(), "\"", "\"");
                if (stripFrontBack2 != null) {
                    this.print(", " + stripFrontBack2);
                }
            }
            this.println(" {");
            this.print(this.processActionForSpecialSymbols(this.grammar.classMemberAction.getText(), this.grammar.classMemberAction.getLine(), this.currentRule, null), this.grammar.classMemberAction.getLine());
            this.println("public " + this.grammar.getClassName() + "() {");
            ++this.tabs;
            this.println("tokenNames = _tokenNames;");
            --this.tabs;
            this.println("}");
            this.println("");
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
            this.println("}");
            this.println("");
            this.getPrintWriterManager().finishOutput();
        }
        finally {
            this.defaultLine = defaultLine;
        }
    }
    
    public void gen(final WildcardElement wildcardElement) {
        final int defaultLine = this.defaultLine;
        try {
            this.defaultLine = wildcardElement.getLine();
            if (wildcardElement.getLabel() != null && this.syntacticPredLevel == 0) {
                this.println(wildcardElement.getLabel() + " = " + this.lt1Value + ";");
            }
            this.genElementAST(wildcardElement);
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println("if ( _t==null ) throw new MismatchedTokenException();");
            }
            else if (this.grammar instanceof LexerGrammar) {
                if (this.grammar instanceof LexerGrammar && (!this.saveText || wildcardElement.getAutoGenType() == 3)) {
                    this.println("_saveIndex=text.length();");
                }
                this.println("matchNot(EOF_CHAR);");
                if (this.grammar instanceof LexerGrammar && (!this.saveText || wildcardElement.getAutoGenType() == 3)) {
                    this.println("text.setLength(_saveIndex);");
                }
            }
            else {
                this.println("matchNot(" + this.getValueString(1) + ");");
            }
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println("_t = _t.getNextSibling();");
            }
        }
        finally {
            this.defaultLine = defaultLine;
        }
    }
    
    public void gen(final ZeroOrMoreBlock obj) {
        final int defaultLine = this.defaultLine;
        try {
            this.defaultLine = obj.getLine();
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("gen*(" + obj + ")");
            }
            this.println("{");
            this.genBlockPreamble(obj);
            String str;
            if (obj.getLabel() != null) {
                str = obj.getLabel();
            }
            else {
                str = "_loop" + obj.ID;
            }
            this.println(str + ":");
            this.println("do {");
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
                if (this.DEBUG_CODE_GENERATOR) {
                    System.out.println("nongreedy (...)* loop; exit depth is " + obj.exitLookaheadDepth);
                }
                final String lookaheadTestExpression = this.getLookaheadTestExpression(obj.exitCache, n);
                this.println("// nongreedy exit test");
                this.println("if (" + lookaheadTestExpression + ") break " + str + ";");
            }
            this.genBlockFinish(this.genCommonBlock(obj, false), "break " + str + ";", obj.getLine());
            --this.tabs;
            this.println("} while (true);");
            this.println("}");
            this.currentASTResult = currentASTResult;
        }
        finally {
            this.defaultLine = defaultLine;
        }
    }
    
    protected void genAlt(final Alternative alternative, final AlternativeBlock alternativeBlock) {
        final boolean genAST = this.genAST;
        this.genAST = (this.genAST && alternative.getAutoGen());
        final boolean saveText = this.saveText;
        this.saveText = (this.saveText && alternative.getAutoGen());
        final Hashtable treeVariableMap = this.treeVariableMap;
        this.treeVariableMap = new Hashtable();
        if (alternative.exceptionSpec != null) {
            this.println("try {      // for error handling", alternative.head.getLine());
            ++this.tabs;
        }
        for (AlternativeElement alternativeElement = alternative.head; !(alternativeElement instanceof BlockEndElement); alternativeElement = alternativeElement.next) {
            alternativeElement.generate();
        }
        if (this.genAST) {
            if (alternativeBlock instanceof RuleBlock) {
                final RuleBlock ruleBlock = (RuleBlock)alternativeBlock;
                if (this.grammar.hasSyntacticPredicate) {}
                this.println(ruleBlock.getRuleName() + "_AST = (" + this.labeledElementASTType + ")currentAST.root;", -888);
                if (this.grammar.hasSyntacticPredicate) {}
            }
            else if (alternativeBlock.getLabel() != null) {
                this.antlrTool.warning("Labeled subrules not yet supported", this.grammar.getFilename(), alternativeBlock.getLine(), alternativeBlock.getColumn());
            }
        }
        if (alternative.exceptionSpec != null) {
            --this.tabs;
            this.println("}", -999);
            this.genErrorHandler(alternative.exceptionSpec);
        }
        this.genAST = genAST;
        this.saveText = saveText;
        this.treeVariableMap = treeVariableMap;
    }
    
    protected void genBitsets(final Vector vector, final int n) {
        this.println("", -999);
        for (int i = 0; i < vector.size(); ++i) {
            final BitSet set = (BitSet)vector.elementAt(i);
            set.growToInclude(n);
            this.genBitSet(set, i);
        }
    }
    
    private void genBitSet(final BitSet set, final int n) {
        final int defaultLine = this.defaultLine;
        try {
            this.defaultLine = -999;
            this.println("private static final long[] mk" + this.getBitsetName(n) + "() {");
            final int lengthInLongWords = set.lengthInLongWords();
            if (lengthInLongWords < 8) {
                this.println("\tlong[] data = { " + set.toStringOfWords() + "};");
            }
            else {
                this.println("\tlong[] data = new long[" + lengthInLongWords + "];");
                final long[] packedArray = set.toPackedArray();
                int i = 0;
                while (i < packedArray.length) {
                    if (packedArray[i] == 0L) {
                        ++i;
                    }
                    else if (i + 1 == packedArray.length || packedArray[i] != packedArray[i + 1]) {
                        this.println("\tdata[" + i + "]=" + packedArray[i] + "L;");
                        ++i;
                    }
                    else {
                        int n2;
                        for (n2 = i + 1; n2 < packedArray.length && packedArray[n2] == packedArray[i]; ++n2) {}
                        this.println("\tfor (int i = " + i + "; i<=" + (n2 - 1) + "; i++) { data[i]=" + packedArray[i] + "L; }");
                        i = n2;
                    }
                }
            }
            this.println("\treturn data;");
            this.println("}");
            this.println("public static final BitSet " + this.getBitsetName(n) + " = new BitSet(" + "mk" + this.getBitsetName(n) + "()" + ");");
        }
        finally {
            this.defaultLine = defaultLine;
        }
    }
    
    private void genBlockFinish(final JavaBlockFinishingInfo javaBlockFinishingInfo, final String s, final int defaultLine) {
        final int defaultLine2 = this.defaultLine;
        try {
            this.defaultLine = defaultLine;
            if (javaBlockFinishingInfo.needAnErrorClause && (javaBlockFinishingInfo.generatedAnIf || javaBlockFinishingInfo.generatedSwitch)) {
                if (javaBlockFinishingInfo.generatedAnIf) {
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
            if (javaBlockFinishingInfo.postscript != null) {
                this.println(javaBlockFinishingInfo.postscript);
            }
        }
        finally {
            this.defaultLine = defaultLine2;
        }
    }
    
    protected void genBlockInitAction(final AlternativeBlock alternativeBlock) {
        if (alternativeBlock.initAction != null) {
            this.printAction(this.processActionForSpecialSymbols(alternativeBlock.initAction, alternativeBlock.getLine(), this.currentRule, null), alternativeBlock.getLine());
        }
    }
    
    protected void genBlockPreamble(final AlternativeBlock alternativeBlock) {
        if (alternativeBlock instanceof RuleBlock) {
            final RuleBlock ruleBlock = (RuleBlock)alternativeBlock;
            if (ruleBlock.labeledElements != null) {
                for (int i = 0; i < ruleBlock.labeledElements.size(); ++i) {
                    final AlternativeElement alternativeElement = (AlternativeElement)ruleBlock.labeledElements.elementAt(i);
                    final int defaultLine = this.defaultLine;
                    try {
                        this.defaultLine = alternativeElement.getLine();
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
                                    this.println("Token " + alternativeElement.getLabel() + "=null;");
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
                    finally {
                        this.defaultLine = defaultLine;
                    }
                }
            }
        }
    }
    
    protected void genCases(final BitSet obj, final int defaultLine) {
        final int defaultLine2 = this.defaultLine;
        try {
            this.defaultLine = defaultLine;
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
        finally {
            this.defaultLine = defaultLine2;
        }
    }
    
    public JavaBlockFinishingInfo genCommonBlock(final AlternativeBlock obj, final boolean b) {
        final int defaultLine = this.defaultLine;
        try {
            this.defaultLine = obj.getLine();
            int n = 0;
            boolean b2 = false;
            int n2 = 0;
            final JavaBlockFinishingInfo javaBlockFinishingInfo = new JavaBlockFinishingInfo();
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
                    str = "_t,";
                }
                this.println("match(" + str + this.getBitsetName(this.markBitsetForGen(look.fset)) + ");");
                if (this.grammar instanceof TreeWalkerGrammar) {
                    this.println("_t = _t.getNextSibling();");
                }
                return javaBlockFinishingInfo;
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
                    return javaBlockFinishingInfo;
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
                    this.println("if (_t==null) _t=ASTNULL;");
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
                            this.genCases(lookahead.fset, alternative2.head.getLine());
                            this.println("{", alternative2.head.getLine());
                            ++this.tabs;
                            this.genAlt(alternative2, obj);
                            this.println("break;", -999);
                            --this.tabs;
                            this.println("}", -999);
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
                        final int defaultLine2 = this.defaultLine;
                        try {
                            this.defaultLine = alternative3.head.getLine();
                            if (alternative3.cache[1].fset.degree() > 127 && suitableForCaseExpression(alternative3)) {
                                if (n == 0) {
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
                                javaBlockFinishingInfo.needAnErrorClause = false;
                            }
                            else {
                                if (alternative3.semPred != null) {
                                    final String processActionForSpecialSymbols = this.processActionForSpecialSymbols(alternative3.semPred, obj.line, this.currentRule, new ActionTransInfo());
                                    if ((this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar) && this.grammar.debuggingOutput) {
                                        s = "(" + s + "&& fireSemanticPredicateEvaluated(antlr.debug.SemanticPredicateEvent.PREDICTING," + this.addSemPred(this.charFormatter.escapeString(processActionForSpecialSymbols)) + "," + processActionForSpecialSymbols + "))";
                                    }
                                    else {
                                        s = "(" + s + "&&(" + processActionForSpecialSymbols + "))";
                                    }
                                }
                                if (n > 0) {
                                    if (alternative3.synPred != null) {
                                        this.println("else {", alternative3.synPred.getLine());
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
                                        this.println("if (_t==null) _t=ASTNULL;");
                                    }
                                    this.println("if " + s + " {");
                                }
                            }
                        }
                        finally {
                            this.defaultLine = defaultLine2;
                        }
                        ++n;
                        ++this.tabs;
                        this.genAlt(alternative3, obj);
                        --this.tabs;
                        this.println("}");
                    }
                }
            }
            String string = "";
            for (int n4 = 1; n4 <= n2; ++n4) {
                string += "}";
            }
            this.genAST = genAST;
            this.saveText = saveText;
            if (b2) {
                --this.tabs;
                javaBlockFinishingInfo.postscript = string + "}";
                javaBlockFinishingInfo.generatedSwitch = true;
                javaBlockFinishingInfo.generatedAnIf = (n > 0);
            }
            else {
                javaBlockFinishingInfo.postscript = string;
                javaBlockFinishingInfo.generatedSwitch = false;
                javaBlockFinishingInfo.generatedAnIf = (n > 0);
            }
            return javaBlockFinishingInfo;
        }
        finally {
            this.defaultLine = defaultLine;
        }
    }
    
    private static boolean suitableForCaseExpression(final Alternative alternative) {
        return alternative.lookaheadDepth == 1 && alternative.semPred == null && !alternative.cache[1].containsEpsilon() && alternative.cache[1].fset.degree() <= 127;
    }
    
    private void genElementAST(final AlternativeElement alternativeElement) {
        final int defaultLine = this.defaultLine;
        try {
            this.defaultLine = alternativeElement.getLine();
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
                            this.println("astFactory.addASTChild(currentAST, " + string2 + ");");
                            break;
                        }
                        case 2: {
                            this.println("astFactory.makeASTRoot(currentAST, " + string2 + ");");
                            break;
                        }
                    }
                }
                if (b2) {}
            }
        }
        finally {
            this.defaultLine = defaultLine;
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
            this.println("}", alternativeElement.getLine());
            this.genErrorHandler(exceptionSpec);
        }
    }
    
    private void genErrorHandler(final ExceptionSpec exceptionSpec) {
        for (int i = 0; i < exceptionSpec.handlers.size(); ++i) {
            final ExceptionHandler exceptionHandler = (ExceptionHandler)exceptionSpec.handlers.elementAt(i);
            final int defaultLine = this.defaultLine;
            try {
                this.defaultLine = exceptionHandler.action.getLine();
                this.println("catch (" + exceptionHandler.exceptionTypeAndName.getText() + ") {", exceptionHandler.exceptionTypeAndName.getLine());
                ++this.tabs;
                if (this.grammar.hasSyntacticPredicate) {
                    this.println("if (inputState.guessing==0) {");
                    ++this.tabs;
                }
                this.printAction(this.processActionForSpecialSymbols(exceptionHandler.action.getText(), exceptionHandler.action.getLine(), this.currentRule, new ActionTransInfo()));
                if (this.grammar.hasSyntacticPredicate) {
                    --this.tabs;
                    this.println("} else {");
                    ++this.tabs;
                    this.println("throw " + this.extractIdOfAction(exceptionHandler.exceptionTypeAndName) + ";");
                    --this.tabs;
                    this.println("}");
                }
                --this.tabs;
                this.println("}");
            }
            finally {
                this.defaultLine = defaultLine;
            }
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
            this.println("try { // for error handling", alternativeElement.getLine());
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
        this.println("// $ANTLR " + Tool.version + ": " + "\"" + this.antlrTool.fileMinusPath(this.antlrTool.grammarFile) + "\"" + " -> " + "\"" + this.grammar.getClassName() + ".java\"$", -999);
    }
    
    private void genLiteralsTest() {
        this.println("_ttype = testLiteralsTable(_ttype);");
    }
    
    private void genLiteralsTestForPartialToken() {
        this.println("_ttype = testLiteralsTable(new String(text.getBuffer(),_begin,text.length()-_begin),_ttype);");
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
        final int defaultLine = this.defaultLine;
        try {
            this.defaultLine = grammarAtom.getLine();
            String s = "";
            if (this.grammar instanceof TreeWalkerGrammar) {
                s = "_t,";
            }
            if (this.grammar instanceof LexerGrammar && (!this.saveText || grammarAtom.getAutoGenType() == 3)) {
                this.println("_saveIndex=text.length();");
            }
            this.print(grammarAtom.not ? "matchNot(" : "match(");
            this._print(s, -999);
            if (grammarAtom.atomText.equals("EOF")) {
                this._print("Token.EOF_TYPE");
            }
            else {
                this._print(grammarAtom.atomText);
            }
            this._println(");");
            if (this.grammar instanceof LexerGrammar && (!this.saveText || grammarAtom.getAutoGenType() == 3)) {
                this.println("text.setLength(_saveIndex);");
            }
        }
        finally {
            this.defaultLine = defaultLine;
        }
    }
    
    protected void genMatchUsingAtomTokenType(final GrammarAtom grammarAtom) {
        String str = "";
        if (this.grammar instanceof TreeWalkerGrammar) {
            str = "_t,";
        }
        this.println((grammarAtom.not ? "matchNot(" : "match(") + (str + this.getValueString(grammarAtom.getType())) + ");", grammarAtom.getLine());
    }
    
    public void genNextToken() {
        final int defaultLine = this.defaultLine;
        try {
            this.defaultLine = -999;
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
                this.println("public Token nextToken() throws TokenStreamException {");
                this.println("\ttry {uponEOF();}");
                this.println("\tcatch(CharStreamIOException csioe) {");
                this.println("\t\tthrow new TokenStreamIOException(csioe.io);");
                this.println("\t}");
                this.println("\tcatch(CharStreamException cse) {");
                this.println("\t\tthrow new TokenStreamException(cse.getMessage());");
                this.println("\t}");
                this.println("\treturn new CommonToken(Token.EOF_TYPE, \"\");");
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
            this.println("public Token nextToken() throws TokenStreamException {");
            ++this.tabs;
            this.println("Token theRetToken=null;");
            this._println("tryAgain:");
            this.println("for (;;) {");
            ++this.tabs;
            this.println("Token _token = null;");
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
            this.println("try {   // for char stream error handling");
            ++this.tabs;
            this.println("try {   // for lexical error handling");
            ++this.tabs;
            for (int j = 0; j < nextTokenRule.getAlternatives().size(); ++j) {
                final Alternative alternative = nextTokenRule.getAlternativeAt(j);
                if (alternative.cache[1].containsEpsilon()) {
                    this.antlrTool.warning("public lexical rule " + CodeGenerator.decodeLexerRuleName(((RuleRefElement)alternative.head).targetRule) + " is optional (can match \"nothing\")");
                }
            }
            final String property = System.getProperty("line.separator");
            final JavaBlockFinishingInfo genCommonBlock = this.genCommonBlock(nextTokenRule, false);
            final String string = "if (LA(1)==EOF_CHAR) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}" + property + "\t\t\t\t";
            String s;
            if (((LexerGrammar)this.grammar).filterMode) {
                if (filterRule == null) {
                    s = string + "else {consume(); continue tryAgain;}";
                }
                else {
                    s = string + "else {" + property + "\t\t\t\t\tcommit();" + property + "\t\t\t\t\ttry {m" + filterRule + "(false);}" + property + "\t\t\t\t\tcatch(RecognitionException e) {" + property + "\t\t\t\t\t\t// catastrophic failure" + property + "\t\t\t\t\t\treportError(e);" + property + "\t\t\t\t\t\tconsume();" + property + "\t\t\t\t\t}" + property + "\t\t\t\t\tcontinue tryAgain;" + property + "\t\t\t\t}";
                }
            }
            else {
                s = string + "else {" + this.throwNoViable + "}";
            }
            this.genBlockFinish(genCommonBlock, s, nextTokenRule.getLine());
            if (((LexerGrammar)this.grammar).filterMode && filterRule != null) {
                this.println("commit();");
            }
            this.println("if ( _returnToken==null ) continue tryAgain; // found SKIP token");
            this.println("_ttype = _returnToken.getType();");
            if (((LexerGrammar)this.grammar).getTestLiterals()) {
                this.genLiteralsTest();
            }
            this.println("_returnToken.setType(_ttype);");
            this.println("return _returnToken;");
            --this.tabs;
            this.println("}");
            this.println("catch (RecognitionException e) {");
            ++this.tabs;
            if (((LexerGrammar)this.grammar).filterMode) {
                if (filterRule == null) {
                    this.println("if ( !getCommitToPath() ) {consume(); continue tryAgain;}");
                }
                else {
                    this.println("if ( !getCommitToPath() ) {");
                    ++this.tabs;
                    this.println("rewind(_m);");
                    this.println("resetText();");
                    this.println("try {m" + filterRule + "(false);}");
                    this.println("catch(RecognitionException ee) {");
                    this.println("\t// horrendous failure: error in filter rule");
                    this.println("\treportError(ee);");
                    this.println("\tconsume();");
                    this.println("}");
                    this.println("continue tryAgain;");
                    --this.tabs;
                    this.println("}");
                }
            }
            if (nextTokenRule.getDefaultErrorHandler()) {
                this.println("reportError(e);");
                this.println("consume();");
            }
            else {
                this.println("throw new TokenStreamRecognitionException(e);");
            }
            --this.tabs;
            this.println("}");
            --this.tabs;
            this.println("}");
            this.println("catch (CharStreamException cse) {");
            this.println("\tif ( cse instanceof CharStreamIOException ) {");
            this.println("\t\tthrow new TokenStreamIOException(((CharStreamIOException)cse).io);");
            this.println("\t}");
            this.println("\telse {");
            this.println("\t\tthrow new TokenStreamException(cse.getMessage());");
            this.println("\t}");
            this.println("}");
            --this.tabs;
            this.println("}");
            --this.tabs;
            this.println("}");
            this.println("");
        }
        finally {
            this.defaultLine = defaultLine;
        }
    }
    
    public void genRule(final RuleSymbol ruleSymbol, final boolean b, final int n) {
        this.tabs = 1;
        if (this.DEBUG_CODE_GENERATOR) {
            System.out.println("genRule(" + ruleSymbol.getId() + ")");
        }
        if (!ruleSymbol.isDefined()) {
            this.antlrTool.error("undefined rule: " + ruleSymbol.getId());
            return;
        }
        final RuleBlock block = ruleSymbol.getBlock();
        final int defaultLine = this.defaultLine;
        try {
            this.defaultLine = block.getLine();
            this.currentRule = block;
            this.currentASTResult = ruleSymbol.getId();
            this.declaredASTVariables.clear();
            final boolean genAST = this.genAST;
            this.genAST = (this.genAST && block.getAutoGen());
            this.saveText = block.getAutoGen();
            if (ruleSymbol.comment != null) {
                this._println(ruleSymbol.comment);
            }
            this.print(ruleSymbol.access + " final ");
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
            this._print(" throws " + this.exceptionThrown);
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
            this._println(" {");
            ++this.tabs;
            if (block.returnAction != null) {
                this.println(block.returnAction + ";");
            }
            this.println(this.commonLocalVars);
            if (this.grammar.traceRules) {
                if (this.grammar instanceof TreeWalkerGrammar) {
                    this.println("traceIn(\"" + ruleSymbol.getId() + "\",_t);");
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
                this.println("int _saveIndex;");
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
                this.println(this.labeledElementASTType + " " + ruleSymbol.getId() + "_AST_in = (_t == ASTNULL) ? null : (" + this.labeledElementASTType + ")_t;", -999);
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
                this.genBlockFinish(this.genCommonBlock(block, false), this.throwNoViable, block.getLine());
            }
            if (exceptionSpec != null || block.getDefaultErrorHandler()) {
                --this.tabs;
                this.println("}");
            }
            if (exceptionSpec != null) {
                this.genErrorHandler(exceptionSpec);
            }
            else if (block.getDefaultErrorHandler()) {
                this.println("catch (" + this.exceptionThrown + " ex) {");
                ++this.tabs;
                if (this.grammar.hasSyntacticPredicate) {
                    this.println("if (inputState.guessing==0) {");
                    ++this.tabs;
                }
                this.println("reportError(ex);");
                if (!(this.grammar instanceof TreeWalkerGrammar)) {
                    this.println("recover(ex," + this.getBitsetName(this.markBitsetForGen(this.grammar.theLLkAnalyzer.FOLLOW(1, block.endNode).fset)) + ");");
                }
                else {
                    this.println("if (_t!=null) {_t = _t.getNextSibling();}");
                }
                if (this.grammar.hasSyntacticPredicate) {
                    --this.tabs;
                    this.println("} else {");
                    this.println("  throw ex;");
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
                this.println("if ( _createToken && _token==null && _ttype!=Token.SKIP ) {");
                this.println("\t_token = makeToken(_ttype);");
                this.println("\t_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));");
                this.println("}");
                this.println("_returnToken = _token;");
            }
            if (block.returnAction != null) {
                this.println("return " + this.extractIdOfAction(block.returnAction, block.getLine(), block.getColumn()) + ";");
            }
            if (this.grammar.debuggingOutput || this.grammar.traceRules) {
                --this.tabs;
                this.println("} finally { // debugging");
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
        finally {
            this.defaultLine = defaultLine;
        }
    }
    
    private void GenRuleInvocation(final RuleRefElement ruleRefElement) {
        final int defaultLine = this.defaultLine;
        try {
            this.defaultLine = ruleRefElement.getLine();
            this.getPrintWriterManager().startSingleSourceLineMapping(ruleRefElement.getLine());
            this._print(ruleRefElement.targetRule + "(");
            this.getPrintWriterManager().endMapping();
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
                this.println("_t = _retTree;");
            }
        }
        finally {
            this.defaultLine = defaultLine;
        }
    }
    
    protected void genSemPred(String s, final int n) {
        s = this.processActionForSpecialSymbols(s, n, this.currentRule, new ActionTransInfo());
        final String escapeString = this.charFormatter.escapeString(s);
        if (this.grammar.debuggingOutput && (this.grammar instanceof ParserGrammar || this.grammar instanceof LexerGrammar)) {
            s = "fireSemanticPredicateEvaluated(antlr.debug.SemanticPredicateEvent.VALIDATING," + this.addSemPred(escapeString) + "," + s + ")";
        }
        this.println("if (!(" + s + "))", n);
        this.println("  throw new SemanticException(\"" + escapeString + "\");", n);
    }
    
    protected void genSemPredMap() {
        final Enumeration elements = this.semPreds.elements();
        this.println("private String _semPredNames[] = {", -999);
        while (elements.hasMoreElements()) {
            this.println("\"" + elements.nextElement() + "\",", -999);
        }
        this.println("};", -999);
    }
    
    protected void genSynPred(final SynPredBlock obj, final String str) {
        final int defaultLine = this.defaultLine;
        try {
            this.defaultLine = obj.getLine();
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("gen=>(" + obj + ")");
            }
            this.println("boolean synPredMatched" + obj.ID + " = false;");
            if (this.grammar instanceof TreeWalkerGrammar) {
                this.println("if (_t==null) _t=ASTNULL;");
            }
            this.println("if (" + str + ") {");
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
            this.println("catch (" + this.exceptionThrown + " pe) {");
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
            this._println("inputState.guessing--;");
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
        finally {
            this.defaultLine = defaultLine;
        }
    }
    
    public void genTokenStrings() {
        final int defaultLine = this.defaultLine;
        try {
            this.defaultLine = -999;
            this.println("");
            this.println("public static final String[] _tokenNames = {");
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
                    this._print(",");
                }
                this._println("");
            }
            --this.tabs;
            this.println("};");
        }
        finally {
            this.defaultLine = defaultLine;
        }
    }
    
    protected void genTokenASTNodeMap() {
        final int defaultLine = this.defaultLine;
        try {
            this.defaultLine = -999;
            this.println("");
            this.println("protected void buildTokenTypeASTClassMap() {");
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
                            this.println("tokenTypeToASTClassMap = new Hashtable();");
                            n = 1;
                        }
                        this.println("tokenTypeToASTClassMap.put(new Integer(" + tokenSymbol.getTokenType() + "), " + tokenSymbol.getASTNodeType() + ".class);");
                    }
                }
            }
            if (n2 == 0) {
                this.println("tokenTypeToASTClassMap=null;");
            }
            --this.tabs;
            this.println("};");
        }
        finally {
            this.defaultLine = defaultLine;
        }
    }
    
    protected void genTokenTypes(final TokenManager tokenManager) throws IOException {
        final int defaultLine = this.defaultLine;
        try {
            this.defaultLine = -999;
            this.currentOutput = this.getPrintWriterManager().setupOutput(this.antlrTool, tokenManager.getName() + JavaCodeGenerator.TokenTypesFileSuffix);
            this.tabs = 0;
            this.genHeader();
            try {
                this.defaultLine = this.behavior.getHeaderActionLine("");
                this.println(this.behavior.getHeaderAction(""));
            }
            finally {
                this.defaultLine = -999;
            }
            this.println("public interface " + tokenManager.getName() + JavaCodeGenerator.TokenTypesFileSuffix + " {");
            ++this.tabs;
            final Vector vocabulary = tokenManager.getVocabulary();
            this.println("int EOF = 1;");
            this.println("int NULL_TREE_LOOKAHEAD = 3;");
            for (int i = 4; i < vocabulary.size(); ++i) {
                final String str = (String)vocabulary.elementAt(i);
                if (str != null) {
                    if (str.startsWith("\"")) {
                        final StringLiteralSymbol stringLiteralSymbol = (StringLiteralSymbol)tokenManager.getTokenSymbol(str);
                        if (stringLiteralSymbol == null) {
                            this.antlrTool.panic("String literal " + str + " not in symbol table");
                        }
                        else if (stringLiteralSymbol.label != null) {
                            this.println("int " + stringLiteralSymbol.label + " = " + i + ";");
                        }
                        else {
                            final String mangleLiteral = this.mangleLiteral(str);
                            if (mangleLiteral != null) {
                                this.println("int " + mangleLiteral + " = " + i + ";");
                                stringLiteralSymbol.label = mangleLiteral;
                            }
                            else {
                                this.println("// " + str + " = " + i);
                            }
                        }
                    }
                    else if (!str.startsWith("<")) {
                        this.println("int " + str + " = " + i + ";");
                    }
                }
            }
            --this.tabs;
            this.println("}");
            this.getPrintWriterManager().finishOutput();
            this.exitIfError();
        }
        finally {
            this.defaultLine = defaultLine;
        }
    }
    
    public String getASTCreateString(final Vector vector) {
        if (vector.size() == 0) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        sb.append("(" + this.labeledElementASTType + ")astFactory.make( (new ASTArray(" + vector.size() + "))");
        for (int i = 0; i < vector.size(); ++i) {
            sb.append(".add(" + vector.elementAt(i) + ")");
        }
        sb.append(")");
        return sb.toString();
    }
    
    public String getASTCreateString(final GrammarAtom grammarAtom, final String str) {
        if (grammarAtom != null && grammarAtom.getASTNodeType() != null) {
            return "(" + grammarAtom.getASTNodeType() + ")" + "astFactory.create(" + str + ",\"" + grammarAtom.getASTNodeType() + "\")";
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
            return "(" + this.labeledElementASTType + ")astFactory.create(" + s + ")";
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
                str = ",\"\"";
            }
            if (astNodeType != null) {
                return "(" + astNodeType + ")" + "astFactory.create(" + s + str + ",\"" + astNodeType + "\")";
            }
        }
        if (this.labeledElementASTType.equals("AST")) {
            return "astFactory.create(" + s + ")";
        }
        return "(" + this.labeledElementASTType + ")" + "astFactory.create(" + s + ")";
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
            return "_t.getType()";
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
            if (str == JavaCodeGenerator.NONUNIQUE) {
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
                this.treeVariableMap.put(s, JavaCodeGenerator.NONUNIQUE);
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
        if (grammar instanceof ParserGrammar) {
            this.labeledElementASTType = "AST";
            if (grammar.hasOption("ASTLabelType")) {
                final Token option = grammar.getOption("ASTLabelType");
                if (option != null) {
                    final String stripFrontBack = StringUtils.stripFrontBack(option.getText(), "\"", "\"");
                    if (stripFrontBack != null) {
                        this.labeledElementASTType = stripFrontBack;
                    }
                }
            }
            this.labeledElementType = "Token ";
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
            this.commonExtraParams = "boolean _createToken";
            this.commonLocalVars = "int _ttype; Token _token=null; int _begin=text.length();";
            this.lt1Value = "LA(1)";
            this.exceptionThrown = "RecognitionException";
            this.throwNoViable = "throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());";
        }
        else if (grammar instanceof TreeWalkerGrammar) {
            this.labeledElementASTType = "AST";
            this.labeledElementType = "AST";
            if (grammar.hasOption("ASTLabelType")) {
                final Token option2 = grammar.getOption("ASTLabelType");
                if (option2 != null) {
                    final String stripFrontBack2 = StringUtils.stripFrontBack(option2.getText(), "\"", "\"");
                    if (stripFrontBack2 != null) {
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
            this.lt1Value = "(" + this.labeledElementASTType + ")_t";
            this.exceptionThrown = "RecognitionException";
            this.throwNoViable = "throw new NoViableAltException(_t);";
        }
        else {
            this.antlrTool.panic("Unknown grammar type");
        }
    }
    
    public JavaCodeGeneratorPrintWriterManager getPrintWriterManager() {
        if (this.printWriterManager == null) {
            this.printWriterManager = new DefaultJavaCodeGeneratorPrintWriterManager();
        }
        return this.printWriterManager;
    }
    
    public void setPrintWriterManager(final JavaCodeGeneratorPrintWriterManager printWriterManager) {
        this.printWriterManager = printWriterManager;
    }
    
    public void setTool(final Tool tool) {
        super.setTool(tool);
    }
    
    static {
        NONUNIQUE = new String();
    }
}
