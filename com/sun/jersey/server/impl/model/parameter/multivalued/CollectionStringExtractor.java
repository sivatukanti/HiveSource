// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model.parameter.multivalued;

import java.util.TreeSet;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.Set;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Collection;

abstract class CollectionStringExtractor<V extends Collection<String>> implements MultivaluedParameterExtractor
{
    final String parameter;
    final String defaultValue;
    
    protected CollectionStringExtractor(final String parameter, final String defaultValue) {
        this.parameter = parameter;
        this.defaultValue = defaultValue;
    }
    
    @Override
    public String getName() {
        return this.parameter;
    }
    
    @Override
    public String getDefaultStringValue() {
        return this.defaultValue;
    }
    
    @Override
    public Object extract(final MultivaluedMap<String, String> parameters) {
        final List<String> stringList = parameters.get(this.parameter);
        if (stringList != null) {
            final V copy = this.getInstance();
            copy.addAll(stringList);
            return copy;
        }
        if (this.defaultValue != null) {
            final V l = this.getInstance();
            l.add(this.defaultValue);
            return l;
        }
        return this.getInstance();
    }
    
    protected abstract V getInstance();
    
    static MultivaluedParameterExtractor getInstance(final Class c, final String parameter, final String defaultValue) {
        if (List.class == c) {
            return new ListString(parameter, defaultValue);
        }
        if (Set.class == c) {
            return new SetString(parameter, defaultValue);
        }
        if (SortedSet.class == c) {
            return new SortedSetString(parameter, defaultValue);
        }
        throw new RuntimeException();
    }
    
    private static final class ListString extends CollectionStringExtractor<List<String>>
    {
        public ListString(final String parameter, final String defaultValue) {
            super(parameter, defaultValue);
        }
        
        @Override
        protected List<String> getInstance() {
            return new ArrayList<String>();
        }
    }
    
    private static final class SetString extends CollectionStringExtractor<Set<String>>
    {
        public SetString(final String parameter, final String defaultValue) {
            super(parameter, defaultValue);
        }
        
        @Override
        protected Set<String> getInstance() {
            return new HashSet<String>();
        }
    }
    
    private static final class SortedSetString extends CollectionStringExtractor<SortedSet<String>>
    {
        public SortedSetString(final String parameter, final String defaultValue) {
            super(parameter, defaultValue);
        }
        
        @Override
        protected SortedSet<String> getInstance() {
            return new TreeSet<String>();
        }
    }
}
