// 
// Decompiled by Procyon v0.5.36
// 

package antlr.debug;

import java.util.Vector;

public class InputBufferEventSupport
{
    private Object source;
    private Vector inputBufferListeners;
    private InputBufferEvent inputBufferEvent;
    protected static final int CONSUME = 0;
    protected static final int LA = 1;
    protected static final int MARK = 2;
    protected static final int REWIND = 3;
    
    public InputBufferEventSupport(final Object source) {
        this.inputBufferEvent = new InputBufferEvent(source);
        this.source = source;
    }
    
    public void addInputBufferListener(final InputBufferListener obj) {
        if (this.inputBufferListeners == null) {
            this.inputBufferListeners = new Vector();
        }
        this.inputBufferListeners.addElement(obj);
    }
    
    public void fireConsume(final char c) {
        this.inputBufferEvent.setValues(0, c, 0);
        this.fireEvents(0, this.inputBufferListeners);
    }
    
    public void fireEvent(final int i, final ListenerBase listenerBase) {
        switch (i) {
            case 0: {
                ((InputBufferListener)listenerBase).inputBufferConsume(this.inputBufferEvent);
                break;
            }
            case 1: {
                ((InputBufferListener)listenerBase).inputBufferLA(this.inputBufferEvent);
                break;
            }
            case 2: {
                ((InputBufferListener)listenerBase).inputBufferMark(this.inputBufferEvent);
                break;
            }
            case 3: {
                ((InputBufferListener)listenerBase).inputBufferRewind(this.inputBufferEvent);
                break;
            }
            default: {
                throw new IllegalArgumentException("bad type " + i + " for fireEvent()");
            }
        }
    }
    
    public void fireEvents(final int n, final Vector vector) {
        Vector<ListenerBase> vector2 = null;
        synchronized (this) {
            if (vector == null) {
                return;
            }
            vector2 = (Vector<ListenerBase>)vector.clone();
        }
        if (vector2 != null) {
            for (int i = 0; i < vector2.size(); ++i) {
                this.fireEvent(n, vector2.elementAt(i));
            }
        }
    }
    
    public void fireLA(final char c, final int n) {
        this.inputBufferEvent.setValues(1, c, n);
        this.fireEvents(1, this.inputBufferListeners);
    }
    
    public void fireMark(final int n) {
        this.inputBufferEvent.setValues(2, ' ', n);
        this.fireEvents(2, this.inputBufferListeners);
    }
    
    public void fireRewind(final int n) {
        this.inputBufferEvent.setValues(3, ' ', n);
        this.fireEvents(3, this.inputBufferListeners);
    }
    
    public Vector getInputBufferListeners() {
        return this.inputBufferListeners;
    }
    
    protected void refresh(final Vector vector) {
        final Vector vector2;
        synchronized (vector) {
            vector2 = (Vector)vector.clone();
        }
        if (vector2 != null) {
            for (int i = 0; i < vector2.size(); ++i) {
                vector2.elementAt(i).refresh();
            }
        }
    }
    
    public void refreshListeners() {
        this.refresh(this.inputBufferListeners);
    }
    
    public void removeInputBufferListener(final InputBufferListener obj) {
        if (this.inputBufferListeners != null) {
            this.inputBufferListeners.removeElement(obj);
        }
    }
}
