// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.module;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationConfig;
import java.lang.reflect.Modifier;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.ClassKey;
import java.util.HashMap;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AbstractTypeResolver;

public class SimpleAbstractTypeResolver extends AbstractTypeResolver implements Serializable
{
    private static final long serialVersionUID = 8635483102371490919L;
    protected final HashMap<ClassKey, Class<?>> _mappings;
    
    public SimpleAbstractTypeResolver() {
        this._mappings = new HashMap<ClassKey, Class<?>>();
    }
    
    public <T> SimpleAbstractTypeResolver addMapping(final Class<T> superType, final Class<? extends T> subType) {
        if (superType == subType) {
            throw new IllegalArgumentException("Can not add mapping from class to itself");
        }
        if (!superType.isAssignableFrom(subType)) {
            throw new IllegalArgumentException("Can not add mapping from class " + superType.getName() + " to " + subType.getName() + ", as latter is not a subtype of former");
        }
        if (!Modifier.isAbstract(superType.getModifiers())) {
            throw new IllegalArgumentException("Can not add mapping from class " + superType.getName() + " since it is not abstract");
        }
        this._mappings.put(new ClassKey(superType), subType);
        return this;
    }
    
    @Override
    public JavaType findTypeMapping(final DeserializationConfig config, final JavaType type) {
        final Class<?> src = type.getRawClass();
        final Class<?> dst = this._mappings.get(new ClassKey(src));
        if (dst == null) {
            return null;
        }
        return type.narrowBy(dst);
    }
    
    @Override
    public JavaType resolveAbstractType(final DeserializationConfig config, final JavaType type) {
        return null;
    }
}
