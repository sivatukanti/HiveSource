// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.identifier;

public interface DatastoreIdentifier
{
    String getIdentifierName();
    
    void setCatalogName(final String p0);
    
    void setSchemaName(final String p0);
    
    String getCatalogName();
    
    String getSchemaName();
    
    String getFullyQualifiedName(final boolean p0);
    
    String toString();
}
