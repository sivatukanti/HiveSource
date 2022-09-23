// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.util.Iterator;
import java.util.Collections;
import java.util.List;

public class UninitializedMessageException extends RuntimeException
{
    private static final long serialVersionUID = -7466929953374883507L;
    private final List<String> missingFields;
    
    public UninitializedMessageException(final MessageLite message) {
        super("Message was missing required fields.  (Lite runtime could not determine which fields were missing).");
        this.missingFields = null;
    }
    
    public UninitializedMessageException(final List<String> missingFields) {
        super(buildDescription(missingFields));
        this.missingFields = missingFields;
    }
    
    public List<String> getMissingFields() {
        return Collections.unmodifiableList((List<? extends String>)this.missingFields);
    }
    
    public InvalidProtocolBufferException asInvalidProtocolBufferException() {
        return new InvalidProtocolBufferException(this.getMessage());
    }
    
    private static String buildDescription(final List<String> missingFields) {
        final StringBuilder description = new StringBuilder("Message missing required fields: ");
        boolean first = true;
        for (final String field : missingFields) {
            if (first) {
                first = false;
            }
            else {
                description.append(", ");
            }
            description.append(field);
        }
        return description.toString();
    }
}
