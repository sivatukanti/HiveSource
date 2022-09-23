// 
// Decompiled by Procyon v0.5.36
// 

package javax.xml.stream.events;

import javax.xml.namespace.QName;

public interface Attribute extends XMLEvent
{
    QName getName();
    
    String getValue();
    
    String getDTDType();
    
    boolean isSpecified();
}
