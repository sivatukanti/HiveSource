// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.header;

import java.util.Collections;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

public class ContentDisposition
{
    private String type;
    private Map<String, String> parameters;
    private String fileName;
    private Date creationDate;
    private Date modificationDate;
    private Date readDate;
    private long size;
    
    protected ContentDisposition(final String type, final String fileName, final Date creationDate, final Date modificationDate, final Date readDate, final long size) {
        this.type = type;
        this.fileName = fileName;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.readDate = readDate;
        this.size = size;
    }
    
    public ContentDisposition(final String header) throws ParseException {
        this(header, false);
    }
    
    public ContentDisposition(final String header, final boolean fileNameFix) throws ParseException {
        this(HttpHeaderReader.newInstance(header), fileNameFix);
    }
    
    public ContentDisposition(final HttpHeaderReader reader) throws ParseException {
        this(reader, false);
    }
    
    public ContentDisposition(final HttpHeaderReader reader, final boolean fileNameFix) throws ParseException {
        reader.hasNext();
        this.type = reader.nextToken();
        if (reader.hasNext()) {
            this.parameters = HttpHeaderReader.readParameters(reader, fileNameFix);
        }
        this.parameters = ((this.parameters == null) ? Collections.emptyMap() : Collections.unmodifiableMap((Map<? extends String, ? extends String>)this.parameters));
        this.createParameters();
    }
    
    public String getType() {
        return this.type;
    }
    
    public Map<String, String> getParameters() {
        return this.parameters;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public Date getCreationDate() {
        return this.creationDate;
    }
    
    public Date getModificationDate() {
        return this.modificationDate;
    }
    
    public Date getReadDate() {
        return this.readDate;
    }
    
    public long getSize() {
        return this.size;
    }
    
    @Override
    public String toString() {
        return this.toStringBuffer().toString();
    }
    
    protected StringBuilder toStringBuffer() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.type);
        this.addStringParameter(sb, "filename", this.fileName);
        this.addDateParameter(sb, "creation-date", this.creationDate);
        this.addDateParameter(sb, "modification-date", this.modificationDate);
        this.addDateParameter(sb, "read-date", this.readDate);
        this.addLongParameter(sb, "size", this.size);
        return sb;
    }
    
    protected void addStringParameter(final StringBuilder sb, final String name, final String p) {
        if (p != null) {
            sb.append("; ").append(name).append("=\"").append(p).append("\"");
        }
    }
    
    protected void addDateParameter(final StringBuilder sb, final String name, final Date p) {
        if (p != null) {
            sb.append("; ").append(name).append("=\"").append(HttpDateFormat.getPreferedDateFormat().format(p)).append("\"");
        }
    }
    
    protected void addLongParameter(final StringBuilder sb, final String name, final Long p) {
        if (p != -1L) {
            sb.append("; ").append(name).append('=').append(Long.toString(p));
        }
    }
    
    private void createParameters() throws ParseException {
        this.fileName = this.parameters.get("filename");
        this.creationDate = this.createDate("creation-date");
        this.modificationDate = this.createDate("modification-date");
        this.readDate = this.createDate("read-date");
        this.size = this.createLong("size");
    }
    
    private Date createDate(final String name) throws ParseException {
        final String value = this.parameters.get(name);
        if (value == null) {
            return null;
        }
        return HttpDateFormat.getPreferedDateFormat().parse(value);
    }
    
    private long createLong(final String name) throws ParseException {
        final String value = this.parameters.get(name);
        if (value == null) {
            return -1L;
        }
        try {
            return Long.valueOf(value);
        }
        catch (NumberFormatException e) {
            throw new ParseException("Error parsing size parameter of value, " + value, 0);
        }
    }
    
    public static ContentDispositionBuilder type(final String type) {
        return new ContentDispositionBuilder(type);
    }
    
    public static class ContentDispositionBuilder<T extends ContentDispositionBuilder<T, V>, V extends ContentDisposition>
    {
        protected String type;
        protected String fileName;
        protected Date creationDate;
        protected Date modificationDate;
        protected Date readDate;
        protected long size;
        
        ContentDispositionBuilder(final String type) {
            this.size = -1L;
            this.type = type;
        }
        
        public T fileName(final String fileName) {
            this.fileName = fileName;
            return (T)this;
        }
        
        public T creationDate(final Date creationDate) {
            this.creationDate = creationDate;
            return (T)this;
        }
        
        public T modificationDate(final Date modificationDate) {
            this.modificationDate = modificationDate;
            return (T)this;
        }
        
        public T readDate(final Date readDate) {
            this.readDate = readDate;
            return (T)this;
        }
        
        public T size(final long size) {
            this.size = size;
            return (T)this;
        }
        
        public V build() {
            final ContentDisposition cd = new ContentDisposition(this.type, this.fileName, this.creationDate, this.modificationDate, this.readDate, this.size);
            return (V)cd;
        }
    }
}
