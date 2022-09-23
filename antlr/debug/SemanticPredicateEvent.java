// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

public class SemanticPredicateEvent extends GuessingEvent
{
    public static final int VALIDATING = 0;
    public static final int PREDICTING = 1;
    private int condition;
    private boolean result;
    
    public SemanticPredicateEvent(final Object o) {
        super(o);
    }
    
    public SemanticPredicateEvent(final Object o, final int n) {
        super(o, n);
    }
    
    public int getCondition() {
        return this.condition;
    }
    
    public boolean getResult() {
        return this.result;
    }
    
    void setCondition(final int condition) {
        this.condition = condition;
    }
    
    void setResult(final boolean result) {
        this.result = result;
    }
    
    void setValues(final int n, final int condition, final boolean result, final int n2) {
        super.setValues(n, n2);
        this.setCondition(condition);
        this.setResult(result);
    }
    
    public String toString() {
        return "SemanticPredicateEvent [" + this.getCondition() + "," + this.getResult() + "," + this.getGuessing() + "]";
    }
}
