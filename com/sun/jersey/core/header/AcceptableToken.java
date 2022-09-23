// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.header;

import java.text.ParseException;
import com.sun.jersey.core.header.reader.HttpHeaderReader;

public class AcceptableToken extends Token implements QualityFactor
{
    protected int quality;
    
    public AcceptableToken(final String header) throws ParseException {
        this(HttpHeaderReader.newInstance(header));
    }
    
    public AcceptableToken(final HttpHeaderReader reader) throws ParseException {
        this.quality = 1000;
        reader.hasNext();
        this.token = reader.nextToken();
        if (reader.hasNext()) {
            this.quality = HttpHeaderReader.readQualityFactorParameter(reader);
        }
    }
    
    @Override
    public int getQuality() {
        return this.quality;
    }
}
