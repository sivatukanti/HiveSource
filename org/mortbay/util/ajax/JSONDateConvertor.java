// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util.ajax;

import java.util.Date;
import org.mortbay.log.Log;
import java.util.Map;
import java.text.DateFormatSymbols;
import java.util.Locale;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import org.mortbay.util.DateCache;

public class JSONDateConvertor implements JSON.Convertor
{
    private boolean _fromJSON;
    DateCache _dateCache;
    SimpleDateFormat _format;
    
    public JSONDateConvertor() {
        this(false);
    }
    
    public JSONDateConvertor(final boolean fromJSON) {
        this(DateCache.DEFAULT_FORMAT, TimeZone.getTimeZone("GMT"), fromJSON);
    }
    
    public JSONDateConvertor(final String format, final TimeZone zone, final boolean fromJSON) {
        (this._dateCache = new DateCache(format)).setTimeZone(zone);
        this._fromJSON = fromJSON;
        (this._format = new SimpleDateFormat(format)).setTimeZone(zone);
    }
    
    public JSONDateConvertor(final String format, final TimeZone zone, final boolean fromJSON, final Locale locale) {
        (this._dateCache = new DateCache(format, locale)).setTimeZone(zone);
        this._fromJSON = fromJSON;
        (this._format = new SimpleDateFormat(format, new DateFormatSymbols(locale))).setTimeZone(zone);
    }
    
    public Object fromJSON(final Map map) {
        if (!this._fromJSON) {
            throw new UnsupportedOperationException();
        }
        try {
            synchronized (this._format) {
                return this._format.parseObject(map.get("value"));
            }
        }
        catch (Exception e) {
            Log.warn(e);
            return null;
        }
    }
    
    public void toJSON(final Object obj, final JSON.Output out) {
        final String date = this._dateCache.format((Date)obj);
        if (this._fromJSON) {
            out.addClass(obj.getClass());
            out.add("value", date);
        }
        else {
            out.add(date);
        }
    }
}
