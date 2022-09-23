// 
// Decompiled by Procyon v0.5.36
// 

package javax.servlet.jsp.tagext;

public class ValidationMessage
{
    private String id;
    private String message;
    
    public ValidationMessage(final String id, final String message) {
        this.id = id;
        this.message = message;
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getMessage() {
        return this.message;
    }
}
