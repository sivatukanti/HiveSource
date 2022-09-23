// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.reader;

import org.codehaus.jackson.JsonLocation;
import javax.xml.stream.Location;

class StaxLocation implements Location
{
    private int charOffset;
    private int column;
    private int line;
    
    StaxLocation(final int charOffset, final int column, final int line) {
        this.charOffset = -1;
        this.column = -1;
        this.line = -1;
        this.charOffset = charOffset;
        this.column = column;
        this.line = line;
    }
    
    StaxLocation(final JsonLocation location) {
        this((int)location.getCharOffset(), location.getColumnNr(), location.getLineNr());
    }
    
    @Override
    public int getCharacterOffset() {
        return this.charOffset;
    }
    
    @Override
    public int getColumnNumber() {
        return this.column;
    }
    
    @Override
    public int getLineNumber() {
        return this.line;
    }
    
    @Override
    public String getPublicId() {
        return null;
    }
    
    @Override
    public String getSystemId() {
        return null;
    }
}
