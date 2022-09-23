// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLEventReader;

public interface XMLEventReader2 extends XMLEventReader
{
    boolean hasNextEvent() throws XMLStreamException;
    
    boolean isPropertySupported(final String p0);
    
    boolean setProperty(final String p0, final Object p1);
}
