// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.Vector;
import java.util.Enumeration;
import java.io.IOException;

public class DocBookCodeGenerator extends CodeGenerator
{
    protected int syntacticPredLevel;
    protected boolean doingLexRules;
    protected boolean firstElementInAlt;
    protected AlternativeElement prevAltElem;
    
    public DocBookCodeGenerator() {
        this.syntacticPredLevel = 0;
        this.doingLexRules = false;
        this.prevAltElem = null;
        this.charFormatter = new JavaCharFormatter();
    }
    
    static String HTMLEncode(final String s) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '&') {
                sb.append("&amp;");
            }
            else if (char1 == '\"') {
                sb.append("&quot;");
            }
            else if (char1 == '\'') {
                sb.append("&#039;");
            }
            else if (char1 == '<') {
                sb.append("&lt;");
            }
            else if (char1 == '>') {
                sb.append("&gt;");
            }
            else {
                sb.append(char1);
            }
        }
        return sb.toString();
    }
    
    static String QuoteForId(final String s) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '_') {
                sb.append(".");
            }
            else {
                sb.append(char1);
            }
        }
        return sb.toString();
    }
    
    public void gen() {
        try {
            final Enumeration<Grammar> elements = this.behavior.grammars.elements();
            while (elements.hasMoreElements()) {
                final Grammar grammar = elements.nextElement();
                grammar.setCodeGenerator(this);
                grammar.generate();
                if (this.antlrTool.hasError()) {
                    this.antlrTool.fatalError("Exiting due to errors.");
                }
            }
        }
        catch (IOException ex) {
            this.antlrTool.reportException(ex, null);
        }
    }
    
    public void gen(final ActionElement actionElement) {
    }
    
    public void gen(final AlternativeBlock alternativeBlock) {
        this.genGenericBlock(alternativeBlock, "");
    }
    
    public void gen(final BlockEndElement blockEndElement) {
    }
    
    public void gen(final CharLiteralElement charLiteralElement) {
        if (charLiteralElement.not) {
            this._print("~");
        }
        this._print(HTMLEncode(charLiteralElement.atomText) + " ");
    }
    
    public void gen(final CharRangeElement charRangeElement) {
        this.print(charRangeElement.beginText + ".." + charRangeElement.endText + " ");
    }
    
    public void gen(final LexerGrammar grammar) throws IOException {
        this.setGrammar(grammar);
        this.antlrTool.reportProgress("Generating " + this.grammar.getClassName() + ".sgml");
        this.currentOutput = this.antlrTool.openOutputFile(this.grammar.getClassName() + ".sgml");
        this.tabs = 0;
        this.doingLexRules = true;
        this.genHeader();
        this.println("");
        if (this.grammar.comment != null) {
            this._println(HTMLEncode(this.grammar.comment));
        }
        this.println("<para>Definition of lexer " + this.grammar.getClassName() + ", which is a subclass of " + this.grammar.getSuperClass() + ".</para>");
        this.genNextToken();
        final Enumeration elements = this.grammar.rules.elements();
        while (elements.hasMoreElements()) {
            final RuleSymbol ruleSymbol = elements.nextElement();
            if (!ruleSymbol.id.equals("mnextToken")) {
                this.genRule(ruleSymbol);
            }
        }
        this.currentOutput.close();
        this.currentOutput = null;
        this.doingLexRules = false;
    }
    
    public void gen(final OneOrMoreBlock oneOrMoreBlock) {
        this.genGenericBlock(oneOrMoreBlock, "+");
    }
    
    public void gen(final ParserGrammar grammar) throws IOException {
        this.setGrammar(grammar);
        this.antlrTool.reportProgress("Generating " + this.grammar.getClassName() + ".sgml");
        this.currentOutput = this.antlrTool.openOutputFile(this.grammar.getClassName() + ".sgml");
        this.tabs = 0;
        this.genHeader();
        this.println("");
        if (this.grammar.comment != null) {
            this._println(HTMLEncode(this.grammar.comment));
        }
        this.println("<para>Definition of parser " + this.grammar.getClassName() + ", which is a subclass of " + this.grammar.getSuperClass() + ".</para>");
        final Enumeration elements = this.grammar.rules.elements();
        while (elements.hasMoreElements()) {
            this.println("");
            final GrammarSymbol grammarSymbol = elements.nextElement();
            if (grammarSymbol instanceof RuleSymbol) {
                this.genRule((RuleSymbol)grammarSymbol);
            }
        }
        --this.tabs;
        this.println("");
        this.genTail();
        this.currentOutput.close();
        this.currentOutput = null;
    }
    
    public void gen(final RuleRefElement ruleRefElement) {
        final RuleSymbol ruleSymbol = (RuleSymbol)this.grammar.getSymbol(ruleRefElement.targetRule);
        this._print("<link linkend=\"" + QuoteForId(ruleRefElement.targetRule) + "\">");
        this._print(ruleRefElement.targetRule);
        this._print("</link>");
        this._print(" ");
    }
    
    public void gen(final StringLiteralElement stringLiteralElement) {
        if (stringLiteralElement.not) {
            this._print("~");
        }
        this._print(HTMLEncode(stringLiteralElement.atomText));
        this._print(" ");
    }
    
    public void gen(final TokenRangeElement tokenRangeElement) {
        this.print(tokenRangeElement.beginText + ".." + tokenRangeElement.endText + " ");
    }
    
    public void gen(final TokenRefElement tokenRefElement) {
        if (tokenRefElement.not) {
            this._print("~");
        }
        this._print(tokenRefElement.atomText);
        this._print(" ");
    }
    
    public void gen(final TreeElement obj) {
        this.print(obj + " ");
    }
    
    public void gen(final TreeWalkerGrammar grammar) throws IOException {
        this.setGrammar(grammar);
        this.antlrTool.reportProgress("Generating " + this.grammar.getClassName() + ".sgml");
        this.currentOutput = this.antlrTool.openOutputFile(this.grammar.getClassName() + ".sgml");
        this.tabs = 0;
        this.genHeader();
        this.println("");
        this.println("");
        if (this.grammar.comment != null) {
            this._println(HTMLEncode(this.grammar.comment));
        }
        this.println("<para>Definition of tree parser " + this.grammar.getClassName() + ", which is a subclass of " + this.grammar.getSuperClass() + ".</para>");
        this.println("");
        ++this.tabs;
        final Enumeration elements = this.grammar.rules.elements();
        while (elements.hasMoreElements()) {
            this.println("");
            final GrammarSymbol grammarSymbol = elements.nextElement();
            if (grammarSymbol instanceof RuleSymbol) {
                this.genRule((RuleSymbol)grammarSymbol);
            }
        }
        --this.tabs;
        this.println("");
        this.currentOutput.close();
        this.currentOutput = null;
    }
    
    public void gen(final WildcardElement wildcardElement) {
        this._print(". ");
    }
    
    public void gen(final ZeroOrMoreBlock zeroOrMoreBlock) {
        this.genGenericBlock(zeroOrMoreBlock, "*");
    }
    
    protected void genAlt(final Alternative alternative) {
        if (alternative.getTreeSpecifier() != null) {
            this._print(alternative.getTreeSpecifier().getText());
        }
        this.prevAltElem = null;
        for (AlternativeElement prevAltElem = alternative.head; !(prevAltElem instanceof BlockEndElement); prevAltElem = prevAltElem.next) {
            prevAltElem.generate();
            this.firstElementInAlt = false;
            this.prevAltElem = prevAltElem;
        }
    }
    
    public void genCommonBlock(final AlternativeBlock alternativeBlock) {
        if (alternativeBlock.alternatives.size() > 1) {
            this.println("<itemizedlist mark=\"none\">");
        }
        for (int i = 0; i < alternativeBlock.alternatives.size(); ++i) {
            final Alternative alternative = alternativeBlock.getAlternativeAt(i);
            final AlternativeElement head = alternative.head;
            if (alternativeBlock.alternatives.size() > 1) {
                this.print("<listitem><para>");
            }
            if (i > 0 && alternativeBlock.alternatives.size() > 1) {
                this._print("| ");
            }
            final boolean firstElementInAlt = this.firstElementInAlt;
            this.firstElementInAlt = true;
            ++this.tabs;
            this.genAlt(alternative);
            --this.tabs;
            this.firstElementInAlt = firstElementInAlt;
            if (alternativeBlock.alternatives.size() > 1) {
                this._println("</para></listitem>");
            }
        }
        if (alternativeBlock.alternatives.size() > 1) {
            this.println("</itemizedlist>");
        }
    }
    
    public void genFollowSetForRuleBlock(final RuleBlock ruleBlock) {
        this.printSet(this.grammar.maxk, 1, this.grammar.theLLkAnalyzer.FOLLOW(1, ruleBlock.endNode));
    }
    
    protected void genGenericBlock(final AlternativeBlock alternativeBlock, final String s) {
        if (alternativeBlock.alternatives.size() > 1) {
            this._println("");
            if (!this.firstElementInAlt) {
                this._println("(");
            }
            else {
                this._print("(");
            }
        }
        else {
            this._print("( ");
        }
        this.genCommonBlock(alternativeBlock);
        if (alternativeBlock.alternatives.size() > 1) {
            this._println("");
            this.print(")" + s + " ");
            if (!(alternativeBlock.next instanceof BlockEndElement)) {
                this._println("");
                this.print("");
            }
        }
        else {
            this._print(")" + s + " ");
        }
    }
    
    protected void genHeader() {
        this.println("<?xml version=\"1.0\" standalone=\"no\"?>");
        this.println("<!DOCTYPE book PUBLIC \"-//OASIS//DTD DocBook V3.1//EN\">");
        this.println("<book lang=\"en\">");
        this.println("<bookinfo>");
        this.println("<title>Grammar " + this.grammar.getClassName() + "</title>");
        this.println("  <author>");
        this.println("    <firstname></firstname>");
        this.println("    <othername></othername>");
        this.println("    <surname></surname>");
        this.println("    <affiliation>");
        this.println("     <address>");
        this.println("     <email></email>");
        this.println("     </address>");
        this.println("    </affiliation>");
        this.println("  </author>");
        this.println("  <othercredit>");
        this.println("    <contrib>");
        final StringBuffer append = new StringBuffer().append("    Generated by <ulink url=\"http://www.ANTLR.org/\">ANTLR</ulink>");
        final Tool antlrTool = this.antlrTool;
        this.println(append.append(Tool.version).toString());
        this.println("    from " + this.antlrTool.grammarFile);
        this.println("    </contrib>");
        this.println("  </othercredit>");
        this.println("  <pubdate></pubdate>");
        this.println("  <abstract>");
        this.println("  <para>");
        this.println("  </para>");
        this.println("  </abstract>");
        this.println("</bookinfo>");
        this.println("<chapter>");
        this.println("<title></title>");
    }
    
    protected void genLookaheadSetForAlt(final Alternative alternative) {
        if (this.doingLexRules && alternative.cache[1].containsEpsilon()) {
            this.println("MATCHES ALL");
            return;
        }
        int n = alternative.lookaheadDepth;
        if (n == Integer.MAX_VALUE) {
            n = this.grammar.maxk;
        }
        for (int i = 1; i <= n; ++i) {
            this.printSet(n, i, alternative.cache[i]);
        }
    }
    
    public void genLookaheadSetForBlock(final AlternativeBlock alternativeBlock) {
        int n = 0;
        for (int i = 0; i < alternativeBlock.alternatives.size(); ++i) {
            final Alternative alternative = alternativeBlock.getAlternativeAt(i);
            if (alternative.lookaheadDepth == Integer.MAX_VALUE) {
                n = this.grammar.maxk;
                break;
            }
            if (n < alternative.lookaheadDepth) {
                n = alternative.lookaheadDepth;
            }
        }
        for (int j = 1; j <= n; ++j) {
            this.printSet(n, j, this.grammar.theLLkAnalyzer.look(j, alternativeBlock));
        }
    }
    
    public void genNextToken() {
        this.println("");
        this.println("/** Lexer nextToken rule:");
        this.println(" *  The lexer nextToken rule is synthesized from all of the user-defined");
        this.println(" *  lexer rules.  It logically consists of one big alternative block with");
        this.println(" *  each user-defined rule being an alternative.");
        this.println(" */");
        final RuleBlock nextTokenRule = MakeGrammar.createNextTokenRule(this.grammar, this.grammar.rules, "nextToken");
        final RuleSymbol ruleSymbol = new RuleSymbol("mnextToken");
        ruleSymbol.setDefined();
        ruleSymbol.setBlock(nextTokenRule);
        ruleSymbol.access = "private";
        this.grammar.define(ruleSymbol);
        this.genCommonBlock(nextTokenRule);
    }
    
    public void genRule(final RuleSymbol ruleSymbol) {
        if (ruleSymbol == null || !ruleSymbol.isDefined()) {
            return;
        }
        this.println("");
        if (ruleSymbol.access.length() != 0 && !ruleSymbol.access.equals("public")) {
            this._print("<para>" + ruleSymbol.access + " </para>");
        }
        this.println("<section id=\"" + QuoteForId(ruleSymbol.getId()) + "\">");
        this.println("<title>" + ruleSymbol.getId() + "</title>");
        if (ruleSymbol.comment != null) {
            this._println("<para>" + HTMLEncode(ruleSymbol.comment) + "</para>");
        }
        this.println("<para>");
        final RuleBlock block = ruleSymbol.getBlock();
        this._println("");
        this.print(ruleSymbol.getId() + ":\t");
        ++this.tabs;
        this.genCommonBlock(block);
        this._println("");
        --this.tabs;
        this._println("</para>");
        this._println("</section><!-- section \"" + ruleSymbol.getId() + "\" -->");
    }
    
    protected void genSynPred(final SynPredBlock synPredBlock) {
    }
    
    public void genTail() {
        this.println("</chapter>");
        this.println("</book>");
    }
    
    protected void genTokenTypes(final TokenManager tokenManager) throws IOException {
        this.antlrTool.reportProgress("Generating " + tokenManager.getName() + DocBookCodeGenerator.TokenTypesFileSuffix + DocBookCodeGenerator.TokenTypesFileExt);
        this.currentOutput = this.antlrTool.openOutputFile(tokenManager.getName() + DocBookCodeGenerator.TokenTypesFileSuffix + DocBookCodeGenerator.TokenTypesFileExt);
        this.tabs = 0;
        this.genHeader();
        this.println("");
        this.println("*** Tokens used by the parser");
        this.println("This is a list of the token numeric values and the corresponding");
        this.println("token identifiers.  Some tokens are literals, and because of that");
        this.println("they have no identifiers.  Literals are double-quoted.");
        ++this.tabs;
        final Vector vocabulary = tokenManager.getVocabulary();
        for (int i = 4; i < vocabulary.size(); ++i) {
            final String str = (String)vocabulary.elementAt(i);
            if (str != null) {
                this.println(str + " = " + i);
            }
        }
        --this.tabs;
        this.println("*** End of tokens used by the parser");
        this.currentOutput.close();
        this.currentOutput = null;
    }
    
    protected String processActionForSpecialSymbols(final String s, final int n, final RuleBlock ruleBlock, final ActionTransInfo actionTransInfo) {
        return s;
    }
    
    public String getASTCreateString(final Vector vector) {
        return null;
    }
    
    public String getASTCreateString(final GrammarAtom grammarAtom, final String s) {
        return null;
    }
    
    public String mapTreeId(final String s, final ActionTransInfo actionTransInfo) {
        return s;
    }
    
    public void printSet(final int n, final int i, final Lookahead lookahead) {
        final int n2 = 5;
        final int[] array = lookahead.fset.toArray();
        if (n != 1) {
            this.print("k==" + i + ": {");
        }
        else {
            this.print("{ ");
        }
        if (array.length > n2) {
            this._println("");
            ++this.tabs;
            this.print("");
        }
        int n3 = 0;
        for (int j = 0; j < array.length; ++j) {
            if (++n3 > n2) {
                this._println("");
                this.print("");
                n3 = 0;
            }
            if (this.doingLexRules) {
                this._print(this.charFormatter.literalChar(array[j]));
            }
            else {
                this._print((String)this.grammar.tokenManager.getVocabulary().elementAt(array[j]));
            }
            if (j != array.length - 1) {
                this._print(", ");
            }
        }
        if (array.length > n2) {
            this._println("");
            --this.tabs;
            this.print("");
        }
        this._println(" }");
    }
}
