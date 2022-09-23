// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.Vector;
import java.util.Enumeration;
import java.io.IOException;

public class HTMLCodeGenerator extends CodeGenerator
{
    protected int syntacticPredLevel;
    protected boolean doingLexRules;
    protected boolean firstElementInAlt;
    protected AlternativeElement prevAltElem;
    
    public HTMLCodeGenerator() {
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
        this.antlrTool.reportProgress("Generating " + this.grammar.getClassName() + ".html");
        this.currentOutput = this.antlrTool.openOutputFile(this.grammar.getClassName() + ".html");
        this.tabs = 0;
        this.doingLexRules = true;
        this.genHeader();
        this.println("");
        if (this.grammar.comment != null) {
            this._println(HTMLEncode(this.grammar.comment));
        }
        this.println("Definition of lexer " + this.grammar.getClassName() + ", which is a subclass of " + this.grammar.getSuperClass() + ".");
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
        this.antlrTool.reportProgress("Generating " + this.grammar.getClassName() + ".html");
        this.currentOutput = this.antlrTool.openOutputFile(this.grammar.getClassName() + ".html");
        this.tabs = 0;
        this.genHeader();
        this.println("");
        if (this.grammar.comment != null) {
            this._println(HTMLEncode(this.grammar.comment));
        }
        this.println("Definition of parser " + this.grammar.getClassName() + ", which is a subclass of " + this.grammar.getSuperClass() + ".");
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
        this._print("<a href=\"" + this.grammar.getClassName() + ".html#" + ruleRefElement.targetRule + "\">");
        this._print(ruleRefElement.targetRule);
        this._print("</a>");
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
        this.antlrTool.reportProgress("Generating " + this.grammar.getClassName() + ".html");
        this.currentOutput = this.antlrTool.openOutputFile(this.grammar.getClassName() + ".html");
        this.tabs = 0;
        this.genHeader();
        this.println("");
        this.println("");
        if (this.grammar.comment != null) {
            this._println(HTMLEncode(this.grammar.comment));
        }
        this.println("Definition of tree parser " + this.grammar.getClassName() + ", which is a subclass of " + this.grammar.getSuperClass() + ".");
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
        for (int i = 0; i < alternativeBlock.alternatives.size(); ++i) {
            final Alternative alternative = alternativeBlock.getAlternativeAt(i);
            final AlternativeElement head = alternative.head;
            if (i > 0 && alternativeBlock.alternatives.size() > 1) {
                this._println("");
                this.print("|\t");
            }
            final boolean firstElementInAlt = this.firstElementInAlt;
            this.firstElementInAlt = true;
            ++this.tabs;
            this.genAlt(alternative);
            --this.tabs;
            this.firstElementInAlt = firstElementInAlt;
        }
    }
    
    public void genFollowSetForRuleBlock(final RuleBlock ruleBlock) {
        this.printSet(this.grammar.maxk, 1, this.grammar.theLLkAnalyzer.FOLLOW(1, ruleBlock.endNode));
    }
    
    protected void genGenericBlock(final AlternativeBlock alternativeBlock, final String s) {
        if (alternativeBlock.alternatives.size() > 1) {
            if (!this.firstElementInAlt) {
                if (this.prevAltElem == null || !(this.prevAltElem instanceof AlternativeBlock) || ((AlternativeBlock)this.prevAltElem).alternatives.size() == 1) {
                    this._println("");
                    this.print("(\t");
                }
                else {
                    this._print("(\t");
                }
            }
            else {
                this._print("(\t");
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
        this.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        this.println("<HTML>");
        this.println("<HEAD>");
        this.println("<TITLE>Grammar " + this.antlrTool.grammarFile + "</TITLE>");
        this.println("</HEAD>");
        this.println("<BODY>");
        this.println("<table summary=\"\" border=\"1\" cellpadding=\"5\">");
        this.println("<tr>");
        this.println("<td>");
        this.println("<font size=\"+2\">Grammar " + this.grammar.getClassName() + "</font><br>");
        this.println("<a href=\"http://www.ANTLR.org\">ANTLR</a>-generated HTML file from " + this.antlrTool.grammarFile);
        this.println("<p>");
        this.println("Terence Parr, <a href=\"http://www.magelang.com\">MageLang Institute</a>");
        final StringBuffer append = new StringBuffer().append("<br>ANTLR Version ");
        final Tool antlrTool = this.antlrTool;
        this.println(append.append(Tool.version).append("; 1989-2005").toString());
        this.println("</td>");
        this.println("</tr>");
        this.println("</table>");
        this.println("<PRE>");
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
        if (ruleSymbol.comment != null) {
            this._println(HTMLEncode(ruleSymbol.comment));
        }
        if (ruleSymbol.access.length() != 0 && !ruleSymbol.access.equals("public")) {
            this._print(ruleSymbol.access + " ");
        }
        this._print("<a name=\"" + ruleSymbol.getId() + "\">");
        this._print(ruleSymbol.getId());
        this._print("</a>");
        final RuleBlock block = ruleSymbol.getBlock();
        this._println("");
        ++this.tabs;
        this.print(":\t");
        this.genCommonBlock(block);
        this._println("");
        this.println(";");
        --this.tabs;
    }
    
    protected void genSynPred(final SynPredBlock synPredBlock) {
        ++this.syntacticPredLevel;
        this.genGenericBlock(synPredBlock, " =>");
        --this.syntacticPredLevel;
    }
    
    public void genTail() {
        this.println("</PRE>");
        this.println("</BODY>");
        this.println("</HTML>");
    }
    
    protected void genTokenTypes(final TokenManager tokenManager) throws IOException {
        this.antlrTool.reportProgress("Generating " + tokenManager.getName() + HTMLCodeGenerator.TokenTypesFileSuffix + HTMLCodeGenerator.TokenTypesFileExt);
        this.currentOutput = this.antlrTool.openOutputFile(tokenManager.getName() + HTMLCodeGenerator.TokenTypesFileSuffix + HTMLCodeGenerator.TokenTypesFileExt);
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
    
    public String getASTCreateString(final Vector vector) {
        return null;
    }
    
    public String getASTCreateString(final GrammarAtom grammarAtom, final String s) {
        return null;
    }
    
    public String mapTreeId(final String s, final ActionTransInfo actionTransInfo) {
        return s;
    }
    
    protected String processActionForSpecialSymbols(final String s, final int n, final RuleBlock ruleBlock, final ActionTransInfo actionTransInfo) {
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
