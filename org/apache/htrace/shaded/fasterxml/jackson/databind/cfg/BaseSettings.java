// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.cfg;

import org.apache.htrace.shaded.fasterxml.jackson.databind.util.StdDateFormat;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.JsonAutoDetect;
import org.apache.htrace.shaded.fasterxml.jackson.annotation.PropertyAccessor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import org.apache.htrace.shaded.fasterxml.jackson.core.Base64Variant;
import java.util.TimeZone;
import java.util.Locale;
import java.text.DateFormat;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.VisibilityChecker;
import org.apache.htrace.shaded.fasterxml.jackson.databind.AnnotationIntrospector;
import org.apache.htrace.shaded.fasterxml.jackson.databind.introspect.ClassIntrospector;
import java.io.Serializable;

public final class BaseSettings implements Serializable
{
    private static final long serialVersionUID = 4939673998947122190L;
    protected final ClassIntrospector _classIntrospector;
    protected final AnnotationIntrospector _annotationIntrospector;
    protected final VisibilityChecker<?> _visibilityChecker;
    protected final PropertyNamingStrategy _propertyNamingStrategy;
    protected final TypeFactory _typeFactory;
    protected final TypeResolverBuilder<?> _typeResolverBuilder;
    protected final DateFormat _dateFormat;
    protected final HandlerInstantiator _handlerInstantiator;
    protected final Locale _locale;
    protected final TimeZone _timeZone;
    protected final Base64Variant _defaultBase64;
    
    public BaseSettings(final ClassIntrospector ci, final AnnotationIntrospector ai, final VisibilityChecker<?> vc, final PropertyNamingStrategy pns, final TypeFactory tf, final TypeResolverBuilder<?> typer, final DateFormat dateFormat, final HandlerInstantiator hi, final Locale locale, final TimeZone tz, final Base64Variant defaultBase64) {
        this._classIntrospector = ci;
        this._annotationIntrospector = ai;
        this._visibilityChecker = vc;
        this._propertyNamingStrategy = pns;
        this._typeFactory = tf;
        this._typeResolverBuilder = typer;
        this._dateFormat = dateFormat;
        this._handlerInstantiator = hi;
        this._locale = locale;
        this._timeZone = tz;
        this._defaultBase64 = defaultBase64;
    }
    
    public BaseSettings withClassIntrospector(final ClassIntrospector ci) {
        if (this._classIntrospector == ci) {
            return this;
        }
        return new BaseSettings(ci, this._annotationIntrospector, this._visibilityChecker, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64);
    }
    
    public BaseSettings withAnnotationIntrospector(final AnnotationIntrospector ai) {
        if (this._annotationIntrospector == ai) {
            return this;
        }
        return new BaseSettings(this._classIntrospector, ai, this._visibilityChecker, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64);
    }
    
    public BaseSettings withInsertedAnnotationIntrospector(final AnnotationIntrospector ai) {
        return this.withAnnotationIntrospector(AnnotationIntrospectorPair.create(ai, this._annotationIntrospector));
    }
    
    public BaseSettings withAppendedAnnotationIntrospector(final AnnotationIntrospector ai) {
        return this.withAnnotationIntrospector(AnnotationIntrospectorPair.create(this._annotationIntrospector, ai));
    }
    
    public BaseSettings withVisibilityChecker(final VisibilityChecker<?> vc) {
        if (this._visibilityChecker == vc) {
            return this;
        }
        return new BaseSettings(this._classIntrospector, this._annotationIntrospector, vc, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64);
    }
    
    public BaseSettings withVisibility(final PropertyAccessor forMethod, final JsonAutoDetect.Visibility visibility) {
        return new BaseSettings(this._classIntrospector, this._annotationIntrospector, (VisibilityChecker<?>)this._visibilityChecker.withVisibility(forMethod, visibility), this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64);
    }
    
    public BaseSettings withPropertyNamingStrategy(final PropertyNamingStrategy pns) {
        if (this._propertyNamingStrategy == pns) {
            return this;
        }
        return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._visibilityChecker, pns, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64);
    }
    
    public BaseSettings withTypeFactory(final TypeFactory tf) {
        if (this._typeFactory == tf) {
            return this;
        }
        return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._visibilityChecker, this._propertyNamingStrategy, tf, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64);
    }
    
    public BaseSettings withTypeResolverBuilder(final TypeResolverBuilder<?> typer) {
        if (this._typeResolverBuilder == typer) {
            return this;
        }
        return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._visibilityChecker, this._propertyNamingStrategy, this._typeFactory, typer, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64);
    }
    
    public BaseSettings withDateFormat(final DateFormat df) {
        if (this._dateFormat == df) {
            return this;
        }
        return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._visibilityChecker, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, df, this._handlerInstantiator, this._locale, this._timeZone, this._defaultBase64);
    }
    
    public BaseSettings withHandlerInstantiator(final HandlerInstantiator hi) {
        if (this._handlerInstantiator == hi) {
            return this;
        }
        return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._visibilityChecker, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, hi, this._locale, this._timeZone, this._defaultBase64);
    }
    
    public BaseSettings with(final Locale l) {
        if (this._locale == l) {
            return this;
        }
        return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._visibilityChecker, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, l, this._timeZone, this._defaultBase64);
    }
    
    public BaseSettings with(final TimeZone tz) {
        if (tz == null) {
            throw new IllegalArgumentException();
        }
        DateFormat df = this._dateFormat;
        if (df instanceof StdDateFormat) {
            df = ((StdDateFormat)df).withTimeZone(tz);
        }
        else {
            df = (DateFormat)df.clone();
            df.setTimeZone(tz);
        }
        return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._visibilityChecker, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, df, this._handlerInstantiator, this._locale, tz, this._defaultBase64);
    }
    
    public BaseSettings with(final Base64Variant base64) {
        if (base64 == this._defaultBase64) {
            return this;
        }
        return new BaseSettings(this._classIntrospector, this._annotationIntrospector, this._visibilityChecker, this._propertyNamingStrategy, this._typeFactory, this._typeResolverBuilder, this._dateFormat, this._handlerInstantiator, this._locale, this._timeZone, base64);
    }
    
    public ClassIntrospector getClassIntrospector() {
        return this._classIntrospector;
    }
    
    public AnnotationIntrospector getAnnotationIntrospector() {
        return this._annotationIntrospector;
    }
    
    public VisibilityChecker<?> getVisibilityChecker() {
        return this._visibilityChecker;
    }
    
    public PropertyNamingStrategy getPropertyNamingStrategy() {
        return this._propertyNamingStrategy;
    }
    
    public TypeFactory getTypeFactory() {
        return this._typeFactory;
    }
    
    public TypeResolverBuilder<?> getTypeResolverBuilder() {
        return this._typeResolverBuilder;
    }
    
    public DateFormat getDateFormat() {
        return this._dateFormat;
    }
    
    public HandlerInstantiator getHandlerInstantiator() {
        return this._handlerInstantiator;
    }
    
    public Locale getLocale() {
        return this._locale;
    }
    
    public TimeZone getTimeZone() {
        return this._timeZone;
    }
    
    public Base64Variant getBase64Variant() {
        return this._defaultBase64;
    }
}
