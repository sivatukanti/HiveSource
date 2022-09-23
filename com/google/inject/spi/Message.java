// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.spi;

import java.io.ObjectStreamException;
import com.google.inject.Binder;
import com.google.inject.internal.util.$Objects;
import com.google.inject.internal.Errors;
import com.google.inject.internal.util.$SourceProvider;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.internal.util.$ImmutableList;
import java.util.List;
import java.io.Serializable;

public final class Message implements Serializable, Element
{
    private final String message;
    private final Throwable cause;
    private final List<Object> sources;
    private static final long serialVersionUID = 0L;
    
    public Message(final List<Object> sources, final String message, final Throwable cause) {
        this.sources = $ImmutableList.copyOf((Iterable<?>)sources);
        this.message = $Preconditions.checkNotNull(message, (Object)"message");
        this.cause = cause;
    }
    
    public Message(final Object source, final String message) {
        this($ImmutableList.of(source), message, null);
    }
    
    public Message(final String message) {
        this($ImmutableList.of(), message, null);
    }
    
    public String getSource() {
        return this.sources.isEmpty() ? $SourceProvider.UNKNOWN_SOURCE.toString() : Errors.convert(this.sources.get(this.sources.size() - 1)).toString();
    }
    
    public List<Object> getSources() {
        return this.sources;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public <T> T acceptVisitor(final ElementVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    public Throwable getCause() {
        return this.cause;
    }
    
    @Override
    public String toString() {
        return this.message;
    }
    
    @Override
    public int hashCode() {
        return this.message.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Message)) {
            return false;
        }
        final Message e = (Message)o;
        return this.message.equals(e.message) && $Objects.equal(this.cause, e.cause) && this.sources.equals(e.sources);
    }
    
    public void applyTo(final Binder binder) {
        binder.withSource(this.getSource()).addError(this);
    }
    
    private Object writeReplace() throws ObjectStreamException {
        final Object[] sourcesAsStrings = this.sources.toArray();
        for (int i = 0; i < sourcesAsStrings.length; ++i) {
            sourcesAsStrings[i] = Errors.convert(sourcesAsStrings[i]).toString();
        }
        return new Message($ImmutableList.of(sourcesAsStrings), this.message, this.cause);
    }
}
