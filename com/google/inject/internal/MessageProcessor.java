// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.Guice;
import java.util.logging.Level;
import com.google.inject.spi.Message;
import java.util.logging.Logger;

final class MessageProcessor extends AbstractProcessor
{
    private static final Logger logger;
    
    MessageProcessor(final Errors errors) {
        super(errors);
    }
    
    @Override
    public Boolean visit(final Message message) {
        if (message.getCause() != null) {
            final String rootMessage = getRootMessage(message.getCause());
            MessageProcessor.logger.log(Level.INFO, "An exception was caught and reported. Message: " + rootMessage, message.getCause());
        }
        this.errors.addMessage(message);
        return true;
    }
    
    public static String getRootMessage(final Throwable t) {
        final Throwable cause = t.getCause();
        return (cause == null) ? t.toString() : getRootMessage(cause);
    }
    
    static {
        logger = Logger.getLogger(Guice.class.getName());
    }
}
