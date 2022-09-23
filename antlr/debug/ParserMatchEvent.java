// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

public class ParserMatchEvent extends GuessingEvent
{
    public static int TOKEN;
    public static int BITSET;
    public static int CHAR;
    public static int CHAR_BITSET;
    public static int STRING;
    public static int CHAR_RANGE;
    private boolean inverse;
    private boolean matched;
    private Object target;
    private int value;
    private String text;
    
    public ParserMatchEvent(final Object o) {
        super(o);
    }
    
    public ParserMatchEvent(final Object o, final int n, final int n2, final Object o2, final String s, final int n3, final boolean b, final boolean b2) {
        super(o);
        this.setValues(n, n2, o2, s, n3, b, b2);
    }
    
    public Object getTarget() {
        return this.target;
    }
    
    public String getText() {
        return this.text;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public boolean isInverse() {
        return this.inverse;
    }
    
    public boolean isMatched() {
        return this.matched;
    }
    
    void setInverse(final boolean inverse) {
        this.inverse = inverse;
    }
    
    void setMatched(final boolean matched) {
        this.matched = matched;
    }
    
    void setTarget(final Object target) {
        this.target = target;
    }
    
    void setText(final String text) {
        this.text = text;
    }
    
    void setValue(final int value) {
        this.value = value;
    }
    
    void setValues(final int n, final int value, final Object target, final String text, final int n2, final boolean inverse, final boolean matched) {
        super.setValues(n, n2);
        this.setValue(value);
        this.setTarget(target);
        this.setInverse(inverse);
        this.setMatched(matched);
        this.setText(text);
    }
    
    public String toString() {
        return "ParserMatchEvent [" + (this.isMatched() ? "ok," : "bad,") + (this.isInverse() ? "NOT " : "") + ((this.getType() == ParserMatchEvent.TOKEN) ? "token," : "bitset,") + this.getValue() + "," + this.getTarget() + "," + this.getGuessing() + "]";
    }
    
    static {
        ParserMatchEvent.TOKEN = 0;
        ParserMatchEvent.BITSET = 1;
        ParserMatchEvent.CHAR = 2;
        ParserMatchEvent.CHAR_BITSET = 3;
        ParserMatchEvent.STRING = 4;
        ParserMatchEvent.CHAR_RANGE = 5;
    }
}
