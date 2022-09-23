// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import java.text.DateFormat;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import java.util.Date;

@JacksonStdImpl
public class DateSerializer extends DateTimeSerializerBase<Date>
{
    public static final DateSerializer instance;
    
    public DateSerializer() {
        this(null, null);
    }
    
    public DateSerializer(final Boolean useTimestamp, final DateFormat customFormat) {
        super(Date.class, useTimestamp, customFormat);
    }
    
    @Override
    public DateSerializer withFormat(final Boolean timestamp, final DateFormat customFormat) {
        return new DateSerializer(timestamp, customFormat);
    }
    
    @Override
    protected long _timestamp(final Date value) {
        return (value == null) ? 0L : value.getTime();
    }
    
    @Override
    public void serialize(final Date value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        if (this._asTimestamp(provider)) {
            jgen.writeNumber(this._timestamp(value));
        }
        else if (this._customFormat != null) {
            synchronized (this._customFormat) {
                jgen.writeString(this._customFormat.format(value));
            }
        }
        else {
            provider.defaultSerializeDateValue(value, jgen);
        }
    }
    
    static {
        instance = new DateSerializer();
    }
}
