// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.Vector;

class ExceptionSpec
{
    protected Token label;
    protected Vector handlers;
    
    public ExceptionSpec(final Token label) {
        this.label = label;
        this.handlers = new Vector();
    }
    
    public void addHandler(final ExceptionHandler exceptionHandler) {
        this.handlers.appendElement(exceptionHandler);
    }
}
