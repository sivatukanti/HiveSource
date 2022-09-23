// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.validation;

import javax.xml.stream.XMLStreamException;

public interface Validatable
{
    XMLValidator validateAgainst(final XMLValidationSchema p0) throws XMLStreamException;
    
    XMLValidator stopValidatingAgainst(final XMLValidationSchema p0) throws XMLStreamException;
    
    XMLValidator stopValidatingAgainst(final XMLValidator p0) throws XMLStreamException;
    
    ValidationProblemHandler setValidationProblemHandler(final ValidationProblemHandler p0);
}
