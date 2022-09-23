// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.factory;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Locale;
import javax.ws.rs.core.MediaType;
import java.util.List;
import javax.ws.rs.core.Variant;

public class VariantListBuilderImpl extends Variant.VariantListBuilder
{
    private List<Variant> variants;
    private final List<MediaType> mediaTypes;
    private final List<Locale> languages;
    private final List<String> charsets;
    private final List<String> encodings;
    
    public VariantListBuilderImpl() {
        this.mediaTypes = new ArrayList<MediaType>();
        this.languages = new ArrayList<Locale>();
        this.charsets = new ArrayList<String>();
        this.encodings = new ArrayList<String>();
    }
    
    @Override
    public List<Variant> build() {
        if (this.variants == null) {
            this.variants = new ArrayList<Variant>();
        }
        return this.variants;
    }
    
    @Override
    public Variant.VariantListBuilder add() {
        if (this.variants == null) {
            this.variants = new ArrayList<Variant>();
        }
        this.addMediaTypes();
        this.charsets.clear();
        this.languages.clear();
        this.encodings.clear();
        this.mediaTypes.clear();
        return this;
    }
    
    private void addMediaTypes() {
        if (this.mediaTypes.isEmpty()) {
            this.addLanguages(null);
        }
        else {
            for (final MediaType mediaType : this.mediaTypes) {
                this.addLanguages(mediaType);
            }
        }
    }
    
    private void addLanguages(final MediaType mediaType) {
        if (this.languages.isEmpty()) {
            this.addEncodings(mediaType, null);
        }
        else {
            for (final Locale language : this.languages) {
                this.addEncodings(mediaType, language);
            }
        }
    }
    
    private void addEncodings(final MediaType mediaType, final Locale language) {
        if (this.encodings.isEmpty()) {
            this.addVariant(mediaType, language, null);
        }
        else {
            for (final String encoding : this.encodings) {
                this.addVariant(mediaType, language, encoding);
            }
        }
    }
    
    private void addVariant(final MediaType mediaType, final Locale language, final String encoding) {
        this.variants.add(new Variant(mediaType, language, encoding));
    }
    
    @Override
    public Variant.VariantListBuilder languages(final Locale... languages) {
        for (final Locale language : languages) {
            this.languages.add(language);
        }
        return this;
    }
    
    @Override
    public Variant.VariantListBuilder encodings(final String... encodings) {
        for (final String encoding : encodings) {
            this.encodings.add(encoding);
        }
        return this;
    }
    
    @Override
    public Variant.VariantListBuilder mediaTypes(final MediaType... mediaTypes) {
        for (final MediaType mediaType : mediaTypes) {
            this.mediaTypes.add(mediaType);
        }
        return this;
    }
}
