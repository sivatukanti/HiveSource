// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.BitSet;

public interface ANTLRGrammarParseBehavior
{
    void abortGrammar();
    
    void beginAlt(final boolean p0);
    
    void beginChildList();
    
    void beginExceptionGroup();
    
    void beginExceptionSpec(final Token p0);
    
    void beginSubRule(final Token p0, final Token p1, final boolean p2);
    
    void beginTree(final Token p0) throws SemanticException;
    
    void defineRuleName(final Token p0, final String p1, final boolean p2, final String p3) throws SemanticException;
    
    void defineToken(final Token p0, final Token p1);
    
    void endAlt();
    
    void endChildList();
    
    void endExceptionGroup();
    
    void endExceptionSpec();
    
    void endGrammar();
    
    void endOptions();
    
    void endRule(final String p0);
    
    void endSubRule();
    
    void endTree();
    
    void hasError();
    
    void noASTSubRule();
    
    void oneOrMoreSubRule();
    
    void optionalSubRule();
    
    void refAction(final Token p0);
    
    void refArgAction(final Token p0);
    
    void setUserExceptions(final String p0);
    
    void refCharLiteral(final Token p0, final Token p1, final boolean p2, final int p3, final boolean p4);
    
    void refCharRange(final Token p0, final Token p1, final Token p2, final int p3, final boolean p4);
    
    void refElementOption(final Token p0, final Token p1);
    
    void refTokensSpecElementOption(final Token p0, final Token p1, final Token p2);
    
    void refExceptionHandler(final Token p0, final Token p1);
    
    void refHeaderAction(final Token p0, final Token p1);
    
    void refInitAction(final Token p0);
    
    void refMemberAction(final Token p0);
    
    void refPreambleAction(final Token p0);
    
    void refReturnAction(final Token p0);
    
    void refRule(final Token p0, final Token p1, final Token p2, final Token p3, final int p4);
    
    void refSemPred(final Token p0);
    
    void refStringLiteral(final Token p0, final Token p1, final int p2, final boolean p3);
    
    void refToken(final Token p0, final Token p1, final Token p2, final Token p3, final boolean p4, final int p5, final boolean p6);
    
    void refTokenRange(final Token p0, final Token p1, final Token p2, final int p3, final boolean p4);
    
    void refTreeSpecifier(final Token p0);
    
    void refWildcard(final Token p0, final Token p1, final int p2);
    
    void setArgOfRuleRef(final Token p0);
    
    void setCharVocabulary(final BitSet p0);
    
    void setFileOption(final Token p0, final Token p1, final String p2);
    
    void setGrammarOption(final Token p0, final Token p1);
    
    void setRuleOption(final Token p0, final Token p1);
    
    void setSubruleOption(final Token p0, final Token p1);
    
    void startLexer(final String p0, final Token p1, final String p2, final String p3);
    
    void startParser(final String p0, final Token p1, final String p2, final String p3);
    
    void startTreeWalker(final String p0, final Token p1, final String p2, final String p3);
    
    void synPred();
    
    void zeroOrMoreSubRule();
}
