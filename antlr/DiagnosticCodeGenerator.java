// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.Vector;
import java.util.Enumeration;
import java.io.IOException;

public class DiagnosticCodeGenerator extends CodeGenerator
{
    protected int syntacticPredLevel;
    protected boolean doingLexRules;
    
    public DiagnosticCodeGenerator() {
        this.syntacticPredLevel = 0;
        this.doingLexRules = false;
        this.charFormatter = new JavaCharFormatter();
    }
    
    public void gen() {
        try {
            final Enumeration<Grammar> elements = this.behavior.grammars.elements();
            while (elements.hasMoreElements()) {
                final Grammar grammar = elements.nextElement();
                grammar.setGrammarAnalyzer(this.analyzer);
                grammar.setCodeGenerator(this);
                this.analyzer.setGrammar(grammar);
                grammar.generate();
                if (this.antlrTool.hasError()) {
                    this.antlrTool.panic("Exiting due to errors.");
                }
            }
            final Enumeration<TokenManager> elements2 = this.behavior.tokenManagers.elements();
            while (elements2.hasMoreElements()) {
                final TokenManager tokenManager = elements2.nextElement();
                if (!tokenManager.isReadOnly()) {
                    this.genTokenTypes(tokenManager);
                }
            }
        }
        catch (IOException ex) {
            this.antlrTool.reportException(ex, null);
        }
    }
    
    public void gen(final ActionElement actionElement) {
        if (!actionElement.isSemPred) {
            this.print("ACTION: ");
            this._printAction(actionElement.actionText);
        }
    }
    
    public void gen(final AlternativeBlock alternativeBlock) {
        this.println("Start of alternative block.");
        ++this.tabs;
        this.genBlockPreamble(alternativeBlock);
        if (!this.grammar.theLLkAnalyzer.deterministic(alternativeBlock)) {
            this.println("Warning: This alternative block is non-deterministic");
        }
        this.genCommonBlock(alternativeBlock);
        --this.tabs;
    }
    
    public void gen(final BlockEndElement blockEndElement) {
    }
    
    public void gen(final CharLiteralElement charLiteralElement) {
        this.print("Match character ");
        if (charLiteralElement.not) {
            this._print("NOT ");
        }
        this._print(charLiteralElement.atomText);
        if (charLiteralElement.label != null) {
            this._print(", label=" + charLiteralElement.label);
        }
        this._println("");
    }
    
    public void gen(final CharRangeElement charRangeElement) {
        this.print("Match character range: " + charRangeElement.beginText + ".." + charRangeElement.endText);
        if (charRangeElement.label != null) {
            this._print(", label = " + charRangeElement.label);
        }
        this._println("");
    }
    
    public void gen(final LexerGrammar grammar) throws IOException {
        this.setGrammar(grammar);
        this.antlrTool.reportProgress("Generating " + this.grammar.getClassName() + DiagnosticCodeGenerator.TokenTypesFileExt);
        this.currentOutput = this.antlrTool.openOutputFile(this.grammar.getClassName() + DiagnosticCodeGenerator.TokenTypesFileExt);
        this.tabs = 0;
        this.doingLexRules = true;
        this.genHeader();
        this.println("");
        this.println("*** Lexer Preamble Action.");
        this.println("This action will appear before the declaration of your lexer class:");
        ++this.tabs;
        this.println(this.grammar.preambleAction.getText());
        --this.tabs;
        this.println("*** End of Lexer Preamble Action");
        this.println("");
        this.println("*** Your lexer class is called '" + this.grammar.getClassName() + "' and is a subclass of '" + this.grammar.getSuperClass() + "'.");
        this.println("");
        this.println("*** User-defined lexer  class members:");
        this.println("These are the member declarations that you defined for your class:");
        ++this.tabs;
        this.printAction(this.grammar.classMemberAction.getText());
        --this.tabs;
        this.println("*** End of user-defined lexer class members");
        this.println("");
        this.println("*** String literals used in the parser");
        this.println("The following string literals were used in the parser.");
        this.println("An actual code generator would arrange to place these literals");
        this.println("into a table in the generated lexer, so that actions in the");
        this.println("generated lexer could match token text against the literals.");
        this.println("String literals used in the lexer are not listed here, as they");
        this.println("are incorporated into the mainstream lexer processing.");
        ++this.tabs;
        final Enumeration symbols = this.grammar.getSymbols();
        while (symbols.hasMoreElements()) {
            final GrammarSymbol grammarSymbol = symbols.nextElement();
            if (grammarSymbol instanceof StringLiteralSymbol) {
                final StringLiteralSymbol stringLiteralSymbol = (StringLiteralSymbol)grammarSymbol;
                this.println(stringLiteralSymbol.getId() + " = " + stringLiteralSymbol.getTokenType());
            }
        }
        --this.tabs;
        this.println("*** End of string literals used by the parser");
        this.genNextToken();
        this.println("");
        this.println("*** User-defined Lexer rules:");
        ++this.tabs;
        final Enumeration elements = this.grammar.rules.elements();
        while (elements.hasMoreElements()) {
            final RuleSymbol ruleSymbol = elements.nextElement();
            if (!ruleSymbol.id.equals("mnextToken")) {
                this.genRule(ruleSymbol);
            }
        }
        --this.tabs;
        this.println("");
        this.println("*** End User-defined Lexer rules:");
        this.currentOutput.close();
        this.currentOutput = null;
        this.doingLexRules = false;
    }
    
    public void gen(final OneOrMoreBlock oneOrMoreBlock) {
        this.println("Start ONE-OR-MORE (...)+ block:");
        ++this.tabs;
        this.genBlockPreamble(oneOrMoreBlock);
        if (!this.grammar.theLLkAnalyzer.deterministic(oneOrMoreBlock)) {
            this.println("Warning: This one-or-more block is non-deterministic");
        }
        this.genCommonBlock(oneOrMoreBlock);
        --this.tabs;
        this.println("End ONE-OR-MORE block.");
    }
    
    public void gen(final ParserGrammar grammar) throws IOException {
        this.setGrammar(grammar);
        this.antlrTool.reportProgress("Generating " + this.grammar.getClassName() + DiagnosticCodeGenerator.TokenTypesFileExt);
        this.currentOutput = this.antlrTool.openOutputFile(this.grammar.getClassName() + DiagnosticCodeGenerator.TokenTypesFileExt);
        this.tabs = 0;
        this.genHeader();
        this.println("");
        this.println("*** Parser Preamble Action.");
        this.println("This action will appear before the declaration of your parser class:");
        ++this.tabs;
        this.println(this.grammar.preambleAction.getText());
        --this.tabs;
        this.println("*** End of Parser Preamble Action");
        this.println("");
        this.println("*** Your parser class is called '" + this.grammar.getClassName() + "' and is a subclass of '" + this.grammar.getSuperClass() + "'.");
        this.println("");
        this.println("*** User-defined parser class members:");
        this.println("These are the member declarations that you defined for your class:");
        ++this.tabs;
        this.printAction(this.grammar.classMemberAction.getText());
        --this.tabs;
        this.println("*** End of user-defined parser class members");
        this.println("");
        this.println("*** Parser rules:");
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
        this.println("*** End of parser rules");
        this.println("");
        this.println("*** End of parser");
        this.currentOutput.close();
        this.currentOutput = null;
    }
    
    public void gen(final RuleRefElement ruleRefElement) {
        final RuleSymbol ruleSymbol = (RuleSymbol)this.grammar.getSymbol(ruleRefElement.targetRule);
        this.print("Rule Reference: " + ruleRefElement.targetRule);
        if (ruleRefElement.idAssign != null) {
            this._print(", assigned to '" + ruleRefElement.idAssign + "'");
        }
        if (ruleRefElement.args != null) {
            this._print(", arguments = " + ruleRefElement.args);
        }
        this._println("");
        if (ruleSymbol == null || !ruleSymbol.isDefined()) {
            this.println("Rule '" + ruleRefElement.targetRule + "' is referenced, but that rule is not defined.");
            this.println("\tPerhaps the rule is misspelled, or you forgot to define it.");
            return;
        }
        if (!(ruleSymbol instanceof RuleSymbol)) {
            this.println("Rule '" + ruleRefElement.targetRule + "' is referenced, but that is not a grammar rule.");
            return;
        }
        if (ruleRefElement.idAssign != null) {
            if (ruleSymbol.block.returnAction == null) {
                this.println("Error: You assigned from Rule '" + ruleRefElement.targetRule + "', but that rule has no return type.");
            }
        }
        else if (!(this.grammar instanceof LexerGrammar) && this.syntacticPredLevel == 0 && ruleSymbol.block.returnAction != null) {
            this.println("Warning: Rule '" + ruleRefElement.targetRule + "' returns a value");
        }
        if (ruleRefElement.args != null && ruleSymbol.block.argAction == null) {
            this.println("Error: Rule '" + ruleRefElement.targetRule + "' accepts no arguments.");
        }
    }
    
    public void gen(final StringLiteralElement stringLiteralElement) {
        this.print("Match string literal ");
        this._print(stringLiteralElement.atomText);
        if (stringLiteralElement.label != null) {
            this._print(", label=" + stringLiteralElement.label);
        }
        this._println("");
    }
    
    public void gen(final TokenRangeElement tokenRangeElement) {
        this.print("Match token range: " + tokenRangeElement.beginText + ".." + tokenRangeElement.endText);
        if (tokenRangeElement.label != null) {
            this._print(", label = " + tokenRangeElement.label);
        }
        this._println("");
    }
    
    public void gen(final TokenRefElement tokenRefElement) {
        this.print("Match token ");
        if (tokenRefElement.not) {
            this._print("NOT ");
        }
        this._print(tokenRefElement.atomText);
        if (tokenRefElement.label != null) {
            this._print(", label=" + tokenRefElement.label);
        }
        this._println("");
    }
    
    public void gen(final TreeElement obj) {
        this.print("Tree reference: " + obj);
    }
    
    public void gen(final TreeWalkerGrammar grammar) throws IOException {
        this.setGrammar(grammar);
        this.antlrTool.reportProgress("Generating " + this.grammar.getClassName() + DiagnosticCodeGenerator.TokenTypesFileExt);
        this.currentOutput = this.antlrTool.openOutputFile(this.grammar.getClassName() + DiagnosticCodeGenerator.TokenTypesFileExt);
        this.tabs = 0;
        this.genHeader();
        this.println("");
        this.println("*** Tree-walker Preamble Action.");
        this.println("This action will appear before the declaration of your tree-walker class:");
        ++this.tabs;
        this.println(this.grammar.preambleAction.getText());
        --this.tabs;
        this.println("*** End of tree-walker Preamble Action");
        this.println("");
        this.println("*** Your tree-walker class is called '" + this.grammar.getClassName() + "' and is a subclass of '" + this.grammar.getSuperClass() + "'.");
        this.println("");
        this.println("*** User-defined tree-walker class members:");
        this.println("These are the member declarations that you defined for your class:");
        ++this.tabs;
        this.printAction(this.grammar.classMemberAction.getText());
        --this.tabs;
        this.println("*** End of user-defined tree-walker class members");
        this.println("");
        this.println("*** tree-walker rules:");
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
        this.println("*** End of tree-walker rules");
        this.println("");
        this.println("*** End of tree-walker");
        this.currentOutput.close();
        this.currentOutput = null;
    }
    
    public void gen(final WildcardElement wildcardElement) {
        this.print("Match wildcard");
        if (wildcardElement.getLabel() != null) {
            this._print(", label = " + wildcardElement.getLabel());
        }
        this._println("");
    }
    
    public void gen(final ZeroOrMoreBlock zeroOrMoreBlock) {
        this.println("Start ZERO-OR-MORE (...)+ block:");
        ++this.tabs;
        this.genBlockPreamble(zeroOrMoreBlock);
        if (!this.grammar.theLLkAnalyzer.deterministic(zeroOrMoreBlock)) {
            this.println("Warning: This zero-or-more block is non-deterministic");
        }
        this.genCommonBlock(zeroOrMoreBlock);
        --this.tabs;
        this.println("End ZERO-OR-MORE block.");
    }
    
    protected void genAlt(final Alternative alternative) {
        for (AlternativeElement alternativeElement = alternative.head; !(alternativeElement instanceof BlockEndElement); alternativeElement = alternativeElement.next) {
            alternativeElement.generate();
        }
        if (alternative.getTreeSpecifier() != null) {
            this.println("AST will be built as: " + alternative.getTreeSpecifier().getText());
        }
    }
    
    protected void genBlockPreamble(final AlternativeBlock alternativeBlock) {
        if (alternativeBlock.initAction != null) {
            this.printAction("Init action: " + alternativeBlock.initAction);
        }
    }
    
    public void genCommonBlock(final AlternativeBlock alternativeBlock) {
        final boolean b = alternativeBlock.alternatives.size() == 1;
        this.println("Start of an alternative block.");
        ++this.tabs;
        this.println("The lookahead set for this block is:");
        ++this.tabs;
        this.genLookaheadSetForBlock(alternativeBlock);
        --this.tabs;
        if (b) {
            this.println("This block has a single alternative");
            if (alternativeBlock.getAlternativeAt(0).synPred != null) {
                this.println("Warning: you specified a syntactic predicate for this alternative,");
                this.println("and it is the only alternative of a block and will be ignored.");
            }
        }
        else {
            this.println("This block has multiple alternatives:");
            ++this.tabs;
        }
        for (int i = 0; i < alternativeBlock.alternatives.size(); ++i) {
            final Alternative alternative = alternativeBlock.getAlternativeAt(i);
            final AlternativeElement head = alternative.head;
            this.println("");
            if (i != 0) {
                this.print("Otherwise, ");
            }
            else {
                this.print("");
            }
            this._println("Alternate(" + (i + 1) + ") will be taken IF:");
            this.println("The lookahead set: ");
            ++this.tabs;
            this.genLookaheadSetForAlt(alternative);
            --this.tabs;
            if (alternative.semPred != null || alternative.synPred != null) {
                this.print("is matched, AND ");
            }
            else {
                this.println("is matched.");
            }
            if (alternative.semPred != null) {
                this._println("the semantic predicate:");
                ++this.tabs;
                this.println(alternative.semPred);
                if (alternative.synPred != null) {
                    this.print("is true, AND ");
                }
                else {
                    this.println("is true.");
                }
            }
            if (alternative.synPred != null) {
                this._println("the syntactic predicate:");
                ++this.tabs;
                this.genSynPred(alternative.synPred);
                --this.tabs;
                this.println("is matched.");
            }
            this.genAlt(alternative);
        }
        this.println("");
        this.println("OTHERWISE, a NoViableAlt exception will be thrown");
        this.println("");
        if (!b) {
            --this.tabs;
            this.println("End of alternatives");
        }
        --this.tabs;
        this.println("End of alternative block.");
    }
    
    public void genFollowSetForRuleBlock(final RuleBlock ruleBlock) {
        this.printSet(this.grammar.maxk, 1, this.grammar.theLLkAnalyzer.FOLLOW(1, ruleBlock.endNode));
    }
    
    protected void genHeader() {
        this.println("ANTLR-generated file resulting from grammar " + this.antlrTool.grammarFile);
        this.println("Diagnostic output");
        this.println("");
        this.println("Terence Parr, MageLang Institute");
        this.println("with John Lilley, Empathy Software");
        final StringBuffer append = new StringBuffer().append("ANTLR Version ");
        final Tool antlrTool = this.antlrTool;
        this.println(append.append(Tool.version).append("; 1989-2005").toString());
        this.println("");
        this.println("*** Header Action.");
        this.println("This action will appear at the top of all generated files.");
        ++this.tabs;
        this.printAction(this.behavior.getHeaderAction(""));
        --this.tabs;
        this.println("*** End of Header Action");
        this.println("");
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
        this.println("*** Lexer nextToken rule:");
        this.println("The lexer nextToken rule is synthesized from all of the user-defined");
        this.println("lexer rules.  It logically consists of one big alternative block with");
        this.println("each user-defined rule being an alternative.");
        this.println("");
        final RuleBlock nextTokenRule = MakeGrammar.createNextTokenRule(this.grammar, this.grammar.rules, "nextToken");
        final RuleSymbol ruleSymbol = new RuleSymbol("mnextToken");
        ruleSymbol.setDefined();
        ruleSymbol.setBlock(nextTokenRule);
        ruleSymbol.access = "private";
        this.grammar.define(ruleSymbol);
        if (!this.grammar.theLLkAnalyzer.deterministic(nextTokenRule)) {
            this.println("The grammar analyzer has determined that the synthesized");
            this.println("nextToken rule is non-deterministic (i.e., it has ambiguities)");
            this.println("This means that there is some overlap of the character");
            this.println("lookahead for two or more of your lexer rules.");
        }
        this.genCommonBlock(nextTokenRule);
        this.println("*** End of nextToken lexer rule.");
    }
    
    public void genRule(final RuleSymbol ruleSymbol) {
        this.println("");
        final String str = this.doingLexRules ? "Lexer" : "Parser";
        this.println("*** " + str + " Rule: " + ruleSymbol.getId());
        if (!ruleSymbol.isDefined()) {
            this.println("This rule is undefined.");
            this.println("This means that the rule was referenced somewhere in the grammar,");
            this.println("but a definition for the rule was not encountered.");
            this.println("It is also possible that syntax errors during the parse of");
            this.println("your grammar file prevented correct processing of the rule.");
            this.println("*** End " + str + " Rule: " + ruleSymbol.getId());
            return;
        }
        ++this.tabs;
        if (ruleSymbol.access.length() != 0) {
            this.println("Access: " + ruleSymbol.access);
        }
        final RuleBlock block = ruleSymbol.getBlock();
        if (block.returnAction != null) {
            this.println("Return value(s): " + block.returnAction);
            if (this.doingLexRules) {
                this.println("Error: you specified return value(s) for a lexical rule.");
                this.println("\tLexical rules have an implicit return type of 'int'.");
            }
        }
        else if (this.doingLexRules) {
            this.println("Return value: lexical rule returns an implicit token type");
        }
        else {
            this.println("Return value: none");
        }
        if (block.argAction != null) {
            this.println("Arguments: " + block.argAction);
        }
        this.genBlockPreamble(block);
        if (!this.grammar.theLLkAnalyzer.deterministic(block)) {
            this.println("Error: This rule is non-deterministic");
        }
        this.genCommonBlock(block);
        final ExceptionSpec exceptionSpec = block.findExceptionSpec("");
        if (exceptionSpec != null) {
            this.println("You specified error-handler(s) for this rule:");
            ++this.tabs;
            for (int i = 0; i < exceptionSpec.handlers.size(); ++i) {
                if (i != 0) {
                    this.println("");
                }
                final ExceptionHandler exceptionHandler = (ExceptionHandler)exceptionSpec.handlers.elementAt(i);
                this.println("Error-handler(" + (i + 1) + ") catches [" + exceptionHandler.exceptionTypeAndName.getText() + "] and executes:");
                this.printAction(exceptionHandler.action.getText());
            }
            --this.tabs;
            this.println("End error-handlers.");
        }
        else if (!this.doingLexRules) {
            this.println("Default error-handling will be generated, which catches all");
            this.println("parser exceptions and consumes tokens until the follow-set is seen.");
        }
        if (!this.doingLexRules) {
            this.println("The follow set for this rule is:");
            ++this.tabs;
            this.genFollowSetForRuleBlock(block);
            --this.tabs;
        }
        --this.tabs;
        this.println("*** End " + str + " Rule: " + ruleSymbol.getId());
    }
    
    protected void genSynPred(final SynPredBlock synPredBlock) {
        ++this.syntacticPredLevel;
        this.gen(synPredBlock);
        --this.syntacticPredLevel;
    }
    
    protected void genTokenTypes(final TokenManager tokenManager) throws IOException {
        this.antlrTool.reportProgress("Generating " + tokenManager.getName() + DiagnosticCodeGenerator.TokenTypesFileSuffix + DiagnosticCodeGenerator.TokenTypesFileExt);
        this.currentOutput = this.antlrTool.openOutputFile(tokenManager.getName() + DiagnosticCodeGenerator.TokenTypesFileSuffix + DiagnosticCodeGenerator.TokenTypesFileExt);
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
        return "***Create an AST from a vector here***" + System.getProperty("line.separator");
    }
    
    public String getASTCreateString(final GrammarAtom grammarAtom, final String str) {
        return "[" + str + "]";
    }
    
    protected String processActionForSpecialSymbols(final String s, final int n, final RuleBlock ruleBlock, final ActionTransInfo actionTransInfo) {
        return s;
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
