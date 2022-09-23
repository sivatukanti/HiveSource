// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.header;

import java.text.ParseException;
import com.sun.jersey.core.header.reader.HttpHeaderReader;

public class AcceptableLanguageTag extends LanguageTag implements QualityFactor
{
    protected int quality;
    
    public AcceptableLanguageTag(final String primaryTag, final String subTags) {
        super(primaryTag, subTags);
        this.quality = 1000;
    }
    
    public AcceptableLanguageTag(final String header) throws ParseException {
        this(HttpHeaderReader.newInstance(header));
    }
    
    public AcceptableLanguageTag(final HttpHeaderReader reader) throws ParseException {
        this.quality = 1000;
        reader.hasNext();
        this.tag = reader.nextToken();
        if (!this.tag.equals("*")) {
            this.parse(this.tag);
        }
        else {
            this.primaryTag = this.tag;
        }
        if (reader.hasNext()) {
            this.quality = HttpHeaderReader.readQualityFactorParameter(reader);
        }
    }
    
    @Override
    public int getQuality() {
        return this.quality;
    }
}
