// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Fields implements Iterable<Field>
{
    private final boolean caseSensitive;
    private final Map<String, Field> fields;
    
    public Fields() {
        this(false);
    }
    
    public Fields(final boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        this.fields = new LinkedHashMap<String, Field>();
    }
    
    public Fields(final Fields original, final boolean immutable) {
        this.caseSensitive = original.caseSensitive;
        final Map<String, Field> copy = new LinkedHashMap<String, Field>();
        copy.putAll(original.fields);
        this.fields = (immutable ? Collections.unmodifiableMap((Map<? extends String, ? extends Field>)copy) : copy);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final Fields that = (Fields)obj;
        if (this.getSize() != that.getSize()) {
            return false;
        }
        if (this.caseSensitive != that.caseSensitive) {
            return false;
        }
        for (final Map.Entry<String, Field> entry : this.fields.entrySet()) {
            final String name = entry.getKey();
            final Field value = entry.getValue();
            if (!value.equals(that.get(name), this.caseSensitive)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return this.fields.hashCode();
    }
    
    public Set<String> getNames() {
        final Set<String> result = new LinkedHashSet<String>();
        for (final Field field : this.fields.values()) {
            result.add(field.getName());
        }
        return result;
    }
    
    private String normalizeName(final String name) {
        return this.caseSensitive ? name : name.toLowerCase(Locale.ENGLISH);
    }
    
    public Field get(final String name) {
        return this.fields.get(this.normalizeName(name));
    }
    
    public void put(final String name, final String value) {
        final Field field = new Field(name, value);
        this.fields.put(this.normalizeName(name), field);
    }
    
    public void put(final Field field) {
        if (field != null) {
            this.fields.put(this.normalizeName(field.getName()), field);
        }
    }
    
    public void add(final String name, final String value) {
        final String key = this.normalizeName(name);
        Field field = this.fields.get(key);
        if (field == null) {
            field = new Field(name, value);
            this.fields.put(key, field);
        }
        else {
            field = new Field(field.getName(), (List)field.getValues(), new String[] { value });
            this.fields.put(key, field);
        }
    }
    
    public Field remove(final String name) {
        return this.fields.remove(this.normalizeName(name));
    }
    
    public void clear() {
        this.fields.clear();
    }
    
    public boolean isEmpty() {
        return this.fields.isEmpty();
    }
    
    public int getSize() {
        return this.fields.size();
    }
    
    @Override
    public Iterator<Field> iterator() {
        return this.fields.values().iterator();
    }
    
    @Override
    public String toString() {
        return this.fields.toString();
    }
    
    public static class Field
    {
        private final String name;
        private final List<String> values;
        
        public Field(final String name, final String value) {
            this(name, Collections.singletonList(value), new String[0]);
        }
        
        private Field(final String name, final List<String> values, final String... moreValues) {
            this.name = name;
            final List<String> list = new ArrayList<String>(values.size() + moreValues.length);
            list.addAll(values);
            list.addAll(Arrays.asList(moreValues));
            this.values = Collections.unmodifiableList((List<? extends String>)list);
        }
        
        public boolean equals(final Field that, final boolean caseSensitive) {
            if (this == that) {
                return true;
            }
            if (that == null) {
                return false;
            }
            if (caseSensitive) {
                return this.equals(that);
            }
            return this.name.equalsIgnoreCase(that.name) && this.values.equals(that.values);
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || this.getClass() != obj.getClass()) {
                return false;
            }
            final Field that = (Field)obj;
            return this.name.equals(that.name) && this.values.equals(that.values);
        }
        
        @Override
        public int hashCode() {
            int result = this.name.hashCode();
            result = 31 * result + this.values.hashCode();
            return result;
        }
        
        public String getName() {
            return this.name;
        }
        
        public String getValue() {
            return this.values.get(0);
        }
        
        public Integer getValueAsInt() {
            final String value = this.getValue();
            return (value == null) ? null : Integer.valueOf(value);
        }
        
        public List<String> getValues() {
            return this.values;
        }
        
        public boolean hasMultipleValues() {
            return this.values.size() > 1;
        }
        
        @Override
        public String toString() {
            return String.format("%s=%s", this.name, this.values);
        }
    }
}
