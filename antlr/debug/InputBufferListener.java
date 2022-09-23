// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

public interface InputBufferListener extends ListenerBase
{
    void inputBufferConsume(final InputBufferEvent p0);
    
    void inputBufferLA(final InputBufferEvent p0);
    
    void inputBufferMark(final InputBufferEvent p0);
    
    void inputBufferRewind(final InputBufferEvent p0);
}
