// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.conf;

import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public interface Validator
{
    String validate(final String p0);
    
    String toDescription();
    
    public static class StringSet implements Validator
    {
        private final boolean caseSensitive;
        private final Set<String> expected;
        
        public StringSet(final String... values) {
            this(false, values);
        }
        
        public StringSet(final boolean caseSensitive, final String... values) {
            this.expected = new LinkedHashSet<String>();
            this.caseSensitive = caseSensitive;
            for (final String value : values) {
                this.expected.add(caseSensitive ? value : value.toLowerCase());
            }
        }
        
        public Set<String> getExpected() {
            return new HashSet<String>(this.expected);
        }
        
        @Override
        public String validate(final String value) {
            if (value == null || !this.expected.contains(this.caseSensitive ? value : value.toLowerCase())) {
                return "Invalid value.. expects one of " + this.expected;
            }
            return null;
        }
        
        @Override
        public String toDescription() {
            return "Expects one of " + this.expected;
        }
    }
    
    public enum TYPE
    {
        INT {
            @Override
            protected boolean inRange(final String value, final Object lower, final Object upper) {
                final int ivalue = Integer.parseInt(value);
                return (lower == null || ivalue >= (int)lower) && (upper == null || ivalue <= (int)upper);
            }
        }, 
        LONG {
            @Override
            protected boolean inRange(final String value, final Object lower, final Object upper) {
                final long lvalue = Long.parseLong(value);
                return (lower == null || lvalue >= (long)lower) && (upper == null || lvalue <= (long)upper);
            }
        }, 
        FLOAT {
            @Override
            protected boolean inRange(final String value, final Object lower, final Object upper) {
                final float fvalue = Float.parseFloat(value);
                return (lower == null || fvalue >= (float)lower) && (upper == null || fvalue <= (float)upper);
            }
        };
        
        public static TYPE valueOf(final Object lower, final Object upper) {
            if (lower instanceof Integer || upper instanceof Integer) {
                return TYPE.INT;
            }
            if (lower instanceof Long || upper instanceof Long) {
                return TYPE.LONG;
            }
            if (lower instanceof Float || upper instanceof Float) {
                return TYPE.FLOAT;
            }
            throw new IllegalArgumentException("invalid range from " + lower + " to " + upper);
        }
        
        protected abstract boolean inRange(final String p0, final Object p1, final Object p2);
    }
    
    public static class RangeValidator implements Validator
    {
        private final TYPE type;
        private final Object lower;
        private final Object upper;
        
        public RangeValidator(final Object lower, final Object upper) {
            this.lower = lower;
            this.upper = upper;
            this.type = TYPE.valueOf(lower, upper);
        }
        
        @Override
        public String validate(final String value) {
            try {
                if (value == null) {
                    return "Value cannot be null";
                }
                if (!this.type.inRange(value.trim(), this.lower, this.upper)) {
                    return "Invalid value  " + value + ", which should be in between " + this.lower + " and " + this.upper;
                }
            }
            catch (Exception e) {
                return e.toString();
            }
            return null;
        }
        
        @Override
        public String toDescription() {
            if (this.lower == null && this.upper == null) {
                return null;
            }
            if (this.lower != null && this.upper != null) {
                return "Expects value between " + this.lower + " and " + this.upper;
            }
            if (this.lower != null) {
                return "Expects value bigger than " + this.lower;
            }
            return "Expects value smaller than " + this.upper;
        }
    }
    
    public static class PatternSet implements Validator
    {
        private final List<Pattern> expected;
        
        public PatternSet(final String... values) {
            this.expected = new ArrayList<Pattern>();
            for (final String value : values) {
                this.expected.add(Pattern.compile(value));
            }
        }
        
        @Override
        public String validate(final String value) {
            if (value == null) {
                return "Invalid value.. expects one of patterns " + this.expected;
            }
            for (final Pattern pattern : this.expected) {
                if (pattern.matcher(value).matches()) {
                    return null;
                }
            }
            return "Invalid value.. expects one of patterns " + this.expected;
        }
        
        @Override
        public String toDescription() {
            return "Expects one of the pattern in " + this.expected;
        }
    }
    
    public static class RatioValidator implements Validator
    {
        @Override
        public String validate(final String value) {
            try {
                final float fvalue = Float.valueOf(value);
                if (fvalue < 0.0f || fvalue > 1.0f) {
                    return "Invalid ratio " + value + ", which should be in between 0 to 1";
                }
            }
            catch (NumberFormatException e) {
                return e.toString();
            }
            return null;
        }
        
        @Override
        public String toDescription() {
            return "Expects value between 0.0f and 1.0f";
        }
    }
    
    public static class TimeValidator implements Validator
    {
        private final TimeUnit timeUnit;
        private final Long min;
        private final boolean minInclusive;
        private final Long max;
        private final boolean maxInclusive;
        
        public TimeValidator(final TimeUnit timeUnit) {
            this(timeUnit, null, false, null, false);
        }
        
        public TimeValidator(final TimeUnit timeUnit, final Long min, final boolean minInclusive, final Long max, final boolean maxInclusive) {
            this.timeUnit = timeUnit;
            this.min = min;
            this.minInclusive = minInclusive;
            this.max = max;
            this.maxInclusive = maxInclusive;
        }
        
        public TimeUnit getTimeUnit() {
            return this.timeUnit;
        }
        
        @Override
        public String validate(final String value) {
            try {
                final long time = HiveConf.toTime(value, this.timeUnit, this.timeUnit);
                Label_0088: {
                    if (this.min != null) {
                        if (this.minInclusive) {
                            if (time >= this.min) {
                                break Label_0088;
                            }
                        }
                        else if (time > this.min) {
                            break Label_0088;
                        }
                        return value + " is smaller than " + this.timeString(this.min);
                    }
                }
                if (this.max != null) {
                    if (this.maxInclusive) {
                        if (time <= this.max) {
                            return null;
                        }
                    }
                    else if (time < this.max) {
                        return null;
                    }
                    return value + " is bigger than " + this.timeString(this.max);
                }
            }
            catch (Exception e) {
                return e.toString();
            }
            return null;
        }
        
        @Override
        public String toDescription() {
            String description = "Expects a time value with unit (d/day, h/hour, m/min, s/sec, ms/msec, us/usec, ns/nsec), which is " + HiveConf.stringFor(this.timeUnit) + " if not specified";
            if (this.min != null && this.max != null) {
                description = description + ".\nThe time should be in between " + this.timeString(this.min) + (this.minInclusive ? " (inclusive)" : " (exclusive)") + " and " + this.timeString(this.max) + (this.maxInclusive ? " (inclusive)" : " (exclusive)");
            }
            else if (this.min != null) {
                description = description + ".\nThe time should be bigger than " + (this.minInclusive ? "or equal to " : "") + this.timeString(this.min);
            }
            else if (this.max != null) {
                description = description + ".\nThe time should be smaller than " + (this.maxInclusive ? "or equal to " : "") + this.timeString(this.max);
            }
            return description;
        }
        
        private String timeString(final long time) {
            return time + " " + HiveConf.stringFor(this.timeUnit);
        }
    }
}
