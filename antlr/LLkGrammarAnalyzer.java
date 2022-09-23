// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public interface LLkGrammarAnalyzer extends GrammarAnalyzer
{
    boolean deterministic(final AlternativeBlock p0);
    
    boolean deterministic(final OneOrMoreBlock p0);
    
    boolean deterministic(final ZeroOrMoreBlock p0);
    
    Lookahead FOLLOW(final int p0, final RuleEndElement p1);
    
    Lookahead look(final int p0, final ActionElement p1);
    
    Lookahead look(final int p0, final AlternativeBlock p1);
    
    Lookahead look(final int p0, final BlockEndElement p1);
    
    Lookahead look(final int p0, final CharLiteralElement p1);
    
    Lookahead look(final int p0, final CharRangeElement p1);
    
    Lookahead look(final int p0, final GrammarAtom p1);
    
    Lookahead look(final int p0, final OneOrMoreBlock p1);
    
    Lookahead look(final int p0, final RuleBlock p1);
    
    Lookahead look(final int p0, final RuleEndElement p1);
    
    Lookahead look(final int p0, final RuleRefElement p1);
    
    Lookahead look(final int p0, final StringLiteralElement p1);
    
    Lookahead look(final int p0, final SynPredBlock p1);
    
    Lookahead look(final int p0, final TokenRangeElement p1);
    
    Lookahead look(final int p0, final TreeElement p1);
    
    Lookahead look(final int p0, final WildcardElement p1);
    
    Lookahead look(final int p0, final ZeroOrMoreBlock p1);
    
    Lookahead look(final int p0, final String p1);
    
    void setGrammar(final Grammar p0);
    
    boolean subruleCanBeInverted(final AlternativeBlock p0, final boolean p1);
}
