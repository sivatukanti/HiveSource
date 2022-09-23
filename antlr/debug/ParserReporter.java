// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

public class ParserReporter extends Tracer implements ParserListener
{
    public void parserConsume(final ParserTokenEvent obj) {
        System.out.println(this.indent + obj);
    }
    
    public void parserLA(final ParserTokenEvent obj) {
        System.out.println(this.indent + obj);
    }
    
    public void parserMatch(final ParserMatchEvent obj) {
        System.out.println(this.indent + obj);
    }
    
    public void parserMatchNot(final ParserMatchEvent obj) {
        System.out.println(this.indent + obj);
    }
    
    public void parserMismatch(final ParserMatchEvent obj) {
        System.out.println(this.indent + obj);
    }
    
    public void parserMismatchNot(final ParserMatchEvent obj) {
        System.out.println(this.indent + obj);
    }
    
    public void reportError(final MessageEvent obj) {
        System.out.println(this.indent + obj);
    }
    
    public void reportWarning(final MessageEvent obj) {
        System.out.println(this.indent + obj);
    }
    
    public void semanticPredicateEvaluated(final SemanticPredicateEvent obj) {
        System.out.println(this.indent + obj);
    }
    
    public void syntacticPredicateFailed(final SyntacticPredicateEvent obj) {
        System.out.println(this.indent + obj);
    }
    
    public void syntacticPredicateStarted(final SyntacticPredicateEvent obj) {
        System.out.println(this.indent + obj);
    }
    
    public void syntacticPredicateSucceeded(final SyntacticPredicateEvent obj) {
        System.out.println(this.indent + obj);
    }
}
