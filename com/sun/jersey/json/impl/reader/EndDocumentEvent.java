// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.reader;

import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;

public class EndDocumentEvent extends JsonXmlEvent
{
    protected EndDocumentEvent(final Location location) {
        super(8, location);
    }
    
    @Override
    public String toString() {
        return "EndDocumentEvent()";
    }
}
