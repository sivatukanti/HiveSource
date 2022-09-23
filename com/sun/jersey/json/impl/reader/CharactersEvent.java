// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.reader;

import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;

public class CharactersEvent extends JsonXmlEvent
{
    public CharactersEvent(final String text, final Location location) {
        super(4, location);
        this.setText(text);
    }
    
    @Override
    public String toString() {
        return "CharactersEvent(" + this.getText() + ")";
    }
}
