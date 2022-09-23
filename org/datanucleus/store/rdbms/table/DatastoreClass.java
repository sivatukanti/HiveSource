// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import org.datanucleus.store.rdbms.mapping.MappingConsumer;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import java.util.Collection;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.IdentityType;

public interface DatastoreClass extends Table
{
    String getType();
    
    IdentityType getIdentityType();
    
    boolean isObjectIdDatastoreAttributed();
    
    boolean isBaseDatastoreClass();
    
    DatastoreClass getBaseDatastoreClass();
    
    DatastoreClass getBaseDatastoreClassWithMember(final AbstractMemberMetaData p0);
    
    boolean isSuperDatastoreClass(final DatastoreClass p0);
    
    DatastoreClass getSuperDatastoreClass();
    
    Collection<SecondaryDatastoreClass> getSecondaryDatastoreClasses();
    
    boolean managesClass(final String p0);
    
    String[] getManagedClasses();
    
    boolean managesMapping(final JavaTypeMapping p0);
    
    String toString();
    
    JavaTypeMapping getDatastoreObjectIdMapping();
    
    JavaTypeMapping getMemberMapping(final String p0);
    
    JavaTypeMapping getMemberMapping(final AbstractMemberMetaData p0);
    
    JavaTypeMapping getMemberMappingInDatastoreClass(final AbstractMemberMetaData p0);
    
    void provideDatastoreIdMappings(final MappingConsumer p0);
    
    void providePrimaryKeyMappings(final MappingConsumer p0);
    
    void provideNonPrimaryKeyMappings(final MappingConsumer p0);
    
    void provideMappingsForMembers(final MappingConsumer p0, final AbstractMemberMetaData[] p1, final boolean p2);
    
    void provideVersionMappings(final MappingConsumer p0);
    
    void provideDiscriminatorMappings(final MappingConsumer p0);
    
    void provideMultitenancyMapping(final MappingConsumer p0);
    
    void provideUnmappedColumns(final MappingConsumer p0);
    
    void provideExternalMappings(final MappingConsumer p0, final int p1);
    
    JavaTypeMapping getExternalMapping(final AbstractMemberMetaData p0, final int p1);
    
    AbstractMemberMetaData getMetaDataForExternalMapping(final JavaTypeMapping p0, final int p1);
}
