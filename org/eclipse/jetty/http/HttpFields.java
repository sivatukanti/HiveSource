// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import org.eclipse.jetty.util.ArrayTernaryTrie;
import org.eclipse.jetty.util.log.Log;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Map;
import org.eclipse.jetty.util.QuotedStringTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Spliterator;
import java.util.stream.StreamSupport;
import java.util.stream.Stream;
import java.util.Iterator;
import java.util.Arrays;
import org.eclipse.jetty.util.Trie;
import org.eclipse.jetty.util.log.Logger;

public class HttpFields implements Iterable<HttpField>
{
    @Deprecated
    public static final String __separators = ", \t";
    private static final Logger LOG;
    private HttpField[] _fields;
    private int _size;
    @Deprecated
    private static final Float __one;
    @Deprecated
    private static final Float __zero;
    @Deprecated
    private static final Trie<Float> __qualities;
    
    public HttpFields() {
        this._fields = new HttpField[20];
    }
    
    public HttpFields(final int capacity) {
        this._fields = new HttpField[capacity];
    }
    
    public HttpFields(final HttpFields fields) {
        this._fields = Arrays.copyOf(fields._fields, fields._fields.length + 10);
        this._size = fields._size;
    }
    
    public int size() {
        return this._size;
    }
    
    @Override
    public Iterator<HttpField> iterator() {
        return new Itr();
    }
    
    public Stream<HttpField> stream() {
        return StreamSupport.stream((Spliterator<HttpField>)Arrays.spliterator((T[])this._fields, 0, this._size), false);
    }
    
    public Set<String> getFieldNamesCollection() {
        final Set<String> set = new HashSet<String>(this._size);
        for (final HttpField f : this) {
            if (f != null) {
                set.add(f.getName());
            }
        }
        return set;
    }
    
    public Enumeration<String> getFieldNames() {
        return Collections.enumeration(this.getFieldNamesCollection());
    }
    
    public HttpField getField(final int index) {
        if (index >= this._size) {
            throw new NoSuchElementException();
        }
        return this._fields[index];
    }
    
    public HttpField getField(final HttpHeader header) {
        for (int i = 0; i < this._size; ++i) {
            final HttpField f = this._fields[i];
            if (f.getHeader() == header) {
                return f;
            }
        }
        return null;
    }
    
    public HttpField getField(final String name) {
        for (int i = 0; i < this._size; ++i) {
            final HttpField f = this._fields[i];
            if (f.getName().equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;
    }
    
    public boolean contains(final HttpField field) {
        int i = this._size;
        while (i-- > 0) {
            final HttpField f = this._fields[i];
            if (f.isSameName(field) && (f.equals(field) || f.contains(field.getValue()))) {
                return true;
            }
        }
        return false;
    }
    
    public boolean contains(final HttpHeader header, final String value) {
        int i = this._size;
        while (i-- > 0) {
            final HttpField f = this._fields[i];
            if (f.getHeader() == header && f.contains(value)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean contains(final String name, final String value) {
        int i = this._size;
        while (i-- > 0) {
            final HttpField f = this._fields[i];
            if (f.getName().equalsIgnoreCase(name) && f.contains(value)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean contains(final HttpHeader header) {
        int i = this._size;
        while (i-- > 0) {
            final HttpField f = this._fields[i];
            if (f.getHeader() == header) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsKey(final String name) {
        int i = this._size;
        while (i-- > 0) {
            final HttpField f = this._fields[i];
            if (f.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    
    @Deprecated
    public String getStringField(final HttpHeader header) {
        return this.get(header);
    }
    
    public String get(final HttpHeader header) {
        for (int i = 0; i < this._size; ++i) {
            final HttpField f = this._fields[i];
            if (f.getHeader() == header) {
                return f.getValue();
            }
        }
        return null;
    }
    
    @Deprecated
    public String getStringField(final String name) {
        return this.get(name);
    }
    
    public String get(final String header) {
        for (int i = 0; i < this._size; ++i) {
            final HttpField f = this._fields[i];
            if (f.getName().equalsIgnoreCase(header)) {
                return f.getValue();
            }
        }
        return null;
    }
    
    public List<String> getValuesList(final HttpHeader header) {
        final List<String> list = new ArrayList<String>();
        for (final HttpField f : this) {
            if (f.getHeader() == header) {
                list.add(f.getValue());
            }
        }
        return list;
    }
    
    public List<String> getValuesList(final String name) {
        final List<String> list = new ArrayList<String>();
        for (final HttpField f : this) {
            if (f.getName().equalsIgnoreCase(name)) {
                list.add(f.getValue());
            }
        }
        return list;
    }
    
    public boolean addCSV(final HttpHeader header, final String... values) {
        QuotedCSV existing = null;
        for (final HttpField f : this) {
            if (f.getHeader() == header) {
                if (existing == null) {
                    existing = new QuotedCSV(false, new String[0]);
                }
                existing.addValue(f.getValue());
            }
        }
        final String value = this.addCSV(existing, values);
        if (value != null) {
            this.add(header, value);
            return true;
        }
        return false;
    }
    
    public boolean addCSV(final String name, final String... values) {
        QuotedCSV existing = null;
        for (final HttpField f : this) {
            if (f.getName().equalsIgnoreCase(name)) {
                if (existing == null) {
                    existing = new QuotedCSV(false, new String[0]);
                }
                existing.addValue(f.getValue());
            }
        }
        final String value = this.addCSV(existing, values);
        if (value != null) {
            this.add(name, value);
            return true;
        }
        return false;
    }
    
    protected String addCSV(final QuotedCSV existing, final String... values) {
        boolean add = true;
        if (existing != null && !existing.isEmpty()) {
            add = false;
            int i = values.length;
            while (i-- > 0) {
                final String unquoted = QuotedCSV.unquote(values[i]);
                if (existing.getValues().contains(unquoted)) {
                    values[i] = null;
                }
                else {
                    add = true;
                }
            }
        }
        if (add) {
            final StringBuilder value = new StringBuilder();
            for (final String v : values) {
                if (v != null) {
                    if (value.length() > 0) {
                        value.append(", ");
                    }
                    value.append(v);
                }
            }
            if (value.length() > 0) {
                return value.toString();
            }
        }
        return null;
    }
    
    public List<String> getCSV(final HttpHeader header, final boolean keepQuotes) {
        QuotedCSV values = null;
        for (final HttpField f : this) {
            if (f.getHeader() == header) {
                if (values == null) {
                    values = new QuotedCSV(keepQuotes, new String[0]);
                }
                values.addValue(f.getValue());
            }
        }
        return (values == null) ? Collections.emptyList() : values.getValues();
    }
    
    public List<String> getCSV(final String name, final boolean keepQuotes) {
        QuotedCSV values = null;
        for (final HttpField f : this) {
            if (f.getName().equalsIgnoreCase(name)) {
                if (values == null) {
                    values = new QuotedCSV(keepQuotes, new String[0]);
                }
                values.addValue(f.getValue());
            }
        }
        return (values == null) ? Collections.emptyList() : values.getValues();
    }
    
    public List<String> getQualityCSV(final HttpHeader header) {
        QuotedQualityCSV values = null;
        for (final HttpField f : this) {
            if (f.getHeader() == header) {
                if (values == null) {
                    values = new QuotedQualityCSV(new String[0]);
                }
                values.addValue(f.getValue());
            }
        }
        return (values == null) ? Collections.emptyList() : values.getValues();
    }
    
    public List<String> getQualityCSV(final String name) {
        QuotedQualityCSV values = null;
        for (final HttpField f : this) {
            if (f.getName().equalsIgnoreCase(name)) {
                if (values == null) {
                    values = new QuotedQualityCSV(new String[0]);
                }
                values.addValue(f.getValue());
            }
        }
        return (values == null) ? Collections.emptyList() : values.getValues();
    }
    
    public Enumeration<String> getValues(final String name) {
        for (int i = 0; i < this._size; ++i) {
            final HttpField f = this._fields[i];
            if (f.getName().equalsIgnoreCase(name) && f.getValue() != null) {
                final int first = i;
                return new Enumeration<String>() {
                    HttpField field = f;
                    int i = first + 1;
                    
                    @Override
                    public boolean hasMoreElements() {
                        if (this.field == null) {
                            while (this.i < HttpFields.this._size) {
                                this.field = HttpFields.this._fields[this.i++];
                                if (this.field.getName().equalsIgnoreCase(name) && this.field.getValue() != null) {
                                    return true;
                                }
                            }
                            this.field = null;
                            return false;
                        }
                        return true;
                    }
                    
                    @Override
                    public String nextElement() throws NoSuchElementException {
                        if (this.hasMoreElements()) {
                            final String value = this.field.getValue();
                            this.field = null;
                            return value;
                        }
                        throw new NoSuchElementException();
                    }
                };
            }
        }
        final List<String> empty = Collections.emptyList();
        return Collections.enumeration(empty);
    }
    
    @Deprecated
    public Enumeration<String> getValues(final String name, final String separators) {
        final Enumeration<String> e = this.getValues(name);
        if (e == null) {
            return null;
        }
        return new Enumeration<String>() {
            QuotedStringTokenizer tok = null;
            
            @Override
            public boolean hasMoreElements() {
                if (this.tok != null && this.tok.hasMoreElements()) {
                    return true;
                }
                while (e.hasMoreElements()) {
                    final String value = e.nextElement();
                    if (value != null) {
                        this.tok = new QuotedStringTokenizer(value, separators, false, false);
                        if (this.tok.hasMoreElements()) {
                            return true;
                        }
                        continue;
                    }
                }
                this.tok = null;
                return false;
            }
            
            @Override
            public String nextElement() throws NoSuchElementException {
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
    
    public void put(final HttpField field) {
        boolean put = false;
        int i = this._size;
        while (i-- > 0) {
            final HttpField f = this._fields[i];
            if (f.isSameName(field)) {
                if (put) {
                    System.arraycopy(this._fields, i + 1, this._fields, i, --this._size - i);
                }
                else {
                    this._fields[i] = field;
                    put = true;
                }
            }
        }
        if (!put) {
            this.add(field);
        }
    }
    
    public void put(final String name, final String value) {
        if (value == null) {
            this.remove(name);
        }
        else {
            this.put(new HttpField(name, value));
        }
    }
    
    public void put(final HttpHeader header, final HttpHeaderValue value) {
        this.put(header, value.toString());
    }
    
    public void put(final HttpHeader header, final String value) {
        if (value == null) {
            this.remove(header);
        }
        else {
            this.put(new HttpField(header, value));
        }
    }
    
    public void put(final String name, final List<String> list) {
        this.remove(name);
        for (final String v : list) {
            if (v != null) {
                this.add(name, v);
            }
        }
    }
    
    public void add(final String name, final String value) {
        if (value == null) {
            return;
        }
        final HttpField field = new HttpField(name, value);
        this.add(field);
    }
    
    public void add(final HttpHeader header, final HttpHeaderValue value) {
        this.add(header, value.toString());
    }
    
    public void add(final HttpHeader header, final String value) {
        if (value == null) {
            throw new IllegalArgumentException("null value");
        }
        final HttpField field = new HttpField(header, value);
        this.add(field);
    }
    
    public HttpField remove(final HttpHeader name) {
        HttpField removed = null;
        int i = this._size;
        while (i-- > 0) {
            final HttpField f = this._fields[i];
            if (f.getHeader() == name) {
                removed = f;
                System.arraycopy(this._fields, i + 1, this._fields, i, --this._size - i);
            }
        }
        return removed;
    }
    
    public HttpField remove(final String name) {
        HttpField removed = null;
        int i = this._size;
        while (i-- > 0) {
            final HttpField f = this._fields[i];
            if (f.getName().equalsIgnoreCase(name)) {
                removed = f;
                System.arraycopy(this._fields, i + 1, this._fields, i, --this._size - i);
            }
        }
        return removed;
    }
    
    public long getLongField(final String name) throws NumberFormatException {
        final HttpField field = this.getField(name);
        return (field == null) ? -1L : field.getLongValue();
    }
    
    public long getDateField(final String name) {
        final HttpField field = this.getField(name);
        if (field == null) {
            return -1L;
        }
        final String val = valueParameters(field.getValue(), null);
        if (val == null) {
            return -1L;
        }
        final long date = DateParser.parseDate(val);
        if (date == -1L) {
            throw new IllegalArgumentException("Cannot convert date: " + val);
        }
        return date;
    }
    
    public void putLongField(final HttpHeader name, final long value) {
        final String v = Long.toString(value);
        this.put(name, v);
    }
    
    public void putLongField(final String name, final long value) {
        final String v = Long.toString(value);
        this.put(name, v);
    }
    
    public void putDateField(final HttpHeader name, final long date) {
        final String d = DateGenerator.formatDate(date);
        this.put(name, d);
    }
    
    public void putDateField(final String name, final long date) {
        final String d = DateGenerator.formatDate(date);
        this.put(name, d);
    }
    
    public void addDateField(final String name, final long date) {
        final String d = DateGenerator.formatDate(date);
        this.add(name, d);
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        for (final HttpField field : this._fields) {
            hash += field.hashCode();
        }
        return hash;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HttpFields)) {
            return false;
        }
        final HttpFields that = (HttpFields)o;
        if (this.size() != that.size()) {
            return false;
        }
    Label_0039:
        for (final HttpField fi : this) {
            for (final HttpField fa : that) {
                if (fi.equals(fa)) {
                    continue Label_0039;
                }
            }
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        try {
            final StringBuilder buffer = new StringBuilder();
            for (final HttpField field : this) {
                if (field != null) {
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
            HttpFields.LOG.warn(e);
            return e.toString();
        }
    }
    
    public void clear() {
        this._size = 0;
    }
    
    public void add(final HttpField field) {
        if (field != null) {
            if (this._size == this._fields.length) {
                this._fields = Arrays.copyOf(this._fields, this._size * 2);
            }
            this._fields[this._size++] = field;
        }
    }
    
    public void addAll(final HttpFields fields) {
        for (int i = 0; i < fields._size; ++i) {
            this.add(fields._fields[i]);
        }
    }
    
    public void add(final HttpFields fields) {
        if (fields == null) {
            return;
        }
        final Enumeration<String> e = fields.getFieldNames();
        while (e.hasMoreElements()) {
            final String name = e.nextElement();
            final Enumeration<String> values = fields.getValues(name);
            while (values.hasMoreElements()) {
                this.add(name, values.nextElement());
            }
        }
    }
    
    public static String stripParameters(final String value) {
        if (value == null) {
            return null;
        }
        final int i = value.indexOf(59);
        if (i < 0) {
            return value;
        }
        return value.substring(0, i).trim();
    }
    
    public static String valueParameters(final String value, final Map<String, String> parameters) {
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
    
    @Deprecated
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
            final Float q = HttpFields.__qualities.get(value, qe, value.length() - qe);
            if (q != null) {
                return q;
            }
        }
        final Map<String, String> params = new HashMap<String, String>(4);
        valueParameters(value, params);
        String qs = params.get("q");
        if (qs == null) {
            qs = "*";
        }
        Float q2 = HttpFields.__qualities.get(qs);
        if (q2 == null) {
            try {
                q2 = new Float(qs);
            }
            catch (Exception e) {
                q2 = HttpFields.__one;
            }
        }
        return q2;
    }
    
    @Deprecated
    public static List<String> qualityList(final Enumeration<String> e) {
        if (e == null || !e.hasMoreElements()) {
            return Collections.emptyList();
        }
        final QuotedQualityCSV values = new QuotedQualityCSV(new String[0]);
        while (e.hasMoreElements()) {
            values.addValue(e.nextElement());
        }
        return values.getValues();
    }
    
    static {
        LOG = Log.getLogger(HttpFields.class);
        __one = new Float("1.0");
        __zero = new Float("0.0");
        (__qualities = new ArrayTernaryTrie<Float>()).put("*", HttpFields.__one);
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
    
    private class Itr implements Iterator<HttpField>
    {
        int _cursor;
        int _last;
        
        private Itr() {
            this._last = -1;
        }
        
        @Override
        public boolean hasNext() {
            return this._cursor != HttpFields.this._size;
        }
        
        @Override
        public HttpField next() {
            final int i = this._cursor;
            if (i >= HttpFields.this._size) {
                throw new NoSuchElementException();
            }
            this._cursor = i + 1;
            final HttpField[] access$200 = HttpFields.this._fields;
            final int last = i;
            this._last = last;
            return access$200[last];
        }
        
        @Override
        public void remove() {
            if (this._last < 0) {
                throw new IllegalStateException();
            }
            System.arraycopy(HttpFields.this._fields, this._last + 1, HttpFields.this._fields, this._last, --HttpFields.this._size - this._last);
            this._cursor = this._last;
            this._last = -1;
        }
    }
}
