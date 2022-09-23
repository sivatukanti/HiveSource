// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

public class Tracer extends TraceAdapter implements TraceListener
{
    String indent;
    
    public Tracer() {
        this.indent = "";
    }
    
    protected void dedent() {
        if (this.indent.length() < 2) {
            this.indent = "";
        }
        else {
            this.indent = this.indent.substring(2);
        }
    }
    
    public void enterRule(final TraceEvent obj) {
        System.out.println(this.indent + obj);
        this.indent();
    }
    
    public void exitRule(final TraceEvent obj) {
        this.dedent();
        System.out.println(this.indent + obj);
    }
    
    protected void indent() {
        this.indent += "  ";
    }
}
