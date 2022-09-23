// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.reader;

import java.util.List;
import javax.xml.stream.Location;
import javax.xml.namespace.QName;

public class EndElementEvent extends JsonXmlEvent
{
    public EndElementEvent(final QName name, final Location location) {
        super(2, location);
        this.setName(name);
    }
    
    @Override
    public String toString() {
        return "EndElementEvent(" + this.getName() + ")";
    }
}
