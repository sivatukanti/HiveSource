// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.header;

import com.sun.jersey.core.header.reader.HttpHeaderReader;
import java.text.ParseException;
import java.util.Date;

public class FormDataContentDisposition extends ContentDisposition
{
    private String name;
    
    protected FormDataContentDisposition(final String type, final String name, final String fileName, final Date creationDate, final Date modificationDate, final Date readDate, final long size) {
        super(type, fileName, creationDate, modificationDate, readDate, size);
        this.name = name;
        if (!this.getType().equalsIgnoreCase("form-data")) {
            throw new IllegalArgumentException("The content dispostion type is not equal to form-data");
        }
        if (name == null) {
            throw new IllegalArgumentException("The name parameter is not present");
        }
    }
    
    public FormDataContentDisposition(final String header) throws ParseException {
        this(header, false);
    }
    
    public FormDataContentDisposition(final String header, final boolean fileNameFix) throws ParseException {
        this(HttpHeaderReader.newInstance(header), fileNameFix);
    }
    
    public FormDataContentDisposition(final HttpHeaderReader reader) throws ParseException {
        this(reader, false);
    }
    
    public FormDataContentDisposition(final HttpHeaderReader reader, final boolean fileNameFix) throws ParseException {
        super(reader, fileNameFix);
        if (!this.getType().equalsIgnoreCase("form-data")) {
            throw new IllegalArgumentException("The content dispostion type is not equal to form-data");
        }
        this.name = this.getParameters().get("name");
        if (this.name == null) {
            throw new IllegalArgumentException("The name parameter is not present");
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    protected StringBuilder toStringBuffer() {
        final StringBuilder sb = super.toStringBuffer();
        this.addStringParameter(sb, "name", this.name);
        return sb;
    }
    
    public static FormDataContentDispositionBuilder name(final String name) {
        return new FormDataContentDispositionBuilder(name);
    }
    
    public static class FormDataContentDispositionBuilder extends ContentDispositionBuilder<FormDataContentDispositionBuilder, FormDataContentDisposition>
    {
        private String name;
        
        FormDataContentDispositionBuilder(final String name) {
            super("form-data");
            this.name = name;
        }
        
        @Override
        public FormDataContentDisposition build() {
            final FormDataContentDisposition cd = new FormDataContentDisposition(this.type, this.name, this.fileName, this.creationDate, this.modificationDate, this.readDate, this.size);
            return cd;
        }
    }
}
