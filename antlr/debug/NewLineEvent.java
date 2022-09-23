// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

public class NewLineEvent extends Event
{
    private int line;
    
    public NewLineEvent(final Object o) {
        super(o);
    }
    
    public NewLineEvent(final Object o, final int values) {
        super(o);
        this.setValues(values);
    }
    
    public int getLine() {
        return this.line;
    }
    
    void setLine(final int line) {
        this.line = line;
    }
    
    void setValues(final int line) {
        this.setLine(line);
    }
    
    public String toString() {
        return "NewLineEvent [" + this.line + "]";
    }
}
