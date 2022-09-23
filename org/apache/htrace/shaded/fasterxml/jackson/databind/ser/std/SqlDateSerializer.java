// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import java.text.DateFormat;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import java.sql.Date;

@JacksonStdImpl
public class SqlDateSerializer extends DateTimeSerializerBase<Date>
{
    public SqlDateSerializer() {
        this(Boolean.FALSE);
    }
    
    protected SqlDateSerializer(final Boolean useTimestamp) {
        super(Date.class, useTimestamp, null);
    }
    
    @Override
    public SqlDateSerializer withFormat(final Boolean timestamp, final DateFormat customFormat) {
        return new SqlDateSerializer(timestamp);
    }
    
    @Override
    protected long _timestamp(final Date value) {
        return (value == null) ? 0L : value.getTime();
    }
    
    @Override
    public void serialize(final Date value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        if (this._useTimestamp) {
            jgen.writeNumber(this._timestamp(value));
        }
        else {
            jgen.writeString(value.toString());
        }
    }
    
    @Override
    public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) {
        return this.createSchemaNode("string", true);
    }
    
    @Override
    public void acceptJsonFormatVisitor(final JsonFormatVisitorWrapper visitor, final JavaType typeHint) throws JsonMappingException {
        this._acceptJsonFormatVisitor(visitor, typeHint, this._useTimestamp);
    }
}
