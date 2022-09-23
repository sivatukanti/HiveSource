// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import javax.xml.stream.events.XMLEvent;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Characters;
import java.util.Map;
import javax.xml.stream.events.Attribute;
import java.util.List;
import javax.xml.stream.events.StartElement;

class NodeInfo
{
    private StartElement startElement;
    private List<Attribute> attributes;
    private Map<StartElement, Characters> elements;
    private Map<QName, List<XMLEvent>> qNameXMLEventsMap;
    
    public NodeInfo(final StartElement startElement) {
        this.attributes = new ArrayList<Attribute>();
        this.elements = new HashMap<StartElement, Characters>();
        this.qNameXMLEventsMap = new HashMap<QName, List<XMLEvent>>();
        this.startElement = startElement;
    }
    
    private void addQNameXMLEvent(final QName qName, final XMLEvent event) {
        List<XMLEvent> events = this.qNameXMLEventsMap.get(qName);
        if (events == null) {
            events = new ArrayList<XMLEvent>();
            this.qNameXMLEventsMap.put(qName, events);
        }
        events.add(event);
    }
    
    public StartElement getStartElement() {
        return this.startElement;
    }
    
    public void addAttribute(final Attribute attribute) {
        this.attributes.add(attribute);
        this.addQNameXMLEvent(attribute.getName(), attribute);
    }
    
    public Characters getElement(final StartElement element) {
        return this.elements.get(element);
    }
    
    public void addElement(final StartElement element) {
        this.setElement(element, null);
        this.addQNameXMLEvent(element.getName(), element);
    }
    
    public void setElement(final StartElement element, final Characters text) {
        this.elements.put(element, text);
    }
    
    public List<QName> getDuplicatedQNames() {
        final List<QName> duplicates = new ArrayList<QName>();
        for (final Map.Entry<QName, List<XMLEvent>> e : this.qNameXMLEventsMap.entrySet()) {
            if (1 < e.getValue().size()) {
                duplicates.add(e.getKey());
            }
        }
        return duplicates;
    }
    
    public List<XMLEvent> getXMLEventsForQName(final QName qName) {
        return this.qNameXMLEventsMap.get(qName);
    }
}
