// 
// Decompiled by Procyon v0.5.36
// 

package javax.xml.bind;

import org.w3c.dom.Node;
import java.net.URL;

public interface ValidationEventLocator
{
    URL getURL();
    
    int getOffset();
    
    int getLineNumber();
    
    int getColumnNumber();
    
    Object getObject();
    
    Node getNode();
}
