// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.symbol;

public interface Symbol
{
    public static final int IDENTIFIER = 0;
    public static final int PARAMETER = 1;
    public static final int VARIABLE = 2;
    
    void setType(final int p0);
    
    int getType();
    
    String getQualifiedName();
    
    void setValueType(final Class p0);
    
    Class getValueType();
}
