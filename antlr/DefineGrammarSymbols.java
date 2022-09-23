// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.BitSet;
import java.util.Hashtable;

public class DefineGrammarSymbols implements ANTLRGrammarParseBehavior
{
    protected Hashtable grammars;
    protected Hashtable tokenManagers;
    protected Grammar grammar;
    protected Tool tool;
    LLkAnalyzer analyzer;
    String[] args;
    static final String DEFAULT_TOKENMANAGER_NAME = "*default";
    protected Hashtable headerActions;
    Token thePreambleAction;
    String language;
    protected int numLexers;
    protected int numParsers;
    protected int numTreeParsers;
    
    public DefineGrammarSymbols(final Tool tool, final String[] args, final LLkAnalyzer analyzer) {
        this.grammars = new Hashtable();
        this.tokenManagers = new Hashtable();
        this.headerActions = new Hashtable();
        this.thePreambleAction = new CommonToken(0, "");
        this.language = "Java";
        this.numLexers = 0;
        this.numParsers = 0;
        this.numTreeParsers = 0;
        this.tool = tool;
        this.args = args;
        this.analyzer = analyzer;
    }
    
    public void _refStringLiteral(final Token token, final Token token2, final int n, final boolean b) {
        if (!(this.grammar instanceof LexerGrammar)) {
            final String text = token.getText();
            if (this.grammar.tokenManager.getTokenSymbol(text) != null) {
                return;
            }
            final StringLiteralSymbol stringLiteralSymbol = new StringLiteralSymbol(text);
            stringLiteralSymbol.setTokenType(this.grammar.tokenManager.nextTokenType());
            this.grammar.tokenManager.define(stringLiteralSymbol);
        }
    }
    
    public void _refToken(final Token token, final Token token2, final Token token3, final Token token4, final boolean b, final int n, final boolean b2) {
        final String text = token2.getText();
        if (!this.grammar.tokenManager.tokenDefined(text)) {
            final int nextTokenType = this.grammar.tokenManager.nextTokenType();
            final TokenSymbol tokenSymbol = new TokenSymbol(text);
            tokenSymbol.setTokenType(nextTokenType);
            this.grammar.tokenManager.define(tokenSymbol);
        }
    }
    
    public void abortGrammar() {
        if (this.grammar != null && this.grammar.getClassName() != null) {
            this.grammars.remove(this.grammar.getClassName());
        }
        this.grammar = null;
    }
    
    public void beginAlt(final boolean b) {
    }
    
    public void beginChildList() {
    }
    
    public void beginExceptionGroup() {
    }
    
    public void beginExceptionSpec(final Token token) {
    }
    
    public void beginSubRule(final Token token, final Token token2, final boolean b) {
    }
    
    public void beginTree(final Token token) throws SemanticException {
    }
    
    public void defineRuleName(final Token token, final String access, final boolean b, final String comment) throws SemanticException {
        String str = token.getText();
        if (token.type == 24) {
            str = CodeGenerator.encodeLexerRuleName(str);
            if (!this.grammar.tokenManager.tokenDefined(token.getText())) {
                final int nextTokenType = this.grammar.tokenManager.nextTokenType();
                final TokenSymbol tokenSymbol = new TokenSymbol(token.getText());
                tokenSymbol.setTokenType(nextTokenType);
                this.grammar.tokenManager.define(tokenSymbol);
            }
        }
        RuleSymbol ruleSymbol;
        if (this.grammar.isDefined(str)) {
            ruleSymbol = (RuleSymbol)this.grammar.getSymbol(str);
            if (ruleSymbol.isDefined()) {
                this.tool.error("redefinition of rule " + str, this.grammar.getFilename(), token.getLine(), token.getColumn());
            }
        }
        else {
            ruleSymbol = new RuleSymbol(str);
            this.grammar.define(ruleSymbol);
        }
        ruleSymbol.setDefined();
        ruleSymbol.access = access;
        ruleSymbol.comment = comment;
    }
    
    public void defineToken(final Token token, final Token token2) {
        String text = null;
        String text2 = null;
        if (token != null) {
            text = token.getText();
        }
        if (token2 != null) {
            text2 = token2.getText();
        }
        if (text2 != null) {
            final StringLiteralSymbol stringLiteralSymbol = (StringLiteralSymbol)this.grammar.tokenManager.getTokenSymbol(text2);
            if (stringLiteralSymbol != null) {
                if (text == null || stringLiteralSymbol.getLabel() != null) {
                    this.tool.warning("Redefinition of literal in tokens {...}: " + text2, this.grammar.getFilename(), token2.getLine(), token2.getColumn());
                    return;
                }
                if (text != null) {
                    stringLiteralSymbol.setLabel(text);
                    this.grammar.tokenManager.mapToTokenSymbol(text, stringLiteralSymbol);
                }
            }
            if (text != null) {
                final TokenSymbol tokenSymbol = this.grammar.tokenManager.getTokenSymbol(text);
                if (tokenSymbol != null) {
                    if (tokenSymbol instanceof StringLiteralSymbol) {
                        this.tool.warning("Redefinition of token in tokens {...}: " + text, this.grammar.getFilename(), token2.getLine(), token2.getColumn());
                        return;
                    }
                    final int tokenType = tokenSymbol.getTokenType();
                    final StringLiteralSymbol stringLiteralSymbol2 = new StringLiteralSymbol(text2);
                    stringLiteralSymbol2.setTokenType(tokenType);
                    stringLiteralSymbol2.setLabel(text);
                    this.grammar.tokenManager.define(stringLiteralSymbol2);
                    this.grammar.tokenManager.mapToTokenSymbol(text, stringLiteralSymbol2);
                    return;
                }
            }
            final StringLiteralSymbol stringLiteralSymbol3 = new StringLiteralSymbol(text2);
            stringLiteralSymbol3.setTokenType(this.grammar.tokenManager.nextTokenType());
            stringLiteralSymbol3.setLabel(text);
            this.grammar.tokenManager.define(stringLiteralSymbol3);
            if (text != null) {
                this.grammar.tokenManager.mapToTokenSymbol(text, stringLiteralSymbol3);
            }
        }
        else {
            if (this.grammar.tokenManager.tokenDefined(text)) {
                this.tool.warning("Redefinition of token in tokens {...}: " + text, this.grammar.getFilename(), token.getLine(), token.getColumn());
                return;
            }
            final int nextTokenType = this.grammar.tokenManager.nextTokenType();
            final TokenSymbol tokenSymbol2 = new TokenSymbol(text);
            tokenSymbol2.setTokenType(nextTokenType);
            this.grammar.tokenManager.define(tokenSymbol2);
        }
    }
    
    public void endAlt() {
    }
    
    public void endChildList() {
    }
    
    public void endExceptionGroup() {
    }
    
    public void endExceptionSpec() {
    }
    
    public void endGrammar() {
    }
    
    public void endOptions() {
        if (this.grammar.exportVocab == null && this.grammar.importVocab == null) {
            this.grammar.exportVocab = this.grammar.getClassName();
            if (this.tokenManagers.containsKey("*default")) {
                this.grammar.exportVocab = "*default";
                this.grammar.setTokenManager(this.tokenManagers.get("*default"));
                return;
            }
            final SimpleTokenManager value = new SimpleTokenManager(this.grammar.exportVocab, this.tool);
            this.grammar.setTokenManager(value);
            this.tokenManagers.put(this.grammar.exportVocab, value);
            this.tokenManagers.put("*default", value);
        }
        else if (this.grammar.exportVocab == null && this.grammar.importVocab != null) {
            this.grammar.exportVocab = this.grammar.getClassName();
            if (this.grammar.importVocab.equals(this.grammar.exportVocab)) {
                this.tool.warning("Grammar " + this.grammar.getClassName() + " cannot have importVocab same as default output vocab (grammar name); ignored.");
                this.grammar.importVocab = null;
                this.endOptions();
                return;
            }
            if (this.tokenManagers.containsKey(this.grammar.importVocab)) {
                final TokenManager tokenManager = (TokenManager)this.tokenManagers.get(this.grammar.importVocab).clone();
                tokenManager.setName(this.grammar.exportVocab);
                tokenManager.setReadOnly(false);
                this.grammar.setTokenManager(tokenManager);
                this.tokenManagers.put(this.grammar.exportVocab, tokenManager);
                return;
            }
            final ImportVocabTokenManager value2 = new ImportVocabTokenManager(this.grammar, this.grammar.importVocab + CodeGenerator.TokenTypesFileSuffix + CodeGenerator.TokenTypesFileExt, this.grammar.exportVocab, this.tool);
            value2.setReadOnly(false);
            this.tokenManagers.put(this.grammar.exportVocab, value2);
            this.grammar.setTokenManager(value2);
            if (!this.tokenManagers.containsKey("*default")) {
                this.tokenManagers.put("*default", value2);
            }
        }
        else if (this.grammar.exportVocab != null && this.grammar.importVocab == null) {
            if (this.tokenManagers.containsKey(this.grammar.exportVocab)) {
                this.grammar.setTokenManager(this.tokenManagers.get(this.grammar.exportVocab));
                return;
            }
            final SimpleTokenManager value3 = new SimpleTokenManager(this.grammar.exportVocab, this.tool);
            this.grammar.setTokenManager(value3);
            this.tokenManagers.put(this.grammar.exportVocab, value3);
            if (!this.tokenManagers.containsKey("*default")) {
                this.tokenManagers.put("*default", value3);
            }
        }
        else {
            if (this.grammar.exportVocab == null || this.grammar.importVocab == null) {
                return;
            }
            if (this.grammar.importVocab.equals(this.grammar.exportVocab)) {
                this.tool.error("exportVocab of " + this.grammar.exportVocab + " same as importVocab; probably not what you want");
            }
            if (this.tokenManagers.containsKey(this.grammar.importVocab)) {
                final TokenManager tokenManager2 = (TokenManager)this.tokenManagers.get(this.grammar.importVocab).clone();
                tokenManager2.setName(this.grammar.exportVocab);
                tokenManager2.setReadOnly(false);
                this.grammar.setTokenManager(tokenManager2);
                this.tokenManagers.put(this.grammar.exportVocab, tokenManager2);
                return;
            }
            final ImportVocabTokenManager value4 = new ImportVocabTokenManager(this.grammar, this.grammar.importVocab + CodeGenerator.TokenTypesFileSuffix + CodeGenerator.TokenTypesFileExt, this.grammar.exportVocab, this.tool);
            value4.setReadOnly(false);
            this.tokenManagers.put(this.grammar.exportVocab, value4);
            this.grammar.setTokenManager(value4);
            if (!this.tokenManagers.containsKey("*default")) {
                this.tokenManagers.put("*default", value4);
            }
        }
    }
    
    public void endRule(final String s) {
    }
    
    public void endSubRule() {
    }
    
    public void endTree() {
    }
    
    public void hasError() {
    }
    
    public void noASTSubRule() {
    }
    
    public void oneOrMoreSubRule() {
    }
    
    public void optionalSubRule() {
    }
    
    public void setUserExceptions(final String s) {
    }
    
    public void refAction(final Token token) {
    }
    
    public void refArgAction(final Token token) {
    }
    
    public void refCharLiteral(final Token token, final Token token2, final boolean b, final int n, final boolean b2) {
    }
    
    public void refCharRange(final Token token, final Token token2, final Token token3, final int n, final boolean b) {
    }
    
    public void refElementOption(final Token token, final Token token2) {
    }
    
    public void refTokensSpecElementOption(final Token token, final Token token2, final Token token3) {
    }
    
    public void refExceptionHandler(final Token token, final Token token2) {
    }
    
    public void refHeaderAction(final Token token, final Token value) {
        String stripFrontBack;
        if (token == null) {
            stripFrontBack = "";
        }
        else {
            stripFrontBack = StringUtils.stripFrontBack(token.getText(), "\"", "\"");
        }
        if (this.headerActions.containsKey(stripFrontBack)) {
            if (stripFrontBack.equals("")) {
                this.tool.error(value.getLine() + ": header action already defined");
            }
            else {
                this.tool.error(value.getLine() + ": header action '" + stripFrontBack + "' already defined");
            }
        }
        this.headerActions.put(stripFrontBack, value);
    }
    
    public String getHeaderAction(final String key) {
        final Token token = this.headerActions.get(key);
        if (token == null) {
            return "";
        }
        return token.getText();
    }
    
    public int getHeaderActionLine(final String key) {
        final Token token = this.headerActions.get(key);
        if (token == null) {
            return 0;
        }
        return token.getLine();
    }
    
    public void refInitAction(final Token token) {
    }
    
    public void refMemberAction(final Token token) {
    }
    
    public void refPreambleAction(final Token thePreambleAction) {
        this.thePreambleAction = thePreambleAction;
    }
    
    public void refReturnAction(final Token token) {
    }
    
    public void refRule(final Token token, final Token token2, final Token token3, final Token token4, final int n) {
        String s = token2.getText();
        if (token2.type == 24) {
            s = CodeGenerator.encodeLexerRuleName(s);
        }
        if (!this.grammar.isDefined(s)) {
            this.grammar.define(new RuleSymbol(s));
        }
    }
    
    public void refSemPred(final Token token) {
    }
    
    public void refStringLiteral(final Token token, final Token token2, final int n, final boolean b) {
        this._refStringLiteral(token, token2, n, b);
    }
    
    public void refToken(final Token token, final Token token2, final Token token3, final Token token4, final boolean b, final int n, final boolean b2) {
        this._refToken(token, token2, token3, token4, b, n, b2);
    }
    
    public void refTokenRange(final Token token, final Token token2, final Token token3, final int n, final boolean b) {
        if (token.getText().charAt(0) == '\"') {
            this.refStringLiteral(token, null, 1, b);
        }
        else {
            this._refToken(null, token, null, null, false, 1, b);
        }
        if (token2.getText().charAt(0) == '\"') {
            this._refStringLiteral(token2, null, 1, b);
        }
        else {
            this._refToken(null, token2, null, null, false, 1, b);
        }
    }
    
    public void refTreeSpecifier(final Token token) {
    }
    
    public void refWildcard(final Token token, final Token token2, final int n) {
    }
    
    public void reset() {
        this.grammar = null;
    }
    
    public void setArgOfRuleRef(final Token token) {
    }
    
    public void setCharVocabulary(final BitSet charVocabulary) {
        ((LexerGrammar)this.grammar).setCharVocabulary(charVocabulary);
    }
    
    public void setFileOption(final Token token, final Token token2, final String s) {
        if (token.getText().equals("language")) {
            if (token2.getType() == 6) {
                this.language = StringUtils.stripBack(StringUtils.stripFront(token2.getText(), '\"'), '\"');
            }
            else if (token2.getType() == 24 || token2.getType() == 41) {
                this.language = token2.getText();
            }
            else {
                this.tool.error("language option must be string or identifier", s, token2.getLine(), token2.getColumn());
            }
        }
        else if (token.getText().equals("mangleLiteralPrefix")) {
            if (token2.getType() == 6) {
                this.tool.literalsPrefix = StringUtils.stripFrontBack(token2.getText(), "\"", "\"");
            }
            else {
                this.tool.error("mangleLiteralPrefix option must be string", s, token2.getLine(), token2.getColumn());
            }
        }
        else if (token.getText().equals("upperCaseMangledLiterals")) {
            if (token2.getText().equals("true")) {
                this.tool.upperCaseMangledLiterals = true;
            }
            else if (token2.getText().equals("false")) {
                this.tool.upperCaseMangledLiterals = false;
            }
            else {
                this.grammar.antlrTool.error("Value for upperCaseMangledLiterals must be true or false", s, token.getLine(), token.getColumn());
            }
        }
        else if (token.getText().equals("namespaceStd") || token.getText().equals("namespaceAntlr") || token.getText().equals("genHashLines")) {
            if (!this.language.equals("Cpp")) {
                this.tool.error(token.getText() + " option only valid for C++", s, token.getLine(), token.getColumn());
            }
            else if (token.getText().equals("noConstructors")) {
                if (!token2.getText().equals("true") && !token2.getText().equals("false")) {
                    this.tool.error("noConstructors option must be true or false", s, token2.getLine(), token2.getColumn());
                }
                this.tool.noConstructors = token2.getText().equals("true");
            }
            else if (token.getText().equals("genHashLines")) {
                if (!token2.getText().equals("true") && !token2.getText().equals("false")) {
                    this.tool.error("genHashLines option must be true or false", s, token2.getLine(), token2.getColumn());
                }
                this.tool.genHashLines = token2.getText().equals("true");
            }
            else if (token2.getType() != 6) {
                this.tool.error(token.getText() + " option must be a string", s, token2.getLine(), token2.getColumn());
            }
            else if (token.getText().equals("namespaceStd")) {
                this.tool.namespaceStd = token2.getText();
            }
            else if (token.getText().equals("namespaceAntlr")) {
                this.tool.namespaceAntlr = token2.getText();
            }
        }
        else if (token.getText().equals("namespace")) {
            if (!this.language.equals("Cpp") && !this.language.equals("CSharp")) {
                this.tool.error(token.getText() + " option only valid for C++ and C# (a.k.a CSharp)", s, token.getLine(), token.getColumn());
            }
            else if (token2.getType() != 6) {
                this.tool.error(token.getText() + " option must be a string", s, token2.getLine(), token2.getColumn());
            }
            else if (token.getText().equals("namespace")) {
                this.tool.setNameSpace(token2.getText());
            }
        }
        else {
            this.tool.error("Invalid file-level option: " + token.getText(), s, token.getLine(), token2.getColumn());
        }
    }
    
    public void setGrammarOption(final Token token, final Token token2) {
        if (token.getText().equals("tokdef") || token.getText().equals("tokenVocabulary")) {
            this.tool.error("tokdef/tokenVocabulary options are invalid >= ANTLR 2.6.0.\n  Use importVocab/exportVocab instead.  Please see the documentation.\n  The previous options were so heinous that Terence changed the whole\n  vocabulary mechanism; it was better to change the names rather than\n  subtly change the functionality of the known options.  Sorry!", this.grammar.getFilename(), token2.getLine(), token2.getColumn());
        }
        else if (token.getText().equals("literal") && this.grammar instanceof LexerGrammar) {
            this.tool.error("the literal option is invalid >= ANTLR 2.6.0.\n  Use the \"tokens {...}\" mechanism instead.", this.grammar.getFilename(), token2.getLine(), token2.getColumn());
        }
        else if (token.getText().equals("exportVocab")) {
            if (token2.getType() == 41 || token2.getType() == 24) {
                this.grammar.exportVocab = token2.getText();
            }
            else {
                this.tool.error("exportVocab must be an identifier", this.grammar.getFilename(), token2.getLine(), token2.getColumn());
            }
        }
        else if (token.getText().equals("importVocab")) {
            if (token2.getType() == 41 || token2.getType() == 24) {
                this.grammar.importVocab = token2.getText();
            }
            else {
                this.tool.error("importVocab must be an identifier", this.grammar.getFilename(), token2.getLine(), token2.getColumn());
            }
        }
        else if (token.getText().equals("k")) {
            if (this.grammar instanceof TreeWalkerGrammar && !token2.getText().equals("1")) {
                this.tool.error("Treewalkers only support k=1", this.grammar.getFilename(), token2.getLine(), token2.getColumn());
            }
            else {
                this.grammar.setOption(token.getText(), token2);
            }
        }
        else {
            this.grammar.setOption(token.getText(), token2);
        }
    }
    
    public void setRuleOption(final Token token, final Token token2) {
    }
    
    public void setSubruleOption(final Token token, final Token token2) {
    }
    
    public void startLexer(final String filename, final Token key, final String s, final String comment) {
        if (this.numLexers > 0) {
            this.tool.panic("You may only have one lexer per grammar file: class " + key.getText());
        }
        ++this.numLexers;
        this.reset();
        final Grammar grammar = this.grammars.get(key);
        if (grammar != null) {
            if (!(grammar instanceof LexerGrammar)) {
                this.tool.panic("'" + key.getText() + "' is already defined as a non-lexer");
            }
            else {
                this.tool.panic("Lexer '" + key.getText() + "' is already defined");
            }
        }
        else {
            final LexerGrammar lexerGrammar = new LexerGrammar(key.getText(), this.tool, s);
            lexerGrammar.comment = comment;
            lexerGrammar.processArguments(this.args);
            lexerGrammar.setFilename(filename);
            this.grammars.put(lexerGrammar.getClassName(), lexerGrammar);
            lexerGrammar.preambleAction = this.thePreambleAction;
            this.thePreambleAction = new CommonToken(0, "");
            this.grammar = lexerGrammar;
        }
    }
    
    public void startParser(final String filename, final Token key, final String s, final String comment) {
        if (this.numParsers > 0) {
            this.tool.panic("You may only have one parser per grammar file: class " + key.getText());
        }
        ++this.numParsers;
        this.reset();
        final Grammar grammar = this.grammars.get(key);
        if (grammar != null) {
            if (!(grammar instanceof ParserGrammar)) {
                this.tool.panic("'" + key.getText() + "' is already defined as a non-parser");
            }
            else {
                this.tool.panic("Parser '" + key.getText() + "' is already defined");
            }
        }
        else {
            this.grammar = new ParserGrammar(key.getText(), this.tool, s);
            this.grammar.comment = comment;
            this.grammar.processArguments(this.args);
            this.grammar.setFilename(filename);
            this.grammars.put(this.grammar.getClassName(), this.grammar);
            this.grammar.preambleAction = this.thePreambleAction;
            this.thePreambleAction = new CommonToken(0, "");
        }
    }
    
    public void startTreeWalker(final String filename, final Token key, final String s, final String comment) {
        if (this.numTreeParsers > 0) {
            this.tool.panic("You may only have one tree parser per grammar file: class " + key.getText());
        }
        ++this.numTreeParsers;
        this.reset();
        final Grammar grammar = this.grammars.get(key);
        if (grammar != null) {
            if (!(grammar instanceof TreeWalkerGrammar)) {
                this.tool.panic("'" + key.getText() + "' is already defined as a non-tree-walker");
            }
            else {
                this.tool.panic("Tree-walker '" + key.getText() + "' is already defined");
            }
        }
        else {
            this.grammar = new TreeWalkerGrammar(key.getText(), this.tool, s);
            this.grammar.comment = comment;
            this.grammar.processArguments(this.args);
            this.grammar.setFilename(filename);
            this.grammars.put(this.grammar.getClassName(), this.grammar);
            this.grammar.preambleAction = this.thePreambleAction;
            this.thePreambleAction = new CommonToken(0, "");
        }
    }
    
    public void synPred() {
    }
    
    public void zeroOrMoreSubRule() {
    }
}
