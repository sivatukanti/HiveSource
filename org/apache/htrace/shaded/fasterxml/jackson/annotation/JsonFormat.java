// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.annotation;

import java.util.TimeZone;
import java.util.Locale;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonFormat {
    public static final String DEFAULT_LOCALE = "##default";
    public static final String DEFAULT_TIMEZONE = "##default";
    
    String pattern() default "";
    
    Shape shape() default Shape.ANY;
    
    String locale() default "##default";
    
    String timezone() default "##default";
    
    public enum Shape
    {
        ANY, 
        SCALAR, 
        ARRAY, 
        OBJECT, 
        NUMBER, 
        NUMBER_FLOAT, 
        NUMBER_INT, 
        STRING, 
        BOOLEAN;
        
        public boolean isNumeric() {
            return this == Shape.NUMBER || this == Shape.NUMBER_INT || this == Shape.NUMBER_FLOAT;
        }
        
        public boolean isStructured() {
            return this == Shape.OBJECT || this == Shape.ARRAY;
        }
    }
    
    public static class Value
    {
        private final String pattern;
        private final Shape shape;
        private final Locale locale;
        private final String timezoneStr;
        private TimeZone _timezone;
        
        public Value() {
            this("", Shape.ANY, "", "");
        }
        
        public Value(final JsonFormat ann) {
            this(ann.pattern(), ann.shape(), ann.locale(), ann.timezone());
        }
        
        public Value(final String p, final Shape sh, final String localeStr, final String tzStr) {
            this(p, sh, (localeStr == null || localeStr.length() == 0 || "##default".equals(localeStr)) ? null : new Locale(localeStr), (tzStr == null || tzStr.length() == 0 || "##default".equals(tzStr)) ? null : tzStr, null);
        }
        
        public Value(final String p, final Shape sh, final Locale l, final TimeZone tz) {
            this.pattern = p;
            this.shape = sh;
            this.locale = l;
            this._timezone = tz;
            this.timezoneStr = null;
        }
        
        public Value(final String p, final Shape sh, final Locale l, final String tzStr, final TimeZone tz) {
            this.pattern = p;
            this.shape = sh;
            this.locale = l;
            this._timezone = tz;
            this.timezoneStr = tzStr;
        }
        
        public Value withPattern(final String p) {
            return new Value(p, this.shape, this.locale, this.timezoneStr, this._timezone);
        }
        
        public Value withShape(final Shape s) {
            return new Value(this.pattern, s, this.locale, this.timezoneStr, this._timezone);
        }
        
        public Value withLocale(final Locale l) {
            return new Value(this.pattern, this.shape, l, this.timezoneStr, this._timezone);
        }
        
        public Value withTimeZone(final TimeZone tz) {
            return new Value(this.pattern, this.shape, this.locale, null, tz);
        }
        
        public String getPattern() {
            return this.pattern;
        }
        
        public Shape getShape() {
            return this.shape;
        }
        
        public Locale getLocale() {
            return this.locale;
        }
        
        public String timeZoneAsString() {
            if (this._timezone != null) {
                return this._timezone.getID();
            }
            return this.timezoneStr;
        }
        
        public TimeZone getTimeZone() {
            TimeZone tz = this._timezone;
            if (tz == null) {
                if (this.timezoneStr == null) {
                    return null;
                }
                tz = (this._timezone = TimeZone.getTimeZone(this.timezoneStr));
            }
            return tz;
        }
        
        public boolean hasShape() {
            return this.shape != Shape.ANY;
        }
        
        public boolean hasPattern() {
            return this.pattern != null && this.pattern.length() > 0;
        }
        
        public boolean hasLocale() {
            return this.locale != null;
        }
        
        public boolean hasTimeZone() {
            return this._timezone != null || (this.timezoneStr != null && !this.timezoneStr.isEmpty());
        }
    }
}
