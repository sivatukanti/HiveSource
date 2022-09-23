// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.header;

import java.util.Locale;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import java.text.ParseException;

public class LanguageTag
{
    protected String tag;
    protected String primaryTag;
    protected String subTags;
    
    protected LanguageTag() {
    }
    
    public static LanguageTag valueOf(final String s) throws IllegalArgumentException {
        final LanguageTag lt = new LanguageTag();
        try {
            lt.parse(s);
        }
        catch (ParseException pe) {
            throw new IllegalArgumentException(pe);
        }
        return lt;
    }
    
    public LanguageTag(final String primaryTag, final String subTags) {
        if (subTags != null && subTags.length() > 0) {
            this.tag = primaryTag + "-" + subTags;
        }
        else {
            this.tag = primaryTag;
        }
        this.primaryTag = primaryTag;
        this.subTags = subTags;
    }
    
    public LanguageTag(final String header) throws ParseException {
        this(HttpHeaderReader.newInstance(header));
    }
    
    public LanguageTag(final HttpHeaderReader reader) throws ParseException {
        reader.hasNext();
        this.tag = reader.nextToken();
        if (reader.hasNext()) {
            throw new ParseException("Invalid Language tag", reader.getIndex());
        }
        this.parse(this.tag);
    }
    
    public final boolean isCompatible(final Locale tag) {
        if (this.tag.equals("*")) {
            return true;
        }
        if (this.subTags == null) {
            return this.primaryTag.equalsIgnoreCase(tag.getLanguage());
        }
        return this.primaryTag.equalsIgnoreCase(tag.getLanguage()) && this.subTags.equalsIgnoreCase(tag.getCountry());
    }
    
    public final Locale getAsLocale() {
        return (this.subTags == null) ? new Locale(this.primaryTag) : new Locale(this.primaryTag, this.subTags);
    }
    
    protected final void parse(final String languageTag) throws ParseException {
        if (!this.isValid(languageTag)) {
            throw new ParseException("String, " + languageTag + ", is not a valid language tag", 0);
        }
        final int index = languageTag.indexOf(45);
        if (index == -1) {
            this.primaryTag = languageTag;
            this.subTags = null;
        }
        else {
            this.primaryTag = languageTag.substring(0, index);
            this.subTags = languageTag.substring(index + 1, languageTag.length());
        }
    }
    
    private boolean isValid(final String tag) {
        int alphaCount = 0;
        for (int i = 0; i < tag.length(); ++i) {
            final char c = tag.charAt(i);
            if (c == '-') {
                if (alphaCount == 0) {
                    return false;
                }
                alphaCount = 0;
            }
            else {
                if (('A' > c || c > 'Z') && ('a' > c || c > 'z')) {
                    return false;
                }
                if (++alphaCount > 8) {
                    return false;
                }
            }
        }
        return alphaCount != 0;
    }
    
    public final String getTag() {
        return this.tag;
    }
    
    public final String getPrimaryTag() {
        return this.primaryTag;
    }
    
    public final String getSubTags() {
        return this.subTags;
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object instanceof LanguageTag) {
            final LanguageTag lt = (LanguageTag)object;
            if (this.tag != null) {
                if (!this.tag.equals(lt.getTag())) {
                    return false;
                }
                if (lt.getTag() != null) {
                    return false;
                }
            }
            if (this.primaryTag != null) {
                if (!this.primaryTag.equals(lt.getPrimaryTag())) {
                    return false;
                }
                if (lt.getPrimaryTag() != null) {
                    return false;
                }
            }
            if (this.subTags != null) {
                if (!this.subTags.equals(lt.getSubTags())) {
                    return false;
                }
                if (lt.getSubTags() != null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return ((this.tag == null) ? 0 : this.tag.hashCode()) + ((this.primaryTag == null) ? 0 : this.primaryTag.hashCode()) + ((this.subTags == null) ? 0 : this.primaryTag.hashCode());
    }
    
    @Override
    public String toString() {
        return this.primaryTag + ((this.subTags == null) ? "" : this.subTags);
    }
}
