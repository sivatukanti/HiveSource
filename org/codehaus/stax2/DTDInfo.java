// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2;

import org.codehaus.stax2.validation.DTDValidationSchema;

public interface DTDInfo
{
    Object getProcessedDTD();
    
    String getDTDRootName();
    
    String getDTDSystemId();
    
    String getDTDPublicId();
    
    String getDTDInternalSubset();
    
    DTDValidationSchema getProcessedDTDSchema();
}
