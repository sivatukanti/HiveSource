// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

public class MessageEvent extends Event
{
    private String text;
    public static int WARNING;
    public static int ERROR;
    
    public MessageEvent(final Object o) {
        super(o);
    }
    
    public MessageEvent(final Object o, final int n, final String s) {
        super(o);
        this.setValues(n, s);
    }
    
    public String getText() {
        return this.text;
    }
    
    void setText(final String text) {
        this.text = text;
    }
    
    void setValues(final int values, final String text) {
        super.setValues(values);
        this.setText(text);
    }
    
    public String toString() {
        return "ParserMessageEvent [" + ((this.getType() == MessageEvent.WARNING) ? "warning," : "error,") + this.getText() + "]";
    }
    
    static {
        MessageEvent.WARNING = 0;
        MessageEvent.ERROR = 1;
    }
}
