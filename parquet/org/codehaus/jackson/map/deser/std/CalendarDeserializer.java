// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.deser.std;

import parquet.org.codehaus.jackson.JsonProcessingException;
import java.io.IOException;
import java.util.Date;
import parquet.org.codehaus.jackson.map.DeserializationContext;
import parquet.org.codehaus.jackson.JsonParser;
import parquet.org.codehaus.jackson.map.annotate.JacksonStdImpl;
import java.util.Calendar;

@JacksonStdImpl
public class CalendarDeserializer extends StdScalarDeserializer<Calendar>
{
    protected final Class<? extends Calendar> _calendarClass;
    
    public CalendarDeserializer() {
        this((Class<? extends Calendar>)null);
    }
    
    public CalendarDeserializer(final Class<? extends Calendar> cc) {
        super(Calendar.class);
        this._calendarClass = cc;
    }
    
    @Override
    public Calendar deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
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
            return c;
        }
        catch (Exception e) {
            throw ctxt.instantiationException(this._calendarClass, e);
        }
    }
}
