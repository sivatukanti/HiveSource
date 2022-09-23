// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.reader;

import java.util.List;
import javax.xml.stream.Location;
import javax.xml.namespace.QName;

public class StartElementEvent extends JsonXmlEvent
{
    public StartElementEvent(final QName name, final Location location) {
        super(1, location);
        this.setName(name);
    }
    
    @Override
    public String toString() {
        return "StartElementEvent(" + this.getName() + ")";
    }
}
