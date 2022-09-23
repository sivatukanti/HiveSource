// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.reader;

import javax.xml.namespace.QName;
import java.util.List;
import javax.xml.stream.Location;

abstract class JsonXmlEvent
{
    private final int eventType;
    private final Location location;
    private List<Attribute> attributes;
    private QName name;
    private String text;
    
    protected JsonXmlEvent(final int eventType, final Location location) {
        this.location = location;
        this.eventType = eventType;
    }
    
    public List<Attribute> getAttributes() {
        return this.attributes;
    }
    
    public int getEventType() {
        return this.eventType;
    }
    
    public Location getLocation() {
        return this.location;
    }
    
    public QName getName() {
        return this.name;
    }
    
    public String getPrefix() {
        return (this.name == null) ? null : this.name.getPrefix();
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setAttributes(final List<Attribute> attributes) {
        this.attributes = attributes;
    }
    
    public void setName(final QName name) {
        this.name = name;
    }
    
    public void setText(final String text) {
        this.text = text;
    }
    
    public static class Attribute
    {
        private final QName name;
        private final String value;
        
        public Attribute(final QName name, final String value) {
            this.name = name;
            this.value = value;
        }
        
        public QName getName() {
            return this.name;
        }
        
        public String getValue() {
            return this.value;
        }
    }
}
