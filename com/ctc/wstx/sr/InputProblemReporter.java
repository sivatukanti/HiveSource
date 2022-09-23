// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.sr;

import javax.xml.stream.Location;
import org.codehaus.stax2.validation.XMLValidationProblem;
import javax.xml.stream.XMLStreamException;

public interface InputProblemReporter
{
    void throwParseError(final String p0) throws XMLStreamException;
    
    void throwParseError(final String p0, final Object p1, final Object p2) throws XMLStreamException;
    
    void reportValidationProblem(final XMLValidationProblem p0) throws XMLStreamException;
    
    void reportValidationProblem(final String p0) throws XMLStreamException;
    
    void reportValidationProblem(final String p0, final Object p1, final Object p2) throws XMLStreamException;
    
    void reportProblem(final Location p0, final String p1, final String p2, final Object p3, final Object p4) throws XMLStreamException;
    
    Location getLocation();
}
