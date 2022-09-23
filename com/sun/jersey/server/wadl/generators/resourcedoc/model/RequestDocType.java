// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.wadl.generators.resourcedoc.model;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "requestDoc", propOrder = {})
public class RequestDocType
{
    private RepresentationDocType representationDoc;
    
    public RepresentationDocType getRepresentationDoc() {
        return this.representationDoc;
    }
    
    public void setRepresentationDoc(final RepresentationDocType representationDoc) {
        this.representationDoc = representationDoc;
    }
}
