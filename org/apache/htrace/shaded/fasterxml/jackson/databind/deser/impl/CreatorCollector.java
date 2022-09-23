// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Member;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedMember;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeBindings;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.StdValueInstantiator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.ValueInstantiator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.CreatorProperty;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import org.apache.htrace.shaded.fasterxml.jackson.databind.BeanDescription;

public class CreatorCollector
{
    protected final BeanDescription _beanDesc;
    protected final boolean _canFixAccess;
    protected AnnotatedWithParams _defaultConstructor;
    protected AnnotatedWithParams _stringCreator;
    protected AnnotatedWithParams _intCreator;
    protected AnnotatedWithParams _longCreator;
    protected AnnotatedWithParams _doubleCreator;
    protected AnnotatedWithParams _booleanCreator;
    protected AnnotatedWithParams _delegateCreator;
    protected CreatorProperty[] _delegateArgs;
    protected AnnotatedWithParams _propertyBasedCreator;
    protected CreatorProperty[] _propertyBasedArgs;
    protected AnnotatedParameter _incompleteParameter;
    
    public CreatorCollector(final BeanDescription beanDesc, final boolean canFixAccess) {
        this._propertyBasedArgs = null;
        this._beanDesc = beanDesc;
        this._canFixAccess = canFixAccess;
    }
    
    public ValueInstantiator constructValueInstantiator(final DeserializationConfig config) {
        boolean maybeVanilla = this._delegateCreator == null;
        JavaType delegateType;
        if (maybeVanilla) {
            delegateType = null;
        }
        else {
            int ix = 0;
            if (this._delegateArgs != null) {
                for (int i = 0, len = this._delegateArgs.length; i < len; ++i) {
                    if (this._delegateArgs[i] == null) {
                        ix = i;
                        break;
                    }
                }
            }
            final TypeBindings bindings = this._beanDesc.bindingsForBeanType();
            delegateType = bindings.resolveType(this._delegateCreator.getGenericParameterType(ix));
        }
        final JavaType type = this._beanDesc.getType();
        maybeVanilla &= (this._propertyBasedCreator == null && this._delegateCreator == null && this._stringCreator == null && this._longCreator == null && this._doubleCreator == null && this._booleanCreator == null);
        if (maybeVanilla) {
            final Class<?> rawType = type.getRawClass();
            if (rawType == Collection.class || rawType == List.class || rawType == ArrayList.class) {
                return new Vanilla(1);
            }
            if (rawType == Map.class || rawType == LinkedHashMap.class) {
                return new Vanilla(2);
            }
            if (rawType == HashMap.class) {
                return new Vanilla(3);
            }
        }
        final StdValueInstantiator inst = new StdValueInstantiator(config, type);
        inst.configureFromObjectSettings(this._defaultConstructor, this._delegateCreator, delegateType, this._delegateArgs, this._propertyBasedCreator, this._propertyBasedArgs);
        inst.configureFromStringCreator(this._stringCreator);
        inst.configureFromIntCreator(this._intCreator);
        inst.configureFromLongCreator(this._longCreator);
        inst.configureFromDoubleCreator(this._doubleCreator);
        inst.configureFromBooleanCreator(this._booleanCreator);
        inst.configureIncompleteParameter(this._incompleteParameter);
        return inst;
    }
    
    public void setDefaultCreator(final AnnotatedWithParams creator) {
        this._defaultConstructor = this._fixAccess(creator);
    }
    
    public void addStringCreator(final AnnotatedWithParams creator) {
        this._stringCreator = this.verifyNonDup(creator, this._stringCreator, "String");
    }
    
    public void addIntCreator(final AnnotatedWithParams creator) {
        this._intCreator = this.verifyNonDup(creator, this._intCreator, "int");
    }
    
    public void addLongCreator(final AnnotatedWithParams creator) {
        this._longCreator = this.verifyNonDup(creator, this._longCreator, "long");
    }
    
    public void addDoubleCreator(final AnnotatedWithParams creator) {
        this._doubleCreator = this.verifyNonDup(creator, this._doubleCreator, "double");
    }
    
    public void addBooleanCreator(final AnnotatedWithParams creator) {
        this._booleanCreator = this.verifyNonDup(creator, this._booleanCreator, "boolean");
    }
    
    public void addDelegatingCreator(final AnnotatedWithParams creator, final CreatorProperty[] injectables) {
        this._delegateCreator = this.verifyNonDup(creator, this._delegateCreator, "delegate");
        this._delegateArgs = injectables;
    }
    
    public void addPropertyCreator(final AnnotatedWithParams creator, final CreatorProperty[] properties) {
        this._propertyBasedCreator = this.verifyNonDup(creator, this._propertyBasedCreator, "property-based");
        if (properties.length > 1) {
            final HashMap<String, Integer> names = new HashMap<String, Integer>();
            for (int i = 0, len = properties.length; i < len; ++i) {
                final String name = properties[i].getName();
                if (name.length() != 0 || properties[i].getInjectableValueId() == null) {
                    final Integer old = names.put(name, i);
                    if (old != null) {
                        throw new IllegalArgumentException("Duplicate creator property \"" + name + "\" (index " + old + " vs " + i + ")");
                    }
                }
            }
        }
        this._propertyBasedArgs = properties;
    }
    
    public void addIncompeteParameter(final AnnotatedParameter parameter) {
        if (this._incompleteParameter == null) {
            this._incompleteParameter = parameter;
        }
    }
    
    public boolean hasDefaultCreator() {
        return this._defaultConstructor != null;
    }
    
    private <T extends AnnotatedMember> T _fixAccess(final T member) {
        if (member != null && this._canFixAccess) {
            ClassUtil.checkAndFixAccess((Member)member.getAnnotated());
        }
        return member;
    }
    
    protected AnnotatedWithParams verifyNonDup(final AnnotatedWithParams newOne, final AnnotatedWithParams oldOne, final String type) {
        if (oldOne != null && oldOne.getClass() == newOne.getClass()) {
            throw new IllegalArgumentException("Conflicting " + type + " creators: already had " + oldOne + ", encountered " + newOne);
        }
        return this._fixAccess(newOne);
    }
    
    protected static final class Vanilla extends ValueInstantiator implements Serializable
    {
        private static final long serialVersionUID = 1L;
        public static final int TYPE_COLLECTION = 1;
        public static final int TYPE_MAP = 2;
        public static final int TYPE_HASH_MAP = 3;
        private final int _type;
        
        public Vanilla(final int t) {
            this._type = t;
        }
        
        @Override
        public String getValueTypeDesc() {
            switch (this._type) {
                case 1: {
                    return ArrayList.class.getName();
                }
                case 2: {
                    return LinkedHashMap.class.getName();
                }
                case 3: {
                    return HashMap.class.getName();
                }
                default: {
                    return Object.class.getName();
                }
            }
        }
        
        @Override
        public boolean canInstantiate() {
            return true;
        }
        
        @Override
        public boolean canCreateUsingDefault() {
            return true;
        }
        
        @Override
        public Object createUsingDefault(final DeserializationContext ctxt) throws IOException {
            switch (this._type) {
                case 1: {
                    return new ArrayList();
                }
                case 2: {
                    return new LinkedHashMap();
                }
                case 3: {
                    return new HashMap();
                }
                default: {
                    throw new IllegalStateException("Unknown type " + this._type);
                }
            }
        }
    }
}
