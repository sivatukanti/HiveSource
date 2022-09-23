// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlElement;

final class XmlElementQuick extends Quick implements XmlElement
{
    private final XmlElement core;
    
    public XmlElementQuick(final Locatable upstream, final XmlElement core) {
        super(upstream);
        this.core = core;
    }
    
    @Override
    protected Annotation getAnnotation() {
        return this.core;
    }
    
    @Override
    protected Quick newInstance(final Locatable upstream, final Annotation core) {
        return new XmlElementQuick(upstream, (XmlElement)core);
    }
    
    public Class<XmlElement> annotationType() {
        return XmlElement.class;
    }
    
    public String namespace() {
        return this.core.namespace();
    }
    
    public boolean required() {
        return this.core.required();
    }
    
    public boolean nillable() {
        return this.core.nillable();
    }
    
    public String name() {
        return this.core.name();
    }
    
    public Class type() {
        return this.core.type();
    }
    
    public String defaultValue() {
        return this.core.defaultValue();
    }
}
