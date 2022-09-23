// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.parameter.multivalued;

import java.util.TreeSet;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import com.sun.jersey.spi.StringReader;
import java.util.Collection;

abstract class CollectionStringReaderExtractor<V extends Collection> extends AbstractStringReaderExtractor
{
    protected CollectionStringReaderExtractor(final StringReader sr, final String parameter, final String defaultStringValue) {
        super(sr, parameter, defaultStringValue);
    }
    
    @Override
    public Object extract(final MultivaluedMap<String, String> parameters) {
        final List<String> stringList = parameters.get(this.parameter);
        if (stringList != null) {
            final V valueList = this.getInstance();
            for (final String v : stringList) {
                valueList.add(this.sr.fromString(v));
            }
            return valueList;
        }
        if (this.defaultStringValue != null) {
            final V valueList = this.getInstance();
            valueList.add(this.sr.fromString(this.defaultStringValue));
            return valueList;
        }
        return this.getInstance();
    }
    
    protected abstract V getInstance();
    
    static MultivaluedParameterExtractor getInstance(final Class c, final StringReader sr, final String parameter, final String defaultValueString) {
        if (List.class == c) {
            return new ListValueOf(sr, parameter, defaultValueString);
        }
        if (Set.class == c) {
            return new SetValueOf(sr, parameter, defaultValueString);
        }
        if (SortedSet.class == c) {
            return new SortedSetValueOf(sr, parameter, defaultValueString);
        }
        throw new RuntimeException();
    }
    
    private static final class ListValueOf extends CollectionStringReaderExtractor<List>
    {
        ListValueOf(final StringReader sr, final String parameter, final String defaultValueString) {
            super(sr, parameter, defaultValueString);
        }
        
        @Override
        protected List getInstance() {
            return new ArrayList();
        }
    }
    
    private static final class SetValueOf extends CollectionStringReaderExtractor<Set>
    {
        SetValueOf(final StringReader sr, final String parameter, final String defaultValueString) {
            super(sr, parameter, defaultValueString);
        }
        
        @Override
        protected Set getInstance() {
            return new HashSet();
        }
    }
    
    private static final class SortedSetValueOf extends CollectionStringReaderExtractor<SortedSet>
    {
        SortedSetValueOf(final StringReader sr, final String parameter, final String defaultValueString) {
            super(sr, parameter, defaultValueString);
        }
        
        @Override
        protected SortedSet getInstance() {
            return new TreeSet();
        }
    }
}
