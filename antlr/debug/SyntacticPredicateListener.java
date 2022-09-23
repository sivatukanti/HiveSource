// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

public interface SyntacticPredicateListener extends ListenerBase
{
    void syntacticPredicateFailed(final SyntacticPredicateEvent p0);
    
    void syntacticPredicateStarted(final SyntacticPredicateEvent p0);
    
    void syntacticPredicateSucceeded(final SyntacticPredicateEvent p0);
}
