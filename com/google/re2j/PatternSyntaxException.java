// 
// Decompiled by Procyon v0.5.36
// 

package com.google.re2j;

public class PatternSyntaxException extends RuntimeException
{
    private final String error;
    private final String input;
    
    public PatternSyntaxException(final String error, final String input) {
        super("error parsing regexp: " + error + ": `" + input + "`");
        this.error = error;
        this.input = input;
    }
    
    public PatternSyntaxException(final String error) {
        super("error parsing regexp: " + error);
        this.error = error;
        this.input = "";
    }
    
    public int getIndex() {
        return -1;
    }
    
    public String getDescription() {
        return this.error;
    }
    
    public String getPattern() {
        return this.input;
    }
}
