// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

public class InputBufferEvent extends Event
{
    char c;
    int lookaheadAmount;
    public static final int CONSUME = 0;
    public static final int LA = 1;
    public static final int MARK = 2;
    public static final int REWIND = 3;
    
    public InputBufferEvent(final Object o) {
        super(o);
    }
    
    public InputBufferEvent(final Object o, final int n, final char c, final int n2) {
        super(o);
        this.setValues(n, c, n2);
    }
    
    public char getChar() {
        return this.c;
    }
    
    public int getLookaheadAmount() {
        return this.lookaheadAmount;
    }
    
    void setChar(final char c) {
        this.c = c;
    }
    
    void setLookaheadAmount(final int lookaheadAmount) {
        this.lookaheadAmount = lookaheadAmount;
    }
    
    void setValues(final int values, final char char1, final int lookaheadAmount) {
        super.setValues(values);
        this.setChar(char1);
        this.setLookaheadAmount(lookaheadAmount);
    }
    
    public String toString() {
        return "CharBufferEvent [" + ((this.getType() == 0) ? "CONSUME, " : "LA, ") + this.getChar() + "," + this.getLookaheadAmount() + "]";
    }
}
