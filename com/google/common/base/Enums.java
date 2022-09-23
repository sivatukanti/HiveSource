// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.base;

import javax.annotation.Nullable;
import java.io.Serializable;
import com.google.common.annotations.GwtIncompatible;
import java.lang.reflect.Field;
import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
@Beta
public final class Enums
{
    private Enums() {
    }
    
    @GwtIncompatible("reflection")
    public static Field getField(final Enum<?> enumValue) {
        final Class<?> clazz = enumValue.getDeclaringClass();
        try {
            return clazz.getDeclaredField(enumValue.name());
        }
        catch (NoSuchFieldException impossible) {
            throw new AssertionError((Object)impossible);
        }
    }
    
    @Deprecated
    public static <T extends Enum<T>> Function<String, T> valueOfFunction(final Class<T> enumClass) {
        return new ValueOfFunction<T>((Class)enumClass);
    }
    
    public static <T extends Enum<T>> Optional<T> getIfPresent(final Class<T> enumClass, final String value) {
        Preconditions.checkNotNull(enumClass);
        Preconditions.checkNotNull(value);
        try {
            return Optional.of((T)Enum.valueOf((Class<T>)enumClass, value));
        }
        catch (IllegalArgumentException iae) {
            return Optional.absent();
        }
    }
    
    public static <T extends Enum<T>> Converter<String, T> stringConverter(final Class<T> enumClass) {
        return (Converter<String, T>)new StringConverter((Class<Enum>)enumClass);
    }
    
    private static final class ValueOfFunction<T extends Enum<T>> implements Function<String, T>, Serializable
    {
        private final Class<T> enumClass;
        private static final long serialVersionUID = 0L;
        
        private ValueOfFunction(final Class<T> enumClass) {
            this.enumClass = Preconditions.checkNotNull(enumClass);
        }
        
        @Override
        public T apply(final String value) {
            try {
                return Enum.valueOf(this.enumClass, value);
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }
        
        @Override
        public boolean equals(@Nullable final Object obj) {
            return obj instanceof ValueOfFunction && this.enumClass.equals(((ValueOfFunction)obj).enumClass);
        }
        
        @Override
        public int hashCode() {
            return this.enumClass.hashCode();
        }
        
        @Override
        public String toString() {
            return "Enums.valueOf(" + this.enumClass + ")";
        }
    }
    
    private static final class StringConverter<T extends Enum<T>> extends Converter<String, T> implements Serializable
    {
        private final Class<T> enumClass;
        private static final long serialVersionUID = 0L;
        
        StringConverter(final Class<T> enumClass) {
            this.enumClass = Preconditions.checkNotNull(enumClass);
        }
        
        @Override
        protected T doForward(final String value) {
            return Enum.valueOf(this.enumClass, value);
        }
        
        @Override
        protected String doBackward(final T enumValue) {
            return enumValue.name();
        }
        
        @Override
        public boolean equals(@Nullable final Object object) {
            if (object instanceof StringConverter) {
                final StringConverter<?> that = (StringConverter<?>)object;
                return this.enumClass.equals(that.enumClass);
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return this.enumClass.hashCode();
        }
        
        @Override
        public String toString() {
            return "Enums.stringConverter(" + this.enumClass.getName() + ".class)";
        }
    }
}
