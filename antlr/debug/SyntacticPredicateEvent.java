// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

public class SyntacticPredicateEvent extends GuessingEvent
{
    public SyntacticPredicateEvent(final Object o) {
        super(o);
    }
    
    public SyntacticPredicateEvent(final Object o, final int n) {
        super(o, n);
    }
    
    void setValues(final int n, final int n2) {
        super.setValues(n, n2);
    }
    
    public String toString() {
        return "SyntacticPredicateEvent [" + this.getGuessing() + "]";
    }
}
