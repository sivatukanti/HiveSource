// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2;

public interface AttributeInfo
{
    int getAttributeCount();
    
    int findAttributeIndex(final String p0, final String p1);
    
    int getIdAttributeIndex();
    
    int getNotationAttributeIndex();
}
