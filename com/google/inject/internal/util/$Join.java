// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.io.IOException;
import java.util.Map;
import java.util.Iterator;
import java.util.Arrays;

public final class $Join
{
    private $Join() {
    }
    
    public static String join(final String delimiter, final Iterable<?> tokens) {
        return join(delimiter, tokens.iterator());
    }
    
    public static String join(final String delimiter, final Object[] tokens) {
        return join(delimiter, Arrays.asList(tokens));
    }
    
    public static String join(final String delimiter, @$Nullable final Object firstToken, final Object... otherTokens) {
        $Preconditions.checkNotNull(otherTokens);
        return join(delimiter, $Lists.newArrayList(firstToken, otherTokens));
    }
    
    public static String join(final String delimiter, final Iterator<?> tokens) {
        final StringBuilder sb = new StringBuilder();
        join(sb, delimiter, tokens);
        return sb.toString();
    }
    
    public static String join(final String keyValueSeparator, final String entryDelimiter, final Map<?, ?> map) {
        return join(new StringBuilder(), keyValueSeparator, entryDelimiter, map).toString();
    }
    
    public static <T extends Appendable> T join(final T appendable, final String delimiter, final Iterable<?> tokens) {
        return join(appendable, delimiter, tokens.iterator());
    }
    
    public static <T extends Appendable> T join(final T appendable, final String delimiter, final Object[] tokens) {
        return join(appendable, delimiter, Arrays.asList(tokens));
    }
    
    public static <T extends Appendable> T join(final T appendable, final String delimiter, @$Nullable final Object firstToken, final Object... otherTokens) {
        $Preconditions.checkNotNull(otherTokens);
        return join(appendable, delimiter, $Lists.newArrayList(firstToken, otherTokens));
    }
    
    public static <T extends Appendable> T join(final T appendable, final String delimiter, final Iterator<?> tokens) {
        $Preconditions.checkNotNull(appendable);
        $Preconditions.checkNotNull(delimiter);
        if (tokens.hasNext()) {
            try {
                appendOneToken(appendable, tokens.next());
                while (tokens.hasNext()) {
                    appendable.append(delimiter);
                    appendOneToken(appendable, tokens.next());
                }
            }
            catch (IOException e) {
                throw new JoinException(e);
            }
        }
        return appendable;
    }
    
    public static <T extends Appendable> T join(final T appendable, final String keyValueSeparator, final String entryDelimiter, final Map<?, ?> map) {
        $Preconditions.checkNotNull(appendable);
        $Preconditions.checkNotNull(keyValueSeparator);
        $Preconditions.checkNotNull(entryDelimiter);
        final Iterator<? extends Map.Entry<?, ?>> entries = map.entrySet().iterator();
        if (entries.hasNext()) {
            try {
                appendOneEntry(appendable, keyValueSeparator, (Map.Entry<?, ?>)entries.next());
                while (entries.hasNext()) {
                    appendable.append(entryDelimiter);
                    appendOneEntry(appendable, keyValueSeparator, (Map.Entry<?, ?>)entries.next());
                }
            }
            catch (IOException e) {
                throw new JoinException(e);
            }
        }
        return appendable;
    }
    
    private static void appendOneEntry(final Appendable appendable, final String keyValueSeparator, final Map.Entry<?, ?> entry) throws IOException {
        appendOneToken(appendable, entry.getKey());
        appendable.append(keyValueSeparator);
        appendOneToken(appendable, entry.getValue());
    }
    
    private static void appendOneToken(final Appendable appendable, final Object token) throws IOException {
        appendable.append(toCharSequence(token));
    }
    
    private static CharSequence toCharSequence(final Object token) {
        return (token instanceof CharSequence) ? ((CharSequence)token) : String.valueOf(token);
    }
    
    public static class JoinException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
        
        private JoinException(final IOException cause) {
            super(cause);
        }
    }
}
