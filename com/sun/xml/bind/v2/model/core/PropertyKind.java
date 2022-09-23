// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.model.core;

public enum PropertyKind
{
    VALUE(true, false, Integer.MAX_VALUE), 
    ATTRIBUTE(false, false, Integer.MAX_VALUE), 
    ELEMENT(true, true, 0), 
    REFERENCE(false, true, 1), 
    MAP(false, true, 2);
    
    public final boolean canHaveXmlMimeType;
    public final boolean isOrdered;
    public final int propertyIndex;
    
    private PropertyKind(final boolean canHaveExpectedContentType, final boolean isOrdered, final int propertyIndex) {
        this.canHaveXmlMimeType = canHaveExpectedContentType;
        this.isOrdered = isOrdered;
        this.propertyIndex = propertyIndex;
    }
}
