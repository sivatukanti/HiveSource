// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.schemagen.xmlschema;

import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.txw2.TypedXmlWriter;

public interface TypeDefParticle extends TypedXmlWriter
{
    @XmlElement
    ExplicitGroup all();
    
    @XmlElement
    ExplicitGroup sequence();
    
    @XmlElement
    ExplicitGroup choice();
}
