// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.ext;

import parquet.org.codehaus.jackson.map.JsonMappingException;
import parquet.org.codehaus.jackson.JsonNode;
import java.lang.reflect.Type;
import parquet.org.codehaus.jackson.JsonGenerationException;
import java.io.IOException;
import java.util.Calendar;
import parquet.org.codehaus.jackson.map.ser.std.CalendarSerializer;
import parquet.org.codehaus.jackson.map.SerializerProvider;
import parquet.org.codehaus.jackson.JsonGenerator;
import parquet.org.codehaus.jackson.map.ser.std.SerializerBase;
import javax.xml.namespace.QName;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.Duration;
import parquet.org.codehaus.jackson.map.ser.std.ToStringSerializer;
import java.util.Collection;
import java.util.HashMap;
import parquet.org.codehaus.jackson.map.JsonSerializer;
import java.util.Map;
import parquet.org.codehaus.jackson.map.util.Provider;

public class CoreXMLSerializers implements Provider<Map.Entry<Class<?>, JsonSerializer<?>>>
{
    static final HashMap<Class<?>, JsonSerializer<?>> _serializers;
    
    public Collection<Map.Entry<Class<?>, JsonSerializer<?>>> provide() {
        return CoreXMLSerializers._serializers.entrySet();
    }
    
    static {
        _serializers = new HashMap<Class<?>, JsonSerializer<?>>();
        final ToStringSerializer tss = ToStringSerializer.instance;
        CoreXMLSerializers._serializers.put(Duration.class, tss);
        CoreXMLSerializers._serializers.put(XMLGregorianCalendar.class, new XMLGregorianCalendarSerializer());
        CoreXMLSerializers._serializers.put(QName.class, tss);
    }
    
    public static class XMLGregorianCalendarSerializer extends SerializerBase<XMLGregorianCalendar>
    {
        public XMLGregorianCalendarSerializer() {
            super(XMLGregorianCalendar.class);
        }
        
        @Override
        public void serialize(final XMLGregorianCalendar value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
            CalendarSerializer.instance.serialize(value.toGregorianCalendar(), jgen, provider);
        }
        
        @Override
        public JsonNode getSchema(final SerializerProvider provider, final Type typeHint) throws JsonMappingException {
            return CalendarSerializer.instance.getSchema(provider, typeHint);
        }
    }
}
