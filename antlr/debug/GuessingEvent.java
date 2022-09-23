// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

public abstract class GuessingEvent extends Event
{
    private int guessing;
    
    public GuessingEvent(final Object o) {
        super(o);
    }
    
    public GuessingEvent(final Object o, final int n) {
        super(o, n);
    }
    
    public int getGuessing() {
        return this.guessing;
    }
    
    void setGuessing(final int guessing) {
        this.guessing = guessing;
    }
    
    void setValues(final int values, final int guessing) {
        super.setValues(values);
        this.setGuessing(guessing);
    }
}
