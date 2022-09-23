// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.reader;

public class JsonFormatException extends RuntimeException
{
    private final String text;
    private final int line;
    private final int column;
    
    public JsonFormatException(final String text, final int line, final int column, final String message) {
        super(message);
        this.text = text;
        this.line = line + 1;
        this.column = column + 1;
    }
    
    public String getErrorToken() {
        return this.text;
    }
    
    public int getErrorLine() {
        return this.line;
    }
    
    public int getErrorColumn() {
        return this.column;
    }
    
    @Override
    public String toString() {
        return "JsonFormatException{text=" + this.text + ", line=" + this.line + ", column=" + this.column + '}';
    }
}
