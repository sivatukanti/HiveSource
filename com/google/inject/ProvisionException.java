// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject;

import java.util.List;
import com.google.inject.internal.util.$ImmutableList;
import java.util.Collection;
import com.google.inject.internal.Errors;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.spi.Message;
import com.google.inject.internal.util.$ImmutableSet;

public final class ProvisionException extends RuntimeException
{
    private final $ImmutableSet<Message> messages;
    private static final long serialVersionUID = 0L;
    
    public ProvisionException(final Iterable<Message> messages) {
        this.messages = $ImmutableSet.copyOf((Iterable<? extends Message>)messages);
        $Preconditions.checkArgument(!this.messages.isEmpty());
        this.initCause(Errors.getOnlyCause(this.messages));
    }
    
    public ProvisionException(final String message, final Throwable cause) {
        super(cause);
        this.messages = $ImmutableSet.of(new Message($ImmutableList.of(), message, cause));
    }
    
    public ProvisionException(final String message) {
        this.messages = $ImmutableSet.of(new Message(message));
    }
    
    public Collection<Message> getErrorMessages() {
        return this.messages;
    }
    
    @Override
    public String getMessage() {
        return Errors.format("Guice provision errors", this.messages);
    }
}
