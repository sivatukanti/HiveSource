// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.util.List;

public final class $SourceProvider
{
    public static final Object UNKNOWN_SOURCE;
    private final $ImmutableSet<String> classNamesToSkip;
    public static final $SourceProvider DEFAULT_INSTANCE;
    
    private $SourceProvider(final Iterable<String> classesToSkip) {
        this.classNamesToSkip = $ImmutableSet.copyOf((Iterable<? extends String>)classesToSkip);
    }
    
    public $SourceProvider plusSkippedClasses(final Class... moreClassesToSkip) {
        return new $SourceProvider($Iterables.concat((Iterable<? extends String>)this.classNamesToSkip, (Iterable<? extends String>)asStrings(moreClassesToSkip)));
    }
    
    private static List<String> asStrings(final Class... classes) {
        final List<String> strings = (List<String>)$Lists.newArrayList();
        for (final Class c : classes) {
            strings.add(c.getName());
        }
        return strings;
    }
    
    public StackTraceElement get() {
        for (final StackTraceElement element : new Throwable().getStackTrace()) {
            final String className = element.getClassName();
            if (!this.classNamesToSkip.contains(className)) {
                return element;
            }
        }
        throw new AssertionError();
    }
    
    static {
        UNKNOWN_SOURCE = "[unknown source]";
        DEFAULT_INSTANCE = new $SourceProvider($ImmutableSet.of($SourceProvider.class.getName()));
    }
}
