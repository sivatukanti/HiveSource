// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAttribute;

final class XmlAttributeQuick extends Quick implements XmlAttribute
{
    private final XmlAttribute core;
    
    public XmlAttributeQuick(final Locatable upstream, final XmlAttribute core) {
        super(upstream);
        this.core = core;
    }
    
    @Override
    protected Annotation getAnnotation() {
        return this.core;
    }
    
    @Override
    protected Quick newInstance(final Locatable upstream, final Annotation core) {
        return new XmlAttributeQuick(upstream, (XmlAttribute)core);
    }
    
    public Class<XmlAttribute> annotationType() {
        return XmlAttribute.class;
    }
    
    public String namespace() {
        return this.core.namespace();
    }
    
    public boolean required() {
        return this.core.required();
    }
    
    public String name() {
        return this.core.name();
    }
}
