// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

public enum RelationType
{
    NONE, 
    ONE_TO_ONE_UNI, 
    ONE_TO_ONE_BI, 
    ONE_TO_MANY_UNI, 
    ONE_TO_MANY_BI, 
    MANY_TO_MANY_BI, 
    MANY_TO_ONE_BI, 
    MANY_TO_ONE_UNI;
    
    public static boolean isRelationSingleValued(final RelationType type) {
        return type == RelationType.ONE_TO_ONE_UNI || type == RelationType.ONE_TO_ONE_BI || type == RelationType.MANY_TO_ONE_UNI || type == RelationType.MANY_TO_ONE_BI;
    }
    
    public static boolean isRelationMultiValued(final RelationType type) {
        return type == RelationType.ONE_TO_MANY_UNI || type == RelationType.ONE_TO_MANY_BI || type == RelationType.MANY_TO_MANY_BI;
    }
    
    public static boolean isBidirectional(final RelationType type) {
        return type == RelationType.ONE_TO_ONE_BI || type == RelationType.ONE_TO_MANY_BI || type == RelationType.MANY_TO_MANY_BI || type == RelationType.MANY_TO_ONE_BI;
    }
}
