// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.header;

import java.text.ParseException;
import com.sun.jersey.core.header.reader.HttpHeaderReader;

public class Token
{
    protected String token;
    
    protected Token() {
    }
    
    public Token(final String header) throws ParseException {
        this(HttpHeaderReader.newInstance(header));
    }
    
    public Token(final HttpHeaderReader reader) throws ParseException {
        reader.hasNext();
        this.token = reader.nextToken();
        if (reader.hasNext()) {
            throw new ParseException("Invalid token", reader.getIndex());
        }
    }
    
    public String getToken() {
        return this.token;
    }
    
    public final boolean isCompatible(final String token) {
        return this.token.equals("*") || this.token.equals(token);
    }
}
