// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.header;

import java.text.ParseException;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import java.util.Map;
import javax.ws.rs.core.MediaType;

public class QualitySourceMediaType extends MediaType
{
    public static final String QUALITY_SOURCE_FACTOR = "qs";
    public static final int DEFAULT_QUALITY_SOURCE_FACTOR = 1000;
    private final int qs;
    
    public QualitySourceMediaType(final String p, final String s) {
        super(p, s);
        this.qs = 1000;
    }
    
    public QualitySourceMediaType(final String p, final String s, final int qs, final Map<String, String> parameters) {
        super(p, s, parameters);
        this.qs = qs;
    }
    
    public QualitySourceMediaType(final MediaType mt) {
        this(mt.getType(), mt.getSubtype(), getQs(mt), mt.getParameters());
    }
    
    public int getQualitySource() {
        return this.qs;
    }
    
    public static QualitySourceMediaType valueOf(final HttpHeaderReader reader) throws ParseException {
        reader.hasNext();
        final String type = reader.nextToken();
        reader.nextSeparator('/');
        final String subType = reader.nextToken();
        int qs = 1000;
        Map<String, String> parameters = null;
        if (reader.hasNext()) {
            parameters = HttpHeaderReader.readParameters(reader);
            if (parameters != null) {
                qs = getQs(parameters.get("qs"));
            }
        }
        return new QualitySourceMediaType(type, subType, qs, parameters);
    }
    
    public static int getQualitySource(final MediaType mt) {
        if (mt instanceof QualitySourceMediaType) {
            final QualitySourceMediaType qsmt = (QualitySourceMediaType)mt;
            return qsmt.getQualitySource();
        }
        return getQs(mt);
    }
    
    private static int getQs(final MediaType mt) {
        try {
            return getQs(mt.getParameters().get("qs"));
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    private static int getQs(final String v) throws ParseException {
        if (v == null) {
            return 1000;
        }
        try {
            final int qs = (int)(Float.valueOf(v) * 1000.0);
            if (qs < 0) {
                throw new ParseException("The quality source (qs) value, " + v + ", must be non-negative number", 0);
            }
            return qs;
        }
        catch (NumberFormatException ex) {
            final ParseException pe = new ParseException("The quality source (qs) value, " + v + ", is not a valid value", 0);
            pe.initCause(ex);
            throw pe;
        }
    }
}
