// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.util;

import java.util.Collection;
import java.util.HashMap;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.MapperConfig;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializationFeature;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializationConfig;
import java.util.Map;
import org.apache.htrace.shaded.fasterxml.jackson.core.SerializableString;
import java.util.EnumMap;

public final class EnumValues
{
    private final Class<Enum<?>> _enumClass;
    private final EnumMap<?, SerializableString> _values;
    
    private EnumValues(final Class<Enum<?>> enumClass, final Map<Enum<?>, SerializableString> v) {
        this._enumClass = enumClass;
        this._values = new EnumMap<Object, SerializableString>(v);
    }
    
    public static EnumValues construct(final SerializationConfig config, final Class<Enum<?>> enumClass) {
        if (config.isEnabled(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)) {
            return constructFromToString(config, enumClass);
        }
        return constructFromName(config, enumClass);
    }
    
    public static EnumValues constructFromName(final MapperConfig<?> config, final Class<Enum<?>> enumClass) {
        final Class<? extends Enum<?>> cls = ClassUtil.findEnumType(enumClass);
        final Enum<?>[] values = (Enum<?>[])cls.getEnumConstants();
        if (values != null) {
            final Map<Enum<?>, SerializableString> map = new HashMap<Enum<?>, SerializableString>();
            for (final Enum<?> en : values) {
                final String value = config.getAnnotationIntrospector().findEnumValue(en);
                map.put(en, config.compileString(value));
            }
            return new EnumValues(enumClass, map);
        }
        throw new IllegalArgumentException("Can not determine enum constants for Class " + enumClass.getName());
    }
    
    public static EnumValues constructFromToString(final MapperConfig<?> config, final Class<Enum<?>> enumClass) {
        final Class<? extends Enum<?>> cls = ClassUtil.findEnumType(enumClass);
        final Enum<?>[] values = (Enum<?>[])cls.getEnumConstants();
        if (values != null) {
            final Map<Enum<?>, SerializableString> map = new HashMap<Enum<?>, SerializableString>();
            for (final Enum<?> en : values) {
                map.put(en, config.compileString(en.toString()));
            }
            return new EnumValues(enumClass, map);
        }
        throw new IllegalArgumentException("Can not determine enum constants for Class " + enumClass.getName());
    }
    
    public SerializableString serializedValueFor(final Enum<?> key) {
        return this._values.get(key);
    }
    
    public Collection<SerializableString> values() {
        return this._values.values();
    }
    
    public EnumMap<?, SerializableString> internalMap() {
        return this._values;
    }
    
    public Class<Enum<?>> getEnumClass() {
        return this._enumClass;
    }
}
