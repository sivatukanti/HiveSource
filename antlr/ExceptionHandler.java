// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class ExceptionHandler
{
    protected Token exceptionTypeAndName;
    protected Token action;
    
    public ExceptionHandler(final Token exceptionTypeAndName, final Token action) {
        this.exceptionTypeAndName = exceptionTypeAndName;
        this.action = action;
    }
}
