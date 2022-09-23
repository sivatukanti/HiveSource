// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.validation;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

public class XMLValidationException extends XMLStreamException
{
    private static final long serialVersionUID = 1L;
    protected XMLValidationProblem mCause;
    
    protected XMLValidationException(final XMLValidationProblem mCause) {
        if (mCause == null) {
            throwMissing();
        }
        this.mCause = mCause;
    }
    
    protected XMLValidationException(final XMLValidationProblem mCause, final String msg) {
        super(msg);
        if (mCause == null) {
            throwMissing();
        }
        this.mCause = mCause;
    }
    
    protected XMLValidationException(final XMLValidationProblem mCause, final String msg, final Location location) {
        super(msg, location);
        if (mCause == null) {
            throwMissing();
        }
        this.mCause = mCause;
    }
    
    public static XMLValidationException createException(final XMLValidationProblem xmlValidationProblem) {
        final String message = xmlValidationProblem.getMessage();
        if (message == null) {
            return new XMLValidationException(xmlValidationProblem);
        }
        final Location location = xmlValidationProblem.getLocation();
        if (location == null) {
            return new XMLValidationException(xmlValidationProblem, message);
        }
        return new XMLValidationException(xmlValidationProblem, message, location);
    }
    
    public XMLValidationProblem getValidationProblem() {
        return this.mCause;
    }
    
    protected static void throwMissing() throws RuntimeException {
        throw new IllegalArgumentException("Validation problem argument can not be null");
    }
}
