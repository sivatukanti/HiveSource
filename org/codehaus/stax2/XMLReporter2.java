// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2;

import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationProblem;
import javax.xml.stream.XMLReporter;

public interface XMLReporter2 extends XMLReporter
{
    void report(final XMLValidationProblem p0) throws XMLStreamException;
}
