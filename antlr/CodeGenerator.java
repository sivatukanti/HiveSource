// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.BitSet;
import java.io.IOException;
import antlr.collections.impl.Vector;
import java.io.PrintWriter;

public abstract class CodeGenerator
{
    protected Tool antlrTool;
    protected int tabs;
    protected transient PrintWriter currentOutput;
    protected Grammar grammar;
    protected Vector bitsetsUsed;
    protected DefineGrammarSymbols behavior;
    protected LLkGrammarAnalyzer analyzer;
    protected CharFormatter charFormatter;
    protected boolean DEBUG_CODE_GENERATOR;
    protected static final int DEFAULT_MAKE_SWITCH_THRESHOLD = 2;
    protected static final int DEFAULT_BITSET_TEST_THRESHOLD = 4;
    protected static final int BITSET_OPTIMIZE_INIT_THRESHOLD = 8;
    protected int makeSwitchThreshold;
    protected int bitsetTestThreshold;
    private static boolean OLD_ACTION_TRANSLATOR;
    public static String TokenTypesFileSuffix;
    public static String TokenTypesFileExt;
    
    public CodeGenerator() {
        this.tabs = 0;
        this.grammar = null;
        this.DEBUG_CODE_GENERATOR = false;
        this.makeSwitchThreshold = 2;
        this.bitsetTestThreshold = 4;
    }
    
    protected void _print(final String s) {
        if (s != null) {
            this.currentOutput.print(s);
        }
    }
    
    protected void _printAction(final String s) {
        if (s == null) {
            return;
        }
        int index;
        for (index = 0; index < s.length() && Character.isSpaceChar(s.charAt(index)); ++index) {}
        int index2;
        for (index2 = s.length() - 1; index2 > index && Character.isSpaceChar(s.charAt(index2)); --index2) {}
        int i = index;
        while (i <= index2) {
            final char char1 = s.charAt(i);
            ++i;
            boolean b = false;
            switch (char1) {
                case 10: {
                    b = true;
                    break;
                }
                case 13: {
                    if (i <= index2 && s.charAt(i) == '\n') {
                        ++i;
                    }
                    b = true;
                    break;
                }
                default: {
                    this.currentOutput.print(char1);
                    break;
                }
            }
            if (b) {
                this.currentOutput.println();
                this.printTabs();
                while (i <= index2 && Character.isSpaceChar(s.charAt(i))) {
                    ++i;
                }
            }
        }
        this.currentOutput.println();
    }
    
    protected void _println(final String x) {
        if (x != null) {
            this.currentOutput.println(x);
        }
    }
    
    public static boolean elementsAreRange(final int[] array) {
        if (array.length == 0) {
            return false;
        }
        final int n = array[0];
        final int n2 = array[array.length - 1];
        if (array.length <= 2) {
            return false;
        }
        if (n2 - n + 1 > array.length) {
            return false;
        }
        int n3 = n + 1;
        for (int i = 1; i < array.length - 1; ++i) {
            if (n3 != array[i]) {
                return false;
            }
            ++n3;
        }
        return true;
    }
    
    protected String extractIdOfAction(final Token token) {
        return this.extractIdOfAction(token.getText(), token.getLine(), token.getColumn());
    }
    
    protected String extractIdOfAction(String removeAssignmentFromDeclaration, final int n, final int n2) {
        removeAssignmentFromDeclaration = this.removeAssignmentFromDeclaration(removeAssignmentFromDeclaration);
        for (int i = removeAssignmentFromDeclaration.length() - 2; i >= 0; --i) {
            if (!Character.isLetterOrDigit(removeAssignmentFromDeclaration.charAt(i)) && removeAssignmentFromDeclaration.charAt(i) != '_') {
                return removeAssignmentFromDeclaration.substring(i + 1);
            }
        }
        this.antlrTool.warning("Ill-formed action", this.grammar.getFilename(), n, n2);
        return "";
    }
    
    protected String extractTypeOfAction(final Token token) {
        return this.extractTypeOfAction(token.getText(), token.getLine(), token.getColumn());
    }
    
    protected String extractTypeOfAction(String removeAssignmentFromDeclaration, final int n, final int n2) {
        removeAssignmentFromDeclaration = this.removeAssignmentFromDeclaration(removeAssignmentFromDeclaration);
        for (int i = removeAssignmentFromDeclaration.length() - 2; i >= 0; --i) {
            if (!Character.isLetterOrDigit(removeAssignmentFromDeclaration.charAt(i)) && removeAssignmentFromDeclaration.charAt(i) != '_') {
                return removeAssignmentFromDeclaration.substring(0, i + 1);
            }
        }
        this.antlrTool.warning("Ill-formed action", this.grammar.getFilename(), n, n2);
        return "";
    }
    
    public abstract void gen();
    
    public abstract void gen(final ActionElement p0);
    
    public abstract void gen(final AlternativeBlock p0);
    
    public abstract void gen(final BlockEndElement p0);
    
    public abstract void gen(final CharLiteralElement p0);
    
    public abstract void gen(final CharRangeElement p0);
    
    public abstract void gen(final LexerGrammar p0) throws IOException;
    
    public abstract void gen(final OneOrMoreBlock p0);
    
    public abstract void gen(final ParserGrammar p0) throws IOException;
    
    public abstract void gen(final RuleRefElement p0);
    
    public abstract void gen(final StringLiteralElement p0);
    
    public abstract void gen(final TokenRangeElement p0);
    
    public abstract void gen(final TokenRefElement p0);
    
    public abstract void gen(final TreeElement p0);
    
    public abstract void gen(final TreeWalkerGrammar p0) throws IOException;
    
    public abstract void gen(final WildcardElement p0);
    
    public abstract void gen(final ZeroOrMoreBlock p0);
    
    protected void genTokenInterchange(final TokenManager tokenManager) throws IOException {
        final String string = tokenManager.getName() + CodeGenerator.TokenTypesFileSuffix + CodeGenerator.TokenTypesFileExt;
        this.currentOutput = this.antlrTool.openOutputFile(string);
        final StringBuffer append = new StringBuffer().append("// $ANTLR ");
        final Tool antlrTool = this.antlrTool;
        this.println(append.append(Tool.version).append(": ").append(this.antlrTool.fileMinusPath(this.antlrTool.grammarFile)).append(" -> ").append(string).append("$").toString());
        this.tabs = 0;
        this.println(tokenManager.getName() + "    // output token vocab name");
        final Vector vocabulary = tokenManager.getVocabulary();
        for (int i = 4; i < vocabulary.size(); ++i) {
            final String str = (String)vocabulary.elementAt(i);
            if (this.DEBUG_CODE_GENERATOR) {
                System.out.println("gen persistence file entry for: " + str);
            }
            if (str != null && !str.startsWith("<")) {
                if (str.startsWith("\"")) {
                    final StringLiteralSymbol stringLiteralSymbol = (StringLiteralSymbol)tokenManager.getTokenSymbol(str);
                    if (stringLiteralSymbol != null && stringLiteralSymbol.label != null) {
                        this.print(stringLiteralSymbol.label + "=");
                    }
                    this.println(str + "=" + i);
                }
                else {
                    this.print(str);
                    final TokenSymbol tokenSymbol = tokenManager.getTokenSymbol(str);
                    if (tokenSymbol == null) {
                        this.antlrTool.warning("undefined token symbol: " + str);
                    }
                    else if (tokenSymbol.getParaphrase() != null) {
                        this.print("(" + tokenSymbol.getParaphrase() + ")");
                    }
                    this.println("=" + i);
                }
            }
        }
        this.currentOutput.close();
        this.currentOutput = null;
    }
    
    public String processStringForASTConstructor(final String s) {
        return s;
    }
    
    public abstract String getASTCreateString(final Vector p0);
    
    public abstract String getASTCreateString(final GrammarAtom p0, final String p1);
    
    protected String getBitsetName(final int i) {
        return "_tokenSet_" + i;
    }
    
    public static String encodeLexerRuleName(final String str) {
        return "m" + str;
    }
    
    public static String decodeLexerRuleName(final String s) {
        if (s == null) {
            return null;
        }
        return s.substring(1, s.length());
    }
    
    public abstract String mapTreeId(final String p0, final ActionTransInfo p1);
    
    protected int markBitsetForGen(final BitSet set) {
        for (int i = 0; i < this.bitsetsUsed.size(); ++i) {
            if (set.equals(this.bitsetsUsed.elementAt(i))) {
                return i;
            }
        }
        this.bitsetsUsed.appendElement(set.clone());
        return this.bitsetsUsed.size() - 1;
    }
    
    protected void print(final String s) {
        if (s != null) {
            this.printTabs();
            this.currentOutput.print(s);
        }
    }
    
    protected void printAction(final String s) {
        if (s != null) {
            this.printTabs();
            this._printAction(s);
        }
    }
    
    protected void println(final String x) {
        if (x != null) {
            this.printTabs();
            this.currentOutput.println(x);
        }
    }
    
    protected void printTabs() {
        for (int i = 1; i <= this.tabs; ++i) {
            this.currentOutput.print("\t");
        }
    }
    
    protected abstract String processActionForSpecialSymbols(final String p0, final int p1, final RuleBlock p2, final ActionTransInfo p3);
    
    public String getFOLLOWBitSet(final String s, final int n) {
        final GrammarSymbol symbol = this.grammar.getSymbol(s);
        if (!(symbol instanceof RuleSymbol)) {
            return null;
        }
        return this.getBitsetName(this.markBitsetForGen(this.grammar.theLLkAnalyzer.FOLLOW(n, ((RuleSymbol)symbol).getBlock().endNode).fset));
    }
    
    public String getFIRSTBitSet(final String s, final int n) {
        final GrammarSymbol symbol = this.grammar.getSymbol(s);
        if (!(symbol instanceof RuleSymbol)) {
            return null;
        }
        return this.getBitsetName(this.markBitsetForGen(this.grammar.theLLkAnalyzer.look(n, ((RuleSymbol)symbol).getBlock()).fset));
    }
    
    protected String removeAssignmentFromDeclaration(String trim) {
        if (trim.indexOf(61) >= 0) {
            trim = trim.substring(0, trim.indexOf(61)).trim();
        }
        return trim;
    }
    
    private void reset() {
        this.tabs = 0;
        this.bitsetsUsed = new Vector();
        this.currentOutput = null;
        this.grammar = null;
        this.DEBUG_CODE_GENERATOR = false;
        this.makeSwitchThreshold = 2;
        this.bitsetTestThreshold = 4;
    }
    
    public static String reverseLexerRuleName(final String s) {
        return s.substring(1, s.length());
    }
    
    public void setAnalyzer(final LLkGrammarAnalyzer analyzer) {
        this.analyzer = analyzer;
    }
    
    public void setBehavior(final DefineGrammarSymbols behavior) {
        this.behavior = behavior;
    }
    
    protected void setGrammar(final Grammar grammar) {
        this.reset();
        this.grammar = grammar;
        if (this.grammar.hasOption("codeGenMakeSwitchThreshold")) {
            try {
                this.makeSwitchThreshold = this.grammar.getIntegerOption("codeGenMakeSwitchThreshold");
            }
            catch (NumberFormatException ex) {
                final Token option = this.grammar.getOption("codeGenMakeSwitchThreshold");
                this.antlrTool.error("option 'codeGenMakeSwitchThreshold' must be an integer", this.grammar.getClassName(), option.getLine(), option.getColumn());
            }
        }
        if (this.grammar.hasOption("codeGenBitsetTestThreshold")) {
            try {
                this.bitsetTestThreshold = this.grammar.getIntegerOption("codeGenBitsetTestThreshold");
            }
            catch (NumberFormatException ex2) {
                final Token option2 = this.grammar.getOption("codeGenBitsetTestThreshold");
                this.antlrTool.error("option 'codeGenBitsetTestThreshold' must be an integer", this.grammar.getClassName(), option2.getLine(), option2.getColumn());
            }
        }
        if (this.grammar.hasOption("codeGenDebug")) {
            final Token option3 = this.grammar.getOption("codeGenDebug");
            if (option3.getText().equals("true")) {
                this.DEBUG_CODE_GENERATOR = true;
            }
            else if (option3.getText().equals("false")) {
                this.DEBUG_CODE_GENERATOR = false;
            }
            else {
                this.antlrTool.error("option 'codeGenDebug' must be true or false", this.grammar.getClassName(), option3.getLine(), option3.getColumn());
            }
        }
    }
    
    public void setTool(final Tool antlrTool) {
        this.antlrTool = antlrTool;
    }
    
    static {
        CodeGenerator.OLD_ACTION_TRANSLATOR = true;
        CodeGenerator.TokenTypesFileSuffix = "TokenTypes";
        CodeGenerator.TokenTypesFileExt = ".txt";
    }
}
