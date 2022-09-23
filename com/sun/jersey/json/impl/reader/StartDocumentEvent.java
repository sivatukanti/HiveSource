// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.reader;

import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;

public class StartDocumentEvent extends JsonXmlEvent
{
    protected StartDocumentEvent(final Location location) {
        super(7, location);
    }
    
    @Override
    public String toString() {
        return "StartDocumentEvent()";
    }
}
