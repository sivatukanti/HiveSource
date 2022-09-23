// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationFeature;
import java.text.ParseException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonFormat;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.StdDateFormat;
import java.text.SimpleDateFormat;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.Annotated;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import java.text.DateFormat;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.util.GregorianCalendar;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Calendar;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonDeserializer;
import java.util.HashSet;

public class DateDeserializers
{
    private static final HashSet<String> _classNames;
    
    public static JsonDeserializer<?> find(final Class<?> rawType, final String clsName) {
        if (DateDeserializers._classNames.contains(clsName)) {
            if (rawType == Calendar.class) {
                return new CalendarDeserializer();
            }
            if (rawType == Date.class) {
                return DateDeserializer.instance;
            }
            if (rawType == java.sql.Date.class) {
                return new SqlDateDeserializer();
            }
            if (rawType == Timestamp.class) {
                return new TimestampDeserializer();
            }
            if (rawType == GregorianCalendar.class) {
                return new CalendarDeserializer(GregorianCalendar.class);
            }
        }
        return null;
    }
    
    static {
        _classNames = new HashSet<String>();
        final Class[] arr$;
        final Class<?>[] numberTypes = (Class<?>[])(arr$ = new Class[] { Calendar.class, GregorianCalendar.class, java.sql.Date.class, Date.class, Timestamp.class });
        for (final Class<?> cls : arr$) {
            DateDeserializers._classNames.add(cls.getName());
        }
    }
    
    protected abstract static class DateBasedDeserializer<T> extends StdScalarDeserializer<T> implements ContextualDeserializer
    {
        protected final DateFormat _customFormat;
        protected final String _formatString;
        
        protected DateBasedDeserializer(final Class<?> clz) {
            super(clz);
            this._customFormat = null;
            this._formatString = null;
        }
        
        protected DateBasedDeserializer(final DateBasedDeserializer<T> base, final DateFormat format, final String formatStr) {
            super(base._valueClass);
            this._customFormat = format;
            this._formatString = formatStr;
        }
        
        protected abstract DateBasedDeserializer<T> withDateFormat(final DateFormat p0, final String p1);
        
        @Override
        public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
            if (property != null) {
                final JsonFormat.Value format = ctxt.getAnnotationIntrospector().findFormat(property.getMember());
                if (format != null) {
                    TimeZone tz = format.getTimeZone();
                    if (format.hasPattern()) {
                        final String pattern = format.getPattern();
                        final Locale loc = format.hasLocale() ? format.getLocale() : ctxt.getLocale();
                        final SimpleDateFormat df = new SimpleDateFormat(pattern, loc);
                        if (tz == null) {
                            tz = ctxt.getTimeZone();
                        }
                        df.setTimeZone(tz);
                        return this.withDateFormat(df, pattern);
                    }
                    if (tz != null) {
                        DateFormat df2 = ctxt.getConfig().getDateFormat();
                        if (df2.getClass() == StdDateFormat.class) {
                            final Locale loc = format.hasLocale() ? format.getLocale() : ctxt.getLocale();
                            StdDateFormat std = (StdDateFormat)df2;
                            std = std.withTimeZone(tz);
                            std = (StdDateFormat)(df2 = std.withLocale(loc));
                        }
                        else {
                            df2 = (DateFormat)df2.clone();
                            df2.setTimeZone(tz);
                        }
                        return this.withDateFormat(df2, this._formatString);
                    }
                }
            }
            return this;
        }
        
        @Override
        protected Date _parseDate(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (this._customFormat != null) {
                JsonToken t = jp.getCurrentToken();
                if (t == JsonToken.VALUE_STRING) {
                    final String str = jp.getText().trim();
                    if (str.length() == 0) {
                        return this.getEmptyValue();
                    }
                    synchronized (this._customFormat) {
                        try {
                            return this._customFormat.parse(str);
                        }
                        catch (ParseException e) {
                            throw new IllegalArgumentException("Failed to parse Date value '" + str + "' (format: \"" + this._formatString + "\"): " + e.getMessage());
                        }
                    }
                }
                if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                    jp.nextToken();
                    final Date parsed = this._parseDate(jp, ctxt);
                    t = jp.nextToken();
                    if (t != JsonToken.END_ARRAY) {
                        throw ctxt.wrongTokenException(jp, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single 'java.util.Date' value but there was more than a single value in the array");
                    }
                    return parsed;
                }
            }
            return super._parseDate(jp, ctxt);
        }
    }
    
    @JacksonStdImpl
    public static class CalendarDeserializer extends DateBasedDeserializer<Calendar>
    {
        protected final Class<? extends Calendar> _calendarClass;
        
        public CalendarDeserializer() {
            super(Calendar.class);
            this._calendarClass = null;
        }
        
        public CalendarDeserializer(final Class<? extends Calendar> cc) {
            super(cc);
            this._calendarClass = cc;
        }
        
        public CalendarDeserializer(final CalendarDeserializer src, final DateFormat df, final String formatString) {
            super(src, df, formatString);
            this._calendarClass = src._calendarClass;
        }
        
        @Override
        protected CalendarDeserializer withDateFormat(final DateFormat df, final String formatString) {
            return new CalendarDeserializer(this, df, formatString);
        }
        
        @Override
        public Calendar deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
            final Date d = this._parseDate(jp, ctxt);
            if (d == null) {
                return null;
            }
            if (this._calendarClass == null) {
                return ctxt.constructCalendar(d);
            }
            try {
                final Calendar c = (Calendar)this._calendarClass.newInstance();
                c.setTimeInMillis(d.getTime());
                final TimeZone tz = ctxt.getTimeZone();
                if (tz != null) {
                    c.setTimeZone(tz);
                }
                return c;
            }
            catch (Exception e) {
                throw ctxt.instantiationException(this._calendarClass, e);
            }
        }
    }
    
    public static class DateDeserializer extends DateBasedDeserializer<Date>
    {
        public static final DateDeserializer instance;
        
        public DateDeserializer() {
            super(Date.class);
        }
        
        public DateDeserializer(final DateDeserializer base, final DateFormat df, final String formatString) {
            super(base, df, formatString);
        }
        
        @Override
        protected DateDeserializer withDateFormat(final DateFormat df, final String formatString) {
            return new DateDeserializer(this, df, formatString);
        }
        
        @Override
        public Date deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
            return this._parseDate(jp, ctxt);
        }
        
        static {
            instance = new DateDeserializer();
        }
    }
    
    public static class SqlDateDeserializer extends DateBasedDeserializer<java.sql.Date>
    {
        public SqlDateDeserializer() {
            super(java.sql.Date.class);
        }
        
        public SqlDateDeserializer(final SqlDateDeserializer src, final DateFormat df, final String formatString) {
            super(src, df, formatString);
        }
        
        @Override
        protected SqlDateDeserializer withDateFormat(final DateFormat df, final String formatString) {
            return new SqlDateDeserializer(this, df, formatString);
        }
        
        @Override
        public java.sql.Date deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
            final Date d = this._parseDate(jp, ctxt);
            return (d == null) ? null : new java.sql.Date(d.getTime());
        }
    }
    
    public static class TimestampDeserializer extends DateBasedDeserializer<Timestamp>
    {
        public TimestampDeserializer() {
            super(Timestamp.class);
        }
        
        public TimestampDeserializer(final TimestampDeserializer src, final DateFormat df, final String formatString) {
            super(src, df, formatString);
        }
        
        @Override
        protected TimestampDeserializer withDateFormat(final DateFormat df, final String formatString) {
            return new TimestampDeserializer(this, df, formatString);
        }
        
        @Override
        public Timestamp deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
            return new Timestamp(this._parseDate(jp, ctxt).getTime());
        }
    }
}
