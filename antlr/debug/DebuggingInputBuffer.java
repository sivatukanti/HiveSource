// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

import java.util.Vector;
import antlr.CharStreamException;
import antlr.InputBuffer;

public class DebuggingInputBuffer extends InputBuffer
{
    private InputBuffer buffer;
    private InputBufferEventSupport inputBufferEventSupport;
    private boolean debugMode;
    
    public DebuggingInputBuffer(final InputBuffer buffer) {
        this.debugMode = true;
        this.buffer = buffer;
        this.inputBufferEventSupport = new InputBufferEventSupport(this);
    }
    
    public void addInputBufferListener(final InputBufferListener inputBufferListener) {
        this.inputBufferEventSupport.addInputBufferListener(inputBufferListener);
    }
    
    public void consume() {
        char la = ' ';
        try {
            la = this.buffer.LA(1);
        }
        catch (CharStreamException ex) {}
        this.buffer.consume();
        if (this.debugMode) {
            this.inputBufferEventSupport.fireConsume(la);
        }
    }
    
    public void fill(final int n) throws CharStreamException {
        this.buffer.fill(n);
    }
    
    public Vector getInputBufferListeners() {
        return this.inputBufferEventSupport.getInputBufferListeners();
    }
    
    public boolean isDebugMode() {
        return this.debugMode;
    }
    
    public boolean isMarked() {
        return this.buffer.isMarked();
    }
    
    public char LA(final int n) throws CharStreamException {
        final char la = this.buffer.LA(n);
        if (this.debugMode) {
            this.inputBufferEventSupport.fireLA(la, n);
        }
        return la;
    }
    
    public int mark() {
        final int mark = this.buffer.mark();
        this.inputBufferEventSupport.fireMark(mark);
        return mark;
    }
    
    public void removeInputBufferListener(final InputBufferListener inputBufferListener) {
        if (this.inputBufferEventSupport != null) {
            this.inputBufferEventSupport.removeInputBufferListener(inputBufferListener);
        }
    }
    
    public void rewind(final int n) {
        this.buffer.rewind(n);
        this.inputBufferEventSupport.fireRewind(n);
    }
    
    public void setDebugMode(final boolean debugMode) {
        this.debugMode = debugMode;
    }
}
