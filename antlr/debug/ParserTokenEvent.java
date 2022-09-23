// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

public class ParserTokenEvent extends Event
{
    private int value;
    private int amount;
    public static int LA;
    public static int CONSUME;
    
    public ParserTokenEvent(final Object o) {
        super(o);
    }
    
    public ParserTokenEvent(final Object o, final int n, final int n2, final int n3) {
        super(o);
        this.setValues(n, n2, n3);
    }
    
    public int getAmount() {
        return this.amount;
    }
    
    public int getValue() {
        return this.value;
    }
    
    void setAmount(final int amount) {
        this.amount = amount;
    }
    
    void setValue(final int value) {
        this.value = value;
    }
    
    void setValues(final int values, final int amount, final int value) {
        super.setValues(values);
        this.setAmount(amount);
        this.setValue(value);
    }
    
    public String toString() {
        if (this.getType() == ParserTokenEvent.LA) {
            return "ParserTokenEvent [LA," + this.getAmount() + "," + this.getValue() + "]";
        }
        return "ParserTokenEvent [consume,1," + this.getValue() + "]";
    }
    
    static {
        ParserTokenEvent.LA = 0;
        ParserTokenEvent.CONSUME = 1;
    }
}
