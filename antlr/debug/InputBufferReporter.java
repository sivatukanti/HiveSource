// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

public class InputBufferReporter implements InputBufferListener
{
    public void doneParsing(final TraceEvent traceEvent) {
    }
    
    public void inputBufferChanged(final InputBufferEvent x) {
        System.out.println(x);
    }
    
    public void inputBufferConsume(final InputBufferEvent x) {
        System.out.println(x);
    }
    
    public void inputBufferLA(final InputBufferEvent x) {
        System.out.println(x);
    }
    
    public void inputBufferMark(final InputBufferEvent x) {
        System.out.println(x);
    }
    
    public void inputBufferRewind(final InputBufferEvent x) {
        System.out.println(x);
    }
    
    public void refresh() {
    }
}
