// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.coder;

import org.apache.hadoop.io.erasurecode.ECBlockGroup;
import org.apache.hadoop.io.erasurecode.ErasureCoderOptions;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configurable;

@InterfaceAudience.Private
public interface ErasureCoder extends Configurable
{
    int getNumDataUnits();
    
    int getNumParityUnits();
    
    ErasureCoderOptions getOptions();
    
    ErasureCodingStep calculateCoding(final ECBlockGroup p0);
    
    boolean preferDirectBuffer();
    
    void release();
}
