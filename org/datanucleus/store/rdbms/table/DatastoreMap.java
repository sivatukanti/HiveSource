// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public interface DatastoreMap extends Table
{
    JavaTypeMapping getOwnerMapping();
    
    JavaTypeMapping getKeyMapping();
    
    JavaTypeMapping getValueMapping();
}
