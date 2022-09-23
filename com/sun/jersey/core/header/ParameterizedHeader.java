// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.header;

import java.util.Collections;
import java.text.ParseException;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import java.util.Map;

public class ParameterizedHeader
{
    private String value;
    private Map<String, String> parameters;
    
    public ParameterizedHeader(final String header) throws ParseException {
        this(HttpHeaderReader.newInstance(header));
    }
    
    public ParameterizedHeader(final HttpHeaderReader reader) throws ParseException {
        reader.hasNext();
        this.value = "";
        while (reader.hasNext() && !reader.hasNextSeparator(';', false)) {
            reader.next();
            this.value += reader.getEventValue();
        }
        if (reader.hasNext()) {
            this.parameters = HttpHeaderReader.readParameters(reader);
        }
        if (this.parameters == null) {
            this.parameters = Collections.emptyMap();
        }
        else {
            this.parameters = Collections.unmodifiableMap((Map<? extends String, ? extends String>)this.parameters);
        }
    }
    
    public String getValue() {
        return this.value;
    }
    
    public Map<String, String> getParameters() {
        return this.parameters;
    }
}
