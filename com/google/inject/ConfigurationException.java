// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject;

import com.google.inject.internal.util.$Preconditions;
import java.util.Collection;
import com.google.inject.internal.Errors;
import com.google.inject.spi.Message;
import com.google.inject.internal.util.$ImmutableSet;

public final class ConfigurationException extends RuntimeException
{
    private final $ImmutableSet<Message> messages;
    private Object partialValue;
    private static final long serialVersionUID = 0L;
    
    public ConfigurationException(final Iterable<Message> messages) {
        this.partialValue = null;
        this.messages = $ImmutableSet.copyOf((Iterable<? extends Message>)messages);
        this.initCause(Errors.getOnlyCause(this.messages));
    }
    
    public ConfigurationException withPartialValue(final Object partialValue) {
        $Preconditions.checkState(this.partialValue == null, "Can't clobber existing partial value %s with %s", this.partialValue, partialValue);
        final ConfigurationException result = new ConfigurationException(this.messages);
        result.partialValue = partialValue;
        return result;
    }
    
    public Collection<Message> getErrorMessages() {
        return this.messages;
    }
    
    public <E> E getPartialValue() {
        return (E)this.partialValue;
    }
    
    @Override
    public String getMessage() {
        return Errors.format("Guice configuration errors", this.messages);
    }
}
