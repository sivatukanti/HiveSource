// 
// Decompiled by Procyon v0.5.36
// 

package javax.xml.stream.events;

import java.util.Iterator;
import javax.xml.namespace.QName;

public interface EndElement extends XMLEvent
{
    QName getName();
    
    Iterator getNamespaces();
}
