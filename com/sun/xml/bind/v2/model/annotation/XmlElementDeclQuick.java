// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlElementDecl;

final class XmlElementDeclQuick extends Quick implements XmlElementDecl
{
    private final XmlElementDecl core;
    
    public XmlElementDeclQuick(final Locatable upstream, final XmlElementDecl core) {
        super(upstream);
        this.core = core;
    }
    
    @Override
    protected Annotation getAnnotation() {
        return this.core;
    }
    
    @Override
    protected Quick newInstance(final Locatable upstream, final Annotation core) {
        return new XmlElementDeclQuick(upstream, (XmlElementDecl)core);
    }
    
    public Class<XmlElementDecl> annotationType() {
        return XmlElementDecl.class;
    }
    
    public String namespace() {
        return this.core.namespace();
    }
    
    public String substitutionHeadNamespace() {
        return this.core.substitutionHeadNamespace();
    }
    
    public String substitutionHeadName() {
        return this.core.substitutionHeadName();
    }
    
    public String name() {
        return this.core.name();
    }
    
    public String defaultValue() {
        return this.core.defaultValue();
    }
    
    public Class scope() {
        return this.core.scope();
    }
}
