// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.symbol;

import java.io.Serializable;

public class PropertySymbol implements Symbol, Serializable
{
    int type;
    final String qualifiedName;
    Class valueType;
    
    public PropertySymbol(final String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }
    
    public PropertySymbol(final String qualifiedName, final Class type) {
        this.qualifiedName = qualifiedName;
        this.valueType = type;
    }
    
    @Override
    public void setType(final int type) {
        this.type = type;
    }
    
    @Override
    public int getType() {
        return this.type;
    }
    
    @Override
    public String getQualifiedName() {
        return this.qualifiedName;
    }
    
    @Override
    public Class getValueType() {
        return this.valueType;
    }
    
    @Override
    public void setValueType(final Class type) {
        this.valueType = type;
    }
    
    @Override
    public String toString() {
        String typeName = null;
        if (this.type == 0) {
            typeName = "IDENTIFIER";
        }
        else if (this.type == 1) {
            typeName = "PARAMETER";
        }
        else if (this.type == 2) {
            typeName = "VARIABLE";
        }
        return "Symbol: " + this.qualifiedName + " [valueType=" + this.valueType + ", " + typeName + "]";
    }
}
