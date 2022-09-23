// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ext;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.TimeZone;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import javax.xml.datatype.DatatypeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.Deserializers;

public class CoreXMLDeserializers extends Deserializers.Base
{
    static final DatatypeFactory _dataTypeFactory;
    protected static final int TYPE_DURATION = 1;
    protected static final int TYPE_G_CALENDAR = 2;
    protected static final int TYPE_QNAME = 3;
    
    @Override
    public JsonDeserializer<?> findBeanDeserializer(final JavaType type, final DeserializationConfig config, final BeanDescription beanDesc) {
        final Class<?> raw = type.getRawClass();
        if (raw == QName.class) {
            return new Std(raw, 3);
        }
        if (raw == XMLGregorianCalendar.class) {
            return new Std(raw, 2);
        }
        if (raw == Duration.class) {
            return new Std(raw, 1);
        }
        return null;
    }
    
    static {
        try {
            _dataTypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static class Std extends FromStringDeserializer<Object>
    {
        private static final long serialVersionUID = 1L;
        protected final int _kind;
        
        public Std(final Class<?> raw, final int kind) {
            super(raw);
            this._kind = kind;
        }
        
        @Override
        public Object deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (this._kind != 2) {
                return super.deserialize(jp, ctxt);
            }
            final Date d = this._parseDate(jp, ctxt);
            if (d == null) {
                return null;
            }
            final GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(d);
            final TimeZone tz = ctxt.getTimeZone();
            if (tz != null) {
                calendar.setTimeZone(tz);
            }
            return CoreXMLDeserializers._dataTypeFactory.newXMLGregorianCalendar(calendar);
        }
        
        @Override
        protected Object _deserialize(final String value, final DeserializationContext ctxt) throws IllegalArgumentException {
            switch (this._kind) {
                case 1: {
                    return CoreXMLDeserializers._dataTypeFactory.newDuration(value);
                }
                case 3: {
                    return QName.valueOf(value);
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
    }
}
