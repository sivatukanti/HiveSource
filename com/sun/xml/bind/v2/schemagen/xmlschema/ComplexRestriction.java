// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.txw2.TypedXmlWriter;

@XmlElement("restriction")
public interface ComplexRestriction extends Annotated, AttrDecls, TypeDefParticle, TypedXmlWriter
{
    @XmlAttribute
    ComplexRestriction base(final QName p0);
}
