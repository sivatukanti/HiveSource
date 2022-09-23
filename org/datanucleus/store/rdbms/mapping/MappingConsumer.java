// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping;

import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;

public interface MappingConsumer
{
    public static final int MAPPING_TYPE_VERSION = 1;
    public static final int MAPPING_TYPE_DATASTORE_ID = 2;
    public static final int MAPPING_TYPE_DISCRIMINATOR = 3;
    public static final int MAPPING_TYPE_EXTERNAL_INDEX = 4;
    public static final int MAPPING_TYPE_EXTERNAL_FK = 5;
    public static final int MAPPING_TYPE_EXTERNAL_FK_DISCRIM = 6;
    public static final int MAPPING_TYPE_MULTITENANCY = 7;
    
    void preConsumeMapping(final int p0);
    
    void consumeMapping(final JavaTypeMapping p0, final AbstractMemberMetaData p1);
    
    void consumeMapping(final JavaTypeMapping p0, final int p1);
    
    void consumeUnmappedColumn(final Column p0);
}
