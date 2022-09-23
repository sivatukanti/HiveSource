// 
// Decompiled by Procyon v0.5.36
// 

package javax.xml.stream.events;

import java.util.List;

public interface DTD extends XMLEvent
{
    String getDocumentTypeDeclaration();
    
    Object getProcessedDTD();
    
    List getNotations();
    
    List getEntities();
}
