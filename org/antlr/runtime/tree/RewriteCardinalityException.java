// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime.tree;

public class RewriteCardinalityException extends RuntimeException
{
    public String elementDescription;
    
    public RewriteCardinalityException(final String elementDescription) {
        this.elementDescription = elementDescription;
    }
    
    public String getMessage() {
        if (this.elementDescription != null) {
            return this.elementDescription;
        }
        return null;
    }
}
