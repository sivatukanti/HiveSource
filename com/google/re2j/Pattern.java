// 
// Decompiled by Procyon v0.5.36
// 

package com.google.re2j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public final class Pattern implements Serializable
{
    public static final int CASE_INSENSITIVE = 1;
    public static final int DOTALL = 2;
    public static final int MULTILINE = 4;
    public static final int DISABLE_UNICODE_GROUPS = 8;
    private final String pattern;
    private final int flags;
    private transient RE2 re2;
    private static final long serialVersionUID = 0L;
    
    Pattern(final String pattern, final int flags, final RE2 re2) {
        if (pattern == null) {
            throw new NullPointerException("pattern is null");
        }
        if (re2 == null) {
            throw new NullPointerException("re2 is null");
        }
        this.pattern = pattern;
        this.flags = flags;
        this.re2 = re2;
    }
    
    public void reset() {
        this.re2.reset();
    }
    
    public int flags() {
        return this.flags;
    }
    
    public String pattern() {
        return this.pattern;
    }
    
    RE2 re2() {
        return this.re2;
    }
    
    public static Pattern compile(final String regex) {
        return compile(regex, regex, 0);
    }
    
    public static Pattern compile(final String regex, final int flags) {
        String flregex = regex;
        if ((flags & 0x1) != 0x0) {
            flregex = "(?i)" + flregex;
        }
        if ((flags & 0x2) != 0x0) {
            flregex = "(?s)" + flregex;
        }
        if ((flags & 0x4) != 0x0) {
            flregex = "(?m)" + flregex;
        }
        if ((flags & 0xFFFFFFF0) != 0x0) {
            throw new IllegalArgumentException("Flags should only be a combination of MULTILINE, DOTALL, CASE_INSENSITIVE, DISABLE_UNICODE_GROUPS");
        }
        return compile(flregex, regex, flags);
    }
    
    private static Pattern compile(final String flregex, final String regex, final int flags) {
        int re2Flags = 212;
        if ((flags & 0x8) != 0x0) {
            re2Flags &= 0xFFFFFF7F;
        }
        return new Pattern(regex, flags, RE2.compileImpl(flregex, re2Flags, false));
    }
    
    public static boolean matches(final String regex, final CharSequence input) {
        return compile(regex).matcher(input).matches();
    }
    
    public boolean matches(final String input) {
        return this.matcher(input).matches();
    }
    
    public Matcher matcher(final CharSequence input) {
        return new Matcher(this, input);
    }
    
    public String[] split(final String input) {
        return this.split(input, 0);
    }
    
    public String[] split(final String input, final int limit) {
        return this.split(new Matcher(this, input), limit);
    }
    
    private String[] split(final Matcher m, final int limit) {
        int matchCount = 0;
        int arraySize = 0;
        int last = 0;
        while (m.find()) {
            ++matchCount;
            if (limit != 0 || last < m.start()) {
                arraySize = matchCount;
            }
            last = m.end();
        }
        if (last < m.inputLength() || limit != 0) {
            arraySize = ++matchCount;
        }
        int trunc = 0;
        if (limit > 0 && arraySize > limit) {
            arraySize = limit;
            trunc = 1;
        }
        final String[] array = new String[arraySize];
        int i = 0;
        last = 0;
        m.reset();
        while (m.find() && i < arraySize - trunc) {
            array[i++] = m.substring(last, m.start());
            last = m.end();
        }
        if (i < arraySize) {
            array[i] = m.substring(last, m.inputLength());
        }
        return array;
    }
    
    public static String quote(final String s) {
        return RE2.quoteMeta(s);
    }
    
    @Override
    public String toString() {
        return this.pattern;
    }
    
    public int groupCount() {
        return this.re2.numberOfCapturingGroups();
    }
    
    Object readReplace() {
        return compile(this.pattern, this.flags);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.re2 = RE2.compileImpl(this.pattern, this.flags, false);
    }
}
