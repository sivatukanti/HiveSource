// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import org.mortbay.io.View;
import org.mortbay.util.LazyList;
import java.util.Collections;
import java.util.StringTokenizer;
import java.io.IOException;
import javax.servlet.http.Cookie;
import org.mortbay.io.ByteArrayBuffer;
import java.util.Locale;
import java.util.Date;
import java.util.Map;
import java.util.List;
import org.mortbay.io.BufferCache;
import org.mortbay.util.QuotedStringTokenizer;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.mortbay.io.BufferUtil;
import java.util.Enumeration;
import org.mortbay.util.StringUtil;
import java.util.GregorianCalendar;
import org.mortbay.util.StringMap;
import java.util.Calendar;
import java.util.HashMap;
import java.util.ArrayList;
import org.mortbay.io.Buffer;
import java.text.SimpleDateFormat;
import org.mortbay.io.BufferDateCache;
import java.util.TimeZone;

public class HttpFields
{
    public static final String __separators = ", \t";
    private static String[] DAYS;
    private static String[] MONTHS;
    private static TimeZone __GMT;
    public static final BufferDateCache __dateCache;
    private static final String[] __dateReceiveFmt;
    private static int __dateReceiveInit;
    private static SimpleDateFormat[] __dateReceive;
    public static final String __01Jan1970;
    public static final Buffer __01Jan1970_BUFFER;
    protected ArrayList _fields;
    protected int _revision;
    protected HashMap _bufferMap;
    protected SimpleDateFormat[] _dateReceive;
    private StringBuffer _dateBuffer;
    private Calendar _calendar;
    private static Float __one;
    private static Float __zero;
    private static StringMap __qualities;
    
    public static String formatDate(final long date, final boolean cookie) {
        final StringBuffer buf = new StringBuffer(32);
        final GregorianCalendar gc = new GregorianCalendar(HttpFields.__GMT);
        gc.setTimeInMillis(date);
        formatDate(buf, gc, cookie);
        return buf.toString();
    }
    
    public static String formatDate(final Calendar calendar, final boolean cookie) {
        final StringBuffer buf = new StringBuffer(32);
        formatDate(buf, calendar, cookie);
        return buf.toString();
    }
    
    public static String formatDate(final StringBuffer buf, final long date, final boolean cookie) {
        final GregorianCalendar gc = new GregorianCalendar(HttpFields.__GMT);
        gc.setTimeInMillis(date);
        formatDate(buf, gc, cookie);
        return buf.toString();
    }
    
    public static void formatDate(final StringBuffer buf, final Calendar calendar, final boolean cookie) {
        final int day_of_week = calendar.get(7);
        final int day_of_month = calendar.get(5);
        final int month = calendar.get(2);
        int year = calendar.get(1);
        final int century = year / 100;
        year %= 100;
        int epoch = (int)(calendar.getTimeInMillis() / 1000L % 86400L);
        final int seconds = epoch % 60;
        epoch /= 60;
        final int minutes = epoch % 60;
        final int hours = epoch / 60;
        buf.append(HttpFields.DAYS[day_of_week]);
        buf.append(',');
        buf.append(' ');
        StringUtil.append2digits(buf, day_of_month);
        if (cookie) {
            buf.append('-');
            buf.append(HttpFields.MONTHS[month]);
            buf.append('-');
            StringUtil.append2digits(buf, century);
            StringUtil.append2digits(buf, year);
        }
        else {
            buf.append(' ');
            buf.append(HttpFields.MONTHS[month]);
            buf.append(' ');
            StringUtil.append2digits(buf, century);
            StringUtil.append2digits(buf, year);
        }
        buf.append(' ');
        StringUtil.append2digits(buf, hours);
        buf.append(':');
        StringUtil.append2digits(buf, minutes);
        buf.append(':');
        StringUtil.append2digits(buf, seconds);
        buf.append(" GMT");
    }
    
    public HttpFields() {
        this._fields = new ArrayList(20);
        this._bufferMap = new HashMap(32);
        this._dateReceive = new SimpleDateFormat[HttpFields.__dateReceive.length];
    }
    
    public Enumeration getFieldNames() {
        final int revision = this._revision;
        return new Enumeration() {
            int i = 0;
            Field field = null;
            
            public boolean hasMoreElements() {
                if (this.field != null) {
                    return true;
                }
                while (this.i < HttpFields.this._fields.size()) {
                    final Field f = HttpFields.this._fields.get(this.i++);
                    if (f != null && f._prev == null && f._revision == revision) {
                        this.field = f;
                        return true;
                    }
                }
                return false;
            }
            
            public Object nextElement() throws NoSuchElementException {
                if (this.field != null || this.hasMoreElements()) {
                    final String n = BufferUtil.to8859_1_String(this.field._name);
                    this.field = null;
                    return n;
                }
                throw new NoSuchElementException();
            }
        };
    }
    
    public Iterator getFields() {
        final int revision = this._revision;
        return new Iterator() {
            int i = 0;
            Field field = null;
            
            public boolean hasNext() {
                if (this.field != null) {
                    return true;
                }
                while (this.i < HttpFields.this._fields.size()) {
                    final Field f = HttpFields.this._fields.get(this.i++);
                    if (f != null && f._revision == revision) {
                        this.field = f;
                        return true;
                    }
                }
                return false;
            }
            
            public Object next() {
                if (this.field != null || this.hasNext()) {
                    final Field f = this.field;
                    this.field = null;
                    return f;
                }
                throw new NoSuchElementException();
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    private Field getField(final String name) {
        return this._bufferMap.get(HttpHeaders.CACHE.lookup(name));
    }
    
    private Field getField(final Buffer name) {
        return this._bufferMap.get(name);
    }
    
    public boolean containsKey(final Buffer name) {
        final Field f = this.getField(name);
        return f != null && f._revision == this._revision;
    }
    
    public boolean containsKey(final String name) {
        final Field f = this.getField(name);
        return f != null && f._revision == this._revision;
    }
    
    public String getStringField(final String name) {
        final Field field = this.getField(name);
        if (field != null && field._revision == this._revision) {
            return field.getValue();
        }
        return null;
    }
    
    public String getStringField(final Buffer name) {
        final Field field = this.getField(name);
        if (field != null && field._revision == this._revision) {
            return BufferUtil.to8859_1_String(field._value);
        }
        return null;
    }
    
    public Buffer get(final Buffer name) {
        final Field field = this.getField(name);
        if (field != null && field._revision == this._revision) {
            return field._value;
        }
        return null;
    }
    
    public Enumeration getValues(final String name) {
        final Field field = this.getField(name);
        if (field == null) {
            return null;
        }
        final int revision = this._revision;
        return new Enumeration() {
            Field f = field;
            
            public boolean hasMoreElements() {
                while (this.f != null && this.f._revision != revision) {
                    this.f = this.f._next;
                }
                return this.f != null;
            }
            
            public Object nextElement() throws NoSuchElementException {
                if (this.f == null) {
                    throw new NoSuchElementException();
                }
                final Field n = this.f;
                do {
                    this.f = this.f._next;
                } while (this.f != null && this.f._revision != revision);
                return n.getValue();
            }
        };
    }
    
    public Enumeration getValues(final Buffer name) {
        final Field field = this.getField(name);
        if (field == null) {
            return null;
        }
        final int revision = this._revision;
        return new Enumeration() {
            Field f = field;
            
            public boolean hasMoreElements() {
                while (this.f != null && this.f._revision != revision) {
                    this.f = this.f._next;
                }
                return this.f != null;
            }
            
            public Object nextElement() throws NoSuchElementException {
                if (this.f == null) {
                    throw new NoSuchElementException();
                }
                final Field n = this.f;
                this.f = this.f._next;
                while (this.f != null && this.f._revision != revision) {
                    this.f = this.f._next;
                }
                return n.getValue();
            }
        };
    }
    
    public Enumeration getValues(final String name, final String separators) {
        final Enumeration e = this.getValues(name);
        if (e == null) {
            return null;
        }
        return new Enumeration() {
            QuotedStringTokenizer tok = null;
            
            public boolean hasMoreElements() {
                if (this.tok != null && this.tok.hasMoreElements()) {
                    return true;
                }
                while (e.hasMoreElements()) {
                    final String value = e.nextElement();
                    this.tok = new QuotedStringTokenizer(value, separators, false, false);
                    if (this.tok.hasMoreElements()) {
                        return true;
                    }
                }
                this.tok = null;
                return false;
            }
            
            public Object nextElement() throws NoSuchElementException {
                if (!this.hasMoreElements()) {
                    throw new NoSuchElementException();
                }
                String next = (String)this.tok.nextElement();
                if (next != null) {
                    next = next.trim();
                }
                return next;
            }
        };
    }
    
    public void put(final String name, final String value) {
        final Buffer n = HttpHeaders.CACHE.lookup(name);
        Buffer v = null;
        if (value != null) {
            v = HttpHeaderValues.CACHE.lookup(value);
        }
        this.put(n, v, -1L);
    }
    
    public void put(final Buffer name, final String value) {
        final Buffer v = HttpHeaderValues.CACHE.lookup(value);
        this.put(name, v, -1L);
    }
    
    public void put(final Buffer name, final Buffer value) {
        this.put(name, value, -1L);
    }
    
    public void put(Buffer name, final Buffer value, final long numValue) {
        if (value == null) {
            this.remove(name);
            return;
        }
        if (!(name instanceof BufferCache.CachedBuffer)) {
            name = HttpHeaders.CACHE.lookup(name);
        }
        Field field = this._bufferMap.get(name);
        if (field != null) {
            field.reset(value, numValue, this._revision);
            for (field = field._next; field != null; field = field._next) {
                field.clear();
            }
            return;
        }
        field = new Field(name, value, numValue, this._revision);
        this._fields.add(field);
        this._bufferMap.put(field.getNameBuffer(), field);
    }
    
    public void put(final String name, final List list) {
        if (list == null || list.size() == 0) {
            this.remove(name);
            return;
        }
        final Buffer n = HttpHeaders.CACHE.lookup(name);
        Object v = list.get(0);
        if (v != null) {
            this.put(n, HttpHeaderValues.CACHE.lookup(v.toString()));
        }
        else {
            this.remove(n);
        }
        if (list.size() > 1) {
            final Iterator iter = list.iterator();
            iter.next();
            while (iter.hasNext()) {
                v = iter.next();
                if (v != null) {
                    this.put(n, HttpHeaderValues.CACHE.lookup(v.toString()));
                }
            }
        }
    }
    
    public void add(final String name, final String value) throws IllegalArgumentException {
        final Buffer n = HttpHeaders.CACHE.lookup(name);
        final Buffer v = HttpHeaderValues.CACHE.lookup(value);
        this.add(n, v, -1L);
    }
    
    public void add(final Buffer name, final Buffer value) throws IllegalArgumentException {
        this.add(name, value, -1L);
    }
    
    private void add(Buffer name, final Buffer value, final long numValue) throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException("null value");
        }
        if (!(name instanceof BufferCache.CachedBuffer)) {
            name = HttpHeaders.CACHE.lookup(name);
        }
        Field field = this._bufferMap.get(name);
        Field last = null;
        if (field != null) {
            while (field != null && field._revision == this._revision) {
                last = field;
                field = field._next;
            }
        }
        if (field != null) {
            field.reset(value, numValue, this._revision);
        }
        else {
            field = new Field(name, value, numValue, this._revision);
            if (last != null) {
                field._prev = last;
                last._next = field;
            }
            else {
                this._bufferMap.put(field.getNameBuffer(), field);
            }
            this._fields.add(field);
        }
    }
    
    public void remove(final String name) {
        this.remove(HttpHeaders.CACHE.lookup(name));
    }
    
    public void remove(final Buffer name) {
        Field field = this._bufferMap.get(name);
        if (field != null) {
            while (field != null) {
                field.clear();
                field = field._next;
            }
        }
    }
    
    public long getLongField(final String name) throws NumberFormatException {
        final Field field = this.getField(name);
        if (field != null && field._revision == this._revision) {
            return field.getLongValue();
        }
        return -1L;
    }
    
    public long getLongField(final Buffer name) throws NumberFormatException {
        final Field field = this.getField(name);
        if (field != null && field._revision == this._revision) {
            return field.getLongValue();
        }
        return -1L;
    }
    
    public long getDateField(final String name) {
        final Field field = this.getField(name);
        if (field == null || field._revision != this._revision) {
            return -1L;
        }
        if (field._numValue != -1L) {
            return field._numValue;
        }
        String val = valueParameters(BufferUtil.to8859_1_String(field._value), null);
        if (val == null) {
            return -1L;
        }
        int i = 0;
        while (i < HttpFields.__dateReceiveInit) {
            if (this._dateReceive[i] == null) {
                this._dateReceive[i] = (SimpleDateFormat)HttpFields.__dateReceive[i].clone();
            }
            try {
                final Date date = (Date)this._dateReceive[i].parseObject(val);
                return field._numValue = date.getTime();
            }
            catch (Exception e) {
                ++i;
                continue;
            }
            break;
        }
        if (val.endsWith(" GMT")) {
            val = val.substring(0, val.length() - 4);
            i = 0;
            while (i < HttpFields.__dateReceiveInit) {
                try {
                    final Date date = (Date)this._dateReceive[i].parseObject(val);
                    return field._numValue = date.getTime();
                }
                catch (Exception e) {
                    ++i;
                    continue;
                }
                break;
            }
        }
        synchronized (HttpFields.__dateReceive) {
            int j = HttpFields.__dateReceiveInit;
            while (j < this._dateReceive.length) {
                if (this._dateReceive[j] == null) {
                    if (HttpFields.__dateReceive[j] == null) {
                        (HttpFields.__dateReceive[j] = new SimpleDateFormat(HttpFields.__dateReceiveFmt[j], Locale.US)).setTimeZone(HttpFields.__GMT);
                    }
                    this._dateReceive[j] = (SimpleDateFormat)HttpFields.__dateReceive[j].clone();
                }
                try {
                    final Date date2 = (Date)this._dateReceive[j].parseObject(val);
                    return field._numValue = date2.getTime();
                }
                catch (Exception e2) {
                    ++j;
                    continue;
                }
                break;
            }
            if (val.endsWith(" GMT")) {
                val = val.substring(0, val.length() - 4);
                j = 0;
                while (j < this._dateReceive.length) {
                    try {
                        final Date date2 = (Date)this._dateReceive[j].parseObject(val);
                        return field._numValue = date2.getTime();
                    }
                    catch (Exception e2) {
                        ++j;
                        continue;
                    }
                    break;
                }
            }
        }
        throw new IllegalArgumentException("Cannot convert date: " + val);
    }
    
    public void putLongField(final Buffer name, final long value) {
        final Buffer v = BufferUtil.toBuffer(value);
        this.put(name, v, value);
    }
    
    public void putLongField(final String name, final long value) {
        final Buffer n = HttpHeaders.CACHE.lookup(name);
        final Buffer v = BufferUtil.toBuffer(value);
        this.put(n, v, value);
    }
    
    public void addLongField(final String name, final long value) {
        final Buffer n = HttpHeaders.CACHE.lookup(name);
        final Buffer v = BufferUtil.toBuffer(value);
        this.add(n, v, value);
    }
    
    public void addLongField(final Buffer name, final long value) {
        final Buffer v = BufferUtil.toBuffer(value);
        this.add(name, v, value);
    }
    
    public void putDateField(final Buffer name, final long date) {
        if (this._dateBuffer == null) {
            this._dateBuffer = new StringBuffer(32);
            this._calendar = new GregorianCalendar(HttpFields.__GMT);
        }
        this._dateBuffer.setLength(0);
        this._calendar.setTimeInMillis(date);
        formatDate(this._dateBuffer, this._calendar, false);
        final Buffer v = new ByteArrayBuffer(this._dateBuffer.toString());
        this.put(name, v, date);
    }
    
    public void putDateField(final String name, final long date) {
        final Buffer n = HttpHeaders.CACHE.lookup(name);
        this.putDateField(n, date);
    }
    
    public void addDateField(final String name, final long date) {
        if (this._dateBuffer == null) {
            this._dateBuffer = new StringBuffer(32);
            this._calendar = new GregorianCalendar(HttpFields.__GMT);
        }
        this._dateBuffer.setLength(0);
        this._calendar.setTimeInMillis(date);
        formatDate(this._dateBuffer, this._calendar, false);
        final Buffer n = HttpHeaders.CACHE.lookup(name);
        final Buffer v = new ByteArrayBuffer(this._dateBuffer.toString());
        this.add(n, v, date);
    }
    
    public void addSetCookie(final Cookie cookie) {
        final String name = cookie.getName();
        final String value = cookie.getValue();
        final int version = cookie.getVersion();
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Bad cookie name");
        }
        final StringBuffer buf = new StringBuffer(128);
        String name_value_params = null;
        synchronized (buf) {
            QuotedStringTokenizer.quoteIfNeeded(buf, name);
            buf.append('=');
            if (value != null && value.length() > 0) {
                QuotedStringTokenizer.quoteIfNeeded(buf, value);
            }
            if (version > 0) {
                buf.append(";Version=");
                buf.append(version);
                final String comment = cookie.getComment();
                if (comment != null && comment.length() > 0) {
                    buf.append(";Comment=");
                    QuotedStringTokenizer.quoteIfNeeded(buf, comment);
                }
            }
            final String path = cookie.getPath();
            if (path != null && path.length() > 0) {
                buf.append(";Path=");
                if (path.startsWith("\"")) {
                    buf.append(path);
                }
                else {
                    QuotedStringTokenizer.quoteIfNeeded(buf, path);
                }
            }
            final String domain = cookie.getDomain();
            if (domain != null && domain.length() > 0) {
                buf.append(";Domain=");
                QuotedStringTokenizer.quoteIfNeeded(buf, domain.toLowerCase());
            }
            final long maxAge = cookie.getMaxAge();
            if (maxAge >= 0L) {
                if (version == 0) {
                    buf.append(";Expires=");
                    if (maxAge == 0L) {
                        buf.append(HttpFields.__01Jan1970);
                    }
                    else {
                        formatDate(buf, System.currentTimeMillis() + 1000L * maxAge, true);
                    }
                }
                else {
                    buf.append(";Max-Age=");
                    buf.append(maxAge);
                }
            }
            else if (version > 0) {
                buf.append(";Discard");
            }
            if (cookie.getSecure()) {
                buf.append(";Secure");
            }
            if (cookie instanceof HttpOnlyCookie) {
                buf.append(";HttpOnly");
            }
            name_value_params = buf.toString();
        }
        this.put(HttpHeaders.EXPIRES_BUFFER, HttpFields.__01Jan1970_BUFFER);
        this.add(HttpHeaders.SET_COOKIE_BUFFER, new ByteArrayBuffer(name_value_params));
    }
    
    public void put(final Buffer buffer) throws IOException {
        for (int i = 0; i < this._fields.size(); ++i) {
            final Field field = this._fields.get(i);
            if (field != null && field._revision == this._revision) {
                field.put(buffer);
            }
        }
        BufferUtil.putCRLF(buffer);
    }
    
    public String toString() {
        try {
            final StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < this._fields.size(); ++i) {
                final Field field = this._fields.get(i);
                if (field != null && field._revision == this._revision) {
                    String tmp = field.getName();
                    if (tmp != null) {
                        buffer.append(tmp);
                    }
                    buffer.append(": ");
                    tmp = field.getValue();
                    if (tmp != null) {
                        buffer.append(tmp);
                    }
                    buffer.append("\r\n");
                }
            }
            buffer.append("\r\n");
            return buffer.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void clear() {
        ++this._revision;
        if (this._revision > 1000000) {
            this._revision = 0;
            int i = this._fields.size();
            while (i-- > 0) {
                final Field field = this._fields.get(i);
                if (field != null) {
                    field.clear();
                }
            }
        }
    }
    
    public void destroy() {
        if (this._fields != null) {
            int i = this._fields.size();
            while (i-- > 0) {
                final Field field = this._fields.get(i);
                if (field != null) {
                    this._bufferMap.remove(field.getNameBuffer());
                    field.destroy();
                }
            }
        }
        this._fields = null;
        this._dateBuffer = null;
        this._calendar = null;
        this._dateReceive = null;
    }
    
    public void add(final HttpFields fields) {
        if (fields == null) {
            return;
        }
        final Enumeration e = fields.getFieldNames();
        while (e.hasMoreElements()) {
            final String name = e.nextElement();
            final Enumeration values = fields.getValues(name);
            while (values.hasMoreElements()) {
                this.add(name, values.nextElement());
            }
        }
    }
    
    public static String valueParameters(final String value, final Map parameters) {
        if (value == null) {
            return null;
        }
        final int i = value.indexOf(59);
        if (i < 0) {
            return value;
        }
        if (parameters == null) {
            return value.substring(0, i).trim();
        }
        final StringTokenizer tok1 = new QuotedStringTokenizer(value.substring(i), ";", false, true);
        while (tok1.hasMoreTokens()) {
            final String token = tok1.nextToken();
            final StringTokenizer tok2 = new QuotedStringTokenizer(token, "= ");
            if (tok2.hasMoreTokens()) {
                final String paramName = tok2.nextToken();
                String paramVal = null;
                if (tok2.hasMoreTokens()) {
                    paramVal = tok2.nextToken();
                }
                parameters.put(paramName, paramVal);
            }
        }
        return value.substring(0, i).trim();
    }
    
    public static Float getQuality(final String value) {
        if (value == null) {
            return HttpFields.__zero;
        }
        int qe = value.indexOf(";");
        if (qe++ < 0 || qe == value.length()) {
            return HttpFields.__one;
        }
        if (value.charAt(qe++) == 'q') {
            ++qe;
            final Map.Entry entry = HttpFields.__qualities.getEntry(value, qe, value.length() - qe);
            if (entry != null) {
                return entry.getValue();
            }
        }
        final HashMap params = new HashMap(3);
        valueParameters(value, params);
        final String qs = params.get("q");
        Float q = (Float)HttpFields.__qualities.get(qs);
        if (q == null) {
            try {
                q = new Float(qs);
            }
            catch (Exception e) {
                q = HttpFields.__one;
            }
        }
        return q;
    }
    
    public static List qualityList(final Enumeration e) {
        if (e == null || !e.hasMoreElements()) {
            return Collections.EMPTY_LIST;
        }
        Object list = null;
        Object qual = null;
        while (e.hasMoreElements()) {
            final String v = e.nextElement().toString();
            final Float q = getQuality(v);
            if (q >= 0.001) {
                list = LazyList.add(list, v);
                qual = LazyList.add(qual, q);
            }
        }
        final List vl = LazyList.getList(list, false);
        if (vl.size() < 2) {
            return vl;
        }
        final List ql = LazyList.getList(qual, false);
        Float last = HttpFields.__zero;
        int i = vl.size();
        while (i-- > 0) {
            final Float q2 = ql.get(i);
            if (last.compareTo(q2) > 0) {
                final Object tmp = vl.get(i);
                vl.set(i, vl.get(i + 1));
                vl.set(i + 1, tmp);
                ql.set(i, ql.get(i + 1));
                ql.set(i + 1, q2);
                last = HttpFields.__zero;
                i = vl.size();
            }
            else {
                last = q2;
            }
        }
        ql.clear();
        return vl;
    }
    
    static {
        HttpFields.DAYS = new String[] { "Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
        HttpFields.MONTHS = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan" };
        HttpFields.__GMT = TimeZone.getTimeZone("GMT");
        __dateCache = new BufferDateCache("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        __dateReceiveFmt = new String[] { "EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss", "EEE MMM dd HH:mm:ss yyyy", "EEE, dd MMM yyyy HH:mm:ss", "EEE dd MMM yyyy HH:mm:ss zzz", "EEE dd MMM yyyy HH:mm:ss", "EEE MMM dd yyyy HH:mm:ss zzz", "EEE MMM dd yyyy HH:mm:ss", "EEE MMM-dd-yyyy HH:mm:ss zzz", "EEE MMM-dd-yyyy HH:mm:ss", "dd MMM yyyy HH:mm:ss zzz", "dd MMM yyyy HH:mm:ss", "dd-MMM-yy HH:mm:ss zzz", "dd-MMM-yy HH:mm:ss", "MMM dd HH:mm:ss yyyy zzz", "MMM dd HH:mm:ss yyyy", "EEE MMM dd HH:mm:ss yyyy zzz", "EEE, MMM dd HH:mm:ss yyyy zzz", "EEE, MMM dd HH:mm:ss yyyy", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE dd-MMM-yy HH:mm:ss zzz", "EEE dd-MMM-yy HH:mm:ss" };
        HttpFields.__dateReceiveInit = 3;
        HttpFields.__GMT.setID("GMT");
        HttpFields.__dateCache.setTimeZone(HttpFields.__GMT);
        HttpFields.__dateReceive = new SimpleDateFormat[HttpFields.__dateReceiveFmt.length];
        for (int i = 0; i < HttpFields.__dateReceiveInit; ++i) {
            (HttpFields.__dateReceive[i] = new SimpleDateFormat(HttpFields.__dateReceiveFmt[i], Locale.US)).setTimeZone(HttpFields.__GMT);
        }
        __01Jan1970 = formatDate(0L, true).trim();
        __01Jan1970_BUFFER = new ByteArrayBuffer(HttpFields.__01Jan1970);
        HttpFields.__one = new Float("1.0");
        HttpFields.__zero = new Float("0.0");
        (HttpFields.__qualities = new StringMap()).put(null, HttpFields.__one);
        HttpFields.__qualities.put("1.0", HttpFields.__one);
        HttpFields.__qualities.put("1", HttpFields.__one);
        HttpFields.__qualities.put("0.9", new Float("0.9"));
        HttpFields.__qualities.put("0.8", new Float("0.8"));
        HttpFields.__qualities.put("0.7", new Float("0.7"));
        HttpFields.__qualities.put("0.66", new Float("0.66"));
        HttpFields.__qualities.put("0.6", new Float("0.6"));
        HttpFields.__qualities.put("0.5", new Float("0.5"));
        HttpFields.__qualities.put("0.4", new Float("0.4"));
        HttpFields.__qualities.put("0.33", new Float("0.33"));
        HttpFields.__qualities.put("0.3", new Float("0.3"));
        HttpFields.__qualities.put("0.2", new Float("0.2"));
        HttpFields.__qualities.put("0.1", new Float("0.1"));
        HttpFields.__qualities.put("0", HttpFields.__zero);
        HttpFields.__qualities.put("0.0", HttpFields.__zero);
    }
    
    public static final class Field
    {
        private Buffer _name;
        private Buffer _value;
        private String _stringValue;
        private long _numValue;
        private Field _next;
        private Field _prev;
        private int _revision;
        
        private Field(final Buffer name, final Buffer value, final long numValue, final int revision) {
            this._name = name.asImmutableBuffer();
            this._value = (value.isImmutable() ? value : new View(value));
            this._next = null;
            this._prev = null;
            this._revision = revision;
            this._numValue = numValue;
            this._stringValue = null;
        }
        
        private void clear() {
            this._revision = -1;
        }
        
        private void destroy() {
            this._name = null;
            this._value = null;
            this._next = null;
            this._prev = null;
            this._stringValue = null;
        }
        
        private void reset(final Buffer value, final long numValue, final int revision) {
            this._revision = revision;
            if (this._value == null) {
                this._value = (value.isImmutable() ? value : new View(value));
                this._numValue = numValue;
                this._stringValue = null;
            }
            else if (value.isImmutable()) {
                this._value = value;
                this._numValue = numValue;
                this._stringValue = null;
            }
            else {
                if (this._value instanceof View) {
                    ((View)this._value).update(value);
                }
                else {
                    this._value = new View(value);
                }
                this._numValue = numValue;
                if (this._stringValue != null) {
                    if (this._stringValue.length() != value.length()) {
                        this._stringValue = null;
                    }
                    else {
                        int i = value.length();
                        while (i-- > 0) {
                            if (value.peek(value.getIndex() + i) != this._stringValue.charAt(i)) {
                                this._stringValue = null;
                                break;
                            }
                        }
                    }
                }
            }
        }
        
        public void put(final Buffer buffer) throws IOException {
            int o = (this._name instanceof BufferCache.CachedBuffer) ? ((BufferCache.CachedBuffer)this._name).getOrdinal() : -1;
            if (o >= 0) {
                buffer.put(this._name);
            }
            else {
                int s = this._name.getIndex();
                final int e = this._name.putIndex();
                while (s < e) {
                    final byte b = this._name.peek(s++);
                    switch (b) {
                        case 10:
                        case 13:
                        case 58: {
                            continue;
                        }
                        default: {
                            buffer.put(b);
                            continue;
                        }
                    }
                }
            }
            buffer.put((byte)58);
            buffer.put((byte)32);
            o = ((this._value instanceof BufferCache.CachedBuffer) ? ((BufferCache.CachedBuffer)this._value).getOrdinal() : -1);
            if (o >= 0 || this._numValue >= 0L) {
                buffer.put(this._value);
            }
            else {
                int s = this._value.getIndex();
                final int e = this._value.putIndex();
                while (s < e) {
                    final byte b = this._value.peek(s++);
                    switch (b) {
                        case 10:
                        case 13: {
                            continue;
                        }
                        default: {
                            buffer.put(b);
                            continue;
                        }
                    }
                }
            }
            BufferUtil.putCRLF(buffer);
        }
        
        public String getName() {
            return BufferUtil.to8859_1_String(this._name);
        }
        
        Buffer getNameBuffer() {
            return this._name;
        }
        
        public int getNameOrdinal() {
            return HttpHeaders.CACHE.getOrdinal(this._name);
        }
        
        public String getValue() {
            if (this._stringValue == null) {
                this._stringValue = BufferUtil.to8859_1_String(this._value);
            }
            return this._stringValue;
        }
        
        public Buffer getValueBuffer() {
            return this._value;
        }
        
        public int getValueOrdinal() {
            return HttpHeaderValues.CACHE.getOrdinal(this._value);
        }
        
        public int getIntValue() {
            return (int)this.getLongValue();
        }
        
        public long getLongValue() {
            if (this._numValue == -1L) {
                this._numValue = BufferUtil.toLong(this._value);
            }
            return this._numValue;
        }
        
        public String toString() {
            return "[" + ((this._prev == null) ? "" : "<-") + this.getName() + "=" + this._revision + "=" + this._value + ((this._next == null) ? "" : "->") + "]";
        }
    }
}
