// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.spi.TypeConverterBinding;
import com.google.inject.internal.util.$SourceProvider;
import java.lang.reflect.Type;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.google.inject.internal.util.$Strings;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverter;

final class TypeConverterBindingProcessor extends AbstractProcessor
{
    TypeConverterBindingProcessor(final Errors errors) {
        super(errors);
    }
    
    void prepareBuiltInConverters(final InjectorImpl injector) {
        this.injector = injector;
        try {
            this.convertToPrimitiveType(Integer.TYPE, Integer.class);
            this.convertToPrimitiveType(Long.TYPE, Long.class);
            this.convertToPrimitiveType(Boolean.TYPE, Boolean.class);
            this.convertToPrimitiveType(Byte.TYPE, Byte.class);
            this.convertToPrimitiveType(Short.TYPE, Short.class);
            this.convertToPrimitiveType(Float.TYPE, Float.class);
            this.convertToPrimitiveType(Double.TYPE, Double.class);
            this.convertToClass(Character.class, new TypeConverter() {
                public Object convert(String value, final TypeLiteral<?> toType) {
                    value = value.trim();
                    if (value.length() != 1) {
                        throw new RuntimeException("Length != 1.");
                    }
                    return value.charAt(0);
                }
                
                @Override
                public String toString() {
                    return "TypeConverter<Character>";
                }
            });
            this.convertToClasses(Matchers.subclassesOf(Enum.class), new TypeConverter() {
                public Object convert(final String value, final TypeLiteral<?> toType) {
                    return Enum.valueOf(toType.getRawType(), value);
                }
                
                @Override
                public String toString() {
                    return "TypeConverter<E extends Enum<E>>";
                }
            });
            this.internalConvertToTypes(new AbstractMatcher<TypeLiteral<?>>() {
                public boolean matches(final TypeLiteral<?> typeLiteral) {
                    return typeLiteral.getRawType() == Class.class;
                }
                
                @Override
                public String toString() {
                    return "Class<?>";
                }
            }, new TypeConverter() {
                public Object convert(final String value, final TypeLiteral<?> toType) {
                    try {
                        return Class.forName(value);
                    }
                    catch (ClassNotFoundException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
                
                @Override
                public String toString() {
                    return "TypeConverter<Class<?>>";
                }
            });
        }
        finally {
            this.injector = null;
        }
    }
    
    private <T> void convertToPrimitiveType(final Class<T> primitiveType, final Class<T> wrapperType) {
        try {
            final Method parser = wrapperType.getMethod("parse" + $Strings.capitalize(primitiveType.getName()), String.class);
            final TypeConverter typeConverter = new TypeConverter() {
                public Object convert(final String value, final TypeLiteral<?> toType) {
                    try {
                        return parser.invoke(null, value);
                    }
                    catch (IllegalAccessException e) {
                        throw new AssertionError((Object)e);
                    }
                    catch (InvocationTargetException e2) {
                        throw new RuntimeException(e2.getTargetException().getMessage());
                    }
                }
                
                @Override
                public String toString() {
                    return "TypeConverter<" + wrapperType.getSimpleName() + ">";
                }
            };
            this.convertToClass(wrapperType, typeConverter);
        }
        catch (NoSuchMethodException e) {
            throw new AssertionError((Object)e);
        }
    }
    
    private <T> void convertToClass(final Class<T> type, final TypeConverter converter) {
        this.convertToClasses(Matchers.identicalTo(type), converter);
    }
    
    private void convertToClasses(final Matcher<? super Class<?>> typeMatcher, final TypeConverter converter) {
        this.internalConvertToTypes(new AbstractMatcher<TypeLiteral<?>>() {
            public boolean matches(final TypeLiteral<?> typeLiteral) {
                final Type type = typeLiteral.getType();
                if (!(type instanceof Class)) {
                    return false;
                }
                final Class<?> clazz = (Class<?>)type;
                return typeMatcher.matches(clazz);
            }
            
            @Override
            public String toString() {
                return typeMatcher.toString();
            }
        }, converter);
    }
    
    private void internalConvertToTypes(final Matcher<? super TypeLiteral<?>> typeMatcher, final TypeConverter converter) {
        this.injector.state.addConverter(new TypeConverterBinding($SourceProvider.UNKNOWN_SOURCE, typeMatcher, converter));
    }
    
    @Override
    public Boolean visit(final TypeConverterBinding command) {
        this.injector.state.addConverter(new TypeConverterBinding(command.getSource(), command.getTypeMatcher(), command.getTypeConverter()));
        return true;
    }
}
