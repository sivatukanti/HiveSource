// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.evt;

import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.XMLStreamWriter2;
import javax.xml.stream.events.XMLEvent;

public interface XMLEvent2 extends XMLEvent
{
    void writeUsing(final XMLStreamWriter2 p0) throws XMLStreamException;
}
