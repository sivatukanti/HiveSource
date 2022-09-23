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
import java.util.Calendar;

@JacksonStdImpl
public class CalendarSerializer extends DateTimeSerializerBase<Calendar>
{
    public static final CalendarSerializer instance;
    
    public CalendarSerializer() {
        this(null, null);
    }
    
    public CalendarSerializer(final Boolean useTimestamp, final DateFormat customFormat) {
        super(Calendar.class, useTimestamp, customFormat);
    }
    
    @Override
    public CalendarSerializer withFormat(final Boolean timestamp, final DateFormat customFormat) {
        return new CalendarSerializer(timestamp, customFormat);
    }
    
    @Override
    protected long _timestamp(final Calendar value) {
        return (value == null) ? 0L : value.getTimeInMillis();
    }
    
    @Override
    public void serialize(final Calendar value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        if (this._asTimestamp(provider)) {
            jgen.writeNumber(this._timestamp(value));
        }
        else if (this._customFormat != null) {
            synchronized (this._customFormat) {
                jgen.writeString(this._customFormat.format(value.getTime()));
            }
        }
        else {
            provider.defaultSerializeDateValue(value.getTime(), jgen);
        }
    }
    
    static {
        instance = new CalendarSerializer();
    }
}
