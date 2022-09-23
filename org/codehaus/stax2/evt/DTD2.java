// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.evt;

import javax.xml.stream.events.DTD;

public interface DTD2 extends DTD
{
    String getRootName();
    
    String getSystemId();
    
    String getPublicId();
    
    String getInternalSubset();
}
