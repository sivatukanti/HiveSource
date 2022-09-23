// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

public interface LocationInfo
{
    long getStartingByteOffset();
    
    long getStartingCharOffset();
    
    long getEndingByteOffset() throws XMLStreamException;
    
    long getEndingCharOffset() throws XMLStreamException;
    
    Location getLocation();
    
    XMLStreamLocation2 getStartLocation();
    
    XMLStreamLocation2 getCurrentLocation();
    
    XMLStreamLocation2 getEndLocation() throws XMLStreamException;
}
