// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.optimization.linear;

@Deprecated
public enum Relationship
{
    EQ("="), 
    LEQ("<="), 
    GEQ(">=");
    
    private final String stringValue;
    
    private Relationship(final String stringValue) {
        this.stringValue = stringValue;
    }
    
    @Override
    public String toString() {
        return this.stringValue;
    }
    
    public Relationship oppositeRelationship() {
        switch (this) {
            case LEQ: {
                return Relationship.GEQ;
            }
            case GEQ: {
                return Relationship.LEQ;
            }
            default: {
                return Relationship.EQ;
            }
        }
    }
}
